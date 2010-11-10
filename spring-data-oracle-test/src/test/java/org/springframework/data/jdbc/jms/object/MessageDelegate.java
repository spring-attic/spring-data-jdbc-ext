package org.springframework.data.jdbc.jms.object;

import javax.jms.JMSException;

import org.springframework.jms.support.converter.MessageConversionException;

public class MessageDelegate {
	
	private int count = 0;

    public void handleMessage(Object payload) throws MessageConversionException, JMSException {
    	count++;
    	System.out.println("---> " + payload.getClass().getName() + " :: " + payload);
    }

	public int getCount() {
		return count;
	}

}
