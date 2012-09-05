package de.piratenpartei.id;

import java.util.*;

/**
 * 
 * Implementation of the Schulze voting procedure.
 * votes = { index of ini with highest priority , index of ini with second-highest priority , ... }
 * @author artus
 *
 */
public class SchulzeVote extends Vote {
	private ArrayList<Integer> votes;

	public ArrayList<Integer> getVotes() {
		return votes;
	}

	public void setVotes(ArrayList<Integer> votes) {
		this.votes = votes;
	}
	
	

}
