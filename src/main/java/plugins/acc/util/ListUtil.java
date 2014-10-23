package plugins.acc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import dao.DatabaseDao;
import dao.impl.DatabaseDaoImpl;

public class ListUtil {
	//
	
	//
	private ListUtil() {}
	
	public static ArrayList<String> createListBody(String sender, String user_name, ResourceBundle domBundle) {
		ArrayList<String> body = new ArrayList<String>();
		String select_query = createSelectQuery(sender, user_name);
		DatabaseDao dbdao = new DatabaseDaoImpl(domBundle);
		ResultSet rs;
		ArrayList<HashMap<String, String>> alist = new ArrayList<HashMap<String,String>>();
		try {
			rs = dbdao.read(select_query, null);
			while(rs.next()) {
				HashMap<String, String> hmap = new HashMap<String, String>();
				hmap.put("content", rs.getString(1));
				hmap.put("dummy", rs.getString(2));
				alist.add(hmap);
			}
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		body.add("your service list.");
		for(int i=0;i<alist.size();i++) {
			body.add("content:"+alist.get(i).get("content")+"/dummy:"+alist.get(i).get("dummy"));
		}
		return body;
	}
	
	private static String createSelectQuery(String sender, String user_name) {
		String select_query = "SELECT content, dummy FROM acc_tables WHERE sender = '"+sender+"' AND user_name = '"+user_name+"'";
		return select_query;
	}

}
