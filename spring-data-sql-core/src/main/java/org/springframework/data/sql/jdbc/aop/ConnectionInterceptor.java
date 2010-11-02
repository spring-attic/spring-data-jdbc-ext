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

package org.springframework.data.sql.jdbc.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.data.sql.jdbc.support.ConnectionPreparer;

import java.sql.Connection;

/**
 * ConnectionInterceptor that delegates to a ConectionPreparer implementation.
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class ConnectionInterceptor implements MethodInterceptor {

    private ConnectionPreparer connectionPreparer;


    /**
     * Setter for the <code>ConnectionPreparer</code> implementation that will handle the
     * preparation of the <code>Connection</code>.
     * @param connectionPreparer the <code>ConnectionPreparer</code> implementation
     */
    public void setConnectionPreparer(ConnectionPreparer connectionPreparer) {
        this.connectionPreparer = connectionPreparer;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object o = methodInvocation.proceed();
        Object ret = o;
        if (o != null) {
            if (connectionPreparer != null) {
                ret = connectionPreparer.prepare((Connection) o);
            }
        }
        return ret;
    }
}
