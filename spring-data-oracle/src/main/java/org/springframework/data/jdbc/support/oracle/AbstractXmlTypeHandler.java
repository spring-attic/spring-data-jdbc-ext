/*
 * Copyright 2008-2010 the original author or authors.
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

package org.springframework.data.jdbc.support.oracle;

import oracle.xdb.XMLType;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.InputStream;

import org.springframework.util.Assert;
import org.springframework.dao.DataRetrievalFailureException;
import org.w3c.dom.Document;

/**
 * Abstract class handling the creation of an XMLType  from
 * a String, Stream or Document representation.  Delegates to
 * subclasses for extraction of the XML content from the XMLType.
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public abstract class AbstractXmlTypeHandler implements XmlTypeHandler {

    
    public XMLType createXmlType(Object object, Connection conn) throws DataRetrievalFailureException {
        Assert.notNull(object, "XML input source must not be null");
        if (!(object instanceof String)) {
            throw new IllegalArgumentException("XML input source must be of type 'java.lang.String' - received: " + object.getClass().getName());
        }
        XMLType xml;
        try {
            if ((object instanceof String)) {
                xml = XMLType.createXML(conn, (String)object);
            }
            else if ((object instanceof InputStream)) {
                xml = XMLType.createXML(conn, (InputStream)object);
            }
            else if ((object instanceof Document)) {
                xml = XMLType.createXML(conn, (Document)object);
            }
            else {
                throw new IllegalArgumentException("The provided value is not a supported type: " + object.getClass().getName());
            }
        } catch (SQLException sqle) {
            throw new DataRetrievalFailureException("Error while creating XMLType", sqle);
        }

        return xml;
    }

    public Object getXmlContent(XMLType data) throws DataRetrievalFailureException {
        Assert.notNull(data, "XMLType input must not be null");
        Object result = null;
        try {
            result = extractXmlContent(data);
        } catch (SQLException sqle) {
            throw new DataRetrievalFailureException("Error while extracting XML content", sqle);
        }
        return result;
    }

    protected abstract Object extractXmlContent(XMLType data) throws SQLException;

}
