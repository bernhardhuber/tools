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

import java.util.Collections;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author pi
 */
@ExtendWith(MockitoExtension.class)
public class MessagePropertyFactoryTest {

    @Mock
    Message message;

    MessagePropertyFactory instance;

    public MessagePropertyFactoryTest() {
    }

    @BeforeEach
    public void setUp() {
        instance = new MessagePropertyFactory();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of populateMessagePropertyWith method, of class
     * MessagePropertyFactory.
     */
    @Test
    public void testPopulateMessagePropertyWith() throws JMSException {
        Map<String, Object> messageMap = Collections.emptyMap();
        instance.populateMessagePropertyWith(message, messageMap);
        //--
        Mockito.verify(this.message, Mockito.never()).setBooleanProperty(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(this.message, Mockito.never()).setByteProperty(Mockito.anyString(), Mockito.anyByte());
        Mockito.verify(this.message, Mockito.never()).setDoubleProperty(Mockito.anyString(), Mockito.anyDouble());
        Mockito.verify(this.message, Mockito.never()).setFloatProperty(Mockito.anyString(), Mockito.anyFloat());
        Mockito.verify(this.message, Mockito.never()).setIntProperty(Mockito.anyString(), Mockito.anyInt());
        Mockito.verify(this.message, Mockito.never()).setLongProperty(Mockito.anyString(), Mockito.anyLong());
        Mockito.verify(this.message, Mockito.never()).setObjectProperty(Mockito.anyString(), Mockito.any());
        Mockito.verify(this.message, Mockito.never()).setShortProperty(Mockito.anyString(), Mockito.anyShort());
        Mockito.verify(this.message, Mockito.never()).setStringProperty(Mockito.anyString(), Mockito.anyString());
    }

    /**
     * Test of populateMessagePropertyWithKeyValue method, of class
     * MessagePropertyFactory.
     */
    @Test
    public void testPopulateMessagePropertyWithKeyValue() throws JMSException {
        final String key = "string:key1";
        final Object value = "value1";

        instance.populateMessagePropertyWithKeyValue(message, key, value);
        Mockito.verify(this.message, Mockito.times(1)).setStringProperty("key1", "value1");
    }

    /**
     * Test of handleXXXPrefixGroup1 method, of class MessagePropertyFactory.
     *
     * @throws JMSException
     */
    @Test
    public void testHandleXXXPrefixGroup1() throws JMSException {
        instance.handleJmsPropertyByTypePrefixGroup1(message, MessagePropertyFactory.JmsPropertyRepresentation.int_prefix, "int:intProp1Key", "10");
        Mockito.verify(this.message, Mockito.times(1)).setIntProperty("intProp1Key", 10);

        instance.handleJmsPropertyByTypePrefixGroup1(message, MessagePropertyFactory.JmsPropertyRepresentation.string_prefix, "string:stringProp1Key", "stringProp1Value");
        Mockito.verify(this.message, Mockito.times(1)).setStringProperty("stringProp1Key", "stringProp1Value");

    }

    /**
     * Test of handleXXXPrefixGroup1 method, of class MessagePropertyFactory.
     *
     * @throws JMSException
     */
    @Test
    public void testHandleYYYPrefixGroup2() throws JMSException {
        instance.handleJmsPropertyByNamePrefixGroup2(message, MessagePropertyFactory.JmsApiPropertyRepresentation.jms_prefix, "JMS:JMSType", "someJMSTypeValue");
        Mockito.verify(this.message, Mockito.times(1)).setJMSType("someJMSTypeValue");
    }
}
