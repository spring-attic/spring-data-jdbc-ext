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

package org.springframework.data.sql.config.oracle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.util.List;
import java.util.ArrayList;

/**
 * BeanDefinitionParser for the "rac-failover-interceptor" element of the "orcl" name space
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class RacFailoverInterceptorBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    // bean classes
    private static final String RETRY_OPERATIONS_INTERCEPTOR_CLASS =
            "org.springframework.data.sql.jdbc.retry.oracle.RacRetryOperationsInterceptor";

    private static final String RETRY_TEMPLATE_CLASS =
            "org.springframework.commons.retry.support.RetryTemplate";

    private static final String RAC_FAILOVER_RETRY_POLICY_CLASS =
            "org.springframework.data.sql.jdbc.retry.oracle.RacFailoverRetryPolicy";

    // attributes
    private static final String RECOVERABLE_ERROR_CODES_ATTRIBUTE = "recoverable-error-codes";

    private static final String MAX_NUMBER_OF_RETRIES_ATTRIBUTE = "max-number-of-retries";

    private static final String BACK_OFF_POLICY_ATTRIBUTE = "back-off-policy";

    // properties
    private static final String RETRY_OPERATIONS_PROPERTY = "retryOperations";

    private static final String RETRY_POLICY_PROPERTY = "retryPolicy";

    private static final String RECOVERABLE_ERROR_CODES_PROPERTY = "recoverableErrorCodes";

    private static final String MAX_NUMBER_OF_RETRIES_PROPERTY = "maxNumberOfRetries";

    private static final String BACK_OFF_POLICY_PROPERTY = "backOffPolicy";

    // logger
    protected final Log logger = LogFactory.getLog(getClass());


    public RacFailoverInterceptorBeanDefinitionParser() {
    }

    protected String getBeanClassName(Element element) {
        return RETRY_OPERATIONS_INTERCEPTOR_CLASS;
    }

    protected boolean shouldGenerateId() {
        return false;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        //attributes
        List<Integer> recoverableErrorCodesList = new ArrayList<Integer>();
        String recoverableErrorCodes = element.getAttribute(RECOVERABLE_ERROR_CODES_ATTRIBUTE);
        if (StringUtils.hasText(recoverableErrorCodes)) {
            String[] parsedRecoverableErrorCodes = StringUtils.tokenizeToStringArray(recoverableErrorCodes, ",", true, true);
            for (int i = 0; i < parsedRecoverableErrorCodes.length; i++) {
                try {
                    recoverableErrorCodesList.add( new Integer(parsedRecoverableErrorCodes[i]));
                } catch (NumberFormatException e) {
                    parserContext.getReaderContext().error(
                            "Error parsing recoverable error code list: \"" + recoverableErrorCodes +
                            "\"; " + e.getClass().getName() + " - " + e.getMessage(), element);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Using provided " + RECOVERABLE_ERROR_CODES_ATTRIBUTE + ": " + recoverableErrorCodesList);
            }
        }

        String maxNumberOfRetries = element.getAttribute(MAX_NUMBER_OF_RETRIES_ATTRIBUTE);
        if (logger.isDebugEnabled()) {
            if (StringUtils.hasText(maxNumberOfRetries)) {
                logger.debug("Using provided " + MAX_NUMBER_OF_RETRIES_ATTRIBUTE + ": " + maxNumberOfRetries);
            }
        }

        String backOffPolicyRef = null;
        if (element.hasAttribute(BACK_OFF_POLICY_ATTRIBUTE)) {
            backOffPolicyRef = element.getAttribute(BACK_OFF_POLICY_ATTRIBUTE);
            if (logger.isDebugEnabled()) {
                logger.debug("Using provided " + BACK_OFF_POLICY_ATTRIBUTE + ": " + backOffPolicyRef);
            }
        }

        BeanDefinitionBuilder  retryTemplateBuilder = BeanDefinitionBuilder.genericBeanDefinition();
        retryTemplateBuilder.getRawBeanDefinition().setBeanClassName(RETRY_TEMPLATE_CLASS);
        BeanDefinitionBuilder  racFailoverRetryPolicyBuilder = BeanDefinitionBuilder.genericBeanDefinition();
        racFailoverRetryPolicyBuilder.getRawBeanDefinition().setBeanClassName(RAC_FAILOVER_RETRY_POLICY_CLASS);
        if (recoverableErrorCodesList.size() > 0) {
            racFailoverRetryPolicyBuilder.addPropertyValue(RECOVERABLE_ERROR_CODES_PROPERTY, recoverableErrorCodesList);
        }
        if (StringUtils.hasText(maxNumberOfRetries)) {
            racFailoverRetryPolicyBuilder.addPropertyValue(MAX_NUMBER_OF_RETRIES_PROPERTY, maxNumberOfRetries);
        }
        retryTemplateBuilder.addPropertyValue(RETRY_POLICY_PROPERTY, racFailoverRetryPolicyBuilder.getRawBeanDefinition());
        if (StringUtils.hasText(backOffPolicyRef)) {
            retryTemplateBuilder.addPropertyReference(BACK_OFF_POLICY_PROPERTY, backOffPolicyRef);
        }

        builder.addPropertyValue(RETRY_OPERATIONS_PROPERTY, retryTemplateBuilder.getRawBeanDefinition());
        if (logger.isDebugEnabled()) {
            logger.debug("Using retry policy: " + racFailoverRetryPolicyBuilder.getRawBeanDefinition().getBeanClassName());
        }

        builder.setRole(BeanDefinition.ROLE_SUPPORT);

    }

    protected void registerBeanDefinition(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry beanDefinitionRegistry) {
        //register other beans here if necessary
        super.registerBeanDefinition(beanDefinitionHolder, beanDefinitionRegistry);
    }

}