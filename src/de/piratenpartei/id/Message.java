package de.piratenpartei.id;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Message {
	
	String message;
	Account author;
	
	/**
	 * Loads a message from a given input stream.
	 * @param in
	 * @throws IOException
	 * @throws KeyException
	 */
	public Message(InputStream in) throws IOException, IllegalFormatException, KeyException, VerificationException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in, Config.CHARSET));
		int line = 0;
		String digest;
		String signature;
		try {
			line++;
			String buffer = br.readLine();
			String authorString = Helper.read("Author", buffer);
			author = new Account(authorString);
			line++;
			buffer = br.readLine();
			digest = Helper.read("Digest", buffer);
			line++;
			buffer = br.readLine();
			signature = Helper.read("Signature", buffer);
			line++;
			StringBuffer str = new StringBuffer();
			while((buffer = br.readLine()) != null) {
				str.append(buffer+"\n"); // this also removes potential windows line-feeds!
				line++;
			}
			message = str.toString();
		}
		catch(IllegalFormatException ex) {
			throw new IllegalFormatException("[Line "+line+"] Failed to read", ex);
		}
		catch(IOException ex) {
			throw new IOException("[Line "+line+"] Failed to read", ex);
		}
		/*
		System.out.println("computing digest of:");
		System.out.println(message);
		System.out.println("EndMessage");
		*/
		String computed_digest = Helper.computeDigest(message);
		if(!computed_digest.equals(digest)) throw new VerificationException("Digest does not match to message. Message may be manipulated!");
		String computed_signature = Helper.computeSignature(digest, author.getPublicKey());
		if(!computed_signature.equals(signature)) throw new VerificationException("Siganture does not match to message. Message may be manipulated!");			
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * creates a new empty message
	 * @param author
	 */
	public Message(Account author) {
		this.author = author;
		this.message = "";
	}
	
	public Account getAuthor() {
		return author;
	}
	
	public void send(PrintWriter ps) throws KeyException {
		// make sure message ends with a new line
		if(!message.endsWith("\n")) {
			message = message + "\n";
		}
		String digest = Helper.computeDigest(message);
		String signature = Helper.computeSignature(digest, author.getPublicKey());
		ps.print("Author: "+Helper.computeHash(author.getPublicKey())+"\n");
		ps.print("Digest: "+digest+"\n");
		ps.print("Signature: "+signature+"\n");
		ps.print(message); // message already contains newline
	}
}
