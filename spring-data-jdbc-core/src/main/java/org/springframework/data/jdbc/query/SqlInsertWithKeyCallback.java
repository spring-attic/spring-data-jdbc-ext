package org.springframework.data.jdbc.query;

import com.mysema.query.sql.dml.SQLInsertClause;

public interface SqlInsertWithKeyCallback<K> {
	
	K doInSqlInsertWithKeyClause(SQLInsertClause insert);

}
