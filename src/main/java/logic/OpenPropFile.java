package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * env.propertiesのチェック、及びそれを用いてRMXで用いる
 * ドメインからruleファイルを呼び出すHashMapの構築を行う<br/>
 * (e.g.) env.propertiesファイルのキー(domain)の値がrmxdev:rmxdev.keio.jpのとき、キーとして
 * rmxdevが必要となる. <br/>
 * その値testkを取得したとき、用意するpropertiesファイルはtestk_rmxdevである.<br/>
 * この実装クラスはコンストラクタをprivateで呼び出し、シングルトンを保証する。
 */
public interface OpenPropFile {
	
	/**
	 * env.propertiesからdomvonf.propertiesに関するHashMapを構築する。
	 */
	public void open();
	
	public ResourceBundle getEnvBundle();
	
	public ArrayList<HashMap<String, String>> getDomainsMaps();
	
	public HashMap<String, ResourceBundle> getDomBundles();
	
}
