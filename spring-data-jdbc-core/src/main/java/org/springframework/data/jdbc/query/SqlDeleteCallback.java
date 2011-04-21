package org.springframework.data.jdbc.query;

import com.mysema.query.sql.dml.SQLDeleteClause;

public interface SqlDeleteCallback {
	
	long doInSqlDeleteClause(SQLDeleteClause delete);

}
