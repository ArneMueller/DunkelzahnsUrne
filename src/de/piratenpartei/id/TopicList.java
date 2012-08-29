package de.piratenpartei.id;

import java.util.*;

import net.sf.json.*;

/**
 * 
 * @author artus
 *
 */
public class TopicList {
	private ArrayList<String> categories;
	private ArrayList<TopicListTopic> topics;

	public TopicList(){
		this.categories = new ArrayList<String>();
		this.topics = new ArrayList<TopicListTopic>();
	}
	
	public void addIniInNewTopic(TopicListIni ini){
		TopicListTopic t = new TopicListTopic();
		t.addIni(ini);
		this.topics.add(t);
	}
	
	public void addIniToTopic(TopicListIni ini, int TopicIndex){
		this.topics.get(TopicIndex).addIni(ini);
	}
	
	public ArrayList<String> getCategories(){
		return this.categories;
	}
	public ArrayList<TopicListTopic> getTopics(){
		return this.topics;
	}
}
