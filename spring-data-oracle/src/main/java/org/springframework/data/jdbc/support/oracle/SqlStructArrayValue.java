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

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import org.springframework.jdbc.core.support.AbstractSqlTypeValue;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implementation of the SqlTypeValue interface, for convenient
 * creation of provided type values as an ARRAY of STRUCTS.
 *
 * <p>A usage example from a StoredProcedure:
 *
 * <pre class="code">proc.declareParameter(new SqlParameter("myarray", Types.ARRAY, "ACTOR_TYPE_ARRAY"));
 * ...
 *
 * Map in = new HashMap();
 * in.put("myarray", new SqlStructArrayValue&lt;Number&gt;(objectArray, actorMapper);
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
public class SqlStructArrayValue<T> extends AbstractSqlTypeValue {

    private T[] values;

	/** The object that will do the mapping **/
	private StructMapper<T> mapper;

	/** The type name of the STRUCT **/
	private String structTypeName;

    /**
     * Constructor that takes a parameter with the array of values passed in to the stored
     * procedure, a parameter with the {@link StructMapper} to be used plus the type name of the STRUCT
	 * that the array will contain.
     * @param values the array containing the values
     * @param mapper the mapper to create the STRUCT values
     * @param structTypeName the type name of the STRUCT.
     */
    public SqlStructArrayValue(T[] values, StructMapper<T> mapper, String structTypeName) {
        this.values = values;
		this.mapper = mapper;
		this.structTypeName = structTypeName;
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
		STRUCT[] structValues = new STRUCT[values.length];
		for (int i = 0; i < values.length; i++) {
			structValues[i] = mapper.toStruct(values[i], conn, structTypeName);
		}
        ARRAY array = new ARRAY(arrayDescriptor, conn, structValues);
        return array;
    }
}
