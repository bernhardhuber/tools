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
package example.browser;

import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
public class Browser {

    private static final Logger logger = LoggerFactory.getLogger(Browser.class);

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final Boolean NON_TRANSACTED = false;
    private static final long DELAY = 100;

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

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        connectionFactory.setClientIDPrefix(Browser.class.getName());
        connectionFactory.setConnectionIDPrefix(Browser.class.getName());

        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            connection.start();

            final Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            try {
                final Queue destination = session.createQueue(queueName);
                final QueueBrowser browser = session.createBrowser(destination);
                try {
                    int i = 0;
                    for (final Enumeration enumeration = browser.getEnumeration(); enumeration.hasMoreElements(); i += 1) {
                        final TextMessage message = (TextMessage) enumeration.nextElement();
                        logger.info("Browsing: " + message);
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                    }
                    logger.info(String.format("Browsed #%d messages", i));
                } finally {
                    browser.close();
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
