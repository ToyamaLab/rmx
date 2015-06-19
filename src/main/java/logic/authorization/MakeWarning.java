package logic.authorization;

import java.util.ArrayList;

import data.Message;

public class MakeWarning {
	
	/* 警告メール作成 */
	public Message makeWarningMassage(Message oMsg, ArrayList<String> unauthorizedRules) {
		Message sMsg = new Message();
		
		String unauthorizedRule = new String();
		for (int i = 0; i < unauthorizedRules.size(); i++)
			unauthorizedRule = unauthorizedRule.concat(unauthorizedRules.get(i) + " ");
		System.out.println("[Unauthorized Rule] " + unauthorizedRule);	//
		
		sMsg.setRecipient(oMsg.getSender());
		sMsg.setSender(oMsg.getSender());
		for (int k = 0; k < oMsg.getHeader().size(); k++) {
			sMsg.addHeader(oMsg.getHeader().get(k));
		}
		sMsg.setSubject("RMX WARNING");
		sMsg.addBody("Unauthorized Rule: "+ unauthorizedRule);
		sMsg.addBody(".");
		
		return sMsg;
	}
	
}