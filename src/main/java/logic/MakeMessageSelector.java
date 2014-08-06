package logic;

import java.util.ResourceBundle;

import logic.parse.Parsable;

/**
 * パーザから適切な{@link MakeMessage}の実装クラスを返すファクトリインターフェース
 */
public interface MakeMessageSelector {
	
	/**
	 * Parsableに含まれるフラグから、MakeMessageを実装したクラスを返す。
	 * @param parser SOPのパーザ
	 * @param domBundle TODO
	 * @return MakeMessageを実装したクラス
	 */
	public MakeMessage select(Parsable parser, ResourceBundle domBundle);

}
