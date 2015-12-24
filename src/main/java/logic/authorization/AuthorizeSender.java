package logic.authorization;

import java.util.ArrayList;

public interface AuthorizeSender {
	
	public boolean isAuthorized();
	
	public ArrayList<String> getUnauthorizedRules();
	
}
