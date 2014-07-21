package logic.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import logic.Incoming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.Message;
import data.State;

/**
 * Incomingの実装
 */
public class IncomingImpl implements Incoming {
	
	private Socket cSocket;
	private BufferedReader in;
	private OutputStreamWriter out;
	private State connState;
	private Message oMsg;
	private static final Logger log = LoggerFactory.getLogger(IncomingImpl.class);
	
	public IncomingImpl(Socket _cSocket) throws IOException {
		cSocket = _cSocket;
		in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
		out = new OutputStreamWriter(cSocket.getOutputStream());
		connState = new State(cSocket);
		oMsg = new Message();
		
		//Acknowledge 220.
	}
	
	@Override
	public void getMail(){
		
	}
	
	private void sendAck(String acknoledgement) throws IOException {
		// TODO Auto-generated method stub
		
	}

	private void conversation() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Message getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
