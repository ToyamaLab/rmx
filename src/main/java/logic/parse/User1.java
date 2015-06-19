package logic.parse;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;


/** Super OBUNAI Parser */
import logic.parse.SOP.*;

public class User1 implements Parsable {
	
	/** ex) db.ics.keio.ac.jp */
	public  String domain;

	/** ex) testo */
	public  String subdomain;

	/** front of @ ex) name{obunai}+grp{speed+wix+ssql} */
	public  String recipient;

	/** debug function name ex) explain, store */
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
	public  ArrayList<String> keys;

	/** ex) obunai, speed, wix, ssql */
	public  ArrayList<String> para;
	
	/** count parameter for each query */
	private ArrayList<Integer> paranum;
	
	/** ture when polymorPara*/
	private boolean polymorflg;
	
	/** query before union, intersect or except among rules
	 *  i.g. queries.size = the number of rules */
	private ArrayList<String> queries;
	
	/** query before union, intersect or except
	 * 	i.g. minimamqueries.size = the number of queries called from conf file */
	private ArrayList<String> minimamqueries;
	
	/** parameter separated by period*/
	private ArrayList<String> paralist;
	
	/** parameter for paralist*/
	private ArrayList<String> tmppara;
	
	/** operateors order by appeared */
	private ArrayList<String> operator;

	/** ex) (select ~) union (~) intersect (~) */
	public  String finalquery;

	/** ex) testo.properties */
	public  ResourceBundle rb;

	/** ex) name, grp */
	public  String rule;

	/** 1st Exp rule */
	public  ArrayList<String> rule1;

	/** ex) db.ics.keio.ac.jp */
	public  String domname;

	/** true if address contains Atmark rule 
	 * ex)course2valuation{DM}@ */
	private boolean containsATmark;
	

	
	/***/
	public static int polymorchildnum;

	public static int paralischildnum;

	/** ex) gradeType = integer,String */
	public  ArrayList<String> polyTypes;
	public  int polyTypesNum;
	public  int polyTypesPointer;
	public  String polyLastType;

//	public  User1 visitor;
//	public  parser parser;

	public User1() {
		domain = new String();
		subdomain = new String();
		recipient = new String();
		function = new String();
		command = new String();
		commandArgs = new ArrayList<String>();
		values = new ArrayList<String>();
		keys = new ArrayList<String>();
		para = new ArrayList<String>();
        paranum = new ArrayList<Integer>();
        queries = new ArrayList<String>();
        paralist = new ArrayList<String>();
        tmppara = new ArrayList<String>();
        operator = new ArrayList<String>();
		finalquery = new String();
		polymorchildnum = 0;
		paralischildnum = 0;
		rule = new String();
		rule1 = new ArrayList<String>();
		minimamqueries = new ArrayList<String>();
	}
	
	@Override
	public void parseStart(String recipient, ResourceBundle dom, String _domname) {

		System.out.println("==== parser1 start ====");
		
		User1 visitor = new User1();
		visitor.rb = dom;
		visitor.domname = _domname;
		parser parser = new parser(new StringReader(recipient));
		
		try {
			Node start = parser.Recipient1();
			System.out.println(start.jjtAccept(visitor, null));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (TokenMgrError e) {
			e.printStackTrace();
		}
		
        operator = visitor.operator;
        keys = visitor.rule1;
        domain = visitor.domain;
        subdomain = visitor.subdomain;
        this.recipient = recipient;
        minimamqueries = visitor.minimamqueries;
        para = visitor.para;
        this.remakepara();
        paranum = visitor.paranum;
        function = visitor.function;
        command = visitor.command;
        commandArgs = visitor.commandArgs;
        functionFlg = visitor.functionFlg;
        normalFlg = visitor.normalFlg;
        paralist = visitor.paralist;
        queries = visitor.queries;

		System.out.println(subdomain);
		System.out.println(domname);
		finalquery = this.simplereplace(visitor.finalquery);
		System.out.println("final query : " + finalquery);

		for (int i = 0; i < para.size(); i++) {
			System.out.println("para : " + para.get(i).toString());
		}

		initPolyTypes();

		System.out.println("==== parser1 end ====");

	}

	public Object visit(ASTParalis node, Object data) {

		String query = "";
		System.out.println("this is : " + node.toString());

		paralischildnum = node.jjtGetNumChildren();
		System.out.println("paralischildnum : " + paralischildnum);
		for (int i = 0; i < paralischildnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

    	String temp = new String();
    	tmppara.clear();
		for (int j = 0; j < paralischildnum; j++) {
        	tmppara.clear();
			String tmp = node.jjtGetChild(j).jjtAccept(this, null).toString();
			System.out.println(tmp);

			if (j == 0) {
				query = tmp;
			} else {
				query = query + " union " + tmp;
			}
			
    		if(j==0){
    			temp = tmppara.get(0).toString();
    		}else{
    			temp = temp + "+" + tmppara.get(0).toString();
    		}
    		
		}
    	tmppara.clear();
    	tmppara.add(temp);		
		
		System.out.println("Paralisquery : " + query);
		System.out.println("");
		return query;
	}

	public Object visit(ASTPolimolPara node, Object data) {
		String query = "";
		System.out.println("this is : " + node.toString());
		polymorchildnum = node.jjtGetNumChildren();
		System.out.println("polymorchildnum : " + polymorchildnum);

		// zonop add. if Type includes integer and String same line.
		if (rb.getString(rule + "Type").indexOf(",") > 0) {
			makePolyTypes();
		}

		polymorflg = true;
		String tmp = new String();
		tmppara.clear();
		for (int i = 0; i < polymorchildnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
			node.jjtGetChild(i).jjtAccept(this, null).toString();
			if (i==0){
				tmp = tmppara.get(0).toString();
			}else{
				tmp = tmp + "-" + tmppara.get(i).toString();
			}
		}
		tmppara.clear();
		tmppara.add(tmp);
		polymorflg = false;
		
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
			paranum.add(1);
		} else {
			keyquery = rule + "[" + polymorchildnum + "]";
		}

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
			paranum.add(count);

		minimamqueries.add(query);
		
		System.out.println("Polymorquery : " + query);

		System.out.println("");
		return query;
	}

	public Object visit(ASTArg node, Object data) {
		String name = node.nodeValue;

		return String.valueOf(name);
	}

	public Object visit(ASTDebugEx node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		String functionExp = node.jjtGetChild(0).jjtAccept(this, null)
				.toString();
		String address = node.jjtGetChild(1).jjtAccept(this, null).toString();

		System.out.println("functionExp :" + functionExp);
		System.out.println("recipient :" + address);

		return "#" + functionExp + "#" + address;
	}

	public Object visit(ASTDebug node, Object data) {

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
		
		System.out.println("function:"+function);
		System.out.println("command:"+command);
		System.out.println("commandArgs:"+commandArgs);
		if(childnum>2) {
			return tmpDubug;
		}else {
			return function+"."+command;
		}
	}

	@Override
	public Object visit(ASTValue node, Object data) {
		String name = node.nodeValue;
		System.out.println("Value : " + name);
		System.out.println("Rule : " + rule);
		String keyquery;

		// zonop add. is this rule answer?
		String str = new String();
		try {
			str = rb.getString(rule + "Source");
		} catch (Exception e) {
			str = "false";
		}

		if (name.equals("all") | name.equals("*")) {
			keyquery = rule + "[" + 0 + "]";
		} else if (str.equalsIgnoreCase("api")) {
			// zonop add. if answer api, get query[0]
			keyquery = rule + "[" + 0 + "]";
			paranum.add(1);
		} else {
			keyquery = rule + "[" + 1 + "]";
		}
		System.out.println("@@"+keyquery);
		String query = "(" + rb.getString(keyquery) + ")";
		
		int count = 0;
		String q = query;
		String regex = "$";
		for(;q.indexOf(regex) > 0;){
			q = q.replaceFirst("\\$", "?");
			count++;
		}
		
		System.out.println("count : " + count);
		if(count != 0)
			paranum.add(count);

		if(!polymorflg){
			minimamqueries.add(query);
		}
		
		System.out.println("Valuequery : " + query);

		if (polyTypesNum > 0 && polyTypesPointer < polyTypesNum) {
			para.add((String) polyTypes.get(polyTypesPointer));
			polyTypesPointer++;
			if (polyTypesPointer == polyTypesNum) {
				polyLastType = (String) polyTypes.get(polyTypesPointer - 1);
			}
		} else if (polyTypesNum > 0 && polyTypesPointer >= polyTypesNum) {
			para.add(polyLastType);
		} else if (rb.getString(rule + "Type").indexOf(",") > 0) {
			makePolyTypes();
			para.add((String) polyTypes.get(0));
		} else if (rb.getString(rule + "Type").equalsIgnoreCase("integer")) {
			para.add("integer");
		} else if (rb.getString(rule + "Type").equalsIgnoreCase("String")) {
			para.add("String");
		}
		para.add(name);
		tmppara.add(name);
		
		return query;
	}

	@Override
	public Object visit(ASTfunction node, Object data) {
		String name = node.nodeValue;
		
		return String.valueOf(name);
	}

	@Override
	public Object visit(ASTalias node, Object data) {
		String name = node.nodeValue;

		return String.valueOf(name);
	}

	@Override
	public Object visit(ASTDomainArg node, Object data) {
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
		ArrayList<String> tmppara = new ArrayList<String>();
		regex = "$";

		for(int i = 0; i < para.size(); i++){
			System.out.println("para : " + para.get(i));
		}

		for (int i = 0; i < minimamqueries.size(); i++ , l = 0){
			currentquery = minimamqueries.get(i).toString();
			for(int j = 1;; j++){
				System.out.println(currentquery);
				index = currentquery.indexOf(regex);
				System.out.println("index : " + index);
				System.out.println("$ no ato ha : " + currentquery.charAt(index + 1));
				if(index < 0){
					if(currentquery.indexOf("?") < 0){
						//plugin 
						tmppara.add(para.get(k++));
						tmppara.add(para.get(k++));
						break;
					}else{
						break;
					}	
				}else if(String.valueOf(currentquery.charAt(index+1)).equals(Integer.toString(j))){
					tmppara.add(para.get(k++));
					tmppara.add(para.get(k++));
					l = l + 2;
					currentquery = currentquery.replaceFirst("\\$", "?");
				}else if(String.valueOf(currentquery.charAt(index+1)).equals("s")){
					tmppara.add("String");
					tmppara.add("sender");
					currentquery = currentquery.replaceFirst("\\$", "?");
				}else if(String.valueOf(currentquery.charAt(index+1)).equals("r")){
					tmppara.add("String");
					tmppara.add("recipient");
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

		System.out.println("this is remakepara2");
		for(int i = 0; i < tmppara.size(); i++){
			System.out.println("tmppara : " +tmppara.get(i));
		}

		if(!tmppara.isEmpty()){
			para = tmppara;	
		}
	}

	public String simplereplace(String q){
		String regex;
		for(int i = 1; ;i++){
			regex = "$"+Integer.toString(i);
			System.out.println("regex : " + regex);
			if(q.indexOf(regex) < 0)
				break;
			q = q.replaceAll("\\$" + Integer.toString(i), "?");
		}
		
		if(q.indexOf("$@") > 0){
			q = q.replaceAll("\\$@", "?");
			containsATmark = true;
		}
		return q;
	}

	/** for 1st Exp */

	@Override
	public Object visit(ASTAddress1 node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}
		
		normalFlg = true;
		String domain1 = "";

		if(childnum > 1){
			domain1 = node.jjtGetChild(1).jjtAccept(this, null).toString();
			finalquery = node.jjtGetChild(0).jjtAccept(this, null).toString();
		}else{
			domain1 = node.jjtGetChild(0).jjtAccept(this, null).toString();
		}
			System.out.println("domain1 :" + domain1);
		return "> para <" + "@" + domain1;
	}

	@Override
	public Object visit(ASTdomain1 node, Object data) {
		System.out.println("this is : " + node.toString());

		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}
		String tmpdom = "";
		int j;
		System.out.println("domname : " + domname);
		for (j = childnum - 1; j > -1; j--) {
			String tmp = node.jjtGetChild(j).jjtAccept(this, null).toString();
			System.out.println(tmp);
			if (j == childnum - 1) {
				tmpdom = tmp;
			} else {
				tmpdom = tmp + "." + tmpdom;
			}
			if (tmpdom.equals(domname)) {
				break;
			}
			System.out.println("tmpdom : " + tmpdom);
		}

		System.out.println("tmpdom : " + tmpdom);

		if (j == 1) {
			// master rule
			subdomain = node.jjtGetChild(0).jjtAccept(this, null).toString();
			rule1.add(subdomain);
		} else {
			for (int k = 0; k < j; k++) {
				if (k < j - 1) {
					rule1.add(node.jjtGetChild(k).jjtAccept(this, null)
							.toString());
				} else {
					subdomain = (node.jjtGetChild(k).jjtAccept(this, null)
							.toString());
				}
			}
		}

		System.out.println(subdomain);
		return subdomain + "." + domain;

	}

	@Override
	public Object visit(ASTParas1 node, Object data) {

		System.out.println("this is : " + node.toString());
		String query = "";
		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}

		for (int i = 0; i < childnum; i++) {
			//System.out.println(rule1);
			rule = rule1.get(i).toString();
			String tmp = node.jjtGetChild(i).jjtAccept(this, null).toString();
			queries.add(tmp);
			paranum.add(-1);
			operator.add(".");
			if (i == 0) {
				query = tmp;
			} else {
				query = "( " + query + ")" + " intersect " + "(" + tmp + ")";
			}
			
			paralist.add(tmppara.get(0));
			tmppara.clear();

		}

		return query;
	}

	@Override
	public Object visit(ASTRecipient1 node, Object data) {
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

	@Override
	public Object visit(ASTDebugEx1 node, Object data) {
		System.out.println("this is : " + node.toString());
		
		int childnum = node.jjtGetNumChildren();
		System.out.println("childnum : " + childnum);
		for (int i = 0; i < childnum; i++) {
			System.out.println("child [ " + i + " ] type : "
					+ node.jjtGetChild(i).toString());
		}
		
		functionFlg = true;
		
		if(childnum==3) {
			String functionDomain = node.jjtGetChild(2).jjtAccept(this, null)
					.toString();
			String functionExp = node.jjtGetChild(0).jjtAccept(this, null)
					.toString();
			String functionExp2 = node.jjtGetChild(1).jjtAccept(this, null)
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
	public String getFunction() {
		return function;
	}
	
	@Override
	public String getCommand() {
		return command;
	}
	
	@Override
	public ArrayList<String> getCommandArgs(){
		return commandArgs;
	}
	
	@Override
	public boolean getFunctionFlg() {
		return functionFlg;
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
	public boolean getNormalFlg() {
		return normalFlg;
	}
	@Override
	public ArrayList<String> getValues() {
		/*
		 * zonop add. make values from para. para : String, zonop+obunai,
		 * integer, 4 values : zonop+obunai, 4
		 */
		for (int i = 1; i < para.size(); i += 2) {
			values.add(para.get(i));
		}
		return values;
	}
	
	@Override
	public ArrayList<String> getPara(){
		return para;
	}
	
	@Override
	public ArrayList<Integer> getParanum(){
		return paranum;
	}
	
	@Override
	public String getQuery() {
		return finalquery;
	}
	
	@Override
	public ArrayList<String> getqueries(){
		ArrayList<String> replaced_queries = new ArrayList<String>();
		for(int i = 0; i < queries.size(); i++){	
			replaced_queries.add(this.simplereplace(queries.get(i).toString()));
		}
		return replaced_queries;
	}

	@Override
	public ArrayList<String> getoperator(){
		return operator;
	}
	
	@Override
	public ArrayList<String> getparalist(){
		return paralist;
	}

	@Override
	public ArrayList<String> getKeys() {
//		keys = rule1;
		return keys;
	}

	@Override
	public String getDomain() {
		return domain;
	}
	
	@Override
	public String getSubdomain() {
		return subdomain;
	}

	@Override
	public String getfulldomain(){
		return subdomain + "." + domname;
	}
	
	@Override
	public String getRecipient() {
		return recipient;
	}
	
	public boolean containsAtmark(){
		return containsATmark;
	}

	/** used only in 2ndExp */

	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	public Object visit(ASTRecipient node, Object data) {

		return null;
	}

	public Object visit(ASTException node, Object data) {

		return null;
	}

	public Object visit(ASTUnion node, Object data) {

		return null;
	}

	public Object visit(ASTIntersection node, Object data) {

		return null;
	}

	public Object visit(ASTExp node, Object data) {

		return null;
	}

	public Object visit(ASTAddress node, Object data) {
		return null;
	}

	public Object visit(ASTdomain node, Object data) {
		String name = node.nodeValue;

		return String.valueOf(name);
	}

	@Override
	public Object visit(ASTRule node, Object data) {
		String name = node.nodeValue;

		return String.valueOf(name);
	}

	@Override
	public Object visit(ASTSubdomain node, Object data) {
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

}
