package de.piratenpartei.id;

import java.util.*;

public class IniTreeTopic implements TreeElement {
	private LinkedList<IniTreeIni> inis;
	private String caption;
	private int idcode;
	private int status;	// 0: closed; 1: discussion; 2: frozen; 3: voting
	private static String idPrefix = "INI";

	public IniTreeTopic(int idcode){
		this.idcode = idcode;
	}
	@Override
	public LinkedList<String> getCaptions() {
		LinkedList<String> l = new LinkedList<String>();
		for (int i = 0; i < l.size(); i++) {
			l.add(this.inis.get(i).getCaption());
		}
		return l;
	}

	public LinkedList<IniTreeIni> getInis() {
		return inis;
	}

	public void setInis(LinkedList<IniTreeIni> inis) {
		this.inis = inis;
	}

	public IniTreeIni getIni(int iniIndex) {
		return this.inis.get(iniIndex);
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
	public void addIni(IniTreeIni ini) {
		this.inis.add(ini);
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
