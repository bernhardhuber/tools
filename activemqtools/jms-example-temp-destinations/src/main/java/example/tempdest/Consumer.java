package example.tempdest;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final Boolean NON_TRANSACTED = false;
    private static final long TIMEOUT = 20000;

    public static void main(String[] args) {

        final String user = env("ACTIVEMQ_USER", "admin");
        final String password = env("ACTIVEMQ_PASSWORD", "password");
        final String host = env("ACTIVEMQ_HOST", "localhost");
        final int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
        final String queueName = env("QUEUE", "test-queue");
        final boolean transacted = Boolean.parseBoolean(env("TRANSACTED", "false"));

        String url = "tcp://" + host + ":" + port;
        if (args.length > 0) {
            url = args[0].trim();
        }
        logger.info(String.format("Running with:%n"
                + "url: %s, %n"
                + "user: %s, [ACTIVEMQ_USER]%n"
                + "password: %s, [ACTIVEMQ_PASSWORD]%n"
                + "host: %s, [ACTIVEMQ_HOST]%n"
                + "port: %d, [ACTIVEMQ_PORT]%n"
                + "queue: %s, [QUEUE]%n"
                + "transacted: %b, [TRANSACTED]%n",
                url, user, password,
                host, port,
                queueName,
                transacted
        ));
        logger.info(String.format("Waiting to receive messages... will timeout after %d s", TIMEOUT / 1000));
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        Connection connection = null;
        try {

            connection = connectionFactory.createConnection();
            connection.start();

            final Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            try {
                final Destination destination = session.createQueue(queueName);
                final MessageConsumer consumer = session.createConsumer(destination);
                try {
                    for (int i = 0;; i++) {
                        Message message = consumer.receive(TIMEOUT);

                        if (message != null) {
                            if (message instanceof TextMessage) {
                                final String text = ((TextMessage) message).getText();
                                logger.info(String.format("Got #%d message: %s", i, text));
                                final Destination replyTo = message.getJMSReplyTo();
                                MessageProducer producer = session.createProducer(replyTo);
                                producer.send(session.createTextMessage("You made it to the consumer, here is your response"));
                                producer.close();
                            }
                        } else {
                            break;
                        }
                    }

                } finally {
                    consumer.close();
                }

            } finally {
                session.close();
            }
        } catch (Exception e) {
            logger.warn("Caught exception!", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    logger.warn("Could not close an open connection...", e);
                }
            }
        }
    }

    private static String env(String key, String defaultValue) {
        String rc = System.getProperty(key, null);
        if (rc == null) {
            rc = _env(key, defaultValue);
        }
        return rc;
    }

    private static String _env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if (rc == null) {
            return defaultValue;
        }
        return rc;
    }
}
