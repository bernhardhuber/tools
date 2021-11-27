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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.jms.Message;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport;
import org.huberb.apacheactivemq.examples.picocli.jms.BrowserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 *
 * @author pi
 */
@CommandLine.Command(name = "MainBrowserFactory",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        version = "MainBrowserFactory 0.1-SNAPSHOT",
        description = "Browse messages from a queue")
public class MainBrowserFactory implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(MainBrowserFactory.class);

    @CommandLine.Mixin
    private ActivemqOptions activemqOptions;

    //--- jms destination
    @CommandLine.Option(names = {"--jms-destination-queue"},
            paramLabel = "QUEUE",
            required = true,
            description = "jms destination queue name")
    private String jmsDestinationQueueName;
    @CommandLine.Option(names = {"--jms-message-selector"},
            paramLabel = "MESSAGE-SELECTOR",
            required = false,
            description = "jms message-selector")
    private String jmsMessageSelector;
    @CommandLine.Option(names = {"--jms-max-browse-count"},
            paramLabel = "MAX-COUNT",
            required = false,
            defaultValue = "-1",
            description = "browse MAX-COUNT messages")
    private int maxBrowseCount;

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new MainBrowserFactory()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        Integer rc = 0;
        final Map<String, Object> m = new HashMap<>();
        //-- activemq factory props
        m.put("userName", this.activemqOptions.getUserName());
        m.put("password", this.activemqOptions.getPassword());
        m.put("brokerURL", this.activemqOptions.getBrokerURL());
        //---
        if (this.jmsDestinationQueueName != null && !this.jmsDestinationQueueName.isBlank()) {
            final String theQueueName = this.jmsDestinationQueueName;
            verboseBrowsingQueue(this.activemqOptions.getBrokerURL(), theQueueName, this.jmsMessageSelector, this.maxBrowseCount);
            //--- ActiveMQConnectionFactory
            final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
            activeMQConnectionFactory.buildFromMap(m);
            try {
                final BrowserFactory browserFactory = new BrowserFactory(activeMQConnectionFactory);
                final String theJmsMessageSelector = this.jmsMessageSelector;
                final int theJmsMessageMaxCount = this.maxBrowseCount;
                final List<Message> messagesList = browserFactory.browseMessages(m, theQueueName, theJmsMessageSelector, theJmsMessageMaxCount);
                verboseBrowsedMessages(messagesList.iterator());
            } catch (AutoCloseableSupport.JMSRuntimeException jmsrtex) {
                rc = -2;
            }
        } else {
            rc = -1;
        }
        return rc;
    }

    void verboseBrowsingQueue(String theBrokerURL, String theDestination, String messageSelector, int maxCount) {
        logger.info(String.format("Browsing queue`%s' from brokerURL `%s' using message selector '%s' browsing max count %d ", theBrokerURL, theDestination,
                messageSelector, maxCount));
    }

    void verboseBrowsedMessages(Iterator<Message> messageIterator) {
        for (int i = 0; messageIterator.hasNext(); i += 1) {
            final Message message = messageIterator.next();
            logger.info(String.format("Message #%d: %s", i, message));
        }
    }
}
