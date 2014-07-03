package logic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import dao.PropfileDao;

public class PropfileService {
	/**ユーティリティクラスなのでコンストラクタは呼び出せないように*/
	private PropfileService() {}
	/**
	 * env.propertiesを読み込み、使えるドメインごとにサブドメインとプロパティファイル名をHashMapに格納して、
	 * それらをArrayListに格納する
	 * */
	public static ArrayList<HashMap<String, String>> setDomainsAndPropName(
			ResourceBundle envBundle){
		
		ArrayList<HashMap<String, String>> setOfDomconfObj = new ArrayList<HashMap<String,String>>();
		/*
		 *env.propertiesからキーdomainから値を取得
		 *(ex)domainNames->rmxdev:rmxdev.db.ics.keio.ac.jp,rmx-keio:rmx-keio.netなど
		 */
		String domainNames = envBundle.getString("domain");
		/*
		 *domainNamesを","で分割する
		 *(ex)domainName[0]->rmxdev:rmxdev.db.ics.keio.ac.jp,domainName[1]->rmx-keio:rmx-keio.net
		 */
		String[] domainName = domainNames.split(",");
		
		//firstDomainName->(ex)rmxdev
		String[] firstDomainName = new String[domainName.length];
		//lastDomainName->(ex)rmxdev.db.ics.keio.ac.jp
		String[] lastDomainName = new String[domainName.length];
		
		
		for(int i=0;i<domainName.length;i++) {
			/*domainName[i]を":"で分割する
			 *(ex)firstDomainName[0]->rmxdev,firstDomainName[1]->rmx-keio 
			 *lastDomainName[0]->rmxdev.db.ics.keio.ac.jp,lastDomainName[1]->rmx-keio.net
			 */
			String[] tmp = domainName[i].split(":");
			firstDomainName[i] = tmp[0];
			lastDomainName[i] = tmp[1];
			
			//
			HashMap<String, String> hashOfFileAndDomain = new HashMap<String, String>();
			
			//env.propertiesファイル内にfirstDomain[i]のキーが存在すれば
			if(envBundle.containsKey(firstDomainName[i])) {
				//(ex)valuesOfFirstDomainKey->testk,testb
				String valuesOfFirstDomainKey = envBundle.getString(firstDomainName[i]);
				//(ex)personalDomaninName[0]->testk,personalDomaninName[1]->testb
				String[] personalDomainName = valuesOfFirstDomainKey.split(",");
				
				
				for(int j=0;j<personalDomainName.length;j++) {
					/*
					 * (ex)"propFileName"->testk_rmxdev,
					 * "fullDomainName"->testk.rmxdev.db.ics.keio.ac.jp
					 * "domainName"->rmxdev.db.ics.keio.ac.jp
					 */
					hashOfFileAndDomain = new HashMap<String, String>();
					//
					hashOfFileAndDomain.put("propFileName",
							firstDomainName[i]+"_"+personalDomainName[j]);
					hashOfFileAndDomain.put("fullDomainName",
							personalDomainName[j]+"."+lastDomainName[i]);
					hashOfFileAndDomain.put("domainName", lastDomainName[i]);
					hashOfFileAndDomain.put("subdomainName", personalDomainName[j]);
					
					//
					setOfDomconfObj.add(hashOfFileAndDomain);
				}
			}
		}
		return setOfDomconfObj;
	}
	
	/**
	 * strに対してbeginStrより後ろの文字列を切り取る
	 * */
	public static String strExtract(String str, String beginStr) {
		int start = str.indexOf(beginStr);
		return str.substring(start+1);
	}
	
	/**
	 * 送られてきたメールのドメインとenv.propertiesから得られたドメインの候補を比べて、候補が存在すれば
	 * そのプロパティファイル名を返す
	 * */
	public static String checkPropFileExistence(String fullDomain,
			ArrayList<HashMap<String, String>> setOfDomconfObj) {
		for(int i=0;i<setOfDomconfObj.size();i++) {
			if(setOfDomconfObj.get(i).get("fullDomainName").equalsIgnoreCase(fullDomain)) 
				return setOfDomconfObj.get(i).get("propFileName");
		}
		return null;
	}
}
