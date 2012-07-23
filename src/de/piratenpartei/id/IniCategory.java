package de.piratenpartei.id;
import java.util.LinkedList;


public class IniCategory implements TreeElement {
	private LinkedList<IniTopic> topics;
	private String caption;
	private static String idPrefix = "CAT";
	private int idcode;
	
	public IniCategory(int idcode){
		this.idcode = idcode;
	}
	
	@Override
	public LinkedList<String> getCaptions() {
		LinkedList<String> l = new LinkedList<String>();
		for (int i = 0; i < l.size(); i++) {
			l.add(this.topics.get(i).getCaption());
		}
		return l;
	}

	public Ini getIni(int topIndex, int iniIndex) {
		return this.topics.get(topIndex).getIni(iniIndex);
	}

	public IniTopic getTopic(int topIndex){
		return this.topics.get(topIndex);
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public String getID() {
		return idPrefix + String.valueOf(this.idcode);
	}

}
