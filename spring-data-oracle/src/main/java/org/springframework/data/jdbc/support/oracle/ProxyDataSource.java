/*
 * Copyright 2008-2015 the original author or authors.
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

package org.springframework.data.jdbc.support.oracle;

import oracle.jdbc.pool.OracleDataSource;

import org.springframework.data.jdbc.support.ConnectionContextProvider;
import org.springframework.jdbc.datasource.SmartDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * A SmartDataSource wrapper that will delegate to a ProxyConnectionPreparer to prepare a proxy connection.
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.jdbc.datasource.SmartDataSource
 */
public class ProxyDataSource implements SmartDataSource {

    OracleDataSource oracleDataSurce;

    ProxyConnectionPreparer connectioPreparer;


    public ProxyDataSource(OracleDataSource oracleDataSurce, ConnectionContextProvider contextProvider) {
        if (contextProvider == null) {
            throw new IllegalArgumentException("You must supply a ConnectionContextProvider that provides the user name");    
        }
        this.oracleDataSurce = oracleDataSurce;
        connectioPreparer = new ProxyConnectionPreparer();
        connectioPreparer.setContextProvider(contextProvider);
    }


    public boolean shouldClose(Connection connection) {
        return true;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = oracleDataSurce.getConnection();
        return connectioPreparer.prepare(conn);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = oracleDataSurce.getConnection(username, password);
        return connectioPreparer.prepare(conn);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return oracleDataSurce.getLogWriter();
    }

    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        oracleDataSurce.setLogWriter(printWriter);
    }

    public void setLoginTimeout(int i) throws SQLException {
        oracleDataSurce.setLoginTimeout(i);
    }

    public int getLoginTimeout() throws SQLException {
        return oracleDataSurce.getLoginTimeout();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return oracleDataSurce.getParentLogger();
    }

    //---------------------------------------------------------------------
    // Implementation of JDBC 4.0's Wrapper interface
    //---------------------------------------------------------------------

    public <T> T  unwrap(Class<T> iface) throws SQLException {
        return ((DataSource)oracleDataSurce).unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return ((DataSource)oracleDataSurce).isWrapperFor(iface);
    }

}
