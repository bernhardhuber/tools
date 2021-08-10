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
package example.advisory;

import java.util.concurrent.CountDownLatch;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
public class AdvisorySubscriber implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(AdvisorySubscriber.class);

    public AdvisorySubscriber() {
    }

    public static void main(String[] args) {
        final String user = env("ACTIVEMQ_USER", "admin");
        final String password = env("ACTIVEMQ_PASSWORD", "password");
        final String host = env("ACTIVEMQ_HOST", "localhost");
        final int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
        final String topicName = env("TOPIC", "ActiveMQ.Advisory.>");
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
                + "topicName: %s, [TOPIC]%n"
                + "transacted: %b, [TRANSACTED]%n",
                url, user, password,
                host, port,
                topicName,
                transacted
        ));

        logger.info("Waiting to receive messages... Either waiting for END message or press Ctrl+C to exit");
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        Connection connection = null;
        final CountDownLatch latch = new CountDownLatch(1);

        try {

            connection = connectionFactory.createConnection();
            connection.start();

            final Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            final Destination destination = new ActiveMQTopic(topicName);

            final MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(new AdvisorySubscriber());

            latch.await();
            consumer.close();
            session.close();

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

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ActiveMQMessage) {
                ActiveMQMessage amqMessage = (ActiveMQMessage) message;
                logger.info("Received amqMessage: {}", amqMessage.toString());
                amqMessage.getDataStructure();
            }
        } catch (Exception e) {
            logger.warn("onMessage got a JMS Exception!", e);
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
