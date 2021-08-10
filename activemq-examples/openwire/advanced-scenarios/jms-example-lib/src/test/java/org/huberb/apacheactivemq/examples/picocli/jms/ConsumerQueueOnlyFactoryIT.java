/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.apacheactivemq.examples.picocli.jms;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

/**
 *
 * @author pi
 */
// use junit4 @Rule 
@EnableRuleMigrationSupport
public class ConsumerQueueOnlyFactoryIT {

    // use junit4 @Rule 
    // use public access otherwise broker.begin/end is *not* invoked!
    @Rule
    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();
    //---
    ConnectionFactory connectionFactory;
    ConsumerQueueOnlyFactory instance;
    final int maxWaittimeSeconds = 10;

    public ConsumerQueueOnlyFactoryIT() {
    }

    @BeforeEach
    public void setUp() {
        assertTrue(this.broker.getBrokerService().isStarted());
        this.connectionFactory = this.broker.createConnectionFactory();
        this.instance = new ConsumerQueueOnlyFactory(connectionFactory);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of consumeQueueMessage method, of class ConsumerQueueOnlyFactory.
     */
    @Test
    public void testConsumeQueueMessage_4args() {
        final Map<String, Object> m = Collections.emptyMap();
        final String queueName = "consumeQueue_testConsumeQueueMessage_4args";
        final String jmsMessageSelector = "";
        Optional<Message> messageOpt = instance.consumeQueueMessage(m, queueName, jmsMessageSelector, 0, maxWaittimeSeconds);
        assertFalse(messageOpt.isPresent());

        assertEquals(0L - 0L, this.broker.getMessageCount(queueName));
    }

    /**
     * Test of consumeQueueMessage method, of class ConsumerQueueOnlyFactory.
     *
     * @throws javax.jms.JMSException
     */
    @Test
    public void testConsumeQueueMessage_4args_1_from_1message() throws JMSException {
        final Map<String, Object> m = Collections.emptyMap();
        final String queueName = "consumeQueue_testConsumeQueueMessage_4args_1_from_1message";
        final String jmsMessageSelector = "";
        this.broker.pushMessage("queue://" + queueName, "message1");

        Optional<Message> messageOpt = instance.consumeQueueMessage(m, queueName, jmsMessageSelector, 1, maxWaittimeSeconds);
        assertTrue(messageOpt.isPresent());
        assertEquals("message1", ((TextMessage) messageOpt.get()).getText());

        assertEquals(1L - 1L, this.broker.getMessageCount(queueName));
    }

    /**
     * Test of consumeQueueMessage method, of class ConsumerQueueOnlyFactory.
     *
     * @throws javax.jms.JMSException
     */
    @Test
    public void testConsumeQueueMessage_4args_1_from_2message() throws JMSException {
        final Map<String, Object> m = Collections.emptyMap();
        final String queueName = "consumeQueue_testConsumeQueueMessage_4args_1_from_2message";
        final String jmsMessageSelector = "";
        this.broker.pushMessage("queue://" + queueName, "message1");
        this.broker.pushMessage("queue://" + queueName, "message2");

        Optional<Message> messageOpt = instance.consumeQueueMessage(m, queueName, jmsMessageSelector, 1, maxWaittimeSeconds);
        assertTrue(messageOpt.isPresent());
        assertEquals("message1", ((TextMessage) messageOpt.get()).getText());

        assertEquals(2L - 1L, this.broker.getMessageCount(queueName));
    }

    /**
     * Test of consumeQueueMessage method, of class ConsumerQueueOnlyFactory.
     *
     * @throws javax.jms.JMSException
     */
    @Test
    public void testConsumeQueueMessage_4args_2_from_2message() throws JMSException {
        final Map<String, Object> m = Collections.emptyMap();
        final String queueName = "consumeQueue_testConsumeQueueMessage_4args_2_from_2message";
        final String jmsMessageSelector = "";
        this.broker.pushMessage("queue://" + queueName, "message1");
        this.broker.pushMessage("queue://" + queueName, "message2");

        Optional<Message> messageOpt = instance.consumeQueueMessage(m, queueName, jmsMessageSelector, 2, maxWaittimeSeconds);
        assertTrue(messageOpt.isPresent());
        assertEquals("message1", ((TextMessage) messageOpt.get()).getText());

        assertEquals(2L - 2L, this.broker.getMessageCount(queueName));
    }

    /**
     * Test of consumeQueueMessage method, of class ConsumerQueueOnlyFactory.
     */
    @Test
    public void testConsumeQueueMessage_5args() {
    }

    /**
     * Test of peekTransactedFrom method, of class ConsumerQueueOnlyFactory.
     */
    @Test
    public void testPeekTransactedFrom() {
    }

}
