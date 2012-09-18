package de.piratenpartei.id;

/**
 * A wrapper class for serializing data.
 * Uses JSONObject atm.
 * @author artus
 *
 */
public class TextStore {
	private Store store;
	
	private Store newStore() {
		return new JSONSimpleStore();
	}

	public TextStore() {
		store = newStore();
	}
	
	public TextStore(String s) {
		store = newStore();
		store.fromString(s);
	}
	
	public void put(String key, Object value){
		store.put(key, value);
	}
	
	public void put(String key, TextStore value){
		store.put(key, value);
	}
	
	public Object get(String key){
		return store.get(key);
	}
	
	public void fromString (String s) {
		store.fromString(s);
	}
	
	public String toString(){
		return store.toString();
	}
}
