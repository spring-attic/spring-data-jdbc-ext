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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * BeanDefinitionParser for the "pooling-data-source" element of the "orcl" name space
 *
 * @author Thomas Risberg
 * @since 1.0
 */
public class PoolingDataSourceBeanDefinitionParser extends AbstractBeanDefinitionParser {

	private static final String DEFAULT_PROPERTY_FILE_LOCATION = "classpath:orcl.properties";
	private static final String DEFAULT_PROPERTY_PREFIX = null;
	private static final String DEFAULT_CONNECTION_CACHING_ENABLED = "true";

	// Property file attributes
	private static final String CONNECTION_PROPERTIES_CHILD_ELEMENT = "connection-properties";
	private static final String CONNECTION_CACHE_PROPERTIES_CHILD_ELEMENT = "connection-cache-properties";
	private static final String USERNAME_CONNECTION_PROXY = "username-connection-proxy";
	private static final String CONNECTION_CONTEXT_PROVIDER = "connection-context-provider";
	private static final String PROPERTIES_LOCATION_ATTRIBUTE = "properties-location";
	private static final String CONNECTION_PROPERTIES_PREFIX_ATTRIBUTE = "connection-properties-prefix";
	private static final String CONNECTON_CACHE_PROPERTIS_PREFIX_ATTRIBUTE = "connection-cache-properties-prefix";

	// Required attributes
	private static final String URL_ATTRIBUTE = "url";

	// Optional attributes
	private static final String USERNAME_ATTRIBUTE = "username";
	private static final String PASSWORD_ATTRIBUTE = "password";
	private static final String ONS_CONFIGURATION_ATTRIBUTE = "ONS-configuration";
	private static final String FAST_CONNECTION_FAILOVER_ENABLED_ATTRIBUTE = "fast-connection-failover-enabled";
	private static final String CONNECTION_CACHING_ENABLED_ATTRIBUTE = "connection-caching-enabled";

	protected final Log logger = LogFactory.getLog(getClass());

	private Map<String, String> attributeToPropertyMap = new HashMap<String, String>();

	public PoolingDataSourceBeanDefinitionParser() {
		attributeToPropertyMap.put(URL_ATTRIBUTE, "url");
		attributeToPropertyMap.put(USERNAME_ATTRIBUTE, "user");
		attributeToPropertyMap.put(PASSWORD_ATTRIBUTE, "password");
		attributeToPropertyMap.put(CONNECTION_CACHING_ENABLED_ATTRIBUTE, "connectionCachingEnabled");
		attributeToPropertyMap.put(FAST_CONNECTION_FAILOVER_ENABLED_ATTRIBUTE, "fastConnectionFailoverEnabled");
		attributeToPropertyMap.put(ONS_CONFIGURATION_ATTRIBUTE, "ONSConfiguration");
	}

	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		//ToDo look for username-connection-proxy
		boolean useWrapper = false;
		String connectionContextProviderRef = null;
		Element usernameConnectionProxyElement = DomUtils.getChildElementByTagName(element, USERNAME_CONNECTION_PROXY);
		if (usernameConnectionProxyElement != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Using username-connection-proxy");
			}
			if (usernameConnectionProxyElement.hasAttribute(CONNECTION_CONTEXT_PROVIDER)) {
				if (logger.isDebugEnabled()) {
					logger.debug(CONNECTION_CONTEXT_PROVIDER + ": " + usernameConnectionProxyElement.getAttribute(CONNECTION_CONTEXT_PROVIDER));
				}
				connectionContextProviderRef = usernameConnectionProxyElement.getAttribute(CONNECTION_CONTEXT_PROVIDER);
			}
			useWrapper = true;
			//builder.addPropertyValue("connectionProperties", connProperties);
		}

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
		builder.getRawBeanDefinition().setBeanClassName(getBeanClassName(element));
		builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
		builder.getRawBeanDefinition().setDestroyMethodName("close");
		if (parserContext.isNested()) {
			// Inner bean definition must receive same scope as containing bean.
			builder.setScope(parserContext.getContainingBeanDefinition().getScope());
		}
		if (parserContext.isDefaultLazyInit()) {
			// Default-lazy-init applies to custom bean definitions as well.
			builder.setLazyInit(true);
		}
		doParse(element, parserContext, builder);
		if (useWrapper) {
			BeanDefinitionBuilder wrapper = BeanDefinitionBuilder.genericBeanDefinition();
			wrapper.getRawBeanDefinition().setBeanClassName("org.springframework.data.jdbc.support.oracle.ProxyDataSource");
			wrapper.addConstructorArgValue(builder.getBeanDefinition());
			if (connectionContextProviderRef == null) {
				wrapper.addConstructorArgValue(null);
			}
			else {
				wrapper.addConstructorArgReference(connectionContextProviderRef);
			}
			return wrapper.getBeanDefinition();
		}
		else {
			return builder.getBeanDefinition();
		}
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		ResourceLoader rl = parserContext.getReaderContext().getResourceLoader();
		//attributes
		String propertyFileLocation = element.getAttribute(PROPERTIES_LOCATION_ATTRIBUTE);
		String connectionPropertyFileLocation = element.getAttribute(PROPERTIES_LOCATION_ATTRIBUTE);

		String connectionPropertyPrefix = element.getAttribute(CONNECTION_PROPERTIES_PREFIX_ATTRIBUTE);
		String cachingPropertyPrefix = element.getAttribute(CONNECTON_CACHE_PROPERTIS_PREFIX_ATTRIBUTE);
		String url = element.getAttribute(URL_ATTRIBUTE);
		String username = element.getAttribute(USERNAME_ATTRIBUTE);
		String password = element.getAttribute(PASSWORD_ATTRIBUTE);
		String onsConfiguration = element.getAttribute(ONS_CONFIGURATION_ATTRIBUTE);
		String fastConnectionFailoverEnabled = element.getAttribute(FAST_CONNECTION_FAILOVER_ENABLED_ATTRIBUTE);
		String connectionCachingEnabled = element.getAttribute(CONNECTION_CACHING_ENABLED_ATTRIBUTE);

		boolean propertyFileProvided = false;

		Map<String, Object> providedProperties = new HashMap<String, Object>();

		// defaults
		if (!StringUtils.hasText(propertyFileLocation) && !StringUtils.hasText(connectionPropertyFileLocation)) {
			propertyFileLocation = DEFAULT_PROPERTY_FILE_LOCATION;
		}
		if (!StringUtils.hasText(connectionPropertyPrefix)) {
			connectionPropertyPrefix = DEFAULT_PROPERTY_PREFIX;
		}

		// look for property files
		if (StringUtils.hasText(propertyFileLocation)) {
			logger.debug("Using properties location: " + propertyFileLocation);
			String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(propertyFileLocation);
			Resource r = rl.getResource(resolvedLocation);
			logger.debug("Loading properties from resource: " + r);
			PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
			factoryBean.setLocation(r);
			try {
				factoryBean.afterPropertiesSet();
				Properties resource = factoryBean.getObject();
				for (Map.Entry<Object, Object> entry : resource.entrySet()) {
					providedProperties.put((String)entry.getKey(), entry.getValue());
				}
				propertyFileProvided = true;
			} catch (FileNotFoundException e) {
				propertyFileProvided = false;
				if (propertyFileLocation.equals(DEFAULT_PROPERTY_FILE_LOCATION)) {
					logger.debug("Unable to find " + propertyFileLocation);
				}
				else {
					parserContext.getReaderContext().error("pooling-datasource defined with attribute '" +
							PROPERTIES_LOCATION_ATTRIBUTE + "' but the property file was not found at location \"" +
							propertyFileLocation + "\"", element);
				}
			} catch (IOException e) {
				logger.warn("Error loading " + propertyFileLocation + ": " + e.getMessage());
			}
		}
		else {
			propertyFileProvided = false;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Using provided properties: " + providedProperties);
		}

		if (connectionPropertyPrefix == null) {
			connectionPropertyPrefix = "";
		}
		if (connectionPropertyPrefix.length() > 0 && !connectionPropertyPrefix.endsWith(".")) {
			connectionPropertyPrefix = connectionPropertyPrefix + ".";
		}
		logger.debug("Using connection properties prefix: " + connectionPropertyPrefix);

		if (cachingPropertyPrefix == null) {
			cachingPropertyPrefix = "";
		}
		if (cachingPropertyPrefix.length() > 0 && !cachingPropertyPrefix.endsWith(".")) {
			cachingPropertyPrefix = cachingPropertyPrefix + ".";
		}
		logger.debug("Using caching properties prefix: " + cachingPropertyPrefix);

		if (!(StringUtils.hasText(connectionCachingEnabled) ||
				providedProperties.containsKey(attributeToPropertyMap.get(CONNECTION_CACHING_ENABLED_ATTRIBUTE)))) {
			connectionCachingEnabled = DEFAULT_CONNECTION_CACHING_ENABLED;
		}

		setRequiredAttribute(builder, parserContext, element, providedProperties, connectionPropertyPrefix, propertyFileProvided, url, URL_ATTRIBUTE, "URL");
		setOptionalAttribute(builder, providedProperties, connectionPropertyPrefix, username, USERNAME_ATTRIBUTE);
		setOptionalAttribute(builder, providedProperties, connectionPropertyPrefix, password, PASSWORD_ATTRIBUTE);
		setOptionalAttribute(builder, providedProperties, connectionPropertyPrefix, connectionCachingEnabled, CONNECTION_CACHING_ENABLED_ATTRIBUTE);
		setOptionalAttribute(builder, providedProperties, connectionPropertyPrefix, fastConnectionFailoverEnabled, FAST_CONNECTION_FAILOVER_ENABLED_ATTRIBUTE);
		setOptionalAttribute(builder, providedProperties, connectionPropertyPrefix, onsConfiguration, ONS_CONFIGURATION_ATTRIBUTE);

		Properties providedConnectionProperties = new Properties();
		Properties providedCachingProperties = new Properties();
		for (String key : providedProperties.keySet()) {
			if (StringUtils.hasText(connectionPropertyPrefix) && key.startsWith(connectionPropertyPrefix)) {
				String newKey = key.substring(connectionPropertyPrefix.length());
				providedConnectionProperties.put(newKey, providedProperties.get(key));
			}
			else {
				if (StringUtils.hasText(cachingPropertyPrefix) && key.startsWith(cachingPropertyPrefix)) {
					String newKey = key.substring(cachingPropertyPrefix.length());
					providedCachingProperties.put(newKey, providedProperties.get(key));
				}
				else {
					providedConnectionProperties.put(key, providedProperties.get(key));
				}
			}
		}

		// look for connectionProperties
		Object connProperties = DomUtils.getChildElementValueByTagName(element, CONNECTION_PROPERTIES_CHILD_ELEMENT);
		if (connProperties != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Using connection-properties");
			}
			builder.addPropertyValue("connectionProperties", connProperties);
		}
		else {
			if (providedConnectionProperties.size() > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("Using provided connection properties: " + providedConnectionProperties);
				}
				builder.addPropertyValue("connectionProperties", providedConnectionProperties);
			}
		}

		// look for connectionCacheProperties
		Object cacheProperties = DomUtils.getChildElementValueByTagName(element, CONNECTION_CACHE_PROPERTIES_CHILD_ELEMENT);
		if (cacheProperties != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Using connection-cache-properties: [" + cacheProperties + "]");
			}
			builder.addPropertyValue("connectionCacheProperties", cacheProperties);
		}
		else {
			if (providedCachingProperties.size() > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("Using provided caching properties: " + providedCachingProperties);
				}
				builder.addPropertyValue("connectionCacheProperties", providedCachingProperties);
			}
		}

		builder.setRole(BeanDefinition.ROLE_SUPPORT);
	}

	protected String getBeanClassName(Element element) {
		return "oracle.jdbc.pool.OracleDataSource";
	}

	protected boolean shouldGenerateId() {
		return false;
	}


	private void setRequiredAttribute(BeanDefinitionBuilder builder,
			  ParserContext parserContext,
			  Element element,
				Map<String, Object> providedProperties,
				String propertyPrefix,
				boolean propertyFileProvided,
				String attributeValue,
				String attributeName,
				String orclPropertyName) {

		String propertyToRemove = null;
		String propertyKey = propertyPrefix != null ? propertyPrefix + attributeName : attributeName;
		String orclKey = propertyPrefix != null ? propertyPrefix + orclPropertyName : orclPropertyName;
		if (StringUtils.hasText(attributeValue)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Registering required attribute " + orclPropertyName + " with attribute value " + attributeValue);
			}
			builder.addPropertyValue(orclPropertyName, attributeValue);
		}
		else if (providedProperties.containsKey(propertyKey) || providedProperties.containsKey(orclKey)) {
			Object value;
			if (providedProperties.containsKey(propertyKey)) {
				value = providedProperties.get(propertyKey);
				propertyToRemove = propertyKey;
			}
			else {
				value = providedProperties.get(orclKey);
				propertyToRemove = orclKey;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Registering required attribute " + orclPropertyName + " with property value " + value);
			}
			builder.addPropertyValue(orclPropertyName, value);
		}
		else {
			if (propertyFileProvided) {
				parserContext.getReaderContext().error(
						"pooling-datasource defined without the required '" + attributeName +
						"' attribute and the property file does not contain a \"" +
						attributeToPropertyMap.get(attributeName) + "\" entry", element);
			}
			else {
				parserContext.getReaderContext().error("pooling-datasource defined without the required '" + attributeName +
						"' attribute and a property file was not found at location \"" + DEFAULT_PROPERTY_FILE_LOCATION + "\"",
						element);
			}
		}
		if (propertyToRemove != null) {
			removeProvidedProperty(providedProperties, propertyToRemove);
		}
	}

	private void setOptionalAttribute(BeanDefinitionBuilder builder,
				Map<String, Object> providedProperties,
				String propertyPrefix,
				String attributeValue,
				String attributeName) {
		String propertyKey;
		if ("username".equals(attributeName)) {
			String userKey = (propertyPrefix != null ?
					propertyPrefix + "user" :
					"user");
			if (providedProperties.containsKey(userKey)) {
				propertyKey = userKey;
			}
			else {
				propertyKey = (propertyPrefix != null ?
						propertyPrefix + attributeName :
						attributeName);
			}
		}
		else {
			propertyKey = (propertyPrefix != null ?
					propertyPrefix + attributeToPropertyMap.get(attributeName) :
					attributeToPropertyMap.get(attributeName));
		}

		if (StringUtils.hasText(attributeValue)) {
			if (logger.isDebugEnabled()) {
				if ("password".equals(attributeName)) {
					logger.debug("Registering optional attribute " + attributeToPropertyMap.get(attributeName) +
							" with attribute value ******");
				}
				else {
					logger.debug("Registering optional attribute " + attributeToPropertyMap.get(attributeName) +
							" with attribute value " + attributeValue);
				}
			}
			builder.addPropertyValue(attributeToPropertyMap.get(attributeName), attributeValue);
		}
		else if (providedProperties.containsKey(propertyKey)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Registering optional attribute " + attributeToPropertyMap.get(attributeName) +
						" with property value " +
						("password".equals(attributeName) ? "******" : providedProperties.get(propertyKey)));
			}
			builder.addPropertyValue(attributeToPropertyMap.get(attributeName), providedProperties.get(propertyKey));
		}
		removeProvidedProperty(providedProperties, propertyKey);
	}

	private void removeProvidedProperty(Map<String, Object> providedProperties, String attributeName) {
		if (providedProperties.containsKey(attributeName)) {
			providedProperties.remove(attributeName);
		}
	}

	protected void registerBeanDefinition(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry beanDefinitionRegistry) {
		//register other beans here if necessary
		super.registerBeanDefinition(beanDefinitionHolder, beanDefinitionRegistry);
	}

}