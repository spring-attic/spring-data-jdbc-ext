package org.springframework.data.jdbc.query;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jdbc.query.domain.Customer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations="classpath:query-dsl-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class CustomerDaoTest {

	@Autowired
	@Qualifier("RowMapper")
	private CustomerDao customerDao;
	
	@Autowired
	@Qualifier("QBean")
	private CustomerDao customerDaoQBean;
	
	@Autowired
	@Qualifier("MappingProjection")
	private CustomerDao customerDaoProjection;
	
	@Test
	public void testFindAllWithRowMapper() {
		List<Customer> customers = customerDao.findAll();
		Assert.assertEquals(2, customers.size());
	}
	
	@Test
	public void testFindAllWithQBean() {
		List<Customer> customers = customerDaoQBean.findAll();
		Assert.assertEquals(2, customers.size());
	}
	
	@Test
	public void testFindAllWithProjection() {
		List<Customer> customers = customerDaoProjection.findAll();
		Assert.assertEquals(2, customers.size());
	}
	
	@Test
	public void testFindOneWithRowMapper() {
		Customer c = customerDao.findById(1L);
		Assert.assertEquals("Mark", c.getFirstName());
	}

	@Test
	public void testFindOneWithQBean() {
		Customer c = customerDaoQBean.findById(1L);
		Assert.assertEquals("Mark", c.getFirstName());
	}

	@Test
	public void testFindOneWithProjection() {
		Customer c = customerDaoProjection.findById(1L);
		Assert.assertEquals("Pollack!", c.getLastName());
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
	public void testInsertBatch() {
		List<Customer> customers = new ArrayList<Customer>();
		Customer c1 = new Customer();
		c1.setFirstName("Oliver");
		c1.setLastName("Gierke");
		customers.add(c1);
		Customer c2 = new Customer();
		c2.setFirstName("Jon");
		c2.setLastName("Brisbin");
		customers.add(c2);
		Customer c3 = new Customer();
		c3.setFirstName("Costin");
		c3.setLastName("Leau");
		customers.add(c3);
		Customer c4 = new Customer();
		c4.setFirstName("Mark");
		c4.setLastName("Fisher");
		customers.add(c4);
		customerDao.addBatch(customers);
		List<Customer> results = customerDao.findAll();
		Assert.assertEquals(6, results.size());
	}

	@Transactional
	@Test
	public void testInsertAndGetKey() {
		Customer c = new Customer();
		c.setFirstName("Oliver");
		c.setLastName("Gierke");
		Long key = customerDao.addWithKey(c);
		Assert.assertNotNull(key);
		Customer inserted = customerDao.findById(key);
		Assert.assertEquals("Oliver", inserted.getFirstName());
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
	
	@Test
	public void testCountCustomer() {
		Customer c = new Customer();
		c.setFirstName("Oliver");
		c.setLastName("Gierke");
		customerDao.add(c);
		long count = customerDao.countCustomers();
		Assert.assertEquals(3, count);
	}

	@Test
	public void testCountDistinctCustomer() {
		Customer c = new Customer();
		c.setFirstName("Mark2");
		c.setLastName("Pollack");
		customerDao.add(c);
		c.setFirstName("Jack");
		c.setLastName("Pollok");
		customerDao.add(c);
		long count = customerDao.countDistinctForLastName("Poll");
		Assert.assertEquals(2, count);
	}

	@Test
	public void testCustomerExists() {
		Customer c = customerDao.findById(1L);
		boolean exists = customerDao.customerExists(c);
		Assert.assertTrue(exists);
	}

	@Test
	public void testCustomerNotExists() {
		boolean exists = customerDao.customerExists(99L);
		Assert.assertFalse(exists);
	}
}
