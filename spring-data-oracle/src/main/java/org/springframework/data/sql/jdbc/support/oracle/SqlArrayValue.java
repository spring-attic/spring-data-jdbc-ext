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

package org.springframework.data.sql.jdbc.support.oracle;

import org.springframework.jdbc.core.support.AbstractSqlTypeValue;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.sql.ArrayDescriptor;
import oracle.sql.ARRAY;

/**
 * Implementation of the SqlTypeValue interface, for convenient
 * creation of type values that are provided As an ARRAY.
 *
 * <p>A usage example from a StoredProcedure:
 *
 * <pre class="code">proc.declareParameter(new SqlParameter("myarray", Types.ARRAY, "NUMBERS"));
 * ...
 *
 * Map in = new HashMap();
 * in.put("myarray", new SqlArrayValue(objectArray);
 * Map out = proc.execute(in);
 * </pre>
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.jdbc.core.SqlTypeValue
 * @see org.springframework.jdbc.core.support.AbstractSqlTypeValue
 * @see org.springframework.jdbc.core.simple.SimpleJdbcCall
 * @see org.springframework.jdbc.object.StoredProcedure
 */
public class SqlArrayValue extends AbstractSqlTypeValue {

    private Object[] values;


    /**
     * Constructor that takes one parameter with the array of values passed in to the stored
     * procedure.
     * @param values the array containing the values.
     */
    public SqlArrayValue(Object[] values) {
        this.values = values;
    }
    

    /**
     * The implementation for this specific type. This method is called internally by the
     * Spring Framework during the out parameter processing and it's not accessed by appplication
     * code directly.
     * @see org.springframework.jdbc.core.support.AbstractSqlTypeValue
     */
    protected Object createTypeValue(Connection conn, int sqlType, String typeName)
            throws SQLException {
        ArrayDescriptor arrayDescriptor = new ArrayDescriptor(typeName, conn);
        ARRAY array =
                new ARRAY(arrayDescriptor, conn, values);
        return array;
    }
}
