package org.springframework.data.jdbc.jms.text;

public class MessageDelegate {
	
	private int count = 0;

    public void handleMessage(String msg) {
    	count++;
        System.out.println("---> " + msg);
    }

	public int getCount() {
		return count;
	}

}
