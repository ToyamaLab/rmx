package plugins.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import logic.interfaces.PluginInterface;
import logic.Parse;
import logic.impl.ParseImpl;
import logic.parse.Parsable;
import logic.authorization.AuthorizeSender;
import logic.authorization.impl.AuthorizeSenderImpl;
import data.Message;

public class AuthorizationTestPlugin implements PluginInterface{

	@Override
	public List<Message> pluginStart(
			Message oMsg,
			String function,
			String command,
			List<String> commandArgs,
			List<String> recipients,
			ResourceBundle domconfBundle,
			String propfile) {

		ArrayList<Message> sMsgs = new ArrayList<Message>();
		if (command.equalsIgnoreCase("test")) {
			String funcAddress = oMsg.getRecipient();
			String testAddress = funcAddress.substring(funcAddress.indexOf("#", 1) + 1);
			
			oMsg.setRecipient(testAddress);
			Parse parse = new ParseImpl(oMsg);
			Parsable parser = parse.getParser();
			
			AuthorizeSender as = new AuthorizeSenderImpl(domconfBundle, parser, oMsg.getSender());
		
			Message sMsg = new Message();
			sMsg.setRecipient(oMsg.getSender());
			sMsg.setSender(oMsg.getSender());
			sMsg.setSubject("Authorization Test Result");
			if (as.isAuthorized())
				sMsg.addBody(testAddress + " is authorized.");
			else {
				sMsg.addBody(testAddress + " is NOT authorized.");
				sMsg.addBody("Unauthorized Rule: " + as.getUnauthorizedRulesStr());
			}
			sMsg.addBody(".");
			for (int j = 0; j < oMsg.getHeader().size(); j++) {
				sMsg.addHeader(oMsg.getHeader().get(j));
			}
			
			sMsgs.add(sMsg);

		} else {
			Message sMsg = new Message();
			sMsg.setRecipient(oMsg.getSender());
			sMsg.setSender(oMsg.getSender());
			sMsg.setSubject("error");
			sMsg.addBody("none command or none input address");
			sMsg.addBody(".");
			
			sMsgs.add(sMsg);
		}
		
		return sMsgs;
	}

	@Override
	public ArrayList<String> getAvailableFunctionNames() {
		String function = "auth";
		ArrayList<String> functions = new ArrayList<String>();
		functions.add(function);
		return functions;
	}

}
