package logic;

import java.util.List;

/**
 * アドレスをパーズしてPropertiesファイルからSQLを取得
 */
public interface Parse {
	
	/**
	 * @param recipient メールの送信先
	 * @return SQLクエリとパラメータのリスト
	 */
	public List<String> makeQuery(String recipient);

}
