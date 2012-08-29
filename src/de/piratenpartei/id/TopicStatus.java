package de.piratenpartei.id;

public enum TopicStatus {
	CLOSED(0),
	NEW(1),
	DISCUSSION(2),
	FROZEN(3),
	VOTING(4);
	int code;
	private TopicStatus(int code){
		this.code = code;
	}
}
