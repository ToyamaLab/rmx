package logic.authorization.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Stack;
import java.util.ResourceBundle;

import logic.authorization.AuthorizeSender;
import logic.parse.Parsable;
import dao.DatabaseDao;
import dao.impl.DatabaseDaoImpl;

public class AuthorizeSenderImpl implements AuthorizeSender{
	private ResourceBundle domconfBundle;
	private ArrayList<String> unauthorizedRulesList;
	
	public AuthorizeSenderImpl(ResourceBundle _domconfBundle, Parsable parser, String sender) {
		domconfBundle = _domconfBundle;
		if (authorizationIsOn())
			unauthorizedRulesList = getUnauthorizedRules(parser, sender);
		else
			unauthorizedRulesList = new ArrayList<String>();
	}
	
	public boolean isAuthorized() {
		if (unauthorizedRulesList.isEmpty())
			return true;
		else
			return false;
	}
	
	public ArrayList<String> getUnauthorizedRules() {
		return unauthorizedRulesList;
	}
	
	private boolean authorizationIsOn() {
		try {
			if (domconfBundle.getString("AUTHORIZATION").trim().equalsIgnoreCase("ON"))
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	private ArrayList<String> getUnauthorizedRules(Parsable parser, String sender) {
		ArrayList<String> unauthorizedRules = new ArrayList<String>();
		
		ArrayList<String> paraList = parser.getParaList();
		ArrayList<String> queries = parser.getQueries();
		ArrayList<String> operators = parser.getOperators();
		ArrayList<String> rules = parser.getRules();
		ArrayList<String> paras = parser.getParas();
		ArrayList<Integer> paraNums = parser.getParaNums();

		ArrayList<String> appliedRules = applyRules(sender);
		ArrayList<String> defaultQueries = constructDefaultQueries(appliedRules);

		/* 各々のルール（集合）の判定 */
		for (int ruleIndex1 = 0, paraIndex1 = 0, paraNumIndex1 = 0; ruleIndex1 < rules.size(); ) {
			String rule = new String();
			String deliQuery = new String();
			ArrayList<String> tmppara = new ArrayList<String>();
			String localQuery = new String();

			/* 積集合を取るルールのクエリ連結 */
			while (true) {
				// 配送ルールのクエリ取得
				if (deliQuery.isEmpty())
					deliQuery = queries.get(ruleIndex1);
				else
					deliQuery = "(" + deliQuery + ") intersect (" + queries.get(ruleIndex1) +")";
				// 配送ルールのパラメータ取得
				while (paraNums.get(paraNumIndex1++) != -1) {
					int paraNum = paraNums.get(paraNumIndex1 - 1);
					if (paraNum == 0 && paras.get(paraIndex1 + 1).equalsIgnoreCase("*"))
						paraIndex1 += 2;
					else for (int i = 0; i < paraNum; i++) {
						while (paras.get(paraIndex1 + 1).equalsIgnoreCase("*"))
							paraIndex1 += 2;
						tmppara.add(paras.get(paraIndex1++));
						tmppara.add(paras.get(paraIndex1++));
					}
				}
				// アドレス上の記述配送ルール・パラメータの取得
				rule = rule.concat(rules.get(ruleIndex1) + "{" + paraList.get(ruleIndex1) + "}");
				
				// 配送別許可ルールの取得
				if (!localQuery.equalsIgnoreCase("null")) {
					String tmpLocalQuery = constructLocalQuery(rules.get(ruleIndex1), defaultQueries, appliedRules);
					if (tmpLocalQuery.equalsIgnoreCase("null"))
						localQuery = tmpLocalQuery;
					else if (localQuery.equalsIgnoreCase("all")) {
						if (!tmpLocalQuery.equalsIgnoreCase("all"))
								localQuery = tmpLocalQuery.replace("$sender", "'" + sender + "'");
					} else {
						if (tmpLocalQuery.equalsIgnoreCase("all"))
							localQuery = tmpLocalQuery;
						else if (localQuery.isEmpty())
							localQuery = tmpLocalQuery.replace("$sender", "'" + sender + "'");
						else
							localQuery = "(" + localQuery + ") intersect (" + tmpLocalQuery.replace("$sender", "'" + sender + "'") + ")";
					}
				}
				
				// すべてのruleを見たらwhileを抜ける
				if (ruleIndex1 == rules.size() - 1) {
					ruleIndex1++;
					break;
				}
				// 積集合を取るルールが無ければwhileを抜ける
				if (!operators.get(ruleIndex1++).equalsIgnoreCase(".")) {
					break;
				}
				
				rule = rule.concat(".");
			}	/* end while */

			/* 取得配送ルール以降のexceptルールの取得 */
			for (int ruleIndex2 = ruleIndex1, paraIndex2 = paraIndex1, paraNumIndex2 = paraNumIndex1; ruleIndex2 < rules.size(); ) {
				if (operators.get(ruleIndex2 - 1).equalsIgnoreCase("-")) {
					String exceptQuery = queries.get(ruleIndex2++);
					while (paraNums.get(paraNumIndex2++) != -1) {
						int paraNum = paraNums.get(paraNumIndex2 - 1);
						if (paraNum == 0 && paras.get(paraIndex2 + 1).equalsIgnoreCase("*"))
							paraIndex2 += 2;
						else for (int i = 0; i < paraNum; i++) {
							while (paras.get(paraIndex2 + 1).equalsIgnoreCase("*"))
								paraIndex2 += 2;
							tmppara.add(paras.get(paraIndex2++));
							tmppara.add(paras.get(paraIndex2++));
						}
					}
					
					while (ruleIndex2 < rules.size()) {
						if (!operators.get(ruleIndex2 - 1).equalsIgnoreCase("."))
							break;
						exceptQuery = "(" + exceptQuery + ") intersect (" + queries.get(ruleIndex2++) + ")";
						while (paraNums.get(paraNumIndex2++) != -1) {
							int paraNum = paraNums.get(paraNumIndex2 - 1);
							if (paraNum == 0 && paras.get(paraIndex2 + 1).equalsIgnoreCase("*"))
								paraIndex2 += 2;
							else for (int i = 0; i < paraNum; i++) {
								while (paras.get(paraIndex2 + 1).equalsIgnoreCase("*"))
									paraIndex2 += 2;
								tmppara.add(paras.get(paraIndex2++));
								tmppara.add(paras.get(paraIndex2++));
							}
						}
					}
					deliQuery = "(" + deliQuery + ") except (" + exceptQuery + ")";
				} else {
					ruleIndex2++;
					while (paraNums.get(paraNumIndex2++) != -1) {
						int paraNum = paraNums.get(paraNumIndex2 - 1);
						if (paraNum == 0 && paras.get(paraIndex2 + 1).equalsIgnoreCase("*"))
							paraIndex2 += 2;
						else for (int i = 0; i < paraNum; i++) {
							while (paras.get(paraIndex2 + 1).equalsIgnoreCase("*"))
								paraIndex2 += 2;
							paraIndex2 += 2;
						}
					}
				}
			}
			/* exceptルールは直接チェックしないためスキップ */
			while (ruleIndex1 < rules.size()) {
				if (!operators.get(ruleIndex1 - 1).equalsIgnoreCase("-"))
					break;
				ruleIndex1++;
				while (paraNums.get(paraNumIndex1++) != -1) {
					int paraNum = paraNums.get(paraNumIndex1 - 1);
					if (paraNum == 0 && paras.get(paraIndex1 + 1).equalsIgnoreCase("*"))
						paraIndex1 += 2;
					else for (int i = 0; i < paraNum; i++) {
						while (paras.get(paraIndex1 + 1).equalsIgnoreCase("*"))
							paraIndex1 += 2;
						paraIndex1 += 2;
					}
				}
				while (ruleIndex1 < rules.size()) {
					if (!operators.get(ruleIndex1 - 1).equalsIgnoreCase("."))
						break;
					ruleIndex1++;
					while (paraNums.get(paraNumIndex1++) != -1) {
						int paraNum = paraNums.get(paraNumIndex1 - 1);
						if (paraNum == 0 && paras.get(paraIndex1 + 1).equalsIgnoreCase("*"))
							paraIndex1 += 2;
						else for (int i = 0; i < paraNum; i++) {
							while (paras.get(paraIndex1 + 1).equalsIgnoreCase("*"))
								paraIndex1 += 2;
							paraIndex1 += 2;
						}
					}
				}
			}

			/* 許可／不許可の判定 */
			if (!judgeRuleAuthorized(deliQuery, tmppara, localQuery))
				unauthorizedRules.add(rule);
		}	/* end for */
		
		return unauthorizedRules;
	}
	
	/* Selectorを基にApplyから適用する許可ルールを決定 */
	private ArrayList<String> applyRules(String sender) {
		ArrayList<String> appliedRules = new ArrayList<String>();
		boolean allFlag = true;
		boolean nullFlag = false;
		
		for (Enumeration<String> e = domconfBundle.getKeys(); e.hasMoreElements(); ) {
			String key = e.nextElement();
			if (key.matches("Selector\\[[a-zA-Z\\d\\*_]+\\]")) {
				String index = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
				if (!domconfBundle.containsKey("Apply["+index+"]"))
					continue;
				
				if (allFlag) {
					allFlag = false;
					nullFlag = true;
				}
				
				String selector = domconfBundle.getString(key);				
				String selectQuery = new String();
				if (selector.substring(0, 6).equalsIgnoreCase("select"))
					selectQuery = selector.replace("$sender", "'" + sender + "'");
				else
					selectQuery = "select 1 " + selector.replace("$sender", "'" + sender + "'");
				
				DatabaseDao db = new DatabaseDaoImpl(domconfBundle);
				ResultSet rs;
				try {
					rs = db.read(selectQuery, new ArrayList<String>().listIterator());
					if (rs.next()) {
						appliedRules.add(domconfBundle.getString("Apply["+index+"]").trim());
						if (nullFlag)
							nullFlag = false;
					}
					rs.close();
					db.close();
				} catch (ClassNotFoundException E) {
					E.printStackTrace();
				} catch (SQLException E) {
					E.printStackTrace();
				}
			}
		}
		
		if (allFlag)
			appliedRules.add("Default:all");
		else if (nullFlag)
			appliedRules.add("Default:null");
		
		return appliedRules;
	}
	
	/* デフォルト許可ルールのクエリのリストを作成 */
	private ArrayList<String> constructDefaultQueries(ArrayList<String> appliedRules) {
		final String DEFAULT = "Default:";
		ArrayList<String> defaultQueries = new ArrayList<String>();
		
		for (int i = 0; i < appliedRules.size(); i++){
			String appliedRule = appliedRules.get(i);
			if (!appliedRule.contains(DEFAULT)) {
				defaultQueries.add("");
				continue;
			}
			
			int defaultBeginIndex = appliedRule.indexOf(DEFAULT) + DEFAULT.length();
			int defaultEndIndex = (appliedRule.indexOf(" ", defaultBeginIndex) > 0) ? appliedRule.indexOf(" ", defaultBeginIndex) : appliedRule.length();
			String defaultRule = appliedRule.substring(defaultBeginIndex, defaultEndIndex);
			if (defaultRule.equalsIgnoreCase("all") || defaultRule.equalsIgnoreCase("null"))
				defaultQueries.add(defaultRule.toLowerCase());
			else
				defaultQueries.add(constructQuery(defaultRule));
		}
		
		return defaultQueries;
	}
	
	/* 配送別許可ルールのクエリを作成 */
	private String constructLocalQuery(String deliRule, ArrayList<String> defaultQueries, ArrayList<String> appliedRules) {
		final String LOCAL = deliRule + ":";
		ArrayList<String> localQueries = new ArrayList<String>();
		String localQuery = new String();
		
		for (int i = 0; i < appliedRules.size(); i++){
			String appliedRule = appliedRules.get(i);
			if (appliedRule.contains(LOCAL)) {
				int localBeginIndex = appliedRule.indexOf(LOCAL) + LOCAL.length();
				int localEndIndex = (appliedRule.indexOf(" ", localBeginIndex) > 0) ? appliedRule.indexOf(" ", localBeginIndex) : appliedRule.length();
				String localRule = appliedRule.substring(localBeginIndex, localEndIndex);
				if (localRule.equalsIgnoreCase("all") || localRule.equalsIgnoreCase("null"))
					localQueries.add(localRule.toLowerCase());
				else
					localQueries.add(constructQuery(localRule).replace("Default", defaultQueries.get(i)));
			} else {
				localQueries.add(defaultQueries.get(i));
			}
		}
		
		if (!localQueries.isEmpty()) {
			if (localQueries.contains("null"))
				return "null";
			else if (localQueries.contains("all"))
				return "all";
			else for (int i = 0; i < localQueries.size(); i++) {
				if (localQueries.get(i).isEmpty())
					continue;
				else if (localQuery.isEmpty())
					localQuery = localQueries.get(i);
				else
					localQuery = "(" + localQuery + ") union (" + localQueries.get(i) + ")";
			}
		}
		
		if (localQuery.isEmpty())
			localQuery = "miss";
		
		return localQuery;
	}
	
	/* インデックス表記の許可ルールをRPNに変換してからクエリ生成 */
	private String constructQuery(String indexedRule) {
		ArrayList<String> RPNRule = translateToRPN(indexedRule);
		ArrayList<Boolean> translatedFlagList = new ArrayList<Boolean>();
		
		for (int i = 0; i < RPNRule.size(); i++)
			translatedFlagList.add(false);
		
		if (RPNRule.size() == 1)
			RPNRule.set(0, translateIndexToQuery(RPNRule.get(0), translatedFlagList.get(0)));
		else {
			int RPNIndex = 0;
			while (RPNRule.size() > 1) {
				if (RPNRule.get(RPNIndex + 2).equalsIgnoreCase(".") || RPNRule.get(RPNIndex + 2).equalsIgnoreCase("+")) {
					String tmpRule = "(" + translateIndexToQuery(RPNRule.get(RPNIndex), translatedFlagList.get(RPNIndex)) + ") " + translateIndexToQuery(RPNRule.get(RPNIndex + 2), translatedFlagList.get(RPNIndex + 2)) + " (" + translateIndexToQuery(RPNRule.get(RPNIndex + 1), translatedFlagList.get(RPNIndex + 1)) + ")";
					RPNRule.subList(RPNIndex, RPNIndex + 3).clear();
					translatedFlagList.subList(RPNIndex, RPNIndex + 3).clear();
					RPNRule.add(RPNIndex, tmpRule);
					translatedFlagList.add(RPNIndex, true);
					RPNIndex--;
				} else {
					RPNIndex++;
				}
			}
		}
		
		return RPNRule.get(0);
	}
	
	/* 許可ルールをRPNに変換 */
	private ArrayList<String> translateToRPN(String indexedRule) {
		ArrayList<String> RPNRule = new ArrayList<String>();
		
		StringTokenizer token = new StringTokenizer(indexedRule, ".+()", true);
		Stack<String> stack = new Stack<String>();
		while (token.hasMoreTokens()) {
			String t = token.nextToken();
			if (t.equalsIgnoreCase(".")) {
				while (!stack.empty()) {
					if (stack.peek().equalsIgnoreCase("."))
						RPNRule.add(stack.pop());
					else 
						break;
				}
				stack.push(t);
			} else if (t.equalsIgnoreCase("+")) {
				while (!stack.empty()) {
					if (stack.peek().equalsIgnoreCase(".") || stack.peek().equalsIgnoreCase("+"))
						RPNRule.add(stack.pop());
					else 
						break;
				}
				stack.push(t);
			} else if (t.equalsIgnoreCase("(")) {
				stack.push(t);
			} else if (t.equalsIgnoreCase(")")) {
				while (!stack.peek().equalsIgnoreCase("("))
					RPNRule.add(stack.pop());
				stack.pop();
			} else {
				RPNRule.add(t);
			}
		}
		while (!stack.empty())
			RPNRule.add(stack.pop());
		
		return RPNRule;
	}
	
	/* 許可ルールのシンボルをクエリに変換 */
	private String translateIndexToQuery(String ruleSymbol, boolean translatedFlag) {
		if (ruleSymbol.equals("Default") || translatedFlag)
			return ruleSymbol;
		
		String ruleString;
		if (ruleSymbol.equals("."))
			ruleString = "intersect";
		else if (ruleSymbol.equals("+"))
			ruleString = "union";
		else if (domconfBundle.containsKey("Auth["+ruleSymbol+"]"))
			ruleString = domconfBundle.getString("Auth["+ruleSymbol+"]");
		else
			ruleString = "miss";
		
		return ruleString;
	}
	
	/* 許可判定 */
	private boolean judgeRuleAuthorized(String deliQuery, ArrayList<String> paras, String localQuery) {
		boolean authFlag = false;
		
		if (localQuery.equalsIgnoreCase("null")) {
			System.out.println("[checkQuery] null");	//
		} else if (localQuery.equalsIgnoreCase("all")) {
			System.out.println("[checkQuery] all");		//	
			authFlag = true;
		} else {
			String checkQuery = "( " + deliQuery + " ) except ( " + localQuery + " )";
			System.out.println("[checkQuery] " + checkQuery);	//
			
			ListIterator<String> params = paras.listIterator();
			DatabaseDao db = new DatabaseDaoImpl(domconfBundle);
			ResultSet rs;
			try {
				rs = db.read(checkQuery, params);
				if (!rs.next())
					authFlag = true;
				rs.close();
				db.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return authFlag;
	}
	
}
