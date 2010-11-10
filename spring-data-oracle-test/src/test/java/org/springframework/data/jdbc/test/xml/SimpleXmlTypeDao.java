package org.springframework.data.jdbc.test.xml;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.xml.SqlXmlHandler;
import org.springframework.jdbc.support.xml.SqlXmlObjectMappingHandler;
import org.springframework.stereotype.Repository;

/**
 * @author trisberg
 */
@Repository
public class SimpleXmlTypeDao implements XmlTypeDao {

    private SimpleJdbcTemplate simpleJdbcTemplate;

    @Autowired
    private SqlXmlHandler sqlXmlHandler;

    @Autowired
    private SqlXmlObjectMappingHandler sqlXmlObjectMappingHandler;

    @Autowired
    public void init(DataSource dataSource) {
        this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    public void addXmlItem(final Long id, final String xml) {
        simpleJdbcTemplate.update("INSERT INTO xml_table (id, xml_text) VALUES (?, ?)",
                id,
                sqlXmlHandler.newSqlXmlValue(xml));
    }

    public String getXmlItem(final Long id) {
        String s = simpleJdbcTemplate.queryForObject(
                "SELECT xml_text FROM xml_table WHERE id = ?",
                new ParameterizedRowMapper<String>() {
                    public String mapRow(ResultSet rs, int i) throws SQLException {
                        String s = sqlXmlHandler.getXmlAsString(rs, 1);
                        return s;
                    }
                },
                id);
        return s;
    }

    public void addItem(Item item) {
        simpleJdbcTemplate.update("INSERT INTO xml_table (id, xml_text) VALUES (?, ?)",
                item.getId(),
                sqlXmlObjectMappingHandler.newMarshallingSqlXmlValue(item));
    }

    public Item getItem(Long id) {
        Item i = simpleJdbcTemplate.queryForObject(
                "SELECT xml_text FROM xml_table WHERE id = ?",
                new ParameterizedRowMapper<Item>() {
                    public Item mapRow(ResultSet rs, int i) throws SQLException {
                        return (Item) sqlXmlObjectMappingHandler.getXmlAsObject(rs, 1);
                    }
                },
                id);
        return i;
    }

}
