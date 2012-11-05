package org.springframework.data.jdbc.query;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MappingProjection;
import org.springframework.data.jdbc.query.domain.Customer;
import org.springframework.data.jdbc.query.generated.QCustomer;

public class MappingCustomerProjection extends MappingProjection<Customer> {

	private final QCustomer qCustomer = QCustomer.customer;

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
