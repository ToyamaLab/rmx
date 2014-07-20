package controller;

import java.util.List;

import data.Message;

/**
 * RMXのロジックに関する制御フロー
 */
public interface LogicController {
	
	/**
	 * メールアドレスに関するロジック
	 * @param originalMsg 受信したメール
	 * @return 宛先をデータベースから取得したメールアドレスにに書き換えたメッセージのリスト
	 */
	public List<Message> startLogic(Message originalMsg);
	
	/**
	 * body(メール本文)に関するロジック
	 * @param originalMsg
	 * @return 本文を編集後もメール
	 */
	public Message bodyLogic(Message originalMsg);

}
