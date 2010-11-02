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

import oracle.jms.AQjmsAdtMessage;
import oracle.jms.AQjmsSession;
import oracle.jms.AdtMessage;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.STRUCT;

import org.springframework.data.sql.jms.support.oracle.DatumMapper;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A <code>MessageConverter</code> that handles mapping between an ADT payload and a domain object.  Delegates
 * the mapping to a <code>DatumMapper</code> implementation.
 * 
 * @author Thomas Risberg
 * @since 1.0
 */
public class MappingAdtMessageConverter implements MessageConverter {

    /* The datum mapper that will handle the mapping */
    private DatumMapper mapper;

    /**
     * Constructor used to construct the <code>MessageConverter</code> and configure it with
     * a <code>DatumMapper</code> implementation to be used.
     * 
     * @param mapper the <code>DatumMapper</code> implementation to be used
     */
    public MappingAdtMessageConverter(DatumMapper mapper) {
        this.mapper = mapper;
    }

    protected DatumMapper getMapper() {
        return mapper;
    }

    public Message toMessage(final Object object, Session session) throws JMSException, MessageConversionException {
        AdtMessage message = ((AQjmsSession)session).createAdtMessage();
        message.setAdtPayload(new ORAData() {
            public Datum toDatum(Connection conn) throws SQLException {
                return getMapper().toDatum(object, conn);
            }
        });
        return message;
    }

    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        AQjmsAdtMessage aqMessage = (AQjmsAdtMessage) message;
        Object result = null;
        ORAData data = (ORAData) aqMessage.getAdtPayload();
		try {
			STRUCT struct = (STRUCT) data.toDatum(null);
			result = getMapper().fromDatum(struct);
		} catch (SQLException sqle) {
			throw new MessageConversionException("Error while accessing ADT payload", sqle);
		}
		return result;
    }
    
}
