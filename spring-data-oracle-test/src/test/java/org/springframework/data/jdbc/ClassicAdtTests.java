package org.springframework.data.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jdbc.test.adt.Actor;
import org.springframework.data.jdbc.test.adt.AdvancedDataTypesDao;
import org.springframework.data.jdbc.test.adt.SqlActor;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ClassicAdtTests {

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("classicAdvancedDataTypesDao")
	private AdvancedDataTypesDao dao;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional @Test
    public void testSqlData() {
        SqlActor a1 = new SqlActor();
        a1.setId(4L);
        a1.setName("Adrian");
        a1.setAge(44);
        dao.addSqlActor(a1);
        int count = jdbcTemplate.queryForObject("select count(*) from actor where id = 4", Integer.class);
        assertEquals("actor not added", 1, count);
        SqlActor a2 = dao.getSqlActor(4L);
        assertEquals("", "Adrian", a2.getName());
        jdbcTemplate.execute(
                (ConnectionCallback<Object>) conn -> {
                    conn.getTypeMap().clear();
                    return null;
                });
    }


    @Transactional @Test
    public void testStruct() {
        Actor a1 = new Actor();
        a1.setId(4L);
        a1.setName("Adrian");
        a1.setAge(44);
        dao.addActor(a1);
        int count = jdbcTemplate.queryForObject("select count(*) from actor where id = 4", Integer.class);
        assertEquals("actor not added", 1, count);
        Actor a2 = dao.getActor(4L);
        assertEquals("", "Adrian", a2.getName());
    }


    @Transactional @Test
    public void testArray() {
        Long[] ids = new Long[] {2L, 3L};  
        dao.deleteActors(ids);
    	String[] names = dao.getActorNames();
    	assertTrue("", names.length == 1);
    }

    @Transactional @Test
    public void testRefCursor() {
    	List<Actor> actors = dao.getActors();
    	assertTrue("", actors.size() > 0);
    }
    
	@Transactional @Test
	public void testStructArray() {
 		List<Actor> actors = dao.getAllActors();
 		assertTrue("", actors.size() > 0);
		Long[] ids = new Long[actors.size()];
		for (int i = 0; i < actors.size(); i++) {
			ids[i] = actors.get(i).getId();
		}
		dao.deleteActors(ids);
		assertTrue("", dao.getAllActors().size() == 0);
		dao.saveActors(actors);
		assertTrue("", dao.getAllActors().size() == actors.size());
 	}

}
