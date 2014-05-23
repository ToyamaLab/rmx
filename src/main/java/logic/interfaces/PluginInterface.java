package logic.interfaces;

import java.util.ArrayList;

import data.Message;

public interface PluginInterface {
	public ArrayList<Message> pluginStart(
			Message oMsg,
			String function,
			String command,
			ArrayList<String> commandArgs,
			ArrayList<String> destinations);
	
	public ArrayList<String> getAvailableFunctionNames();
}
