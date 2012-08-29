package de.piratenpartei.id;

import java.util.ArrayList;

public class TopicListTopic {
	private ArrayList<String> tags;
	private ArrayList<TopicListIni> inis;
	
	public TopicListTopic(){
		this.tags = new ArrayList<String>();
		this.inis = new ArrayList<TopicListIni>();
	}

	public void addTag(String tag){
		this.getTags().add(tag);
	}
	public void addIni(TopicListIni ini){
		this.getInis().add(ini);
	}
	
	// Setters and Getters
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	public ArrayList<TopicListIni> getInis() {
		return inis;
	}
	public void setInis(ArrayList<TopicListIni> inis) {
		this.inis = inis;
	}
}
