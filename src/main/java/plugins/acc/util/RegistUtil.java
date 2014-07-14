package plugins.acc.util;

import java.util.ArrayList;

import data.Message;

public class RegistUtil {
	private RegistUtil() {}
	
	public static String getAccPropfileName(String domconfPropFileName) {
		return domconfPropFileName+"_acc";
	}
	
	public static String createInsertQuery(String dummy, String sender, String content, String user_name) {
		
		String insert_query = "INSERT INTO acc_tables(sender,dummy,content,user_name) "
				+ "VALUES('"+sender+"','"+dummy+"','"+content+"','"+user_name+"')";
		
		return insert_query;
	}
	
	public static ArrayList<String> createRegistBody(Message oMsg, String dummy, String user_name, String content){
		ArrayList<String> body = new ArrayList<String>();
		
		body.add("Regist your account.");
		body.add("service:"+content);
		body.add("name:"+user_name);
		body.add("Issue your mail-address.");
		body.add("email:"+dummy);
		body.add("Please regist on this mail-address.");
		body.add(".");
		
		return body;
	}
	
	public static String strExtract(String str, String beginStr) {
		int start = str.indexOf(beginStr);
		return str.substring(start+1);
	}
	
	public static String getDummy(String user_name, String content, String recipient) {
		String domain = getDomain(recipient);
		String dummy = user_name+"@"+content+"."+domain;
		return dummy;
	}
	
	public static String getDomain(String str) {
		int num_at = str.indexOf("@");
		return str.substring(num_at+1);
	}
	
	
}
