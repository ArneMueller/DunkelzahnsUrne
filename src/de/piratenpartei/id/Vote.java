package de.piratenpartei.id;

import net.sf.json.JSONObject;

public abstract class Vote {
	
	public abstract JSONObject toJSON();
	public abstract void setData(JSONObject jo);
}
