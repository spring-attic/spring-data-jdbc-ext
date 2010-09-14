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

package org.springframework.datastore.sql.jdbc.jms.listener.oracle;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.springframework.datastore.sql.jdbc.jms.support.oracle.OraDataFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import oracle.jms.AQjmsSession;

/**
 * MessageListenerContainer that handles ADT payloads by providing a custom implementation
 * of the <code>ORADataFactory</code> interface.  This factory is required to integrate with
 * the AQ JMS API.
 * 
 * @author Thomas Risberg
 * @since 1.0
 */
public class AdtMessageListenerContainer extends DefaultMessageListenerContainer {
	
	protected MessageConsumer createConsumer(Session session, Destination destination) throws JMSException {
		return ((AQjmsSession) session).createConsumer(destination, null, new OraDataFactory(), null, false);
	}

	protected void doShutdown() throws JMSException {
		super.doShutdown();
		logger.info("Shut down complete."); 
	}

}
