/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.apacheactivemq.examples.picocli.jms;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
public class BrowserFactoryIT {

    // use junit4 @Rule 
    // use public access otherwise broker.begin/end is *not* invoked!
    @Rule
    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();
    BrowserFactory instance;
    ConnectionFactory connectionFactory;

    public BrowserFactoryIT() {
    }

    @BeforeEach
    public void setUp() {
        assertTrue(this.broker.getBrokerService().isStarted());
        connectionFactory = this.broker.createConnectionFactory();
        instance = new BrowserFactory(connectionFactory);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of browseMessages method, of class BrowserFactory.
     */
    @Test
    public void testBrowseMessages_4args() {
        final Map<String, Object> m = Collections.emptyMap();
        final String queueName = "browserQueue1";
        final String jmsMessageSelector = "";
        final List<Message> l = instance.browseMessages(m, queueName, jmsMessageSelector, 10);
        Assertions.assertTrue(l.isEmpty());
    }

    /**
     * Test of browseMessages method, of class BrowserFactory.
     * @throws javax.jms.JMSException
     */
    @Test
    public void testBrowseMessages_4args_1message() throws JMSException {
        final Map<String, Object> m = Collections.emptyMap();
        final String queueName = "browserQueue1";
        final String jmsMessageSelector = "";
        final TextMessage textMessage = this.broker.pushMessage(queueName, "message1");

        final List<Message> l = instance.browseMessages(m, queueName, jmsMessageSelector, 10);
        assertNotNull(l);
        assertEquals(1, l.size());
        assertEquals("message1", ((TextMessage) l.get(0)).getText());

    }

    /**
     * Test of browseMessagesCounting method, of class BrowserFactory.
     */
    @Test
    public void testBrowseMessagesCounting() {
        final Map<String, Object> m = Collections.emptyMap();
        final String queueName = "browserQueue1";
        final String jmsMessageSelector = "";
        final TextMessage textMessage = this.broker.pushMessage(queueName, "message1");

        final Integer i = instance.browseMessagesCounting(m, queueName, jmsMessageSelector, 10);
        assertEquals(1, i);
    }

    /**
     * Test of browseMessages method, of class BrowserFactory.
     */
    @Test
    public void testBrowseMessages_5args() {
        final Map<String, Object> m = Collections.emptyMap();
        final String queueName = "browserQueue1";
        final String jmsMessageSelector = "";
        final TextMessage textMessage = this.broker.pushMessage(queueName, "message1");

        AtomicInteger consumedCount = new AtomicInteger(0);
        Consumer<Message> c = (message) -> {
            consumedCount.incrementAndGet();
        };
        instance.browseMessages(m, queueName, jmsMessageSelector, 10, c);
        assertEquals(1, consumedCount.get());
    }

}
