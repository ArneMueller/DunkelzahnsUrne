package de.piratenpartei.id;

import java.util.*;

/**
 * 
 * @author artus
 *
 */
public class TopicList {
	/*
	 * The Categories from Liquid Feedback are implemented here as "tags". Categories are a special case of tags: every topic can only habe one tag.
	 */
	private ArrayList<String> tags;
	private ArrayList<TopicListTopic> topics;

	public TopicList(){
		this.tags = new ArrayList<String>();
		this.topics = new ArrayList<TopicListTopic>();
	}
	
	private void refreshTags(){
		for(int i=0; i<topics.size(); i++){
			ArrayList<String> tagList = topics.get(i).getTags();
			for(int k=0; k<tagList.size(); k++){
				String thisTag = tagList.get(k);
				if(!tags.contains(thisTag)){
					tags.add(thisTag);
				}
			}
		}
		Collections.sort(tags);
	}
	
	/**
	 * create a new Topic and add an Ini to it
	 * @param ini: the first Ini in the topic
	 */
	public void addIniInNewTopic(TopicListIni ini, ArrayList<String> tags){
		TopicListTopic t = new TopicListTopic();
		t.addIni(ini);
		for(int i=0; i<tags.size(); i++){
			t.addTag(tags.get(i));
		}
		this.topics.add(t);
		this.refreshTags();
	}
	
	/**
	 * Add an Ini to an existing Topic
	 * @param ini: the Ini to add
	 * @param TopicIndex: the index of the topic to add the Ini to
	 */
	public void addIniToTopic(TopicListIni ini, int TopicIndex){
		this.topics.get(TopicIndex).addIni(ini);
		this.refreshTags();
	}
	
	// default Setters and Getters
	public ArrayList<String> getCategories(){
		return this.tags;
	}
	public ArrayList<TopicListTopic> getTopics(){
		return this.topics;
	}
}
