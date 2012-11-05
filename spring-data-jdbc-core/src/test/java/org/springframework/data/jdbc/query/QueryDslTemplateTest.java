package org.springframework.data.jdbc.query;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.types.QBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jdbc.query.domain.Customer;
import org.springframework.data.jdbc.query.generated.QBadCustomer;
import org.springframework.data.jdbc.query.generated.QCustomer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@ContextConfiguration(locations="classpath:query-dsl-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class QueryDslTemplateTest {

	private final QCustomer qCustomer = QCustomer.customer;
	private final QBadCustomer qBadCustomer = QBadCustomer.customer;

	private QueryDslJdbcTemplate template;

	@Autowired
	public void setDataSource(DataSource dataSource) {
	this.template = new QueryDslJdbcTemplate(dataSource);
	}
	
	@Test(expected = DataAccessException.class)
	public void testCount() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		long results = template.count(sqlQuery);
	}
	
	@Test(expected = DataAccessException.class)
	public void testCountDistinct() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		long results = template.countDistinct(sqlQuery);
	}

	@Test(expected = DataAccessException.class)
	public void testExist() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		boolean results = template.exists(sqlQuery);
	}

	@Test(expected = DataAccessException.class)
	public void testNotExist() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		boolean results = template.notExists(sqlQuery);
	}

	@Test(expected = DataAccessException.class)
	public void testQueryForObjectWithRowMapper() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		Customer results = template.queryForObject(sqlQuery,
				BeanPropertyRowMapper.newInstance(Customer.class),
				qBadCustomer.all());
	}

	@Test(expected = DataAccessException.class)
	public void testQueryForObjectWithQBean() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		Customer results = template.queryForObject(sqlQuery,
				new QBean<Customer>(Customer.class, qBadCustomer.all()));
	}

	@Test(expected = DataAccessException.class)
	public void testInsert() {
		long results = template.insert(qBadCustomer,
				new SqlInsertCallback() {
					public long doInSqlInsertClause(SQLInsertClause insert) {
						insert.set(qBadCustomer.firstName, "Fail");
						return insert.execute();
					}
				});
	}

	@Test(expected = DataAccessException.class)
	public void testInsertWithKey() {
		Long results = template.insertWithKey(qBadCustomer,
				new SqlInsertWithKeyCallback<Long>() {
					public Long doInSqlInsertWithKeyClause(SQLInsertClause insert) throws SQLException {
						insert.set(qBadCustomer.firstName, "Fail");
						return insert.execute();
					}
				});
	}

	@Test(expected = DataAccessException.class)
	public void testUpdate() {
		long results = template.update(qBadCustomer,
				new SqlUpdateCallback() {
					public long doInSqlUpdateClause(SQLUpdateClause update) {
						update.set(qBadCustomer.firstName, "Fail");
						return update.execute();
					}
				});
	}

	@Test(expected = DataAccessException.class)
	public void testDelete() {
		long results = template.delete(qBadCustomer,
				new SqlDeleteCallback() {
					public long doInSqlDeleteClause(SQLDeleteClause delete) {
						delete.where(qBadCustomer.firstName.isNotNull());
						return delete.execute();
					}
				});
	}

	@Test
	public void testQueryWithStringPath() {
		SQLQuery sqlQuery = template.newSqlQuery()
					.from(qCustomer);
		List<String> results = template.query(sqlQuery, qCustomer.lastName);
	}
}
