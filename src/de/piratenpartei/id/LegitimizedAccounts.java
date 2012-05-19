package de.piratenpartei.id;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LegitimizedAccounts {
	private Document doc;
	private Node body;

	private static final LegitimizedAccounts INSTANCE = new LegitimizedAccounts();
	
	private LegitimizedAccounts() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		dbf.setNamespaceAware(true);
		try {
			dbf.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(Config.legitimatedAccountsSchema));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
		DocumentBuilder builder;
		try {
			//TODO: set Schema
			builder = dbf.newDocumentBuilder();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream in = Config.legitimatedAccounts.openStream();
			int read = 0;
			byte[] buffer = new byte[1024]; // read 1KB chunks
			do {
				read = in.read(buffer);
				if(read != -1) baos.write(buffer, 0, read);
			} while(read != -1); // -1 indicates EOF
			
			// check if the file is the correct one
			MessageDigest md = MessageDigest.getInstance(Config.HASH_ALGORITHM);
			byte[] digest = md.digest(baos.toByteArray());
			String base64 = Base64.encodeBase64String(digest);
			System.out.println(base64);
			if(!base64.equals(Config.legitimateChecksum)) {
				throw new VerificationException("Checksumme für Liste mit legitimierten Accounts stimmt nicht!");
			}
			
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			doc = builder.parse(bais);
			NodeList nl = doc.getElementsByTagNameNS(Config.legitimateNamespace, "Accounts");
			if(nl.getLength() == 0) {
				throw new IOException("Liste enthält keine Accounts");
			}
			if(nl.getLength() > 1) {
				throw new IOException("Bereich für Account-Liste nicht wohldefiniert!");
			}
			body = nl.item(0);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (VerificationException e) {
			throw new RuntimeException(e);
		}  
	}
	
	public static LegitimizedAccounts getInstance() {
		return INSTANCE;
	}
	
	public boolean hasEntry(String hash) {
		// delete the last character which is always "=" and invalid for xml-IDs
		// attach a _ so it never starts with a number
		hash = "_"+hash.substring(0, hash.length()-1);
		Element e = doc.getElementById(hash);
		return( e != null);
	}
}
