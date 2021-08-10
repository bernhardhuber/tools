/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.apacheactivemq.examples.picocli.jms;

import javax.jms.JMSException;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.JMSRuntimeException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author pi
 */
public class JMSRuntimeExceptionTest {

    public JMSRuntimeExceptionTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testNewJMSRuntimeException_arg1() {
        final JMSException jmsException = new JMSException("jmsExceptionMessage");
        final JMSRuntimeException instance = new JMSRuntimeException(jmsException);
        assertEquals("", instance.getMessage());
        assertEquals(jmsException, instance.getCause());
        assertEquals("jmsExceptionMessage", jmsException.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "jmsRuntimeExceptionMessage,jmsRuntimeExceptionMessage",
        "'',''",
        "null,''"},
            nullValues = "null")
    public void testNewJMSRuntimeException_arg2_null(String jmsRuntimeExceptionMessageIn, String jmsRuntimeMessageMessageExpected) {
        final JMSException jmsException = new JMSException("jmsExceptionMessage");
        final JMSRuntimeException instance = new JMSRuntimeException(jmsRuntimeExceptionMessageIn, jmsException);
        assertEquals(jmsRuntimeMessageMessageExpected, instance.getMessage());
        assertEquals(jmsException, instance.getCause());
        assertEquals("jmsExceptionMessage", jmsException.getMessage());
    }
}
