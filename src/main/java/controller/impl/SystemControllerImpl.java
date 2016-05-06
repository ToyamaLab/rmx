package controller.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
	private ExecutorService pool;

	public SystemControllerImpl() {
		opf = OpenPropFileImpl.getInstance();
	}

	@Override
	public void startSystem() {
		opf.open();
		if (!opf.getDomBundles().isEmpty()) {
			pool = Executors.newFixedThreadPool(Integer.parseInt(opf.getEnvBundle().getString("max_receive_socket").trim()));
			this.listen();
			System.exit(0);
		} else {
			log.error("RMX System does NOT open PropFile.");
			System.exit(-1);
		}
	}

	private void listen() {
		ResourceBundle envBundle = opf.getEnvBundle();
		try {
			sSocket = new ServerSocket(Integer.parseInt(envBundle.getString("receive_port").trim()));
			log.info("RMX System START on Port " + envBundle.getString("receive_port"));
			while (true) {
				cSocket = sSocket.accept();
				log.info("S :Accepted: (" + cSocket.getInetAddress().getHostName() + ")");
				LogicController lc = new LogicControllerImpl(cSocket);
				pool.submit(lc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (cSocket != null)
					cSocket.close();
				if (sSocket != null)
					sSocket.close();
				pool.shutdown();
				try {
					if (!pool.awaitTermination(25, TimeUnit.MINUTES))
						pool.shutdownNow();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
			}
		}
	}

}
