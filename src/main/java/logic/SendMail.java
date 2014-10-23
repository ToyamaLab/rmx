package logic;

import data.Message;

/**
 * メールをサーバに送信
 */
public interface SendMail {
	
	/**
	 * メールを送信
	 * @param sMsg サーバと通信し、実際に送信する
	 */
	public void send(Message sMsg);

}
