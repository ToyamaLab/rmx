package plugins.acc.list;

import java.util.ArrayList;
import java.util.ResourceBundle;

import plugins.acc.util.ListUtil;
import data.Message;

public class ListAcc {
	//メンバ変数
	private Message oMsg;
	private ResourceBundle domBundle;
	private ArrayList<String> recipietnts;
	private String user_name;
	private String content;
	
	//コンストラクタ
	public ListAcc(
			ArrayList<String> commandArgs,
			Message oMsg,
			ResourceBundle domBundle,
			ArrayList<String> recipients) {
		this.content = commandArgs.get(0);
		this.user_name = commandArgs.get(1);
		this.oMsg = oMsg;
		this.domBundle = domBundle;
		this.recipietnts = recipients;
	}
	
	public Message createReply() {
		Message sMsg = new Message();
		ArrayList<String> body;
		body = ListUtil.createListBody(oMsg.getSender(), user_name, domBundle);
		sMsg.setRecipient(oMsg.getSender());
		sMsg.setSender(oMsg.getSender());
		sMsg.setSubject("");
		for(int i=0;i<body.size();i++) {
			sMsg.addBody(body.get(i));
		}
		sMsg.addBody(".");
		for(int i=0;i<oMsg.getHeader().size();i++) {
			sMsg.addHeader(oMsg.getHeader().get(i));
		}
		return sMsg;
	}
}
