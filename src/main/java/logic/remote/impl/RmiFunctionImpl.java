package logic.remote.impl;

import java.util.ArrayList;
import java.util.List;

import logic.remote.RmiFunction;
import data.Message;

/**
 * RmiFunctionの実装
 */
public class RmiFunctionImpl implements RmiFunction {
	
	private List<Object> info;
	
	public RmiFunctionImpl() {
		info = new ArrayList<Object>();
	}
		
	@Override
	public void makeSendMailsInfo(Message oMsg, ArrayList<String> recipients) {
		info.add(oMsg);
		info.add(recipients);
	}
	
	@Override
	public List<Object> getSendMailsInfo() {
		return info;
	}

}
