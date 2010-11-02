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

import oracle.xdb.XMLType;
import org.springframework.jdbc.support.xml.*;
import org.w3c.dom.Document;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of the SqlXmlHandler interface.  Provides database specific
 * implementations for handling storing and retrieving XML documents to and from
 * fields in a database.
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.jdbc.support.xml.SqlXmlHandler
 */
public class OracleXmlHandler implements SqlXmlHandler {


    public String getXmlAsString(ResultSet rs, String columnName) throws SQLException {
        XMLType oxml = (XMLType)rs.getObject(columnName);
        String value = oxml.getStringVal();
        oxml.close();
        return value;
    }

    public String getXmlAsString(ResultSet rs, int columnIndex) throws SQLException {
        XMLType oxml = (XMLType)rs.getObject(columnIndex);
        String value = oxml.getStringVal();
        oxml.close();
        return value;
    }

    public InputStream getXmlAsBinaryStream(final ResultSet rs, final String columnName) throws SQLException {
        return doGetXmlAsBinaryStream(
                new XmlTypeProvider() {
                    public XMLType getXmlType() throws SQLException {
                        return (XMLType)rs.getObject(columnName);
                    }
                }, rs.getStatement().getConnection());
    }

    public InputStream getXmlAsBinaryStream(final ResultSet rs, final int columnIndex) throws SQLException {
        return doGetXmlAsBinaryStream(
                new XmlTypeProvider() {
                    public XMLType getXmlType() throws SQLException {
                        return (XMLType)rs.getObject(columnIndex);
                    }
                }, rs.getStatement().getConnection());
    }

    private InputStream doGetXmlAsBinaryStream(XmlTypeProvider provider, Connection conn) throws SQLException {
        if (XMLType.getConnType(conn) == XMLType.CONNTYPE_THIN) {
            throw new SqlXmlFeatureNotImplementedException("getXmlAsBinaryStream method is not implemented for the \"thin\" driver");
        }
        XMLType oxml = provider.getXmlType();
        InputStream is = oxml.getInputStream();
        oxml.close();
        return is;
    }

    public Reader getXmlAsCharacterStream(ResultSet rs, String columnName) throws SQLException {
        throw new SqlXmlFeatureNotImplementedException("getXmlAsCharacterStream method is not implemented yet");
    }

    public Reader getXmlAsCharacterStream(ResultSet rs, int columnIndex) throws SQLException {
        throw new SqlXmlFeatureNotImplementedException("getXmlAsCharacterStream method is not implemented yet");
    }

    @SuppressWarnings("rawtypes")
	public Source getXmlAsSource(final ResultSet rs, final String columnName, Class sourceClass) throws SQLException {
        return doGetXmlAsSource(
                new XmlTypeProvider() {
                    public XMLType getXmlType() throws SQLException {
                        return (XMLType)rs.getObject(columnName);
                    }
                },
                rs.getStatement().getConnection(),
                sourceClass);
    }

    @SuppressWarnings("rawtypes")
    public Source getXmlAsSource(final ResultSet rs, final int columnIndex, Class sourceClass) throws SQLException {
        return doGetXmlAsSource(
                new XmlTypeProvider() {
                    public XMLType getXmlType() throws SQLException {
                        return (XMLType)rs.getObject(columnIndex);
                    }
                }, 
                rs.getStatement().getConnection(),
                sourceClass);
    }

    @SuppressWarnings("rawtypes")
	private Source doGetXmlAsSource(XmlTypeProvider provider, Connection conn, Class sourceClass) throws SQLException, SqlXmlFeatureNotImplementedException {
        int connType = XMLType.getConnType(conn);
        Class<?> sourceClassToUse;
        if (sourceClass == null) {
            if (connType == XMLType.CONNTYPE_THIN) {
                sourceClassToUse = DOMSource.class;
            }
            else {
                sourceClassToUse = StreamSource.class;
            }
        }
        else {
            if (connType == XMLType.CONNTYPE_THIN) {
                if (!sourceClass.equals(DOMSource.class)) {
                    throw new SqlXmlFeatureNotImplementedException("getXmlAsSource method does not support a source class of " + sourceClass.getName());
                }
            }
            else {
                if (!(sourceClass.equals(DOMSource.class) || sourceClass.equals(StreamSource.class))) {
                    throw new SqlXmlFeatureNotImplementedException("getXmlAsSource method does not support a source class of " + sourceClass.getName());
                }
            }
            sourceClassToUse = sourceClass;
        }
        Source source = null;
        XMLType oxml = provider.getXmlType();
        if (sourceClassToUse.equals(DOMSource.class)) {
            Document doc;
            if (connType == XMLType.CONNTYPE_THIN) {
                doc = oxml.getDocument();
            }
            else {
                // convert the XDB Lazy DOM to a regular parser thin style XMLDocument
                XMLType xt = XMLType.createXML(oxml, oracle.xml.parser.v2.XMLDocument.THIN);
                doc = xt.getDocument();
            }
            source = new DOMSource(doc);
        }
        else if (sourceClassToUse.equals(StreamSource.class)) {
            InputStream is = oxml.getInputStream();
            source = new StreamSource(is);
        }
        return source;
    }

    public SqlXmlValue newSqlXmlValue(String value) {
        return new OracleXmlTypeValue(value);
    }

    public SqlXmlValue newSqlXmlValue(XmlBinaryStreamProvider provider) {
        throw new SqlXmlFeatureNotImplementedException("newSqlXmlValue method is not implemented yet");
    }

    public SqlXmlValue newSqlXmlValue(XmlCharacterStreamProvider provider) {
        throw new SqlXmlFeatureNotImplementedException("newSqlXmlValue method is not implemented yet");
    }

    @SuppressWarnings("rawtypes")
	public SqlXmlValue newSqlXmlValue(Class resultClass, XmlResultProvider xmlResultProvider) {
        throw new SqlXmlFeatureNotImplementedException("newSqlXmlValue method is not implemented yet");
    }

    public SqlXmlValue newSqlXmlValue(Document document) {
        return new OracleXmlTypeValue(document);
    }

    private interface XmlTypeProvider {
        public abstract XMLType getXmlType() throws SQLException;
    }

}
