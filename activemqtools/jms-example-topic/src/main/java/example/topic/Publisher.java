/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.topic;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
public class Publisher {

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

    private static final long DELAY = 100;

    public static void main(String[] args) {
        final String user = env("ACTIVEMQ_USER", "admin");
        final String password = env("ACTIVEMQ_PASSWORD", "password");
        final String host = env("ACTIVEMQ_HOST", "localhost");
        final int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
        final String topicName = env("TOPIC", "test-topic");
        final boolean transacted = Boolean.parseBoolean(env("TRANSACTED", "false"));
        final int deliveryMode = env("DELIVERY_MODE", "PERSISTENT").equals("PERSISTENT")
                ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
        final long timeToLive = Long.parseLong(env("TIME_TO_LIVE", "60000"));
        int numMessages = Integer.parseInt(env("NUM_MESSAGES", "100"));
        if (numMessages < 0) {
            numMessages = 0;
        }

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
                + "topic: %s, [TOPIC]%n"
                + "transacted: %b, [TRANSACTED]%n"
                + "deliveryMode: %d (PERSISTENT %d, NON_PERSISTENT %d), [DELIVERY_MODE]%n"
                + "timeToLive: %d ms [TIME_TO_LIVE]%n"
                + "numMessages: %d [NUM_MESSAGES]",
                url, user, password,
                host, port,
                topicName,
                transacted,
                deliveryMode, DeliveryMode.PERSISTENT, DeliveryMode.NON_PERSISTENT,
                timeToLive,
                numMessages
        ));

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            connection.start();

            final Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            try {
                final Destination destination = session.createTopic(topicName);
                final MessageProducer producer = session.createProducer(destination);
                try {
                    producer.setTimeToLive(timeToLive);
                    producer.setDeliveryMode(deliveryMode);

                    for (int i = 0; i < numMessages; i++) {
                        TextMessage message = session.createTextMessage("Message #" + i);
                        logger.info("Sending message #{}", i);
                        producer.send(message);
                        Thread.sleep(DELAY);
                    }

                    // tell the subscribers we're done
                    producer.send(session.createTextMessage("END"));

                } finally {
                    producer.close();
                }
                if (transacted) {
                    session.commit();
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
