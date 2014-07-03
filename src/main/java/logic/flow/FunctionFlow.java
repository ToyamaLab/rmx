package logic.flow;

import java.util.ArrayList;
import java.util.ResourceBundle;

import logic.RmxController;
import logic.interfaces.PluginInterface;
import logic.parse.User;
import logic.plugin.MailProvider;
import logic.plugin.PluginsHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import presentation.mail.SendMailService;
import data.Message;

public class FunctionFlow {
	//
	private Message oMsg;
	private ResourceBundle domconfBundle;
	private ResourceBundle envBundle;
	private User user;
	private String propfile;
	private static final Logger log = LoggerFactory.getLogger(RmxController.class);
	
	public FunctionFlow(
			Message oMsg,
			ResourceBundle envBundle,
			ResourceBundle domconfBundle,
			User user,
			String propfile) {
		this.oMsg = oMsg;
		this.envBundle = envBundle;
		this.domconfBundle = domconfBundle;
		this.user = user;
		this.propfile = propfile;
	}
	
	public void startFunction() {
		//プラグインを入手
		PluginsHolder p_holder = new PluginsHolder();
		ArrayList<PluginInterface> plugins = p_holder.holdPlugins();
		PluginInterface plugin = p_holder.selectPlugin(plugins, user.getFunction());
		
		//送信用メッセージ
		ArrayList<Message> sMsgs = new ArrayList<Message>();
		MailProvider mp = new MailProvider();
		sMsgs = mp.createMails(plugin, oMsg, propfile, domconfBundle, user);
		
		//メールの送信
		log.info("Mail:{} -> {}", oMsg.getSender(), oMsg.getRecipient());
		SendMailService sm = new SendMailService();
		for(int i=0;i<sMsgs.size();i++)
			sm.sendMail(sMsgs.get(i), envBundle);
	}
}
