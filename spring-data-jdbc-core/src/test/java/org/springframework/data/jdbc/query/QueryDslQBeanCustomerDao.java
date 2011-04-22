package org.springframework.data.jdbc.query;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jdbc.query.domain.Customer;
import org.springframework.data.jdbc.query.generated.QCustomer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.QBean;

@Transactional
@Repository
@Qualifier("QBean")
public class QueryDslQBeanCustomerDao extends QueryDslCustomerDao {

    private final QCustomer qCustomer = QCustomer.customer;

    private QueryDslJdbcTemplate template;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.template = new QueryDslJdbcTemplate(dataSource);
	}
	
	@Override
	public Customer findById(Long id) {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer)
			.where(qCustomer.id.eq(id));
		return template.queryForObject(sqlQuery, 
			new QBean<Customer>(Customer.class, qCustomer.all()));
	}
	
	@Override
	public List<Customer> findAll() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer);
		return template.query(sqlQuery, 
				new QBean<Customer>(Customer.class, qCustomer.all()));
	}
	
}
