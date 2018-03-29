package org.springframework.data.jdbc.jms.map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MapPayloadTests {

    @Autowired
	private JmsTemplate jmsTemplate;
    
    @Autowired
    private MessageDelegate delegate;

    @Autowired
    private DefaultMessageListenerContainer container;
        
    @SuppressWarnings("unchecked")
	@Transactional @Test @Rollback(false)
    public void sendMessage() {
        final Map mapval = new HashMap();
        mapval.put("id", 15L);
        mapval.put("description", "Bar");
        mapval.put("price", new Double(26.75));
        jmsTemplate.convertAndSend("jmsadmin.jms_map_queue", mapval);
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
