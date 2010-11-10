package org.springframework.data.jdbc.jms.adt;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

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
public class AdtPayloadTests {

    @Autowired
	private JmsTemplate jmsTemplate;
    
    @Autowired
    private MessageDelegate delegate;

    @Autowired
    private DefaultMessageListenerContainer container;
        
    @Transactional @Test @Rollback(false)
    public void sendMessage() {
        Product product = new Product();
        product.setId(22L);
        product.setDescription("Spring Book");
        product.setPrice(new BigDecimal("42.95"));
        jmsTemplate.convertAndSend("jmsadmin.jms_product_queue", product);
    }

    @Test
    public void stopListener() {
    	// let the container process the message
        try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("interrupted");
		}
    	container.stop();
    	assertTrue("messages not received", delegate.getCount() > 0);
    }
}
