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
package org.huberb.apacheactivemq.examples.picocli;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Make jms-examples available via a single command line interface.
 *
 * @author pi
 */
@Command(name = "Main0",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        version = "Main0 0.1-SNAPSHOT",
        description = "Invoke activemq jms operations as provided by activemq-exaplemes openwire")
public class Main0 implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(Main0.class);

    //--- activemq 
    @CommandLine.Option(names = {"--activemq-user"}, defaultValue = "admin", description = "activemq user")
    private String user;
    @CommandLine.Option(names = {"--activemq-password"}, defaultValue = "password", description = "activemq password")
    private String password;

    @CommandLine.Option(names = {"--activemq-brokerURL"}, defaultValue = "tcp://localhost:61616", description = "activemq brokerURL")
    private String brokerURL;

    @CommandLine.Option(names = {"--activemq-host"}, defaultValue = "localhost", description = "activemq host")
    private String host;
    @CommandLine.Option(names = {"--activemq-port"}, defaultValue = "61616", description = "activemq port")
    private Integer port;

    //--- jms
    @CommandLine.Option(names = {"--jms-queue"}, defaultValue = "test-queue", description = "jms destination queue")
    private String queueName;
    @CommandLine.Option(names = {"--jms-topic"}, defaultValue = "test-topic", description = "jms destination topic")
    private String topicName;

    @CommandLine.Option(names = {"--jms-transacted"}, defaultValue = "false", description = "jms transacted ")
    private Boolean transacted;
    @CommandLine.Option(names = {"--jms-deliverymode"}, defaultValue = "NON_PERSISTENT", description = "jms delivery mode [PERSISTENT|NON_PERSISTENT]")
    private String deliveryMode;

    @CommandLine.Option(names = {"--jms-timetolive"}, defaultValue = "60000", description = "jms timetolive [ms]")
    private Long timeToLive;

    //--- message
    @CommandLine.Option(names = {"--nummessages"}, defaultValue = "100", description = "count of messages to produce")
    private Integer numMessages;

    @CommandLine.Option(names = {"--command", "-c"}, required = true, description = "command ["
            + "queueProducer|queueConsumer|"
            + "topicPublisher|topicSubscriber|"
            + "advisory|"
            + "durSubPublisher|durSubSubscriber|"
            + "browser|browserProducer|"
            + "tempDestConsumer|tempDestProducerRequestReply"
            + "]")
    private String command;

    public static void main(String[] args) {

        final int exitCode = new CommandLine(new Main0()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        //---
        logger.info(String.format("Parameters:%n"
                + "command %s, "
                + "user %s, password %s, "
                + "brokerURL %s, "
                + "host %s, port %d, "
                + "queue %s, topic %s, "
                + "transacted %s, deliveryMode %s, timeToLive %d"
                + "numMessages %d",
                this.command,
                this.user, this.password,
                this.brokerURL,
                this.host, this.port,
                this.queueName, this.topicName,
                this.transacted,
                this.deliveryMode,
                this.timeToLive,
                this.numMessages));
        final Map m = createMapForSystemProperties();
        System.getProperties().putAll(m);
        final String[] args = createArgsArray(this.brokerURL);

        if ("queueProducer".equalsIgnoreCase(command)) {
            example.queue.Producer.main(args);
        } else if ("queueConsumer".equalsIgnoreCase(command)) {
            example.queue.Consumer.main(args);
        } else if ("advisory".equalsIgnoreCase(command)) {
            System.getProperties().put("TOPIC", "ActiveMQ.Advisory.>");
            example.advisory.AdvisorySubscriber.main(args);
        } else if ("topicPublisher".equalsIgnoreCase(command)) {
            example.topic.Publisher.main(args);
        } else if ("topicSubscriber".equalsIgnoreCase(command)) {
            example.topic.Subscriber.main(args);
        } else if ("durSubPublisher".equalsIgnoreCase(command)) {
            example.topic.durable.Publisher.main(args);
        } else if ("durSubSubscriber".equalsIgnoreCase(command)) {
            example.topic.durable.Subscriber.main(args);
        } else if ("browser".equalsIgnoreCase(command)) {
            example.browser.Browser.main(args);
        } else if ("browserProducer".equalsIgnoreCase(command)) {
            example.browser.Producer.main(args);
        } else if ("tempDestConsumer".equalsIgnoreCase(command)) {
            example.tempdest.Consumer.main(args);
        } else if ("tempDestProducerRequestReply".equalsIgnoreCase(command)) {
            example.tempdest.ProducerRequestReply.main(args);
        } else {
            logger.warn(String.format("Unknown command %s", command));
        }
        return 0;
    }

    Map createMapForSystemProperties() {
        final Map m = new HashMap<>();
        m.put("ACTIVEMQ_USER", this.user);
        m.put("ACTIVEMQ_PASSWORD", this.password);

        m.put("ACTIVEMQ_HOST", this.host);
        m.put("ACTIVEMQ_PORT", this.port.toString());

        m.put("QUEUE", "" + this.queueName);
        m.put("TOPIC", "" + this.topicName);

        m.put("TRANSACTED", "" + this.transacted.toString());
        m.put("DELIVERY_MODE", "" + this.deliveryMode);
        m.put("TIME_TO_LIVE", "" + this.timeToLive.toString());
        m.put("NUM_MESSAGES", "" + this.numMessages.toString());
        return m;
    }

    String[] createArgsArray(String arg0) {
        final String[] args;
        if (arg0 != null && !arg0.isBlank()) {
            args = new String[]{arg0};
        } else {
            args = new String[]{};
        }
        return args;
    }
}
