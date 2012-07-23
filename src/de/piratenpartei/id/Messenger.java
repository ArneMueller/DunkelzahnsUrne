package de.piratenpartei.id;

import java.io.*;

public class Messenger {
	PrintWriter outputStream;
	private void message(String s) throws IOException, IllegalFormatException, KeyException, VerificationException{
		ByteArrayInputStream is = new java.io.ByteArrayInputStream(s.getBytes());
		Message m = new Message(is);
		m.send(this.outputStream);
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
