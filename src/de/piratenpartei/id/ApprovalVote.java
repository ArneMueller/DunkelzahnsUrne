package de.piratenpartei.id;

import java.util.LinkedList;

/**
 * 
 * Implementation of the Approval voting procedure.
 * An Approval Vote with list length 1 is equivalent to a simple yes/no vote.
 * @author Dunkelzahn
 *
 */
public class ApprovalVote implements Vote {
	private LinkedList<Boolean> votes;

	public LinkedList<Boolean> getVotes() {
		return votes;
	}

	public void setVotes(LinkedList<Boolean> votes) {
		this.votes = votes;
	}
	
}
