package org.springframework.data.jdbc.query;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.query.domain.Customer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@ContextConfiguration(locations="classpath:query-dsl-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomerDaoTest {

	@Autowired
	private CustomerDao customerDao;
	
	@Test
	public void testFindAll() {
		List<Customer> customers = customerDao.findAll();
		Assert.assertEquals(2, customers.size());
	}
	
	@Test
	public void testFindOne() {
		Customer c = customerDao.findById(1L);
		Assert.assertEquals("Mark", c.getFirstName());
	}

	@Transactional
	@Test
	public void testInsert() {
		Customer c = new Customer();
		c.setFirstName("Oliver");
		c.setLastName("Gierke");
		customerDao.add(c);
		List<Customer> customers = customerDao.findAll();
		Assert.assertEquals(3, customers.size());
	}
	
	@Transactional
	@Test
	public void testUpdate() {
		Customer customer = customerDao.findById(1L);
		customer.setFirstName("Bubba");
		customer.setLastName("Smith");
		customerDao.save(customer);
		Customer found = customerDao.findById(1L);
		Assert.assertEquals("Bubba", found.getFirstName());
	}
	
	@Transactional
	@Test
	public void testDelete() {
		Customer c = customerDao.findById(1L);
		customerDao.delete(c);
		List<Customer> customers = customerDao.findAll();
		Assert.assertEquals(1, customers.size());
	}
	
}
