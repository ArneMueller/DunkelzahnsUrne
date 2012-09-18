package de.piratenpartei.id;

import org.json.simple.JSONObject;

public class Ini {
	private String caption;
	private String text;
	
	public Ini(String caption, String text){
		this.caption = caption;
		this.text = text;
	}
	
	public Ini(JSONObject jo) {
		this.fromJSON(jo);
	}

	//Setters and Getters
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public void fromJSON(JSONObject jo){
		caption = (String) jo.get("caption");
		text = (String) jo.get("text");
	}
	
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();
		jo.put("caption", this.caption);
		jo.put("text", this.text);
		return jo;
	}
}
