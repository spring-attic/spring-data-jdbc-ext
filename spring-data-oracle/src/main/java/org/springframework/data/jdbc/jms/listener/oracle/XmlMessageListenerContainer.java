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

package org.springframework.data.jdbc.jms.listener.oracle;

import oracle.jms.AQjmsSession;
import oracle.xdb.XMLType;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * MessageListenerContainer that handles XML payloads by providing <code>XMLType.getORADataFactory()</code>
 * as the <code>ORADataFactory</code> interface.  This factory is required to integrate with the AQ JMS API.
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class XmlMessageListenerContainer extends DefaultMessageListenerContainer {

	protected MessageConsumer createConsumer(Session session, Destination destination) throws JMSException {
		return ((AQjmsSession) session).createConsumer(destination, null, XMLType.getORADataFactory(), null, false);
	}

}
