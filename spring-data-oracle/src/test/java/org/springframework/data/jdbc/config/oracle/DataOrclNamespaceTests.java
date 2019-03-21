/*
 * Copyright 2008-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.jdbc.config.oracle;

import junit.framework.TestCase;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import javax.xml.bind.SchemaOutputResolver;

/**
 * Tests for Oracle namespace
 *
 * Note: Tests should be run with working directory set as spring-data-jdbc-ext/spring-data-oracle
 *
 * @author Thomas Risberg
 */
public class DataOrclNamespaceTests extends TestCase {

    private XmlBeanFactory beanFactory;

	{
		// set ${test.home} for property file location resolution
		String pwd = System.getProperty("user.dir");
		String path = "/src/test/";
		System.setProperty("test.home", pwd + path);
	}

    protected void setUp() throws Exception {
        this.beanFactory = new XmlBeanFactory(new ClassPathResource("test-data-orcl-namespace.xml", getClass()));
    }

    public void testSimpleDataSourceDefinition() throws Exception {
        Object bean = this.beanFactory.getBean("simpleDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
    }

    public void testClasspathPropertyFileDataSourceDefinition() throws Exception {
        Object bean = this.beanFactory.getBean("classpathPropertyFileDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
    }

    public void testLocalPropertyFileDataSourceDefinition() throws Exception {
        Object bean = this.beanFactory.getBean("localPropertyFileDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
    }

    public void testConectionPropertiesDataSourceDefinition() throws Exception {
        Object bean = this.beanFactory.getBean("connectionPropertiesDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        assertTrue("connection properties not found", ((OracleDataSource)bean).getConnectionProperties().size() > 0);
        assertTrue("cache properties not found", ((OracleDataSource)bean).getConnectionCacheProperties().size() > 0);
    }

    public void testConnectionCachingIsEnabledByDefault() throws Exception {
        Object bean = this.beanFactory.getBean("cachingByDefaultDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        assertTrue("caching not enabled", ((OracleDataSource)bean).getConnectionCachingEnabled());
    }

    public void testConnectionCachingCanBeDisabled() throws Exception {
        Object bean = this.beanFactory.getBean("nonCachingDataSource");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        assertTrue("caching not enabled", !((OracleDataSource)bean).getConnectionCachingEnabled());
    }

    public void testConnectionCachingDisabledViaProperties() throws Exception {
        Object bean = this.beanFactory.getBean("nonCachingDataSourceUsingProperties");
        assertEquals("not the correct class", "oracle.jdbc.pool.OracleDataSource", bean.getClass().getName());
        assertTrue("caching not enabled", !((OracleDataSource)bean).getConnectionCachingEnabled());
    }

    public void testUsernameProxyDataSourceDefinition() throws Exception {
        Object bean = this.beanFactory.getBean("proxyDataSource");
        assertEquals("not the correct class", "org.springframework.data.jdbc.support.oracle.ProxyDataSource", bean.getClass().getName());
    }

    public void testUsernameProxyDataSourceDefinitionWithoutContext() throws Exception {
        try {
            this.beanFactory.getBean("proxyDataSourceWithoutContext");
            fail("Instantiation without ConnectionContextProvider should fail");
        }
        catch (BeanCreationException bce) {
            assertTrue(true);            
        }
    }

    public void testUsernamePasswordProxyDataSourceDefinition() throws Exception {
        Object bean = this.beanFactory.getBean("proxyWithPasswordDataSource");
        assertEquals("not the correct class", "org.springframework.data.jdbc.support.oracle.ProxyDataSource", bean.getClass().getName());
    }

    public void testRacFailoverInterceptor() throws Exception {
        this.beanFactory.getBean("racFailoverInterceptor");
    }

    public void testAqConnectionFactoryDefinition() throws Exception {
        Object bean = this.beanFactory.getBean("aqConnectionFactory");
        assertEquals("not the correct class",
                "oracle.jms.AQjmsConnectionFactory",
                bean.getClass().getName());
    }

    public void testAqConnectionFactoryDefinitionWithQueueType() throws Exception {
        Object bean = this.beanFactory.getBean("aqQueueConnectionFactory");
        assertEquals("not the correct class",
                "oracle.jms.AQjmsQueueConnectionFactory",
                bean.getClass().getName());
    }

    public void testAqConnectionFactoryDefinitionWithTopicType() throws Exception {
        Object bean = this.beanFactory.getBean("aqTopicConnectionFactory");
        assertEquals("not the correct class",
                "oracle.jms.AQjmsTopicConnectionFactory",
                bean.getClass().getName());
    }
}
