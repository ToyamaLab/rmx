package main;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import plugins.acc.util.ListUtil;
import plugins.acc.util.RegistUtil;
import logic.SmtpListener;
import logic.bodyfunction.ContentsMatch;
import logic.flow.AnswerFlow;
import logic.interfaces.PluginInterface;
import logic.parse.Distributor;
import logic.parse.User;
import logic.parse.User1;
import logic.parse.SOP.parserVisitor;
import logic.plugin.PluginsHolder;
import logic.propfile.PropFileService;
import logic.utils.FlowUtils;
import logic.utils.ParseUtils;
import logic.utils.PluginUtils;
import logic.utils.PropfileUtils;

public class START {
	public static void main(String args[]) {
		SmtpListener.startPkg();
		
	}
	
}
