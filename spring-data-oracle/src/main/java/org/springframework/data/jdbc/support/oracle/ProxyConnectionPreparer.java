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

package org.springframework.data.jdbc.support.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.data.jdbc.support.ConnectionContextProvider;
import org.springframework.data.jdbc.support.ConnectionPreparer;
import org.springframework.data.jdbc.support.ConnectionUsernamePasswordProvider;
import org.springframework.data.jdbc.support.ConnectionUsernameProvider;
import org.springframework.util.Assert;
import org.springframework.jdbc.datasource.ConnectionProxy;
import oracle.jdbc.OracleConnection;

/**
 * A ConnectionPreparer that will delegate to a ConnectionContextProvider to obtaing the current user name
 * to be used for the proxy connection.
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class ProxyConnectionPreparer implements ConnectionPreparer {

    private ConnectionContextProvider contextProvider;

    public void setContextProvider(ConnectionContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public Connection prepare(Connection connection) {
        Assert.notNull(contextProvider, "You must provide a ConnectionContextProvider implementation that provides the username");

        OracleConnection oraCon;

        if (contextProvider instanceof ConnectionUsernamePasswordProvider || contextProvider instanceof ConnectionUsernameProvider) {
            if (connection instanceof OracleConnection) {
                oraCon = (OracleConnection) connection;
            }
            else {
                try {
                    oraCon = connection.unwrap(OracleConnection.class);
                    if (oraCon == null) {
                        throw new NonTransientDataAccessResourceException("Native connection is not of type OracleConnection");
                    }
                } catch (SQLException e) {
                    throw new NonTransientDataAccessResourceException("Unable to access native connection: " + e.getMessage(), e);
                }
            }
        }
        else {
            throw new InvalidDataAccessApiUsageException("The provided ContextProvider must implement one of the CurrenUsernameProvider or CurrentUsernamePasswordProvider interfaces");
        }

        try {
            return doPrepareUserNameProxyConnection((ConnectionUsernameProvider)contextProvider, oraCon);
        } catch (SQLException e) {
            System.out.println("!!! " + e);
            throw new NonTransientDataAccessResourceException("Unable to prepare user name proxy connection: " + e.getMessage(), e);
        }
    }

    private Connection doPrepareUserNameProxyConnection(ConnectionUsernameProvider contextProvider, OracleConnection oraCon)
            throws SQLException {

        String proxyUserName = contextProvider.getUserName();
        String proxyPassword = null;
        if (contextProvider instanceof ConnectionUsernamePasswordProvider) {
        	proxyPassword = ((ConnectionUsernamePasswordProvider)contextProvider).getPassword();
        }
        if (proxyUserName != null) {
            java.util.Properties proxyProperties = new java.util.Properties();
            proxyProperties.setProperty(OracleConnection.PROXY_USER_NAME, proxyUserName);
            if (proxyPassword != null) {
                proxyProperties.setProperty(OracleConnection.PROXY_USER_PASSWORD, proxyPassword);
            }
            oraCon.openProxySession(OracleConnection.PROXYTYPE_USER_NAME, proxyProperties);
            return getUserNameConnectionProxyWrapper(oraCon);
        }
        return oraCon;

    }

    /**
     * Wrap the given Connection with a proxy that delegates every method call to it
     * and resets user name proxy for close calls.
     * @param target the original Connection to wrap
     * @return the wrapped Connection
     */
    protected Connection getUserNameConnectionProxyWrapper(Connection target) {
        return (Connection) Proxy.newProxyInstance(
                ConnectionProxy.class.getClassLoader(),
                new Class[] {ConnectionProxy.class},
                new UserNameConnectionProxyInvocationHandler(target));
    }


    /**
     * Invocation handler that intercepts close calls on JDBC Connections and resets the user name proxy.
     */
    private static class UserNameConnectionProxyInvocationHandler implements InvocationHandler {

        private final Connection target;

        public UserNameConnectionProxyInvocationHandler(Connection target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Invocation on ConnectionProxy interface coming in...

            if (method.getName().equals("getTargetConnection")) {
                // Handle getTargetConnection method: return underlying Connection.
                return this.target;
            }
            else if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0] ? Boolean.TRUE : Boolean.FALSE);
            }
            else if (method.getName().equals("hashCode")) {
                // Use hashCode of Connection proxy.
                return new Integer(hashCode());
            }
            else if (method.getName().equals("close")) {
                // Handle close method: cose proxy if its an Oracle Connection.
                if (target instanceof OracleConnection) {
                    ((OracleConnection)target).close(OracleConnection.PROXY_SESSION);
                }
                target.close();
                return null;
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
