package logic.plugin;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;

import dao.DBDao;
import data.Message;

import logic.interfaces.PluginInterface;
import logic.parse.User;

public class GetSendMailsOfFunction {
	private User user;
	private ResourceBundle domconfBundle;
	private Message oMsg;
	private String function;
	private String command;
	private ArrayList<String> commandArgs;
	private ArrayList<PluginInterface> plugins;
	private PluginInterface plugin;
	
	public GetSendMailsOfFunction(User user,
			ResourceBundle domconfBundle,
			Message oMsg) {
		this.user = user;
		this.domconfBundle = domconfBundle;
		this.oMsg = oMsg;
		this.function = user.getFunction();
		this.command = user.getCommand();
		this.commandArgs = user.getCommandArgs();
	}
	
	public ArrayList<Message> getSendMailsOfFunction(){
		//使用できる全てのプラグインを得る ex)eventやhelpなど
		plugins = PluginService.getPlugins();
		
		//その中から今回の受信メールに対応するプラグインを１つ選択する
		plugin = this.getUsablePlugin();
		
		//送信メッセージの宛先(destinations)を得る
		ArrayList<String> destinations = this.getFunctionDestinations();
		
		//送信メッセージを返す
		//引数で宛先(destinations)を予め渡しておく事で、plugin側でパースしない
		ArrayList<Message> sMsgs = plugin.pluginStart(oMsg, function, command, commandArgs, destinations,domconfBundle);
		
		return sMsgs;
	}
	
	//
	private PluginInterface getUsablePlugin() {
		for(int i=0;i<plugins.size();i++) {
			ArrayList<String> pluginFunctions = plugins.get(i).getAvailableFunctionNames();
			for(int j=0;j<pluginFunctions.size();j++) {
				if(pluginFunctions.get(j).equalsIgnoreCase(function))
					return plugins.get(i);
				else 
					continue;
			}
		}
		return null;
	}
	
	//
	private ArrayList<String> getFunctionDestinations(){
		//宛先 ex)#func.command.arg1#team{rmx}@test.keio.com
		String recipient = oMsg.getRecipient();
		
		//target ex)team{rmx}@test.keio.com
		int start = recipient.indexOf("#", 1);
		String target = recipient.substring(start+1);
		
		if(target.indexOf("@")>0) {
			//targetをパースする
			User funcUser = new User();
			funcUser.UserStart(target, domconfBundle);
			
			//クエリとパラメーターを得る
			String query = funcUser.getQuery();
			ListIterator<String> params = funcUser.getPara().listIterator();
			
			//宛先(destinations)を得る
			ArrayList<String> destinations = this.getMailAddresses(domconfBundle, query, params);
			
			return destinations;
		}else {
			ArrayList<String> returnMe = new ArrayList<String>();
			returnMe.add(oMsg.getSender());
			
			return returnMe;
		}
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
