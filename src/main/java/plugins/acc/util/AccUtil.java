package plugins.acc.util;

import java.util.ArrayList;

import data.Message;

public class AccUtil {
	private AccUtil() {}
	
	public static String getAccPropfileName(String domconfPropFileName) {
		return domconfPropFileName+"_acc";
	}
	
	public static String createInsertQuery(String recipient, String sender, ArrayList<String> commandArgs) {
		//宛先の@以降を切り取る
		//ex.#~#@test.com=>test.com
		String domain_name = strExtract(recipient, "@");
		
		//仮のアドレス
		//#acc.regist.mixi.kita#rmx.keio.com=>kita@mixi.rmx.keio.com
		String alt_address = commandArgs.get(1)+"@"+commandArgs.get(0)+domain_name;
		
		String insert_query = "INSERT INTO acc_tables(address,alt_address,arg1,arg2) "
				+ "VALUES('"+sender+"','"+alt_address+"','"+commandArgs.get(0)+"','"+commandArgs.get(1)+"')";
		
		return insert_query;
	}
	
	public static ArrayList<String> createRegistBody(Message oMsg, ArrayList<String> commandArgs){
		ArrayList<String> body = new ArrayList<String>();
		String arg1 = commandArgs.get(0);
		String arg2 = commandArgs.get(1);
		
		body.add("Regist your account.");
		body.add("service:"+arg1);
		body.add("name:"+arg2);
		body.add("Issue your mail-address.");
		body.add("email:");
		body.add("Please regist on this mail-address.");
		
	}
	
	public static String strExtract(String str, String beginStr) {
		int start = str.indexOf(beginStr);
		return str.substring(start+1);
	}
	
	
}
