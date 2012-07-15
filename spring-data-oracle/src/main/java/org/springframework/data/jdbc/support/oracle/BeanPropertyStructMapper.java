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

package org.springframework.data.jdbc.support.oracle;

import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.*;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping implementation that converts struct attributes into a new instance
 * of the specified mapped target class. The mapped target class must be a
 * top-level class and it must have a default or no-arg constructor.
 *
 * <p>Attribute values are mapped based on matching the column name as obtained from result set
 * metadata to public setters for the corresponding properties. The names are matched either
 * directly or by transforming a name separating the parts with underscores to the same name
 * using "camel" case.
 *
 * <p>Mapping is provided for fields in the target class for many common types, e.g.:
 * String, boolean, Boolean, byte, Byte, short, Short, int, Integer, long, Long,
 * float, Float, double, Double, BigDecimal, <code>java.util.Date</code>, etc.
 * 
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @since 1.0
 */
public class BeanPropertyStructMapper<T> implements StructMapper<T> {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** The class we are mapping to */
	protected Class<T> mappedClass;

	/** Map of the fields we provide mapping for */
	private Map<String, PropertyDescriptor> mappedFields;


	/**
	 * Create a new BeanPropertyRowMapper.
	 * @see #setMappedClass
	 */
	public BeanPropertyStructMapper() {
	}

	/**
	 * Create a new BeanPropertyRowMapper.
	 * @param mappedClass the class that each row should be mapped to.
	 */
	public BeanPropertyStructMapper(Class<T> mappedClass) {
		initialize(mappedClass);
	}


	/**
	 * Set the class that each row should be mapped to.
	 */
	public void setMappedClass(Class<T> mappedClass) {
		if (this.mappedClass == null) {
			initialize(mappedClass);
		}
		else {
			if (!this.mappedClass.equals(mappedClass)) {
				throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to " +
						mappedClass + " since it is already providing mapping for " + this.mappedClass);
			}
		}
	}

	/**
	 * Initialize the mapping metadata for the given class.
	 * @param mappedClass the mapped class.
	 */
	protected void initialize(Class<T> mappedClass) {
		this.mappedClass = mappedClass;
		this.mappedFields = new HashMap<String, PropertyDescriptor>();
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
		for (int i = 0; i < pds.length; i++) {
			PropertyDescriptor pd = pds[i];
			if (pd.getWriteMethod() != null) {
				this.mappedFields.put(pd.getName().toLowerCase(), pd);
				String underscoredName = underscoreName(pd.getName());
				if (!pd.getName().toLowerCase().equals(underscoredName)) {
					this.mappedFields.put(underscoredName, pd);
				}
			}
		}
	}

	/**
	 * Convert a name in camelCase to an underscored name in lower case.
	 * Any upper case letters are converted to lower case with a preceding underscore.
	 * @param name the string containing original name
	 * @return the converted name
	 */
	private String underscoreName(String name) {
		StringBuffer result = new StringBuffer();
		if (name != null && name.length() > 0) {
			result.append(name.substring(0, 1).toLowerCase());
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				if (s.equals(s.toUpperCase())) {
					result.append("_");
					result.append(s.toLowerCase());
				}
				else {
					result.append(s);
				}
			}
		}
		return result.toString();
	}

	/**
	 * Get the class that we are mapping to.
	 */
	public final Class<T> getMappedClass() {
		return this.mappedClass;
	}

	/**
	 * Get the fields that we are mapping to.
	 */
	public final Map<String, PropertyDescriptor> getMappedFields() {
		return this.mappedFields;
	}


    public STRUCT toStruct(T source, Connection conn, String typeName) throws SQLException {
        StructDescriptor descriptor = new StructDescriptor(typeName, conn);
        ResultSetMetaData rsmd = descriptor.getMetaData();
        int columns = rsmd.getColumnCount();
        Object[] values = new Object[columns];
        for (int i = 1; i <= columns; i++) {
            String column = JdbcUtils.lookupColumnName(rsmd, i).toLowerCase();
            PropertyDescriptor fieldMeta = (PropertyDescriptor) this.mappedFields.get(column);
            if (fieldMeta != null) {
                BeanWrapper bw = new BeanWrapperImpl(source);
                if (bw.isReadableProperty(fieldMeta.getName())) {
                    try {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Mapping column named \"" + column + "\"" +
                                    " to property \"" + fieldMeta.getName() + "\"");
                        }
                        values[i - 1] = bw.getPropertyValue(fieldMeta.getName());
                    }
                    catch (NotReadablePropertyException ex) {
                        throw new DataRetrievalFailureException(
                                "Unable to map column " + column + " to property " + fieldMeta.getName(), ex);
                    }
                }
                else {
                    logger.warn("Unable to access the getter for " + fieldMeta.getName() +
                            ".  Check that " + "get" + StringUtils.capitalize(fieldMeta.getName()) +
                            " is declared and has public access.");
                }
            }
        }
        STRUCT struct = new STRUCT(descriptor, conn, values);
        return struct;
    }

    /**
	 * Extract the values for all attributes in the struct.
	 * <p>Utilizes public setters and result set metadata.
	 * @see java.sql.ResultSetMetaData
	 */
	public T fromStruct(STRUCT struct) throws SQLException {
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
        initBeanWrapper(bw);

        ResultSetMetaData rsmd = struct.getDescriptor().getMetaData();
        Object[] attr = struct.getAttributes();
        int columnCount = rsmd.getColumnCount();
        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index).toLowerCase();
            PropertyDescriptor pd = (PropertyDescriptor) this.mappedFields.get(column);
            if (pd != null) {
                try {
                    Object value = attr[index - 1];
                    if (logger.isDebugEnabled()) {
                        logger.debug("Mapping column '" + column + "' to property '" +
                                pd.getName() + "' of type " + pd.getPropertyType());
                    }
                    bw.setPropertyValue(pd.getName(), value);
                }
                catch (NotWritablePropertyException ex) {
                    throw new DataRetrievalFailureException(
                            "Unable to map column " + column + " to property " + pd.getName(), ex);
                }
            }
        }

        return mappedObject;
    }

	/**
	 * Initialize the given BeanWrapper to be used for row mapping.
	 * To be called for each row.
	 * <p>The default implementation is empty. Can be overridden in subclasses.
	 * @param bw the BeanWrapper to initialize
	 */
	protected void initBeanWrapper(BeanWrapper bw) {
	}

	/**
	 * Static factory method to create a new BeanPropertyStructMapper
	 * (with the mapped class specified only once).
	 * @param mappedClass the class that each row should be mapped to
	 */
	public static <T> BeanPropertyStructMapper<T> newInstance(Class<T> mappedClass) {
		BeanPropertyStructMapper<T> newInstance = new BeanPropertyStructMapper<T>();
		newInstance.setMappedClass(mappedClass);
		return newInstance;
	}
}
