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

package org.springframework.data.sql.jms.support.oracle;

import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * An ORADataFactory that handles ADT payloads by simply wrapping the datum object passed in.  This
 * factory is required to integrate with the AQ JMS API.
 */
public class OraDataFactory implements ORADataFactory {

	public ORAData create(Datum datum, int sqlType) throws SQLException {
        return new OraData(datum);
	}

	private static class OraData implements ORAData {

		private final Datum datum;
	
		public OraData(Datum datum) {
			this.datum = datum;
		}
		
		public Datum toDatum(Connection con) throws SQLException {
			return this.datum;
		}
	}

}
