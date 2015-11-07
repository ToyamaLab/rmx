package logic.impl;

import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import logic.MakeMessage;
import logic.MakeMessageSelector;
import logic.parse.Parsable;

/**
 * {@link MakeMessageSelector}の実装
 */
public class MakeMessageSelectorImpl implements MakeMessageSelector {
	
	boolean transfer;
	boolean answer;
	boolean mixture;
	boolean function;

	public MakeMessageSelectorImpl() {
		transfer = false;
		answer = false;
		mixture = false;
		function = false;
	}
	
	/**
	 * @inheritDoc
	 * 返すのはTransfer, Answer, Function(いずれもMakeMessageを実装)
	 */
	@Override
	public MakeMessage select(Parsable parser, ResourceBundle domBundle) {
		
		if (parser.getNormalFlg()) {
			transfer = this.checkForTransfer(parser.getRules(), domBundle);
			answer = this.checkForAnswer(parser.getRules(), domBundle);
			mixture = this.checkForMixture(parser.getRules(), domBundle);
		} else if (parser.getFunctionFlg()) {
			function = true;
		}
		
		if (transfer)
			return new MakeTransfer(parser);
		if (answer)
			return null;
		if (mixture)
			return null;
		if (function)
			return new MakeFunction(parser, domBundle);
		else return new MakeError("syntax");

	}
	
	/**
	 * 使用されているルールが全て、配送ルールの時trueを返し、それ以外のときはfalseを返す.
	 * @param keys ルールの集合
	 * @param domBundle 
	 * */
	private boolean checkForTransfer(ArrayList<String> keys, ResourceBundle domBundle) {
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
					if (domBundle.getString(key).equalsIgnoreCase("generate")) {
						if(keys.size()>1)
							key_answer_flg[i] = true;
						AnswerFlag = true;
					} 
					else {
						if(keys.size()>1)
							key_answer_flg[i] = false;
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
	 * 使用されているルールが全て、生成ルールの時trueを返し、それ以外のときはfalseを返す.
	 * @param keys ルールの集合
	 * @param domBundle
	 * */
	private boolean checkForAnswer(ArrayList<String> keys, ResourceBundle domBundle) {
		boolean[] key_answer_flg = null;
		boolean NormalFlag = false;
		boolean AnswerFlag = false;
		boolean ERROR = false;

		if (keys.size() < 1) {
			return false;
		} 

		try {
			if (keys.size() > 1){
				key_answer_flg = new boolean[keys.size()];
			}
			
			for (int i = 0; i < keys.size(); i++) {
				String key = (String) keys.get(i);
				try {
					if (domBundle.getString(key).equalsIgnoreCase("generate")){
						if(keys.size()>1)
							key_answer_flg[i] = true;
						AnswerFlag = true;
					} 
					else {
						if(keys.size()>1)
							key_answer_flg[i] = false;
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
			
			if (NormalFlag && AnswerFlag){
				System.out.println("both transfer and answer rule(Parse)");
				return false;
			} else if (!NormalFlag && AnswerFlag) {
				System.out.println("only answer rule(Parse)");
				return true;
			} else if(NormalFlag && !AnswerFlag){
				System.out.println("only transfer rule(Parse)");
				return false;
			} else{
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
	 * 使用されているルールが生成および配送ルールを両方使用している時trueを返し、それ以外はfalseを返す.
	 * ただし、ルールが1つのときはfalseを返す.
	 * @param keys ルールの集合
	 * @param domBundle
	 * */
	private boolean checkForMixture (ArrayList<String> keys, ResourceBundle domBundle) {
		boolean[] key_answer_flg = null;
		boolean mixture_flg = false;
		
		if (keys.size() < 1)
			return false;
		
		else if (keys.size() > 1){
			key_answer_flg = new boolean[keys.size()];
			for (int i = 0; i < keys.size(); i++) {
				String key = (String) keys.get(i);
				if (domBundle.getString(key).equalsIgnoreCase("generate"))
					key_answer_flg[i] = true;
				else 
					key_answer_flg[i] = false;
			}
			
			System.out.println("key_answer_flg num : " + key_answer_flg.length);
			for(int i = 0; i < key_answer_flg.length-1; i++){
				System.out.println("searching key_answer_flg...");
				if(key_answer_flg[i] != key_answer_flg[i+1])
					mixture_flg = true;
			}
		}	
		return mixture_flg;
	}
}
