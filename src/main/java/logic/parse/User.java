package logic.parse;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import logic.parse.SOP.*;

public class User implements Parsable {
	
	/** ex) db.ics.keio.ac.jp */
	public  String domain;

	/** ex) testo */
	public  String subdomain;

	/** front of @ ex) name{obunai}+grp{speed+wix+ssql} */
	public  String recipient;

	/** Plugin function name ex) event */
	public  String function;
	
	/** function's command name ex) attend,absence*/
	public String command;
	
	/** function's command args*/
	public ArrayList<String> commandArgs;
	
	/** function's target ex) #~#target*/
	public String functionTarget;
	
	/** function flg*/
	public boolean functionFlg;
	
	/** normalFlg*/
	public boolean normalFlg;

	/** ex) obunai, speed+wix+ssql */
	public  ArrayList<String> values;

	/** ex) name, grp */
	public  ArrayList<String> rules;

	/** ex) obunai, speed, wix, ssql */
	public  ArrayList<String> paras;

	/** count parameter for each query */
	private ArrayList<Integer> paraNums;

	/** ture when polymorPara*/
	private boolean polymorFlg;

	/** query before union, intersect or except among rules
	 *  i.g. queries.size = the number of rules */
	private ArrayList<String> queries;

	/** query before union, intersect or except
	 * 	i.g. minimumQueries.size = the number of queries called from conf file */
	private ArrayList<String> minimumQueries;

	/** operateors order by appeared */
	private ArrayList<String> operators;

	/** ex) (select ~) union (~) intersect (~) */
	public  String finalQuery;

	/** ex) testo.properties */
	public  ResourceBundle rb;

	/** ex) name, grp */
	public  String rule;

	/** true if address has paraList until intersection, union, except's leftnode*/
	private boolean paraListFlg;

	/** rule{paraList} */
	private ArrayList<String> paraList;

	/** values for paraList*/
	private ArrayList<String> tmpParas;

	/** Passed parameter index for nested query*/
	private ArrayList<Integer> Passed_para_index;

	/** true if address contains Atmark rule 
	 * ex)course2valuation{DM}@ */
	private boolean containsATmark;

	/***/
	public  int polymorChildNum;

	public  int paraListChildNum;

	/** ex) gradeType = integer,String */
	public  ArrayList<String> polyTypes;
	public  int polyTypesNum;
	public  int polyTypesPointer;
	public  String polyLastType;

	//	public static User visitor;
	//	public static parser parser;

	public User() {
		domain = new String();
		subdomain = new String();
		recipient = new String();
		function = new String();
		command = new String();
		commandArgs = new ArrayList<String>();
		functionTarget = new String();
		values = new ArrayList<String>();
		rules = new ArrayList<String>();
		paras = new ArrayList<String>();
		paraNums = new ArrayList<Integer>();
		queries = new ArrayList<String>();
		operators = new ArrayList<String>();
		paraList = new ArrayList<String>();
		tmpParas = new ArrayList<String>();
		Passed_para_index = new ArrayList<Integer>();
		finalQuery = new String();
		polymorChildNum = 0;
		paraListChildNum = 0;
		rule = new String();
		minimumQueries = new ArrayList<String>();
	}

	public void parseStart(String recipient, ResourceBundle dom, String _domname) {

		System.out.println("==== parser start ====");
		
		this.recipient = recipient;
		User visitor = new User();
		visitor.rb = dom;
		Parser parser = new Parser(new StringReader(recipient));
		try {
			Node start = parser.Recipient();
			System.out.println(start.jjtAccept(visitor, null));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (TokenMgrError e) {
			e.printStackTrace();
		}
		
		visitor.paraNums.add(-1);
		System.out.println(subdomain);
		System.out.println(domain);

		operators = visitor.operators;
		rules = visitor.rules;
		domain = visitor.domain;
		subdomain = visitor.subdomain;
		recipient = visitor.recipient;
		minimumQueries = visitor.minimumQueries;
		paras = visitor.paras;
		queries = visitor.queries;
		this.remakepara();
		paraNums = visitor.paraNums;
		function = visitor.function;
		command = visitor.command;
		commandArgs = visitor.commandArgs;
		functionFlg = visitor.functionFlg;
		normalFlg = visitor.normalFlg;
		paraList = visitor.paraList;
		//		queries = visitor.queries;

		for (int i = 0; i < paras.size(); i++) {
			System.out.println("para : " + paras.get(i).toString());
		}
		
		visitor.finalQuery = this.simplereplace(visitor.finalQuery);
		System.out.println("final query : " + visitor.finalQuery);
		this.checkATmark();

		finalQuery = visitor.finalQuery;

		initPolyTypes();

		System.out.println("==== parser end ====");

	}

	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	public Object visit(ASTRecipient node, Object data) {
		System.out.println("this is : " + node.toString());
		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}
		System.out.println("");

		return node.jjtGetChild(0).jjtAccept(this, null);
	}

	public Object visit(ASTException node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		paraNums.add(-1);
		operators.add("-");
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();

		System.out.println("left query : " + left);
		System.out.println("right query : " + right);

		if(!(left.contains("intersect")|left.contains("union")|left.contains("except")) | paraListFlg){
			//    		queries.add(left);
			if(node.jjtGetChild(0).toString().equalsIgnoreCase("Paralis") && !node.jjtGetChild(1).toString().equalsIgnoreCase("Paralis")){
				paraListFlg = false;		
			}
		}
		if(!(right.contains("intersect")|right.contains("union")|right.contains("except")) | paraListFlg){
			//    		queries.add(right);
			paraListFlg =false;
		}

		System.out.println("");
		String query = " ( " + left + " ) " + " except " + " ( " + right
		+ " ) ";
		return query;

	}

	public Object visit(ASTUnion node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		paraNums.add(-1);
		operators.add("+");
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();

		System.out.println("left query : " + left);
		System.out.println("right query : " + right);

		if(!(left.contains("intersect")|left.contains("union")|left.contains("except")) | paraListFlg){
			//    		queries.add(left);
			if(node.jjtGetChild(0).toString().equalsIgnoreCase("Paralis") && !node.jjtGetChild(1).toString().equalsIgnoreCase("Paralis")){
				paraListFlg = false;		
			}
		}
		if(!(right.contains("intersect")|right.contains("union")|right.contains("except")) | paraListFlg){
			//    		queries.add(right);
			paraListFlg =false;
		} 

		System.out.println("");
		String query = " ( " + left + " ) " + " union " + " ( " + right + " ) ";
		return query;
	}

	public Object visit(ASTIntersection node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		paraNums.add(-1);
		operators.add(".");
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();

		System.out.println("left query : " + left);
		System.out.println("right query : " + right);

		if(!(left.contains("intersect")|left.contains("union")|left.contains("except")) | paraListFlg){
			//    		queries.add(left);
			if(node.jjtGetChild(0).toString().equalsIgnoreCase("Paralis") && !node.jjtGetChild(1).toString().equalsIgnoreCase("Paralis")){
				paraListFlg = false;		
			}
		}
		if(!(right.contains("intersect")|right.contains("union")|right.contains("except")) | paraListFlg){
			//    		queries.add(right);
			paraListFlg =false;
		}

		System.out.println("");
		return " ( " + left + " ) " + " intersect " + " ( " + right + " ) ";
	}

	public Object visit(ASTExp node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		rule = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String query ="";
		
		if(childnum == 1){
			//user defined no parameters
			//i.e. rule{}@example.jp
			paras.add("no parameters");
			paras.add("*");
			paraList.add("*");
			tmpParas.clear();
			
			String keyquery = rule + "[" + 0 + "]";  
			query = "(" + rb.getString(keyquery) + ")";

			String tmp_q = query;
			String regex = "$";
			int count = 0;
			for(;tmp_q.indexOf(regex) > 0;){
				tmp_q = tmp_q.replaceFirst("\\$", "?");
				count++;
			}
			System.out.println("count : " + count);

			if (count > 0) {
				paraNums.add(count);
			}
			minimumQueries.add(query);

		}else{    		
			
			query = node.jjtGetChild(1).jjtAccept(this, null).toString();

			if(node.jjtGetChild(1).toString().equalsIgnoreCase("Value") || node.jjtGetChild(1).toString().equalsIgnoreCase("PolimolPara") ){
				if(tmpParas.size()>0){
					paraList.add(tmpParas.get(0));
					tmpParas.clear();
				}
			}
		}
		
		System.out.println("rule : " + rule);
		System.out.println("Expquery : " + query);
		queries.add(query);
		rules.add(rule);

		checkNest();

		System.out.println("");
		return query;
	}

	public Object visit(ASTParaList node, Object data) {
		String query = "";
		System.out.println("this is : " + node.toString());
		paraListFlg = true;

		paraListChildNum = node.jjtGetNumChildren();
		System.out.println("paraListChildNum : " + paraListChildNum);

		for (int i = 0; i < paraListChildNum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		String[] tmpParaList = new String[paraListChildNum];
		tmpParas.clear();		
		for (int j = 0; j < paraListChildNum; j++) {

			String tmp = node.jjtGetChild(j).jjtAccept(this, null).toString();
			System.out.println(tmp);

			if (j == 0) {
				query = tmp;
			} else {
				query = query + " union " + tmp;
			}

			System.out.println("tmpPara::"+tmpParas.get(0));
			tmpParaList[j] = tmpParas.get(0).toString();
			tmpParas.clear();
		}

		String tmp = new String();
		for(int k = 0; k < paraListChildNum; k++){
			if(k==0){
				tmp = tmpParaList[k];
			}else{
				tmp = tmp + "+" + tmpParaList[k];
			}
		}
		paraList.add(tmp);

		System.out.println("Paralisquery : " + query);
		System.out.println("");
		return query;
	}

	public Object visit(ASTPolimorPara node, Object data) {
		String query = "";
		System.out.println("this is : " + node.toString());

		polymorChildNum = node.jjtGetNumChildren();
		System.out.println("polymorChildNum : " + polymorChildNum);

		// zonop add. if Type includes integer and String same line.
		if (rb.getString(rule + "Type").indexOf(",") > 0) {
			makePolyTypes();
		}
		polymorFlg = true;

		for (int i = 0; i < polymorChildNum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
			node.jjtGetChild(i).jjtAccept(this, null).toString();
		}

		polymorFlg = false;
		// zonop add. is this rule answer?
		String str = new String();
		try {
			str = rb.getString(rule + "Source");
		} catch (Exception e) {
			str = "false";
		}

		// zonop add. if answer api, get query[0]
		String keyquery = new String();
		if (str.equalsIgnoreCase("api")) {
			keyquery = rule + "[" + 0 + "]";
		} else {
			keyquery = rule + "[" + polymorChildNum + "]";
		}

		query = "(" + rb.getString(keyquery) + ")";
		minimumQueries.add(query);
		System.out.println("Polymorquery" + query);

		query = "(" + rb.getString(keyquery) + ")";
		int count = 0;
		String q = query;
		String regex = "$";
		for(;q.indexOf(regex) > 0;){
			q = q.replaceFirst("\\$", "?");
			count++;
		}
		System.out.println("count : " + count);
		paraNums.add(count);

		String tmp = new String();
		for(int i = 0; i < polymorChildNum; i++){
			if(i==0){
				tmp = tmpParas.get(i).toString();
			}else{
				tmp = tmp + "-"+tmpParas.get(i).toString();
			}
		}
		tmpParas.clear();
		tmpParas.add(tmp);

		System.out.println("");
		return query;
	}

	public Object visit(ASTPluginExp node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}
		
		functionFlg = true;
		
		if(childnum==3) {
			String functionExp = node.jjtGetChild(0).jjtAccept(this, null)
					.toString();
			String functionExp2 = node.jjtGetChild(1).jjtAccept(this, null)
					.toString();
			String functionDomain = node.jjtGetChild(2).jjtAccept(this, null)
					.toString();
					
			recipient = node.jjtGetChild(2).jjtAccept(this, null).toString();
					
			System.out.println("functionExp :" + functionExp);
			System.out.println("recipient :" + recipient);
			
			return "#" + functionExp + "#" + functionExp2 + "@" + functionDomain;
		}else {
			String functionExp = node.jjtGetChild(0).jjtAccept(this, null)
					.toString();
			String functionDomain = node.jjtGetChild(1).jjtAccept(this, null)
					.toString();
					
			recipient = node.jjtGetChild(1).jjtAccept(this, null).toString();
					
			System.out.println("functionExp :" + functionExp);
			System.out.println("recipient :" + recipient);
			
			return "#" + functionExp + "#" + "@" + functionDomain;
		}
	}

	public Object visit(ASTPlugin node, Object data) {

		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		for (int j = 0; j < childnum; j++) {
			String tmp = node.jjtGetChild(j).jjtAccept(this, null).toString();
			System.out.println(tmp);
		}
		function = "";
		command = "";

		function = node.jjtGetChild(0).jjtAccept(this, null).toString();
		command = node.jjtGetChild(1).jjtAccept(this, null).toString();
		
		if(childnum>2) {
			for(int i=2;i<childnum;i++) {
				commandArgs.add(node.jjtGetChild(i).jjtAccept(this, null).toString());
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<commandArgs.size();i++) {sb.append("."+commandArgs.get(i));}
		String tmpStr = new String(sb);
		String tmpDubug = function + "." + command + tmpStr;
		
		if(childnum>2) {
			return tmpDubug;
		}else {
			return function+"."+command;
		}
	}

	public Object visit(ASTAddress node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		String domain = "";

		if(childnum > 1){
			domain = node.jjtGetChild(1).jjtAccept(this, null).toString();
		}
		
		normalFlg = true;
		finalQuery = node.jjtGetChild(0).jjtAccept(this, null).toString();
		System.out.println("domain :" + domain);
		return ">rule{para}<" + "@" + domain;

	}

	public Object visit(ASTDomain node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		subdomain = node.jjtGetChild(0).jjtAccept(this, null).toString();
		System.out.println("subdomain :" + subdomain);

		for (int j = 1; j < childnum; j++) {
			String tmp = node.jjtGetChild(j).jjtAccept(this, null).toString();
			System.out.println(tmp);
			if (j == 1) {
				domain = tmp;
			} else {
				domain = domain + "." + tmp;
			}
		}

		System.out.println("");
		System.out.println("domain :" + domain);
		return subdomain + "." + domain;
	}

	@Override
	public Object visit(ASTrule node, Object data) {
		String name = node.nodeValue;
		
		return String.valueOf(name);
	}

	@Override
	public Object visit(ASTvalue node, Object data) {
		String name = node.nodeValue;
		System.out.println("Value : " + name);
		String keyquery = new String();

		// zonop add. is this rule answer?
		String str = new String();
		try {
			str = rb.getString(rule + "Source");
		} catch (Exception e) {
			str = "false";
		}

		if (name.equals("*")) {
			keyquery = rule + "[" + 0 + "]";
		} else if (str.equalsIgnoreCase("api")) {
			// zonop add. if answer api, get query[0]
			keyquery = rule + "[" + 0 + "]";
			paraNums.add(1);
		} else {
			keyquery = rule + "[" + 1 + "]";
		}

		String query = "";

		try{
			if(!polymorFlg){
				query = "(" + rb.getString(keyquery) + ")";
				int count = 0;
				String q = query;
				String regex = "$";
				for(;q.indexOf(regex) > 0;){
					q = q.replaceFirst("\\$", "?");
					count++;
				}
				System.out.println("count : " + count);
				if(count != 0)
					paraNums.add(count);
				minimumQueries.add(query);
			}
		}catch(Exception e){
			query = "miss";

			return query;
		}

		System.out.println("Valuequery : " + query);

		if (polyTypesNum > 0 && polyTypesPointer < polyTypesNum) {
			paras.add((String) polyTypes.get(polyTypesPointer));
			polyTypesPointer++;
			if (polyTypesPointer == polyTypesNum) {
				polyLastType = (String) polyTypes.get(polyTypesPointer - 1);
			}
		} else if (polyTypesNum > 0 && polyTypesPointer >= polyTypesNum) {
			paras.add(polyLastType);
		} else if (rb.getString(rule + "Type").indexOf(",") > 0) {
			makePolyTypes();
			paras.add((String) polyTypes.get(0));
		} else if (rb.getString(rule + "Type").equalsIgnoreCase("integer")) {
			paras.add("integer");
		} else if (rb.getString(rule + "Type").equalsIgnoreCase("String")) {
			paras.add("String");
		}

		paras.add(name);
		tmpParas.add(name);

		return query;
	}

	public void DefineTransferkey(){

		String Nested_rule = new String();
		try{
			Nested_rule = rb.getString(rule + "Source");

			System.out.println("Nested_rule1 : "+ Nested_rule);
			System.out.println(Nested_rule.indexOf("["));

			if(Nested_rule.indexOf("[") > 0){
				Nested_rule = Nested_rule.substring(0,Nested_rule.indexOf("["));
			}

		}catch (Exception e) {
			Nested_rule = "none";
		}
		System.out.println("Nested_rule2 : "+ Nested_rule);

		//Nested rule : defined Source in transfer rule

		String SourcePara = new String();
		String PassedPara = new String();
		try{
			SourcePara =  rb.getString(rule+"SourcePara");

			SourcePara = SourcePara.replaceAll(" ", "");
			String[] SourcePara_Array = SourcePara.split("\\,");

			for(int i = 0 ; i < SourcePara_Array.length; i++){
				System.out.println(SourcePara_Array[i]);
				String regex = new String("[" + polymorChildNum + ":");
				//				System.out.println(regex);
				int index = SourcePara_Array[i].indexOf(regex) + 3;
				//				System.out.println(index);
				if(index > 2){
					PassedPara = SourcePara_Array[i].substring(index);
					PassedPara = PassedPara.substring(0, PassedPara.indexOf("]"));
					System.out.println(PassedPara);
					break;
				}

			}


		}catch(Exception e){
			SourcePara = "none";
		}

		if (Nested_rule != "none"){
			int minus = 0;
			String tmp_PassedPara = PassedPara;
			String regex = "-";

			for(;tmp_PassedPara.indexOf(regex) > 0;){
				tmp_PassedPara = tmp_PassedPara.replaceFirst("-", "");
				minus++;
			}
		}

	}

	public void checkNest(){
		String Nested_rule = new String();
		try{
			Nested_rule = rb.getString(rule + "Source");

			if(Nested_rule.indexOf("[") > 0){
				Nested_rule = Nested_rule.substring(0, Nested_rule.indexOf("["));
			}

		}catch (Exception e) {
			Nested_rule = "none";
		}
		System.out.println("Nested_rule : "+ Nested_rule);

		//Nested rule : defined Source in transfer rule
		if(!Nested_rule.equals("none")){
			String SourcePara = new String();
			String PassedPara = new String();
			try{
				Nested_rule =  rb.getString(rule+"Source");

				if(Nested_rule.indexOf("[") > 0){
					SourcePara = Nested_rule.substring(Nested_rule.indexOf("["));
					Nested_rule = Nested_rule.substring(0, Nested_rule.indexOf("["));
				}

				SourcePara = SourcePara.replaceAll(" ", "");

				ArrayList<String> SourcePara_Array = new ArrayList<String>(); 

				if(SourcePara.indexOf(",") > 0){
					for(;SourcePara.indexOf(",") > 0;){
						SourcePara_Array.add(SourcePara.substring(0,SourcePara.indexOf(",")));
						SourcePara_Array.add(SourcePara.substring(SourcePara.indexOf(",")+1));
						SourcePara = SourcePara.substring(SourcePara.indexOf(",")+1);
					}
				}else{
					SourcePara_Array.add(SourcePara);
				}

				System.out.println(SourcePara_Array);

				for(int i = 0 ; i < SourcePara_Array.size(); i++){
					System.out.println(SourcePara_Array.get(i));
					System.out.println(paras.size());

					String regex = new String();
					if(polymorChildNum == 0 && paras.size() > 0){
						regex = new String("[" + 1 + ":");
					}else{
						regex = new String("[" + polymorChildNum + ":");
					}
					System.out.println(regex);
					int index = SourcePara_Array.get(i).indexOf(regex) + 3;
					//				System.out.println(index);
					if(index > 2){
						PassedPara = SourcePara_Array.get(i).substring(index);
						PassedPara = PassedPara.substring(0, PassedPara.indexOf("]"));
						System.out.println(PassedPara);
					}

				}


			}catch(Exception e){
				SourcePara = "none";
			}

			String Nested_query = new String();
			String Nested_query_key = new String();

			try{

				int query_key_index = 0;

				if(!PassedPara.equals("")){
					
					String tmp_PassedPara = PassedPara;
					
					if(tmp_PassedPara.indexOf("-") > -1){
						//number of passed parameter is more than 1
						String first_para = new String();
						
						for(; !tmp_PassedPara.equals(""); query_key_index++){
							//tmp_PassedPara : $1-obunai-$2
							//first_para : $1
							if(tmp_PassedPara.indexOf("-") > -1){
								first_para = tmp_PassedPara.substring(0, tmp_PassedPara.indexOf("-"));
								tmp_PassedPara = tmp_PassedPara.substring(tmp_PassedPara.indexOf("-")+1);
							}else{
								//last para
								first_para = tmp_PassedPara;
								tmp_PassedPara = "";
							}
							
							if(first_para.indexOf("$") > -1){
								//if passed parameter contains $
								//paras : [String, obunai, String, matt], $2
								//new paras : [String, obunai, String, matt, String, matt]
								paras.add(paras.get(Integer.parseInt(""+first_para.charAt(first_para.indexOf("$")+1))*2-2));
								paras.add(paras.get(Integer.parseInt(""+first_para.charAt(first_para.indexOf("$")+1))*2-1));
							}else{
								//if passed parameter is constant
								//type is nested rule's type
								//paras : [String, obunai, String, matt], yohei
								//new paras : [String, obunai, String, matt, String, yohei]
								String Nested_para_type	= rb.getString(rule+"Type");
								paras.add(Nested_para_type);
								paras.add(first_para);
							}
							
						}						
						
					}else{
						//number of passed parameter is 1
						//[1:$1] or [2:obunai]		
						query_key_index++;
						if(tmp_PassedPara.indexOf("$") > -1){
							//if passed parameter contains $
							//paras : [String, obunai, String, matt], $2
							//new paras : [String, obunai, String, matt, String, matt]
							paras.add(paras.get(Integer.parseInt(""+tmp_PassedPara.charAt(tmp_PassedPara.indexOf("$")+1))*2-2));
							paras.add(paras.get(Integer.parseInt(""+tmp_PassedPara.charAt(tmp_PassedPara.indexOf("$")+1))*2-1));
						}else{
							//if passed parameter is constant
							//type is nested rule's type
							//paras : [String, obunai, String, matt], yohei
							//new paras : [String, obunai, String, matt, String, yohei]
							String Nested_para_type	= rb.getString(rule+"Type");
							paras.add(Nested_para_type);
							paras.add(tmp_PassedPara);
							
						}
					}

				}

				Nested_query_key = Nested_rule + "[" + query_key_index + "]";
				System.out.println("Nested_query_key : " +Nested_query_key);

				Nested_query =  rb.getString(Nested_query_key);
				int count = 0;
				String q = Nested_query;

				for(;q.indexOf("$") > 0;){
					q = q.replaceFirst("\\$", "?");
					count++;
				}
				System.out.println("count : " + count);
				paraNums.add(-1);
				if(count != 0)
					paraNums.add(count);
				minimumQueries.add(Nested_query);
				queries.add(Nested_query);
				rules.add(Nested_rule);

				for(int i = 0; i < Passed_para_index.size(); i++){
					paras.add(paras.get(Passed_para_index.get(i)*2-2));
					paras.add(paras.get(Passed_para_index.get(i)*2-1));
				}


			}catch(Exception e){
				Nested_query = "miss";

			}			

			System.out.println(Nested_query);

		}else{
			System.out.println("it is not used Nest");
		}

	}


	@Override
	public Object visit(ASTfunction node, Object data) {
		String name = node.nodeValue;

		return String.valueOf(name);
	}
	
	@Override
	public Object visit(ASTcommand node, Object data) {
		String name = node.nodeValue;

		return String.valueOf(name);
	}
	
	@Override
	public Object visit(ASTcommandArg node, Object data) {
		String name = node.nodeValue;

		return String.valueOf(name);
	}

	public void remakepara(){
		System.out.println("this is remakepara1");

		String regex;
		String currentquery;
		int index;
		int k = 0;
		int l = 0;
		ArrayList<String> tmpParas_ = new ArrayList<String>();
		regex = "$";

		System.out.println(paras.size());

		//		System.out.println(queries);

		boolean need_remake = false;
		//if query contains $recipient or $sender need_remake become true
		//or $ is used several times again in same query
		for(int i = 0; i < queries.size(); i++ ){
			System.out.println(queries.get(i));
			if(queries.get(i).indexOf("$s") > 0 || queries.get(i).indexOf("$r") > 0){
				System.out.println("contains $sender or $recipient");
				need_remake = true;
				break;
			}

			String q = queries.get(i);
			for(int j = 0; q.indexOf("$") > 0; j++){
				if(q.indexOf("$" + j) > 0){
					q = q.replaceFirst("\\$", "?");
					if(q.indexOf("$" + j) > 0){
						System.out.println("contains same $ is used in same query");
						need_remake = true;
						break;
					}
				}

			}
		}

		System.out.println(need_remake);

		if(need_remake){

			for (int i = 0; i < minimumQueries.size(); i++ , l = 0){
				currentquery = minimumQueries.get(i).toString();
				for(int j = 1;; j++){
					index = currentquery.indexOf(regex);
					if(index < 0){
						if(currentquery.indexOf("?") < 0 && currentquery.indexOf(".jar") > 0){
							//plugin
							tmpParas_.add(paras.get(k++));
							tmpParas_.add(paras.get(k++));
							break;
						}else{
							//rule[0]
							break;
						}					
					}else if(String.valueOf(currentquery.charAt(index+1)).equals(Integer.toString(j))){
						tmpParas_.add(paras.get(k++));
						tmpParas_.add(paras.get(k++));
						l = l + 2;
						currentquery = currentquery.replaceFirst("\\$", "?");
					}else if(String.valueOf(currentquery.charAt(index+1)).equals("s")){
						tmpParas_.add("String");
						tmpParas_.add("sender");
						currentquery = currentquery.replaceFirst("\\$", "?");
					}else if(String.valueOf(currentquery.charAt(index+1)).equals("r")){
						tmpParas_.add("String");
						tmpParas_.add("recipient");
						currentquery = currentquery.replaceFirst("\\$", "?");
					}else{
						if (String.valueOf(currentquery.charAt(index+1)).matches("[^0-9]")){
							System.out.println("illegal placeholder in query : " + currentquery);
							break;
						}else{
							j = 0;
							k = k - l;
						}
					}
				}
			}
		}
		System.out.println("this is remakepara2");
		for(int i = 0; i < tmpParas_.size(); i++){
			System.out.println("tmpPara : " +tmpParas_.get(i));
		}
		if(!tmpParas_.isEmpty()){
			paras = tmpParas_;	
		}

	}

	public String simplereplace(String q){
		String regex;
		for(int i = 1; ;i++){
			regex = "$"+Integer.toString(i);
			if(q.indexOf(regex) < 0)
				break;
			System.out.println("regex : " + regex);
			q = q.replaceAll("\\$" + Integer.toString(i), "?");
		}
		
		if(q.indexOf("$sender") > 0){
			q = q.replaceAll("\\$sender", "?");
		}

		if(q.indexOf("$recipient") > 0){
			q = q.replaceAll("\\$recipient", "?");
		}
		return q;
	}

	public void checkATmark(){
		for(int i = 0; i < paras.size(); i ++){
			if(paras.get(i).equals("sender")){
				containsATmark = true;
			}else if (paras.get(i).equals("recipient")){
				containsATmark = true;
			}
		}
	}

	@Override
	public Object visit(ASTdomainArg node, Object data) {
		String name = node.nodeValue;

		return String.valueOf(name);
	}

	/**
	 * If ruleTypes include ','. Divide Rules for Poly.
	 * */
	public void makePolyTypes() {
		polyTypes = new ArrayList<String>();
		polyTypesNum = 0;
		polyTypesPointer = 0;

		StringTokenizer or = new StringTokenizer((String) rb.getString(rule
				+ "Type"), ",");
		while (or.hasMoreTokens()) {
			String buffer = or.nextToken();
			polyTypesNum++;
			polyTypes.add(buffer);
		}
		System.out.println("polyTypesNum : " + polyTypesNum);

	}

	public void initPolyTypes() {
		polyTypesNum = 0;
		polyTypesPointer = 0;
	}
	
	@Override
	public ArrayList<String> getParaList(){
		return paraList;
	}
	
	@Override
	public ArrayList<String> getValues() {
		/*
		 * zonop add. make values from paras. paras : String, zonop+obunai,
		 * integer, 4 values : zonop+obunai, 4
		 */
		for (int i = 1; i < paras.size(); i += 2) {
			values.add(paras.get(i));
		}
		return values;
	}

	@Override
	public ArrayList<String> getQueries(){
		ArrayList<String> replaced_queries = new ArrayList<String>();
		for(int i = 0; i < queries.size(); i++){		
			replaced_queries.add(this.simplereplace(queries.get(i).toString()));
		}
		return replaced_queries;
	}
	
	public ArrayList<String> getminimumQueries(){

		return minimumQueries;
	}

	@Override
	public ArrayList<String> getOperators(){
		return operators;
	}

	@Override
	public ArrayList<String> getRules() {
		return rules;
	}
	
	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public String getFunction() {
		return function;
	}
	
	@Override
	public boolean getFunctionFlg() {
		return functionFlg;
	}
	
	@Override
	public boolean getNormalFlg() {
		return normalFlg;
	}
	
	@Override
	public String getCommand(){
		return command;
	}
	
	@Override
	public ArrayList<String> getCommandArgs(){
		return commandArgs;
	}
	
	@Override
	public String getTarget() {
		if(getFunctionFlg()) {
			int start = recipient.indexOf("#", 1);
			return recipient.substring(start+1);
		}else
			return null;
	}
	
	@Override
	public String getSubdomain() {
		return subdomain;
	}

	@Override
	public String getRecipient() {
		return recipient;
	}
	
	@Override
	public ArrayList<String> getParas() {
		return paras;
	}
	
	@Override
	public ArrayList<Integer> getParaNums(){
		return paraNums;
	}

	@Override
	public String getQuery() {
		return finalQuery;
	}

	@Override
	public String getFullDomain(){
		return subdomain + "." + domain; 
	}

	public boolean containsAtmark(){
		return containsATmark;
	}

	public ArrayList<String> getExps(){
		ArrayList<String> Exps = new ArrayList<String>();
		
		for(int i = 0; i < rules.size(); i++){
			Exps.add(rules.get(i) + "{" + paraList.get(i) + "}");
		}
		
		return Exps;
		
	}
	
	
	/** used only in 1st Exp */
	@Override
	public Object visit(ASTN_Recipient node, Object data) {

		return null;
	}

	@Override
	public Object visit(ASTN_PluginExp node, Object data) {

		return null;
	}

	@Override
	public Object visit(ASTN_Address node, Object data) {

		return null;
	}

	@Override
	public Object visit(ASTN_Paras node, Object data) {

		return null;
	}

	@Override
	public Object visit(ASTN_ParaList node, Object data) {

		return null;
	}

	@Override
	public Object visit(ASTN_PolimorPara node, Object data) {

		return null;
	}

}
