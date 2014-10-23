package logic;

import logic.parse.Parsable;

/**
 * メールアドレスから適したパーザを設定するファクトリインターフェース。
 * そのパーザに必要な情報も返すメソッドも保持する。
 */
public interface ParserSelector {
	
	/**
	 * メールアドレスから関数形式、自然形式に必要なパーザ(User, User1)を返す。
	 * その判別についてもチェックする。
	 * @param recipient メールアドレス
	 * @return UserもしくはUser1
	 */
	public Parsable select(String recipient);

}
