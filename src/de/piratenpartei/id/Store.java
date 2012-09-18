package de.piratenpartei.id;

public interface Store {
	public void put(String key, Object value);
	public void put(String key, Store value);
	public Object get(String key);
	public void fromString(String s);
	public String toString();
}
