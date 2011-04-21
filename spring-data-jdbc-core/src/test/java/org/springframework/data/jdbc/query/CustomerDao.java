package org.springframework.data.jdbc.query;

import java.util.List;

import org.springframework.data.jdbc.query.domain.Customer;

public interface CustomerDao {

	void add(Customer customer);

	void save(Customer customer);

	void delete(Customer customer);

	Customer findById(Long id);
	
	List<Customer> findAll();
	
	long countCustomers();
	
	long countDistinctForLastName(String name);
	
	boolean customerExists(Long id);
	
	boolean customerExists(Customer c);

}
