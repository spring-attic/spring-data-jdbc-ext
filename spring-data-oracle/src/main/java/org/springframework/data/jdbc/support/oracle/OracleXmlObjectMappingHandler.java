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
import org.springframework.jdbc.support.xml.SqlXmlValue;
import org.springframework.jdbc.support.xml.SqlXmlObjectMappingHandler;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.UnmarshallingFailureException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of the SqlXmlObjectMappingHandler interface.  Provides database specific
 * implementations for handling XML object mapping to fields in a database.
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.jdbc.support.xml.SqlXmlObjectMappingHandler
 * @see org.springframework.jdbc.support.xml.SqlXmlHandler
 */
public class OracleXmlObjectMappingHandler extends OracleXmlHandler implements SqlXmlObjectMappingHandler {

    private Marshaller marshaller;
    
    private Unmarshaller unmarshaller;


    public Marshaller getMarshaller() {
        return marshaller;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public Object getXmlAsObject(ResultSet rs, String columnName) throws SQLException {
        String xml = this.getXmlAsString(rs, columnName);
        Object o;
        try {
            o = doUnmarshall(xml);
        } catch (IOException e) {
            throw new UnmarshallingFailureException("Error unmarshalling xml data from '" + columnName + "'  column: ", e);
        }
        return o;
    }

    public Object getXmlAsObject(ResultSet rs, int columnIndex) throws SQLException {
        String xml = this.getXmlAsString(rs, columnIndex);
        Object o;
        try {
            o = doUnmarshall(xml);
        } catch (IOException e) {
            throw new UnmarshallingFailureException("Error unmarshalling xml data from column " + columnIndex + ": ", e);
        }
        return o;
    }

    public SqlXmlValue newMarshallingSqlXmlValue(Object value) {
        if (this.marshaller == null) {
            throw new InvalidDataAccessApiUsageException("Marshalling requires that a Marshaller is configured");
        }
        return new OracleXmlTypeMarshallingValue(value, this.marshaller);
    }


    private Object doUnmarshall(String xml) throws IOException {
        if (this.unmarshaller == null) {
            throw new InvalidDataAccessApiUsageException("Unmarshalling requires that an Unmarshaller is configured");
        }
        return this.unmarshaller.unmarshal(new StreamSource(new StringReader(xml)));
    }

}
