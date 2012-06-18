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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface to be used when mapping child data using the {@link OneToManyResultSetExtractor}.
 *
 * @author Thomas Risberg
 * @author Keith Donald
 * @since 1.0
 */
public interface ChildRowMapper<R, C, K> extends RowMapper<C> {

	/**
	 * Implementations must implement this method to map the ResultSet data for
	 * the child object for the current row. The resulting object should then be added
	 * to the parent. This method should not call <code>next()</code> on
	 * the ResultSet.
	 * @param rs the ResultSet to map (pre-initialized for the current row)
	 * @param rowNum the number of the current row
	 * @param root the root object
	 * @throws SQLException if a SQLException is encountered getting
	 * column values (that is, there's no need to catch SQLException)
	 */
	void mapAndAddChildRow(ResultSet rs, int rowNum, R root) throws SQLException;

	/**
	 * Implementations must implement this method to map the foreign key for
	 * the current row of the ResultSet. This method should not call <code>next()</code> on
	 * the ResultSet.
	 * @param rs the ResultSet to map (pre-initialized for the current row)
	 * @param rowNum the number of the current row
	 * @return the result object for the current row
	 * @throws SQLException if a SQLException is encountered getting
	 * column values (that is, there's no need to catch SQLException)
	 */
	K mapForeignKey(ResultSet rs, int rowNum) throws SQLException;

}
