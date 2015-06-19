package logic.authorization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Stack;
import java.util.ResourceBundle;

import logic.parse.Parsable;
import dao.DatabaseDao;
import dao.impl.DatabaseDaoImpl;

public class AuthorizeSender {
	private ResourceBundle domconfBundle;
	
	//コンストラクタ
	public AuthorizeSender(ResourceBundle _domconfBundle) {
		domconfBundle = _domconfBundle;
	}
	
	public boolean authorizationIsOn() {
		if (domconfBundle.containsKey("AUTHORIZATION")) {
			if (domconfBundle.getString("AUTHORIZATION").equalsIgnoreCase("ON"))
				return true;
		}
		return false;
	}
	
	public ArrayList<String> getUnauthorizedRules(Parsable parser, String sender) {
		ArrayList<String> unauthorizedRules = new ArrayList<String>();
		ArrayList<String> queries = parser.getqueries();
		ArrayList<String> keys = parser.getKeys();
		ArrayList<String> para = parser.getPara();
		ArrayList<String> paralist = parser.getparalist();
		ArrayList<Integer> paranum = parser.getParanum();
		ArrayList<String> operator = parser.getoperator();
		
		ArrayList<String> appliedRules = applyRules(sender);
		ArrayList<String> defaultQueries = constructDefaultQueries(appliedRules);

		for (int keyi = 0, parai = 0, paranumi = 0; keyi < keys.size(); ) {
			String deliQuery = new String();
			String localQuery = new String();
			String rule = new String();
			ArrayList<String> tmppara = new ArrayList<String>();

			// 積集合を取るルールのクエリ連結
			while (keyi < keys.size()) {
				// 配送ルールのクエリ取得
				if (deliQuery.isEmpty())
					deliQuery = queries.get(keyi);
				else
					deliQuery = "(" + deliQuery + ") intersect (" + queries.get(keyi) +")";
				// 配送ルールのパラメータ取得
				if (para.get(parai + 1).equalsIgnoreCase("all") || para.get(parai + 1).equalsIgnoreCase("*")) {
					tmppara.add(para.get(parai++));
					tmppara.add(para.get(parai++));
					paranumi++;
				} else while (paranum.get(paranumi++) != -1) {
					tmppara.add(para.get(parai++));
					tmppara.add(para.get(parai++));
				}
				// アドレス上の記述配送ルール・パラメータの取得
				rule = rule.concat(keys.get(keyi) + "{" + paralist.get(keyi) + "}");
				
				// 配送別許可ルールの取得
				if (!localQuery.equalsIgnoreCase("null")) {
					String tmpLocalQuery = constructLocalQuery(keys.get(keyi), defaultQueries, appliedRules);
					if (tmpLocalQuery.equalsIgnoreCase("null")) {
						localQuery = tmpLocalQuery;
					} else if (!tmpLocalQuery.isEmpty() && !tmpLocalQuery.equalsIgnoreCase("all")) {
						if (localQuery.isEmpty())
							localQuery = tmpLocalQuery.replace("$sender", "'" + sender + "'");
						else
							localQuery = "(" + localQuery + ") intersect (" + tmpLocalQuery.replace("$sender", "'" + sender + "'") + ")"; 		
					}
				}
				
				// すべてのkeyを見たらwhileを抜ける
				if (keyi == keys.size() - 1) {
					keyi++;
					break;
				}
				// 積集合を取るルールが無ければwhileを抜ける
				if (!operator.get(keyi++).equalsIgnoreCase(".")) {
					break;
				}
				
				rule = rule.concat(".");
			}	/* end while */

			// 取得配送ルール以降のexceptルールの取得
			for (int keyj = keyi, paraj = parai, paranumj = paranumi; keyj < keys.size(); ) {
				if (operator.get(keyj - 1).equalsIgnoreCase("-")) {
					String exceptQuery = queries.get(keyj++);
					if (para.get(paraj + 1).equalsIgnoreCase("all") || para.get(paraj + 1).equalsIgnoreCase("*")) {
						tmppara.add(para.get(paraj++));
						tmppara.add(para.get(paraj++));
						paranumj++;
					} else while (paranum.get(paranumj++) != -1) {
						tmppara.add(para.get(paraj++));
						tmppara.add(para.get(paraj++));
					}
					while (keyj < keys.size()) {
						if (!operator.get(keyj - 1).equalsIgnoreCase("."))
							break;
						exceptQuery = "(" + exceptQuery + ") intersect (" + queries.get(keyj++) + ")";
						if (para.get(paraj + 1).equalsIgnoreCase("all") || para.get(paraj + 1).equalsIgnoreCase("*")) {
							tmppara.add(para.get(paraj++));
							tmppara.add(para.get(paraj++));
							paranumj++;
						} else while (paranum.get(paranumj++) != -1) {
							tmppara.add(para.get(paraj++));
							tmppara.add(para.get(paraj++));
						}
					}
					deliQuery = "(" + deliQuery + ") except (" + exceptQuery + ")";
				} else {
					keyj++;
					if (para.get(paraj + 1).equalsIgnoreCase("all") || para.get(paraj + 1).equalsIgnoreCase("*")) {
						paraj = paraj + 2;
						paranumj++;
					} else while (paranum.get(paranumj++) != -1) {
						paraj = paraj + 2;
					}
				}
			}
			// exceptルールは直接チェックしないためスキップ
			while (keyi < keys.size()) {
				if (!operator.get(keyi - 1).equalsIgnoreCase("-"))
					break;
				keyi++;
				if (para.get(parai + 1).equalsIgnoreCase("all") || para.get(parai + 1).equalsIgnoreCase("*")) {
					parai = parai + 2;
					paranumi++;
				} else while (paranum.get(paranumi++) != -1) {
					parai = parai + 2;
				}
				while (keyi < keys.size()) {
					if (!operator.get(keyi - 1).equalsIgnoreCase("."))
						break;
					keyi++;
					if (para.get(parai + 1).equalsIgnoreCase("all") || para.get(parai + 1).equalsIgnoreCase("*")) {
						parai = parai + 2;
						paranumi++;
					} else while (paranum.get(paranumi++) != -1) {
						parai = parai + 2;
					}
				}
			}

			// 許可／不許可の判定
			if (!judgeRuleAuthorized(deliQuery, tmppara, localQuery))
				unauthorizedRules.add(rule);
		}	// end for
		
		return unauthorizedRules;
	}
	
	/* Selectorを基にApplyから適用する許可ルールを決定 */
	private ArrayList<String> applyRules(String sender) {
		ArrayList<String> appliedRules = new ArrayList<String>();
		boolean nullFlag = true;
		
		for (Enumeration<String> e = domconfBundle.getKeys(); e.hasMoreElements(); ) {
			String key = e.nextElement();
			if (key.matches("Selector\\[[a-zA-Z\\d\\*_]+\\]")) {
				String index = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
				if (!domconfBundle.containsKey("Apply["+index+"]"))
					continue;
				
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
						appliedRules.add(domconfBundle.getString("Apply["+index+"]"));
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
		
		if (nullFlag)
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
			else if (localQueries.contains("all") || localQueries.contains(""))
				return "all";
			else for (int i = 0; i < localQueries.size(); i++){
				if (i == 0)
					localQuery = localQuery.concat(localQueries.get(i));
				else
					localQuery = "(" + localQuery + ") union (" + localQueries.get(i) + ")";
			}
		}
			
		return localQuery;
	}
	
	/* シンボル化された許可ルールをRPNに変換してからクエリ生成 */
	private String constructQuery(String Rule) {
		ArrayList<String> RPNRule = RPN(Rule);
		
		if (RPNRule.size() == 1)
			RPNRule.set(0, ruleSymbolToQuery(RPNRule.get(0), 0));
		else {
			int index = 0;
			ArrayList<Integer> flagList = new ArrayList<Integer>();
			for (int i = 0; i < RPNRule.size(); i++)
				flagList.add(0);
			while (RPNRule.size() > 1) {
				if (RPNRule.get(index + 2).equalsIgnoreCase(".") || RPNRule.get(index + 2).equalsIgnoreCase("+")) {
					String tmpRule = "(" + ruleSymbolToQuery(RPNRule.get(index), flagList.get(index)) + ") " + ruleSymbolToQuery(RPNRule.get(index + 2), flagList.get(index + 2)) + " (" + ruleSymbolToQuery(RPNRule.get(index + 1), flagList.get(index + 1)) + ")";
					for (int i = 0; i < 3; i++) {
						RPNRule.remove(index);
						flagList.remove(index);
					}
					RPNRule.add(index, tmpRule);
					flagList.add(index, 1);
					index--;
				} else {
					index++;
				}
			}
		}
		
		return RPNRule.get(0);
	}
	
	/* 許可ルールをRPNに変換 */
	private ArrayList<String> RPN(String rule) {
		ArrayList<String> RPNRule = new ArrayList<String>();
		
		StringTokenizer token = new StringTokenizer(rule, ".+()", true);
		Stack<String> stack = new Stack<String>();
		while (token.hasMoreTokens()) {
			String part = token.nextToken();
			if (part.equalsIgnoreCase(".")) {
				while (!stack.empty()) {
					if (stack.peek().equalsIgnoreCase("."))
						RPNRule.add(stack.pop());
					else 
						break;
				}
				stack.push(part);
			} else if (part.equalsIgnoreCase("+")) {
				while (!stack.empty()) {
					if (stack.peek().equalsIgnoreCase(".") || stack.peek().equalsIgnoreCase("+"))
						RPNRule.add(stack.pop());
					else 
						break;
				}
				stack.push(part);
			} else if (part.equalsIgnoreCase("(")) {
				stack.push(part);
			} else if (part.equalsIgnoreCase(")")) {
				while (!stack.peek().equalsIgnoreCase("("))
					RPNRule.add(stack.pop());
				stack.pop();
			} else {
				RPNRule.add(part);
			}
		}
		while (!stack.empty())
			RPNRule.add(stack.pop());
		
		return RPNRule;
	}
	
	/* 許可ルールのシンボルをクエリに変換 */
	private String ruleSymbolToQuery(String ruleSymbol, int flag) {
		if (ruleSymbol.equalsIgnoreCase("Default") || flag == 1)
			return ruleSymbol;
		
		String ruleString;
		if (ruleSymbol.equalsIgnoreCase("."))
			ruleString = "intersect";
		else if (ruleSymbol.equalsIgnoreCase("+"))
			ruleString = "union";
		else if (domconfBundle.containsKey("Auth["+ruleSymbol+"]"))
			ruleString = domconfBundle.getString("Auth["+ruleSymbol+"]");
		else
			ruleString = "miss";
		
		return ruleString;
	}
	
	private boolean judgeRuleAuthorized(String deliQuery, ArrayList<String> para, String localQuery) {
		boolean authFlag = true;
		
		if (!localQuery.isEmpty()) {
			if (localQuery.equalsIgnoreCase("null")) {
				System.out.println("[checkQuery] null");	//
				authFlag = false;
			} else {
				String checkQuery = "( " + deliQuery + " ) except ( " + localQuery + " )";
				System.out.println("[checkQuery] " + checkQuery);	//
				
				ListIterator<String> params = para.listIterator();
				DatabaseDao db = new DatabaseDaoImpl(domconfBundle);
				ResultSet rs;
				try {
					rs = db.read(checkQuery, params);
					if (rs.next())
						authFlag = false;
					rs.close();
					db.close();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return authFlag;
	}
	
}
