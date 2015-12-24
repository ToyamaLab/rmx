package logic.authorization;

import java.util.ArrayList;

import data.Message;

public interface MakeWarning {
	
	public Message makeWarningMessage(Message oMsg, ArrayList<String> unauthorizedRules);

}
