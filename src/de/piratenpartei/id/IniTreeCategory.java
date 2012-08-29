package de.piratenpartei.id;
import java.util.LinkedList;


public class IniTreeCategory implements TreeElement {
	private LinkedList<IniTreeTopic> topics;
	private String caption;
	private static String idPrefix = "CAT";
	private int idcode;
	
	public IniTreeCategory(int idcode){
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

	public IniTreeIni getIni(int topIndex, int iniIndex) {
		return this.topics.get(topIndex).getIni(iniIndex);
	}

	public IniTreeTopic getTopic(int topIndex){
		return this.topics.get(topIndex);
	}

	public String getCaption() {
		return caption;
	}

	@Override
	public String getID() {
		return idPrefix + String.valueOf(this.idcode);
	}

	public void addIni(IniTreeIni ini, int top) {
		this.topics.get(top).addIni(ini);		
	}

}
