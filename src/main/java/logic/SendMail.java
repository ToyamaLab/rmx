package logic;

import data.Message;

/**
 * メールをサーバに送信
 */
public interface SendMail {
	
	/**
	 * メールを送信
	 * @param sMsg 実際に送信するオブジェクト
	 */
	public void send(Message sMsg);

}
