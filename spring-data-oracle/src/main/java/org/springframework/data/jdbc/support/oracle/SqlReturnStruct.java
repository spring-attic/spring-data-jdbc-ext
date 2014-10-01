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

import oracle.sql.STRUCT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.SqlReturnType;

import java.sql.CallableStatement;
import java.sql.SQLException;

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
 */
public class SqlReturnStruct implements SqlReturnType {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    /** The object that will do the mapping **/
    private StructMapper<?> mapper;

    
    /**
     * Constructor that takes one parameter with the class that the retrieved data should be
     * mapped into.
     * @param targetClass JavaBean class that STRUCT attributes will be mapped into
     */
    public SqlReturnStruct(Class<?> targetClass) {
        this.mapper = BeanPropertyStructMapper.newInstance(targetClass);
    }

	/**
	 * Constructor that takes one parameter with the {@link StructMapper} to be used.
	 * @param mapper the mapper
	*/
    public SqlReturnStruct(StructMapper<?> mapper) {
        this.mapper = mapper;
    }

    /**
     * The implementation for this specific type.  This method is called internally by the
     * Spring Framework during the out parameter processing and it's not accessed by appplication
     * code directly.
     */
    public Object getTypeValue(CallableStatement cs, int paramIndex, int sqlType, String typeName)
            throws SQLException {
        STRUCT struct = (STRUCT)cs.getObject(paramIndex);
        if (struct == null) {
            return null;
        }
        Object result = mapper.fromStruct(struct);
        return result;
    }

}
