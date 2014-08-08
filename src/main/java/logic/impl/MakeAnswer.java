package logic.impl;

import java.util.List;
import java.util.ResourceBundle;

import logic.MakeMessage;
import logic.Parse;
import logic.parse.Parsable;
import data.Message;

/**
 * {@link MakeMessage}の実装
 */
public class MakeAnswer implements MakeMessage {
	
	private ResourceBundle domBundle;
	private ResourceBundle envBundle;
	private Parsable parser;
	
	public MakeAnswer (Parsable _parser, ResourceBundle _domBundle) {
		envBundle = OpenPropFileImpl.getInstance().getEnvBundle();
		domBundle = _domBundle;
		parser = _parser;
	}
	
	/**
	 * @inheritDoc
	 * TODO 要実装
	 */
	@Override
	public List<Message> make(Message oMsg, Parse parse) {
		// TODO Auto-generated method stub
		return null;
	}

}
