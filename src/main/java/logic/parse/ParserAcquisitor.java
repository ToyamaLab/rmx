package logic.parse;

import java.util.ArrayList;

public interface ParserAcquisitor {
	
	public String getRecipient();
	public String getFullDomain();
	public String getDomain();
	public String getSubDomain();
	public String getQuery();
	public ArrayList<String> getQueries();
	public ArrayList<String> getRules();
	public ArrayList<String> getOperators();
	public ArrayList<String> getParaList();
	public ArrayList<String> getParas();
	public ArrayList<Integer> getParaNums();
	public ArrayList<String> getGeneQueries();
	public ArrayList<String> getGeneRules();
	public ArrayList<String> getGeneParaList();
	public ArrayList<String> getGeneParas();
	public ArrayList<Integer> getGeneParaNums();
	public boolean getDeliveryFlg();
	public boolean getGenerateFlg();
	public boolean getFunctionFlg();
	public String getFunction();
	public String getCommand();
	public ArrayList<String> getCommandArgs();
	public String getTarget();
	public boolean containsAddressPara();

}
