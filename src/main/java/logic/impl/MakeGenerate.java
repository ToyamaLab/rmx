package logic.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.MakeMessage;
import logic.Parse;
import logic.parse.Parsable;
import dao.DatabaseDao;
import dao.impl.DatabaseDaoImpl;
import data.Message;

/**
 * {@link MakeMessage}の実装
 * Generateのみのルールのとき、この具象クラスを用いる。
 */
public class MakeGenerate implements MakeMessage {
	
	private Parsable parser;
	private static final String CRLF = "\r\n";

	public MakeGenerate(Parsable _parser) {
		parser = _parser;
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public List<Message> make(Message oMsg, Parse parse) {
		
		ArrayList<Message> sMsgs = new ArrayList<Message>();
		
		Message sMsg = new Message();
		sMsg.setSender(oMsg.getSender());
		sMsg.setRecipient(oMsg.getSender());
		for (int i = 0; i < oMsg.getHeader().size(); i++) {
			sMsg.addHeader(oMsg.getHeader().get(i));
		}
		sMsg.setSubject(oMsg.getSubject());
		
		DatabaseDao db = new DatabaseDaoImpl(parse.getDomBundle());
		ResultSet rs;
		ArrayList<String> queries = parser.getGeneQueries();
		ArrayList<String> paras = parser.getGeneParas();
		ArrayList<Integer> paraNums = parser.getGeneParaNums();
		int numIndex = 0;	/* index for paraNums, which is needed to look for sublist of paras */
		int fromIndex = 0;	/* fromIndex of sublist of paras */
		int toIndex = 0;	/* toIndex of sublist of paras */
		
		/* combine results of some generation rules */
		for (int j = 0; j < queries.size(); j++) {
			for (; numIndex < paraNums.size(); numIndex++) {
				int num = paraNums.get(numIndex);
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
				rs = db.read(queries.get(j), paras.subList(fromIndex, toIndex).listIterator());

				if (j > 0)
					sMsg.addBody(CRLF);
				while (rs.next()) {
					StringBuilder sb = new StringBuilder();
					for (int k = 1; k <= rs.getMetaData().getColumnCount(); k++) {
						if (k == 1)
							sb.append(rs.getString(k));
						else
							sb.append(", " + rs.getString(k));
					}
					sMsg.addBody(sb.toString());
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
		sMsg.addBody(".");
		
		sMsgs.add(sMsg);
			
		return sMsgs;
	}

}
