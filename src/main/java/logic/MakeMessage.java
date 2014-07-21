package logic;

import java.util.List;

import data.Message;

/**
 * 送信メッセージの作成
 * Transfer, Answer, Plugin, Errorでこのインターフェースを実装する。
 */
public interface MakeMessage {
	
	/**
	 * メッセージを作成
	 * @param oMseg 受信したメール
	 * @param recipients データベース等で取得したメールアドレスのリスト
	 * @return 送信メッセージのリスト
	 */
	public List<Message> make(Message oMseg, List<String> recipients);

}
