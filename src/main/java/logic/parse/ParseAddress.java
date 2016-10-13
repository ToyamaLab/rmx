package logic.parse;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.ResourceBundle;

import logic.parse.SOP.*;

public class ParseAddress implements Parsable {
	
	/** e.g.) krmx_test.properties */
	public ResourceBundle domBundle;
	
	/** given in advance e.g.) krmx.jp */
	public String domName;
	
	/** e.g.) name{andy}@test.krmx.jp */
	public String recipient;
	
	/** e.g.) krmx.jp */
	public String domain;
	
	/** e.g.) test */
	public String subDomain;
	
	/** e.g.) name, grade */
	public ArrayList<String> rules;
	
	/** e.g.) String, andy, integer, 1 */
	public ArrayList<String> paras;
	
	/** count parameter for each query */
	public ArrayList<Integer> paraNums;
	
	/** rule{paraList} */
	public ArrayList<String> paraList;
	
	/** "+", ".", "-" */
	public ArrayList<String> operators;
	
	/** query before union, intersect or except between rules
	 *  i.e.) queries.size = the number of rules */
	public ArrayList<String> queries;
	
	/** query before union, intersect or except
	 * 	i.e.) minimumQueries.size = the number of queries called from prop file */
	public ArrayList<String> minimumQueries;
	
	/** query for getting final recipients */
	public String finalQuery;
	
	/** rule, paras, query, etc. corresponding to generate rule */
	public ArrayList<String> generateRules;
	public ArrayList<String> generateParas;
	public ArrayList<Integer> generateParaNums;
	public ArrayList<String> generateParaList;
	public ArrayList<String> generateQueries;
	
	private ArrayList<String> ruleTypeList;
	
	public boolean deliveryFlg;
	public boolean generateFlg;
	public boolean functionFlg;
	
	/** plugin's function name e.g.) event */
	public String function;
	
	/** function's command name e.g.) attend, absence */
	public String command;
	
	/** function's command args */
	public ArrayList<String> commandArgs;
	
	/** temporal variable */
	private String rule;
	private ArrayList<String> tmpParas;
	private boolean isGenerate;
	
	/** temporal variable for polymorphic */
	private boolean polymorFlg;
	private int polymorChildNum;
	
	public ParseAddress() {
		recipient = new String();
		domain = new String();
		subDomain = new String();
		rules = new ArrayList<String>();
		paras = new ArrayList<String>();
		paraNums = new ArrayList<Integer>();
		paraList = new ArrayList<String>();
		operators = new ArrayList<String>();
		queries = new ArrayList<String>();
		minimumQueries = new ArrayList<String>();
		finalQuery = new String();
		generateRules = new ArrayList<String>();
		generateParas = new ArrayList<String>();
		generateParaNums = new ArrayList<Integer>();
		generateParaList = new ArrayList<String>();
		generateQueries = new ArrayList<String>();
		ruleTypeList = new ArrayList<String>();
		deliveryFlg = false;
		generateFlg = false;
		functionFlg = false;
		function = new String();
		command = new String();
		commandArgs = new ArrayList<String>();
		
		rule = new String();
		tmpParas = new ArrayList<String>();
		isGenerate = false;
		
		polymorFlg = false;
		polymorChildNum = 0;
	}

	public void parseStart(String _recipient, ResourceBundle _domBundle, String _domName) {
		
		System.out.println("==== parser start ====");
		
		recipient = _recipient;
		ParseAddress visitor = new ParseAddress();
		visitor.domBundle = _domBundle;
		visitor.domName = _domName;
		try {
			Node start = selectStartNode(recipient);
			System.out.println(start.jjtAccept(visitor, null));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (TokenMgrError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (visitor.generateFlg) {
			generateRules = visitor.generateRules;
			generateParas = visitor.generateParas;
			generateParaNums = visitor.generateParaNums;
			generateParaList = visitor.generateParaList;
			generateQueries = visitor.generateQueries;
			if (visitor.deliveryFlg && !visitor.functionFlg) {
				String newRecipient = reformRecipient(recipient, visitor);
				visitor = new ParseAddress();
				visitor.domBundle = _domBundle;
				visitor.domName = _domName;
				visitor.generateFlg = true;
				try {
					Node start = selectStartNode(newRecipient);
					System.out.println(start.jjtAccept(visitor, null));
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (TokenMgrError e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		visitor.finalQuery = simpleReplace(visitor.finalQuery);
		finalQuery = visitor.finalQuery;
		domain = visitor.domain;
		subDomain = visitor.subDomain;
		queries = visitor.queries;
		minimumQueries = visitor.minimumQueries;
		rules = visitor.rules;
		operators = visitor.operators;
		paraList = visitor.paraList;
		paras = visitor.paras;
		paraNums = visitor.paraNums;
		deliveryFlg = visitor.deliveryFlg;
		generateFlg = visitor.generateFlg;
		functionFlg = visitor.functionFlg;
		function = visitor.function;
		command = visitor.command;
		commandArgs = visitor.commandArgs;
		
		if (deliveryFlg) {
			paras = remakeParas(paras, queries, minimumQueries);
			for (int i = 0; i < paras.size(); i++)
				System.out.println("para : " + paras.get(i));
		}
		if (generateFlg) {
			generateParas = remakeParas(generateParas, generateQueries, generateQueries);
			for (int i = 0; i < generateParas.size(); i++)
				System.out.println("generatePara : " + generateParas.get(i));
		}
		
		System.out.println("subDomain : " + subDomain);
		System.out.println("domain : " + domain);
		
		System.out.println("final query : " + finalQuery);
		System.out.println("==== parser end ====");
		
	}

	@Override
	public Object visit(ASTRecipient node, Object data) {
		System.out.println("THIS IS : " + node.toString());
		
		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		String recipient_ = node.jjtGetChild(0).jjtAccept(this, null).toString();
		paraNums.add(-1);
		
		System.out.println("END OF : " + node.toString());
		return recipient_;
	}

	@Override
	public Object visit(ASTPluginExp node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		functionFlg = true;
		
		String pluginExp = new String();
		if (childNum == 3) {
			String functionExp = node.jjtGetChild(0).jjtAccept(this, null).toString();
			String functionTarget = node.jjtGetChild(1).jjtAccept(this, null).toString();
			String functionDomain = node.jjtGetChild(2).jjtAccept(this, null).toString();
					
			System.out.println("functionExp : " + functionExp);
			System.out.println("functionTarget : " + functionTarget);
			System.out.println("recipient : >rule{para}<@" + functionDomain);
			
			pluginExp = "#" + functionExp + "#>rule{para}<@" + functionDomain;
			finalQuery = functionTarget;
			
		} else {
			String functionExp = node.jjtGetChild(0).jjtAccept(this, null).toString();
			String functionDomain = node.jjtGetChild(1).jjtAccept(this, null).toString();
			
			System.out.println("functionExp : " + functionExp);
			System.out.println("recipient : " + functionDomain);
			
			pluginExp =  "#" + functionExp + "#@" + functionDomain;
		}
		
		System.out.println("END OF : " + node.toString());
		return pluginExp;
	}

	@Override
	public Object visit(ASTPlugin node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		function = node.jjtGetChild(0).jjtAccept(this, null).toString();
		command = node.jjtGetChild(1).jjtAccept(this, null).toString();
		System.out.println("function : " + function);
		System.out.println("command : " + command);
		
		String plugin = new String();
		if (childNum >= 3) {
			for (int j = 2; j < childNum; j++) {
				String tmpCommand = node.jjtGetChild(j).jjtAccept(this, null).toString();
				commandArgs.add(tmpCommand);
				System.out.println("command" + (j - 1) + " : " + tmpCommand);
			}
			
			StringBuilder sb = new StringBuilder();
			for (int k = 0; k < commandArgs.size(); k++) {
				sb.append("." + commandArgs.get(k));
			}
			String tmpCommandArgs = new String(sb);
			plugin = function + "." + command + tmpCommandArgs;
		} else {
			plugin = function + "." + command;
		}
		
		System.out.println("END OF : " + node.toString());
		return plugin;
	}
	
	@Override
	public Object visit(ASTCommandTarget node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < childNum; j++) {
			String tmpTarget = node.jjtGetChild(j).jjtAccept(this, null).toString();
			if (j == 0)
				sb.append(tmpTarget);
			else
				sb.append("+" + tmpTarget);
		}
		String commandTarget = new String(sb);
		
		System.out.println("CommandTarget : " + commandTarget);
		System.out.println("END OF : " + node.toString());
		return commandTarget;
	}

	@Override
	public Object visit(ASTCommandExp node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		String commandExp = node.jjtGetChild(0).jjtAccept(this, null).toString();
		if (childNum == 1) {
			commandExp = commandExp.concat("{}");
		} else {
			StringBuilder sb = new StringBuilder();
			for (int j = 1; j < childNum; j++) {
				String tmp = node.jjtGetChild(j).jjtAccept(this, null).toString();
				if (j == 1)
					sb.append(tmp);
				else
					sb.append("-" + tmp);
			}
			String tmpPara = new String(sb);
			
			commandExp = commandExp + "{" + tmpPara + "}";
		}
		
		System.out.println("CommandExp : " + commandExp);
		System.out.println("END OF : " + node.toString());
		return commandExp;
	}

	@Override
	public Object visit(ASTCommandPara node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < childNum; j++) {
			String tmp = node.jjtGetChild(j).jjtAccept(this, null).toString();
			if (j == 0)
				sb.append(tmp);
			else
				sb.append("-" + tmp);
		}
		String commandPara = new String(sb);
		
		System.out.println("CommandPara : " + commandPara);
		System.out.println("END OF : " + node.toString());
		return commandPara;
	}

	@Override
	public Object visit(ASTAddress node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}

		finalQuery = node.jjtGetChild(0).jjtAccept(this, null).toString();
		String tmpDomain = node.jjtGetChild(1).jjtAccept(this, null).toString();

		System.out.println("domain : " + tmpDomain);
		System.out.println("END OF : " + node.toString());
		
		return ">rule{para}<" + "@" + tmpDomain;
	}

	@Override
	public Object visit(ASTDomain node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}

		StringBuilder sb = new StringBuilder();
		for (int j = 1; j < childNum; j++) {
			String tmpDomArg = node.jjtGetChild(j).jjtAccept(this, null).toString();
			if (j == 1)
				sb.append(tmpDomArg);
			else
				sb.append("." + tmpDomArg);
		}
		domain = new String(sb);
		
		if (domain.equals(domName))
			subDomain = node.jjtGetChild(0).jjtAccept(this, null).toString();
		else
			domain = node.jjtGetChild(0).jjtAccept(this, null).toString() + "." + domain;

		System.out.println("domain : " + domain);
		System.out.println("subDomain :" + subDomain);
		System.out.println("END OF : " + node.toString());
		
		return subDomain + "." + domain;
	}

	@Override
	public Object visit(ASTException node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}

		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		paraNums.add(-1);
		operators.add("-");
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		String query = " ( " + left + " ) " + " except " + " ( " + right + " ) ";
		
		System.out.println("query : " + query);
		System.out.println("END OF : " + node.toString());

		return query;
	}

	@Override
	public Object visit(ASTUnion node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}

		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		paraNums.add(-1);
		operators.add("+");
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		String query = " ( " + left + " ) " + " union " + " ( " + right + " ) ";
		
		System.out.println("query : " + query);
		System.out.println("END OF : " + node.toString());

		return query;
	}

	@Override
	public Object visit(ASTIntersection node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}

		String left = node.jjtGetChild(0).jjtAccept(this, null).toString();
		paraNums.add(-1);
		operators.add(".");
		String right = node.jjtGetChild(1).jjtAccept(this, null).toString();
		String query = " ( " + left + " ) " + " intersect " + " ( " + right + " ) ";
		
		System.out.println("query : " + query);
		System.out.println("END OF : " + node.toString());

		return query;
	}

	@Override
	public Object visit(ASTExp node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}

		rule = node.jjtGetChild(0).jjtAccept(this, null).toString();
		try {
			if (domBundle.getString(rule).equalsIgnoreCase("generate")) {
				ruleTypeList.add("generate");
				generateFlg = true;
				isGenerate = true;
			} else {
				ruleTypeList.add("delivery");
				deliveryFlg = true;
			}
		} catch (Exception e) {
			ruleTypeList.add("delivery");
			deliveryFlg = true;
		}
		
		String query = new String();
		
		if (childNum == 1) {
			// user defined no parameters
			// i.e. rule{}@test.krmx.jp
			tmpParas.clear();
			
			String queryKey = rule + "[" + 0 + "]";  
			query = "(" + domBundle.getString(queryKey) + ")";
			
			String regex = "$";
			String tmpQuery = query;
			int count = 0;
			for (int j = tmpQuery.indexOf(regex); j > 0; j = tmpQuery.indexOf(regex, j + 1)) {
				count++;
			}
			System.out.println("count : " + count);
			
			if (!isGenerate) {
				paras.add("*");
				paraList.add("*");
				paraNums.add(count);
				minimumQueries.add(query);
			} else {
				generateParas.add("*");
				generateParaList.add("*");
				generateParaNums.add(count);
			}

		} else {    		
			query = node.jjtGetChild(1).jjtAccept(this, null).toString();

			if ((node.jjtGetChild(1).toString().equalsIgnoreCase("value") || node.jjtGetChild(1).toString().equalsIgnoreCase("PolymorPara")) && tmpParas.size() > 0){
				if (!isGenerate)
					paraList.add(tmpParas.get(0));
				else
					generateParaList.add(tmpParas.get(0));
				
				tmpParas.clear();
			}
		}
		
		System.out.println("rule : " + rule);
		System.out.println("ExpQuery : " + query);
		
		if (!isGenerate) {
			rules.add(rule);
			queries.add(query);
			
			checkNest();
		} else {
			generateRules.add(rule);
			generateQueries.add(query);
			generateParaNums.add(-1);
			isGenerate = false;
		}

		System.out.println("END OF : " + node.toString());
		return query;
	}

	@Override
	public Object visit(ASTParaList node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);

		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}

		String query = new String();
		String[] tmpPara = new String[childNum];
		for (int j = 0, placeHolderNum = 0; j < childNum; j++) {
			String tmpQuery = node.jjtGetChild(j).jjtAccept(this, null).toString();

			if (j == 0) {
				query = tmpQuery;
				while (tmpQuery.contains("$" + (placeHolderNum + 1)))
					placeHolderNum++;
			} else {
				int endNum = 1;
				while (tmpQuery.contains("$" + endNum)) {
					placeHolderNum++;
					endNum++;
				}
				for (int k = endNum - 1; k >= 1; k--) {
					tmpQuery = tmpQuery.replace("$" + k, "$" + (placeHolderNum - (endNum - 1 - k)));
				}
				query = query + " union " + tmpQuery;
			}

			System.out.println("tmpPara::"+tmpParas.get(0));
			tmpPara[j] = tmpParas.get(0);
			tmpParas.clear();
		}

		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < childNum; k++) {
			if (k == 0)
				sb.append(tmpPara[k]);
			else
				sb.append("+" + tmpPara[k]);
		}
		String tmpParaList = new String(sb);
		if (!isGenerate)
			paraList.add(tmpParaList);
		else
			generateParaList.add(tmpParaList);

		System.out.println("ParaListQuery : " + query);
		System.out.println("END OF : " + node.toString());
		
		return query;
	}

	@Override
	public Object visit(ASTPolymorPara node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		polymorChildNum = node.jjtGetNumChildren();
		System.out.println("polymorChildNum : " + polymorChildNum);

		// zonop add. if Type includes integer and String same line.
		polymorFlg = true;

		for (int i = 0; i < polymorChildNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
			node.jjtGetChild(i).jjtAccept(this, null);
		}
		polymorFlg = false;
		
		String queryKey = rule + "[" + polymorChildNum + "]";
		String query = "(" + domBundle.getString(queryKey) + ")";
		if (!isGenerate)
			minimumQueries.add(query);
		System.out.println("PolymorQuery : " + query);

		String regex = "$";
		String tmpQuery = query;
		int count = 0;
		for (int j = tmpQuery.indexOf(regex); j > 0; j = tmpQuery.indexOf(regex, j + 1)) {
			count++;
		}
		if (!isGenerate)
			paraNums.add(count);
		else
			generateParaNums.add(count);
		System.out.println("count : " + count);

		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < polymorChildNum; k++) {
			if (k == 0)
				sb.append(tmpParas.get(k));
			else
				sb.append("-" + tmpParas.get(k));
		}
		String tmpPolymorPara = new String(sb);
		tmpParas.clear();
		tmpParas.add(tmpPolymorPara);
		
		polymorChildNum = 0;

		System.out.println("END OF : " + node.toString());
		return query;
	}

	@Override
	public Object visit(ASTrule node, Object data) {
		String arg = node.nodeValue;
		
		return String.valueOf(arg);
	}

	@Override
	public Object visit(ASTvalue node, Object data) {
		String arg = node.nodeValue;
		System.out.println("value : " + arg);

		String queryKey = new String();
		if (arg.equals("*"))
			queryKey = rule + "[" + 0 + "]";
		else
			queryKey = rule + "[" + 1 + "]";

		String query = new String();
		try {
			if (!polymorFlg) {
				query = "(" + domBundle.getString(queryKey) + ")";
				String regex = "$";
				String tmpQuery = query;
				int count = 0;
				for (int i = tmpQuery.indexOf(regex); i > 0; i = tmpQuery.indexOf(regex, i + 1)) {
					count++;
				}
				if (!isGenerate) {
					paraNums.add(count);
					minimumQueries.add(query);
				} else {
					generateParaNums.add(count);
				}
				System.out.println("count : " + count);
				System.out.println("ValueQuery : " + query);
			}
		} catch(Exception e) {
			query = "miss";
			return query;
		}
		if (!isGenerate)
			paras.add(arg);
		else
			generateParas.add(arg);
		
		tmpParas.add(arg);
		
		System.out.println("END OF : " + node.toString());
		return query;
	}

	@Override
	public Object visit(ASTfunction node, Object data) {
		String arg = node.nodeValue;

		return String.valueOf(arg);
	}

	@Override
	public Object visit(ASTcommand node, Object data) {
		String arg = node.nodeValue;

		return String.valueOf(arg);
	}

	@Override
	public Object visit(ASTcommandArg node, Object data) {
		String arg = node.nodeValue;

		return String.valueOf(arg);
	}

	@Override
	public Object visit(ASTdomainArg node, Object data) {
		String arg = node.nodeValue;

		return String.valueOf(arg);
	}

	@Override
	public Object visit(ASTN_Recipient node, Object data) {
		System.out.println("THIS IS : " + node.toString());
		
		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		System.out.println("END OF : " + node.toString());
		return node.jjtGetChild(0).jjtAccept(this, null);
	}

	@Override
	public Object visit(ASTN_PluginExp node, Object data) {
		System.out.println("THIS IS : " + node.toString());
		
		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		functionFlg = true;
		
		String pluginExp = new String();
		if (childNum == 3) {
			String functionDomain = node.jjtGetChild(2).jjtAccept(this, null).toString();
			String functionTarget = node.jjtGetChild(1).jjtAccept(this, null).toString();
			String functionExp = node.jjtGetChild(0).jjtAccept(this, null).toString();
			
			System.out.println("functionExp : " + functionExp);
			System.out.println("functionTarget : " + functionTarget);
			System.out.println("recipient : >rule<@" + functionDomain);
			
			pluginExp =  "#" + functionExp + "#>rule<@" + functionDomain;
			finalQuery = functionTarget;

		} else {
			String functionExp = node.jjtGetChild(0).jjtAccept(this, null).toString();
			String functionDomain = node.jjtGetChild(1).jjtAccept(this, null).toString();
					
			System.out.println("functionExp : " + functionExp);
			System.out.println("recipient : " + functionDomain);
			
			pluginExp = "#" + functionExp + "#@" + functionDomain;
		}
		
		System.out.println("END OF : " + node.toString());
		return pluginExp;
	}

	@Override
	public Object visit(ASTN_Address node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		String tmpDomain = node.jjtGetChild(1).jjtAccept(this, null).toString();
		finalQuery = node.jjtGetChild(0).jjtAccept(this, null).toString();
		
		System.out.println("domain : " + tmpDomain);
		System.out.println("END OF : " + node.toString());
		
		return "> para <" + "@" + tmpDomain;
	}
	
	@Override
	public Object visit(ASTN_Domain node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
		
		System.out.println("domName : " + domName);
		String tmpDomName = new String();
		int domArgIndex;
		for (domArgIndex = childNum - 1; domArgIndex >= 0; domArgIndex--) {
			String domArg = node.jjtGetChild(domArgIndex).jjtAccept(this, null).toString();
			if (domArgIndex == childNum - 1) {
				tmpDomName = domArg;
			} else {
				tmpDomName = domArg + "." + tmpDomName;
			}
			if (tmpDomName.equals(domName))
				break;
			System.out.println("tmpDomName : " + tmpDomName);
		}
		domain = tmpDomName;

		for (int j = 0; j <= domArgIndex - 1; j++) {
			if (j == domArgIndex - 1 && j != 0)
				subDomain = node.jjtGetChild(j).jjtAccept(this, null).toString();
			else
				rules.add(node.jjtGetChild(j).jjtAccept(this, null).toString());
		}
		
		System.out.println("domain : " + domain);
		System.out.println("subDomain : " + subDomain);
		System.out.println("END OF : " + node.toString());
		return subDomain + "." + domain;
	}

	@Override
	public Object visit(ASTN_Paras node, Object data) {
		System.out.println("THIS IS : " + node.toString());
		
		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}
				
		String query = new String();
		for (int j = 0; j < childNum; j++) {
			rule = rules.get(j);
			System.out.println("rule : " + rule);
			try {
				if (domBundle.getString(rule).equalsIgnoreCase("generate")) {
					ruleTypeList.add("generate");
					generateFlg = true;
					isGenerate = true;
				} else {
					ruleTypeList.add("delivery");
					deliveryFlg = true;
					isGenerate = false;
				}
			} catch(Exception e) {
				ruleTypeList.add("delivery");
				deliveryFlg = true;
				isGenerate = false;
			}
			
			String tmpQuery = node.jjtGetChild(j).jjtAccept(this, null).toString();
			if (!isGenerate) {
				queries.add(tmpQuery);
				paraNums.add(-1);
				if (j != childNum -1)
					operators.add(".");
				
				if (j == 0)
					query = tmpQuery;
				else
					query = "( " + query + " )" + " intersect " + "( " + tmpQuery + " )";
				paraList.add(tmpParas.get(0));
				
			} else {
				generateQueries.add(tmpQuery);
				generateParaNums.add(-1);
				generateParaList.add(tmpParas.get(0));
			}
			
			tmpParas.clear();
		}
		
		System.out.println("END OF : " + node.toString());
		return query;
	}
	
	@Override
	public Object visit(ASTN_ParaList node, Object data) {
		System.out.println("THIS IS : " + node.toString());

		int childNum = node.jjtGetNumChildren();
		System.out.println("childNum : " + childNum);
		for (int i = 0; i < childNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
		}

		String query = new String();
    	String tmpPara = new String();
    	tmpParas.clear();
		for (int j = 0, placeHolderNum = 0; j < childNum; j++) {
        	tmpParas.clear();
			String tmpQuery = node.jjtGetChild(j).jjtAccept(this, null).toString();
			
			if (j == 0) {
				query = tmpQuery;
				tmpPara = tmpParas.get(0);
				while (tmpQuery.contains("$" + (placeHolderNum + 1)))
					placeHolderNum++;
			} else {
				int endNum = 1;
				while (tmpQuery.contains("$" + endNum)) {
					placeHolderNum++;
					endNum++;
				}
				for (int k = endNum - 1; k >= 1; k--) {
					tmpQuery = tmpQuery.replace("$" + k, "$" + (placeHolderNum - (endNum - 1 - k)));
				}
				query = query + " union " + tmpQuery;
				tmpPara = tmpPara + "+" + tmpParas.get(0);
			}
		}
    	tmpParas.clear();
    	tmpParas.add(tmpPara);		
		
		System.out.println("ParaListQuery : " + query);
		System.out.println("END OF : " + node.toString());
		return query;
	}

	@Override
	public Object visit(ASTN_PolymorPara node, Object data) {
		System.out.println("THIS IS : " + node.toString());
		
		polymorChildNum = node.jjtGetNumChildren();
		System.out.println("polymorChildNum : " + polymorChildNum);

		// zonop add. if Type includes integer and String same line.
		polymorFlg = true;
		
		StringBuilder sb = new StringBuilder();
		tmpParas.clear();
		for (int i = 0; i < polymorChildNum; i++) {
			System.out.println("child [ " + i + " ] type : " + node.jjtGetChild(i).toString());
			node.jjtGetChild(i).jjtAccept(this, null);
			if (i == 0)
				sb.append(tmpParas.get(i));
			else
				sb.append("-" + tmpParas.get(i));
		}
		String tmpPara = new String(sb);
		tmpParas.clear();
		tmpParas.add(tmpPara);
		polymorFlg = false;
		
		String queryKey = rule + "[" + polymorChildNum + "]";
		String query = "(" + domBundle.getString(queryKey) + ")";
		if (!isGenerate)
			minimumQueries.add(query);
		System.out.println("PolymorQuery : " + query);
		
		String regex = "$";
		String tmpQuery = query;
		int count = 0;
		for (int j = tmpQuery.indexOf(regex); j > 0; j = tmpQuery.indexOf(regex, j + 1)) {
			count++;
		}
		if (!isGenerate)
			paraNums.add(count);
		else
			generateParaNums.add(count);
		System.out.println("count : " + count);
		
		polymorChildNum = 0;
		
		System.out.println("END OF : " + node.toString());
		return query;
	}
	
	@Override
	public Object visit(SimpleNode node, Object data) {
		return null;
	}
	
	@Override
	public String getRecipient() {
		return recipient;
	}
	
	@Override
	public String getFullDomain() {
		if (subDomain.isEmpty())
			return domain;
		else
			return subDomain + "." + domain;
	}
	
	@Override
	public String getDomain() {
		return domain;
	}
	
	@Override
	public String getSubDomain() {
		return subDomain;
	}
	
	@Override
	public String getQuery() {
		return finalQuery;
	}
	
	@Override
	public ArrayList<String> getQueries() {
		ArrayList<String> replacedQueries = new ArrayList<String>();
		for (int i = 0; i < queries.size(); i++)
			replacedQueries.add(simpleReplace(queries.get(i)));
		
		return replacedQueries;
	}
	
	@Override
	public ArrayList<String> getRules() {
		return rules;
	}
	
	@Override
	public ArrayList<String> getOperators() {
		return operators;
	}

	
	@Override
	public ArrayList<String> getParaList() {
		return paraList;
	}
	
	@Override
	public ArrayList<String> getParas() {
		return paras;
	}

	@Override
	public ArrayList<Integer> getParaNums() {
		return paraNums;
	}

	@Override
	public ArrayList<String> getGeneQueries() {
		ArrayList<String> replacedQueries = new ArrayList<String>();
		for (int i = 0; i < generateQueries.size(); i++)
			replacedQueries.add(simpleReplace(generateQueries.get(i)));
		
		return replacedQueries;
	}

	@Override
	public ArrayList<String> getGeneRules() {
		return generateRules;
	}

	@Override
	public ArrayList<String> getGeneParaList() {
		return generateParaList;
	}

	@Override
	public ArrayList<String> getGeneParas() {
		return generateParas;
	}

	@Override
	public ArrayList<Integer> getGeneParaNums() {
		return generateParaNums;
	}
	
	@Override
	public boolean getDeliveryFlg() {
		return deliveryFlg;
	}
	
	@Override
	public boolean getGenerateFlg() {
		return generateFlg;
	}
	
	@Override
	public boolean getFunctionFlg() {
		return functionFlg;
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
	public ArrayList<String> getCommandArgs() {
		return commandArgs;
	}

	@Override
	public String getTarget() {
		if (getFunctionFlg()) {
			int start = recipient.indexOf("#", 1) + 1;
			return recipient.substring(start);
		} else {
			return null;
		}
	}
	
	@Override
	public boolean containsAddressPara(){
		for (int i = 0; i < paras.size(); i++) {
			if (paras.get(i).equals("$sender") || paras.get(i).equals("$recipient"))
				return true;
		}
		for (int i = 0; i < generateParas.size(); i++) {
			if (generateParas.get(i).equals("$sender") || generateParas.get(i).equals("$recipient"))
				return true;
		}
		return false;
	}
	
	private String simpleReplace(String query) {
		for (int i = 1; ; i++){
			String regex = "$" + Integer.toString(i);
			if (query.indexOf(regex) < 0)
				break;
			query = query.replaceAll("\\$" + Integer.toString(i), "?");
			System.out.println("regex : " + regex);
		}
		
		if(query.indexOf("$sender") > 0){
			query = query.replaceAll("\\$sender", "?");
			System.out.println("regex : $sender");
		}

		if(query.indexOf("$recipient") > 0){
			query = query.replaceAll("\\$recipient", "?");
			System.out.println("regex : $recipient");
		}
		
		return query;
	}
	
	private ArrayList<String> remakeParas(ArrayList<String> paras, ArrayList<String> queries, ArrayList<String> minimumQueries) {
		System.out.println("THIS IS remakeParas1");
		System.out.println("size : " + paras.size());

		boolean needRemake = false;
		// if query contains $recipient or $sender
		// or same $numbers are used several times in same query,
		// needRemake becomes true
		
		for (int i = 0; i < queries.size(); i++) {
			System.out.println(queries.get(i));
			if (queries.get(i).indexOf("$s") > 0 || queries.get(i).indexOf("$r") > 0) {
				needRemake = true;
				System.out.println("contains $sender or $recipient");
				break;
			}

			String tmpQuery = queries.get(i);
			for (int j = 0; tmpQuery.indexOf("$") > 0; j++) {
				if (tmpQuery.indexOf("$" + j) > 0) {
					tmpQuery = tmpQuery.replaceFirst("\\$", "?");
					if (tmpQuery.indexOf("$" + j) > 0) {
						System.out.println("same $numbers are used in same query");
						needRemake = true;
						break;
					}
				}
			}
		}

		System.out.println("needRemake : " + needRemake);

		ArrayList<String> tmpParas_ = new ArrayList<String>();
		if (needRemake) {
			for (int i = 0, paraIndex = 0; i < minimumQueries.size(); i++){
				String currentQuery = minimumQueries.get(i);
				
				for (int placeHolderNum = 1; paraIndex <= paras.size(); ) {
					int index = currentQuery.indexOf("$");
					if (index < 0) {
						// there is no $
						if (paraIndex < paras.size() - 1 && paras.get(paraIndex).equalsIgnoreCase("*")) {
							tmpParas_.add(paras.get(paraIndex++));
						}
						break;
						
					} else {
						String placeHolder = String.valueOf(currentQuery.charAt(index + 1));
						if (placeHolder.equals(Integer.toString(placeHolderNum))) {
							tmpParas_.add(paras.get(paraIndex++));
							currentQuery = currentQuery.replaceFirst("\\$", "?");
							placeHolderNum++;
						} else if (placeHolder.equals("s")) {
							tmpParas_.add("'$sender'");
							currentQuery = currentQuery.replaceFirst("\\$", "?");
						} else if (placeHolder.equals("r")) {
							tmpParas_.add("'$recipient'");
							currentQuery = currentQuery.replaceFirst("\\$", "?");
						} else {
							if (placeHolder.matches("^[1-9][0-9]*$")) {
								int nowPlaceHolder = Integer.valueOf(placeHolder);
								if (nowPlaceHolder <= placeHolderNum) {
									tmpParas_.add(paras.get(paraIndex - (placeHolderNum - nowPlaceHolder)));
								} else {
									tmpParas_.add(paras.get(paraIndex + (nowPlaceHolder - placeHolderNum)));
								}
								currentQuery = currentQuery.replaceFirst("\\$", "?");
							} else {
								System.out.println("there is illegal placeholder : " + currentQuery);
								break;
							}
						}
					}
				}
			}
		}
		
		System.out.println("THIS IS remakeParas2");
		
		if (!tmpParas_.isEmpty()) {
			paras = tmpParas_;
			System.out.println("remake paras");
		}
		
		System.out.println();
		return paras;
	}
	
	private void checkNest() {
		System.out.println("THIS IS checkNest");
		
		String nestedRuleSource = new String();
		String nestedRule = new String();
		try {
			// nested rule : defined Source in transfer rule
			nestedRuleSource = domBundle.getString(rule + "Source").trim();
			if (nestedRuleSource.indexOf("[") > 0)
				nestedRule = nestedRuleSource.substring(0, nestedRuleSource.indexOf("["));
			else
				nestedRule = nestedRuleSource;
		} catch (Exception e) {}
		System.out.println("nestedRule : "+ nestedRule);

		if (nestedRule.isEmpty()) {
			System.out.println("it doesn't use nest");
		} else {
			generateFlg = true;
			
			String passedPara = new String();
			int refNum = 0;
			try {
				String sourcePara = new String();
				if (nestedRuleSource.indexOf("[") > 0) {
					sourcePara = nestedRuleSource.substring(nestedRuleSource.indexOf("[")).replaceAll(" ", "");

					ArrayList<String> sourceParaList = new ArrayList<String>(); 
					if (sourcePara.indexOf(",") < 0) {
						sourceParaList.add(sourcePara);
					} else {
						String[] sourceParas = sourcePara.split(",");
						for (int i = 0; i < sourceParas.length; i++) {
							if (!sourceParas[i].isEmpty())
								sourceParaList.add(sourceParas[i]);
						}
					}
					
					int paraNum = paraNums.get(paraNums.size() - 1);
					if (paraNum == 0 && !paras.get(paras.size() - 1).equalsIgnoreCase("*"))
						refNum = 1;
					else
						refNum = paraNum;
					String regex = new String("[" + refNum + ":");
					System.out.println("index : " + regex);
					
					for (int j = 0; j < sourceParaList.size(); j++) {
						String tmpSourcePara = sourceParaList.get(j);
						System.out.println(tmpSourcePara);
						
						int beginIndex = tmpSourcePara.indexOf(regex);
						if (beginIndex >= 0) {
							int endIndex = tmpSourcePara.indexOf("]");
							passedPara = tmpSourcePara.substring(beginIndex + 3, endIndex);
							break;
						}
					}
				} else {
					passedPara = "*";
				}
			} catch(Exception e) {}
			System.out.println("passedPara : " + passedPara);
			System.out.println("paras size : " + paras.size());

			String nestedQuery = new String();
			try {
				int queryKeyIndex = 0;

				if (passedPara.isEmpty()) {
					throw new Exception();
					
				} else if (passedPara.equalsIgnoreCase("*")) {
					generateParas.add(passedPara);
					generateParaList.add(passedPara);
					
				} else {
					String tmpPassedPara = passedPara;
					StringBuilder tmpParaList = new StringBuilder();
					
					if (tmpPassedPara.indexOf("-") > 0) {
						//number of passed parameter is more than 1
						String firstPara = new String();
						for (; !tmpPassedPara.isEmpty(); queryKeyIndex++) {
							//tmpPassedPara : $1-obunai-$2
							//firstPara : $1
							if (tmpPassedPara.indexOf("-") > 0) {
								firstPara = tmpPassedPara.substring(0, tmpPassedPara.indexOf("-"));
								tmpPassedPara = tmpPassedPara.substring(tmpPassedPara.indexOf("-") + 1);
							} else {
								//last para
								firstPara = tmpPassedPara;
								tmpPassedPara = new String();
							}
							
							if (firstPara.indexOf("$") >= 0) {
								//passed parameter contains $
								//paras : […, obunai, matt], firstPara : $2
								//new paras : […, obunai, matt, matt]
								int paraIndex = paras.size() - (refNum - Character.getNumericValue(firstPara.charAt(firstPara.indexOf("$") + 1)) + 1);
								generateParas.add(paras.get(paraIndex));
								if (tmpParaList.length() == 0)
									tmpParaList.append(paras.get(paraIndex));
								else
									tmpParaList.append("-" + paras.get(paraIndex));
							} else {
								//passed parameter is constant
								//type is nested rule's type
								//paras : [obunai, matt], firstPara : yohei
								//new paras : [obunai, matt, yohei]
								generateParas.add(firstPara);
								if (tmpParaList.length() == 0)
									tmpParaList.append(firstPara);
								else
									tmpParaList.append("-" + firstPara);
							}
						}
						
					} else {
						//number of passed parameter is 1
						//[1:$1] or [2:obunai]		
						queryKeyIndex++;
						if (tmpPassedPara.indexOf("$") >= 0) {
							//passed parameter contains $
							//paras : […, String, obunai, String, matt], tmpPassedPara : $2
							//new paras : […, String, obunai, String, matt, String, matt]
							int paraIndex = paras.size() - (refNum - Character.getNumericValue(tmpPassedPara.charAt(tmpPassedPara.indexOf("$") + 1)) + 1);
							generateParas.add(paras.get(paraIndex));
							if (tmpParaList.length() == 0)
								tmpParaList.append(paras.get(paraIndex));
							else
								tmpParaList.append("-" + paras.get(paraIndex));
						} else {
							//if passed parameter is constant
							//type is nested rule's type
							//paras : [String, obunai, String, matt], yohei
							//new paras : [String, obunai, String, matt, String, yohei]
							generateParas.add(tmpPassedPara);
							if (tmpParaList.length() == 0)
								tmpParaList.append(tmpPassedPara);
							else
								tmpParaList.append("-" + tmpPassedPara);
						}
					}
					
					generateParaList.add(tmpParaList.toString());
				}

				String nestedQueryKey = nestedRule + "[" + queryKeyIndex + "]";
				System.out.println("nestedQueryKey : " + nestedQueryKey);
				
				nestedQuery =  domBundle.getString(nestedQueryKey);
				String regex = "$";
				String tmpQuery = nestedQuery;
				int count = 0;
				for (int j = tmpQuery.indexOf(regex); j > 0; j = tmpQuery.indexOf(regex, j + 1)) {
					count++;
				}
				System.out.println("count : " + count);
				generateParaNums.add(count);
				generateParaNums.add(-1);

				
				generateQueries.add(nestedQuery);
				generateRules.add(nestedRule);

			} catch(Exception e) {
				nestedQuery = "miss";
			}			

			System.out.println("nestedQuery : " + nestedQuery);
		}
	}
	
	private Node selectStartNode(String recipient) throws ParseException {
		Parser parser = new Parser(new StringReader(recipient));
		String target = recipient.substring(recipient.indexOf("#", 1) + 1);
		
		if (target.contains("{") && target.contains("}"))
			return parser.Recipient();
		else
			return parser.N_Recipient();
	}
	
	private String reformRecipient(String recipient, ParseAddress visitor) {
		ArrayList<String> rules = visitor.rules;
		ArrayList<String> paraList = visitor.paraList;
		ArrayList<String> operators = visitor.operators;
		ArrayList<String> ruleTypeList = visitor.ruleTypeList; 

		String newRecipient = new String();
		if (recipient.contains("{") && recipient.contains("}")) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0, j = 0; i < ruleTypeList.size(); i++) {
				if (ruleTypeList.get(i).equalsIgnoreCase("delivery")) {
					if (i != 0 && sb.length() != 0)
						sb.append(operators.get(i - 1));
					sb.append(rules.get(j) + "{" + paraList.get(j++) + "}");
				}
			}
			newRecipient = sb.toString() + "@" + visitor.getFullDomain();
		} else {
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			for (int i = 0, j = 0; i < ruleTypeList.size(); i++) {
				if (ruleTypeList.get(i).equalsIgnoreCase("delivery")) {
					if (i != 0) {
						sb1.append(".");
						sb2.append(".");
					}
					sb1.append(paraList.get(j));
					sb2.append(rules.get(j++));
				}
			}
			newRecipient = sb1.toString() + "@" + sb2.toString() + "." + visitor.getFullDomain();
		}
		
		return newRecipient;
	}

}
