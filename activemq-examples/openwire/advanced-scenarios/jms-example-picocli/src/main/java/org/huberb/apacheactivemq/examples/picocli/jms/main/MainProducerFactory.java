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
package org.huberb.apacheactivemq.examples.picocli.jms.main;

import org.huberb.apacheactivemq.examples.picocli.jms.ProducerFactory;
import org.huberb.apacheactivemq.examples.picocli.jms.MessagePropertyAsStringConverter;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport;
import org.huberb.apacheactivemq.examples.picocli.jms.main.MainProducerFactory.QueueSubCommand;
import org.huberb.apacheactivemq.examples.picocli.jms.main.MainProducerFactory.TopicSubCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

/**
 *
 * @author pi
 */
@Command(name = "MainProducerFactory",
        subcommands = {
            QueueSubCommand.class,
            TopicSubCommand.class
        },
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        version = "MainProducerFactory 0.1-SNAPSHOT",
        description = "Send messages to a queue or a topic")
public class MainProducerFactory implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(MainProducerFactory.class);

    //--- activemq
    @CommandLine.Option(names = {"--activemq-userName"}, defaultValue = "admin",
            description = "activemq userName")
    private String userName;
    @CommandLine.Option(names = {"--activemq-password"}, defaultValue = "password",
            description = "activemq password")
    private String password;
    @CommandLine.Option(names = {"--activemq-brokerURL"}, defaultValue = "tcp://localhost:61616",
            paramLabel = "BROKER_URL",
            description = "activemq brokerURL, format tcp://{host}:{port}, eg. `tcp://localost:61616' ")
    private String brokerURL;

    //--- jms session
    @CommandLine.Option(names = {"--jms-session-transacted"}, defaultValue = "true",
            description = "jms session transacted or non-transacted")
    private boolean transacted = true;
    @CommandLine.Option(names = {"--jms-session-acknowledgemode"}, defaultValue = "AUTO_ACKNOWLEDGE",
            description = "jms session acknowledge mode")
    private String acknowledgeMode = "AUTO_ACKNOWLEDGE"; // Session.AUTO_ACKNOWLEDGE

    //--- jms producer
    @CommandLine.Option(names = {"--jms-producer-timetolive"}, defaultValue = "60000",
            description = "jms producer timetolive value in ms, eg. `60000'")
    private long timeToLive = TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS);
    @CommandLine.Option(names = {"--jms-producer-deliverymode"}, defaultValue = "PERSISTENT",
            description = "jms producer deliverymode value [PERSISTENT|NON_PERSISTENT]")
    private String deliveryMode = "PERSISTENT"; // DeliveryMode.PERSISTENT
    @CommandLine.Option(names = {"--jms-producer-priority"}, defaultValue = "4",
            description = "jms producer priortiy, eg. `4'")
    private int priority = 4;

    //--- jms message header
    @CommandLine.Option(names = {"--jms-message-property"},
            description = "jms message property, format {type}:key=value;... type=[boolean|byte|double|float|int|long|object|string|short]")
    private String jmsMessageProperty;

    //--- message
    @CommandLine.Option(names = {"--message-text"}, defaultValue = "Hello, world!",
            description = "read message text from option value")
    private String messageText;
    @CommandLine.Option(names = {"--message-file"},
            description = "read message text from this file")
    private File messageFile;
    @CommandLine.Option(names = {"--message-file-charset"}, defaultValue = "UTF-8",
            description = "read message text using this charset")
    private String messageFileCharset;
    @CommandLine.Option(names = {"--message-stdin"},
            description = "read message text from stdin")
    private boolean messageFromStdin;

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new MainProducerFactory()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }

    @Command(name = "queue",
            mixinStandardHelpOptions = true,
            showDefaultValues = true,
            description = "Send messages to a queue")
    public static class QueueSubCommand implements Callable<Integer> {

        @ParentCommand
        MainProducerFactory mainProducerFactory; // picocli injects reference to parent command
        //--- jms destination
        @CommandLine.Option(names = {"--jms-destination-queue"},
                paramLabel = "QUEUE",
                required = true,
                description = "jms destination queue name")
        private String jmsDestinationQueueName;

        @Override
        public Integer call() throws Exception {
            Integer rc = 0;
            final Map<String, Object> m = new HashMap<>();
            //-- activemq factory props
            m.put("userName", this.mainProducerFactory.userName);
            m.put("password", this.mainProducerFactory.password);
            m.put("brokerURL", this.mainProducerFactory.brokerURL);

            m.put("session.transacted", this.mainProducerFactory.transacted);
            m.put("session.acknowledgeMode", this.mainProducerFactory.acknowledgeMode);

            m.put("producer.timeToLive", this.mainProducerFactory.timeToLive);
            m.put("producer.deliveryMode", this.mainProducerFactory.deliveryMode);
            m.put("producer.priority", this.mainProducerFactory.priority);

            //-- message props
            final Map<String, Object> jmsMessagePropertyMap = new MessagePropertyAsStringConverter().messagePropertyFromStringConverter(this.mainProducerFactory.jmsMessageProperty);
            m.putAll(jmsMessagePropertyMap);

            final ProducerFactorySupport producerFactorySupport = new ProducerFactorySupport(this.mainProducerFactory);
            final String theMessage = producerFactorySupport.evaluateMessageText();
            final List<String> listOfMessageText = Arrays.asList(theMessage);

            if (this.jmsDestinationQueueName != null && !this.jmsDestinationQueueName.isBlank()) {
                final String theQueueName = this.jmsDestinationQueueName;
                producerFactorySupport.verboseSendingMessage(this.mainProducerFactory.brokerURL, theMessage, theQueueName);
                //--- ActiveMQConnectionFactory
                final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
                activeMQConnectionFactory.buildFromMap(m);
                //activeMQConnectionFactory.setClientIDPrefix("clientIDPrefix_Foo");
                //activeMQConnectionFactory.setConnectionIDPrefix("connectionIDPrefix_Bar");
                try {
                    final ProducerFactory producerFactory = new ProducerFactory(activeMQConnectionFactory);
                    producerFactory.sendQueueMessages(m, theQueueName, listOfMessageText);
                } catch (AutoCloseableSupport.JMSRuntimeException jmsrtex) {
                    rc = -2;
                }
            } else {
                logger.warn("No queue-name defined, no jms-message sent!");
                rc = -1;
            }
            return rc;
        }
    }

    @Command(name = "topic",
            mixinStandardHelpOptions = true,
            showDefaultValues = true,
            description = "Send messages to a topic")
    public static class TopicSubCommand implements Callable<Integer> {

        @ParentCommand
        MainProducerFactory mainProducerFactory; // picocli injects reference to parent command
        //--- jms destination
        @CommandLine.Option(names = {"--jms-destination-topic"},
                paramLabel = "TOPIC",
                required = true,
                description = "jms destination topic name")
        private String jmsDestinationTopicName;

        @Override
        public Integer call() throws Exception {
            Integer rc = 0;
            final Map<String, Object> m = new HashMap<>();
            //-- activemq factory props
            m.put("userName", this.mainProducerFactory.userName);
            m.put("password", this.mainProducerFactory.password);
            m.put("brokerURL", this.mainProducerFactory.brokerURL);

            m.put("session.transacted", this.mainProducerFactory.transacted);
            m.put("session.acknowledgeMode", this.mainProducerFactory.acknowledgeMode);

            m.put("producer.timeToLive", this.mainProducerFactory.timeToLive);
            m.put("producer.deliveryMode", this.mainProducerFactory.deliveryMode);
            m.put("producer.priority", this.mainProducerFactory.priority);

            //-- message props
            final Map<String, Object> jmsMessagePropertyMap = new MessagePropertyAsStringConverter().messagePropertyFromStringConverter(this.mainProducerFactory.jmsMessageProperty);
            m.putAll(jmsMessagePropertyMap);

            final ProducerFactorySupport producerFactorySupport = new ProducerFactorySupport(this.mainProducerFactory);
            final String theMessage = producerFactorySupport.evaluateMessageText();
            final List<String> listOfMessageText = Arrays.asList(theMessage);

            if (this.jmsDestinationTopicName != null && !this.jmsDestinationTopicName.isBlank()) {
                final String theTopicName = this.jmsDestinationTopicName;
                producerFactorySupport.verboseSendingMessage(this.mainProducerFactory.brokerURL, theMessage, theTopicName);
                //--- ActiveMQConnectionFactory
                final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
                activeMQConnectionFactory.buildFromMap(m);
                //activeMQConnectionFactory.setClientIDPrefix("clientIDPrefix_Foo");
                //activeMQConnectionFactory.setConnectionIDPrefix("connectionIDPrefix_Bar");
                try {
                    final ProducerFactory producerFactory = new ProducerFactory(activeMQConnectionFactory);
                    producerFactory.sendTopicMessages(m, theTopicName, listOfMessageText);
                } catch (AutoCloseableSupport.JMSRuntimeException jmsrtex) {
                    rc = -2;
                }
            } else {
                logger.warn("No topic-name defined, no jms-message sent!");
                rc = -1;
            }

            return rc;

        }
    }

    static class ProducerFactorySupport {

        final MainProducerFactory mainProducerFactory;

        public ProducerFactorySupport(MainProducerFactory mainProducerFactory) {
            this.mainProducerFactory = mainProducerFactory;
        }

        void verboseSendingMessage(String theBrokerURL, String theMessage, String theDestination) {
            final String abbrevMessage = theMessage.length() > 120 ? theMessage.substring(0, 120) + "..." : theMessage;
            logger.info(String.format("Sending message `%s' to brokerURL `%s' into destination `%s'", abbrevMessage, theBrokerURL, theDestination));
        }

        /**
         * Evaluate messageText either via messageText, messageFile, stdin.
         *
         * @return messsageText used for sending to AMQ.
         *
         */
        String evaluateMessageText() {
            final String theMessageText;

            if (this.mainProducerFactory.messageFromStdin) {
                final EvaluateMessageText emt = EvaluateMessageText.createForCapturingStdin();
                theMessageText = emt.build();
            } else if (this.mainProducerFactory.messageFile != null) {
                final EvaluateMessageText emt = EvaluateMessageText.createForCapturingFile(
                        this.mainProducerFactory.messageFile,
                        this.mainProducerFactory.messageFileCharset);
                theMessageText = emt.build();

            } else if (this.mainProducerFactory.messageText != null) {
                final EvaluateMessageText emt = EvaluateMessageText.createForCapturingString(
                        this.mainProducerFactory.messageText);
                theMessageText = emt.build();
            } else {
                theMessageText = "";
            }

            return theMessageText;
        }
    }
}
