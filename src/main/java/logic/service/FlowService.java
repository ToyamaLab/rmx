package logic.service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;

import logic.parse.User;
import logic.parse.SOP.parserVisitor;
import dao.DBDao;
import data.Message;

public class FlowService {
	private FlowService() {}
	
	public static ArrayList<Message>getTransferMails(
			parserVisitor user_info,
			ResourceBundle domconfBundle,
			Message oMsg) {
		//userからクエリーとパラメーターを得る
		String query = user_info.getQuery();
		ListIterator<String> params = user_info.getPara().listIterator();
		
		//宛先を得る
		ArrayList<String> mail_addresses = getMailAddresses(domconfBundle, query, params);
		
		//送信用のメールを作成
		ArrayList<Message> sMsgs = new ArrayList<Message>();
				
		for(int i=0;i<mail_addresses.size();i++) {
			//1通ごとにオブジェクト作成
			Message sMsg = new Message();
					
			//送信メッセージの作成
			sMsg.setRecipient(mail_addresses.get(i));
			sMsg.setSender(oMsg.getSender());
			sMsg.setSubject(oMsg.getSubject());
			for(int j=0;j<oMsg.getBody().size();j++) {
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
	
	public static ArrayList<Message> getAnswerMails(
			User user,
			ResourceBundle domconfBundle,
			Message oMsg){
		//userからクエリーとパラメーターを得る
		String query = user.getQuery();
		ListIterator<String> params = user.getPara().listIterator();
		
		ArrayList<String> body_contents = 
	}
	
	public static ArrayList<String> getMailAddresses(
			ResourceBundle domconfBundle,
			String query,
			ListIterator<String> params){
		//
		ArrayList<String> mail_addresses  = new ArrayList<String>();
		try {
			DBDao dbDao = new DBDao(domconfBundle);
			ResultSet rs;
			rs = dbDao.read(query, params);
			while(rs.next()) {mail_addresses.add(rs.getString(1));}
			rs.close();
		} catch (Exception e) {e.printStackTrace();}
		return mail_addresses;
	}
	
	public static ArrayList<String> getBodyContents(
			ResourceBundle domconfBundle,
			String query,
			ListIterator<String> params){
		ArrayList<String> body_contents = new ArrayList<String>();
		ArrayList<String> colmun_names = new ArrayList<String>();
		try {
			DBDao dbDao = new DBDao(domconfBundle);
			ResultSet rs = dbDao.read(query, params);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++) 
				{colmun_names.add(rsmd.getColumnName(i));}
			
		}catch(Exception e) {
			
		}
	}
}
