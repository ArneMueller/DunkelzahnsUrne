package de.piratenpartei.id;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

public class PrivateAccount extends Account {
	
	KeyPair keys;
	
	/**
	 * creates a new private Account.
	 */
	public PrivateAccount() throws KeyException {
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		kpg.initialize(4096);
		keys = kpg.generateKeyPair();
		init(keys.getPublic());
	}
	
	public PrivateKey getPrivateKey() {
		return keys.getPrivate();
	}
	
	/**
	 * publishes the public key, i.e. registers the Account.
	 */
	public void publish() throws KeyException, IOException {
		Message m = new Message(this);
		StringBuilder builder = new StringBuilder();
		builder.append("Publish Account\n");
		builder.append("Hash: ");
		builder.append(Helper.computeHash(getPublicKey()));
		builder.append("\n");
		Helper.writePublicKey(builder, getPublicKey());
		m.setMessage(builder.toString());
		
		/*
		URLConnection conn = Config.publishAccount.openConnection();
		OutputStream out = conn.getOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, Config.CHARSET));
		m.send(pw);
		*/
		m.send(System.out);
	}
}
