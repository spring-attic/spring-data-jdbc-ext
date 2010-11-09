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

import oracle.xdb.XMLType;

import java.sql.Connection;

import org.springframework.dao.DataRetrievalFailureException;

/**
 * The interface needed to be implementated by any classes that will perform the low level handling
 * of <code>XMLType</code> objects - both creation and retrieval of the content.
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public interface XmlTypeHandler {

    /**
     * Create an <code>XMLType</code> for the supplied object using the provided connection
     *
     * @param object The object contining the data to be represented in the XMLType
     * @param conn the <code>Connection</code> to use
     * @return the XMLType populated with data from the object passed in
     * @throws DataRetrievalFailureException
     */
    XMLType createXmlType(Object object, Connection conn) throws DataRetrievalFailureException;

    /**
     * Extract the data from an XMLType
     *
     * @param data the XMLType containing the XML data
     * @return the object containing the data extracted from the XML
     * @throws DataRetrievalFailureException
     */
    Object getXmlContent(XMLType data) throws DataRetrievalFailureException;
    
}
