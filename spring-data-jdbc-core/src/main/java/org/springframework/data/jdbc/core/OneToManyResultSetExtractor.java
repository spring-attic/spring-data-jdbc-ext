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

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An results extractor for row mapping operations that map multiple rows to a single root object.
 * This is useful when joining a one-to-many relationship where there can be multiple child rows returned per
 * parent root.
 *
 * It's assumed the the Root type R has a primary key (id) of type K and that the Child type C can map a foreign key of
 * type K referencing the root primary key.
 *
 * For example, consider the relationship: "a Customer has one-to-many Addresses".
 * When joining the Customer table with the Address table to build a Customer object, multiple rows would be returned
 * for a Customer if it has more than one Address. This extractor is useful in that case.
 *
 * @author Thomas Risberg
 * @author Keith Donald
 * @since 1.0
 */
public class OneToManyResultSetExtractor<R, C, K> implements ResultSetExtractor<List<R>> {

	public enum ExpectedResults {
		ANY,
		ONE_AND_ONLY_ONE,
		ONE_OR_NONE,
		AT_LEAST_ONE
	}

	private ExpectedResults expectedResults = ExpectedResults.ANY;

	private RootRowMapper<R, K> rootMapper;

	private ChildRowMapper<R, C, K> childMapper;

	public OneToManyResultSetExtractor(RootRowMapper<R, K> rootMapper, ChildRowMapper<R, C, K> childMapper) {
		this.rootMapper = rootMapper;
		this.childMapper = childMapper;
	}

	public OneToManyResultSetExtractor(RootRowMapper<R, K> rootMapper, ChildRowMapper<R, C, K> childMapper,
			  ExpectedResults expectedResults) {
		this(rootMapper, childMapper);
		this.expectedResults = expectedResults;
	}

	public List<R> extractData(ResultSet rs) throws SQLException, DataAccessException {

		int row = 0;
		List<R> results = new ArrayList<R>();
		boolean more = rs.next();
		if (more) {
			row++;
		}
		while (more) {
			R root = rootMapper.mapRow(rs, row);
			K primaryKey = rootMapper.mapPrimaryKey(rs, row, root);
			if (childMapper.mapForeignKey(rs, row) != null) {
				while (more && primaryKey.equals(childMapper.mapForeignKey(rs, row))) {
					childMapper.mapAndAddChildRow(rs, row, root);
					more = rs.next();
					if (more) {
						row++;
					}
				}
			}
			else {
				more = rs.next();
				if (more) {
					row++;
				}
			}
			results.add(root);
		}
		if ((expectedResults == ExpectedResults.ONE_AND_ONLY_ONE || expectedResults == ExpectedResults.ONE_OR_NONE) &&
				results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, results.size());
		}
		if ((expectedResults == ExpectedResults.ONE_AND_ONLY_ONE || expectedResults == ExpectedResults.AT_LEAST_ONE) &&
				results.size() < 1) {
			throw new IncorrectResultSizeDataAccessException(1, 0);
		}
		return results;
	}

}
