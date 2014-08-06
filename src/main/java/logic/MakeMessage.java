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
	 * @param parse パーズオブジェクト
	 * @return 送信メッセージのリスト
	 */
	public List<Message> make(Message oMseg, Parse parse);

}
