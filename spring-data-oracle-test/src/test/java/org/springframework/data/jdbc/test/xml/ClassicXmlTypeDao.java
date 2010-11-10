package org.springframework.data.jdbc.test.xml;

import org.springframework.data.jdbc.support.oracle.OracleXmlTypeValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.xml.SqlXmlHandler;
import org.springframework.jdbc.support.xml.SqlXmlObjectMappingHandler;
import org.springframework.stereotype.Repository;

/**
 * @author trisberg
 */
@Repository
public class ClassicXmlTypeDao implements XmlTypeDao {

    private JdbcTemplate jdbcTemplate;

    private SqlXmlHandler sqlXmlHandler;

    @Autowired
    private SqlXmlObjectMappingHandler sqlXmlObjectMappingHandler;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public void setSqlXmlHandler(SqlXmlHandler sqlXmlHandler) {
        this.sqlXmlHandler = sqlXmlHandler;
    }

    public void addXmlItem(final Long id, final String xml) {
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        jdbcTemplate.update("INSERT INTO xml_table (id, xml_text) VALUES (?, ?)",
                new Object[] {
                        id,
                        new OracleXmlTypeValue(is)
                });
    }

    @SuppressWarnings("unchecked")
	public String getXmlItem(final Long id) {
        List<String> l = jdbcTemplate.query("select xml_text from xml_table where id = ?",
                new Object[] {id},
                new RowMapper() {
                    public Object mapRow(ResultSet rs, int i) throws SQLException {
                        String s = sqlXmlHandler.getXmlAsString(rs, 1);
                        return s;
                    }
                });
        if (l.size() > 0) {
            return l.get(0);
        }
        return null;
    }

    public void addItem(Item item) {
        jdbcTemplate.update("INSERT INTO xml_table (id, xml_text) VALUES (?, ?)",
                new Object[] {
                	item.getId(),
                	sqlXmlObjectMappingHandler.newMarshallingSqlXmlValue(item)
                }
        );
    }

    public Item getItem(Long id) {
        Item i = (Item) jdbcTemplate.queryForObject(
                "SELECT xml_text FROM xml_table WHERE id = ?",
                new Object[] {id},
                new RowMapper<Item>() {
                    public Item mapRow(ResultSet rs, int i) throws SQLException {
                        return (Item) sqlXmlObjectMappingHandler.getXmlAsObject(rs, 1);
                    }
                });
        return i;
    }
}
