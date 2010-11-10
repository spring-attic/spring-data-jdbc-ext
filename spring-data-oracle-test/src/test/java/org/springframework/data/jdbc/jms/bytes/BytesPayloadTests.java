package org.springframework.data.jdbc.jms.bytes;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration
public class BytesPayloadTests {

    @Autowired
	private JmsTemplate jmsTemplate;
    
    @Autowired
    private MessageDelegate delegate;

    @Autowired
    private DefaultMessageListenerContainer container;
        
	@Transactional @Test @Rollback(false)
    public void sendMessage() {
        final byte[] value = new byte[] {72, 101, 108, 108, 111};
        jmsTemplate.convertAndSend("jmsadmin.jms_bytes_queue", value);
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
