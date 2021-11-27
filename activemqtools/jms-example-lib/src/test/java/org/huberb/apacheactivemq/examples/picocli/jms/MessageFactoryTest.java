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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author pi
 */
@ExtendWith(MockitoExtension.class)
public class MessageFactoryTest {

    public MessageFactoryTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of createTextMessageFromSession method, of class MessageFactory.
     */
    @Test
    public void testCreateTextMessageFromSession() throws JMSException {
        final MessageFactory instance = new MessageFactory();
        final Map<String, Object> messagePropertyMap = new HashMap<>();
        final String textMessageAsString = "textMessageAsString";
        //--
        final Function<Session, Message> f = instance.createTextMessageFromSession(textMessageAsString, messagePropertyMap);
        final Session sessionMock = Mockito.mock(Session.class);
        final TextMessage textMessageMock = Mockito.mock(TextMessage.class);
        Mockito.when(sessionMock.createTextMessage(textMessageAsString)).thenReturn(textMessageMock);
        //---
        final Message m = f.apply(sessionMock);
        assertSame(textMessageMock, m);
    }

    /**
     * Test of createTextMessageIteratorFromSession method, of class
     * MessageFactory.
     */
    @Test
    public void testCreateTextMessageIteratorFromSession() throws JMSException {
        final MessageFactory instance = new MessageFactory();
        Map<String, Object> messagePropertyMap = new HashMap<>();
        final List<String> textMessagesAsList = Arrays.asList(
                "textMessageAsString0",
                "textMessageAsString1",
                "textMessageAsString2"
        );
        //--
        final Function<Session, Iterator<Message>> f = instance.createTextMessageIteratorFromSession(textMessagesAsList, messagePropertyMap);
        final Session sessionMock = Mockito.mock(Session.class);
        final TextMessage textMessageMock0 = Mockito.mock(TextMessage.class);
        final TextMessage textMessageMock1 = Mockito.mock(TextMessage.class);
        final TextMessage textMessageMock2 = Mockito.mock(TextMessage.class);
        Mockito.when(sessionMock.createTextMessage(textMessagesAsList.get(0))).thenReturn(textMessageMock0);
        Mockito.when(sessionMock.createTextMessage(textMessagesAsList.get(1))).thenReturn(textMessageMock1);
        Mockito.when(sessionMock.createTextMessage(textMessagesAsList.get(2))).thenReturn(textMessageMock2);
        //---
        final Iterator<Message> mIt = f.apply(sessionMock);
        assertTrue(mIt.hasNext());

        for (int i = 0; i < 3; i++) {
            String m = "" + "i " + i;
            assertTrue(mIt.hasNext(), m);
            Message message = mIt.next();
            assertNotNull(message, m);
        }
    }
}
