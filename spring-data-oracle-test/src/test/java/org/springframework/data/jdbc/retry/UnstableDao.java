package org.springframework.data.jdbc.retry;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UnstableDao implements TestDao {

	private SimpleJdbcTemplate simpleJdbcTemplate;
	
	private int count1 = 0;
	private int count2 = 0;
	private int count3 = 0;
	
	@Autowired
	public void init(final DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	@Transactional
	public String getUserNameFailTwice() {
		count1++;
		String user = 
			simpleJdbcTemplate.queryForObject("select {fn user()} from dual", String.class);
		if (count1 <= 2) {
            SQLException se = new SQLException("First Error-"+count1, "1000", 1000);
			throw new InvalidDataAccessApiUsageException("First Failure", se);
		}

		return user;
	}

	@Transactional
	public String getUserNameFailAlways(int id) {
		if (id > 0) {
			count2++;
		}
		else {
			count3++;
		}
		String user = 
			simpleJdbcTemplate.queryForObject("select {fn user()} from dual", String.class);

		if (id >= 0) {
			SQLException se;
			if (id > 0) {
				se = new SQLException("Second Error-"+count2, "2000", 2000);
			}
			else {
				se = new SQLException("Third Error-"+count3, "3000", 3000);
			}
	        throw new InvalidDataAccessApiUsageException("Second or Third failure", se);
		}

		return user;
	}
}
