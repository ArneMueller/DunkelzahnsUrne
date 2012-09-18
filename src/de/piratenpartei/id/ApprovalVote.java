package de.piratenpartei.id;

import java.util.List;

/**
 * 
 * Implementation of the Approval voting procedure.
 * An Approval Vote with list length 1 is equivalent to a simple yes/no vote.
 * @author Dunkelzahn
 *
 */
public class ApprovalVote implements Vote {
	private List<Boolean> votes;

	public List<Boolean> getVotes() {
		return votes;
	}

	public void setVotes(List<Boolean> votes) {
		this.votes = votes;
	}
	
}
