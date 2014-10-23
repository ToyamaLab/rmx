package logic;

import data.Message;

/**
 * メールをソケットから取得
 */
public interface Incoming {
	
	public void getMail();
	
	public Message getMessage();

}
