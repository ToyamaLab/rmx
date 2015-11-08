/* Generated By:JavaCC: Do not edit this line. ParserVisitor.java Version 5.0 */
package logic.parse.SOP;

public interface ParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTRecipient node, Object data);
  public Object visit(ASTPluginExp node, Object data);
  public Object visit(ASTPlugin node, Object data);
  public Object visit(ASTAddress node, Object data);
  public Object visit(ASTDomain node, Object data);
  public Object visit(ASTException node, Object data);
  public Object visit(ASTUnion node, Object data);
  public Object visit(ASTIntersection node, Object data);
  public Object visit(ASTExp node, Object data);
  public Object visit(ASTParaList node, Object data);
  public Object visit(ASTPolimorPara node, Object data);
  public Object visit(ASTfunction node, Object data);
  public Object visit(ASTcommand node, Object data);
  public Object visit(ASTcommandArg node, Object data);
  public Object visit(ASTdomainArg node, Object data);
  public Object visit(ASTrule node, Object data);
  public Object visit(ASTvalue node, Object data);
  public Object visit(ASTN_Recipient node, Object data);
  public Object visit(ASTN_PluginExp node, Object data);
  public Object visit(ASTN_Address node, Object data);
  public Object visit(ASTN_Paras node, Object data);
  public Object visit(ASTN_ParaList node, Object data);
  public Object visit(ASTN_PolimorPara node, Object data);
}
/* JavaCC - OriginalChecksum=3d96219ff750154c1ebf2f690130fe20 (do not edit this line) */
