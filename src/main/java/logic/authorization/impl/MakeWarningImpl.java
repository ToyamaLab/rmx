package logic.authorization.impl;

import data.Message;
import logic.authorization.MakeWarning;

public class MakeWarningImpl implements MakeWarning {
	
	/* 警告メール作成 */
	public Message makeWarningMessage(Message oMsg, String unauthorizedRule) {
		Message sMsg = new Message();

		sMsg.setRecipient(oMsg.getSender());
		sMsg.setSender(oMsg.getSender());
		for (int k = 0; k < oMsg.getHeader().size(); k++) {
			sMsg.addHeader(oMsg.getHeader().get(k));
		}
		sMsg.setSubject("RMX WARNING");
		sMsg.addBody(oMsg.getRecipient() + " is NOT authorized.");
		sMsg.addBody("Unauthorized Rule: "+ unauthorizedRule);
		sMsg.addBody(".");
		
		return sMsg;
	}
	
}