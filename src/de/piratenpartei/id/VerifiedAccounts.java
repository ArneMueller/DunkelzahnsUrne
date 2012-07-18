package de.piratenpartei.id;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

import org.apache.commons.codec.binary.Base64;

public class VerifiedAccounts {
	private HashSet<String> accounts;

	private static final VerifiedAccounts INSTANCE = createVerifiedAccounts();
	
	private static VerifiedAccounts createVerifiedAccounts() {
		try {
			return new VerifiedAccounts();
		} catch (VerificationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (IllegalFormatException e) {
			throw new RuntimeException(e);
		}
	}
	
	private VerifiedAccounts() throws VerificationException, IOException, IllegalFormatException {
		int line = 0;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream in = Config.verifiedAccounts.openStream();
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
			BufferedReader br = new BufferedReader(new InputStreamReader(bais, Config.CHARSET));
			String buff;
			while((buff = br.readLine()) != null) {
				String hash = Helper.read("Account", buff);
				accounts.add(hash);
				line++;
			}
		} catch (IOException e) {
			throw new IOException("[Line "+line+"] Failed to read",e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (IllegalFormatException e) {
			throw new IllegalFormatException("[Line "+line+"] "+e.getMessage(),e);
		}  
	}
	
	public static VerifiedAccounts getInstance() {
		return INSTANCE;
	}
	
	public boolean hasEntry(String hash) {
		return accounts.contains(hash);
	}
}
