package logic.impl;

import java.util.ResourceBundle;

import logic.MakeMessage;
import logic.MakeMessageSelector;
import logic.parse.Parsable;

/**
 * {@link MakeMessageSelector}の実装
 */
public class MakeMessageSelectorImpl implements MakeMessageSelector {
	
	boolean delivery;
	boolean generate;
	boolean function;

	public MakeMessageSelectorImpl() {
		delivery = false;
		generate = false;
		function = false;
	}
	
	/**
	 * @inheritDoc
	 * 返すのはDelivery, Generate, Function(いずれもMakeMessageを実装)
	 */
	@Override
	public MakeMessage select(Parsable parser, ResourceBundle domBundle) {
		
		if (parser.getDeliveryFlg())
			delivery = true;
		if (parser.getGenerateFlg())
			generate = true;
		if (parser.getFunctionFlg())
			function = true;
			
		if (generate && function) {
			System.out.println("# Error: generate rule appears in function target!");
			return new MakeError("semantic");
		
		} else if (function) {
			System.out.println("use function");
			return new MakeFunction(parser, domBundle);
			
		} else if (delivery && generate) {
			System.out.println("both delivery and generate rule");
			return new MakeMixture(parser);
			
		} else if (delivery) {
			System.out.println("only delivery rule");
			return new MakeDelivery(parser);
			
		} else if (generate) {
			System.out.println("only generate rule");
			return new MakeGenerate(parser);
			
		} else {
			System.out.println("# Error: syntax error!");
			return new MakeError("syntax");
		}
		
	}
	
}
