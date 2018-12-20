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

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import oracle.xdb.XMLType;

import org.springframework.jdbc.support.xml.SqlXmlValue;
import org.w3c.dom.Document;

import java.io.InputStream;
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
 * in.put("myXml", new OracleXmlTypeValue(xmlDocument);
 * Map out = proc.execute(in);
 * </pre>
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.jdbc.support.xml.SqlXmlValue
 * @see org.springframework.jdbc.support.SqlValue
 * @see org.springframework.jdbc.core.simple.SimpleJdbcCall
 * @see org.springframework.jdbc.object.StoredProcedure
 */
public class OracleXmlTypeValue implements SqlXmlValue {

    private static final int STRING = 0;
    private static final int STREAM = 1;
    private static final int DOCUMENT = 2;

    private int inputType = STRING;

    private String value;

    private Document doc;

    private InputStream stream;

    private XMLType xmlValue;


    /**
     * Constructor that takes one parameter with the XML String value passed in to be used.
     * @param value the <code>String</code> containing the XML to use.
     */
    public OracleXmlTypeValue(String value) {
        this.value = value;
        inputType = STRING;
    }

    /**
     * Constructor that takes one parameter with the XML InputStream passed in to be used.
     * @param stream the <code>InputStream</code> containing the XML to use.
     */
    public OracleXmlTypeValue(InputStream stream) {
        this.stream = stream;
        inputType = STREAM;
    }

    /**
     * Constructor that takes one parameter with the XML Document value passed in to be used.
     * @param doc the <code>Document</code> containing the values.
     */
    public OracleXmlTypeValue(Document doc) {
        this.doc = doc;
        inputType = DOCUMENT;
    }


    /**
     * The implementation for this specific type. This method is called internally by the
     * Spring Framework during the out parameter processing and it's not accessed by appplication
     * code directly.
     * @see org.springframework.jdbc.support.xml.SqlXmlValue
     */
    public void setValue(PreparedStatement ps, int paramIndex) throws SQLException {
        Connection conn = ps.getConnection();

        if (!(conn instanceof OracleConnection)) {
            if (conn.isWrapperFor(OracleConnection.class)) {
                conn = conn.unwrap(OracleConnection.class);
            }
        }

        switch (inputType) {
            case STRING:
                xmlValue = XMLType.createXML(conn, value);
                break;
            case STREAM:
                xmlValue = XMLType.createXML(conn, stream);
                break;
            case DOCUMENT:
                xmlValue = XMLType.createXML(conn, doc);
                break;
        }
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
