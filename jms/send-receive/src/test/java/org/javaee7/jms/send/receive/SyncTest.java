package org.javaee7.jms.send.receive;

import java.util.concurrent.TimeoutException;
import org.javaee7.jms.send.receive.classic.ClassicMessageSender;
import org.javaee7.jms.send.receive.classic.ClassicMessageReceiver;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;

import org.javaee7.jms.send.receive.simple.MessageReceiverSync;
import org.javaee7.jms.send.receive.simple.MessageSenderSync;
import org.javaee7.jms.send.receive.simple.appmanaged.MessageReceiverAppManaged;
import org.javaee7.jms.send.receive.simple.appmanaged.MessageSenderAppManaged;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

/**
 * @author Patrik Dudits
 */
@RunWith(Arquillian.class)
public class SyncTest {

    @EJB
    ClassicMessageSender classicSender;

    @EJB
    ClassicMessageReceiver classicReceiver;

    @EJB
    MessageSenderSync simpleSender;

    @EJB
    MessageReceiverSync simpleReceiver;

    @EJB
    MessageSenderAppManaged appManagedSender;

    @EJB
    MessageReceiverAppManaged appManagedReceiver;
    
    private final int messageReceiveTimeoutInMillis = 10000;

    @Test
    public void testClassicApi() throws JMSException, TimeoutException {
        String message = "The test message over JMS 1.1 API";
        classicSender.sendMessage(message);

        assertEquals(message, classicReceiver.receiveMessage(messageReceiveTimeoutInMillis));
    }

    @Test
    public void testContainerManagedJmsContext() throws JMSRuntimeException, TimeoutException {
        String message = "Test message over container-managed JMSContext";
        simpleSender.sendMessage(message);

        assertEquals(message, simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
    }

    @Test
    public void testAppManagedJmsContext() throws JMSRuntimeException, TimeoutException {
        String message = "The test message over app-managed JMSContext";
        appManagedSender.sendMessage(message);

        assertEquals(message, appManagedReceiver.receiveMessage(messageReceiveTimeoutInMillis));
    }

    @Test
    public void testMultipleSendAndReceive() throws JMSRuntimeException, TimeoutException {
        simpleSender.sendMessage("1");
        simpleSender.sendMessage("2");
        assertEquals("1", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
        assertEquals("2", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
        simpleSender.sendMessage("3");
        simpleSender.sendMessage("4");
        simpleSender.sendMessage("5");
        assertEquals("3", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
        assertEquals("4", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
        assertEquals("5", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
    }

    @Deployment
    public static WebArchive deploy() {
        return ShrinkWrap.create(WebArchive.class)
            .addClasses(MessageSenderSync.class,
                MessageReceiverSync.class,
                ClassicMessageSender.class,
                ClassicMessageReceiver.class,
                MessageSenderAppManaged.class,
                MessageReceiverAppManaged.class,
                Resources.class);
    }

}
