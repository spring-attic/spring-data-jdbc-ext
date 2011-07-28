package org.springframework.data.jdbc.query;

import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.Expression;
import com.mysema.query.types.ExpressionBase;

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
 */
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
	 * @param rowMapper the RowMapper to map the results
	 * @param projection the column projection to be used for the mapping
	 * @return the mapped object
	 */
	<T> T queryForObject(final SQLQuery sqlQuery, final RowMapper<T> rowMapper,
			final Expression<?>... projection);

	/**
	 * Query for a single object using the {@link SQLQuery}. The results are mapped using the
	 * {@link ExpressionBase} which could be a QBean or a MappingProjection.  
	 * @param sqlQuery the SQLQuery to use
	 * @param expression the implementation to be used for the projection/mapping
	 * @return the mapped object
	 */
	<T> T queryForObject(final SQLQuery sqlQuery, final ExpressionBase<T> expression);

	/**
	 * Query for a list of objects using the {@link SQLQuery}. The results are mapped using the
	 * {@link RowMapper} based on the specified projection.  
	 * @param sqlQuery the SQLQuery to use
	 * @param rowMapper the RowMapper to map the results
	 * @param projection the column projection to be used for the mapping
	 * @return the mapped object
	 */
	<T> List<T> query(final SQLQuery sqlQuery, final RowMapper<T> rowMapper,
			final Expression<?>... projection);

	/**
	 * Query for a list of objects using the {@link SQLQuery}. The results are mapped using the
	 * {@link ExpressionBase} which could be a QBean or a MappingProjection.  
	 * @param sqlQuery the SQLQuery to use
	 * @param expression the implementation to be used for the projection/mapping
	 * @return the mapped object
	 */
	<T> List<T> query(final SQLQuery sqlQuery, final ExpressionBase<T> expression);

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