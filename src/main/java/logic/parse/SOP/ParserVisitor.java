/* Generated By:JavaCC: Do not edit this line. ParserVisitor.java Version 5.0 */
package logic.parse.SOP;

import java.util.ArrayList;

public interface ParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTRecipient node, Object data);
  public Object visit(ASTPluginEx node, Object data);
  public Object visit(ASTPlugin node, Object data);
  public Object visit(ASTAddress node, Object data);
  public Object visit(ASTdomain node, Object data);
  public Object visit(ASTException node, Object data);
  public Object visit(ASTUnion node, Object data);
  public Object visit(ASTIntersection node, Object data);
  public Object visit(ASTExp node, Object data);
  public Object visit(ASTParalis node, Object data);
  public Object visit(ASTPolimolPara node, Object data);
  public Object visit(ASTRule node, Object data);
  public Object visit(ASTValue node, Object data);
  public Object visit(ASTSubdomain node, Object data);
  public Object visit(ASTArg node, Object data);
  public Object visit(ASTfunction node, Object data);
  public Object visit(ASTalias node, Object data);
  public Object visit(ASTcommand node, Object data);
  public Object visit(ASTcommandArg node, Object data);
  public Object visit(ASTDomainArg node, Object data);
  public Object visit(ASTRecipient1 node, Object data);
  public Object visit(ASTPluginEx1 node, Object data);
  public Object visit(ASTAddress1 node, Object data);
  public Object visit(ASTParas1 node, Object data);
  public Object visit(ASTdomain1 node, Object data);
  public ArrayList<String> getparalist();
  public ArrayList<String> getValues();
  public ArrayList<String> getqueries();
  public ArrayList<String> getoperator();
  public ArrayList<String> getKeys();
  public String getDomain();
  public String getFunction();
  public boolean getFunctionFlg();
  public boolean getNormalFlg();
  public String getCommand();
  public ArrayList<String> getCommandArgs();
  public String getTarget();
  public String getSubdomain();
  public String getRecipient();
  public ArrayList<String> getPara();
  public ArrayList<Integer> getParanum();
  public String getQuery();
  public String getfulldomain();
}
/* JavaCC - OriginalChecksum=8b37f90dfddff7d20836e96f15f3b484 (do not edit this line) */