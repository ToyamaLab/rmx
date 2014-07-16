package logic.flow;

import java.util.ArrayList;
import java.util.ResourceBundle;

import presentation.mail.SendMailService;
import logic.bodyfunction.ContentsMatch;
import logic.parse.SOP.parserVisitor;
import logic.utils.FlowUtils;
import data.Message;
import data.PropFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferFlow {
	//メンバ変数
	private Message oMsg;
	private ResourceBundle domBundle;
	private ResourceBundle envBundle;
	private parserVisitor userInfo;
	private static final Logger log = LoggerFactory.getLogger(TransferFlow.class);
	private PropFile pf = PropFile.getInstance();
	
	//コンストラクタ
	public TransferFlow(Message oMsg, ResourceBundle domconfBundle, parserVisitor user_info) {
		this.oMsg = oMsg;
		this.envBundle = pf.getEnvBundle();
		this.domBundle = domconfBundle;
		this.userInfo = user_info;
	}
	
	
	public void startTransfer() {
		// 1. 送信用メッセージ
		ArrayList<Message> sMsgs = new ArrayList<Message>();
		
		// 2. コンテンツマッチを使用する場合
		//要修正
		ContentsMatch cm = new ContentsMatch();
		System.out.println("@@"+cm.checkErr(oMsg.getBody()));
		if(cm.checkUse(oMsg.getBody())) {
			if(!cm.checkErr(oMsg.getBody()))
				cm.getResults(oMsg.getBody(), domBundle);
		}
		
		// 3. 送信用メッセージを得る
		sMsgs = FlowUtils.getTransferMails(userInfo, domBundle, oMsg, /*cm*/null);
		System.out.println(sMsgs);
		// 4. メールの送信
		log.info("Mail:{} -> {}", oMsg.getSender(), oMsg.getRecipient());
		SendMailService sm = new SendMailService();
		for(int i=0;i<sMsgs.size();i++)
			sm.sendMail(sMsgs.get(i), envBundle);
		
	}

}
