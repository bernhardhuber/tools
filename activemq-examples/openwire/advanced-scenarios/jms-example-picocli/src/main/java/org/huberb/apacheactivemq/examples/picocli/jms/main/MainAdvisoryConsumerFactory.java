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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.jms.Message;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.huberb.apacheactivemq.examples.picocli.jms.AdvisoryConsumerFactory;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.JMSRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 *
 * @author pi
 */
@CommandLine.Command(name = "MainAdvisoryConsumer",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        version = "MainAdvisoryConsumer 0.1-SNAPSHOT",
        description = "Consume messages from advisory topics")
public class MainAdvisoryConsumerFactory implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(MainAdvisoryConsumerFactory.class);

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

//    //--- jms message header
//    @CommandLine.Option(names = {"--jms-message-property"},
//            description = "jms message property, format {type}:key=value;... type=[boolean|byte|double|float|int|long|object|string|short]")
//    private String jmsMessageProperty;
    //--- jms destination
    @CommandLine.Option(names = {"--jms-destination-advisory-topic"},
            paramLabel = "ADVISORY_TOPIC_NAME",
            required = false,
            defaultValue = "ActiveMQ.Advisory.>",
            description = "jms advisory topic name")
    private String jmsDestinationAdvisoryTopicName;
    @CommandLine.Option(names = {"--jms-message-selector"},
            paramLabel = "MESSAGE-SELECTOR",
            required = false,
            description = "jms message-selector")
    private String jmsMessageSelector;
    @CommandLine.Option(names = {"--jms-max-receive-count"},
            paramLabel = "MAX-RECEIVE-COUNT",
            required = false,
            defaultValue = "-1",
            description = "terminate after receiving MAX_RECEIVE_COUNT advisory messages")
    private int maxReceiveCount = -1;
    @CommandLine.Option(names = {"--jms-max-waittime-seconds"},
            paramLabel = "MAX-WAITTIME-SECONDS",
            required = false,
            defaultValue = "300",
            description = "max time waiting for a message")
    private int maxWaittimeSeconds;

    public static void main(String[] args) {

        final int exitCode = new CommandLine(new MainAdvisoryConsumerFactory()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        Integer rc = 0;
        final Map<String, Object> m = new HashMap<>();
        //-- message props
        //final Map<String, Object> jmsMessagePropertyMap = new MessagePropertyAsStringConverter().messagePropertyFromStringConverter(this.jmsMessageProperty);
        //m.putAll(jmsMessagePropertyMap);
        if (this.jmsDestinationAdvisoryTopicName != null && !this.jmsDestinationAdvisoryTopicName.isBlank()) {
            final String theQueueName = this.jmsDestinationAdvisoryTopicName;
            verboseReceivingFromAdvisoryTopic(this.brokerURL, theQueueName, this.jmsMessageSelector);
            //--- ActiveMQConnectionFactory
            final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
            activeMQConnectionFactory.buildFromMap(m);
            try {
                final AdvisoryConsumerFactory browserFactory = new AdvisoryConsumerFactory(activeMQConnectionFactory);
                //List<Message> messagesList =
                final int theMaxReceiveCount = this.maxReceiveCount;
                final int theMaxWaittimeSeconds = this.maxWaittimeSeconds;

                final String theJmsMessageSelector = this.jmsMessageSelector;
                browserFactory.consumeAdvisoryMessage(m,
                        theQueueName,
                        theJmsMessageSelector,
                        theMaxReceiveCount,
                        theMaxWaittimeSeconds);
                //verboseBrowsedMessages(messagesList.iterator());
            } catch (JMSRuntimeException jmsrtex) {
                rc = -2;
            }
        } else {
            rc = -1;
        }
        return rc;
    }

    void verboseReceivingFromAdvisoryTopic(String theBrokerURL, String theDestination, String messageSelector) {
        logger.info(String.format("Receiving from advisory topic `%s' from brokerURL `%s' using message selector '%s'", theBrokerURL, theDestination, messageSelector));
    }

    void verboseBrowsedMessages(Iterator<Message> messageIterator) {
        for (int i = 0; messageIterator.hasNext(); i += 1) {
            final Message message = messageIterator.next();
            logger.info(String.format("Message #%d: %s", i, message));
        }
    }
}
