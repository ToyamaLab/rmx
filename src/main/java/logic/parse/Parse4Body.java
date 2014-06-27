package logic.parse;


import java.util.ArrayList;
import java.util.ResourceBundle;
/**
 * 本文に書かれたものからデータベースへ問い合わせするためのSQLを返すためのクラス。<br/>
 * 本文を読みこむためのパーザを後日作成するため、なるべく使用しないことを推奨。
 */
public class Parse4Body {
	
	private ResourceBundle domBundle;
	private StringBuffer query;
	private ArrayList<String> parameter;
	
	public Parse4Body(ResourceBundle _domBundle) {
		domBundle = _domBundle;
		query = new StringBuffer();
		parameter = new ArrayList<String>();
	}

	
	public void setParse(String tag) {
		String[] keyvalue = this.separateTag(tag);
		String key = keyvalue[0];
		String value = keyvalue[1];
		String condquery = new String();
		parameter.clear();
		
		String[] or = value.split("\\+");
		for(int i = 0; i < or.length; i++){
			String buffer = or[i];
			condquery = domBundle.getString(key + "[1]");
			if(domBundle.getString(key + "Type").equalsIgnoreCase("integer")){
				condquery = generateQueryPara(condquery, buffer, "integer");
			}
			else if (domBundle.getString(key + "Type").equalsIgnoreCase("string")) {
				condquery = generateQueryPara(condquery, buffer, "string");
			}
			query.append("(" + condquery + ")");
		}
		return;
	}


	private String generateQueryPara(String condquery, String buffer, String type) {
		String tmp;
		while(condquery.indexOf("$") > -1) {
			parameter.add(type);
			parameter.add(buffer);
			tmp = condquery.replaceFirst("\\$1", "?");
			condquery = tmp;
		}
		return condquery;
	}
	
	private String[] separateTag(String tag) {
		String[] keyvalue = new String[2];
		keyvalue[0] = tag.substring(tag.indexOf("<if") + "<if".length(), tag.indexOf("{")).trim();
		keyvalue[1] = tag.substring(tag.indexOf("{") + "{".length(), tag.indexOf("}"));
		
		return keyvalue;
	}
	

	public String getQuery() {
		return query.toString();
	}

	public ArrayList<String> getParameter() {
		return parameter;
	}

}
