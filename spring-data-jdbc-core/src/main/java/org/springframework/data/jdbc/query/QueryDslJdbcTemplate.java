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

package org.springframework.data.jdbc.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.mysema.query.QueryException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.jdbc.support.DatabaseType;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import com.mysema.query.sql.DerbyTemplates;
import com.mysema.query.sql.H2Templates;
import com.mysema.query.sql.HSQLDBTemplates;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.OracleTemplates;
import com.mysema.query.sql.PostgresTemplates;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.sql.SQLServerTemplates;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.types.Expression;

/**
 * Template class with a basic set of JDBC operations, allowing the use
 * of QueryDSL features.
 *
 * <p>This class delegates to a wrapped {@link #getJdbcOperations() JdbcTemplate}.
 *
 * <p>The underlying {@link org.springframework.jdbc.core.JdbcTemplate} is
 * exposed to allow for convenient access to the traditional
 * {@link org.springframework.jdbc.core.JdbcTemplate} methods.
 *
 * Thanks to Alex Soto (@alexsotob) for getting this started and implementing the
 * initial prototype version.
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.jdbc.core.JdbcTemplate
 */
public class QueryDslJdbcTemplate implements QueryDslJdbcOperations {

	private JdbcTemplate jdbcTemplate;
	
	private SQLTemplates dialect;

	public QueryDslJdbcTemplate(DataSource dataSource) {
		this(new JdbcTemplate(dataSource));
	}

	public QueryDslJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		DatabaseType dbType;
		try {
			dbType = DatabaseType.fromMetaData(jdbcTemplate.getDataSource());
		} catch (MetaDataAccessException e) {
			throw new DataAccessResourceFailureException("Unable to determine database type: ", e);
		}
		if (dbType == DatabaseType.DERBY) {
			this.dialect = new DerbyTemplates();			
		}
		else if (dbType == DatabaseType.H2) {
			this.dialect = new H2Templates();			
		}
		else if (dbType == DatabaseType.HSQL) {
			this.dialect = new HSQLDBTemplates();			
		}
		else if (dbType == DatabaseType.MYSQL) {
			this.dialect = new MySQLTemplates();			
		}
		else if (dbType == DatabaseType.ORACLE) {
			this.dialect = new OracleTemplates();			
		}
		else if (dbType == DatabaseType.POSTGRES) {
			this.dialect = new PostgresTemplates();			
		}
		else if (dbType == DatabaseType.SQLSERVER) {
			this.dialect = new SQLServerTemplates();			
		}
		else {
			throw new InvalidDataAccessResourceUsageException(dbType + " is an unsupported database");			
		}
	}

	public JdbcOperations getJdbcOperations() {
		return this.jdbcTemplate;
	}

	public SQLQuery newSqlQuery() {
		return new SQLQueryImpl(this.dialect);
	}
	
	public long count(final SQLQuery sqlQuery) {
		long count = jdbcTemplate.execute(new ConnectionCallback<Long>() {
			public Long doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLQuery liveQuery = sqlQuery.clone(con);
				try {
					return liveQuery.count();
				} catch (QueryException qe) {
					throw translateQueryException(qe, "SQLQuery", liveQuery.toString());
				}
			}});
		return count;
	}

	public long countDistinct(final SQLQuery sqlQuery) {
		long count = jdbcTemplate.execute(new ConnectionCallback<Long>() {
			public Long doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLQuery liveQuery = sqlQuery.clone(con);
				try {
					return liveQuery.countDistinct();
				} catch (QueryException qe) {
					throw translateQueryException(qe, "SQLQuery", liveQuery.toString());
				}
			}});
		return count;
	}

	public boolean exists(final SQLQuery sqlQuery) {
		boolean exists = jdbcTemplate.execute(new ConnectionCallback<Boolean>() {
			public Boolean doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLQuery liveQuery = sqlQuery.clone(con);
				try {
					return liveQuery.exists();
				} catch (QueryException qe) {
					throw translateQueryException(qe, "SQLQuery", liveQuery.toString());
				}
			}});
		return exists;
	}

	public boolean notExists(final SQLQuery sqlQuery) {
		boolean notExists = jdbcTemplate.execute(new ConnectionCallback<Boolean>() {
			public Boolean doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLQuery liveQuery = sqlQuery.clone(con);
				try {
					return liveQuery.notExists();
				} catch (QueryException qe) {
					throw translateQueryException(qe, "SQLQuery", liveQuery.toString());
				}
			}});
		return notExists;
	}

	public <T> T queryForObject(final SQLQuery sqlQuery, final ResultSetExtractor<T> resultSetExtractor,
				final Expression<?>... projection) {
		T results = jdbcTemplate.execute(new ConnectionCallback<T>() {
			public T doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLQuery liveQuery = sqlQuery.clone(con);
				ResultSet resultSet = queryForResultSet(liveQuery, projection);
				T t = resultSetExtractor.extractData(resultSet);
				JdbcUtils.closeResultSet(resultSet);
				return t;
			}});
		return results;
	}

	public <T> T queryForObject(final SQLQuery sqlQuery, final RowMapper<T> rowMapper,
				final Expression<?>... projection) {
		List<T> results = query(sqlQuery, rowMapper, projection);
		if (results.size() == 0) {
			return null;
		}
		if (results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, results.size());
		}
		return results.get(0);
	}
	
	public <T> T queryForObject(final SQLQuery sqlQuery, final Expression<T> expression) {
		List<T> results = query(sqlQuery, expression);
		if (results.size() == 0) {
			return null;
		}
		if (results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, results.size());
		}
		return results.get(0);
	}

	public <T> List<T> query(final SQLQuery sqlQuery, final ResultSetExtractor<List<T>> resultSetExtractor,
				final Expression<?>... projection) {
		List<T> results = jdbcTemplate.execute(new ConnectionCallback<List<T>>() {
			public List<T> doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLQuery liveQuery = sqlQuery.clone(con);
				ResultSet resultSet = queryForResultSet(liveQuery, projection);
				List<T> list = resultSetExtractor.extractData(resultSet);
				JdbcUtils.closeResultSet(resultSet);
				return list;
			}});
		return results;
	}

	public <T> List<T> query(final SQLQuery sqlQuery, final RowMapper<T> rowMapper, final Expression<?>... projection) {
		return query(sqlQuery, new RowMapperResultSetExtractor<T>(rowMapper), projection);
	}
	
	public <T> List<T> query(final SQLQuery sqlQuery, final Expression<T> expression) {
		List<T> results = jdbcTemplate.execute(new ConnectionCallback<List<T>>() {
			public List<T> doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLQuery liveQuery = sqlQuery.clone(con);
				try {
					return liveQuery.list(expression);
				} catch (QueryException qe) {
					throw translateQueryException(qe, "SQLQuery", liveQuery.toString());
				}
			}});
		return results;
	}
	
	public long insert(final RelationalPath<?> entity, final SqlInsertCallback callback) {
		long rowsAffected = jdbcTemplate.execute(new ConnectionCallback<Long>() {
			public Long doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLInsertClause sqlClause = new SQLInsertClause(con, dialect, entity);
				try {
					return callback.doInSqlInsertClause(sqlClause);
				} catch (QueryException qe) {
					throw  translateQueryException(qe, "SQLInsertClause", sqlClause.toString());
				}
			}});
		return rowsAffected;
	}

	public <K> K insertWithKey(final RelationalPath<?> entity, final SqlInsertWithKeyCallback<K> callback) {
		K generatedKey = jdbcTemplate.execute(new ConnectionCallback<K>() {
			public K doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLInsertClause sqlClause = new SQLInsertClause(con, dialect, entity);
				try {
					return callback.doInSqlInsertWithKeyClause(sqlClause);
				} catch (QueryException qe) {
					throw translateQueryException(qe, "SQLInsertClause", sqlClause.toString());
				}
			}});
		return generatedKey;
	}

	public long update(final RelationalPath<?> entity, final SqlUpdateCallback callback) {
		long rowsAffected = jdbcTemplate.execute(new ConnectionCallback<Long>() {
			public Long doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLUpdateClause sqlClause = new SQLUpdateClause(con, dialect, entity);
				try {
					return callback.doInSqlUpdateClause(sqlClause);
				} catch (QueryException qe) {
					throw  translateQueryException(qe, "SQLUpdateClause", sqlClause.toString());
				}
			}});
		return rowsAffected;
	}

	public long delete(final RelationalPath<?> entity, final SqlDeleteCallback callback) {
		long rowsAffected = jdbcTemplate.execute(new ConnectionCallback<Long>() {
			public Long doInConnection(Connection con) throws SQLException,
					DataAccessException {
				SQLDeleteClause sqlClause = new SQLDeleteClause(con, dialect, entity);
				try {
					return callback.doInSqlDeleteClause(sqlClause);
				} catch (QueryException qe) {
					throw  translateQueryException(qe, "SQLDeleteClause", sqlClause.toString());
				}
			}});
		return rowsAffected;
	}

	private ResultSet queryForResultSet(SQLQuery liveQuery, Expression<?>[] projection) throws DataAccessException {
		ResultSet resultSet;
		try {
			resultSet = liveQuery.getResults(projection);
		} catch (QueryException qe) {
			throw  translateQueryException(qe, "SQLQuery", liveQuery.toString());
		}
		return resultSet;
	}

	private RuntimeException translateQueryException(QueryException qe, String task, String query) {
		Throwable t = qe.getCause();
		if (t instanceof SQLException) {
			return jdbcTemplate.getExceptionTranslator()
					.translate(task, query, (SQLException) t);
		}
		return new UncategorizedQueryException("Error in " + "SQLQuery", qe);
	}
}
