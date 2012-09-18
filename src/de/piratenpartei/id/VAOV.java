package de.piratenpartei.id;

import java.io.BufferedReader;
import java.io.IOException; 
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import org.json.simple.*;

/**
 * 
 * This class ist the top-level interface that should be used by GUIs. Data should be processed using only this class.
 * @author Dunkelzahn
 *
 */
public class VAOV {
	private TopicList inis;
	private Messenger m;
	public static final String readPath = "data.dat";
	
	/**
	 * 
	 * @throws IOException 
	 */
	public VAOV(Account a) throws IOException {
		this.buildTopicList();
		System.out.println("Building inis finished");
		
		m = new Messenger(a); // TODO
	}

	/**
	 * 
	 * @param topicID Index of the Topic to vote for
	 * @param vote 
	 * @param type
	 * @throws IOException
	 * @throws IllegalFormatException
	 * @throws KeyException
	 * @throws VerificationException
	 */
	public void vote(int topicID, Vote vote, String type) throws IOException, IllegalFormatException, KeyException, VerificationException{
		m.sendVote(topicID, vote, type);
	}
	
	public void buildTopicList() throws IOException{
		String s = "";
		s = loadInis();
		
		TextStore ts = new TextStore(s);

		String structure = (String) ts.get("structure");
		if(structure.equals("list")) inis = new TopicList((JSONObject) ts.get("data"));
		else throw new RuntimeException("Structure property in JOSN-file has unknown value:" + structure);		
	}
	
	public void addIniInNewTopic(Ini ini, ArrayList<String> tags){
		this.inis.addIniInNewTopic(ini, tags);
	}
	
	public void addIniToTopic(Ini ini, int topicIndex){
		this.inis.addIniToTopic(ini, topicIndex);
	}
	
	public String loadInis() throws IOException{
		BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(readPath));

		String s;
		String result = "";
		while((s = br.readLine()) != null) result += s;
		br.close();
		
		return result;
	}
	
}
