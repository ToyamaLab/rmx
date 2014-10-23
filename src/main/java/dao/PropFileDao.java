package dao;

import java.util.ResourceBundle;

/**
 * プロパティファイルの読み込み
 */
public interface PropFileDao {
	
	/**
	 * @param propFileName プロパティファイル名
	 * @return ファイルがあればそのオブジェクト、なければnullを返す。
	 */
	public ResourceBundle read(String propFileName);

}
