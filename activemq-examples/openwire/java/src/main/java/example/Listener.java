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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;

class Listener {

    public static void main(String[] args) throws JMSException {

        final String user = env("ACTIVEMQ_USER", "admin");
        final String password = env("ACTIVEMQ_PASSWORD", "password");
        final String host = env("ACTIVEMQ_HOST", "localhost");
        final int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
        final boolean transacted = Boolean.parseBoolean(env("TRANSACTED", "false"));
        final String destination = arg(args, 0, "event");
        final String url = "tcp://" + host + ":" + port;

        System.out.println(String.format("Running with:%n"
                + "url: %s, %n"
                + "user: %s, [ACTIVEMQ_USER]%n"
                + "password: %s, [ACTIVEMQ_PASSWORD]%n"
                + "host: %s, [ACTIVEMQ_HOST]%n"
                + "port: %d, [ACTIVEMQ_PORT]%n"
                + "queueName: %s, [QUEUE]%n"
                + "transacted: %b, [TRANSACTED]%n",
                url, user, password,
                host, port,
                destination,
                transacted
        ));
        final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);

        final Connection connection = factory.createConnection(user, password);
        try {
            connection.start();
            final Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            try {
                final Destination dest = new ActiveMQTopic(destination);

                final MessageConsumer consumer = session.createConsumer(dest);
                try {
                    long start = System.currentTimeMillis();
                    long count = 1;
                    System.out.println("Waiting for messages...");
                    while (true) {
                        final Message msg = consumer.receive();
                        if (msg instanceof TextMessage) {
                            final String body = ((TextMessage) msg).getText();
                            if ("SHUTDOWN".equals(body)) {
                                long diff = System.currentTimeMillis() - start;
                                System.out.println(String.format("Received %d in %.2f seconds", count, (1.0 * diff / 1000.0)));
                                break;
                            } else {
                                if (count != msg.getIntProperty("id")) {
                                    System.out.println("mismatch: " + count + "!=" + msg.getIntProperty("id"));
                                }
                                count = msg.getIntProperty("id");

                                if (count == 0) {
                                    start = System.currentTimeMillis();
                                }
                                if (count % 1000 == 0) {
                                    System.out.println(String.format("Received %d messages.", count));
                                }
                                count++;
                            }
                        } else {
                            System.out.println("Unexpected message type: " + msg.getClass());
                        }
                    }
                } finally {
                    consumer.close();
                }
            } finally {
                session.close();
            }
        } finally {
            connection.close();
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
