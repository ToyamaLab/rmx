package plugins.acc.regist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import plugins.acc.util.AccUtil;
import dao.DBDao;
import data.Message;

public class RegisterAcc {
	//
	private ArrayList<String> commandArgs;
	private Message oMsg;
	private String propfile;
	private String acc_propfile;
	private ResourceBundle domconfBundle;
	
	//コンストラクタ
	public RegisterAcc(
			ArrayList<String> commandArgs,
			Message oMsg,
			String propfile,
			String acc_propfile,
			ResourceBundle domconfBundle) {
		this.commandArgs = commandArgs;
		this.oMsg = oMsg;
		this.propfile = propfile;
		this.acc_propfile = acc_propfile;
		this.domconfBundle = domconfBundle;
	}
	
	public void saveAddress() {
		try {
			String insert_query = AccUtil.createInsertQuery(oMsg.getRecipient(), oMsg.getSender() , commandArgs);
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
		ArrayList<String> body = AccUtil.createRegistBody(oMsg, commandArgs);
		
		
	}
	
	
	
	
}
