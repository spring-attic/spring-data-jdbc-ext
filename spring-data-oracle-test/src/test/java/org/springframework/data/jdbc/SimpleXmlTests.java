package org.springframework.data.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jdbc.test.xml.Item;
import org.springframework.data.jdbc.test.xml.XmlTypeDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleXmlTests {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("simpleXmlTypeDao")
	XmlTypeDao xmlDao;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional @Test
    public void readAndWriteXml() {
        String s = "<item id=\"11\">\n" +
                "  <itemName>Bar</itemName>\n" +
                "  <price>311</price>\n" +
                "</item>";
        xmlDao.addXmlItem(11L, s);
        int count = jdbcTemplate.queryForObject("select count(*) from xml_table where id = 11", Integer.class);
        assertEquals("actor not added", 1, count);
        String result = xmlDao.getXmlItem(11L);
        assertTrue("xml text not found", result.contains("<itemName>Bar</itemName>"));
    }

    @Transactional @Test
    public void marshallAndUnsmarshallXml() {
        Item i = new Item();
        i.setId(2L);
        i.setName("Bar");
        i.setPrice(new BigDecimal("123.45"));
        xmlDao.addItem(i);
        int count = jdbcTemplate.queryForObject("select count(*) from xml_table where id = 2", Integer.class);
        assertEquals("item not added", 1, count);
        Item i2 = xmlDao.getItem(2L);
        assertEquals("item not read", "Bar", i2.getName());
    }

}
