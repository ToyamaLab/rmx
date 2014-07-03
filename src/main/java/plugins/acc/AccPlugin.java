package plugins.acc;

import java.util.ArrayList;
import java.util.ResourceBundle;

import plugins.acc.regist.RegisterAcc;
import plugins.acc.util.AccUtil;
import data.Message;
import logic.interfaces.PluginInterface;

public class AccPlugin implements PluginInterface{

	@Override
	public ArrayList<Message> pluginStart(
			Message oMsg,
			String function,
			String command,
			ArrayList<String> commandArgs,
			String target,
			ArrayList<String> recipients,
			String propfile,
			ResourceBundle domconfBundle) {
		
		String acc_propfile = AccUtil.getAccPropfileName(propfile);
		ArrayList<Message> sMsg = new ArrayList<Message>();
		if(command.equalsIgnoreCase("regist")) {
			RegisterAcc r_acc = new RegisterAcc(commandArgs, oMsg, propfile, acc_propfile, domconfBundle);
			r_acc.saveAddress();
			
		}
		return sMsg;
	}

	@Override
	public ArrayList<String> getAvailableFunctionNames() {
		ArrayList<String> al = new ArrayList<String>();
		String function_name = "acc";
		al.add(function_name);
		return al;
	}

}
