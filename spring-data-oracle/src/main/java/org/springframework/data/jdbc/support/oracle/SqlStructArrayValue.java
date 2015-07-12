/*
 * Copyright 2008-2015 the original author or authors.
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

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.support.AbstractSqlTypeValue;

import oracle.jdbc.driver.OracleConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

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
 * @author Marc Teufel
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

	/** The type name of the ARRAY **/
	private String arrayTypeName;

    /**
     * Constructor that takes a parameter with the array of values passed in to the
     * statement, a parameter with the {@link StructMapper} to be used plus the type
	 * name of the STRUCT that the array will contain.
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
     * Constructor that takes a parameter with the array of values passed in to the
     * statement, a parameter with the {@link StructMapper} to be used plus the type
	 * name of the STRUCT that the array will contain.
     * @param values the array containing the values
     * @param mapper the mapper to create the STRUCT values
     * @param structTypeName the type name of the STRUCT.
     * @param arrayTypeName the type name of the ARRAY when this class is used in a context where the
	 * name of the array type is not known.
     */
    public SqlStructArrayValue(T[] values, StructMapper<T> mapper, String structTypeName, String arrayTypeName) {
        this.values = values;
		this.mapper = mapper;
		this.structTypeName = structTypeName;
		this.arrayTypeName = arrayTypeName;
    }


    /**
     * The implementation for this specific type. This method is called internally by the
     * Spring Framework during the out parameter processing and it's not accessed by application
     * code directly.
     * @see org.springframework.jdbc.core.support.AbstractSqlTypeValue
     */
    protected Object createTypeValue(Connection conn, int sqlType, String typeName)
            throws SQLException {
		if (typeName == null && arrayTypeName == null) {
			throw new InvalidDataAccessApiUsageException(
					"The typeName for the array is null in this context. Consider setting the arrayTypeName.");
		}
		Struct[] structValues = new Struct[values.length];
		for (int i = 0; i < values.length; i++) {
			structValues[i] = mapper.toStruct(values[i], conn, structTypeName);
		}
		OracleConnection oracleConn = (OracleConnection) conn;
		return oracleConn.createOracleArray(typeName != null ? typeName : arrayTypeName, structValues);
    }
}
