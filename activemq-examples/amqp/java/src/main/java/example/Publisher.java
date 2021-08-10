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

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.policy.JmsDefaultPrefetchPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Publisher {

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

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
        final int deliveryMode = env("DELIVERY_MODE", "PERSISTENT").equals("PERSISTENT")
                ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
        final long timeToLive = Long.parseLong(env("TIME_TO_LIVE", "60000"));
        int numMessages = Integer.parseInt(env("NUM_MESSAGES", "200"));
        if (numMessages < 0) {
            numMessages = 0;
        }

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
                + "destinationName: %s, [QUEUE,TOPIC]%n"
                + "transacted: %b, [TRANSACTED]%n"
                + "deliveryMode: %d (PERSISTENT %d, NON_PERSISTENT %d), [DELIVERY_MODE]%n"
                + "timeToLive: %d ms [TIME_TO_LIVE]%n"
                + "numMessages: %d [NUM_MESSAGES]",
                connectionURI, user, password,
                host, port,
                destinationName,
                transacted,
                deliveryMode, DeliveryMode.PERSISTENT, DeliveryMode.NON_PERSISTENT,
                timeToLive,
                numMessages
        ));

        final String DATA = "abcdefghijklmnopqrstuvwxyz";

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

                try (final MessageProducer producer = session.createProducer(destination)) {
                    producer.setDeliveryMode(deliveryMode);
                    producer.setTimeToLive(timeToLive);

                    for (int i = 1; i <= numMessages; i++) {
                        final String textMessageBody = String.format("#%d: %s", i, DATA);
                        final TextMessage textMessage = session.createTextMessage(textMessageBody);
                        textMessage.setJMSDeliveryMode(deliveryMode);
                        textMessage.setJMSExpiration(timeToLive);

                        textMessage.setIntProperty("id", i);
                        producer.send(textMessage);

                        if ((i % 100) == 0) {
                            logger.info(String.format("Sent %d messages", i));
                            if (transacted) {
                                session.commit();
                            }
                        }
                    }
                    producer.send(session.createTextMessage("SHUTDOWN"));

                    if (transacted) {
                        session.commit();
                    }
                }
            }
        }
        System.exit(0);
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
