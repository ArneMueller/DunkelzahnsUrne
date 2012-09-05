package de.piratenpartei.id;

import java.io.IOException; 
import java.util.ArrayList;
import java.util.LinkedList;
import net.sf.json.*;

/**
 * 
 * This class ist the top-level interface that should be used by GUIs. Data should be processed using only this class.
 * @author Dunkelzahn
 *
 */
public class VAOV {
	private TopicList inis;
	private Messenger m;
	
	/**
	 * 
	 * @throws IOException 
	 */
	public VAOV(char[] accountName, char[] password) throws IOException {
		this.buildTopicList();
		System.out.println("Building inis finished");
		
		m = new Messenger(null); // TODO
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
		
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(s);

		String structure = (String) jo.get("structure");
		if(structure == "list") inis = (TopicList) jo.get("data");
		else throw new RuntimeException("Structure property in JOSN-file has unknown value.");		
	}
	
	public void addIniInNewTopic(TopicListIni ini, ArrayList<String> tags){
		this.inis.addIniInNewTopic(ini, tags);
	}
	
	public void addIniToTopic(TopicListIni ini, int topicIndex){
		this.inis.addIniToTopic(ini, topicIndex);
	}
	
	public String loadInis() throws IOException{
		return loadInisFromFile("data.dat");
	}
	
	public String loadInisFromFile(String path) throws IOException{
		LinkedList<String> list = new LinkedList<String>();
		
		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(path));
		String s;
		while((s = br.readLine()) != null) list.add(s);
		br.close();
		
		String result = "";
		while(!list.isEmpty()) result += list.pop();
		
		return result;
	}

}
