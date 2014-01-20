package org.springframework.data.jdbc.query;

import com.mysema.query.sql.HSQLDBTemplates;
import com.mysema.query.sql.SQLTemplates;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.query.generated.QCustomer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:query-dsl-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class QueryDslTemplateConstructionTest {

	private final QCustomer qCustomer = QCustomer.customer;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private JdbcTemplate jdbcTemplate;

	@Test
	public void testPrintSchema() {
		SQLTemplates dialectWithSchema = HSQLDBTemplates.builder().printSchema().build();
		SQLTemplates dialectWithQuotes = HSQLDBTemplates.builder().quote().build();
		SQLTemplates dialectPlain = HSQLDBTemplates.builder().build();
		QueryDslJdbcTemplate templateWithSchema = new QueryDslJdbcTemplate(jdbcTemplate, dialectWithSchema);
		QueryDslJdbcTemplate templateWithQuotes = new QueryDslJdbcTemplate(jdbcTemplate, dialectWithQuotes);
		QueryDslJdbcTemplate templatePlain = new QueryDslJdbcTemplate(jdbcTemplate, dialectPlain);
		assertEquals("from PUBLIC.CUSTOMER CUSTOMER", templateWithSchema.newSqlQuery().from(qCustomer).toString());
		assertEquals("from \"CUSTOMER\" \"CUSTOMER\"", templateWithQuotes.newSqlQuery().from(qCustomer).toString());
		assertEquals("from CUSTOMER CUSTOMER", templatePlain.newSqlQuery().from(qCustomer).toString());
	}

}
