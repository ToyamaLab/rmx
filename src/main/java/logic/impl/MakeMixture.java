package logic.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import dao.DatabaseDao;
import dao.impl.DatabaseDaoImpl;
import data.Message;
import logic.MakeMessage;
import logic.Parse;
import logic.parse.Parsable;

/**
 * {@link MakeMessage}の実装
 * DeliveryとGenerateルールが両方使われているとき、この具象クラスを用いる。
 */
public class MakeMixture implements MakeMessage {
	
	private Parsable parser;
	private static final String CRLF = "\r\n";

	public MakeMixture(Parsable _parser) {
		parser = _parser;
	}

	/**
	 * @inheritDoc
	 * TODO 要実装
	 */
	@Override
	public List<Message> make(Message oMsg, Parse parse) {
		
		ArrayList<Message> sMsgs = new ArrayList<Message>();
		
		ArrayList<String> body = new ArrayList<String>(); 
		DatabaseDao db = new DatabaseDaoImpl(parse.getDomBundle());
		ResultSet rs;
		ArrayList<String> geneQueries = parser.getGeneQueries();
		ArrayList<String> geneParas = parser.getGeneParas();
		ArrayList<Integer> geneParaNums = parser.getGeneParaNums();
		int numIndex = 0;	/* index for paraNums, which is needed to look for sublist of paras */
		int fromIndex = 0;	/* fromIndex of sublist of paras */
		int toIndex = 0;	/* toIndex of sublist of paras */
		
		/* combine results of some generation rules */
		for (int i = 0; i < geneQueries.size(); i++) {
			for (; numIndex < geneParaNums.size(); numIndex++) {
				int num = geneParaNums.get(numIndex);
				if (num > 0)
					toIndex += num;
				else if (num == 0)
					toIndex++;
				else {
					numIndex++;
					break;
				}
			}
			
			try {
				rs = db.read(geneQueries.get(i), geneParas.subList(fromIndex, toIndex).listIterator());

				if (i > 0)
					body.add(CRLF);
				while (rs.next()) {
					StringBuilder sb = new StringBuilder();
					for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
						if (j == 1)
							sb.append(rs.getString(j));
						else
							sb.append(", " + rs.getString(j));
					}
					body.add(sb.toString());
				}
				rs.close();
				db.close();

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			fromIndex = toIndex;
		}
		body.add(".");
		
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
			for (int j = 0; j < body.size(); j++) {
				sMsg.addBody(body.get(j));
			}
			sMsgs.add(sMsg);
		}
		
		return sMsgs;
	}

}
