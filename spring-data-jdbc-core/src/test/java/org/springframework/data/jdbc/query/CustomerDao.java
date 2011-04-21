package org.springframework.data.jdbc.query;

import java.util.List;

import org.springframework.data.jdbc.query.domain.Customer;
import org.springframework.data.jdbc.query.domain.CustomerQ;

public interface CustomerDao {

	void add(Customer customer);

	void save(Customer customer);

	void delete(Customer customer);

	Customer findById(Long id);
	
	CustomerQ findByIdQ(Long id);
	
	List<Customer> findAll();

	List<CustomerQ> findAllQ();

}
