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
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableConnection;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableSession;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableTopicSubscriber;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.JMSRuntimeException;

/**
 *
 * @author pi
 */
public class ConsumerDurableTopicOnlyFactory {

    final DestinationFactory df = new DestinationFactory();
    final ToConverters toConverters = new ToConverters();
    final CountDownLatch latch;
    final ConnectionFactory connectionFactory;

    public ConsumerDurableTopicOnlyFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.latch = new CountDownLatch(1);
    }

    public Optional<Message> consumeTopicMessageAsDurableSubscriber(
            final Map<String, Object> m,
            final String topicName,
            final String jmsMessageSelector,
            final int maxReceiveCount) {
        final List<Message> l = new ArrayList<>();
        final Consumer<Message> messageConsumer = (Message message) -> {
            l.clear();
            l.add(message);
        };
        final String theJmsMessageSelector = jmsMessageSelector;
        final int theMaxReceiveCount = maxReceiveCount;
        consumeTopicMessageAsDurableSubscriber(m, topicName, theJmsMessageSelector, theMaxReceiveCount, messageConsumer);
        final Optional<Message> result = Optional.ofNullable(l.get(0));
        return result;
    }

    public void consumeTopicMessageAsDurableSubscriber(
            final Map<String, Object> m,
            final String topicName,
            final String jmsMessageSelector,
            final int maxReceiveCount,
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
                final Topic topic = (Topic) destination;
                final String theJmsMessageSelector = jmsMessageSelector;
                final String theDurableSubscriberName = "A";
                try (final AutoCloseableTopicSubscriber autoCloseableTopicSubscriber = new AutoCloseableTopicSubscriber(
                        session.createDurableSubscriber(topic, theDurableSubscriberName, theJmsMessageSelector, false))) {
                    final TopicSubscriber topicSubscriber = autoCloseableTopicSubscriber.topicSubscriber();
                    final int theMaxReceiveCount = maxReceiveCount;
                    topicSubscriber.setMessageListener(new ConsumerMessageListener(messageConsumer, theMaxReceiveCount, latch));
                    //---
                    long awaitInMillis = TimeUnit.MINUTES.toMillis(60);
                    latch.await(awaitInMillis, TimeUnit.MILLISECONDS);
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
