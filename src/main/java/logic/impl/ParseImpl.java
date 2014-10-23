/**
 * 
 */
package logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import logic.Parse;
import logic.ParserSelector;
import logic.parse.Parsable;
import logic.parse.User;
import logic.parse.User1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.Message;

/**
 * <p>
 * {@link Parse}の実装
 * </p>
 */
public class ParseImpl implements Parse {

	private String recipient;
	private String domain;
	private String subdomain;
	private String propfile;

	/** subdomain files */
	private ResourceBundle domBundle;
	private HashMap<String, ResourceBundle> domBundles;
	/** subdomain, domain,propfiles's map */
	private ArrayList<HashMap<String, String>> domainsMaps;

	/** logger */
	private static final Logger log = LoggerFactory.getLogger(ParseImpl.class);

	public ParseImpl(Message oMsg) {
		recipient = oMsg.getRecipient();
		domBundles = OpenPropFileImpl.getInstance().getDomBundles();
		domain = new String();
		subdomain = new String();
		propfile = new String();
		domainsMaps = OpenPropFileImpl.getInstance().getDomainsMaps();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Parsable getParser() {

		ParserSelector ps = new ParserSelectorImpl();
		Parsable parser = ps.select(recipient);
		this.setField(parser);
		parser.parseStart(recipient, domBundle, domain);
		return parser;
	}
	
	private void setField(Parsable _parser){
		if(_parser instanceof User) {
			domain = this.getDomainOfFunctional(recipient);
			subdomain = this.getSubdomainOfFunctional(recipient);
		} else if (_parser instanceof User1) {
			domain = this.getDomainOfNatural(recipient);
			subdomain = this.getSubdomainOfNatural(recipient);
		}
		propfile = this.getPropfileName(domain, subdomain, domainsMaps);
		domBundle = this.searchDomBundle(domain, subdomain, domBundles);
		
		return;
	}

	/**
	 * 関数形式の宛先のドメイン名を得る.(ex)team{rmx}@testk.keio.com=>keio.com
	 *
	 * @oaram recipient 関数形式の宛先
	 * @return ドメイン名
	 */
	private String getDomainOfFunctional(String recipient) {
		// 1. @以降を切り取る
		int num_at = recipient.indexOf("@");
		String fulldomain = recipient.substring(num_at + 1);

		// 2. 最初の"."より後を切り取る
		int num_dot = fulldomain.indexOf(".");
		String domain = fulldomain.substring(num_dot + 1);

		return domain;
	}

	/**
	 * 関数形式の宛先のサブドメイン名を得る.(ex)team{rmx}@testk.keio.com=>testk
	 *
	 * @oaram recipient 関数形式の宛先
	 * @return サブドメイン名
	 * */
	private String getSubdomainOfFunctional(String recipient) {
		// 1. @以降を切り取る
		int num_at = recipient.indexOf("@");
		String fulldomain = recipient.substring(num_at + 1);

		// 2. 最初の"."より前を切り取る
		int num_dot = fulldomain.indexOf(".");
		String subdomain = fulldomain.substring(0, num_dot);

		return subdomain;
	}
	
	/**
	 * ドメイン名とサブドメイン名を参考にして、HashMapsの中からファイル名を返す.
	 * @param domain ドメイン名
	 * @param subdomain サブドメイン名
	 * @param domainsMaps env.propertiesから得られたドメイン名などの集合
	 * @return ファイル名 
	 * */
	private String getPropfileName(String domain, String subdomain, ArrayList<HashMap<String, String>> domainsMaps) {
		for(int i=0;i<domainsMaps.size();i++) {
			HashMap<String, String> domainsMap = domainsMaps.get(i);
			if(domainsMap.get("domain").equalsIgnoreCase(domain) && domainsMap.get("subdomain").equalsIgnoreCase(subdomain)) 
				return domainsMap.get("propfile");		
		}
		log.error("it is not a suitable domain. ");
		return null;
	}

	/**
	 * ドメイン名とサブドメイン名を参考にして、domBundlesの中から適切なdomBundleを返す.
	 * @param domain ドメイン名
	 * @param subdomain サブドメイン名
	 * @param domBundles env.propertiesから得られたdomBundleの集合
	 * @return ドメイン名とサブドメイン名に対応したdomBundle
	 * */
	private ResourceBundle searchDomBundle(String domain, String subdomian, HashMap<String, ResourceBundle> domBundles){
		String fullDomain = subdomian+"."+domain;
		return domBundles.get(fullDomain);
	}
	
	/**
	 *関数形式の宛先のドメイン名を得る.(ex)rmx@team.testk.keio.com=>keio.com
	 *@oaram recipient 関数形式の宛先
	 *@return ドメイン名
	 * */
	private String getDomainOfNatural(String _recipient) {
		String recipient = new String();
		//2つの#が含まれていれば,その間の文字列を取り除く
		if(_recipient.contains("#")) {
			int sharp_last_num = _recipient.lastIndexOf("#");
			if (sharp_last_num>0)
				recipient = _recipient.substring(sharp_last_num+1);
			else 
				return null;
		}else 
			recipient = _recipient;
			
		//ルールの数
		int rule_num;
		//宛先に#が存在しないとき
			
		int num_at = recipient.indexOf("@");
		//@より前
		String account = recipient.substring(0, num_at);
		//@より後
		String fulldomain = recipient.substring(num_at+1);
				
		//accountに"."があれば分割し、無ければnullを返す.
		if(account.contains(".")) {
			String[] accounts = account.split("\\.");
			rule_num = accounts.length;
		}
		//accountに"."が無ければルールは1つ
		else
			rule_num = 1;
				
		//ドメインの中の"."の数はルール数より大きくなければいけない.そうでなければnullを返す
		int dot_num = (fulldomain.split("\\.").length)-1;//ドメインの中の"."の数
		if (dot_num>rule_num) {
			//ex)ルール数が2なら3つめの"."以降がドメイン名である.
			//rule1.rule2.subdomain.domain
			int start_num = searchChr(fulldomain, ".", rule_num+1);
			//start_numが-1でないとき
			if (start_num>0) {
				String domain = fulldomain.substring(start_num+1);
				return domain;
			}
			//start_numが-1のとき
			else 
				return null;
		}
		//ドメインに含まれる"."の数がルール数以下のとき
		else 
			return null; 
	}
	
	/**
	 *関数形式の宛先のサブドメイン名を得る.(ex)rmx@team.testk.keio.com=>testk
	 *@oaram recipient 関数形式の宛先
	 *@return サブドメイン名
	 * */
	private String getSubdomainOfNatural(String _recipient) {
		String recipient = new String();
		//2つの#が含まれていれば,その間の文字列を取り除く
		if(_recipient.contains("#")) {
			int sharp_last_num = _recipient.lastIndexOf("#");
			if (sharp_last_num>0)
				recipient = _recipient.substring(sharp_last_num+1);
			else 
				return null;
		} else 
			recipient = _recipient;
		
		//ルールの数
		int rule_num;
		
		int num_at = recipient.indexOf("@");
		//@より前
		String account = recipient.substring(0, num_at);
		//@より後
		String fulldomain = recipient.substring(num_at+1);
			
		//accountに"."があれば分割し、無ければnullを返す.
		if(account.contains(".")) {
			String[] accounts = account.split("\\.");
			rule_num = accounts.length;
		} else
			rule_num = 1;
			
		//ドメインの中の"."の数はルール数より大きくなければいけない.そうでなければnullを返す
		int dot_num = (fulldomain.split("\\.").length)-1;//ドメインの中の"."の数
		if (dot_num>rule_num) {
			//ex)ルール数が2なら3つめの"."以降がドメイン名である.
			//rule1.rule2.subdomain.domain
			int start_num = searchChr(fulldomain, ".", rule_num);
			int end_num = searchChr(fulldomain, ".", rule_num+1);
			//start_numが-1でないとき
			if(start_num>0 && end_num>start_num) {
				String subdomain = fulldomain.substring(start_num+1,end_num);
				return subdomain;
			}
			//start_numが-1のとき
			else
				return null;
		}
		//ドメインに含まれる"."の数がルール数以下のとき
		else
			return null; 
	}
	
	/**
	 * str1の中のn番目のst2の位置を返す.
	 * @param str1 文字列
	 * @param str2 文字列
	 * @paramn n n番目
	 * @return n番目のstr2の位置
	 * */
	private int searchChr(String str1, String str2, int n) {
		int return_num = -1;
		for (int i=0;i<n;i++) {
			return_num = str1.indexOf(str2, return_num+1);
		}
		return return_num;
	}

	@Override
	public ResourceBundle getDomBundle() {
		return domBundle;
	}
}
