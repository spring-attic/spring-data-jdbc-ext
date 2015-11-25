package org.springframework.data.jdbc.query;

import com.mysema.query.SearchResults;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.types.QBean;
import junit.framework.Assert;
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

	@Test
	public void testQueryWithStringPath() {
		SQLQuery sqlQuery = template.newSqlQuery()
				.from(qCustomer);
		List<String> results = template.query(sqlQuery, qCustomer.lastName);
		Assert.assertEquals(2, results.size());
	}

	@Test
	public void testQueryWithQBean() {
		SQLQuery sqlQuery = template.newSqlQuery()
				.from(qCustomer)
				.where(qCustomer.firstName.isNotNull());
		List<Customer> results = template.query(sqlQuery,
				new QBean<Customer>(Customer.class, qCustomer.all()));
		Assert.assertEquals(2, results.size());
	}

	@Test
	public void testQueryWithMappingProjection() {
		SQLQuery sqlQuery = template.newSqlQuery()
				.from(qCustomer)
				.where(qCustomer.firstName.isNotNull());
		List<Customer> results = template.query(sqlQuery,
				new MappingCustomerProjection(qCustomer.all()));
		Assert.assertEquals(2, results.size());
	}
	@Test

	public void testQueryWithRowMapper() {
		SQLQuery sqlQuery = template.newSqlQuery()
				.from(qCustomer)
				.where(qCustomer.firstName.isNotNull());
		List<Customer> results = template.query(sqlQuery,
				BeanPropertyRowMapper.newInstance(Customer.class),
				qCustomer.all());

		Assert.assertEquals(2, results.size());
	}

	@Test
	public void testCount() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer)
			.where(qCustomer.firstName.isNotNull());
		long results = template.count(sqlQuery);
		Assert.assertEquals(2, results);
	}

	@Test
	public void testCountDistinct() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer)
			.where(qCustomer.firstName.isNotNull());
		long results = template.countDistinct(sqlQuery);
		Assert.assertEquals(2, results);
	}

	@Test
	public void testExist() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer)
			.where(qCustomer.firstName.isNotNull());
		boolean results = template.exists(sqlQuery);
		Assert.assertEquals(true, results);
	}

	@Test
	public void testNotExist() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer)
			.where(qCustomer.firstName.isNotNull());
		boolean results = template.notExists(sqlQuery);
		Assert.assertEquals(false, results);
	}

	@Test(expected = DataAccessException.class)
	public void testBadCount() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		long results = template.count(sqlQuery);
	}
	
	@Test(expected = DataAccessException.class)
	public void testBadCountDistinct() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		long results = template.countDistinct(sqlQuery);
	}

	@Test(expected = DataAccessException.class)
	public void testBadExist() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		boolean results = template.exists(sqlQuery);
	}

	@Test(expected = DataAccessException.class)
	public void testBadNotExist() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		boolean results = template.notExists(sqlQuery);
	}

	@Test(expected = DataAccessException.class)
	public void testBadQueryForObjectWithRowMapper() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		Customer results = template.queryForObject(sqlQuery,
				BeanPropertyRowMapper.newInstance(Customer.class),
				qBadCustomer.all());
	}

	@Test(expected = DataAccessException.class)
	public void testBadQueryForObjectWithQBean() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qBadCustomer)
			.where(qBadCustomer.firstName.isNotNull());
		Customer results = template.queryForObject(sqlQuery,
				new QBean<Customer>(Customer.class, qBadCustomer.all()));
	}

	@Test(expected = DataAccessException.class)
	public void testBadInsert() {
		long results = template.insert(qBadCustomer,
				new SqlInsertCallback() {
					public long doInSqlInsertClause(SQLInsertClause insert) {
						insert.set(qBadCustomer.firstName, "Fail");
						return insert.execute();
					}
				});
	}

	@Test(expected = DataAccessException.class)
	public void testBadInsertWithKey() {
		Long results = template.insertWithKey(qBadCustomer,
				new SqlInsertWithKeyCallback<Long>() {
					public Long doInSqlInsertWithKeyClause(SQLInsertClause insert) throws SQLException {
						insert.set(qBadCustomer.firstName, "Fail");
						return insert.execute();
					}
				});
	}

	@Test(expected = DataAccessException.class)
	public void testBadUpdate() {
		long results = template.update(qBadCustomer,
				new SqlUpdateCallback() {
					public long doInSqlUpdateClause(SQLUpdateClause update) {
						update.set(qBadCustomer.firstName, "Fail");
						return update.execute();
					}
				});
	}

	@Test(expected = DataAccessException.class)
	public void testBadDelete() {
		long results = template.delete(qBadCustomer,
				new SqlDeleteCallback() {
					public long doInSqlDeleteClause(SQLDeleteClause delete) {
						delete.where(qBadCustomer.firstName.isNotNull());
						return delete.execute();
					}
				});
	}

	@Test
	public void testQueryWithSearchResults() {
		SQLQuery sqlQuery = template.newSqlQuery()
				.from(qCustomer)
				.where(qCustomer.firstName.isNotNull());
		SearchResults<Customer> results =
				template.queryResults(sqlQuery, new QBean<Customer>(Customer.class, qCustomer.all()));
		Assert.assertEquals(2, results.getResults().size());
	}
}
