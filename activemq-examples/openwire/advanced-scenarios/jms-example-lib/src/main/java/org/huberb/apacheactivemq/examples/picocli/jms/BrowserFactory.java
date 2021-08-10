/*
 * Copyright 2021 pi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.huberb.apacheactivemq.examples.picocli.jms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableConnection;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableQueueBrowser;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableSession;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.JMSRuntimeException;

/**
 * Factory for creating JMS queue browsers.
 *
 * @author pi
 */
public class BrowserFactory {

    final DestinationFactory df = new DestinationFactory();
    final ToConverters toConverters = new ToConverters();
    final ConnectionFactory connectionFactory;

    public BrowserFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Browse a queue, returning browsed messages.
     *
     * @param m
     * @param queueName
     * @param jmsMessageSelector
     * @param maxBrowseCount
     * @return
     */
    public List<Message> browseMessages(
            final Map<String, Object> m,
            final String queueName,
            final String jmsMessageSelector,
            int maxBrowseCount) {
        final List<Message> result = new ArrayList<>();
        final Consumer<Message> messageConsumer = (Message message) -> {
            result.add(message);
        };
        browseMessages(m, queueName, jmsMessageSelector, maxBrowseCount, messageConsumer);
        return result;
    }

    /**
     * Browse a queue, returning the number of messages in the queue.
     *
     * @param m
     * @param queueName
     * @param jmsMessageSelector
     * @param maxBrowseCount
     * @return
     */
    public Integer browseMessagesCounting(
            final Map<String, Object> m,
            final String queueName,
            final String jmsMessageSelector,
            int maxBrowseCount) {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final Consumer<Message> messageConsumer = (Message message) -> {
            atomicInteger.incrementAndGet();
        };
        browseMessages(m, queueName, jmsMessageSelector, maxBrowseCount, messageConsumer);
        return atomicInteger.intValue();
    }

    /**
     * Generic entry for consuming messages internally via
     * {@code @QueueBrowser}.
     *
     * @param m
     * @param queueName
     * @param jmsMessageSelector
     * @param maxBrowseCount
     * @param messageConsumer
     */
    public void browseMessages(
            final Map<String, Object> m,
            final String queueName,
            final String jmsMessageSelector,
            int maxBrowseCount,
            final Consumer<Message> messageConsumer) {
        final Function<Session, Destination> fDestinationFromSession = df.createDestinationQueueFromSessionFunction(queueName);

        //--- javax.jms.Session
        final boolean transacted = false;
        final int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;

        //---
        try (final AutoCloseableConnection autocloseableConnection = new AutoCloseableConnection(connectionFactory.createConnection())) {
            final Connection connection = autocloseableConnection.connection();
            connection.start();

            try (final AutoCloseableSession autoCloseableSession = new AutoCloseableSession(connection.createSession(transacted, acknowledgeMode))) {
                final Session session = autoCloseableSession.session();

                //---
                final Destination destination = fDestinationFromSession.apply(session);
                final Queue queue = (Queue) destination;
                final String theJmsMessageSelector = jmsMessageSelector;
                try (final AutoCloseableQueueBrowser autoClosableQueueBrowser = new AutoCloseableQueueBrowser(session.createBrowser(queue, theJmsMessageSelector))) {
                    final QueueBrowser queueBrowser = autoClosableQueueBrowser.queueBrowser();
                    browseMessage(queueBrowser, maxBrowseCount, messageConsumer);
                }
            }
        } catch (JMSException jmsex) {
            throw new JMSRuntimeException(jmsex);
        }
    }

    //---
    void browseMessage(QueueBrowser queueBrowser, int maxBrowseCount, Consumer<Message> messageConsumer) throws JMSException {
        final Enumeration enumeration = queueBrowser.getEnumeration();
        final LimitingIterator<Message> limitingIterator = new LimitingIterator<>(enumeration.asIterator(), maxBrowseCount);
        limitingIterator.forEachRemaining(messageConsumer);
    }

}
