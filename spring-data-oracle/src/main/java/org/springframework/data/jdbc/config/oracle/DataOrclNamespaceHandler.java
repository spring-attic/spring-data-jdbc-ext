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

package org.springframework.data.jdbc.config.oracle;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * The NamespaceHandler for the "orcl" xml namespace.
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class DataOrclNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("pooling-datasource", new PoolingDataSourceBeanDefinitionParser());
        registerBeanDefinitionParser("rac-failover-interceptor", new RacFailoverInterceptorBeanDefinitionParser());
        registerBeanDefinitionParser("aq-jms-connection-factory", new AqJmsConnectionFactoryBeanDefinitionParser());
    }

}
