package org.springframework.data.jdbc.query;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jdbc.query.domain.Customer;
import org.springframework.data.jdbc.query.generated.QCustomer;
import org.springframework.data.jdbc.query.generated.QCustomerNames;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;

@Transactional
@Repository
@Qualifier("RowMapper")
public class QueryDslCustomerDao implements CustomerDao {

    private final QCustomer qCustomer = QCustomer.customer;

    private QueryDslJdbcTemplate template;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.template = new QueryDslJdbcTemplate(dataSource);
	}
	
	public Customer findById(Long id) {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer)
			.where(qCustomer.id.eq(id));
		return template.queryForObject(sqlQuery, 
			BeanPropertyRowMapper.newInstance(Customer.class), 
			qCustomer.all());
	}
	
	public List<Customer> findAll() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer);
		return template.query(sqlQuery, 
			BeanPropertyRowMapper.newInstance(Customer.class), 
			qCustomer.all());
	}

	public void add(final Customer customer) {
		template.insert(qCustomer, new SqlInsertCallback() {
			public long doInSqlInsertClause(SQLInsertClause sqlInsertClause) {
				return sqlInsertClause.columns(qCustomer.firstName, qCustomer.lastName)
					.values(customer.getFirstName(), customer.getLastName()).execute();
			}
		});
	}

	public Long addWithKey(final Customer customer) {
		Long generatedKey = template.insertWithKey(qCustomer, new SqlInsertWithKeyCallback<Long>() {
			public Long doInSqlInsertWithKeyClause(
					SQLInsertClause sqlInsertClause) {
				return sqlInsertClause
						.columns(qCustomer.firstName,
								qCustomer.lastName)
						.values(customer.getFirstName(),
								customer.getLastName()).executeWithKey(qCustomer.id);
			}
		});
		return generatedKey;
	}

	public long addBatch(final List<Customer> customers) {
		long inserted = template.insert(qCustomer, new SqlInsertCallback() {
			public long doInSqlInsertClause(SQLInsertClause insert) {
				insert.columns(qCustomer.firstName, qCustomer.lastName);
				for (Customer customer : customers) {
					insert.values(customer.getFirstName(), customer.getLastName()).addBatch();
				}
				return insert.execute();
			}
		});
		return inserted;
	}

	public void save(final Customer customer) {
		template.update(qCustomer, new SqlUpdateCallback() {
			public long doInSqlUpdateClause(SQLUpdateClause sqlUpdateClause) {
				return sqlUpdateClause.where(qCustomer.id.eq(customer.getId()))
					.set(qCustomer.firstName, customer.getFirstName())
					.set(qCustomer.lastName, customer.getLastName())
					.execute();
			}
		});
	}

	public void delete(final Customer customer) {
		template.delete(qCustomer, new SqlDeleteCallback() {
			public long doInSqlDeleteClause(SQLDeleteClause sqlDeleteClause) {
				return sqlDeleteClause.where(qCustomer.id.eq(customer.getId()))
					.execute();
			}
		});
	}

	public long countCustomers() {
		SQLQuery sqlQuery = template.newSqlQuery()
		.from(qCustomer);
		return template.count(sqlQuery);
	}

	public long countDistinctForLastName(String name) {
		QCustomerNames qCustomerNames = QCustomerNames.customerNames;
		SQLQuery sqlQuery = template.newSqlQuery()
		.from(qCustomerNames)
		.where(qCustomerNames.name.like(name + "%"));
		return template.countDistinct(sqlQuery);
	}

	public boolean customerExists(Long id) {
		SQLQuery sqlQuery = template.newSqlQuery()
		.from(qCustomer)
		.where(qCustomer.id.eq(id));
		return !template.notExists(sqlQuery);
	}

	public boolean customerExists(Customer c) {
		SQLQuery sqlQuery = template.newSqlQuery()
		.from(qCustomer)
		.where(qCustomer.id.eq(c.getId()));
		return template.exists(sqlQuery);
	}
	
}
