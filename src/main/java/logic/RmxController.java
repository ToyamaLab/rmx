package logic;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import logic.error.ErrorMailService;
import logic.parse.User;
import logic.plugin.GetSendMailsOfFunction;
import logic.propfile.PropFileService;
import logic.service.GetSendMails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import presentation.mail.IncomingMailService;
import presentation.mail.SendMailService;
import dao.PropfileDao;
import data.Message;

public class RmxController {
	
	private static final Logger log = LoggerFactory.getLogger(RmxController.class);
	
	//コンストラクタ
	public RmxController() {		
	}
	
	public static void startPkg() {
		propFileController();
	}
	
	//env.propertiesファイルをチェックする
	public static void propFileController() {
		PropFileService propFileService = new PropFileService();
		ArrayList<HashMap<String, String>> propFileNamesAndDomains =
				propFileService.getDomconfPropFileNamesAndDomains();
		if(!propFileNamesAndDomains.isEmpty())
			smtpListenerController();
	}
	
	//ソケットを開き、システムをスタート
	public static void smtpListenerController() {
		try {
			ServerSocket sSocket;
			Socket socket;
			int RECEIV_PORT;
			Message oMsg;
			
			ResourceBundle envBundle = PropfileDao.readPropFile("env");
			
			RECEIV_PORT = Integer.parseInt(envBundle.getString("receive_port"));
			sSocket = new ServerSocket(RECEIV_PORT);
			log.info("RMX System begin");
			log.debug("-----START<<PORT_NUM = "+RECEIV_PORT+">>-----");

			while(true) {
				//メールがメールサーバにくると、originalMessageインスタンスを作成
				
				socket = sSocket.accept();
				log.debug("S :Accepted: ("
						+ socket.getInetAddress().getHostName() + ")");
				IncomingMailService incoming;
				Thread th = new Thread(incoming = new IncomingMailService(socket));
				th.start();
				th.join();
				
				//送られてきたメールをオブジェクトとして得る
				oMsg = new Message();
				oMsg = incoming.getMessage();
				
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
					continue;
				}
				
				//得られたときはpropertiesファイルをオブジェクトとして得る
				ResourceBundle domconfBundle = PropfileDao.readPropFile(domconfPropFileName);
				
				//メールの宛先にSOPを通す
				User user = new User();
				user.UserStart(oMsg.getRecipient(), domconfBundle);
				
				//RMX形式の場合 ex)team{rmx}+grade{3}@test.keio.com
				//normalFlgがtrue
				if(user.getNormalFlg()) {
					GetSendMails gsm = new GetSendMails(user, domconfBundle, oMsg);
					sMsgs = gsm.getSendMails();
				}
				//#functionの場合 ex)#event.attend#team{rmx}@test.keio.com
				//functionFlgがtrue
				else if(user.getFunctionFlg()) {
					GetSendMailsOfFunction gsmf = new GetSendMailsOfFunction(user, domconfBundle, oMsg);
					sMsgs = gsmf.getSendMailsOfFunction();
				}
				//それ以外は文法エラー
				else {
					errorMsg = ErrorMailService.syntaxErrorMail(oMsg);
					SendMailService sm = new SendMailService();
					sm.sendMail(errorMsg, envBundle);
					log.info("Error Mail \"syntaxError\" is sending to {}", errorMsg.getRecipient());
					continue;
				}
				
				//メッセージが0件の時
				if(sMsgs.isEmpty()) {
					errorMsg = ErrorMailService.noneRecipientError(oMsg);
					SendMailService sm = new SendMailService();
					sm.sendMail(errorMsg, envBundle);
					log.info("Error Mail \"noneRecipient\" is sending to {}", errorMsg.getRecipient());
					continue;
				}
				
				//メールの送信
				log.info("Mail:{} -> {}", oMsg.getSender(), oMsg.getRecipient());
				SendMailService sm = new SendMailService();
				for(int i=0;i<sMsgs.size();i++)
					sm.sendMail(sMsgs.get(i), envBundle);
		
			}
		}catch (SecurityException e) {
			log.error("# Error: " + e.toString());
			System.exit(-1);
		}catch (Exception e) {
			log.error("# Error: " + e.toString());
			System.exit(-1);
		}
	}
}
