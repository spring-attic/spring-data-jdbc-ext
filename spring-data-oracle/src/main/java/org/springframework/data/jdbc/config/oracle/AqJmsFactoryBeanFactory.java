/*
 * Copyright 2008-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.jdbc.config.oracle;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.jdbc.support.nativejdbc.SimpleNativeJdbcExtractor;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import javax.jms.ConnectionFactory;

import oracle.jms.AQjmsFactory;
import oracle.jdbc.OracleConnection;

import java.sql.*;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * FactoryBean used for the "aq-jms-queue-connection-factory" element of the "orcl" name space
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class AqJmsFactoryBeanFactory implements FactoryBean<ConnectionFactory> {

    protected static final Log logger = LogFactory.getLog(AqJmsFactoryBeanFactory.class);

    private ConnectionFactory aqConnectionFactory;

    private boolean coordinateWithDataSourceTransactions = false;

    private DataSource dataSource;

    private NativeJdbcExtractor nativeJdbcExtractor = new SimpleNativeJdbcExtractor();
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setCoordinateWithDataSourceTransactions(boolean coordinateWithDataSourceTransactions) {
        this.coordinateWithDataSourceTransactions = coordinateWithDataSourceTransactions;
    }

    public void setNativeJdbcExtractor(NativeJdbcExtractor nativeJdbcExtractor) {
        this.nativeJdbcExtractor = nativeJdbcExtractor;
    }

    public synchronized ConnectionFactory getObject() throws Exception {
        DataSource dataSourceToUse;
        if (coordinateWithDataSourceTransactions) {
            if (logger.isDebugEnabled()) {
                logger.debug("Enabling coordination of messaging transactions with data source transactions");
            }
            dataSourceToUse = new TransactionAwareDataSource(dataSource);
        }
        else {
            dataSourceToUse = dataSource;
        }
        if (aqConnectionFactory == null) {
            aqConnectionFactory = AQjmsFactory.getConnectionFactory(dataSourceToUse);
        }
        return aqConnectionFactory;
    }

    public Class<? extends ConnectionFactory> getObjectType() {
        return ConnectionFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }

    private class TransactionAwareDataSource extends DelegatingDataSource {

        public TransactionAwareDataSource(DataSource dataSource) {
            super(dataSource);
        }

        @Override
        public java.sql.Connection getConnection() throws SQLException {
            java.sql.Connection con = DataSourceUtils.getConnection(getTargetDataSource());
            java.sql.Connection conToUse = con;
            if (!(con instanceof OracleConnection)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Unwrapping JDBC Connection of type:" + con.getClass().getName());
                }
                try {
                    conToUse = nativeJdbcExtractor.getNativeConnection(con);
                } catch (SQLException e) {
                    throw new NonTransientDataAccessResourceException(
                            "Error unwrapping the Oracle Connection: " + e.getMessage(), e);
                }
            }

            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Using Proxied JDBC Connection [" + conToUse + "]");
                }
                return getCloseSuppressingConnectionProxy(conToUse);
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Using Native JDBC Connection [" + conToUse + "]");
                }
                return conToUse;
            }
        }

    }

    /**
     * Wrap the given Connection with a proxy that delegates every method call
     * to it but suppresses close calls.
     * @param target the original Connection to wrap
     * @return the wrapped Connection
     */
    protected Connection getCloseSuppressingConnectionProxy(Connection target) {
        return (Connection) Proxy.newProxyInstance(OracleConnectionProxy.class.getClassLoader(),
                new Class[] { OracleConnectionProxy.class }, new CloseSuppressingInvocationHandler(target));
    }

    /**
     * Invocation handler that suppresses close calls on JDBC Connections until
     * the associated instance of the ExtendedConnectionDataSourceProxy
     * determines the connection should actually be closed.
     */
    private static class CloseSuppressingInvocationHandler implements InvocationHandler {

        private final Connection target;

        public CloseSuppressingInvocationHandler(Connection target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Invocation on ConnectionProxy interface coming in...

            if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0] ? Boolean.TRUE : Boolean.FALSE);
            }
            else if (method.getName().equals("hashCode")) {
                // Use hashCode of Connection proxy.
                return new Integer(System.identityHashCode(proxy));
            }
            else if (method.getName().equals("close")) {
                // Handle close method: don't pass the call on
                if (logger.isDebugEnabled()) {
                    logger.debug("Coordinating transaction management for Connection [" + target + "]");
                }
                return null;
            }
            else if (method.getName().equals("getTargetConnection")) {
                // Handle getTargetConnection method: return underlying
                // Connection.
                return this.target;
            }

            // Invoke method on target Connection.
            try {
                return method.invoke(this.target, args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

}
