/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.jdbc.jms.support.oracle;

import oracle.sql.Datum;
import oracle.sql.STRUCT;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jdbc.support.oracle.BeanPropertyStructMapper;
import org.springframework.data.jdbc.support.oracle.StructMapper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implementation of the DatumMapper interface.  Provides mapping from an ADT STRUCT to a
 * bean based on attribute to property name mapping.
 *
 * @author Thomas Risberg
 * @author Marc Teufel
 * @since 1.0
 */
public class StructDatumMapper<T> implements DatumMapper<T> {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    /** The class we are mapping to */
    protected String typeName;

    /** The object that will do the mapping **/
    private StructMapper<T> mapper;


    /**
     * Constructor that takes the type name and a StructMapper implemenatation that will
     * handle the mapping to and from STRUCT values.
     * 
     * @param typeName name of the database type
     * @param mapper {@link StructMapper} implementation to do the mapping
     */
    public StructDatumMapper(String typeName, StructMapper<T> mapper) {
        this.typeName = typeName;
        this.mapper = mapper;
    }

    /**
     * Constructor that takes the type name and a parameter with the class that the data should be
     * mapped from/into.  Using this constructor results in the <code>BeanPropertyStructMapper</code>
     * being used for the mapping.
     *  
     * @param typeName name of the database type
     * @param targetClass JavaBean class that STRUCT attributes will be mapped to
     */
    public StructDatumMapper(String typeName, Class<T> targetClass) {
        this.typeName = typeName;
        this.mapper = new BeanPropertyStructMapper<T>(targetClass);
    }


    public Datum toDatum(T source, Connection conn) throws SQLException {
        STRUCT struct = (STRUCT) mapper.toStruct(source, conn, typeName);
        return struct;
    }

    public T fromDatum(Datum datum) throws SQLException {
        STRUCT struct = (STRUCT) datum;
        T result = mapper.fromStruct(struct);
        return result;
    }

}
