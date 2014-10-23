package controller.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

import logic.OpenPropFile;
import logic.impl.OpenPropFileImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.LogicController;
import controller.SystemController;

/**
 * SystemControllerの実装
 */
public class SystemControllerImpl implements SystemController {
	
	private ServerSocket sSocket;
	private Socket cSocket;
	private OpenPropFile opf;
	private static final Logger log = LoggerFactory.getLogger(SystemControllerImpl.class); 
	
	public SystemControllerImpl() {		
		opf = OpenPropFileImpl.getInstance();
	}
	
	@Override
	public void startSystem() {
		opf.open();
		if (!opf.getDomBundles().isEmpty()) {
			this.listen();
			System.exit(0);
		} else {
			log.error("RMX System does NOT open PropFile.");
			System.exit(-1);
		}
	}
	
	private void listen() {
		ResourceBundle envBundle = opf.getEnvBundle();
		try{
			sSocket = new ServerSocket(Integer.parseInt(envBundle.getString("receive_port")));
			log.info("RMX System START on Port " + envBundle.getString("receive_port"));
			
			while (true) {
				cSocket = sSocket.accept();
				log.info("S :Accepted: (" + cSocket.getInetAddress().getHostName() + ")");
				LogicController lc = new LogicControllerImpl(cSocket);
				Thread th = new Thread(lc);
				th.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(cSocket != null)
					cSocket.close();
				if(sSocket != null)
					sSocket.close();
			} catch (IOException e) {
			}			
		}
	}

}
