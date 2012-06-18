package org.springframework.data.jdbc.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.domain.Address;
import org.springframework.data.jdbc.core.domain.Customer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@ContextConfiguration(locations="classpath:core-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class OneToManyResultSetExtractorTests {

	@Autowired
	DataSource dataSource;

	JdbcTemplate template;

	OneToManyResultSetExtractor<Customer, Address, Integer> resultSetExtractor;

	@Before
	public void before() {
		template = new JdbcTemplate(dataSource);
		resultSetExtractor = new CustomerAddressExtractor();
	}

	@Test
	public void testExtractingData() {
		System.out.println("TEST");
		List<Customer> result = template.query(
				"select customer.id, customer.name, address.id, address.customer_id, address.street, address.city from customer " +
				"left join address on customer.id = address.customer_id",
				resultSetExtractor);
		System.out.println(result);
	}

	private static class CustomerAddressExtractor extends OneToManyResultSetExtractor<Customer, Address, Integer> {

		public CustomerAddressExtractor() {
			super(
				new RowMapper<Customer>() {
					public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
						Customer c = new Customer();
						c.setId(rs.getInt("customer.id"));
						c.setName(rs.getString("customer.name"));
						return c;
					}
				},
				new RowMapper<Address>() {
					public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
						Address a = new Address();
						a.setId(rs.getInt("address.id"));
						a.setStreet(rs.getString("address.street"));
						a.setCity(rs.getString("address.city"));
						return a;
					}
				});
		}

		@Override
		protected Integer mapPrimaryKey(ResultSet rs) throws SQLException {
			return rs.getInt("customer.id");
		}

		@Override
		protected Integer mapForeignKey(ResultSet rs) throws SQLException {
			if (rs.getObject("address.customer_id") == null) {
				return null;
			}
			else {
				return rs.getInt("address.customer_id");
			}
		}

		@Override
		protected void addChild(Customer root, Address child) {
			root.addAddress(child);
		}
	}
}
