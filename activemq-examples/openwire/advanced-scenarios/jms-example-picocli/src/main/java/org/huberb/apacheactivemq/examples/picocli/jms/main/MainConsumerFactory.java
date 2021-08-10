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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import javax.jms.Message;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.JMSRuntimeException;
import org.huberb.apacheactivemq.examples.picocli.jms.ConsumerDurableTopicOnlyFactory;
import org.huberb.apacheactivemq.examples.picocli.jms.ConsumerQueueOnlyFactory;
import org.huberb.apacheactivemq.examples.picocli.jms.ConsumerTopicOnlyFactory;
import org.huberb.apacheactivemq.examples.picocli.jms.main.MainConsumerFactory.QueueSubCommand;
import org.huberb.apacheactivemq.examples.picocli.jms.main.MainConsumerFactory.TopicSubCommand;
import org.huberb.apacheactivemq.examples.picocli.jms.main.MainConsumerFactory.DurableTopicSubCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.ParentCommand;

/**
 *
 * @author pi
 */
@Command(name = "MainConsumerFactory",
        subcommands = {
            QueueSubCommand.class,
            TopicSubCommand.class,
            DurableTopicSubCommand.class
        },
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        version = "MainConsumerFactory 0.1-SNAPSHOT",
        description = "Receive messages from a queue or a topic")
public class MainConsumerFactory implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(MainConsumerFactory.class);

//    //--- activemq
//    @CommandLine.Option(names = {"--activemq-userName"}, defaultValue = "admin",
//            description = "activemq userName")
//    private String userName;
//    @CommandLine.Option(names = {"--activemq-password"}, defaultValue = "password",
//            description = "activemq password")
//    private String password;
//    @CommandLine.Option(names = {"--activemq-brokerURL"}, defaultValue = "tcp://localhost:61616",
//            paramLabel = "BROKER_URL",
//            description = "activemq brokerURL, format tcp://{host}:{port}, eg. `tcp://localost:61616' ")
//    private String brokerURL;
    // adds the options defined in ReusableOptions to this command
    @Mixin
    private ActivemqOptions activemqOptions;

    //--- jms session
    @CommandLine.Option(names = {"--jms-session-transacted"}, defaultValue = "true",
            description = "jms session transacted or non-transacted")
    private boolean transacted = true;
    @CommandLine.Option(names = {"--jms-session-acknowledgemode"}, defaultValue = "AUTO_ACKNOWLEDGE",
            description = "jms session acknowledge mode")
    private String acknowledgeMode = "AUTO_ACKNOWLEDGE"; // Session.AUTO_ACKNOWLEDGE
    @CommandLine.Option(names = {"--jms-message-selector"},
            paramLabel = "MESSAGE-SELECTOR",
            required = false,
            description = "jms message-selector")
    private String jmsMessageSelector;
    @CommandLine.Option(names = {"--jms-max-receive-count"},
            paramLabel = "MAX-RECEIVE-COUNT",
            required = false,
            defaultValue = "1",
            description = "terminate after receiving MAX_RECEIVE_COUNT advisory messages")
    private int maxReceiveCount;
    @CommandLine.Option(names = {"--jms-max-waittime-seconds"},
            paramLabel = "MAX-WAITTIME-SECONDS",
            required = false,
            defaultValue = "300",
            description = "max time waiting for a message")
    private int maxWaittimeSeconds;

    public static void main(String[] args) {

        final int exitCode = new CommandLine(new MainConsumerFactory()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }

    ActiveMQConnectionFactory createActiveMQConnectionFactory(final Map<String, Object> m) {
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.buildFromMap(m);

        activeMQConnectionFactory.setClientIDPrefix(this.getClass().getSimpleName());
        activeMQConnectionFactory.setConnectionIDPrefix(this.getClass().getSimpleName());
        final ActiveMQPrefetchPolicy activeMQPrefetchPolicy = new ActiveMQPrefetchPolicy();
        activeMQPrefetchPolicy.setQueuePrefetch(1);
        activeMQPrefetchPolicy.setTopicPrefetch(1);
        activeMQPrefetchPolicy.setDurableTopicPrefetch(1);
        activeMQPrefetchPolicy.setOptimizeDurableTopicPrefetch(1);
        activeMQConnectionFactory.setPrefetchPolicy(activeMQPrefetchPolicy);
        {
            final Properties activeMQConnectionFactoryProperties = new Properties();
            activeMQConnectionFactory.populateProperties(activeMQConnectionFactoryProperties);
            activeMQConnectionFactoryProperties.forEach((k, v) -> {
                logger.info(String.format("activeMQConnectionFactoryProperties key %s, value %s", k, v));
            });
        }
        return activeMQConnectionFactory;
    }

    @Command(name = "queue",
            mixinStandardHelpOptions = true,
            showDefaultValues = true,
            description = "Receive messages from a queue")
    static class QueueSubCommand implements Callable<Integer> {

        @ParentCommand
        MainConsumerFactory mainConsumerFactory; // picocli injects reference to parent command
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
            m.put("userName", this.mainConsumerFactory.activemqOptions.getUserName());
            m.put("password", this.mainConsumerFactory.activemqOptions.getPassword());
            m.put("brokerURL", this.mainConsumerFactory.activemqOptions.getBrokerURL());

            m.put("session.transacted", this.mainConsumerFactory.transacted);
            m.put("session.acknowledgeMode", this.mainConsumerFactory.acknowledgeMode);

            //-- message props
//            final Map<String, Object> jmsMessagePropertyMap = new MessagePropertyAsStringConverter().messagePropertyFromStringConverter(this.mainConsumerFactory.jmsMessageProperty);
//            m.putAll(jmsMessagePropertyMap);
            final ConsumerFactorySupport consumerFactorySupport = new ConsumerFactorySupport(this.mainConsumerFactory);

            if (this.jmsDestinationQueueName != null && !this.jmsDestinationQueueName.isBlank()) {
                final String theQueueName = this.jmsDestinationQueueName;
                consumerFactorySupport.verboseReceivingMessage(this.mainConsumerFactory.activemqOptions.getBrokerURL(), theQueueName);
                //--- ActiveMQConnectionFactory
                final ActiveMQConnectionFactory activeMQConnectionFactory = mainConsumerFactory.createActiveMQConnectionFactory(m);
                try {
                    final ConsumerQueueOnlyFactory consumerQueueOnlyFactory = new ConsumerQueueOnlyFactory(activeMQConnectionFactory);
                    final String theJmsMessageSelector = this.mainConsumerFactory.jmsMessageSelector;
                    final int theMaxReceiveCount = this.mainConsumerFactory.maxReceiveCount;
                    final int theMaxWaittimeSeconds = this.mainConsumerFactory.maxWaittimeSeconds;
                    final Optional<Message> optMessage = consumerQueueOnlyFactory.consumeQueueMessage(
                            m, theQueueName,
                            theJmsMessageSelector,
                            theMaxReceiveCount,
                            theMaxWaittimeSeconds
                    );
                    consumerFactorySupport.verboseReceivedMessage(optMessage);
                } catch (JMSRuntimeException jmsrtex) {
                    logger.warn("receiving", jmsrtex);
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
            description = "Receive messages from a topic")
    public static class TopicSubCommand implements Callable<Integer> {

        @ParentCommand
        MainConsumerFactory mainConsumerFactory; // picocli injects reference to parent command
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
            m.put("userName", this.mainConsumerFactory.activemqOptions.getUserName());
            m.put("password", this.mainConsumerFactory.activemqOptions.getPassword());
            m.put("brokerURL", this.mainConsumerFactory.activemqOptions.getBrokerURL());

            m.put("session.transacted", this.mainConsumerFactory.transacted);
            m.put("session.acknowledgeMode", this.mainConsumerFactory.acknowledgeMode);

            //-- message props
//            final Map<String, Object> jmsMessagePropertyMap = new MessagePropertyAsStringConverter().messagePropertyFromStringConverter(this.mainConsumerFactory.jmsMessageProperty);
//            m.putAll(jmsMessagePropertyMap);
            final ConsumerFactorySupport consumerFactorySupport = new ConsumerFactorySupport(this.mainConsumerFactory);

            if (this.jmsDestinationTopicName != null && !this.jmsDestinationTopicName.isBlank()) {
                final String theTopicName = this.jmsDestinationTopicName;
                consumerFactorySupport.verboseReceivingMessage(this.mainConsumerFactory.activemqOptions.getBrokerURL(), theTopicName);
                //--- ActiveMQConnectionFactory
                final ActiveMQConnectionFactory activeMQConnectionFactory = mainConsumerFactory.createActiveMQConnectionFactory(m);

                try {
                    final ConsumerTopicOnlyFactory consumerTopicOnlyFactory = new ConsumerTopicOnlyFactory(activeMQConnectionFactory);
                    final String theJmsMessageSelector = this.mainConsumerFactory.jmsMessageSelector;
                    final int theMaxReceiveCount = this.mainConsumerFactory.maxReceiveCount;
                    final int theMaxWaittimeSeconds = this.mainConsumerFactory.maxWaittimeSeconds;
                    final Optional<Message> optMessage = consumerTopicOnlyFactory.consumeTopicMessage(
                            m, theTopicName,
                            theJmsMessageSelector,
                            theMaxReceiveCount,
                            theMaxWaittimeSeconds
                    );
                    consumerFactorySupport.verboseReceivedMessage(optMessage);
                } catch (JMSRuntimeException jmsrtex) {
                    logger.warn("receiving", jmsrtex);
                    rc = -2;
                }
            } else {
                logger.warn("No topic-name defined, no jms-message sent!");
                rc = -1;
            }

            return rc;

        }
    }

    @Command(name = "durableTopic",
            mixinStandardHelpOptions = true,
            showDefaultValues = true,
            description = "Receive messages from a durable subscribed topic")
    public static class DurableTopicSubCommand implements Callable<Integer> {

        @ParentCommand
        MainConsumerFactory mainConsumerFactory; // picocli injects reference to parent command
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
            m.put("userName", this.mainConsumerFactory.activemqOptions.getUserName());
            m.put("password", this.mainConsumerFactory.activemqOptions.getPassword());
            m.put("brokerURL", this.mainConsumerFactory.activemqOptions.getBrokerURL());

            m.put("session.transacted", this.mainConsumerFactory.transacted);
            m.put("session.acknowledgeMode", this.mainConsumerFactory.acknowledgeMode);

            //-- message props
//            final Map<String, Object> jmsMessagePropertyMap = new MessagePropertyAsStringConverter().messagePropertyFromStringConverter(this.mainConsumerFactory.jmsMessageProperty);
//            m.putAll(jmsMessagePropertyMap);
            final ConsumerFactorySupport consumerFactorySupport = new ConsumerFactorySupport(this.mainConsumerFactory);

            if (this.jmsDestinationTopicName != null && !this.jmsDestinationTopicName.isBlank()) {
                final String theTopicName = this.jmsDestinationTopicName;
                consumerFactorySupport.verboseReceivingMessage(this.mainConsumerFactory.activemqOptions.getBrokerURL(), theTopicName);
                //--- ActiveMQConnectionFactory
                final ActiveMQConnectionFactory activeMQConnectionFactory = mainConsumerFactory.createActiveMQConnectionFactory(m);
                // *MUST* set clientID
                activeMQConnectionFactory.setClientID(this.getClass().getSimpleName() + "-clientId");
                try {
                    final ConsumerDurableTopicOnlyFactory consumerDurableTopicOnlyFactory = new ConsumerDurableTopicOnlyFactory(activeMQConnectionFactory);
                    final String theJmsMessageSelector = this.mainConsumerFactory.jmsMessageSelector;
                    final int theMaxReceiveCount = this.mainConsumerFactory.maxReceiveCount;
                    final Optional<Message> optMessage = consumerDurableTopicOnlyFactory.consumeTopicMessageAsDurableSubscriber(
                            m, theTopicName,
                            theJmsMessageSelector, theMaxReceiveCount
                    );
                    consumerFactorySupport.verboseReceivedMessage(optMessage);
                } catch (JMSRuntimeException jmsrtex) {
                    logger.warn("receiving", jmsrtex);
                    rc = -2;
                }
            } else {
                logger.warn("No topic-name defined, no jms-message sent!");
                rc = -1;
            }

            return rc;

        }
    }

    static class ConsumerFactorySupport {

        final MainConsumerFactory mainConsumerFactory;

        public ConsumerFactorySupport(MainConsumerFactory mainProducerFactory) {
            this.mainConsumerFactory = mainProducerFactory;
        }

        void verboseReceivingMessage(String theBrokerURL, String theDestination) {
            logger.info(String.format("Receiving message using brokerURL `%s' from destination `%s'", theBrokerURL, theDestination));
        }

        void verboseReceivedMessage(Optional<Message> optMessage) {
            if (optMessage.isPresent()) {
                logger.info(String.format("Received message `%s'", optMessage.get()));
            } else {
                logger.info("Received no message");
            }
        }
    }
}
