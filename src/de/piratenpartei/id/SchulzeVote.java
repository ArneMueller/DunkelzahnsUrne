package de.piratenpartei.id;

import net.sf.json.JSONObject;
import java.util.*;

public class SchulzeVote extends Vote {
	LinkedList<Integer> votes;
	
	@Override
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();
		for(int i=0; i<this.votes.size(); i++){
			jo.accumulate("vote" + String.valueOf(i), votes.get(i));
		}
		return jo;
	}

	@Override
	public void setData(JSONObject jo) {
		int i=0;
		while(!jo.isEmpty()){
			String key = "vote" + String.valueOf(i); 
			jo.get(key);
			jo.discard(key);
			i++;
		}
	}

}
