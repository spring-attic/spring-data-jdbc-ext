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

package org.springframework.data.sql.jms.support.converter.oracle;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.sql.jdbc.support.oracle.XmlTypeHandler;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.JMSException;

import oracle.jms.AdtMessage;
import oracle.jms.AQjmsSession;
import oracle.jms.AQjmsAdtMessage;
import oracle.xdb.XMLType;

import java.sql.Connection;

/**
 * @author Thomas Risberg
 * @since 1.0
 */
public class XmlMessageConverter implements MessageConverter {

    /* The <code>XmlTypeHandler</code> that will handle the payload */
    private XmlTypeHandler handler;

    /**
     * Constructor used to construct the <code>MessageConverter</code> and configure it with
     * a <code>XmlTypeHandler</code> implementation to be used.
     *
     * @param handler <code>XmlTypeHandler</code> that will do the low level handling of the <code>XMLType</code>
     */
    public XmlMessageConverter(XmlTypeHandler handler) {
        this.handler = handler;
    }

    protected XmlTypeHandler getHandler() {
        return handler;
    }

    public Message toMessage(final Object object, Session session) throws JMSException, MessageConversionException {
        AdtMessage message = ((AQjmsSession)session).createAdtMessage();
        Connection conn = ((AQjmsSession)session).getDBConnection();
        XMLType xml = null;
        try {
            xml = handler.createXmlType(object, conn);
        } catch (DataRetrievalFailureException e) {
            throw new MessageConversionException("Error while creating XML message", e);
        }
        message.setAdtPayload(xml);
        return message;
    }

    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        AQjmsAdtMessage aqMessage = (AQjmsAdtMessage) message;
        Object payload = aqMessage.getAdtPayload();
        if (payload instanceof XMLType) {
            try {
                return handler.getXmlContent((XMLType) payload);
            } catch (DataRetrievalFailureException e) {
                throw new MessageConversionException("Error while processing XML payload", e);
            }
        }
        throw new MessageConversionException("Payload is not an XMLType: " + payload.getClass().getName());
    }

}
