package logic.error;

import data.Message;
/*
 * 受信メールがRMX形式およびプラグインにも対応していないとき
 */
public class ErrorMailService {
	
	public static Message syntaxErrorMail(Message oMsg) {
		//シンタックスエラーメール作成
		Message syntaxErrorMsg = new Message();
		syntaxErrorMsg.setRecipient(oMsg.getSender());
		syntaxErrorMsg.setSender(oMsg.getSender());
		syntaxErrorMsg.setSubject("FROM:RMX MTA");
		syntaxErrorMsg.addBody("syntax errors can be recognized.");
		syntaxErrorMsg.addBody("Please check your mail address." );
		syntaxErrorMsg.addBody(".");
		for(int i=0;i<oMsg.getHeader().size();i++) {
			syntaxErrorMsg.addHeader(oMsg.getHeader().get(i));
		}
		
		return syntaxErrorMsg;
	}
	
	public static Message nonePropError(Message oMsg) {
		Message nonePropErrorMsg = new Message();
		nonePropErrorMsg.setRecipient(oMsg.getSender());
		nonePropErrorMsg.setSender(oMsg.getSender());
		nonePropErrorMsg.setSubject("FROM:RMX MTA");
		nonePropErrorMsg.addBody("A suitable .properties file does not exist.");
		nonePropErrorMsg.addBody("Please check your .properties file." );
		nonePropErrorMsg.addBody(".");
		for(int i=0;i<oMsg.getHeader().size();i++) {
			nonePropErrorMsg.addHeader(oMsg.getHeader().get(i));
		}
		
		return nonePropErrorMsg;
	}
	
	public static Message noneRecipientError(Message oMsg) {
		Message noneRecipientErrorMsg = new Message();
		noneRecipientErrorMsg.setRecipient(oMsg.getSender());
		noneRecipientErrorMsg.setSender(oMsg.getSender());
		noneRecipientErrorMsg.setSubject("FROM:RMX MTA");
		noneRecipientErrorMsg.addBody("An address does not exist. ");
		noneRecipientErrorMsg.addBody("Please check your mail address" );
		noneRecipientErrorMsg.addBody(".");
		for(int i=0;i<oMsg.getHeader().size();i++) {
			noneRecipientErrorMsg.addHeader(oMsg.getHeader().get(i));
		}
		
		return noneRecipientErrorMsg;
	}
}
