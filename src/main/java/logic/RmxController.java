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
				CreateFlow flow;
				Thread th = new Thread(flow = new CreateFlow(socket,envBundle));
				th.start();
				
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
