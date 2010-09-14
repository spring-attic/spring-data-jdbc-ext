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

package org.springframework.datastore.sql.jdbc.support.oracle;

import org.springframework.jdbc.core.SqlReturnType;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Implementation of the SqlReturnType interface, for convenient
 * access of object data returned from stored procedure.  The target class
 * specified in the constructor must implement java.sql.SQLData.
 *
 * <p>A usage example from a StoredProcedure:
 *
 * <pre class="code">proc.declareParameter(new SqlOutParameter("return", OracleTypes.STRUCT, "ACTOR_TYPE",
 *         new SqlReturnArray()));
 * </pre>
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.jdbc.core.SqlReturnType
 * @see org.springframework.jdbc.core.simple.SimpleJdbcCall
 * @see org.springframework.jdbc.object.StoredProcedure
 * @see java.sql.SQLData
 */
public class SqlReturnSqlData implements SqlReturnType {

    private Class<?> targetClass;


    /**
     * Constructor that takes one parameter with the class that the retrieved data should be
     * mapped into.
     * @param targetClass class that implements java.sql.SQLData
     */
    public SqlReturnSqlData(Class<?> targetClass) {
        this.targetClass = targetClass;
    }
    

    /**
     * The implementation for this specific type.  This method is called internally by the
     * Spring Framework during the out parameter processing and it's not accessed by appplication
     * code directly.
     */
    public Object getTypeValue(CallableStatement cs, int paramIndex, int sqlType, String typeName)
            throws SQLException {
        Connection con = cs.getConnection();
        Map<String, Class<?>> typeMap = con.getTypeMap();
        typeMap.put(typeName, targetClass);
        Object o = cs.getObject(paramIndex);
        return o;
    }
}
