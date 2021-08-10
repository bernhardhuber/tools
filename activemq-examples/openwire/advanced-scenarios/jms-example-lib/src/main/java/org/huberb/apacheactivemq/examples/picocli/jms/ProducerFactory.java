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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableConnection;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableMessageProducer;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableSession;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.JMSRuntimeException;
import org.huberb.apacheactivemq.examples.picocli.jms.EnumRepresentations.DeliveryModeRepresentation;

/**
 * Factory for creating JMS producers.
 *
 * @author pi
 */
public class ProducerFactory {

    final DestinationFactory df = new DestinationFactory();
    final MessageFactory mf = new MessageFactory();
    final ToConverters toConverters = new ToConverters();
    final ConnectionFactory connectionFactory;

    public ProducerFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Send a single message into a jms-queue.
     *
     * @param m
     * @param queueName
     * @param messageText
     */
    public void sendQueueMessage(
            final Map<String, Object> m,
            final String queueName,
            final String messageText
    ) {
        final Function<Session, Destination> fDestinationFromSession = df.createDestinationQueueFromSessionFunction(queueName);
        final Function<Session, Iterator<Message>> fMesageIteratorFromSession = mf.createTextMessageIteratorFromSession(Arrays.asList(messageText), m);

        sendDestinationMessages(m, fDestinationFromSession, fMesageIteratorFromSession);
    }

    /**
     * Send messages into a jms-queue.
     *
     * @param m
     * @param queueName
     * @param listOfMessageText
     */
    public void sendQueueMessages(
            final Map<String, Object> m,
            final String queueName,
            final List<String> listOfMessageText
    ) {
        final Function<Session, Destination> fDestinationFromSession = df.createDestinationQueueFromSessionFunction(queueName);
        final Function<Session, Iterator<Message>> fMesageIteratorFromSession = mf.createTextMessageIteratorFromSession(listOfMessageText, m);

        sendDestinationMessages(m, fDestinationFromSession, fMesageIteratorFromSession);
    }

    /**
     * Send a single message into a jms-topic.
     *
     * @param m
     * @param topicName
     * @param messageText
     */
    public void sendTopicMessage(
            final Map<String, Object> m,
            final String topicName,
            final String messageText
    ) {
        final Function<Session, Destination> fDestinationFromSession = df.createDestinationTopicFromSessionFunction(topicName);
        final Function<Session, Iterator<Message>> fMesageIteratorFromSession = mf.createTextMessageIteratorFromSession(Arrays.asList(messageText), m);

        sendDestinationMessages(m, fDestinationFromSession, fMesageIteratorFromSession);
    }

    /**
     * Send messages into jms-topic.
     *
     * @param m
     * @param queueName
     * @param listOfMessageText
     */
    public void sendTopicMessages(
            final Map<String, Object> m,
            final String queueName,
            final List<String> listOfMessageText
    ) {
        final Function<Session, Destination> fDestinationFromSession = df.createDestinationTopicFromSessionFunction(queueName);
        final Function<Session, Iterator<Message>> fMesageIteratorFromSession = mf.createTextMessageIteratorFromSession(listOfMessageText, m);

        sendDestinationMessages(m, fDestinationFromSession, fMesageIteratorFromSession);
    }

    /**
     * Sending messages into a jms-queue or jms-topic.
     *
     * @param m
     * @param fDestinationFromSession
     * @param fMesageIteratorFromSession
     */
    public void sendDestinationMessages(
            final Map<String, Object> m,
            final Function<Session, Destination> fDestinationFromSession,
            final Function<Session, Iterator<Message>> fMesageIteratorFromSession
    ) {
        //--- javax.jms.Session
        final boolean transacted = peekTransactedFrom(m);
        // TODO make acknowledgeMode configurable
        final int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
        //--- javax.jms.MessageProducer
        final int deliveryMode = peekDeliveryMode(m);
        final long timeToLive = peekTimeToLive(m);
        final int priority = peekPriority(m);

        //---
        try (final AutoCloseableConnection autocloseableConnection = new AutoCloseableConnection(connectionFactory.createConnection())) {
            final Connection connection = autocloseableConnection.connection();
            connection.start();

            try (final AutoCloseableSession autoCloseableSession = new AutoCloseableSession(connection.createSession(transacted, acknowledgeMode))) {
                final Session session = autoCloseableSession.session();

                //---
                final Destination destination = fDestinationFromSession.apply(session);
                try (final AutoCloseableMessageProducer autoCloseableMessageProducer = new AutoCloseableMessageProducer(session.createProducer(destination))) {
                    final MessageProducer messageProducer = autoCloseableMessageProducer.messageProducer();
                    messageProducer.setDeliveryMode(deliveryMode);
                    messageProducer.setTimeToLive(timeToLive);
                    messageProducer.setPriority(priority);
                    //---
                    for (Iterator<Message> it = fMesageIteratorFromSession.apply(session); it.hasNext();) {
                        final Message message = it.next();
                        messageProducer.send(message);
                    }
                }
                //---
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

    int peekDeliveryMode(Map<String, Object> m) {
        final int deliveryMode;

        final Object v = m.getOrDefault("producer.deliveryMode", DeliveryMode.NON_PERSISTENT);
        final List<DeliveryModeRepresentation> deliveryModeValuesAllowedValues = Arrays.asList(
                DeliveryModeRepresentation.non_persistent,
                DeliveryModeRepresentation.persistent);
        deliveryMode = toConverters.toInteger(v, deliveryModeValuesAllowedValues).orElse(DeliveryMode.NON_PERSISTENT);

        return deliveryMode;
    }

    long peekTimeToLive(Map<String, Object> m) {
        final long msFrom60seconds = TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS);
        final long timeToLive;

        final Object v = m.getOrDefault("producer.timeToLive", msFrom60seconds);
        long l = toConverters.toLong(v).orElse(msFrom60seconds);

        if (l >= 0) {
            timeToLive = l;
        } else {
            timeToLive = msFrom60seconds;
        }
        return timeToLive;
    }

    int peekPriority(Map<String, Object> m) {
        final int defaultPriortiy = 4;
        final int priority;

        final Object v = m.getOrDefault("producer.priority", defaultPriortiy);
        final int i = toConverters.toInteger(v).orElse(defaultPriortiy);

        if (i >= 0) {
            priority = i;
        } else {
            priority = defaultPriortiy;
        }
        return priority;
    }
}
