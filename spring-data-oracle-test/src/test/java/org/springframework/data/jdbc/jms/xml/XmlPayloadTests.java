package org.springframework.data.jdbc.jms.xml;

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
public class XmlPayloadTests {

    @Autowired
	private JmsTemplate jmsTemplate;
    
    @Autowired
    private MessageDelegate delegate;

    @Autowired
    private DefaultMessageListenerContainer container;
        
    @Transactional @Test @Rollback(false)
    public void sendMessage() {
        String xmlval = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<product id=\"10\">\n" +
	        " <description>Foo</description>\n" +
	        " <price>2.05</price>\n" +
	        "</product>";
        jmsTemplate.convertAndSend("jmsadmin.jms_xml_queue", xmlval);
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
