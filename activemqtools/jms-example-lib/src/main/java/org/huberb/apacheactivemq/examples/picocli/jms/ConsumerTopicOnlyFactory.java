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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableConnection;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableMessageConsumer;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableSession;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.JMSRuntimeException;

/**
 *
 * @author pi
 */
public class ConsumerTopicOnlyFactory {

    final DestinationFactory df = new DestinationFactory();
    final ToConverters toConverters = new ToConverters();
    final CountDownLatch latch;
    final ConnectionFactory connectionFactory;

    /**
     *
     * @param connectionFactory
     */
    public ConsumerTopicOnlyFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.latch = new CountDownLatch(1);
    }

    /**
     *
     * @param m
     * @param topicName
     * @param jmsMessageSelector
     * @param maxReceiveCount
     * @param maxWaittimeSeconds
     * @return
     */
    public Optional<Message> consumeTopicMessage(
            final Map<String, Object> m,
            final String topicName,
            final String jmsMessageSelector,
            final int maxReceiveCount,
            final int maxWaittimeSeconds) {

        final List<Message> l = new ArrayList<>();
        final Consumer<Message> messageConsumer = (Message message) -> {
            l.add(message);
        };
        final String theJmsMessageSelector = jmsMessageSelector;
        final int theMaxReceiveCount = maxReceiveCount;
        consumeTopicMessage(m, topicName, theJmsMessageSelector, theMaxReceiveCount, maxWaittimeSeconds, messageConsumer);
        final Optional<Message> result = l.stream().findFirst();
        return result;
    }

    /**
     *
     * @param m
     * @param topicName
     * @param jmsMessageSelector
     * @param maxReceiveCount
     * @param maxWaittimeSeconds
     * @param messageConsumer
     */
    public void consumeTopicMessage(
            final Map<String, Object> m,
            final String topicName,
            final String jmsMessageSelector,
            final int maxReceiveCount,
            final int maxWaittimeSeconds,
            Consumer<Message> messageConsumer) {
        final Function<Session, Destination> fDestinationFromSession = df.createDestinationTopicFromSessionFunction(topicName);
        //--- javax.jms.Session
        final boolean transacted = peekTransactedFrom(m);
        final int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;

        //---
        try (final AutoCloseableConnection autocloseableConnection = new AutoCloseableConnection(connectionFactory.createConnection())) {
            final Connection connection = autocloseableConnection.connection();
            connection.start();

            try (final AutoCloseableSession autoCloseableSession = new AutoCloseableSession(connection.createSession(transacted, acknowledgeMode))) {
                final Session session = autoCloseableSession.session();
                //---
                final Destination destination = fDestinationFromSession.apply(session);
                final String theJmsMessageSelector = jmsMessageSelector;

                try (final AutoCloseableMessageConsumer autoCloseableMessageConsumer = new AutoCloseableSupport.AutoCloseableMessageConsumer(session.createConsumer(destination, theJmsMessageSelector, false))) {
                    final MessageConsumer consumer = autoCloseableMessageConsumer.messageConsumer();
                    final int theMaxReceiveCount = maxReceiveCount;
                    consumer.setMessageListener(new ConsumerMessageListener(messageConsumer, theMaxReceiveCount, latch));
                    //---
                    int theMaxWaittimeSeconds = maxWaittimeSeconds;
                    if (theMaxWaittimeSeconds < 0) {
                        theMaxWaittimeSeconds = Integer.MAX_VALUE;
                    }
                    latch.await(theMaxWaittimeSeconds, TimeUnit.SECONDS);
                } catch (InterruptedException intrpEx) {
                    // do notthing
                }

                if (transacted) {
                    session.commit();
                }
            }
        } catch (JMSException jmsex) {
            throw new JMSRuntimeException(jmsex);
        }
    }

    boolean peekTransactedFrom(Map<String, Object> m) {
        final boolean transacted;

        final Object v = m.getOrDefault("session.transacted", Boolean.FALSE);
        transacted = toConverters.toBoolean(v).orElse(Boolean.FALSE);

        return transacted;
    }

}
