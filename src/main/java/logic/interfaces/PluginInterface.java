package logic.interfaces;

import java.util.List;
import java.util.ResourceBundle;

import data.Message;

public interface PluginInterface {
	public List<Message> pluginStart(
			Message oMsg,
			String function,
			String command,
			List<String> commandArgs,
			List<String> recipients,
			ResourceBundle domBundle,
			String propfile);

	public List<String> getAvailableFunctionNames();
}
