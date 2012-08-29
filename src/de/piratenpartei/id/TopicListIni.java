package de.piratenpartei.id;

public class TopicListIni {
	private String caption;
	private String text;
	
	public TopicListIni(String caption, String text){
		this.caption = caption;
		this.text = text;
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
}
