package logic.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import logic.SmtpListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropfileUtils {
	private static final Logger log = LoggerFactory.getLogger(SmtpListener.class);
	/** コンストラクタは呼び出せないようにするためにprivateにする*/
	private PropfileUtils() {}
	
	/*PropfileService*/
	
	/**
	 * env.propertiesを読み込み、domconfをキー名としてそのpropertiesを提供するマップを格納し、返す
	 * @param envBundle env.properties
	 * @return 使用できるdomconfBundleが格納
	 * */
	public static HashMap<String, ResourceBundle> getBundles(ResourceBundle envBundle){
		// 1. domconf.properties格納するためのHashMap
		HashMap<String, ResourceBundle> domBundles = new HashMap<String, ResourceBundle>();
		
		// 2. env.propertiesにdomainが無ければnullを返す
		if(!envBundle.containsKey("domain")) 
			{log.error("not exists domain-key at env.properties"); return null;}
		
		// 3. env.propertiesのdomainから値を取得
		String domainNames = envBundle.getString("domain");
		
		// 4. domain-valueに記述されていない.or ","から始まるときはnulllを返す.
		if(domainNames.length()==0 || domainNames.startsWith(","))
			{log.error("not exists domain-value or start with ',' at env.properties"); return null;}
		
		// 5. domainNamesを","で分割
		String[] domainName = domainNames.split(",");
		
		// 6. domain-keyに対するvalueが無ければnullを返す
		if(domainName[0].length()==0)
			{log.error("not exists domain-value at env.properties"); return null;}
		
		// 7. default.propertiesを追加
		domBundles.put("default", ResourceBundle.getBundle("default"));
		
		// 8. domainNameの数だけループ
		for(int i = 0; i < domainName.length; i++){
			// .domainName[]に":"無ければnullを返す.文字列の最初or最後に":"があればnullを返す.
			if(!domainName[i].contains(":") && !domainName[i].startsWith(":") && !domainName[i].endsWith(":"))
				{log.error("not exists ':' or incorrect location at env.properties"); return null;}
			
			// 10. domainNameを":"で分割
			String[] propertiesAndAddress = domainName[i].split(":");
			String formerDomainName = propertiesAndAddress[0];
			String latterDomainName = propertiesAndAddress[1];
			
			// 11. formerDomainNameがkeyとして存在しないときnullを返す.
			if(!envBundle.containsKey(formerDomainName))
				{log.warn("not exists "+formerDomainName+"-key at env.properties"); continue;}
			
			// 12. env.propertiesのformerDomainNameから値を取得
			String properties = envBundle.getString(formerDomainName);
			
			// 13. propertiesが","で始まるか終わるときnullを返す
			if(properties.startsWith(",") || properties.endsWith(","))
				{log.error("incorrect location ',' "+formerDomainName+"-key at env.properties"); return null;}
			
			// 14. propertiesを","で分割
			String[] property = properties.split(",");
			
			// 15. propertyの数だけループ
			for (int j = 0; j < property.length; j++){
				// 16. formerDomainName _propertyがdomBundlesにあるかチェック
				if (isExist(formerDomainName + "_" + property[j]))
					// 17. domBundlesに格納."fulldomain"->"propfile"
					domBundles.put(property[j] + "." + latterDomainName,
							ResourceBundle.getBundle(formerDomainName + "_" + property[j]));
			}
		}
		return domBundles;
	}
	
	/**
	 * key.propertiesが存在すればtrue,無ければfalseを返す
	 * @param key domconf名 ex.rmxdev_testk,keio_testm
	 * */
	private static boolean isExist(String key) {
		try {
			ResourceBundle.getBundle(key);
		} catch (MissingResourceException e){
			return false;
		}
		return true;
	}
	
	/**
	 * env.propertiesを読み込み、使えるドメインとサブドメインごとにとプロパティファイル名などをHashMapに格納して、それらをArrayListに格納する
	 * @param envBundle env.properties
	 * @return 使用できるドメイン名など
	 * */
	public static ArrayList<HashMap<String, String>> getDomainsObj(ResourceBundle envBundle){
		// 1. ドメイン名、サブドメイン名、フルドメイン名、ファイル名が格納されるHashMapの集合
		ArrayList<HashMap<String, String>> domainMaps = new ArrayList<HashMap<String,String>>();
		
		// 2. env.propertiesにdomainが無ければnullを返す
		if(!envBundle.containsKey("domain")) 
			{log.error("not exists domain-key at env.properties"); return null;}
		
		// 3. env.propertiesのdomainから値を取得
		String domainName = envBundle.getString("domain");
		
		// 4. domain-valueに記述されていない.or ","から始まるときはnulllを返す.
		if(domainName.length()==0 || domainName.startsWith(","))
			{log.error("not exists domain-value or start with ',' at env.properties"); return null;}
		
		// 5. domainNamesを","で分割
		String[] domainNames = domainName.split(",");
		
		// 6. domain-keyに対するvalueが無ければnullを返す
		if(domainNames[0].length()==0)
			{log.error("not exists domain-value at env.properties"); return null;}
		
		// 7. ドメインごとにサブドメインをチェックし、サブドメインごとにHashMapを格納する
		for(int i=0;i<domainNames.length;i++) {
			// 8.domainName[]に":"無ければnullを返す.文字列の最初or最後に":"があればnullを返す.
			if(!domainNames[i].contains(":") && !domainNames[i].startsWith(":") && !domainNames[i].endsWith(":"))
				{log.error("not exists ':' or incorrect location at env.properties"); return null;}
			
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
			
			// 11. formerDomainNameがkeyとして存在しないときnullを返す.
			if(!envBundle.containsKey(firstDomainName))
				{log.warn("not exists "+firstDomainName+"-key at env.properties"); continue;}
			
			String property = envBundle.getString(firstDomainName);
			// 12. propertiesが","で始まるか終わるときnullを返す
			if(property.startsWith(",") || property.endsWith(","))
				{log.error("incorrect location ',' "+firstDomainName+"-key at env.properties"); return null;}
			
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
}
