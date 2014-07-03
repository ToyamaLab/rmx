package logic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import logic.parse.User;
import logic.propfile.PropfileOperator;

public class ParseService {
	private ParseService() {}
	/**
	 * 使用されているルールが全て、生成ルールの時trueを返し、それ以外のときはfalseを返す
	 * */
	public static boolean checkForAnswer(ArrayList<String> keys, ResourceBundle domconfBundle) {
		boolean[] key_answer_flg = null;
		boolean NormalFlag = false;
		boolean AnswerFlag = false;
		boolean ERROR = false;

		if (keys.size() < 1) {
			return false;
		} 

		try {
			if(keys.size() > 1){
				key_answer_flg = new boolean[keys.size()];
			}
			
			for (int i = 0; i < keys.size(); i++) {
				String key = (String) keys.get(i);
				try {
					if (domconfBundle.getString(key).equalsIgnoreCase("generate")){
						if(keys.size()>1){key_answer_flg[i] = true;}	
						AnswerFlag = true;
					} 
					else {
						if(keys.size()>1){key_answer_flg[i] = false;}
						NormalFlag = true;
					}
				} catch (Exception E) {
					// when enter here, there is no ruleAnswer. means this is delivery rule.
						ERROR = true;
						System.out.println("error in checkforanswer");
				}
			}


			if (ERROR) {
				System.out.println("# Error: Include illegal rule that is not for Answer!");
				return false;
			}
			
			if(NormalFlag && AnswerFlag){
				System.out.println("both transfer and answer rule(Parse)");
				return false;
			}else if (!NormalFlag && AnswerFlag) {
				System.out.println("only answer rule(Parse)");
				return true;
			}else if(NormalFlag && !AnswerFlag){
				System.out.println("only transfer rule(Parse)");
				return false;
			}else{
				System.out.println("?????");
				return false;
			}
			
		} catch (NullPointerException E) {
			System.out.println("# Error in Parse.checkForAnswer: " + E.toString());
			return false;

		} catch (MissingResourceException E) {

			// The rule query is not define in the file "rule.properties".
			System.out.println("# Error in Parse.checkForAnswer: " + E.toString());
			return false;

		} catch (ClassCastException E) {
			System.out.println("# Error in Parse.checkForAnswer: " + E.toString());
			return false;

		} catch (Exception E) {
			System.out.println("# Error in Parse.checkForAnswer: " + E.toString());
			return false;
		}
	}
	
	/**
	 * 使用されているルールが全て、配送ルールの時trueを返し、それ以外のときはfalseを返す
	 * */
	public static boolean checkForTransfer(ArrayList<String> keys, ResourceBundle domconfBundle) {
		boolean[] key_answer_flg = null;
		boolean NormalFlag = false;
		boolean AnswerFlag = false;
		boolean ERROR = false;

		if (keys.size() < 1) {
			return false;
		} 

		try {
			if(keys.size() > 1){
				key_answer_flg = new boolean[keys.size()];
			}
			
			for (int i = 0; i < keys.size(); i++) {
				String key = (String) keys.get(i);
				try {
					if (domconfBundle.getString(key).equalsIgnoreCase("generate")){
						if(keys.size()>1){key_answer_flg[i] = true;}	
						AnswerFlag = true;
					} 
					else {
						if(keys.size()>1){key_answer_flg[i] = false;}
						NormalFlag = true;
					}
				} catch (Exception E) {
					// when enter here, there is no ruleAnswer. means this is delivery rule.
						ERROR = true;
						System.out.println("error in checkforanswer");
				}
			}


			if (ERROR) {
				System.out.println("# Error: Include illegal rule that is not for Answer!");
				return false;
			}
			
			if(NormalFlag && AnswerFlag){
				System.out.println("both transfer and answer rule(Parse)");
				return false;
			}else if (!NormalFlag && AnswerFlag) {
				System.out.println("only answer rule(Parse)");
				return false;
			}else if(NormalFlag && !AnswerFlag){
				System.out.println("only transfer rule(Parse)");
				return true;
			}else{
				System.out.println("?????");
				return false;
			}
			
		} catch (NullPointerException E) {
			System.out.println("# Error in Parse.checkForAnswer: " + E.toString());
			return false;

		} catch (MissingResourceException E) {

			// The rule query is not define in the file "rule.properties".
			System.out.println("# Error in Parse.checkForAnswer: " + E.toString());
			return false;

		} catch (ClassCastException E) {
			System.out.println("# Error in Parse.checkForAnswer: " + E.toString());
			return false;

		} catch (Exception E) {
			System.out.println("# Error in Parse.checkForAnswer: " + E.toString());
			return false;
		}
	}
	
	/**
	 * 使用されているルールが生成および配送ルールを両方使用している時trueを返し、それ以外はfalseを返す
	 * ただし、ルールが1つのときはfalseを返す
	 * */
	public static boolean checkForMixture (ArrayList<String> keys, ResourceBundle domconfBundle) {
		boolean[] key_answer_flg = null;
		boolean mixture_flg = false;
		
		if (keys.size() < 1) {return false;}
		
		else if(keys.size() > 1){
			key_answer_flg = new boolean[keys.size()];
			for (int i = 0; i < keys.size(); i++) {
				String key = (String) keys.get(i);
				if (domconfBundle.getString(key).equalsIgnoreCase("generate"))
					key_answer_flg[i] = true;
				else 
					key_answer_flg[i] = false;
			}
			
			System.out.println("key_answer_flg num : " + key_answer_flg.length);
			for(int i = 0; i < key_answer_flg.length-1; i++){
				System.out.println("searching key_answer_flg...");
				if(key_answer_flg[i]!=key_answer_flg[i+1]){
					mixture_flg=true;
				}
			}
		}	
		return mixture_flg;
	}
	
	public static String whichForm(String recipient) {
		if(recipient.contains("{"))
			return "functional_form";
		else
			return "natural_form";
	}
	
	//宛先(関数形式)のドメイン名を返す
	public static String getDomainOfFunctional(String _recipient) {
		//@以降を切り取る
		int num_at = _recipient.indexOf("@");
		String fulldomain = _recipient.substring(num_at+1);
		
		//最初の"."以降を切り取る
		int num_dot = fulldomain.indexOf(".");
		String domain = fulldomain.substring(num_dot+1);
		
		return domain;
	}
	
	//宛先(関数形式)のサブドメイン名を返す
	public static String getSubdomainOfFunctional(String _recipient) {
		//@以降を切り取る
		int num_at = _recipient.indexOf("@");
		String fulldomain = _recipient.substring(num_at+1);
		
		//最初の"."以降を切り取る
		int num_dot = fulldomain.indexOf(".");
		String subdomain = fulldomain.substring(0, num_dot);
		
		return subdomain;
	}
	
	//宛先(自然形式)のドメイン名を返す
	public static String getDomainOfNatural(String _recipient) {
		String recipient = new String();
		//2つの#が含まれていれば,その間の文字列を取り除く
		if(_recipient.contains("#")) {
			int sharp_last_num = _recipient.lastIndexOf("#");
			if(sharp_last_num>0)
				recipient = _recipient.substring(sharp_last_num+1);
			else return null;
		}else {recipient = _recipient;}
		
		//ルールの数
		int rule_num;
		//宛先に#が存在しないとき
		
		int num_at = recipient.indexOf("@");
		//@より前
		String account = recipient.substring(0, num_at);
		//@より後
		String fulldomain = recipient.substring(num_at+1);
			
		//accountに"."があれば分割し、無ければnullを返す.
		if(account.contains(".")) {
			String[] accounts = account.split("\\.");
			rule_num = accounts.length;
		}
		//accountに"."が無ければルールは1つ
		else
			rule_num = 1;
			
		//ドメインの中の"."の数はルール数より大きくなければいけない.そうでなければnullを返す
		int dot_num = (fulldomain.split("\\.").length)-1;//ドメインの中の"."の数
		if(dot_num>rule_num) {
			//ex)ルール数が2なら3つめの"."以降がドメイン名である.
			//rule1.rule2.subdomain.domain
			int start_num = searchChr(fulldomain, ".", rule_num+1);
			//start_numが-1でないとき
			if(start_num>0) {
				String domain = fulldomain.substring(start_num+1);
				return domain;
			}
			//start_numが-1のとき
			else return null;
		}
		//ドメインに含まれる"."の数がルール数以下のとき
		else return null; 
	}
	
	//宛先(自然形式)のサブドメイン名を返す
	public static String getSubdomainOfNatural(String _recipient) {
		String recipient = new String();
		//2つの#が含まれていれば,その間の文字列を取り除く
		if(_recipient.contains("#")) {
			int sharp_last_num = _recipient.lastIndexOf("#");
			if(sharp_last_num>0)
				recipient = _recipient.substring(sharp_last_num+1);
			else return null;
		}else {recipient = _recipient;}
		
		//ルールの数
		int rule_num;
		
		int num_at = recipient.indexOf("@");
		//@より前
		String account = recipient.substring(0, num_at);
		//@より後
		String fulldomain = recipient.substring(num_at+1);
			
		//accountに"."があれば分割し、無ければnullを返す.
		if(account.contains(".")) {
			String[] accounts = account.split("\\.");
			rule_num = accounts.length;
		}else
			rule_num = 1;
			
		//ドメインの中の"."の数はルール数より大きくなければいけない.そうでなければnullを返す
		int dot_num = (fulldomain.split("\\.").length)-1;//ドメインの中の"."の数
		if(dot_num>rule_num) {
			//ex)ルール数が2なら3つめの"."以降がドメイン名である.
			//rule1.rule2.subdomain.domain
			int start_num = searchChr(fulldomain, ".", rule_num);
			int end_num = searchChr(fulldomain, ".", rule_num+1);
			//start_numが-1でないとき
			if(start_num>0 && end_num>start_num) {
				String subdomain = fulldomain.substring(start_num+1,end_num);
				return subdomain;
			}
			//start_numが-1のとき
			else return null;
		}
		//ドメインに含まれる"."の数がルール数以下のとき
		else return null; 
	}
	
	//
	public static String trimSharp(String str) {
		int start = str.indexOf("#", 1);
		return str.substring(start+1);
	}
	
	
	//文字列str1でn番目の文字列str2が出現する位置を返す.ただし、str2が含まれなかったり、n番目にstr2が無い場合は-1を返す
	public static int searchChr(String str1, String str2, int n) {
		int return_num = -1;
		for(int i=0;i<n;i++) {
			return_num = str1.indexOf(str2, return_num+1);
		}
		return return_num;
	}
	
	//ドメインとサブドメインから適切な.propertiesファイル名を得る
	public static String checkDomainAndSubdomain(String domain, String subdomain, ArrayList<HashMap<String, String>> domain_set) {
		for(int i=0;i<domain_set.size();i++) {
			HashMap<String, String> domain_map = domain_set.get(i);
			if(domain_map.get("domainName").equalsIgnoreCase(domain) 
					&& domain_map.get("subdomainName").equalsIgnoreCase(subdomain)) {
				return domain_map.get("propFileName");
			}			
		}
		return null;
	}
}
