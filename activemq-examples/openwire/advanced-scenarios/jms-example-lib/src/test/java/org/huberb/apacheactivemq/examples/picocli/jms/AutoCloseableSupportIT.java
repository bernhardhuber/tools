/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.apacheactivemq.examples.picocli.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableConnection;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableMessageConsumer;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableMessageProducer;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableQueueBrowser;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableSession;
import org.huberb.apacheactivemq.examples.picocli.jms.AutoCloseableSupport.AutoCloseableTopicSubscriber;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author pi
 */
@ExtendWith(MockitoExtension.class)
// use junit4 @Rule 
@EnableRuleMigrationSupport
public class AutoCloseableSupportIT {
    // use junit4 @Rule 
    // use public access otherwise broker.begin/end is *not* invoked!

    @Rule
    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();
    ConnectionFactory connectionFactory;

    public AutoCloseableSupportIT() {
    }

    @BeforeEach
    public void setUp() {
        assertTrue(this.broker.getBrokerService().isStarted());
        connectionFactory = this.broker.createConnectionFactory();
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testAutoCloseableConnection() throws JMSException {
        final Connection spyConnection = Mockito.spy(this.connectionFactory.createConnection());
        final AutoCloseableConnection spyAutoCloseableConnection = Mockito.spy(new AutoCloseableConnection(spyConnection));
        try (spyAutoCloseableConnection) {
            spyConnection.start();
        }
        Mockito.verify(spyAutoCloseableConnection, Mockito.times(1)).close();
        Mockito.verify(spyConnection, Mockito.times(1)).close();
    }

    @Test
    public void testAutoCloseableSession() throws JMSException {

        try (final AutoCloseableConnection autoCloseableConnection = new AutoCloseableConnection(this.connectionFactory.createConnection())) {
            final Connection connection = autoCloseableConnection.connection();
            connection.start();

            final Session spySession = Mockito.spy(connection.createSession(true, Session.AUTO_ACKNOWLEDGE));
            final AutoCloseableSession spyAutoCloseableSession = Mockito.spy(new AutoCloseableSupport.AutoCloseableSession(spySession));
            try (spyAutoCloseableSession) {
            }
            Mockito.verify(spyAutoCloseableSession, Mockito.times(1)).close();
            Mockito.verify(spySession, Mockito.times(1)).close();
        }
    }

    @Test
    public void testAutoCloseableMessageProducer() throws JMSException {

        try (final AutoCloseableConnection autoCloseableConnection = new AutoCloseableConnection(this.connectionFactory.createConnection())) {
            final Connection connection = autoCloseableConnection.connection();
            connection.start();

            final AutoCloseableSession autoCloseableSession = new AutoCloseableSupport.AutoCloseableSession(connection.createSession(true, Session.AUTO_ACKNOWLEDGE));
            try (autoCloseableSession) {
                final Session session = autoCloseableSession.session();

                final Destination destination = session.createQueue("queue://queueA");
                final MessageProducer spyMessageProducer = Mockito.spy(session.createProducer(destination));
                final AutoCloseableMessageProducer spyAutoCloseableMessageProducer = Mockito.spy(new AutoCloseableMessageProducer(spyMessageProducer));
                try (spyAutoCloseableMessageProducer) {

                }
                Mockito.verify(spyAutoCloseableMessageProducer, Mockito.times(1)).close();
                Mockito.verify(spyMessageProducer, Mockito.times(1)).close();
            }
        }
    }

    @Test
    public void testAutoCloseableMessageConsumer() throws JMSException {

        try (final AutoCloseableConnection autoCloseableConnection = new AutoCloseableConnection(this.connectionFactory.createConnection())) {
            final Connection connection = autoCloseableConnection.connection();
            connection.start();

            final AutoCloseableSession autoCloseableSession = new AutoCloseableSupport.AutoCloseableSession(connection.createSession(true, Session.AUTO_ACKNOWLEDGE));
            try (autoCloseableSession) {
                final Session session = autoCloseableSession.session();

                final Destination destination = session.createQueue("queue://queueA");
                final MessageConsumer spyMessageConsumer = Mockito.spy(session.createConsumer(destination));
                final AutoCloseableMessageConsumer spyAutoCloseableMessageConsumer = Mockito.spy(new AutoCloseableMessageConsumer(spyMessageConsumer));
                try (spyAutoCloseableMessageConsumer) {

                }
                Mockito.verify(spyAutoCloseableMessageConsumer, Mockito.times(1)).close();
                Mockito.verify(spyMessageConsumer, Mockito.times(1)).close();
            }
        }
    }

    @Test
    public void testAutoCloseableTopicSubscriber() throws JMSException {

        try (final AutoCloseableConnection autoCloseableConnection = new AutoCloseableConnection(this.connectionFactory.createConnection())) {
            final Connection connection = autoCloseableConnection.connection();
            connection.setClientID("topicASubscriberClientId");
            connection.start();

            final AutoCloseableSession autoCloseableSession = new AutoCloseableSupport.AutoCloseableSession(connection.createSession(true, Session.AUTO_ACKNOWLEDGE));
            try (autoCloseableSession) {
                final Session session = autoCloseableSession.session();

                final Topic topic = session.createTopic("topic://topicA");
                final TopicSubscriber spyTopicSubscriber = Mockito.spy(session.createDurableSubscriber(topic, "topicASubscriber"));

                final AutoCloseableTopicSubscriber spyAutoCloseableTopicSubscriber = Mockito.spy(new AutoCloseableTopicSubscriber(spyTopicSubscriber));
                try (spyAutoCloseableTopicSubscriber) {
                }
                Mockito.verify(spyAutoCloseableTopicSubscriber, Mockito.times(1)).close();
                Mockito.verify(spyTopicSubscriber, Mockito.times(1)).close();

                session.unsubscribe("topicASubscriber");
            }

        }
    }

    @Test
    public void testAutoCloseableQueueBrowser() throws JMSException {

        try (final AutoCloseableConnection autoCloseableConnection = new AutoCloseableConnection(this.connectionFactory.createConnection())) {
            final Connection connection = autoCloseableConnection.connection();
            connection.start();

            final AutoCloseableSession autoCloseableSession = new AutoCloseableSupport.AutoCloseableSession(connection.createSession(true, Session.AUTO_ACKNOWLEDGE));
            try (autoCloseableSession) {
                final Session session = autoCloseableSession.session();

                final Queue queue = session.createQueue("queue://queueA");
                final QueueBrowser spyQueueBrowser = Mockito.spy(session.createBrowser(queue));
                final AutoCloseableQueueBrowser spyAutoCloseableQueueBrowser = Mockito.spy(new AutoCloseableQueueBrowser(spyQueueBrowser));
                try (spyAutoCloseableQueueBrowser) {

                }
                Mockito.verify(spyAutoCloseableQueueBrowser, Mockito.times(1)).close();
                Mockito.verify(spyQueueBrowser, Mockito.times(1)).close();
            }
        }
    }
}
