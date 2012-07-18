package de.piratenpartei.id;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.HashMap;

public class PublishedAccounts {
	private HashMap<String, PublicKey> accounts;
	
	private static final PublishedAccounts INSTANCE = createPublishedAccounts();
	
	private static PublishedAccounts createPublishedAccounts() {
		try {
			return new PublishedAccounts();
		}
		catch(IllegalFormatException ex) {
			throw new RuntimeException(ex);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Loads list of published accounts from a file (possibly fetched from the internet).
	 * The file has to have entries of the form: <br>
	 * Hash: <hash of RSA key 1> <br>
	 * Modulus: <modulus of the RSA key 1> <br>
	 * Exponent: <public exponent of the RSA key 1> <br>
	 * Hash: <hash of RSA key 2> <br>
	 * Modulus: <modulus of the RSA key 2> <br>
	 * Exponent: <public exponent of the RSA key 2> <br>
	 * Extra lines are not allowed
	 * @throws IllegalFormatException
	 */
	private PublishedAccounts() throws IllegalFormatException, IOException {
		accounts = new HashMap<String,PublicKey>();
		int line = 1;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Config.publishedAccounts.openStream(), Config.CHARSET));
			String buffer;
			while((buffer = br.readLine()) != null) {
				try {
					String hash = Helper.read("Hash", buffer);
					line++;
					buffer = br.readLine();
					if(buffer == null) throw new IllegalFormatException("[Line "+line+"] Expected \"Modulus:\"");
					String modulus = Helper.read("Modulus", buffer);
					line++;
					buffer = br.readLine();
					if(buffer == null) throw new IllegalFormatException("[Line "+line+"] Expected \"Exponent:\"");
					String exponent = Helper.read("Exponent", buffer);
					PublicKey pk = Helper.readPublicKey(modulus,  exponent);
					//Helper.verifyKey(pk, hash); //we do this later, may reject complete list
					accounts.put(hash, pk);
					line++;
				} catch (IllegalFormatException e) {
					throw new IllegalFormatException("[Line "+line+"] "+e.getMessage(), e);
				}
			}
		} catch (IOException e) {
			throw new IOException("[Line "+line+"] Failed to read", e);
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
	public PublicKey getKey(String hash) throws KeyException {
		PublicKey pk = accounts.get(hash);
		if(pk == null) throw new KeyException("Key is not published: "+hash);
		return pk;
	}
	
	/**
	 * checks if the database has a valid entry for the hash
	 * @param hash
	 * @return
	 */
	public boolean hasKey(String hash) {
		return accounts.containsKey(hash);
	}
}
