package de.piratenpartei.id;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class PrivateAccount extends Account {
	
	KeyPair keys;
	
	/**
	 * creates a new private Account.
	 */
	public PrivateAccount() throws KeyException {
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("DSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		kpg.initialize(1024);
		keys = kpg.generateKeyPair();
		init(keys.getPublic());
	}
	
	public PrivateKey getPrivateKey() {
		return keys.getPrivate();
	}
	
	public KeyInfo getKeyInfo() throws KeyException {
		KeyInfoFactory kif = KeyInfoFactory.getInstance();
		KeyValue kv;
		try {
			kv = kif.newKeyValue(publicKey);
		} catch (java.security.KeyException e) {
			throw new KeyException("failed to write publicKey into XML", e);
		}
		List<KeyValue> content = new ArrayList<KeyValue>();
		content.add(kv);
		KeyInfo ki = kif.newKeyInfo(content);
		
		return ki;
	}
	
	/**
	 * publishes the public key, i.e. registers the Account.
	 */
	public void publish() throws KeyException {
		Message m = new Message();
		KeyInfo ki = getKeyInfo();
		DOMStructure dom = new DOMStructure(m.getBody());
		try {
			ki.marshal(dom, null);
		} catch (MarshalException e) {
			throw new KeyException("Konnte Schlüssel nicht schreiben");
		}
		m.sign(this);
		m.send();
		try {
			m.verify();
		} catch (VerificationException e) {
			throw new KeyException("Die Eigene Signatur wird nicht verifiziert!",e);
		}
		m.send();
	}
}
