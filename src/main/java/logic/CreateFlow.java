package logic;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logic.error.ErrorMailService;
import logic.parse.User;
import logic.plugin.GetSendMailsOfFunction;
import logic.propfile.PropFileService;
import logic.service.GetSendMails;
import logic.bodyfunction.*;
import presentation.mail.IncomingMailService;
import presentation.mail.SendMailService;
import dao.PropfileDao;
import data.Message;

public class CreateFlow implements Runnable{
	//メンバ変数
	private Socket socket;
	private ResourceBundle envBundle;
	private static final Logger log = LoggerFactory.getLogger(RmxController.class);
	
	public CreateFlow(Socket socket, ResourceBundle envBundle) {
		this.socket = socket;
		this.envBundle = envBundle;
	}

	@Override
	public void run() {
		this.rmxFlow();
	}
	
	public void rmxFlow() {
		try {
			//送られてきたメールをオブジェクトとして得る
			Message oMsg = new Message();
			IncomingMailService icm = new IncomingMailService(socket);
			oMsg = icm.getMessage();
			
			//送信用メッセージ
			ArrayList<Message> sMsgs = new ArrayList<Message>();
			
			//エラー用メッセージ
			Message errorMsg = new Message();
			
			//送られてきたメールのドメインから使用する.propertiesファイル名を得る
			PropFileService pfs = new PropFileService();
			String domconfPropFileName = pfs.getPropFileName(oMsg.getRecipient());
			
			//poropertiesファイル名が得られなかったとき
			if(domconfPropFileName==null) {
				errorMsg = ErrorMailService.nonePropError(oMsg);
				SendMailService sm = new SendMailService();
				sm.sendMail(errorMsg, envBundle);
				log.info("Error Mail \"nonePropError\" is sending to {}", errorMsg.getRecipient());
				
			}
			
			//得られたときはpropertiesファイルをオブジェクトとして得る
			ResourceBundle domconfBundle = PropfileDao.readPropFile(domconfPropFileName);
			
			//メールの宛先にSOPを通す
			User user = new User();
			user.UserStart(oMsg.getRecipient(), domconfBundle);
			
			//RMX形式の場合 ex)team{rmx}+grade{3}@test.keio.com
			//normalFlgがtrue
			if(user.getNormalFlg()) {
				ContentsMatch cm = new ContentsMatch();
				GetSendMails gsm = new GetSendMails(user, domconfBundle, oMsg);
				//BodyFunctionを使用している時
				if(cm.checkUse(oMsg.getBody())){
					if(!cm.checkErr(oMsg.getBody())){
						cm.getResults(oMsg.getBody(), domconfBundle);
						gsm.setCm(cm);
					}
				}
				sMsgs = gsm.getSendMails();
			}
			//#functionの場合 ex)#event.attend#team{rmx}@test.keio.com
			//functionFlgがtrue
			else if(user.getFunctionFlg()) {
				GetSendMailsOfFunction gsmf = new GetSendMailsOfFunction(user, domconfBundle, oMsg,domconfPropFileName);
				sMsgs = gsmf.getSendMailsOfFunction();
			}
			//それ以外は文法エラー
			else {
				errorMsg = ErrorMailService.syntaxErrorMail(oMsg);
				SendMailService sm = new SendMailService();
				sm.sendMail(errorMsg, envBundle);
				log.info("Error Mail \"syntaxError\" is sending to {}", errorMsg.getRecipient());
			}
			
			//メッセージが0件の時
			if(sMsgs.isEmpty()) {
				errorMsg = ErrorMailService.noneRecipientError(oMsg);
				SendMailService sm = new SendMailService();
				sm.sendMail(errorMsg, envBundle);
				log.info("Error Mail \"noneRecipient\" is sending to {}", errorMsg.getRecipient());
			}
			
			//メールの送信
			log.info("Mail:{} -> {}", oMsg.getSender(), oMsg.getRecipient());
			SendMailService sm = new SendMailService();
			for(int i=0;i<sMsgs.size();i++)
				sm.sendMail(sMsgs.get(i), envBundle);
		}catch(IOException e) {
			
		}
	}
}

