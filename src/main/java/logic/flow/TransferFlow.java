package logic.flow;

import java.util.ArrayList;
import java.util.ResourceBundle;

import presentation.mail.SendMailService;
import logic.RmxController;
import logic.parse.User;
import logic.parse.SOP.parserVisitor;
import logic.service.FlowService;
import data.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferFlow {
	//
	private Message oMsg;
	private ResourceBundle domconfBundle;
	private ResourceBundle envBundle;
	private parserVisitor user_info;
	private static final Logger log = LoggerFactory.getLogger(RmxController.class);
	
	//
	public TransferFlow(Message oMsg, ResourceBundle domconfBundle, ResourceBundle envBundle, parserVisitor user_info) {
		this.oMsg = oMsg;
		this.envBundle = envBundle;
		this.domconfBundle = domconfBundle;
		this.user_info = user_info;
	}
	
	public void startTransfer() {
		//送信用メッセージ
		ArrayList<Message> sMsgs = new ArrayList<Message>();
		sMsgs = FlowService.getTransferMails(user_info, domconfBundle, oMsg);
		
		//メールの送信
		log.info("Mail:{} -> {}", oMsg.getSender(), oMsg.getRecipient());
		SendMailService sm = new SendMailService();
		for(int i=0;i<sMsgs.size();i++)
			sm.sendMail(sMsgs.get(i), envBundle);
	}

}
