package de.piratenpartei.id;

import java.security.PublicKey;

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
		publicKey = pa.getKey(hash);
		// check if this is the correct public key
		String test = Helper.computeHash(publicKey);
		if(!test.equals(hash)) {
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
		hash = Helper.computeHash(publicKey);
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
		// check if this is the correct public key
		String test;
		try {
			PublicKey pk = pa.getKey(hash);
			if(!pk.equals(publicKey)) {
				return false;
			}
			test = Helper.computeHash(publicKey);
		} catch (KeyException e) {
			throw new RuntimeException(e);
		}
		if(!test.equals(hash)) {
			return(false);
		}
		return true;
	}
	
	/**
	 * checks if the hash of the publick key has been published on the official list of legitimized keys.
	 * @return
	 */
	public boolean isVerified() {
		return VerifiedAccounts.getInstance().hasEntry(hash);
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
