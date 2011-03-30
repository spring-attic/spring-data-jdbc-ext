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

package org.springframework.data.jdbc.config.oracle;

import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

/**
 * BeanDefinitionParser for the "aq-jms-queue-connection-factory" element of the "orcl" name space
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class AqJmsConnectionFactoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    // bean factory config
    private static final String FACTORY_BEAN_CLASS = "org.springframework.data.jdbc.config.oracle.AqJmsFactoryBeanFactory";

    // attributes
    private static final String DATA_SOURCE_ATTRIBUTE = "data-source";

    private static final String USE_LOCAL_DATA_SOURCE_TX_ATTRIBUTE = "use-local-data-source-transaction";

    private static final String CONNECTION_FACTORY_TYPE_ATTRIBUTE = "connection-factory-type";

    private static final String NATIVE_JDBC_EXTRACTOR_ATTRIBUTE = "native-jdbc-extractor";

    protected final Log logger = LogFactory.getLog(getClass());

    public AqJmsConnectionFactoryBeanDefinitionParser() {
    }

    protected String getBeanClassName(Element element) {
        return FACTORY_BEAN_CLASS;
    }

    protected boolean shouldGenerateId() {
        return false;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        //attributes
        String dataSourceRef = element.getAttribute(DATA_SOURCE_ATTRIBUTE);

        if (logger.isDebugEnabled()) {
            logger.debug("Using provided " + NATIVE_JDBC_EXTRACTOR_ATTRIBUTE + ": " + dataSourceRef);
        }

        String useLocalDataSourceTx = element.getAttribute(USE_LOCAL_DATA_SOURCE_TX_ATTRIBUTE);

        String connectionFactoryTypeRef = element.getAttribute(CONNECTION_FACTORY_TYPE_ATTRIBUTE);

        String nativeJdbcExtractorRef = element.getAttribute(NATIVE_JDBC_EXTRACTOR_ATTRIBUTE);

        builder.getRawBeanDefinition().setBeanClassName(FACTORY_BEAN_CLASS);
        RuntimeBeanReference ds = new RuntimeBeanReference(dataSourceRef);
        builder.addPropertyValue("dataSource", ds);

        if (StringUtils.hasText(useLocalDataSourceTx)) {
            if ("true".equals(useLocalDataSourceTx.toLowerCase()) ||
                    "false".equals(useLocalDataSourceTx.toLowerCase())) {
                builder.addPropertyValue("coordinateWithDataSourceTransactions", useLocalDataSourceTx);
            }
            else {
                parserContext.getReaderContext().error(
                        "The 'use-local-data-source-transaction' attribute should be \"true\" or \"false\"; \"" +
                                useLocalDataSourceTx + "\" is an invalid value", element);
            }
        }

        if (StringUtils.hasText(connectionFactoryTypeRef)) {
            builder.addPropertyValue("connectionFactoryType", connectionFactoryTypeRef);
        }

        if (StringUtils.hasText(nativeJdbcExtractorRef)) {
            RuntimeBeanReference nje = new RuntimeBeanReference(nativeJdbcExtractorRef);
            builder.addPropertyValue("nativeJdbcExtractor", nje);
        }

        builder.setRole(BeanDefinition.ROLE_SUPPORT);

    }

    protected void registerBeanDefinition(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry beanDefinitionRegistry) {
        //register other beans here if necessary
        super.registerBeanDefinition(beanDefinitionHolder, beanDefinitionRegistry);
    }

}
