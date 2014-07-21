package controller;

import java.util.List;

import data.Message;

/**
 * RMXのロジックに関する制御フロー
 * マルチスレッドで実装する。
 */
public interface LogicController extends Runnable {
	
	/**
	 * メールアドレスに関するロジック
	 * @param originalMsg 受信したメール
	 * @return 宛先をデータベースから取得したメールアドレスにに書き換えた<code>Message</code>のリスト
	 */
	public List<Message> startLogic(Message originalMsg);
	
	/**
	 * body(メール本文)に関するロジック、本文を編集する。
	 * @param originalMsg
	 * @return 本文を編集後の<code>Message</code>
	 */
	public Message bodyLogic(Message originalMsg);
	
	public void run();

}
