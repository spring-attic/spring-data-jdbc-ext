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

package org.springframework.data.jdbc.aop;

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Thomas Risberg
 */
public class ConnectionInterceptorTests extends TestCase {

    private ApplicationContext appContext;

    protected void setUp() throws Exception {
        this.appContext = new ClassPathXmlApplicationContext("org/springframework/data/jdbc/aop/test-orcl-interceptor.xml");
    }

    public void testSimpleGetConnectionWithInterceptor() throws Exception {
        Object bean = this.appContext.getBean("simpleDataSource");
        assertTrue("not a proxy", bean.getClass().getName().toUpperCase().indexOf("PROXY") > 0);
        try {
            DataSourceUtils.getConnection((DataSource)bean);
        }
        catch (DataAccessException e) {
            fail("issue getting connection: " + e.getMessage());
        }
        CountingConnectionPreparer prep =
                (CountingConnectionPreparer) this.appContext.getBean("countingConnectionPreparer");
        assertTrue("connection not intercepted", prep.getCount() > 0);
    }

    public void testSimpleGetConnectionWithTransactionSynchronization() throws Exception {
        final Object bean = this.appContext.getBean("simpleDataSource");
        final DataSource ds = (DataSource)bean;
        final int[] commits = new int[] {0};
        assertTrue("not a proxy", ds.getClass().getName().toUpperCase().indexOf("PROXY") > 0);
        PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
        ((DataSourceTransactionManager)tm).afterPropertiesSet();
        TransactionTemplate tt = new TransactionTemplate(tm);
        try {
            tt.execute(new TransactionCallback<Object>() {

                public Object doInTransaction(TransactionStatus transactionStatus) {
                    Connection con = DataSourceUtils.getConnection((DataSource)bean);
                    TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronization() {
                                public void suspend() {
                                }

                                public void resume() {
                                }

                                public void flush() {
                                }

                                public void beforeCommit(boolean b) {
                                }

                                public void beforeCompletion() {
                                }

                                public void afterCommit() {
                                    commits[0] = commits[0] + 1;
                                }

                                public void afterCompletion(int i) {
                                }
                            });
                    DataSourceUtils.releaseConnection(con, ds);
                    return null;
                }
            });
        }
        catch (DataAccessException e) {
            fail("issue getting connection: " + e.getMessage());
        }
        assertTrue("commit not counted", commits[0] > 0);
    }

}
