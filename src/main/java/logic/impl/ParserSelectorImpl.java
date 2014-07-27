package logic.impl;

import logic.ParserSelector;
import logic.parse.Parsable;
import logic.parse.User;
import logic.parse.User1;

/**
 * {@link logic.ParserSelector}の実装
 */
public class ParserSelectorImpl implements ParserSelector {

	/**
	 * @inheritDoc
	 * 
	 * 
	 * "{}"の有無をチェックして、User, User1のオブジェクトを返す。
	 */
	@Override
	public Parsable select(String recipient) {
		if(recipient.contains("{") && recipient.contains("}"))
			return new User();
		else
			return new User1();
	}

}
