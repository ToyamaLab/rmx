package plugins.acc.regist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import plugins.acc.util.RegistUtil;
import dao.DBDao;
import data.Message;

public class RegisterAcc {
	//
	private Message oMsg;
	private ResourceBundle domconfBundle;
	private String dummy;
	private String sender;
	private String user_name;
	private String content;
	
	//コンストラクタ
	public RegisterAcc(
			ArrayList<String> commandArgs,
			Message oMsg,
			ResourceBundle domconfBundle) {
		this.oMsg = oMsg;
		this.domconfBundle = domconfBundle;
		this.sender = oMsg.getSender();
		this.content = commandArgs.get(0);
		this.user_name = commandArgs.get(1);
	}
	
	public void saveAddress() {
		try {
			dummy = RegistUtil.getDummy(user_name, content, oMsg.getRecipient());
			String insert_query = RegistUtil.createInsertQuery(oMsg.getRecipient(), oMsg.getSender() , commandArgs);
			DBDao dbDao = new DBDao(domconfBundle);
			dbDao.write(insert_query);
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public Message createReply(Message oMsg, ArrayList<String> commandArgs) {
		Message sMsg = new Message();
		ArrayList<String> body = RegistUtil.createRegistBody(oMsg, commandArgs);
		
		
	}
	
	
	
	
}
