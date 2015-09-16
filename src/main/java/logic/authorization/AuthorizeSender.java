package logic.authorization;

public interface AuthorizeSender {
	
	public boolean isAuthorized();
	
	public String getUnauthorizedRulesStr();
	
}
