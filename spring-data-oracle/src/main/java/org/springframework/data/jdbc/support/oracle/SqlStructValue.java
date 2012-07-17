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

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.support.AbstractSqlTypeValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

/**
 * Implementation of the SqlTypeValue interface, for convenient
 * creation of type values that to be provided as a STRUCT.
 *
 * <p>A usage example from a StoredProcedure:
 *
 * <pre class="code">proc.declareParameter(new SqlParameter("actor", OracleTypes.STRUCT, "ACTOR_TYPE"));
 * ...
 *
 * Map in = new HashMap();
 * in.put("myarray", new SqlArrayValue&lt;Actor&gt;(actor);
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
public class SqlStructValue<T> extends AbstractSqlTypeValue {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private T source;

    /** The object that will do the mapping **/
    private StructMapper mapper;

	private String defaultTypeName;


	/**
	 * Constructor that takes one parameter with the Object value passed in to the stored
	 * procedure.
	 *
	 * @param source the Object containing the values to be mapped to the STRUCT.
	 */
	public SqlStructValue(T source) {
		this.source = source;
		this.mapper = new BeanPropertyStructMapper(source.getClass());
	}

	/**
	 * Constructor that takes two parameters, the Object value passed in to the
	 * statement and the default type name to be used when the context where this class is used
	 * is not aware of the type name to use.
	 *
	 * @param source the Object containing the values to be mapped to the STRUCT.
	 * @param defaultTypeName the default type name.
	 */
	public SqlStructValue(T source, String defaultTypeName) {
		this.source = source;
		this.defaultTypeName = defaultTypeName;
	}

	/**
     * Constructor that takes two parameters, the Object value passed in to the
	 * statement and the {@link StructMapper} to be used
     * @param source the Object containing the values to be mapped to the STRUCT.
	 * @param mapper the mapper
     */
    public SqlStructValue(T source, StructMapper mapper) {
        this.source = source;
        this.mapper = mapper;
    }

	/**
     * Constructor that takes three parameters, the Object value passed in to the
	 * statement, the {@link StructMapper} to be used and the default type name
	 * to be used when the context where this class is used is not aware of the type
	 * name to use.

     * @param source the Object containing the values to be mapped to the STRUCT.
	 * @param mapper the mapper
	 * @param defaultTypeName the default type name.
     */
    public SqlStructValue(T source, StructMapper mapper, String defaultTypeName) {
        this.source = source;
        this.mapper = mapper;
		this.defaultTypeName = defaultTypeName;
    }


	/**
     * The implementation for this specific type. This method is called internally by the
     * Spring Framework during the out parameter processing and it's not accessed by application
     * code directly.
     * @see org.springframework.jdbc.core.support.AbstractSqlTypeValue
     */
    protected Object createTypeValue(Connection conn, int sqlType, String typeName) throws SQLException {
		if (typeName == null && defaultTypeName == null) {
			throw new InvalidDataAccessApiUsageException(
					"The typeName is null in this context. Consider setting the defaultTypeName.");
		}
        Struct struct = mapper.toStruct(this.source, conn, typeName != null ? typeName : defaultTypeName);
        return struct;
    }

}
