package de.piratenpartei.id;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Topic {
	private ArrayList<String> tags;
	private ArrayList<Ini> inis;
	
	public Topic(){
		this.init();
	}

	public void init(){
		this.tags = new ArrayList<String>();
		this.inis = new ArrayList<Ini>();
	}
	
	public Topic(JSONObject jo) {
		this.fromJSON(jo);
	}

	public void vote(Vote v){
		
	}
	
	public void addTag(String tag){
		this.tags.add(tag);
	}
	
	public void addIni(Ini ini){
		this.inis.add(ini);
	}
	
	// Setters and Getters
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	public ArrayList<Ini> getInis() {
		return inis;
	}
	public void setInis(ArrayList<Ini> inis) {
		this.inis = inis;
	}

	public void fromJSON(JSONObject jo){
		this.init();

		JSONArray inisArr = (JSONArray) jo.get("inis");
		for(int i=0; i<inisArr.size(); i++){
			inis.add(new Ini((JSONObject)inisArr.get(i)));
		}
		JSONArray tagsArr = (JSONArray) jo.get("tags");
		tags.addAll(tagsArr);
	}
	
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();
		JSONArray inisArr = new JSONArray();
		for(int i=0; i<this.inis.size(); i++){
			inisArr.add(this.inis.get(i).toJSON());
		}
		JSONArray tagsArray = new JSONArray();
		for(int i=0; i<this.tags.size(); i++){
			tagsArray.add(this.tags.get(i));
		}
		jo.put("inis", inisArr);
		jo.put("tags", tagsArray);
		return jo;
	}
}
