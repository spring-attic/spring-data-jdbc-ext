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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

/**
 * Interface defining signatures needed for pluggable implementations of STRUCT mappers.
 * Implementations must handle the mapping from STRUCT attributes to and from the target class.
 *
 * @author Thomas Risberg
 * @author Marc Teufel
 * @since 1.0
 */
public interface StructMapper<T> {

    /**
     * Create a struct of the defined type and populate it with values from the passed in source object.
     * @param source instance containing the values to map
     * @param conn database connection to be used to create the Struct
     * @param typeName name of the Struct type
     * @return the new Struct
     * @throws SQLException
     */
    Struct toStruct(T source, Connection conn, String typeName) throws SQLException;

    /**
     * Map attributes from the passed in Struct to the desired object type.
     * @param struct the Struct containing attribute values to be used
     * @return new instance of the target class populated with attribute values
     * @throws SQLException
     */
    T fromStruct(Struct struct) throws SQLException;
}
