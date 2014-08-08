package plugins.acc.regist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import plugins.acc.util.RegistUtil;
import dao.DatabaseDao;
import dao.impl.DatabaseDaoImpl;
import data.Message;

public class RegistAcc {
	//
	private Message oMsg;
	private ResourceBundle domconfBundle;
	private String dummy;
	private String sender;
	private String user_name;
	private String content;
	List<String> recipients;
	
	//コンストラクタ
	public RegistAcc(
			List<String> commandArgs,
			Message oMsg,
			ResourceBundle domconfBundle,
			List<String> recipients) {
		this.oMsg = oMsg;
		this.domconfBundle = domconfBundle;
		this.sender = oMsg.getSender();
		this.content = commandArgs.get(0);
		this.user_name = commandArgs.get(1);
		this.recipients = recipients;
	}
	
	public void saveAddress() {
		try {
			dummy = RegistUtil.getDummy(user_name, content, oMsg.getRecipient());
			String insert_query = RegistUtil.createInsertQuery(dummy, sender, content, user_name);
			DatabaseDao dbDao = new DatabaseDaoImpl(domconfBundle);
			dbDao.write(insert_query);
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public Message createReply() {
		Message sMsg = new Message();
		ArrayList<String> body = RegistUtil.createRegistBody(oMsg, dummy, user_name, content);
		sMsg.setRecipient(sender);
		sMsg.setSender(sender);
		sMsg.setSubject("");
		for(int i=0;i<body.size();i++) {
			sMsg.addBody(body.get(i));
		}
		for(int j=0;j<oMsg.getHeader().size();j++) {
			sMsg.addHeader(oMsg.getHeader().get(j));
		}
		
		return sMsg; 
	}
	
	
	
	
}
