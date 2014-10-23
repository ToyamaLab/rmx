package logic;

import java.util.ResourceBundle;

import logic.parse.Parsable;

/**
 * <p>
 * アドレスをパーズしてPropertiesファイルからSQLを取得
 * アドレスの形式をチェックし、適切なパーザを選択する。
 * </p>
 */
public interface Parse {
	
	/**
	 * パーズした後のパーザオブジェクトを返す。これにはSQLクエリとパラメータが保持されている。
	 * @param recipient メールの送信先
	 * @return パーザオブジェクト
	 */
	public Parsable getParser();
	
	/**
	 * 使用したProperties(domconf)ファイルのリソースバンドルを返す。
	 * @return 使用したdomBundle
	 */
	public ResourceBundle getDomBundle();
	
}
