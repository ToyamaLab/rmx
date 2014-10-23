package controller;

/**
 * RMXシステムに関する制御フロー
 * <p>RMX起動、設定ファイルの読み込み、サーバ待機に関する制御を行う。
 * メールの受信をマルチスレッドで行う。
 */
public interface SystemController {

	/**
	 * Propertiesファイルを展開し、サーバとの通信をおこなう
	 */
	public void startSystem();
	
}
