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

import java.util.Objects;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TopicSubscriber;

/**
 * Support classes for auto-closing of jms resources.
 * <p>
 * As of JMS 2.x these classes are mostly useless.
 *
 * @author pi
 */
public class AutoCloseableSupport {

    /**
     * Wrap {@link Connection} as {@link AutoCloseable}.
     */
    public static class AutoCloseableConnection implements AutoCloseable {

        private final Connection connection;

        public AutoCloseableConnection(Connection connection) {
            this.connection = connection;
        }

        public Connection connection() {
            return this.connection;
        }

        @Override
        public void close() throws JMSException {
            if (this.connection != null) {
                this.connection.close();
            }
        }
    }

    /**
     * Wrap {@link Sesion} as {@link AutoCloseable}.
     */
    public static class AutoCloseableSession implements AutoCloseable {

        private final Session session;

        public AutoCloseableSession(Session session) {
            this.session = session;
        }

        public Session session() {
            return this.session;
        }

        @Override
        public void close() throws JMSException {
            if (this.session != null) {
                this.session.close();
            }
        }
    }

    /**
     * Wrap {@link MessageProducer} as {@link AutoCloseable}.
     */
    public static class AutoCloseableMessageProducer implements AutoCloseable {

        private final MessageProducer messageProducer;

        public AutoCloseableMessageProducer(MessageProducer messageProducer) {
            this.messageProducer = messageProducer;
        }

        public MessageProducer messageProducer() {
            return this.messageProducer;
        }

        @Override
        public void close() throws JMSException {
            if (this.messageProducer != null) {
                this.messageProducer.close();
            }
        }
    }

    /**
     * Wrap {@link MessageConsumer} as {@link AutoCloseable}.
     */
    public static class AutoCloseableMessageConsumer implements AutoCloseable {

        private final MessageConsumer messageConsumer;

        public AutoCloseableMessageConsumer(MessageConsumer messageConsumer) {
            this.messageConsumer = messageConsumer;
        }

        public MessageConsumer messageConsumer() {
            return this.messageConsumer;
        }

        @Override
        public void close() throws JMSException {
            if (this.messageConsumer != null) {
                this.messageConsumer.close();
            }
        }
    }

    /**
     * Wrap {@link TopicSubscriber} as {@link AutoCloseable}.
     */
    public static class AutoCloseableTopicSubscriber implements AutoCloseable {

        private final TopicSubscriber topicSubscriber;

        public AutoCloseableTopicSubscriber(TopicSubscriber topicSubscriber) {
            this.topicSubscriber = topicSubscriber;
        }

        public TopicSubscriber topicSubscriber() {
            return this.topicSubscriber;
        }

        @Override
        public void close() throws JMSException {
            if (this.topicSubscriber != null) {
                this.topicSubscriber.close();
            }
        }
    }

    /**
     * Wrap {@link QueueBrowser} as {@link AutoCloseable}.
     */
    public static class AutoCloseableQueueBrowser implements AutoCloseable {

        private final QueueBrowser queueBrowser;

        public AutoCloseableQueueBrowser(QueueBrowser queueBrowser) {
            this.queueBrowser = queueBrowser;
        }

        public QueueBrowser queueBrowser() {
            return this.queueBrowser;
        }

        @Override
        public void close() throws JMSException {
            if (this.queueBrowser != null) {
                this.queueBrowser.close();
            }
        }
    }

    /**
     * Simple RuntimeException wrapping a {@link JMSException}.
     *
     */
    public static class JMSRuntimeException extends RuntimeException {

        public JMSRuntimeException(JMSException jmsex) {
            super("", jmsex);
        }

        public JMSRuntimeException(String m, JMSException jmsex) {
            super(Objects.toString(m, ""), jmsex);
        }
    }

}
