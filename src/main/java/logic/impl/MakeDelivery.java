package logic.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.MakeMessage;
import logic.Parse;
import logic.authorization.AuthorizeSender;
import logic.authorization.MakeWarning;
import logic.authorization.impl.AuthorizeSenderImpl;
import logic.authorization.impl.MakeWarningImpl;
import logic.parse.Parsable;
import dao.DatabaseDao;
import dao.impl.DatabaseDaoImpl;
import data.Message;

/**
 * {@link MakeMessage}の実装
 * Deliveryのみのルールのとき、この具象クラスを用いる。
 */
public class MakeDelivery implements MakeMessage {

	private Parsable parser;

	public MakeDelivery(Parsable _parser) {
		parser = _parser;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public List<Message> make(Message oMsg, Parse parse) {
		
		ArrayList<Message> sMsgs = new ArrayList<Message>();
		
	    // 送信許可
		AuthorizeSender as = new AuthorizeSenderImpl(parse.getDomBundle(), parser, oMsg.getSender());
		if (!as.isAuthorized()) {
			MakeWarning mw = new MakeWarningImpl();
			sMsgs.add(mw.makeWarningMessage(oMsg, as.getUnauthorizedRules()));
			return sMsgs;
		}
		
		// 通常
		DatabaseDao db = new DatabaseDaoImpl(parse.getDomBundle());
		ResultSet rs;
		ArrayList<String> finalrecipients = new ArrayList<String>();
		try {
			rs = db.read(parser.getQuery(), parser.getParas().listIterator());

			while (rs.next())
				finalrecipients.add(rs.getString(1));
			rs.close();
			db.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < finalrecipients.size(); i++) {
			Message sMsg = new Message();

			sMsg.setSender(oMsg.getSender());
			sMsg.setRecipient(finalrecipients.get(i));
			for (int k = 0; k < oMsg.getHeader().size(); k++) {
				sMsg.addHeader(oMsg.getHeader().get(k));
			}
			sMsg.setSubject(oMsg.getSubject());
			for (int j = 0; j < oMsg.getBody().size(); j++) {
				sMsg.addBody(oMsg.getBody().get(j));
			}
			sMsgs.add(sMsg);
		}
		return sMsgs;
	}

}
