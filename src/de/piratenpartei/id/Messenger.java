package de.piratenpartei.id;

import java.io.*;

public class Messenger {
	private PrintWriter outputStream;
	private Account author;
	private Message m;
	
	public Messenger(Account author){
		m = new Message(author);
	}
	
	public void message(String s) throws IOException, IllegalFormatException, KeyException, VerificationException{
		this.m.setMessage(s);
		this.m.send(this.outputStream);
	}
	public void sendVote(IniTopic topic, String vote, String type) throws IOException, IllegalFormatException, KeyException, VerificationException{
		this.message("vote:{type:\"" + type + "\",topic:\"" + topic.getID() + "\",vote:\"" + vote + "\"");
	}
	public void sendMessageToUser(String userName, String message) throws IOException, IllegalFormatException, KeyException, VerificationException{
		this.message("userMsg:{to:\"" + userName + "\",message:\"" + message + "\"}");
	}
	public void sendNickchange(String newNick) throws IOException, IllegalFormatException, KeyException, VerificationException{
		this.message("nickChange:{newNick:\"" + newNick + "\"}");
	}
}
