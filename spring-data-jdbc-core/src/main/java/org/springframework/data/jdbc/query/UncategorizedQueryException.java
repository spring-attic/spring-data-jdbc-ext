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

import com.mysema.query.QueryException;
import org.springframework.dao.UncategorizedDataAccessException;

/**
 * Exception thrown when we can't classify a Query into
 * one of Spring's data access exceptions.
 *
 * @author Thomas Risberg
 * @deprecated as of Spring JDBC Extensions 1.2.0 in favor of directly 
 * using the Querydsl <a href="https://github.com/querydsl/querydsl/tree/master/querydsl-sql-spring">Spring support</a> 
 */
@Deprecated
public class UncategorizedQueryException extends UncategorizedDataAccessException {

	/**
	 * Constructor for UncategorizedDataAccessException.
	 * @param msg   the detail message
	 * @param cause the exception thrown by underlying data access API
	 */
	public UncategorizedQueryException(String msg, QueryException cause) {
		super(msg, cause);
	}
}
