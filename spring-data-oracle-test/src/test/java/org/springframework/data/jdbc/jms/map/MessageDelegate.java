package org.springframework.data.jdbc.jms.map;

import java.util.Map;

import javax.jms.JMSException;

import org.springframework.jms.support.converter.MessageConversionException;

public class MessageDelegate {
	
	private int count = 0;

    @SuppressWarnings("unchecked")
	public void handleMessage(Map messageMap) throws MessageConversionException, JMSException {
    	count++;
    	System.out.println("---> " + messageMap);
    }

	public int getCount() {
		return count;
	}

}
