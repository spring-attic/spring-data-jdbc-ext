package org.springframework.data.jdbc.retry;

public interface TestDao {

	String getUserNameFailTwice();

	String getUserNameFailAlways(int id);

}