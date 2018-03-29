package org.springframework.data.jdbc.jms.bytes;

import javax.jms.JMSException;

import org.springframework.jms.support.converter.MessageConversionException;

public class MessageDelegate {
	
	private int count = 0;

	@SuppressWarnings("unchecked")
    public void handleMessage(byte[] payload) throws MessageConversionException, JMSException {
    	count++;
    	System.out.println("---> " + new String(payload));
    }

	public int getCount() {
		return count;
	}

}
