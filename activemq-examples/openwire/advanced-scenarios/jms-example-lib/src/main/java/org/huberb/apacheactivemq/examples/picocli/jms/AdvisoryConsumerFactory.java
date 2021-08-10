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

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableConnection;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableMessageConsumer;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pi
 */
public class AdvisoryConsumerFactory {

    private static final Logger logger = LoggerFactory.getLogger(AdvisoryConsumerFactory.class);

    final DestinationFactory df = new DestinationFactory();
    final ToConverters toConverters = new ToConverters();
    final CountDownLatch latch;
    final ConnectionFactory connectionFactory;

    public AdvisoryConsumerFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.latch = new CountDownLatch(1);
    }

    /**
     *
     * @param m
     * @param advisoryTopicName
     * @param jmsMessageSelector
     * @param maxReceiveCount
     * @param maxWaittimeSeconds
     */
    public void consumeAdvisoryMessage(final Map<String, Object> m,
            final String advisoryTopicName,
            final String jmsMessageSelector,
            final int maxReceiveCount,
            final int maxWaittimeSeconds) {
        final Consumer<Message> messageConsumer = (Message message) -> {
            if (message != null && message instanceof ActiveMQMessage) {
                final ActiveMQMessage amqMessage = (ActiveMQMessage) message;
                logger.info("Received amqMessage: {}", amqMessage);
            }
        };
        consumeAdvisoryMessage(m, advisoryTopicName, jmsMessageSelector, maxReceiveCount, maxWaittimeSeconds, messageConsumer);
    }

    /**
     *
     * @param m
     * @param advisoryTopicName
     * @param jmsMessageSelector
     * @param maxReceiveCount
     * @param maxWaittimeSeconds
     * @param messageConsumer
     */
    public void consumeAdvisoryMessage(final Map<String, Object> m,
            final String advisoryTopicName,
            final String jmsMessageSelector,
            final int maxReceiveCount,
            final int maxWaittimeSeconds,
            Consumer<Message> messageConsumer) {

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
                final Destination destination = new ActiveMQTopic(advisoryTopicName);
                final String theJmsMessageSelector = jmsMessageSelector;

                try (final AutoCloseableMessageConsumer autoClosableMessageConsumer = new AutoCloseableMessageConsumer(session.createConsumer(destination, theJmsMessageSelector))) {
                    final MessageConsumer consumer = autoClosableMessageConsumer.messageConsumer();
                    int theMaxReceiveCount = maxReceiveCount;
                    consumer.setMessageListener(new AdvisoryMessageListener(messageConsumer, theMaxReceiveCount, latch));
                    //---
                    int theMaxWaittimeSeconds = maxWaittimeSeconds;
                    if (theMaxWaittimeSeconds < 0) {
                        theMaxWaittimeSeconds = Integer.MAX_VALUE;
                    }
                    latch.await(theMaxWaittimeSeconds, TimeUnit.SECONDS);
                } catch (InterruptedException intrpEx) {
                    // do notthing
                }
            }
        } catch (JMSException jmsex) {
            throw new AutoCloseableSupport.JMSRuntimeException(jmsex);
        }
    }

    /**
     * A {@code MessageListener} listening on advisory messages.
     */
    static class AdvisoryMessageListener implements MessageListener {

        private static final Logger logger = LoggerFactory.getLogger(AdvisoryMessageListener.class);

        private final int maxReceiveCount;
        private int receivedCount;
        private final CountDownLatch latch;
        private final Consumer<Message> messageConsumer;

        AdvisoryMessageListener(Consumer<Message> messageConsumer, int maxReceiveCount, CountDownLatch latch) {
            this.messageConsumer = messageConsumer;
            this.maxReceiveCount = maxReceiveCount;
            this.receivedCount = 0;
            this.latch = latch;
        }

        @Override
        public void onMessage(Message message) {
            boolean countDownLatch = false;
            try {
                receivedCount += 1;
                countDownLatch = (this.maxReceiveCount >= 0 && receivedCount >= this.maxReceiveCount);
                messageConsumer.accept(message);
            } catch (Exception e) {
                logger.warn("onMessage", e);
            } finally {
                if (countDownLatch) {
                    latch.countDown();
                }
            }
        }
    }

}
