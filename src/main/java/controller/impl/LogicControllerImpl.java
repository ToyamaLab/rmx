package controller.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import logic.Incoming;
import logic.impl.IncomingImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.LogicController;
import data.Message;
import data.State;

/**
 * LogicControllerの実装
 */
public class LogicControllerImpl implements LogicController {
	
	private Socket cSocket;
	private Message oMsg;
	private static final Logger log = LoggerFactory.getLogger(LogicControllerImpl.class);
	private State connState;
	
	
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
		} catch (IOException e) {
			
		}
	}

	@Override
	public List<Message> startLogic(Message originalMsg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message bodyLogic(Message originalMsg) {
		// TODO Auto-generated method stub
		return null;
	}

}
