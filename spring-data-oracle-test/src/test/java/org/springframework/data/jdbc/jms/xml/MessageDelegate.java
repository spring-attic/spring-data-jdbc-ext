package org.springframework.data.jdbc.jms.xml;

import java.io.StringWriter;

import javax.jms.JMSException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.jms.support.converter.MessageConversionException;
import org.w3c.dom.Document;

public class MessageDelegate {
	
	private int count = 0;

    public void handleMessage(Document xmlDoc) throws MessageConversionException, JMSException {
    	count++;
    	System.out.println("---> " + xmlDoc);
        String xmlString = xmlDocumentToString(xmlDoc);
        System.out.println(xmlString);
    }

	private String xmlDocumentToString(Document xmlDoc)
			throws TransformerFactoryConfigurationError {
		Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(xmlDoc);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        String xmlString = result.getWriter().toString();
		return xmlString;
	}

	public int getCount() {
		return count;
	}

}
