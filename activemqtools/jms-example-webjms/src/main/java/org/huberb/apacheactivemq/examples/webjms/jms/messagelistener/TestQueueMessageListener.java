/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.apacheactivemq.examples.webjms.jms.messagelistener;

import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.huberb.apacheactivemq.examples.webjms.jms.messagestore.JmsMessageStore;
import org.huberb.apacheactivemq.examples.webjms.jms.messagestore.JmsMessageStoreObserver;

/**
 *
 * @author pi
 */
@MessageDriven(
        name = "TestQueueMessageListenerMessageDriven",
        activationConfig = {
            /*
                acknowledgeMode. This property is used to specify the JMS acknowledgement mode 
                    for the message delivery when bean-managed transaction demarcation is used. 
                    Its values are Auto_acknowledge or Dups_ok_acknowledge. 
                    If this property is not specified, JMS AUTO_ACKNOWLEDGE semantics are assumed.
                messageSelector. This property is used to specify the JMS message selector to 
                    be used in determining which messages a JMS message driven bean is to receive.
                destinationType. This property is used to specify whether the message 
                    driven bean is intended to be used with a queue or a topic. 
                    The value must be either javax.jms.Queue or javax.jms.Topic.
                destinationLookup. This property is used to specify the JMS queue or topic 
                    from which a JMS message-driven bean is to receive messages.
                connectionFactoryLookup. This property is used to specify the JMS connection 
                    factory that will be used to connect to the JMS provider from 
                    which a JMS message-driven bean is to receive messages.
                subscriptionDurability. If the message driven bean is intended to be 
                    used with a topic, this property may be used to indicate whether 
                    a durable or non-durable subscription should be used. 
                    The value of this property must be either Durable or NonDurable
                subscriptionName. This property is used to specify the name of the 
                    durable subscription if the message-driven bean is intended to be 
                    used with a Topic, and the bean provider has indicated that 
                    a durable subscription should be used.
                clientId. This property is used to specify the JMS client identifier 
                    that will be used when connecting to the JMS provider from which 
                    a JMS message-driven bean is to receive messages. 
                    If this property is not specified then the client identifier 
                    will be left unset.
             */
            @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
            //@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Dups-ok-acknowledge"),
            @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
            @ActivationConfigProperty(propertyName = "useJndi", propertyValue = "true"),
            @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/testQueue"),
            //@ActivationConfigProperty(propertyName = "destination", propertyValue = "testQueue"),
            // not supported @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/testQueue"),
            // not supported @ActivationConfigProperty(propertyName = "connectionFactoryLookup", propertyValue = "java:/activemq/TestQueueMessageListenerConnectionFactory"),
            @ActivationConfigProperty(propertyName = "clientId", propertyValue = "clientIdTestQueueMessageListener"),
            @ActivationConfigProperty(propertyName = "maxSessions", propertyValue = "1"),})
public class TestQueueMessageListener implements MessageListener {

    private static final Logger logger = Logger.getLogger(TestQueueMessageListener.class.getName());

    @Resource
    private MessageDrivenContext mdc;

    @Inject
    private JmsMessageStore jmsMessageStore;

    @Inject
    private Event<JmsMessageStoreObserver.JmsMessageStoreEvent> event;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        logger.info(String.format("Received jms-message %s", message));

        //---
        JmsMessageStoreObserver.JmsMessageStoreEvent jmsMessageStoreEvent
                = new JmsMessageStoreObserver.JmsMessageStoreEvent(message, "categoryTestQueueMessageListener");
        this.event.fire(jmsMessageStoreEvent);
        //--
        JmsMessageStore.JmsMessagStoreEntry e = new JmsMessageStore.JmsMessagStoreEntry(message);
        jmsMessageStore.addJmsMessage(JmsMessageStore.JmsMessageStoreCategory.messageListenerQueueA, e);
    }

}
