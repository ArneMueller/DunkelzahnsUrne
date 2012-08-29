package de.piratenpartei.id;

import java.io.*;

import net.sf.json.JSONObject;

public class Messenger {
	private PrintWriter outputStream;
	private Account author;
	
	public Messenger(Account author){
		this.author = author;
	}
	
	public void message(String s) throws IOException, IllegalFormatException, KeyException, VerificationException{
		Message m = new Message(this.author);
		m.setMessage(s);
		m.send(this.outputStream);
	}
	public void sendVote(IniTreeTopic topic, Vote vote, String type) throws IOException, IllegalFormatException, KeyException, VerificationException{
		net.sf.json.JSONObject jo1 = new JSONObject();
		net.sf.json.JSONObject jo2 = new JSONObject();
		jo1.accumulate("type",type);
		jo1.accumulate("vote", vote.toJSON());
		jo1.accumulate("topic", topic.getID());
		jo2.accumulate("vote", jo1);
		this.message(jo2.toString());
	}
	public void sendMessageToUser(String userName, String message) throws IOException, IllegalFormatException, KeyException, VerificationException{
		net.sf.json.JSONObject jo1 = new JSONObject();
		net.sf.json.JSONObject jo2 = new JSONObject();
		jo1.accumulate("userName",userName);
		jo1.accumulate("message",message);
		jo2.accumulate("userMsg", jo1);
		this.message(jo2.toString());
	}
	public void sendNickchange(String newNick) throws IOException, IllegalFormatException, KeyException, VerificationException{
		net.sf.json.JSONObject jo1 = new JSONObject();
		net.sf.json.JSONObject jo2 = new JSONObject();
		jo1.accumulate("newNick", newNick);
		jo2.accumulate("nickChange", jo1);
		this.message(jo2.toString());
	}
}
