/* Generated By:JJTree: Do not edit this line. ASTDomain.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package logic.parse.SOP;

public
class ASTDomain extends SimpleNode {
  public ASTDomain(int id) {
    super(id);
  }

  public ASTDomain(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=507f37913677f3e5a0d3c13aa14b1f61 (do not edit this line) */
