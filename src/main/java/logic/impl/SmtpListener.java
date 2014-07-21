package logic.impl;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logic.parse.Distributor;



public class SmtpListener {
	//メンバ変数
	private static final Logger log = LoggerFactory.getLogger(SmtpListener.class);
	private static OpenPropFileImpl propfileInstance = OpenPropFileImpl.getInstance();
	
	
	public void startPkg() {
		//env.propertiesファイルをチェックする
		propfileInstance.open();
		
		//正しく展開した後、ソケットを開く
		if(!propfileInstance.getDomBundles().isEmpty())
			this.smtpListenerController();
		else {
			log.error("# Error: RMX Config Files are NOT correct.");
			System.exit(-1);
		}
	}
	
	private void smtpListenerController() {
		ServerSocket sSocket;
		Socket socket;
		int RECEIV_PORT;
		ResourceBundle envBundle = propfileInstance.getEnvBundle();

		try {
			RECEIV_PORT = Integer.parseInt(envBundle.getString("receive_port"));
			sSocket = new ServerSocket(RECEIV_PORT);
			log.info("RMX System begin");
			log.debug("-----START<<PORT_NUM = "+RECEIV_PORT+">>-----");

			while(true) {
				//メールがメールサーバにくると、originalMessageインスタンスを作成
				
				socket = sSocket.accept();
				log.debug("S :Accepted: (" + socket.getInetAddress().getHostName() + ")");

				Thread t = new Thread(new Distributor(socket));
				t.start();
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
