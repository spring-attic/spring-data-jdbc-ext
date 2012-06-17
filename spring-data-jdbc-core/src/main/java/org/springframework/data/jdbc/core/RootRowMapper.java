/*
 * Copyright 2008-2012 the original author or authors.
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

package org.springframework.data.jdbc.core;

import org.springframework.jdbc.core.RowMapper;

/**
 * Interface to be used when mapping parent data using the {@link OneToManyResultSetExtractor}.
 *
 * @author Thomas Risberg
 * @author Keith Donald
 * @since 1.0
 */
public interface RootRowMapper<R, PK> extends RowMapper<R> {

	/**
	 * Implementations must implement this method to provide the primary key for the root object.
\	 * @param root the root object
	 * @throws java.sql.SQLException if a SQLException is encountered getting
	 * column values (that is, there's no need to catch SQLException)
	 */
	PK getPrimaryKey(R root);

}
