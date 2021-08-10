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

class Publisher {

    public static void main(String[] args) throws JMSException {

        final String user = env("ACTIVEMQ_USER", "admin");
        final String password = env("ACTIVEMQ_PASSWORD", "password");
        final String host = env("ACTIVEMQ_HOST", "localhost");
        final int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61616"));
        final boolean transacted = Boolean.parseBoolean(env("TRANSACTED", "false"));
        final int deliveryMode = env("DELIVERY_MODE", "PERSISTENT").equals("PERSISTENT")
                ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
        final long timeToLive = Long.parseLong(env("TIME_TO_LIVE", "60000"));
        final String destination = arg(args, 0, "event");
        final String url = "tcp://" + host + ":" + port;

        System.out.println(String.format("Running with:%n"
                + "url: %s, %n"
                + "user: %s, [ACTIVEMQ_USER]%n"
                + "password: %s, [ACTIVEMQ_PASSWORD]%n"
                + "host: %s, [ACTIVEMQ_HOST]%n"
                + "port: %d, [ACTIVEMQ_PORT]%n"
                + "queueName: %s, [QUEUE]%n"
                + "transacted: %b, [TRANSACTED]%n"
                + "deliveryMode: %d (PERSISTENT %d, NON_PERSISTENT %d), [DELIVERY_MODE]%n"
                + "timeToLive [ms] %d [TIME_TO_LIVE]%n",
                url, user, password,
                host, port,
                destination,
                transacted,
                deliveryMode, DeliveryMode.PERSISTENT, DeliveryMode.NON_PERSISTENT,
                timeToLive
        ));

        final int messages = 10000;
        final int size = 256;

        final String DATA = "abcdefghijklmnopqrstuvwxyz";
        String body = "";
        for (int i = 0; i < size; i++) {
            body += DATA.charAt(i % DATA.length());
        }

        final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();

        final Connection connection = factory.createConnection(user, password);
        try {
            connection.start();
            final Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            try {
                final Destination dest = new ActiveMQTopic(destination);
                final MessageProducer producer = session.createProducer(dest);
                producer.setDeliveryMode(deliveryMode);
                producer.setTimeToLive(timeToLive);
                try {
                    for (int i = 1; i <= messages; i++) {
                        TextMessage msg = session.createTextMessage(body);
                        msg.setIntProperty("id", i);
                        producer.send(msg);
                        if ((i % 1000) == 0) {
                            System.out.println(String.format("Sent %d messages", i));
                        }
                    }
                    producer.send(session.createTextMessage("SHUTDOWN"));

                } finally {
                    producer.close();
                }
                if (transacted) {
                    session.commit();
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
