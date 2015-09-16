/**
 * 
 */
package logic.parse;

import java.util.ResourceBundle;

import logic.impl.ParseImpl;
import logic.parse.SOP.ParserVisitor;

/**
 * User, User1のstartメソッドのインターフェース。
 * これによって、{@link ParseImpl}はUserかUser1かを判断することなく、パーズできるようになる。
 * {@link parserVisitor}を継承しているのでこれを実装すればおｋ
 */
public interface Parsable extends ParserVisitor {
	
	/**
	 * パーズをスタートする。
	 * これらのパラメータから、実装オブジェクトが値を持つように状態を変更する。
	 * @param recipient メールアドレス
	 * @param dom Properties(domconf)ファイルのリソースバンドル
	 * @param _domname ドメイン
	 */
	public void parseStart(String recipient, ResourceBundle dom, String _domname);

}
