package de.piratenpartei.id;

import java.util.*;
import net.sf.json.*;

/*
 * Implements a three-level tree, first level and second level
 * values are Strings, third level values are of type "Ini" 
 */

public class IniTree {
	private LinkedList<IniTreeCategory> categories;
	
	/**
	 * default constructor
	 * @param cats IniCategories to use
	 */
	public IniTree(LinkedList<IniTreeCategory> cats){
		this.categories = cats;
	}

	/**
	 * constructor with "setCategories(JSONObject)" included
	 * @param jo JSONObject to Parse
	 */
	public IniTree(JSONObject jo){
		int i=0;
		String key = "category" + String.valueOf(i);
		while(jo.has(key)){
			this.categories.add((IniTreeCategory) jo.get(key));
			jo.discard(key);
			i++;
			key = "category" + String.valueOf(i);
		}
	}

	public LinkedList<String> getCategoryCaptions() {
		LinkedList<String> l = new LinkedList<String>();
		for (int i = 0; i < l.size(); i++) {
			l.add(this.categories.get(i).getCaption());
		}
		return l;
	}
	
	public void AddIni(IniTreeIni ini, int cat, int top){
		this.categories.get(cat).addIni(ini, top);
	}

}
