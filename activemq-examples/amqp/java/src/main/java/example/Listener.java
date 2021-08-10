/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example;

import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.policy.JmsDefaultPrefetchPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Listener {

    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    public static void main(String[] args) throws Exception {
        final String TOPIC_PREFIX = "topic://";

        String user = env("ACTIVEMQ_USER", "admin");
        String password = env("ACTIVEMQ_PASSWORD", "password");
        String host = env("ACTIVEMQ_HOST", "localhost");
        int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));
        final String topicName = env("TOPIC", null);
        final String queueName = env("QUEUE", null);
        final String destinationName;
        if (topicName != null) {
            destinationName = topicName;
        } else if (queueName != null) {
            destinationName = queueName;
        } else {
            destinationName = "topic://event";
        }
        final boolean transacted = Boolean.parseBoolean(env("TRANSACTED", "false"));

        String connectionURI = "amqp://" + host + ":" + port;
        if (args.length > 0) {
            connectionURI = args[0].trim();
        }

        logger.info(String.format("Running with:%n"
                + "connectionURI: %s, %n"
                + "user: %s, [ACTIVEMQ_USER]%n"
                + "password: %s, [ACTIVEMQ_PASSWORD]%n"
                + "host: %s, [ACTIVEMQ_HOST]%n"
                + "port: %d, [ACTIVEMQ_PORT]%n"
                + "destinationName: %s, [QUEUE, TOPIC]%n"
                + "transacted: %b, [TRANSACTED]%n",
                connectionURI, user, password,
                host, port,
                destinationName,
                transacted
        ));

        final JmsConnectionFactory factory = new JmsConnectionFactory(connectionURI);
        final JmsDefaultPrefetchPolicy prefetchPolicy = new JmsDefaultPrefetchPolicy();
        prefetchPolicy.setAll(100);
        factory.setPrefetchPolicy(prefetchPolicy);
        try (final Connection connection = factory.createConnection(user, password)) {
            connection.start();
            try (final Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE)) {

                final Destination destination;
                if (destinationName.startsWith(TOPIC_PREFIX)) {
                    destination = session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
                } else {
                    destination = session.createQueue(destinationName);
                }

                try (final MessageConsumer consumer = session.createConsumer(destination)) {
                    long start = System.currentTimeMillis();
                    long count = 1;
                    logger.info("Waiting for messages...");
                    while (true) {
                        final Message msg = consumer.receive();
                        if (msg instanceof TextMessage) {
                            final String body = ((TextMessage) msg).getText();
                            if ("SHUTDOWN".equals(body)) {
                                long diff = System.currentTimeMillis() - start;
                                logger.info(String.format("Received %d in %.2f seconds", count, (1.0d * diff / 1000.0d)));
                                TimeUnit.MILLISECONDS.sleep(10L);
                                break;
                            } else {
                                try {
                                    if (count != msg.getIntProperty("id")) {
                                        logger.info("mismatch: " + count + "!=" + msg.getIntProperty("id"));
                                    }
                                } catch (NumberFormatException ignore) {
                                }

                                if (count == 1) {
                                    start = System.currentTimeMillis();
                                    logger.info(String.format("Start receiving messages."));
                                } else if (count % 100 == 0) {
                                    logger.info(String.format("Received %d messages.", count));
                                }
                                count++;
                            }

                        } else {
                            logger.info("Unexpected message type: " + msg.getClass());
                        }
                    }
                }
                if (transacted) {
                    session.commit();
                }
            } // session
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

    private static String arg(String[] args, int index, String defaultValue) {
        if (index < args.length) {
            return args[index];
        } else {
            return defaultValue;
        }
    }
}
