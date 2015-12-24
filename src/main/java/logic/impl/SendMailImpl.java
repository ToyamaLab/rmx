package logic.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ListIterator;
import java.util.ResourceBundle;

import logic.SendMail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.Message;

/**
 * {@link SendMail}の実装
 */
public class SendMailImpl implements SendMail, Runnable {

	/** インプットテキストリーダ */
	private BufferedInputStream in;
	/** アウトプットテキストリーダ */
	private BufferedOutputStream out;
	/** ログ出力 */
	private static final Logger log = LoggerFactory.getLogger(SendMailImpl.class);
	/** プロトコルの終端記号 */
	private static final String CRLF = "\r\n";
	/** RMXシステムプロパティファイル */
	private ResourceBundle envBundle;
	/** 送信するメール */
	Message sMsg;
	/** OpenPropFileImpl */
	OpenPropFileImpl opf = OpenPropFileImpl.getInstance();
	
	public SendMailImpl(Message _sMsg) {
		envBundle = opf.getEnvBundle();
		sMsg = _sMsg;
	}
	
	@Override
	public void run() {
		this.send(sMsg);
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void send(Message sMsg) {

		try {
			Socket mySocket = new Socket(InetAddress.getByName(envBundle.getString("mailServer")), 25);
			String buffer = new String();
			
			in = new BufferedInputStream(mySocket.getInputStream());
			out = new BufferedOutputStream(mySocket.getOutputStream());
			
			buffer = getAck();
			if (buffer.startsWith("220")) {
				log.info("C : Received : {}", buffer);
			}

			// 2. Request HELO.
			buffer = "HELO " + InetAddress.getLocalHost().getHostName() + CRLF;
			sendReq(buffer);
			log.debug("C :   Send   : {}", buffer);

			// Check for 250.
			buffer = getAck();
			if (buffer.startsWith("250")) {
				log.debug("C : Received : {}", buffer);
			}
			log.trace("c33333");

			// 3. Request MAIL.
			buffer = "MAIL FROM: " + sMsg.getSender() + CRLF;
			sendReq(buffer);
			log.debug("C :   Send   : {}", buffer);

			// Check for 250.
			buffer = getAck();
			if (buffer.startsWith("250")) {
				log.debug("C : Received : {}", buffer);
			}
			String recipient = sMsg.getRecipient();
			log.trace("c44444");

			// 4. Request RCPT.
			buffer = "RCPT TO: " + recipient + CRLF;
			sendReq(buffer);
			log.debug("C :   Send   : {}", buffer);
			buffer = getAck();
			if (buffer.startsWith("250")) {
				log.debug("C : Received : {}", buffer);
			}
			log.trace("c55555");

			// 5. Request DATA.
			buffer = "DATA" + CRLF;
			sendReq(buffer);
			log.debug("C :   Send   : {}", buffer);

			// Check for 354.
			buffer = getAck();
			if (buffer.startsWith("354")) {
				log.debug("C : Received : {}", buffer);
			}

			// 5-1. Send the message header.
			out.write(("X-RMX: Powered by Toyama Lab. in Keio University" + CRLF).getBytes());
			out.flush();
			out.write(("X-RMX-Version: 1.0.0" + CRLF).getBytes());
			out.flush();
			String subject = sMsg.getSubject();
			boolean needsChanging = (subject != null) ? true : false;
			ListIterator<String> header = sMsg.getHeader().listIterator();
			while (header.hasNext()) {
				String next = header.next();
				if (next.startsWith("Subject") && needsChanging) {
					out.write(("Subject: " + subject + CRLF).getBytes());
					out.flush();
				} else if (!header.hasNext() && needsChanging) {
					out.write(("Subject: " + subject + CRLF).getBytes());
					out.flush();
					out.write((next + CRLF).getBytes());
					out.flush();
				} else {
					out.write((next + CRLF).getBytes());
					out.flush();
				}
			}

			// 5-2. Send the message body.
			ListIterator<String> body = sMsg.getBody().listIterator();
			while (body.hasNext()) {
				out.write((body.next() + CRLF).getBytes());
				out.flush();
			}

			// Check for 250.
			buffer = getAck();
			if (buffer.startsWith("250")) {
				log.debug("C : Received : {}", buffer);
			}
			log.trace("c66666");

			// 6. Request QUIT.
			buffer = "QUIT" + CRLF;
			sendReq(buffer);
			log.debug("C :   Send   : {}", buffer);

			// Check for 221.
			buffer = getAck();
			if (buffer.startsWith("221")) {
				log.info("C : Received : {}", buffer);
				mySocket.close();
			}
		} catch (UnknownHostException e) {
			log.warn("# Error: " + e.toString());
		} catch (IOException e) {
			log.warn("# Error: " + e.toString());
		}
	}
	
	private void sendReq(String request) {
		try {
			// Send my request to the server.
			out.write(request.getBytes());
			out.flush();
		} catch (IOException E) {
			log.error("# Error: " + E.toString());
			System.exit(-1);
		} catch (Exception E) {
			log.error("# Error: " + E.toString());
			System.exit(-1);
		}
	}

	/**
	 * get ack
	 *
	 * @return
	 */
	private String getAck() {
		StringBuffer ack = new StringBuffer();
		try {
			int avail = in.available();

			// Wait for the acknowledge.
			while (avail == 0) {
				Thread.sleep(100);
				avail = in.available();
			}
			
			// Receive the acknowledge.
			while (avail > 0) {
				byte[] buff = new byte[avail];
				in.read(buff);
				for (int i = 0; i < buff.length; i++) {
					ack.append((char) buff[i]);
				}
				avail = in.available();
			}
		} catch (IOException E) {
			log.error("# Error: " + E.toString());
			System.exit(-1);
		} catch (InterruptedException E) {
			log.error("# Error: " + E.toString());
			System.exit(-1);
		} catch (Exception E) {
			log.error("# Error: " + E.toString());
			System.exit(-1);
		}
		return ack.toString();
	}

}
