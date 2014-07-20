package logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import logic.OpenPropFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.PropFileDao;
import dao.impl.PropFileDaoImpl;

/**
 * PropFileの実装<br/>
 * env.propertiesのチェックを行う.<br/>
 * キー(domain)から値を取り出す.(domain = ドメイン名:ホスト名)<br/>
 * 次にキー(ドメイン名)から値を取り出し、propertiesファイルを決定する.<br/>
 * (ex)env.propertiesファイルのキー(domain)の値がrmxdev:rmxdev.keio.jpのとき、キーとして
 * rmxdevが必要となる. <br/>
 * その値testkを取得したとき、用意するpropertiesファイルはtestk_rmxdevである.
 */
public final class OpenPropFileImpl implements OpenPropFile {
	// メンバ変数
	private final String ENVPROPFILENAME = "env";
	private ArrayList<HashMap<String, String>> domainsMaps;
	private ResourceBundle envBundle;
	private HashMap<String, ResourceBundle> domBundles;
	private PropFileDao pfd;
	private static OpenPropFileImpl pfs = new OpenPropFileImpl();

	/** ログ出力 */
	private static final Logger log = LoggerFactory.getLogger(OpenPropFileImpl.class);

	/**
	 * コンストラクタを<code>private</にすることで>シングルトンを保証
	 */
	private OpenPropFileImpl() {
		domainsMaps = new ArrayList<HashMap<String, String>>();
		domBundles = new HashMap<String, ResourceBundle>();
		pfd = new PropFileDaoImpl();
	}
	
	public static OpenPropFileImpl getInstance() {
		return pfs;
	}

	/**
	 * domconf.propertiesが格納されているdomBundlesを得る
	 * */
	@Override
	public void open() {
		envBundle = pfd.read(ENVPROPFILENAME);
		domBundles = this.setBundles();
		domainsMaps = this.setDomainsObj();
		return;
	}
	
	/**
	 * env.propertiesを読み込み、domconfをキー名としてそのpropertiesを提供するマップを格納するヘルパーメソッド
	 * @return 使用できるdomconfBundleが格納
	 */
	private HashMap<String, ResourceBundle> setBundles(){
		// 1. env.propertiesのdomainをチェック
		if (!envBundle.containsKey("domain")) {
			log.error("not exists domain-key at env.properties");
			return null;
		}		
		String domainNames = envBundle.getString("domain");
		
		// 2. domain-valueに記述されていない or ","から始まるときはnullを返す.
		if (domainNames.length()==0 || domainNames.startsWith(",")) {
			log.error("not exists domain-value or start with ',' at env.properties");
			return null;
		}	
		
		// 3. domainNamesを","で分割
		String[] domainName = domainNames.split(",");
		
		// 4. domain-keyに対するvalueをチェック
		if (domainName[0].length() == 0) {
			log.error("not exists domain-value at env.properties");
			return null;
		}
		
		// 5. domainNameの数だけループ
		for (int i = 0; i < domainName.length; i++){
			// .domainName[]に":"無ければnullを返す.文字列の最初or最後に":"があればnullを返す.
			if (!domainName[i].contains(":") || domainName[i].startsWith(":") || domainName[i].endsWith(":")) {
				log.error("not exists ':' or incorrect location at env.properties");
				return null;
			}
			
			// 6. domainNameを":"で分割
			String[] propertiesAndAddress = domainName[i].split(":");
			String formerDomainName = propertiesAndAddress[0];
			String latterDomainName = propertiesAndAddress[1];
			
			// 7. formerDomainNameがkeyとして存在しないときは格納しない。
			if (!envBundle.containsKey(formerDomainName)) {
				log.warn("not exists "+formerDomainName+"-key at env.properties");
				continue;
			}
			String properties = envBundle.getString(formerDomainName);
			
			// 8. propertiesが","で始まるか終わるときnullを返す
			if (properties.startsWith(",") || properties.endsWith(",")) {
				log.error("incorrect location ',' "+formerDomainName+"-key at env.properties");
				return null;
			}
			
			// 9. propertiesを","で分割
			String[] property = properties.split(",");
			
			// 10. propertyの数だけループ
			for (int j = 0; j < property.length; j++){
				// 11. formerDomainName_propertyがdomBundlesにあるかチェック
				if (isExist(formerDomainName + "_" + property[j]))
					// 12. domBundlesに格納."fulldomain"->"propfile"
					domBundles.put(property[j] + "." + latterDomainName, pfd.read(formerDomainName + "_" + property[j]));
			}
		}
		// 13. default.propertiesを追加
		try {
			domBundles.put("default", ResourceBundle.getBundle("default"));
		} catch (MissingResourceException e) {
			log.warn("not exists default-key at env.properties");
		}
		return domBundles;
	}
	

	/**
	 * env.propertiesを読み込み、使えるドメインとサブドメインごとにとプロパティファイル名などをHashMapに格納して、それらをArrayListに格納するヘルパーメソッド
	 * @return 使用できるドメイン名など
	 * */
	private ArrayList<HashMap<String, String>> setDomainsObj(){
		// 1. ドメイン名、サブドメイン名、フルドメイン名、ファイル名が格納されるHashMapの集合
		ArrayList<HashMap<String, String>> domainMaps = new ArrayList<HashMap<String,String>>();
		
		// 2. env.propertiesにdomainが無ければnullを返す
		if(!envBundle.containsKey("domain")) {
			log.error("not exists domain-key at env.properties");
			return null;
		}
		
		// 3. env.propertiesのdomainから値を取得
		String domainName = envBundle.getString("domain");
		
		// 4. domain-valueに記述されていない. or ","から始まるときはnullを返す.
		if (domainName.length()==0 || domainName.startsWith(",")) {
			log.error("not exists domain-value or start with ',' at env.properties");
			return null;
		}
		
		// 5. domainNamesを","で分割
		String[] domainNames = domainName.split(",");
		
		// 6. domain-keyに対するvalueが無ければnullを返す
		if (domainNames[0].length()==0) {
			log.error("not exists domain-value at env.properties");
			return null;
		}
		
		// 7. ドメインごとにサブドメインをチェックし、サブドメインごとにHashMapを格納する
		for (int i=0;i<domainNames.length;i++) {
			// 8.domainName[]に":"無ければnullを返す.文字列の最初or最後に":"があればnullを返す.
			if (!domainNames[i].contains(":") && !domainNames[i].startsWith(":") && !domainNames[i].endsWith(":")) {
				log.error("not exists ':' or incorrect location at env.properties");
				return null;
			}
			
			// 9. domainNameを":"で分割
			String[] propertiesAndAddress = domainNames[i].split(":");
			String firstDomainName = propertiesAndAddress[0];
			String lastDomainName = propertiesAndAddress[1];
			
			// 10. defaultも追加
			HashMap<String, String> defaultMap = new HashMap<String, String>();
			defaultMap.put("domain",lastDomainName );
			defaultMap.put("subdomain", "default");
			defaultMap.put("fulldomain",lastDomainName);
			defaultMap.put("propfile", "default");
			domainMaps.add(defaultMap);
			
			// 11. formerDomainNameがkeyとして存在しないとき、何もしない.
			if (!envBundle.containsKey(firstDomainName)) {
				log.warn("not exists "+firstDomainName+"-key at env.properties");
				continue;
			}
			
			String property = envBundle.getString(firstDomainName);
			// 12. propertiesが","で始まるか終わるときnullを返す
			if(property.startsWith(",") || property.endsWith(",")) {
				log.error("incorrect location ',' "+firstDomainName+"-key at env.properties");
				return null;
			}
			
			String[] properties = property.split(",");
			for(int j=0;j<properties.length;j++) {
				// 13. propertiesごとにHasMapを作成
				HashMap<String, String> domainsMap = new HashMap<String, String>();
				
				// 14. domain,subdomain,fulldomain,propfileを格納
				domainsMap.put("domain",lastDomainName );
				domainsMap.put("subdomain", properties[j]);
				domainsMap.put("fulldomain",properties[j]+"."+lastDomainName );
				domainsMap.put("propfile", firstDomainName+"_"+properties[j]);
				domainMaps.add(domainsMap);
			}
		}
		return domainMaps;
	}

	/**
	 * key.propertiesが存在すればtrue,無ければfalseを返す
	 * @param key domconf名 ex.rmxdev_testk,keio_testm
	 * */
	private boolean isExist(String key) {
		try {
			pfd.read(key);
		} catch (MissingResourceException e){
			return false;
		}
		return true;
	}
	
	public ResourceBundle getEnvBundle() {
		return envBundle;
	}
	
	public ArrayList<HashMap<String, String>> getDomainsMaps() {
		return domainsMaps;
	}

	public HashMap<String, ResourceBundle> getDomBundles() {
		return domBundles;
	}
}
