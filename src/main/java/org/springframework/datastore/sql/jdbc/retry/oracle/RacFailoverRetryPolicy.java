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

package org.springframework.datastore.sql.jdbc.retry.oracle;

import org.springframework.commons.retry.RetryContext;
import org.springframework.datastore.sql.jdbc.retry.JdbcRetryPolicy;

/**
 * A JdbcRetryPolicy that will handle determining whether error codes are considered to
 * indicate a recoverable error condition.
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see com.springsource.data.retry.JdbcRetryPolicy
 */
public class RacFailoverRetryPolicy extends JdbcRetryPolicy {
    private static Integer[] DEFAULT_RECOVERABLE_ERROR_CODES =
            new Integer[] {Integer.valueOf(3113),
                           Integer.valueOf(3114),
                           Integer.valueOf(1033),
                           Integer.valueOf(1034),
                           Integer.valueOf(1089),
                           Integer.valueOf(17002),
                           Integer.valueOf(17008),
                           Integer.valueOf(17410)};

    public RacFailoverRetryPolicy() {
        super.setRecoverableErrorCodes(DEFAULT_RECOVERABLE_ERROR_CODES);
    }


    public void setRecoverableErrorCodes(Integer[] recoverableErrorCodes) {
        super.setRecoverableErrorCodes(recoverableErrorCodes);
    }

    /**
     * If retry is not to be attempted, clear the RacRetry ThreadLocal 
     */
    public boolean canRetry(RetryContext retryContext) {
        boolean retry =  super.canRetry(retryContext);
        if (!retry) {
            RacRetryOperationsInterceptor.clearRacRetryInterception();
        }
        return retry;
    }
}
