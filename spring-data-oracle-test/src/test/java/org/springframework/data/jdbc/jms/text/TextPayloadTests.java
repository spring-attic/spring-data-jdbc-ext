package org.springframework.data.jdbc.jms.text;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration
public class TextPayloadTests {

    @Autowired
	private JmsTemplate jmsTemplate;
    
    @Autowired
    private MessageDelegate delegate;

    @Autowired
    private DefaultMessageListenerContainer container;
        
    @Transactional @Test @Rollback(false)
    public void sendMessage() {
        jmsTemplate.send("jmsadmin.jms_text_queue", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage msg = session.createTextMessage();
                msg.setText("Hello Spring, from JmsTemplate!");
                return msg;
            }
        });
    }

    @Test
    public void stopListener() {
    	// let the container process the message
        try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("interrupted");
		}
    	container.stop();
    	assertTrue("messages not received", delegate.getCount() > 0);
    }
}
