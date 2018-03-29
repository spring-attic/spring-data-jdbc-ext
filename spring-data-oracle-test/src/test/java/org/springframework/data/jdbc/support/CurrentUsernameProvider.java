package org.springframework.data.jdbc.support;

/**
 * @author Thomas Risberg
 */
public class CurrentUsernameProvider implements ConnectionUsernameProvider {

	String userName = "SPRING";
	
	public void setUserName(String username) {
		this.userName = username;
	}

	public String getUserName() {
		return userName;
	}

}
