package de.piratenpartei.id;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.HashMap;

/**
 * This class manages the list of published accounts.
 * 
 * Currently this list is loaded statically. This should be changed in the future.
 * 
 * @author arne
 *
 */
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
			BufferedReader br = new BufferedReader(new InputStreamReader(Config.getPublishedAccounts().openStream(), Config.CHARSET));
			String buffer = br.readLine();
			String server_hash = Helper.read("Account-Server", buffer);
			line++;
			buffer = br.readLine();
			String server_modulus = Helper.read("Modulus", buffer);
			line++;
			buffer = br.readLine();
			String server_exponent = Helper.read("Exponent", buffer);
			line++;
			buffer = br.readLine();
			String digest = Helper.read("Digest", buffer);
			line++;
			buffer = br.readLine();
			String signature = Helper.read("Signature", buffer);
			line++;
			
			PublicKey server_key = Helper.readPublicKey(server_modulus, server_exponent);
			if(!server_hash.equals(Config.getAccountServer())) {
				throw new IllegalFormatException("Presented hash of the Account-Server does not match to the internally stored hash of the server");
			}
			try {
				Helper.verifyKey(server_key, server_hash);
			} catch (KeyException e1) {
				throw new IllegalFormatException("Key of the Account-Server does not match to the hash of the server");
			}
			try {
				Helper.verifySignature(digest, signature, server_key);
			} catch (KeyException e1) {
				throw new IllegalFormatException("Signature of the registered accounts list does not decode to digest!");
			}
			
			StringBuilder listText = new StringBuilder();
			
			while((buffer = br.readLine()) != null) {
				try {
					listText.append(buffer + "\n");
					String hash = Helper.read("Hash", buffer);
					line++;
					buffer = br.readLine();
					listText.append(buffer + "\n");
					if(buffer == null) throw new IllegalFormatException("[Line "+line+"] Expected \"Modulus:\"");
					String modulus = Helper.read("Modulus", buffer);
					line++;
					buffer = br.readLine();
					listText.append(buffer + "\n");
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
			
			String computed_digest = Helper.computeDigest(listText.toString());
			if(!digest.equals(computed_digest)) {
				//TODO: comment the following line in as soon as we have a proper server.
				//throw new IllegalFormatException("Digest does not match to list. Maybe the list got manipulated?");
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
