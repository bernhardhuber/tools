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
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

/**
 *
 * @author pi
 */
@ExtendWith(MockitoExtension.class)
public class DestinationFactoryTest {

    DestinationFactory instance;

    public DestinationFactoryTest() {
    }

    @BeforeEach
    public void setUp() {
        instance = new DestinationFactory();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of createDestinationQueueFromSessionFunction method, of class
     * DestinationFactory.
     * @throws javax.jms.JMSException
     */
    @Test
    public void testCreateDestinationQueueFromSessionFunction() throws JMSException {
        final String aQueueName = "aQueueName";
        final Function<Session, Destination> resultF = instance.createDestinationQueueFromSessionFunction(aQueueName);

        final Session sessionMock = Mockito.mock(Session.class);
        final Queue queueMock = Mockito.mock(Queue.class);
        //Mockito.when(sessionMock.createQueue(aQueueName)).thenReturn(queueMock);
        Mockito.when(sessionMock.createQueue(aQueueName)).then(new Answer<Queue>() {
            public Queue answer(InvocationOnMock invocation) throws JMSException {
                final String queueName = invocation.getArgument(0, String.class);
                Mockito.when(queueMock.getQueueName()).thenReturn(queueName);
                return queueMock;
            }
        });
        final Destination resultDestination = resultF.apply(sessionMock);
        assertSame(queueMock, resultDestination);
        assertEquals(aQueueName, queueMock.getQueueName());
    }

    /**
     * Test of createDestinationTopicFromSessionFunction method, of class
     * DestinationFactory.
     * @throws javax.jms.JMSException
     */
    @Test
    public void testCreateDestinationTopicFromSessionFunction() throws JMSException {
        final String aTopicName = "aTopicName";
        final Function<Session, Destination> resultF = instance.createDestinationTopicFromSessionFunction(aTopicName);

        final Session sessionMock = Mockito.mock(Session.class);
        final Topic topicMock = Mockito.mock(Topic.class);
        Mockito.when(sessionMock.createTopic(aTopicName)).then(new Answer<Topic>() {
            public Topic answer(InvocationOnMock invocation) throws JMSException {
                final String topicName = invocation.getArgument(0, String.class);
                Mockito.when(topicMock.getTopicName()).thenReturn(topicName);
                return topicMock;
            }
        });
        final Destination resultDestination = resultF.apply(sessionMock);
        assertSame(topicMock, resultDestination);
        assertEquals(aTopicName, topicMock.getTopicName());
    }

}
