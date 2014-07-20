package controller;

/**
 * RMXシステムに関する制御フロー<br/>
 * RMXのロジック以外(サーバとの接続、設定ファイルの読み込みetc.)を制御
 * メールの受信をマルチスレッドで行う。
 */
public interface SystemController {

	/**
	 * Propertiesファイルを展開し、メールの送受信をおこなう
	 */
	public void startSystem();

}
