package de.piratenpartei.id;

import java.util.*;
import net.sf.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/*
 * Implements a three-level tree, first level and second level
 * values are Strings, third level values are of type "Ini" 
 */

public class IniTree {
	private LinkedList<IniCategory> categories;
	
	/**
	 * default constructor
	 */
	public IniTree(){
		this.categories = null;
	}

	/**
	 * constructor with "setCategories(LinkedList<IniCategory>)" included
	 * @param val parameter given to setInis
	 */
	public IniTree(LinkedList<IniCategory> val){
		this.setCategories(val);
	}

	/**
	 * constructor with "setCategories(String)" included
	 * @param val parameter given to setInis
	 */
	public IniTree(String val){
		net.sf.json.JSONObject jo = (JSONObject) JSONSerializer.toJSON(val);
		
	}

	public LinkedList<String> getCaptions() {
		LinkedList<String> l = new LinkedList<String>();
		for (int i = 0; i < l.size(); i++) {
			l.add(this.categories.get(i).getCaption());
		}
		return l;
	}

	/**
	 * Returns the IniTopic at Index topIndex in the IniCategory at catIndex
	 * @param catIndex index of the IniCategory of the return value
	 * @param topIndex index of the return value in its IniCategory
	 * @return the found IniTopic
	 */
	public IniTopic getTopic(int catIndex, int topIndex){
		return this.categories.get(catIndex).getTopic(topIndex);
	}
	
	/**
	 * Returns the IniTopic at Index topIndex in the IniCategory at catIndex
	 * @param catIndex index of the IniCategory of the return value
	 * @param topIndex index of the IniTopic of the return value
	 * @param iniIndex index of the return value in its IniTopic
	 * @return the found Ini
	 */
	public Ini getIni(int catIndex, int topIndex, int iniIndex){
		return this.categories.get(catIndex).getIni(topIndex,iniIndex);
	}
	
	public LinkedList<IniCategory> getCategories() {
		return categories;
	}

	public void setCategories(LinkedList<IniCategory> inis) {
		this.categories = inis;
	}
	
}
