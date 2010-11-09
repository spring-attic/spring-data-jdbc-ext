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

import oracle.jdbc.OracleTypes;
import oracle.xdb.XMLType;

import org.springframework.jdbc.support.xml.SqlXmlValue;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.MarshallingFailureException;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementation of the SqlXmlValue interface, for convenient
 * creation of type values that are provided As an XML Document.
 *
 * <p>A usage example from a StoredProcedure:
 *
 * <pre class="code">proc.declareParameter(new SqlParameter("myXml", OracleTypes.OPAQUE, "SYS.XMLTYPE"));
 * ...
 *
 * Map in = new HashMap();
 * in.put("myXml", new OracleXmlTypeMarshallingValue(object, marshaller);
 * Map out = proc.execute(in);
 * </pre>
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.jdbc.support.xml.SqlXmlValue
 * @see org.springframework.jdbc.support.SqlValue
 */
public class OracleXmlTypeMarshallingValue implements SqlXmlValue {

    private Object value;

    private Marshaller marshaller;

    private XMLType xmlValue;

    /**
     * Constructor that takes a parameter with the Object value and another with
     * the <code>Marshaller</code> to be used.
     * @param value the <code>Object</code> containing the object to be marshalled.
     */
    public OracleXmlTypeMarshallingValue(Object value, Marshaller marshaller) {
        this.value = value;
        this.marshaller = marshaller;
    }


    /**
     * The implementation for this specific type. This method is called internally by the
     * Spring Framework during the out parameter processing and it's not accessed by appplication
     * code directly.
     * @see org.springframework.jdbc.support.xml.SqlXmlValue
     */
    public void setValue(PreparedStatement ps, int paramIndex) throws SQLException {
        Connection conn = ps.getConnection();
        StreamResult result = new StreamResult();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        result.setOutputStream(os);
        String stringValue = null;
        try {
            marshaller.marshal(value, result);
            stringValue = os.toString();
        }
        catch (IOException e) {
            throw new MarshallingFailureException("Error marshalling xml data from object " + value.getClass().getName() + ": ");
        }
        finally {
            try {
                os.close();
            } catch (IOException ignore) {}
        }
        xmlValue = new XMLType(conn, stringValue);
        ps.setObject(paramIndex, xmlValue, OracleTypes.OPAQUE);
    }

    /**
     * Close the XMLType
     */
    public void cleanup() {
        if (xmlValue != null) {
            xmlValue.close();
        }
    }

}
