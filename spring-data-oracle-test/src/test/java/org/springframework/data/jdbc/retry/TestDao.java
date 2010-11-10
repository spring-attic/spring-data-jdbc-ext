package org.springframework.data.jdbc.retry;

public interface TestDao {

	public abstract String getUserNameFailTwice();

	public abstract String getUserNameFailAlways(int id);

}