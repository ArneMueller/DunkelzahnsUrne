package de.piratenpartei.id;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * A signed message that is used to communicate with any kind of service.
 * Every Message is associated to an {@link Account}. The account supplies the key for verifying the message.
 * 
 * Messages that were created by a {@link PrivateAccount} can be send.
 * The sending process adds a signature to the message text.
 * 
 * @author arne
 *
 */
public class Message {
	
	/** The message text */
	String message;
	/** The author of the message */
	Account author;
	
	/**
	 * Loads a message from a given input stream.
	 * Use this for received messages.
	 * 
	 * @param in to load the message
	 * @throws IOException if the stream could not be read properly.
	 * @throws KeyException if something is fishy with the key.
	 * @throws IllegalFormatException if the input data has the wrong format.
	 * @throws VerificationException if the signature of the message could not be validated.
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
		boolean verified = Helper.verifySignature(digest, signature, author.getPublicKey());
		if(!verified) throw new VerificationException("Siganture does not match to message. Message may be manipulated!");			
	}
	
	/**
	 * Fetches the message text.
	 * @return the message text.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the message text (without signature information etc.)
	 * @param message the new message text.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Creates a new empty message.
	 * If the author is a {@link PrivateAccount}, the message can be send.
	 * @param author
	 */
	public Message(Account author) {
		this.author = author;
		this.message = "";
	}
	
	/**
	 * Returns the author of the message.
	 * @return the author
	 */
	public Account getAuthor() {
		return author;
	}
	
	/**
	 * Sends the message. To be precise, writes the content of the message to a PrintWriter which is then responsible for sending the message to its destination.
	 * Make sure that the PrintWriter is writing in UTF8!
	 * 
	 * @param ps the PrintWriter to write the message to.
	 * @throws KeyException if the author has not a private key, i.e. if the author is not a {@link PrivateAccount}
	 */
	public void send(PrintWriter ps) throws KeyException {
		if(!(author instanceof PrivateAccount)) throw new KeyException("You cannot sign other peoples messages!");
		PrivateAccount privateauthor = (PrivateAccount) author;
		// make sure message ends with a new line
		if(!message.endsWith("\n")) {
			message = message + "\n";
		}
		String digest = Helper.computeDigest(message);
		String signature = Helper.computeSignature(digest, privateauthor.getPrivateKey());
		ps.print("Author: "+Helper.computeHash(author.getPublicKey())+"\n");
		ps.print("Digest: "+digest+"\n");
		ps.print("Signature: "+signature+"\n");
		ps.print(message); // message already contains newline
	}
}
