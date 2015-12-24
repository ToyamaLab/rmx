/**
 * 
 */
package logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import logic.Parse;
import logic.parse.Parsable;
import logic.parse.ParseAddress;

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
		this.setField();
		Parsable parser = new ParseAddress();
		parser.parseStart(recipient, domBundle, domain);
		return parser;
	}
	
	private void setField(){
		String target = recipient.substring(recipient.indexOf("#", 1) + 1);
		
		if (target.contains("{") && target.contains("}")) {
			domain = this.getDomainOfFunctional(recipient);
			subdomain = this.getSubdomainOfFunctional(recipient);
		} else {
			domain = this.getDomainOfNatural(recipient);
			subdomain = this.getSubdomainOfNatural(recipient);
		}
		propfile = this.getPropfileName(domain, subdomain, domainsMaps);
		if (propfile.equalsIgnoreCase("default") && !subdomain.equalsIgnoreCase("default")) {
			domain = subdomain + "." + domain;
			subdomain = "default";
		}
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
		int atIndex = recipient.indexOf("@");
		String fullDomain = recipient.substring(atIndex + 1);

		// 2. 最初の"."より後を切り取る
		int firstDotIndex = fullDomain.indexOf(".");
		String domain = fullDomain.substring(firstDotIndex + 1);

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
		int atIndex = recipient.indexOf("@");
		String fullDomain = recipient.substring(atIndex + 1);

		// 2. 最初の"."より前を切り取る
		int firstDotIndex = fullDomain.indexOf(".");
		String subdomain = fullDomain.substring(0, firstDotIndex);

		return subdomain;
	}
	
	/**
	 *自然形式の宛先のドメイン名を得る.(ex)rmx@team.testk.keio.com=>keio.com
	 *@oaram recipient 自然形式の宛先
	 *@return ドメイン名
	 * */
	private String getDomainOfNatural(String _recipient) {
		String recipient = new String();
		//2つの#が含まれていれば,その間の文字列を取り除く
		int lastSharpIndex = _recipient.lastIndexOf("#");
		if (lastSharpIndex > 0)
			recipient = _recipient.substring(lastSharpIndex + 1);
		else if (lastSharpIndex < 0)
			recipient = _recipient;
		else
			return null;
		
		//ルールの数
		int ruleNum;
		
		int atIndex = recipient.indexOf("@");
		//@より前
		String account = recipient.substring(0, atIndex);
		//@より後
		String fullDomain = recipient.substring(atIndex + 1);
				
		//accountに"."があれば分割
		if (account.contains(".")) {
			String[] accounts = account.split("\\.");
			ruleNum = accounts.length;
		}
		//accountが空でなく"."が無ければルールは1つ
		else if (!account.isEmpty())
			ruleNum = 1;
		//accountが空の場合はルールは0
		else
			ruleNum = 0;
				
		//ドメインの中の"."の数はルール数より大きくなければいけない.そうでなければnullを返す
		int dotNum = fullDomain.split("\\.").length - 1;//ドメインの中の"."の数
		if (dotNum > ruleNum) {
			//ex)ルール数が2なら3つめの"."以降がドメイン名である.
			//rule1.rule2.subdomain.domain
			int startIndex = searchChr(fullDomain, ".", ruleNum + 1);
			if (startIndex > 0) {
				String domain = fullDomain.substring(startIndex + 1);
				return domain;
			} else 
				return null;
		} else 
			return null; 
	}
	
	/**
	 *自然形式の宛先のサブドメイン名を得る.(ex)rmx@team.testk.keio.com=>testk
	 *@oaram recipient 自然形式の宛先
	 *@return サブドメイン名
	 * */
	private String getSubdomainOfNatural(String _recipient) {
		String recipient = new String();
		//2つの#が含まれていれば,その間の文字列を取り除く
		int lastSharpIndex = _recipient.lastIndexOf("#");
		if (lastSharpIndex > 0)
			recipient = _recipient.substring(lastSharpIndex + 1);
		else if (lastSharpIndex < 0)
			recipient = _recipient;
		else
			return null;
		
		//ルールの数
		int ruleNum;
		
		int atIndex = recipient.indexOf("@");
		//@より前
		String account = recipient.substring(0, atIndex);
		//@より後
		String fullDomain = recipient.substring(atIndex + 1);
			
		//accountに"."があれば分割
		if (account.contains(".")) {
			String[] accounts = account.split("\\.");
			ruleNum = accounts.length;
		} else if (!account.isEmpty())
			ruleNum = 1;
		else
			ruleNum = 0;
			
		//ドメインの中の"."の数はルール数より大きくなければいけない.そうでなければnullを返す
		int dotNum = (fullDomain.split("\\.").length) - 1;	//ドメインの中の"."の数
		if (dotNum > ruleNum) {
			//ex)ルール数が2なら3つめの"."以降がドメイン名である.
			//rule1.rule2.subdomain.domain
			int startIndex = searchChr(fullDomain, ".", ruleNum) + 1;
			int endIndex = searchChr(fullDomain, ".", ruleNum + 1);

			if ((ruleNum == 0 || startIndex > 0) && endIndex > startIndex) {
				String subdomain = fullDomain.substring(startIndex, endIndex);
				return subdomain;
			} else
				return null;
		} else
			return null; 
	}
	
	/**
	 * ドメイン名とサブドメイン名を参考にして、HashMapsの中からファイル名を返す.
	 * @param domain ドメイン名
	 * @param subdomain サブドメイン名
	 * @param domainsMaps env.propertiesから得られたドメイン名などの集合
	 * @return ファイル名 
	 * */
	private String getPropfileName(String domain, String subdomain, ArrayList<HashMap<String, String>> domainsMaps) {
		boolean applyDefault = false;
		for (int i = 0; i < domainsMaps.size(); i++) {
			HashMap<String, String> domainsMap = domainsMaps.get(i);
			if (domainsMap.get("domain").equalsIgnoreCase(domain) && domainsMap.get("subdomain").equalsIgnoreCase(subdomain)) 
				return domainsMap.get("propfile");
			else if (!applyDefault && (domainsMap.get("domain").equalsIgnoreCase(subdomain + "." + domain)))
				applyDefault = true;
		}
		if (applyDefault) {
			log.warn("default.properties will be applied.");
			return "default";
		} else {
			log.error("domain is not a suitable.");
			return null;
		}
	}

	/**
	 * ドメイン名とサブドメイン名を参考にして、domBundlesの中から適切なdomBundleを返す.
	 * @param domain ドメイン名
	 * @param subdomain サブドメイン名
	 * @param domBundles env.propertiesから得られたdomBundleの集合
	 * @return ドメイン名とサブドメイン名に対応したdomBundle
	 * */
	private ResourceBundle searchDomBundle(String domain, String subdomian, HashMap<String, ResourceBundle> domBundles) {
		ResourceBundle tmpBundle;
		if (subdomain.equalsIgnoreCase("default"))
			tmpBundle = domBundles.get("default");
		else
			tmpBundle = domBundles.get(subdomain + "." + domain);
		
		return tmpBundle;
	}
	
	/**
	 * str1の中のn番目のst2の位置を返す.
	 * @param str1 文字列
	 * @param str2 文字列
	 * @paramn n n番目
	 * @return n番目のstr2の位置
	 * */
	private int searchChr(String str1, String str2, int n) {
		int index = -1;
		for (int i = 0; i < n; i++)
			index = str1.indexOf(str2, index + 1);
		return index;
	}

	@Override
	public ResourceBundle getDomBundle() {
		return domBundle;
	}
}
