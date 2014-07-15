package logic.utils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.DBDao;
import data.Message;
import logic.bodyfunction.ContentsMatch;
import logic.parse.SOP.parserVisitor;

public class FlowUtils {
	private static final Logger log = LoggerFactory.getLogger(FlowUtils.class);
	
	/**
	 * 宛先をリストとして返す.
	 * @param userInfo userもしくはuser1
	 * @param domBundle propertyオブジェクト
	 * @return 宛先のリスト
	 * */
	public static ArrayList<String> getRecipients(parserVisitor userInfo, ResourceBundle domBundle){
		// 1. 宛先用のリスト
		ArrayList<String> recipients = new ArrayList<String>();
		// 2. クエリを得る
		String query = userInfo.getQuery();
		System.out.println(query);
		// 3. パラメータを得る
		ListIterator<String> params = userInfo.getPara().listIterator();
		System.out.println(userInfo.getPara());
		// 4. DBから宛先の集合を得る
		try {
			DBDao dbDao = new DBDao(domBundle);
			ResultSet rs;
			rs = dbDao.read(query, params);
			while(rs.next()) {recipients.add(rs.getString(1));}
			rs.close();
		}catch (Exception e) {e.printStackTrace();}
		return recipients;
	}
	
	/**
	 * メールの宛先から関数形式or自然形式を簡易的に判断する.関数形式なら"functionalForm"を返し、自然形式なら"naturalForm"を返す.
	 * @param recipient メールの宛先
	 * @return 形式
	 * */
	public static String whichForm(String recipient) {
		if(recipient.contains("{") && recipient.contains("}"))
			return "functionalForm";
		else
			return "naturalForm";
	}
	
	/**
	 *transfer用の全メール(中身も)を作成して、リストで返す.
	 *@param userInfo userもしくはuser1
	 *@param domBundle propertyオブジェクト
	 *@param oMsg 受信メッセージ
	 *@param cm コンテンツマッチ
	 *@return 送信用メッセージのリスト
	 * */
	public static ArrayList<Message> getTransferMails(
			parserVisitor userInfo,
			ResourceBundle domBundle,
			Message oMsg,
			ContentsMatch cm){
		// 1. 宛先用のリスト
		ArrayList<String> recipients = getRecipients(userInfo, domBundle);
		
		// 2. 送信用のメール作成
		ArrayList<Message> sMsgs = new ArrayList<Message>();
		
		// 3. メール作成
		for(int i=0;i<recipients.size();i++) {
			// 4. 1通ごとにオブジェクト作成
			Message sMsg = new Message();
			
			sMsg.setRecipient(recipients.get(i));
			sMsg.setSender(oMsg.getSender());
			sMsg.setSubject(oMsg.getSubject());
			if(cm != null){
				ArrayList<String> edited = cm.editBody(oMsg.getBody(), recipients.get(i));
				for(int j = 0; j < edited.size(); j++){
					sMsg.addBody(edited.get(j));
				}
			}
			else{
				for(int j=0;j<oMsg.getBody().size();j++) 
					sMsg.addBody(oMsg.getBody().get(j));
			}
			
			for(int k=0;k<oMsg.getHeader().size();k++) {
				sMsg.addHeader(oMsg.getHeader().get(k));
			}
			
			//送信メッセージをリストに挿入
			if(sMsg != null)
				sMsgs.add(sMsg);
		}
		return sMsgs;
	}
	
//	++++++++++作成中++++++++++
//	public static ArrayList<Message> getAnswerMails(
//			parserVisitor userInfo,
//			ResourceBundle domBundle,
//			Message oMsg){
//		//answer mailの作成
//		return null;
//	}
//	
//	++++++++++作成中++++++++++
//	public static ArrayList<Message> getMixturedMails(
//			parserVisitor userInfo,
//			ResourceBundle domBundle,
//			Message oMsg){
//		//mixtured mailの作成
//		return null;
//	}
	
	
}
