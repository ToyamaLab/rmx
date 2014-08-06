package controller.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import logic.Incoming;
import logic.MakeMessage;
import logic.MakeMessageSelector;
import logic.Parse;
import logic.SendMail;
import logic.impl.IncomingImpl;
import logic.impl.MakeMessageSelectorImpl;
import logic.impl.ParseImpl;
import logic.impl.SendMailImpl;
import logic.parse.Parsable;

import controller.LogicController;
import data.Message;

/**
 * LogicControllerの実装
 */
public class LogicControllerImpl implements LogicController {
	
	private Socket cSocket;
	private Message oMsg;
	
	
	public LogicControllerImpl(Socket _cSocket){
		cSocket = _cSocket;
	}
	
	@Override
	public void run() {
		this.startlogic();
	}
	
	private void startlogic(){
		try{
			Incoming ic = new IncomingImpl(cSocket);
			ic.getMail();
			oMsg = ic.getMessage();
			Parse parse = new ParseImpl(oMsg);
			Parsable parser = parse.getParser();
						
			MakeMessageSelector mms = new MakeMessageSelectorImpl();
			MakeMessage mm = mms.select(parser, parse.getDomBundle());
			List<Message> sendMessages = mm.make(oMsg, parse);
			SendMail sm = new SendMailImpl();
			for (int i = 0; i < sendMessages.size(); i++)
				sm.send(sendMessages.get(i));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Message> startLogic(Message originalMsg) {
		return null;
	}

	@Override
	public Message bodyLogic(Message originalMsg) {
		return null;
	}

}
