package logic.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import data.Message;

/**
 * RMI通信を用いるときに使用するメソッド
 */
public interface RmiFunction extends Remote {
	
	/**
	 * 送信するために必要な情報をList形式にまとめる。<br/>
	 * マスターサーバ上で用いる。
	 * @param oMsg 受信したメッセージ
	 * @param recipients 送信先のリスト
	 * @throws RemoteException
	 */
	public void makeSendMailsInfo(Message oMsg, ArrayList<String> recipients) throws RemoteException;

	/**
	 * 送信するために必要な情報をListにして得る。<br/>
	 * リモートメソッドとしてRMIregistoryに登録する。
	 * 
	 * @return 必要な情報(送信先、本文etc.)のリスト
	 * @throws RemoteException
	 */
	public List<Object> getSendMailsInfo() throws RemoteException;
	
}
