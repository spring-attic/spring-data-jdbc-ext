/*
 * Copyright 2008-2010 the original author or authors.
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

package org.springframework.datastore.sql.jdbc.test;

import static org.easymock.EasyMock.*;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * FactryBean to be used for testing 
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class OracleDataSourceFactoryBean extends AbstractFactoryBean<DataSource> {

    public Class<? extends DataSource> getObjectType() {
        return oracle.jdbc.pool.OracleDataSource.class;
    }

    protected DataSource createInstance() throws Exception {
        DataSource ds = createMock(DataSource.class);
        Connection con = createMock(Connection.class);
        expect(ds.getConnection()).andReturn(con);
        expect(con.getAutoCommit()).andReturn(false);
        con.close();
        con.commit();
        replay(con);
        replay(ds);
        return ds;
    }
}
