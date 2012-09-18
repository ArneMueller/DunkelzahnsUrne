package de.piratenpartei.id;

import org.json.simple.*;

public class JSONSimpleStore implements Store {
	JSONObject store;
	
	public JSONSimpleStore() {
		store = new JSONObject();
	}
	
	@Override
	public void put(String key, Object value){
		store.put(key, value);
	}
	
	@Override
	public Object get(String key){
		return store.get(key);
	}
	
	@Override
	public void fromString(String s) {
		store = (JSONObject) JSONValue.parse(s);
	}

	@Override
	public String toString(){
		return store.toJSONString();
	}

	@Override
	public void put(String key, Store value) {
		store.put(key, value);
	}
	
}
