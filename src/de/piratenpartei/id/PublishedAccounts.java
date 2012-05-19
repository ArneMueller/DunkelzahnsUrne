package de.piratenpartei.id;

import java.io.IOException;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PublishedAccounts {
	private Document doc;
	
	private static final PublishedAccounts INSTANCE = new PublishedAccounts();
	
	private PublishedAccounts() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		dbf.setNamespaceAware(true); 
		try {
			dbf.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(Config.publishedAccountsSchema));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
		DocumentBuilder builder;
		try {
			builder = dbf.newDocumentBuilder();
			doc = builder.parse(Config.publishedAccounts.openStream());
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}  
	}
	
	public static PublishedAccounts getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Loads Key from Database
	 * @param hash
	 * @return
	 * @throws KeyException
	 */
	public KeyValue getKey(String hash) throws KeyException {
		// delete the last character which is always "=" and invalid for xml-IDs
		// attach a _ so it never starts with a number
		hash = "_"+hash.substring(0, hash.length()-1);
		KeyInfoFactory factory = KeyInfoFactory.getInstance();
		Element node = doc.getElementById(hash);
		if(node == null) {
			throw new KeyException("Es ist kein Schlüssel für"+hash+" eingetragen!");
		}
		NodeList nl = node.getElementsByTagNameNS(XMLSignature.XMLNS, "KeyInfo");
		if(nl.getLength() != 1) {
			throw new KeyException("Wrong Number of Keys contained for Account "+hash);
		}
		
		DOMStructure xmlStructure = new DOMStructure(nl.item(0));
		KeyInfo k;
		try {
			k = factory.unmarshalKeyInfo(xmlStructure);
		} catch (MarshalException e) {
			throw new KeyException("Der Eintrag für "+hash+" konnte nicht geladen werden!");
		}
		List<?> content = k.getContent();
		if(content.size() == 0) {
			throw new KeyException("Es ist kein Schlüssel für "+hash+" abgespeichert!");
		}
		if(content.size() > 1) {
			throw new KeyException("Es ist mehr als ein Schlüssel für "+hash+" abgespeichert!");
		}
		if(! (content.get(0) instanceof KeyValue) ) {
			throw new KeyException("Es ist zwar was für "+hash+" abgespeichert, dass ist aber kein Schlüssel!");
		}
		KeyValue kv = (KeyValue) content.get(0);
		return kv;
	}
	
	/**
	 * checks if the database has a valid entry for the hash
	 * @param hash
	 * @return
	 */
	public boolean hasKey(String hash) {
		// delete the last character which is always "=" and invalid for xml-IDs
		// attach a _ so it never starts with a number
		hash = "_"+hash.substring(0, hash.length()-1); 
		KeyInfoFactory factory = KeyInfoFactory.getInstance();
		Element node = doc.getElementById(hash);
		if(node == null) {
			return false;
		}
		NodeList nl = node.getElementsByTagNameNS(XMLSignature.XMLNS, "KeyInfo");
		if(nl.getLength() != 1) {
			return false;
		}
		
		DOMStructure xmlStructure = new DOMStructure(nl.item(0));
		KeyInfo k;
		try {
			k = factory.unmarshalKeyInfo(xmlStructure);
		} catch (MarshalException e) {
			return false;
		}
		List<?> content = k.getContent();
		if(content.size() == 0) {
			return false;
		}
		if(content.size() > 1) {
			return false;
		}
		if(! (content.get(0) instanceof KeyValue) ) {
			return false;
		}
		return true;
	}
}
