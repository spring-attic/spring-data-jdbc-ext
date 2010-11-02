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

package org.springframework.data.sql.jms.support.oracle;

import oracle.sql.Datum;

import java.sql.SQLException;
import java.sql.Connection;

/**
 * Interface that defines mapping of an advanced data type to and from an object
 *
 * @author Thomas Risberg
 * @since 1.0
 * @throws java.sql.SQLException
 */
public interface DatumMapper {

    /**
     * Create a <code>Datum</code> object based on the passed in object using the connection if necessary.
     * 
     * @param object the object containing tha data to be used
     * @param conn the database <code>Connection</code> that can be used to create database specific instances
     * @return the created Datum
     * @throws SQLException
     */
    public Datum toDatum(Object object, Connection conn) throws SQLException;

    /**
     * Extract the <code>Datum</code> content and return it in a domain class instance.
     * 
     * @param datum the <code>Datum</code> containing the data
     * @return the instance of the domain class populated with extracted data from the <code>Datum</code>
     * @throws SQLException
     */
    public Object fromDatum(Datum datum) throws SQLException;

}