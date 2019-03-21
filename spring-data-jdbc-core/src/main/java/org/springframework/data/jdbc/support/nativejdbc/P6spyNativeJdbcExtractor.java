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

package org.springframework.data.jdbc.support.nativejdbc;

import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractorAdapter;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;
import org.springframework.util.ReflectionUtils;

import java.sql.*;
import java.lang.reflect.Method;

/**
 * Implementation of the {@link org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor} interface.
 * 
 * @author Thomas Risberg
 * @since 1.0
 */
public class P6spyNativeJdbcExtractor extends NativeJdbcExtractorAdapter {

    private static final String JDBC_WRAPPER_NAME = "com.p6spy.engine.spy.P6Connection";


    private final Class<?> jdbcWrapperClass;

    private final Method getVendorConnectionMethod;

    private NativeJdbcExtractor nextNativeJdbcExtractor = null;

    /**
     * This constructor retrieves the P6Spy JDBC wrapper class,
     * so we can get the underlying native connection using reflection.
     */
    public P6spyNativeJdbcExtractor() {
        try {
            this.jdbcWrapperClass = getClass().getClassLoader().loadClass(JDBC_WRAPPER_NAME);
            this.getVendorConnectionMethod = this.jdbcWrapperClass.getMethod("getJDBC", (Class[]) null);
        }
        catch (Exception ex) {
            throw new IllegalStateException(
                    "Could not initialize P6spyNativeJdbcExtractor because P6Spy API classes are not available: " + ex);
        }
    }


    public void setNextNativeJdbcExtractor(NativeJdbcExtractor nextNativeJdbcExtractor) {
        this.nextNativeJdbcExtractor = nextNativeJdbcExtractor;
    }

    /**
     * Return <code>true</code>, as P6Spy returns wrapped Statements.
     */
    public boolean isNativeConnectionNecessaryForNativeStatements() {
        return true;
    }

    /**
     * Return <code>true</code>, as P6Spy returns wrapped PreparedStatements.
     */
    public boolean isNativeConnectionNecessaryForNativePreparedStatements() {
        return true;
    }

    /**
     * Return <code>true</code>, as P6Spy returns wrapped CallableStatements.
     */
    public boolean isNativeConnectionNecessaryForNativeCallableStatements() {
        return true;
    }

    /**
     * Retrieve the Connection via P6Spy's <code>getVendorConnection</code> method.
     */
    protected Connection doGetNativeConnection(Connection con) throws SQLException {
        if (this.jdbcWrapperClass.isAssignableFrom(con.getClass())) {
            Connection unwrappedCon =
                    (Connection) ReflectionUtils.invokeJdbcMethod(this.getVendorConnectionMethod, con);
            if (nextNativeJdbcExtractor != null) {
                return nextNativeJdbcExtractor.getNativeConnection(unwrappedCon);
            }
            return unwrappedCon;
        }
        return con;
    }

}
