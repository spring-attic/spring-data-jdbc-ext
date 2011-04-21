package org.springframework.data.jdbc.query;

import com.mysema.query.sql.dml.SQLInsertClause;

public interface SqlInsertCallback {
	
	long doInSqlInsertClause(SQLInsertClause insert);

}
