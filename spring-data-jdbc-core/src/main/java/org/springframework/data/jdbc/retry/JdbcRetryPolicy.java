/*
 * Copyright 2008-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.jdbc.retry;

import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.RetryContext;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.util.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * JDBC specific implementation of a RetryPolicy that checks the Exception for clues
 * to whether retry should be attempted or not.
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class JdbcRetryPolicy extends ExceptionClassifierRetryPolicy {
    private final static int DEFAULT_MAX_NUMBER_OF_RETRIES = 5;

    protected final Log logger = LogFactory.getLog(getClass());

    private List<Integer> recoverableErrorCodes = new ArrayList<Integer>();
    private int maxNumberOfRetries = DEFAULT_MAX_NUMBER_OF_RETRIES;

    public Integer[] getRecoverableErrorCodes() {
        return recoverableErrorCodes.toArray(new Integer[recoverableErrorCodes.size()]);
    }

    public void setRecoverableErrorCodes(Integer[] recoverableErrorCodes) {
        this.recoverableErrorCodes = Arrays.asList(recoverableErrorCodes);
    }

    public int getMaxNumberOfRetries() {
        return maxNumberOfRetries;
    }

    public void setMaxNumberOfRetries(int maxNumberOfRetries) {
        this.maxNumberOfRetries = maxNumberOfRetries;
    }

    /**
     * This gets called for any new invocation.  Make sure that we haven't exceeded the max number of retries.
     */
    public boolean canRetry(RetryContext retryContext) {
        if (retryContext.getRetryCount() > maxNumberOfRetries) {
            logger.warn("Retry count " + retryContext.getRetryCount() + " exceeds max number of retries.");
            return false;
        }
        if (retryContext.getLastThrowable() == null) {
        	return true;
        }
        else {
        	return causeIsRecoverable(retryContext.getLastThrowable());
        }
    }

	private boolean causeIsRecoverable(Throwable lastThrowable) {
        Assert.notNull(lastThrowable, "RetryContext did not contain a Throwable");
        List<SQLException> sqlExceptions = new ArrayList<SQLException>();
        extractSqlExceptions(lastThrowable, sqlExceptions);
        logger.debug("Recoverable errorcodes defined as: " + recoverableErrorCodes);
        for (int i = 0; i < sqlExceptions.size(); i++) {
            SQLException se = sqlExceptions.get(i);
            if (lastThrowable.getClass().getName().equals("java.sql.SQLRecoverableException") ) {
                logger.warn("Recoverable cause [" + lastThrowable.getClass().getName() + "] : " + lastThrowable.getMessage());
                return true;
            }
            else {
                if (recoverableErrorCodes.contains(new Integer(se.getErrorCode()))) {
                    logger.warn("Recoverable cause [" + se.getErrorCode() + "] : " + lastThrowable.getMessage());
                    return true;
                }
            }
        }
        logger.debug("No recoverable cause found.");
        return false;
	}

    private void extractSqlExceptions(Throwable currentThrowable, List<SQLException> sqlExceptions) {
	Assert.notNull(currentThrowable, "Exception must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Extracting from Exception: " + currentThrowable);
        }
        if (currentThrowable instanceof TransactionSystemException) {
            Throwable t = ((TransactionSystemException)currentThrowable).getRootCause();
            if (t != null && t != currentThrowable) {
                extractSqlExceptions(t, sqlExceptions);
            }
            Throwable a = ((TransactionSystemException)currentThrowable).getApplicationException();
            if (a != null && a != currentThrowable) {
                extractSqlExceptions(a, sqlExceptions);
            }
        }
        else if (currentThrowable instanceof TransactionException) {
            Throwable t = ((TransactionException)currentThrowable).getRootCause();
            if (t != null && t != currentThrowable) {
                extractSqlExceptions(t, sqlExceptions);
            }
        }
        else if (currentThrowable instanceof DataAccessException) {
            Throwable t = ((DataAccessException)currentThrowable).getRootCause();
            if (t != null && t != currentThrowable) {
                extractSqlExceptions(t, sqlExceptions);
            }
        }
        else if (currentThrowable instanceof SQLException) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found SQLException ["
                    + ((SQLException)currentThrowable).getErrorCode()
                    + "] : " + currentThrowable.getMessage());
            }
            sqlExceptions.add((SQLException)currentThrowable);
            Throwable t = currentThrowable.getCause();
            if (t != null && t != currentThrowable) {
                extractSqlExceptions(t, sqlExceptions);
            }
        }
        else {
            Throwable t = currentThrowable.getCause();
            if (t != null && t != currentThrowable) {
                extractSqlExceptions(t, sqlExceptions);
            }
        }
    }
}

