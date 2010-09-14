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

package org.springframework.datastore.sql.jdbc.aop;

import org.springframework.datastore.sql.jdbc.support.ConnectionPreparer;

import java.sql.Connection;

/**
 * @author Thomas Risberg
 */
public class CountingConnectionPreparer implements ConnectionPreparer {

    private int count = 0;

    public Connection prepare(Connection connection) {
        count++;
        return connection;
    }

    public int getCount() {
        return count;
    }

}
