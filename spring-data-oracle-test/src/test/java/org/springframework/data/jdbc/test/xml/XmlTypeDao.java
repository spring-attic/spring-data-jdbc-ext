package org.springframework.data.jdbc.test.xml;


/**
 * @author trisberg
 */
public interface XmlTypeDao {

    void addXmlItem(Long id, String xml);
    
    String getXmlItem(Long id);

    void addItem(Item item);

    Item getItem(Long id);

}
