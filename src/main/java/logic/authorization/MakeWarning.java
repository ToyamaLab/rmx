package logic.authorization;

import data.Message;

public interface MakeWarning {
	
	public Message makeWarningMessage(Message oMsg, String unauthorizedRule);

}
