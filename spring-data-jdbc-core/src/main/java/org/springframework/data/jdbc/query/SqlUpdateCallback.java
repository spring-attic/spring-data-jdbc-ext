package org.springframework.data.jdbc.query;

import com.mysema.query.sql.dml.SQLUpdateClause;

public interface SqlUpdateCallback {
	
	long doInSqlUpdateClause(SQLUpdateClause update);

}
