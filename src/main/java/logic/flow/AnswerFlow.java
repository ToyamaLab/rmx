package logic.flow;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logic.RmxController;
import logic.parse.User;
import logic.parse.SOP.parserVisitor;
import data.Message;

public class AnswerFlow {
	//
	private Message oMsg;
	private ResourceBundle domconfBundle;
	private ResourceBundle envBundle;
	private parserVisitor user_info;
	private static final Logger log = LoggerFactory.getLogger(RmxController.class);
	
	public AnswerFlow(Message oMsg, ResourceBundle domconfBundle, ResourceBundle envBundle, parserVisitor user_info) {
		this.oMsg = oMsg;
		this.envBundle = envBundle;
		this.domconfBundle = domconfBundle;
		this.user_info = user_info;
	}
	
	public void startAnswer() {
		
	}
}
