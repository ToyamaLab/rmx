package logic.interfaces;

import java.util.ArrayList;
import java.util.ResourceBundle;

import data.Message;

public interface PluginInterface {
	public ArrayList<Message> pluginStart(
			Message oMsg,
			String function,
			String command,
			ArrayList<String> commandArgs,

			ArrayList<String> destinations,
			ResourceBundle domconfBundle);

			String target,
			ArrayList<String> recipients,
			String propFileName,
			ResourceBundle domconfBundle,
			String domain,
			String subdomain,
			String propfile);

	public ArrayList<String> getAvailableFunctionNames();
}
