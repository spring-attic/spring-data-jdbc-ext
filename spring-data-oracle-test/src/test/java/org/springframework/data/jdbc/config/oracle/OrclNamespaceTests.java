package org.springframework.data.jdbc.config.oracle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Thomas Risberg
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class OrclNamespaceTests implements BeanFactoryAware {
	
	private BeanFactory beanFactory;

    @Test
    public void testSimpleDataSourceDefinition() throws Exception {
        Object bean = this.beanFactory.getBean("simpleDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        String databaseName = (String)JdbcUtils.extractDatabaseMetaData((DataSource)bean, "getDatabaseProductName");
        assertEquals("wrong database", "Oracle", databaseName);
    }
    
    @Test
    public void testWithPropertyFile() throws Exception {
        Object bean = this.beanFactory.getBean("propertyFileDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        String databaseName = (String)JdbcUtils.extractDatabaseMetaData((DataSource)bean, "getDatabaseProductName");
        assertEquals("wrong database", "Oracle", databaseName);
    }

    @Test
    public void testWithConnectionProperties() throws Exception {
        Object bean = this.beanFactory.getBean("connectionPropertiesDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        oracle.jdbc.pool.OracleDataSource ds = (oracle.jdbc.pool.OracleDataSource)bean;
        String prop = ds.getConnectionProperties().getProperty("processEscapes");
        assertEquals("processEscapes property not set", "false", prop);
    }

    @Test
    public void testWithCacheProperties() throws Exception {
        Object bean = this.beanFactory.getBean("cachePropertiesDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        oracle.jdbc.pool.OracleDataSource ds = (oracle.jdbc.pool.OracleDataSource)bean;
        String prop = ds.getConnectionCacheProperties().getProperty("InitialLimit");
        assertEquals("InitialLimit property not set", "7", prop);
    }

    @Test
    public void testCachingByDefault() throws Exception {
        Object bean = this.beanFactory.getBean("simpleDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        oracle.jdbc.pool.OracleDataSource ds = (oracle.jdbc.pool.OracleDataSource)bean;
        boolean cachingEnabled = ds.getConnectionCachingEnabled();
        assertTrue("caching not enabled", cachingEnabled);
    }

    @Test
    public void testCachingTurnedOffWithAttribute() throws Exception {
        Object bean = this.beanFactory.getBean("nonCachingDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        oracle.jdbc.pool.OracleDataSource ds = (oracle.jdbc.pool.OracleDataSource)bean;
        boolean cachingEnabled = ds.getConnectionCachingEnabled();
        assertTrue("caching should not be enabled", !cachingEnabled);
    }

    @Test
    public void testCachingTurnedOffWithProperties() throws Exception {
        Object bean = this.beanFactory.getBean("nonCachingDataSourceUsingProperties");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        oracle.jdbc.pool.OracleDataSource ds = (oracle.jdbc.pool.OracleDataSource)bean;
        boolean cachingEnabled = ds.getConnectionCachingEnabled();
        assertTrue("caching should not be enabled", !cachingEnabled);
    }

    @Test
    public void testProxyUser() throws Exception {
        Object bean = this.beanFactory.getBean("proxyDataSource");
        assertEquals("not the correct class", "org.springframework.data.jdbc.support.oracle.ProxyDataSource", bean.getClass().getName());
        DataSource ds = (DataSource)bean;
        JdbcTemplate jt = new JdbcTemplate(ds);
        String currentUser = jt.queryForObject("select {fn user()} from dual", String.class);
        assertEquals("not connecting via proxy", "SCOTT", currentUser);
    }

    //@Test
    // This doesn't work with older drivers - you get
    // ORA-28183: proper authentication not provided by proxy
    // 11gR2 and newer 11gR1 versions should work
    public void testProxyUserWithPassword() throws Exception {
        Object bean = this.beanFactory.getBean("proxyDataSource2");
        assertEquals("not the correct class", "org.springframework.data.jdbc.support.oracle.ProxyDataSource", bean.getClass().getName());
        DataSource ds = (DataSource)bean;
        JdbcTemplate jt = new JdbcTemplate(ds);
        String currentUser = jt.queryForObject("select {fn user()} from dual", String.class);
        assertEquals("not connecting via proxy", "SCOTT2", currentUser);
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
