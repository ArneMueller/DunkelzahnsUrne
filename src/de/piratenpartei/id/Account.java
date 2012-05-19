package de.piratenpartei.id;

import java.net.URL;
import java.security.*;

import javax.xml.crypto.dsig.keyinfo.KeyValue;


import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;

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
		byte[] val = d.digest(publicKey.getEncoded());
		Base32 enc = new Base32();
		String base32 = enc.encodeAsString(val);
		if(!hash.equals(base32)) {
			throw new KeyException("Schlüssel passt nicht zu hash!");
		}
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
		byte[] val = d.digest(publicKey.getEncoded());
		Base32 enc = new Base32();
		hash = enc.encodeAsString(val);
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
		byte[] val = d.digest(publicKey.getEncoded());
		Base32 enc = new Base32();
		String base32 = enc.encodeAsString(val);
		if(!hash.equals(base32)) {
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
