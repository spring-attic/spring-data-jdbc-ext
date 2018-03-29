package org.springframework.data.jdbc.test.xml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.xml.SqlXmlHandler;
import org.springframework.jdbc.support.xml.SqlXmlObjectMappingHandler;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author trisberg
 */
@Repository
public class SimpleXmlTypeDao implements XmlTypeDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SqlXmlHandler sqlXmlHandler;

    @Autowired
    private SqlXmlObjectMappingHandler sqlXmlObjectMappingHandler;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addXmlItem(final Long id, final String xml) {
        jdbcTemplate.update("INSERT INTO xml_table (id, xml_text) VALUES (?, ?)",
                id,
                sqlXmlHandler.newSqlXmlValue(xml));
    }

    public String getXmlItem(final Long id) {
        String s = jdbcTemplate.queryForObject(
                "SELECT xml_text FROM xml_table WHERE id = ?",
                new SingleColumnRowMapper<String>() {
                    public String mapRow(ResultSet rs, int i) throws SQLException {
                        String s = sqlXmlHandler.getXmlAsString(rs, 1);
                        return s;
                    }
                },
                id);
        return s;
    }

    public void addItem(Item item) {
        jdbcTemplate.update("INSERT INTO xml_table (id, xml_text) VALUES (?, ?)",
                item.getId(),
                sqlXmlObjectMappingHandler.newMarshallingSqlXmlValue(item));
    }

    public Item getItem(Long id) {
        Item i = jdbcTemplate.queryForObject(
                "SELECT xml_text FROM xml_table WHERE id = ?",
                (rs, i1) -> (Item) sqlXmlObjectMappingHandler.getXmlAsObject(rs, 1),
                id);
        return i;
    }

}
