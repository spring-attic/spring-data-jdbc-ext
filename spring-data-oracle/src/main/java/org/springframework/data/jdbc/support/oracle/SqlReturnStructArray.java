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
import oracle.sql.STRUCT;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.SqlReturnType;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the SqlReturnType interface, for convenient
 * access of ARRAYs of STUCTs returned from stored procedure.
 *
 * <p>A usage example from a StoredProcedure:
 *
 * <pre class="code">proc.declareParameter(new SqlOutParameter("return", Types.ARRAY, "ACTOR_TYPE_ARRAY",
 *         new SqlReturnStructArray(actorMapper)));
 * </pre>
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.jdbc.core.SqlReturnType
 * @see org.springframework.jdbc.core.simple.SimpleJdbcCall
 * @see org.springframework.jdbc.object.StoredProcedure
 */
public class SqlReturnStructArray<T> implements SqlReturnType {

	/** The object that will do the mapping **/
	private StructMapper<T> mapper;

	/**
	 * Constructor that takes a parameter with the {@link StructMapper} to be used.
	 * @param mapper the mapper
	 */
	public SqlReturnStructArray(StructMapper<T> mapper) {
		this.mapper = mapper;
	}


	/**
     * The implementation for this specific type.  This method is called internally by the
     * Spring Framework during the out parameter processing and it's not accessed by application
     * code directly.
     */
	@SuppressWarnings("unchecked")
    public Object getTypeValue(CallableStatement cs, int i, int sqlType, String typeName)
                                        throws SQLException {
        ARRAY array = (ARRAY) cs.getObject(i);
        Object[] structValues = (Object[]) array.getArray();
		List<T> values = new ArrayList<T>();
		for (int x = 0; x < structValues.length; x++) {
			Object struct = structValues[x];
			if (struct instanceof STRUCT) {
				values.add(mapper.fromStruct((STRUCT) struct));
			}
			else {
				throw new InvalidDataAccessApiUsageException("Expected STRUCT but got " + struct.getClass().getName());
			}
		}
        return values.toArray();
    }
}
