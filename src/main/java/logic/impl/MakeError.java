package logic.impl;

import java.util.ArrayList;
import java.util.List;

import logic.MakeMessage;
import logic.Parse;
import data.Message;

/**
 * {@link MakeMessage}の実装
 * エラーメールを作成するとき、この具象クラスを用いる。
 */
public class MakeError implements MakeMessage {
	
	private String error;
	
	public MakeError(String _error){
		error = _error;
	}

	/**
	 * @inheritDoc
	 * 基本的にパーズは行わないので、第2引数は<code>null</code>でも問題なし。
	 */
	@Override
	public List<Message> make(Message oMsg, Parse parse) {		
		List<Message> sMsgs = new ArrayList<Message>();		
		sMsgs.add(this.decideErr(oMsg));
		return sMsgs;
	}
	
	private Message decideErr(Message oMsg){
		Message sMsg = new Message();
		sMsg.setRecipient(oMsg.getSender());
		sMsg.setSender(oMsg.getSender());
		for(int i = 0; i < oMsg.getHeader().size(); i++)
			sMsg.addHeader(oMsg.getHeader().get(i));
		
		if (error == null) {
			
		} else switch (error) {
			case "syntax":
				sMsg = this.syntaxErrorMail(sMsg);
				break;
			case "noneprop":
				sMsg = this.nonePropError(sMsg);
				break;
			case "nonerecipient":
				sMsg = this.noneRecipientError(sMsg);
				break;
		}
		
		return sMsg;
	}
	
	private Message syntaxErrorMail(Message syntaxErrorMsg) {
		//シンタックスエラーメール作成
		
		syntaxErrorMsg.setSubject("FROM:RMX MTA");
		syntaxErrorMsg.addBody("syntax errors can be recognized.");
		syntaxErrorMsg.addBody("Please check your mail address." );
		syntaxErrorMsg.addBody(".");
		
		return syntaxErrorMsg;
	}
	
	private Message nonePropError(Message nonePropErrorMsg) {
		nonePropErrorMsg.setSubject("FROM:RMX MTA");
		nonePropErrorMsg.addBody("A suitable \".properties\" file does not exist.");
		nonePropErrorMsg.addBody("Please check your .properties file." );
		nonePropErrorMsg.addBody(".");
		
		return nonePropErrorMsg;
	}
	
	private Message noneRecipientError(Message noneRecipientErrorMsg) {
		noneRecipientErrorMsg.setSubject("FROM:RMX MTA");
		noneRecipientErrorMsg.addBody("An address does not exist. ");
		noneRecipientErrorMsg.addBody("Please check your mail address" );
		noneRecipientErrorMsg.addBody(".");
		
		return noneRecipientErrorMsg;
	}
}
