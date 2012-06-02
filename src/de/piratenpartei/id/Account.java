package de.piratenpartei.id;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.security.*;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Account {

	/** public key of the account */
	protected PublicKey publicKey;
	
	/** hash of the account */
	protected String hash;
	
	/**
	 * performs a lookup in the official list and retrieves corresponding key
	 * @param hash
	 */
	public Account(String hash) throws KeyException {
		this.hash = hash;
		PublishedAccounts pa = PublishedAccounts.getInstance();
		KeyValue kv = pa.getKey(hash);
		try {
			publicKey = kv.getPublicKey();
		} catch (java.security.KeyException e) {
			throw new KeyException("Konnte Schlüssel nicht laden", e);
		}
		// check if this is the correct public key
		MessageDigest d;
		try {
			d = MessageDigest.getInstance(Config.HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] val = d.digest(keyData());
		String encoded = encode(val);
		if(!hash.equals(encoded)) {
			throw new KeyException("Schlüssel passt nicht zu hash!");
		}
	}
	
	public byte[] keyData() throws KeyException {
		try {
			XMLSignatureFactory sf = XMLSignatureFactory.getInstance();
			KeyValue kv = sf.getKeyInfoFactory().newKeyValue(publicKey);
			CanonicalizationMethod c = sf.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null);
			
			
			KeyInfo ki = sf.getKeyInfoFactory().newKeyInfo(Collections.singletonList(kv));
			
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			DOMStructure root = new DOMStructure(doc);
			ki.marshal(root, null);
			
			/*NodeSetData nsd = new NodeSetData() {
				public Iterator<Node> iterator() {
					Iterator<Node> i = new Iterator<Node> () {

						boolean unused = true;
						@Override
						public boolean hasNext() {
							return unused;
						}

						@Override
						public Node next() {
							unused = false;
							Node n = doc.getElementsByTagName("KeyValue").item(0); // should always work (by spec of KeyInfo)
							return n;
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
						
					};
					return i;
				}
			};*/
			
			
			Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
			//Node n = ((DOMStructure) kv).getNode();
			Node n = doc.getElementsByTagName("KeyValue").item(0); // should always work (by spec of KeyInfo)
			return canon.canonicalizeSubtree(n);
		} catch (java.security.KeyException e) {
			throw new KeyException("Hash-Berechnung fehlgeschlagen",e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Hash-Berechnung fehlgeschlagen",e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException("Hash-Berechnung fehlgeschlagen",e);
		} catch (InvalidCanonicalizerException e) {
			throw new RuntimeException("Hash-Berechnung fehlgeschlagen",e);
		} catch (CanonicalizationException e) {
			throw new KeyException("Hash-Berechnung fehlgeschlagen",e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Hash-Berechnung fehlgeschlagen",e);
		} catch (MarshalException e) {
			throw new RuntimeException("Hash-Berechnung fehlgeschlagen",e);
		}
	}
	
	public static String encode(byte[] hash) {
		Base32 enc = new Base32();
		String base32 = enc.encodeAsString(hash);
		return "_"+base32.replace('=', '_');
	}
	
	/**
	 * Only for PrivateAccount construction.
	 * {@link #init(PublicKey)} must be called immediatly after the key has been constructed. 
	 */
	protected Account() {
	}
	
	protected void init(PublicKey pk) throws KeyException {
		publicKey = pk;
		MessageDigest d;
		try {
			d = MessageDigest.getInstance(Config.HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] val = d.digest(keyData());
		hash = encode(val);
	}
	
	/**
	 * checks if the public key of this account has been published on the official Account list.
	 * @return true if it has been published
	 */
	public boolean isPublished() {
		PublishedAccounts pa = PublishedAccounts.getInstance();
		if(!pa.hasKey(hash)) {
			return false;
		}
		KeyValue kv;
		try {
			kv = pa.getKey(hash);
		} catch (KeyException e) {
			return false;
		}
		PublicKey pk;
		try {
			pk = kv.getPublicKey();
		} catch (java.security.KeyException e) {
			return false;
		}
		if(!pk.equals(publicKey)) {
			return false;
		}
		// check if this is the correct public key
		MessageDigest d;
		try {
			d = MessageDigest.getInstance(Config.HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] val;
		try {
			val = d.digest(keyData());
		} catch (KeyException e) {
			return false;
		}
		String encoded = encode(val);
		if(!hash.equals(encoded)) {
			return false;
		}
		return true;
	}
	
	/**
	 * checks if the hash of the publick key has been published on the official list of legitimized keys.
	 * @return
	 */
	public boolean isLegitimized() {
		return LegitimizedAccounts.getInstance().hasEntry(hash);
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	/**
	 * returns hash of the public key of this account.
	 * @return
	 */
	public String getHash() {
		return hash;
	}
}
