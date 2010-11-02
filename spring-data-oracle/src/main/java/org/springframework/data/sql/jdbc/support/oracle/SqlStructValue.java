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
 * in.put("myarray", new SqlArrayValue(actor);
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
public class SqlStructValue extends AbstractSqlTypeValue {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private Object source;

    /** The object that will do the mapping **/
    private StructMapper mapper;

    /**
     * Constructor that takes one parameter with the array of values passed in to the stored
     * procedure.
     * @param source the Object containing the values to be mapped to the STRUCT.
     */
    public SqlStructValue(Object source) {
        this.source = source;
        this.mapper = new BeanPropertyStructMapper(source.getClass());
    }
    

    /**
     * The implementation for this specific type. This method is called internally by the
     * Spring Framework during the out parameter processing and it's not accessed by application
     * code directly.
     * @see org.springframework.jdbc.core.support.AbstractSqlTypeValue
     */
    protected Object createTypeValue(Connection conn, int sqlType, String typeName) throws SQLException {
        Struct struct = mapper.toStruct(this.source, conn, typeName);
        return struct;
    }

}
