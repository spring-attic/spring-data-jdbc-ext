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

package org.springframework.data.jdbc.query.generated;

import com.mysema.query.sql.codegen.MetaDataExporter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * App to generate the Querydsl query classes.
 *
 * Run this app as needed, usually when yhe Querydsl version
 * is upgraded to a new version.  The generated classes are
 * written to build/generated-sources/java
 *
 * It's easiest to run this class from an IDE since the classpath
 * is already set.
 *
 * Copy new QCustomer.java and QCustomerNames.java classes to the
 * src/test/java directory
 *
 * Make a duplicate of QCustomer.java called QBadCustomer.java and
 * remove underscores from FIRST_NAME and LAST_NAME field name
 * metadata. Copy this class to src/test/java
 *
 * @author Thomas Risberg
 * @since 1.0.1
 */
public class GeneratorApp {
	public static void main(String[] args) {
		System.out.println("Generating Querydsl classes for the tests");
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("query-dsl-context.xml");
		DataSource ds = ctx.getBean(DataSource.class);
		Connection conn = null;
		try {
			conn = ds.getConnection();
			MetaDataExporter exporter = new MetaDataExporter();
			exporter.setPackageName("org.springframework.data.jdbc.query.generated");
			exporter.setTargetFolder(new File("build/generated-sources/java"));
			exporter.export(conn.getMetaData());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException ignore) {}
		}
		ctx.close();
	}
}
