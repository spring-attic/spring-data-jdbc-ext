package org.springframework.data.jdbc.retry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RetryTests {

	@Autowired
	private TestDao testDao;
	
	@Test
	public void testSuccessfulRetry() {
		String username = testDao.getUserNameFailTwice();
		assertEquals("database call didn't succeed", "SPRING", username);
	}
	
	@Test(expected=DataAccessException.class)
	public void testFailedRetry() {
		try {
			testDao.getUserNameFailAlways(1);
		}
		catch (Exception e) {
			Throwable t = getNestedException(e);
			assertTrue("Wrong exception class returned", t instanceof DataAccessException);
			assertTrue("Not the right number of retries", t.getCause().getMessage().endsWith("Second Error-4"));
			throw (DataAccessException)t;
		}
		fail("database call shouldn't succeed");
	}

	@Test(expected=DataAccessException.class)
	public void testFailedCallWithoutRetry() {
		try {
			testDao.getUserNameFailAlways(0);
		}
		catch (Exception e) {
			Throwable t = e;
			assertTrue("Wrong exception class returned", t instanceof DataAccessException);
			assertTrue("Not the right number of retries", t.getCause().getMessage().endsWith("Third Error-1"));
			throw (DataAccessException)t;
		}
		fail("database call shouldn't succeed");
	}
	
	private Throwable getNestedException(Exception e) {
		Throwable t;
		if (e instanceof ExhaustedRetryException) {
			t = e.getCause();
		}
		else {
			t = e;
		}
		return t;
	}
	
}
