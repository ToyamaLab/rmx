package logic.parse;

import java.util.ArrayList;

public interface ParserAcquisitor {
	
	public ArrayList<String> getParaList();
	public ArrayList<String> getValues();
	public ArrayList<String> getQueries();
	public ArrayList<String> getOperators();
	public ArrayList<String> getRules();
	public String getDomain();
	public String getFunction();
	public boolean getFunctionFlg();
	public boolean getNormalFlg();
	public String getCommand();
	public ArrayList<String> getCommandArgs();
	public String getTarget();
	public String getSubdomain();
	public String getRecipient();
	public ArrayList<String> getParas();
	public ArrayList<Integer> getParaNums();
	public String getQuery();
	public String getFullDomain();

}
