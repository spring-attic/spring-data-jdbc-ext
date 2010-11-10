package org.springframework.data.jdbc.jms.adt;

import javax.jms.JMSException;

import org.springframework.jms.support.converter.MessageConversionException;

public class MessageDelegate {
	
	private int count = 0;

    public void handleMessage(Product product) throws MessageConversionException, JMSException {
    	count++;
        System.out.println("---> " + product);
    }

	public int getCount() {
		return count;
	}

}
