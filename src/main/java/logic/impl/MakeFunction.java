package logic.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import logic.MakeMessage;
import logic.Parse;
import logic.interfaces.PluginInterface;
import logic.parse.Parsable;
import logic.plugin.PluginsHolder;
import dao.DatabaseDao;
import dao.impl.DatabaseDaoImpl;
import data.Message;

/**
 * {@link MakeMessage}の実装
 * Functionの時、この具象クラスを用いる。
 */
public class MakeFunction implements MakeMessage {

	private Parsable parser;
	private ResourceBundle domBundle;
	private ResourceBundle envBundle;
	private String propfile;
	
	public MakeFunction(Parsable _parser, ResourceBundle _domBundle) {
		parser = _parser;
		envBundle = OpenPropFileImpl.getInstance().getEnvBundle();
		domBundle = _domBundle;
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public List<Message> make(Message oMsg, Parse parse) {
		// 1. function, command, commandArgs, recipientsを得る
		// ex. #random.shuffle.50#team{rmx}@example.comのとき
		// function->random, command->shuffle, commandArgs->[50], recipients-[kita@~,matt@~,…]
		String function = parser.getFunction();
		String command = parser.getCommand();
		List<String> commandArgs = parser.getCommandArgs();
		
		// 2. 2つめの#以降を切り取り、targetがなければ送信者が宛先になる
		// (ex)#~#team{rmx}@keio.com -> team{rmx}@keio.com
		// (ex)#~#@keio.com -> @keio.com
		List<String> recipients = new ArrayList<String>();
		String target = parser.getTarget();
		if (target.indexOf("@") == 0) {
			recipients.add(oMsg.getSender());
		} else {
			try{
				DatabaseDao db = new DatabaseDaoImpl(domBundle);
				ResultSet rs;
				rs = db.read(parser.getQuery(), parser.getParas().listIterator());
				while (rs.next())
					recipients.add(rs.getString(1));
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		// 3. src/main/java/pluginsの中にあるプラグインを全て入手
		PluginsHolder p_holder = new PluginsHolder();
		ArrayList<PluginInterface> plugins = p_holder.holdPlugins();

		// 4. function名に合ったプラグイン1つを入手
		PluginInterface plugin = p_holder.selectPlugin(plugins, function);

		// 5. 送信用メッセージを作成		
		List<Message> sMsgs = new ArrayList<Message>();

		// 6. 送信用メッセージを得る		
		sMsgs = plugin.pluginStart(oMsg, function, command, commandArgs, recipients, domBundle, propfile);

		return sMsgs;
	}

}
