package de.piratenpartei.id;

import java.util.*;

/**
 * 
 * Implementation of the Schulze voting procedure.
 * votes = { index of ini with highest priority , index of ini with second-highest priority , ... }
 * @author artus
 *
 */
public class SchulzeVote implements Vote {
	private List<Integer> votes;

	public List<Integer> getVotes() {
		return votes;
	}

	public void setVotes(List<Integer> votes) {
		this.votes = votes;
	}
	
	

}
