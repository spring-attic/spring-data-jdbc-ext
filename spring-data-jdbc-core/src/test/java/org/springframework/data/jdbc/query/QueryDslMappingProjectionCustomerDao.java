package org.springframework.data.jdbc.query;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jdbc.query.domain.Customer;
import org.springframework.data.jdbc.query.generated.QCustomer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.Tuple;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.QBean;

@Transactional
@Repository
@Qualifier("MappingProjection")
public class QueryDslMappingProjectionCustomerDao extends QueryDslCustomerDao {

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
			new MappingCustomerProjection(qCustomer.all()));
	}
	
	@Override
	public List<Customer> findAll() {
		SQLQuery sqlQuery = template.newSqlQuery()
			.from(qCustomer);
		return template.query(sqlQuery, 
				new MappingCustomerProjection(qCustomer.all()));
	}

	public class MappingCustomerProjection extends MappingProjection<Customer> {
		
		public MappingCustomerProjection(Expression<?>... args) {
	        super(Customer.class, args);
		}
		
		@Override
		protected Customer map(Tuple tuple) {
			Customer customer = new Customer();
			customer.setId(tuple.get(qCustomer.id));
			customer.setFirstName(tuple.get(qCustomer.firstName)); 
			customer.setLastName(tuple.get(qCustomer.lastName) + "!");
			return customer;
		}
	}
}
