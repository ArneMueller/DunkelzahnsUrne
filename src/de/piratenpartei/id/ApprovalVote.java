package de.piratenpartei.id;

import java.util.LinkedList;

import net.sf.json.JSONObject;

public class ApprovalVote extends Vote {
	LinkedList<Boolean> votes;
	
	@Override
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();
		for(int i=0; i<votes.size(); i++){
			jo.accumulate("vote" + String.valueOf(i), votes.get(i));
		}
		return jo;
	}

	@Override
	public void setData(JSONObject jo) {
		int i=0;
		while(!jo.isEmpty()){
			String key = "vote" + String.valueOf(i);
			votes.add(jo.getBoolean(key));
			jo.discard(key);
			i++;
		}
	}
}
