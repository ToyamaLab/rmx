package logic.service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;

import javax.activation.MailcapCommandMap;

import dao.DBDao;
import data.Message;

import logic.parse.User;

public class GetSendMails {
	private User user;
	private ResourceBundle domconfBundle;
	private Message oMsg;
	
	public GetSendMails(User user,ResourceBundle domconfBundle,Message oMsg) {
		this.user = user;
		this.domconfBundle = domconfBundle;
		this.oMsg = oMsg;
	}
	
	public ArrayList<Message> getSendMails(){
		//userからクエリーとパラメーターを得る
		String query = user.getQuery();
		ListIterator<String> params = user.getPara().listIterator();
		
		//宛先を得る
		ArrayList<String> mailAddresses = this.getMailAddresses(domconfBundle, query, params);
		
		//送信用のメールを作成
		ArrayList<Message> sMsgs = new ArrayList<Message>();
		
		for(int i=0;i<mailAddresses.size();i++) {
			//1通ごとにオブジェクト作成
			Message sMsg = new Message();
			
			//送信メッセージの作成
			sMsg.setRecipient(mailAddresses.get(i));
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
	
	public ArrayList<String> getMailAddresses(
			ResourceBundle domconfBundle,
			String query,
			ListIterator<String> params){
		//
		ArrayList<String> mailAddresses  = new ArrayList<String>();
		try {
			DBDao dbDao = new DBDao(domconfBundle);
			ResultSet rs;
			rs = dbDao.read(query, params);
			while(rs.next()) {mailAddresses.add(rs.getString(1));}
			rs.close();
		} catch (Exception e) {e.printStackTrace();}
		return mailAddresses;
	}
	
}
