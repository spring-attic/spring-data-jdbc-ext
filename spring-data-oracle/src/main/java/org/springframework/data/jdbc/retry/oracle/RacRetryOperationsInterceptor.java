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

package org.springframework.data.jdbc.retry.oracle;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.data.jdbc.retry.JdbcRetryException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A RetryOperationsInterceptor that will start the RAC retry interception logic. It will keep track
 * of started RAC retry interceptions using a ThreadLocal and only allow one to be started if there
 * is no current transaction.
 *
 * @author Thomas Risberg
 * @since 1.0
 * @see org.springframework.data.jdbc.retry.JdbcRetryPolicy
 */
public class RacRetryOperationsInterceptor extends RetryOperationsInterceptor {

    private static final Log logger = LogFactory.getLog(RacRetryOperationsInterceptor.class);

    private static final ThreadLocal<MethodInvocation> activeRacRetry =
            new ThreadLocal<MethodInvocation>();

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        Object result;

        if (activeRacRetry.get() == null) {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                throw new JdbcRetryException("An active transaction was found.  This is not allowed when starting a retryable operation.");
            }
            startRacRetryInterception(methodInvocation);
            result = super.invoke(methodInvocation);
            clearRacRetryInterception();
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("Participating in existing RAC Retry Interceptor");
            }
            result = methodInvocation.proceed();
        }

        return result;
    }

    /**
     * Method to obtain the status of RAC Retry Interception.  Returns true if
     * RAC Retry Interception is active.
     */
    public static boolean isRacRetryInterceptionActive() {
        return activeRacRetry.get() != null;
    }

    /**
     * Method to be called when RAC Retry Interception is activated. Used internally
     * by RAC Failover handling classes.
     */
    protected static void startRacRetryInterception(MethodInvocation methodInvocation) {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting a new RAC Retry Interceptor for " + (methodInvocation != null ? methodInvocation.getMethod() : null));
        }
        activeRacRetry.set(methodInvocation);
    }

    /**
     * Method to be called when RAC Retry Interception is de-activated.  This
     * would be after succesfull call to the intercepted method or after an
     * exception was throwm.  Used internally by RAC Failover handling classes.
     */
    protected static void clearRacRetryInterception() {
        if (logger.isDebugEnabled()) {
            logger.debug("Clearing RAC Retry Interceptor");
        }
        activeRacRetry.remove();
    }
}