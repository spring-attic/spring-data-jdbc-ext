/*
 * Copyright 2008-2011 the original author or authors.
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

package org.springframework.data.jdbc.query;

import java.util.List;

import com.mysema.query.SearchResults;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.Expression;

/**
 * Interface specifying a basic set of JDBC operations allowing the use
 * of QueryDSL features for these operations.
 *
 * <p>This is an alternative to the classic
 * {@link org.springframework.jdbc.core.JdbcOperations} interface and is 
 * implemented by {@link QueryDslJdbcTemplate}. This interface is not
 * often used directly, but provides a useful option to enhance testability,
 * as it can easily be mocked or stubbed.
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see QueryDslJdbcTemplate
 * @see org.springframework.jdbc.core.JdbcOperations
 * @deprecated as of Spring JDBC Extensions 1.2.0 in favor of directly 
 * using the Querydsl <a href="https://github.com/querydsl/querydsl/tree/master/querydsl-sql-spring">Spring support</a> 
 */
@Deprecated
public interface QueryDslJdbcOperations {

	/**
	 * Expose the classic Spring JdbcTemplate to allow invocation of
	 * classic JDBC operations.
	 */
	JdbcOperations getJdbcOperations();

	/**
	 * Create a new {@link SQLQuery} for this configuration.
	 * @return the new SQLQuery object
	 */
	SQLQuery newSqlQuery();
	
	/**
	 * Count the rows that are part of the results for the given {@link SQLQuery}
	 * @param sqlQuery query to be used
	 * @return the count
	 */
	long count(final SQLQuery sqlQuery);
	
	/**
	 * Count the distinct rows that are part of the results for the given {@link SQLQuery}
	 * @param sqlQuery query to be used
	 * @return the count
	 */
	long countDistinct(final SQLQuery sqlQuery);

	/**
	 * Determine if the result for the given {@link SQLQuery} contains some rows
	 * @param sqlQuery query to be used
	 * @return whether result exist
	 */
	boolean exists(final SQLQuery sqlQuery);

	/**
	 * Determine if the result for the given {@link SQLQuery} is empty
	 * @param sqlQuery query to be used
	 * @return whether result are empty
	 */
	boolean notExists(final SQLQuery sqlQuery);

	/**
	 * Query for a single object using the {@link SQLQuery}. The results are mapped using the
	 * {@link RowMapper} based on the specified projection.  
	 * @param sqlQuery the SQLQuery to use
	 * @param resultSetExtractor the ResultSetExtractor to extract the results
	 * @param projection the column projection to be used for the mapping
	 * @return the mapped object
	 */
	<T> T queryForObject(final SQLQuery sqlQuery, final ResultSetExtractor<T> resultSetExtractor,
			final Expression<?>... projection);

	/**
	 * Query for a single object using the {@link SQLQuery}. The results are mapped using the
	 * {@link RowMapper} based on the specified projection.
	 * @param sqlQuery the SQLQuery to use
	 * @param rowMapper the RowMapper to map the results
	 * @param projection the column projection to be used for the mapping
	 * @return the mapped object
	 */
	<T> T queryForObject(final SQLQuery sqlQuery, final RowMapper<T> rowMapper,
			final Expression<?>... projection);

	/**
	 * Query for a single object using the {@link SQLQuery}. The results are mapped using the
	 * {@link Expression} which could be a QBean or a MappingProjection.
	 * @param sqlQuery the SQLQuery to use
	 * @param expression the implementation to be used for the projection/mapping
	 * @return the mapped object
	 */
	<T> T queryForObject(final SQLQuery sqlQuery, final Expression<T> expression);

	/**
	 * Query for a list of objects using the {@link SQLQuery}. The results are mapped using the
	 * {@link RowMapper} based on the specified projection.  
	 * @param sqlQuery the SQLQuery to use
	 * @param resultSetExtractor the ResultSetExtractor to extract the results
	 * @param projection the column projection to be used for the mapping
	 * @return list of the mapped objects
	 */
	<T> List<T> query(final SQLQuery sqlQuery, final ResultSetExtractor<List<T>> resultSetExtractor,
			final Expression<?>... projection);

	/**
	 * Query for a list of objects using the {@link SQLQuery}. The results are mapped using the
	 * {@link RowMapper} based on the specified projection.
	 * @param sqlQuery the SQLQuery to use
	 * @param rowMapper the RowMapper to map the results
	 * @param projection the column projection to be used for the mapping
	 * @return list of the mapped objects
	 */
	<T> List<T> query(final SQLQuery sqlQuery, final RowMapper<T> rowMapper,
			final Expression<?>... projection);

	/**
	 * Query for a list of objects using the {@link SQLQuery}. The results are mapped using the
	 * {@link Expression} which could be a QBean or a MappingProjection.
	 * @param sqlQuery the SQLQuery to use
	 * @param expression the implementation to be used for the projection/mapping
	 * @return list of the mapped objects
	 */
	<T> List<T> query(final SQLQuery sqlQuery, final Expression<T> expression);

	/**
	 * Query for {@link SearchResults} using the {@link SQLQuery}. The results are mapped using the
	 * {@link Expression} which could be a QBean or a MappingProjection.
	 * @param sqlQuery the SQLQuery to use
	 * @param expression the implementation to be used for the projection/mapping
	 * @return the SearchResults
	 */
	public <T> SearchResults<T> queryResults(final SQLQuery sqlQuery, final Expression<T> expression);

	/**
	 * Execute a database insert using the provided {@link SqlInsertCallback}. 
	 * @param entity {@link RelationalPath} representing the table for the entity
	 * @param callback the SqlInsertCallback implementation that operates on the SQLInsertClause
	 * @return number of affected rows
	 */
	long insert(final RelationalPath<?> entity, final SqlInsertCallback callback);

	/**
	 * Execute a database insert using the provided {@link SqlInsertWithKeyCallback}. 
	 * @param entity {@link RelationalPath} representing the table for the entity
	 * @param callback the SqlInsertWithKeyCallback implementation that operates on the SQLInsertClause
	 * @return the key of the inserted row
	 */
	<K> K insertWithKey(final RelationalPath<?> entity, final SqlInsertWithKeyCallback<K> callback);

	/**
	 * Execute a database update using the provided {@link SqlUpdateCallback}. 
	 * @param entity {@link RelationalPath} representing the table for the entity
	 * @param callback the SqlUpdateCallback implementation that operates on the SQLUpdateClause
	 * @return number of affected rows
	 */
	long update(final RelationalPath<?> entity, final SqlUpdateCallback callback);

	/**
	 * Execute a database delete using the provided {@link SqlDeleteCallback}. 
	 * @param entity {@link RelationalPath} representing the table for the entity
	 * @param callback the SqlDeleteCallback implementation that operates on the SQLDeleteClause
	 * @return number of affected rows
	 */
	long delete(final RelationalPath<?> entity, final SqlDeleteCallback callback);

}