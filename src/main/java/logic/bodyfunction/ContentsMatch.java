package logic.bodyfunction;

import java.util.*;
import java.sql.*;

import dao.DBDao;
import logic.parse.*;

public class ContentsMatch {
	
	private final String BEGINTAG = "<if";
	private final String ENDTAG = "</if>";
	
	private ArrayList<ArrayList<String>> queryResult;

	public ContentsMatch() {
		queryResult = new ArrayList<ArrayList<String>>();
	}
	
	/**
	 * body(メール本文)内に開始タグ(<if)が含まれていればtrue、そうでなければfalseを返す
	 * @param _body メール本文
	 * @return boolean
	 * */
	public boolean checkUse(ArrayList<String> _body) {
		Iterator<String> itr = _body.iterator();
		while (itr.hasNext()) {
			if (itr.next().indexOf(BEGINTAG) > -1)
				return true;
		}
		return false;
	}

	/**
	 * エラーをチェック
	 * @param body メール本文
	 * @return boolean
	 * */
	public boolean checkErr(ArrayList<String> _body) {		
		if (isCorrectOrder(_body)) {
			return false;
			}
		else
			return true;
	}
	
	public void getResults(ArrayList<String> _body, ResourceBundle domBundle){
		
		ArrayList<String> tags = this.findTags(_body);
		DBDao db = new DBDao(domBundle);
		Parse4Body parse = new Parse4Body(domBundle);
		for(int i = 0; i < tags.size(); i++){
			parse.setParse(tags.get(i));
			try {
				ResultSet rs = db.bodyRead(parse);
				ArrayList<String> queryResultpart = new ArrayList<String>();
				while(rs.next()){
					queryResultpart.add(rs.getString(1));
				}
				rs.close();
				queryResult.add(queryResultpart);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
	}
	
	
	public ArrayList<String> editBody(ArrayList<String> _body, String _recipient){
		return this.editBody(_body, _recipient, queryResult, 0);	
	}
	
	private ArrayList<String> editBody(ArrayList<String> _body, String _recipient, ArrayList<ArrayList<String>> _queryResult, int queryResultnum){
		
		String startTagOutside = new String();
		String startTagInside = new String();
		String endTagInside = new String();
		String endTagOutside = new String();
		int startIndex = 0; 
		int endIndex = 0;

		ArrayList<String> edited = new ArrayList<String>();
		ArrayList<String> insideBody = new ArrayList<String>();
		ArrayList<String> bottomBody = new ArrayList<String>();
		
		int tagStack = 0;
		for (int i = 0; i < _body.size(); i++){
			String part = _body.get(i);
			if (part.indexOf(BEGINTAG) > -1){
				if (tagStack == 0){
					startIndex = i;
					startTagOutside = part.substring(0, part.indexOf(BEGINTAG));
					startTagInside = part.substring(part.indexOf(">") + ">".length());
				}
				tagStack++;
				continue;
			}
			if (part.indexOf(ENDTAG) > -1){
				tagStack--;
				if (tagStack == 0) {
					endIndex = i;
					endTagInside = part.substring(0, part.indexOf(ENDTAG));
					endTagOutside = part.substring(part.indexOf(ENDTAG) + ENDTAG.length());
					break;
				}
			}
		}
		
		for (int j = 0; j < startIndex; j++)
			edited.add(_body.get(j).trim());
		if (!this.hasNothing(startTagOutside))
			edited.add(startTagOutside.trim());
		
		ArrayList<String> queryResultpart = _queryResult.get(queryResultnum);
		if (queryResultpart.contains(_recipient)){
			if (!this.hasNothing(startTagInside))
				insideBody.add(startTagInside.trim());
			for (int k = startIndex + 1; k < endIndex; k++)
				insideBody.add(_body.get(k).trim());
			if (!this.hasNothing(endTagInside))
				insideBody.add(endTagInside.trim());
		}
		
		if (!this.hasNothing(endTagOutside))
			bottomBody.add(endTagOutside.trim());
		for (int l = endIndex + 1; l < _body.size(); l++){
			if (!this.hasNothing(_body.get(l)))
				bottomBody.add(_body.get(l).trim());
		}
		if (queryResultnum >= queryResult.size() - 1 && !queryResultpart.contains(_recipient)){
			edited.addAll(bottomBody);
			return edited;
		}
		if (this.checkUse(insideBody))
			edited.addAll(this.editBody(insideBody, _recipient, queryResult, queryResultnum++));
		else
			edited.addAll(insideBody);
		if (this.checkUse(bottomBody))
			edited.addAll(this.editBody(bottomBody, _recipient, queryResult, queryResultnum++));
		else
			edited.addAll(bottomBody);
		return edited;
	}
	
	
	private boolean isCorrectOrder(ArrayList<String> _body) {
		Iterator<String> itr = _body.iterator();
		int count = 0;
		while(itr.hasNext()){
			String part = itr.next();
			while(part.indexOf(BEGINTAG) > -1 || part.indexOf(ENDTAG) > -1){
				while((part.indexOf(BEGINTAG) == -1 || part.indexOf(BEGINTAG) > part.indexOf(ENDTAG))
				&& part.indexOf(ENDTAG) > -1){
					count--;
					if(count < 0)
						return false;
					else
						part = part.substring(part.indexOf(ENDTAG) + ENDTAG.length());
				}
				while((part.indexOf(ENDTAG) == -1 || part.indexOf(ENDTAG) > part.indexOf(BEGINTAG))
				&& part.indexOf(BEGINTAG) > -1){
					count++;
					part = part.substring(part.indexOf(">", part.indexOf(BEGINTAG)) + ">".length());
				}
			}
		}
		if(count == 0)
			return true;
		else 
			return false;
	}
	
	private ArrayList<String> findTags(ArrayList<String> _body){
		ArrayList<String> tag = new ArrayList<String>();
		Iterator<String> itr = _body.iterator();
		while(itr.hasNext()){
			String part = itr.next();
			if (part.indexOf(BEGINTAG) > -1)
				tag.add(part.substring(part.indexOf("<"), part.indexOf(">") + ">".length()));
			}
		return tag;
	}


	private boolean hasNothing(String part){
		part = part.trim();
		if (part.equals("") || part.equals(null) || part.equals("\n"))
			return true;
		else return false;
	}
}
