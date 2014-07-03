package logic.plugin;

import java.util.ArrayList;
import java.util.ResourceBundle;

import data.Message;
import logic.interfaces.PluginInterface;
import logic.parse.User;
import logic.service.PluginService;

public class MailProvider {
	public MailProvider() {}
	
	public  ArrayList<Message> createMails(PluginInterface plugin,
			Message oMsg,
			String propFileName,
			ResourceBundle domconfBundle,
			User user){
		String function = user.getFunction();
		String command = user.getCommand();
		ArrayList<String> commandArgs = user.getCommandArgs();
		String target = PluginService.trimSharp(oMsg.getRecipient());
		
		ArrayList<String> recipients = PluginService.getRecipients(oMsg, domconfBundle);
		
		return plugin.pluginStart(oMsg, function, command, commandArgs, target, recipients, propFileName, domconfBundle);
	}
}
