package logic.flow;

import java.util.ArrayList;
import java.util.ResourceBundle;

import logic.impl.OpenPropFileImpl;
import logic.impl.SmtpListener;
import logic.interfaces.PluginInterface;
import logic.parse.User;
import logic.parse.User1;
import logic.parse.SOP.parserVisitor;
import logic.plugin.PluginsHolder;
import logic.utils.FlowUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import presentation.mail.SendMailService;
import data.Message;

public class FunctionFlow {
	//メンバ変数
	private Message oMsg;
	private ResourceBundle domBundle;
	private ResourceBundle envBundle;
	private parserVisitor userInfo;
	private String propfile;
	private String domain;
	private static final Logger log = LoggerFactory.getLogger(FunctionFlow.class);
	private OpenPropFileImpl pf = OpenPropFileImpl.getInstance();
	private User funcUser;
	private User1 funcUser1;
	
	//コンストラクタ
	public FunctionFlow(
			Message oMsg,
			ResourceBundle domBundle,
			parserVisitor userInfo,
			String propfile,
			String domain) {
		this.oMsg = oMsg;
		this.envBundle = pf.getEnvBundle();
		this.domBundle = domBundle;
		this.userInfo = userInfo;
		this.propfile = propfile;
		this.domain = domain;
		funcUser = new User();
		funcUser1 = new User1();
	}
	
	public void startFunction() {
		// 1. function,command,commandArgs,recipientsを得る
		//ex.#random.shuffle.50#team{rmx}@example.comのとき
		//function->random,command->shuffle,commandArgs->[50],recipients-[kita@~,matt@~,…]
		String function = userInfo.getFunction();
		String command = userInfo.getCommand();
		ArrayList<String> commandArgs = userInfo.getCommandArgs();
		
		// 2. 2つめの#以降を切り取り、targetがあれば再びパースし、そうでなければ送信者が宛先になる
		// (ex)#~#team{rmx}@keio.com -> team{rmx}@keio.com
		// (ex)#~#@keio.com -> @keio.com
		ArrayList<String> recipients = new ArrayList<String>();
		String target = userInfo.getTarget();
		if(target.indexOf("@")==0)//@keio.comのとき
			recipients.add(oMsg.getSender());
		else {//team{rmx}@keio.comのとき
			if(FlowUtils.whichForm(target).equalsIgnoreCase("functionalForm")) {
				funcUser.UserStart(target, domBundle);
				recipients = FlowUtils.getRecipients(funcUser, domBundle);
			}
			else {
				funcUser1.User1Start(target, domBundle, domain);
				recipients = FlowUtils.getRecipients(funcUser1, domBundle);
			}
		}
		
		// 3. src/main/java/pluginsの中にあるプラグインを全て入手
		PluginsHolder p_holder = new PluginsHolder();
		ArrayList<PluginInterface> plugins = p_holder.holdPlugins();
		
		// 4. function名に合ったプラグイン1つを入手
		PluginInterface plugin = p_holder.selectPlugin(plugins, function);
		
		// 5. 送信用メッセージを作成
		ArrayList<Message> sMsgs = new ArrayList<Message>();
		// 6. 送信用メッセージを得る
		sMsgs = plugin.pluginStart(oMsg, function, command, commandArgs, recipients, domBundle, propfile);
		
		// 7. メールの送信
		log.info("Mail:{} -> {}", oMsg.getSender(), oMsg.getRecipient());
		SendMailService sm = new SendMailService();
		for(int i=0;i<sMsgs.size();i++)
			sm.sendMail(sMsgs.get(i), envBundle);
	}
}
