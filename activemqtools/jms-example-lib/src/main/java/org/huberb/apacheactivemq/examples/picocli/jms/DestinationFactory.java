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
package org.huberb.apacheactivemq.examples.picocli.jms;

import java.util.function.Function;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Factory for creating a jms-queue, or a jms-topic destinations.
 *
 * @author pi
 */
public class DestinationFactory {

    /**
     * Create a function which creates a queue destination.
     *
     * @param queueName
     * @return
     */
    public Function<Session, Destination> createDestinationQueueFromSessionFunction(String queueName) {
        final Function<Session, Destination> f = new Function<Session, Destination>() {
            @Override
            public Destination apply(Session session) {
                try {
                    final Destination destination = session.createQueue(queueName);
                    return destination;
                } catch (JMSException jmsex) {
                    throw new AutoCloseableSupport.JMSRuntimeException(jmsex);
                }
            }
        };
        return f;
    }

    /**
     * Create a function which creates a topic destination.
     *
     * @param topicName
     * @return
     */
    public Function<Session, Destination> createDestinationTopicFromSessionFunction(String topicName) {
        final Function<Session, Destination> f = new Function<Session, Destination>() {
            @Override
            public Destination apply(Session session) {
                try {
                    final Destination destination = session.createTopic(topicName);
                    return destination;
                } catch (JMSException jmsex) {
                    throw new AutoCloseableSupport.JMSRuntimeException(jmsex);
                }
            }
        };
        return f;
    }

}
