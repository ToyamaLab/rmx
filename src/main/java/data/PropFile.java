package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import logic.utils.PropfileUtils;
import dao.PropfileDao;

/**
 * env.propertiesのチェックを行う.<br/>
 * キー(domain)から値を取り出す.(domain = ドメイン名:ホスト名)<br/>
 * 次にキー(ドメイン名)から値を取り出し、propertiesファイルを決定する.<br/>
 * (ex)env.propertiesファイルのキー(domain)の値がrmxdev:rmxdev.keio.jpのとき、キーとして
 * rmxdevが必要となる. <br/>
 * その値testkを取得したとき、用意するpropertiesファイルはtestk_rmxdevである.
 */
public final class PropFile {
	// メンバ変数
	private final String ENVPROPFILENAME = "env";
	private ArrayList<HashMap<String, String>> domainsMaps;
	private ResourceBundle envBundle;
	private HashMap<String, ResourceBundle> domBundles;
	private static PropFile pfs = new PropFile();

	// コンストラクタ
	private PropFile() {
		domainsMaps = new ArrayList<HashMap<String, String>>();
		envBundle = PropfileDao.readPropFile(ENVPROPFILENAME);
		domBundles = new HashMap<String, ResourceBundle>();
	}
	
	
	public static PropFile getInstance() {
		return pfs;
	}

	/**
	 * domconf.propertiesが格納されているdomBundlesを得る
	 * */
	public void init() {
		domBundles = PropfileUtils.getBundles(envBundle);
		return;
	}

	public ArrayList<HashMap<String, String>> getDomainsMaps() {
		domainsMaps = PropfileUtils.getDomainsObj(envBundle);
		return domainsMaps;
	}
	
//	public ResourceBundle searchDomBundle(String domain, String subdomian){
//		String fullDomain = subdomian+"."+domain;
//		return domBundles.get(fullDomain);
//	}
	

	// メールアドレスを引数として、env.propertiesファイルから適切な.propertiesファイル名を返す
	// ex) testk_rmxdevなど
//	public String getPropFileName(String _recipient) {
//		recipient = _recipient;
//		String propFileName = new String();
//
//		// 宛先の@までを切り取る ex)test@test.com -> test.com
//		int start = recipient.indexOf("@");
//		String fullDomain = recipient.substring(start + 1);
//
//		// env.propertiesファイルから適切なドメインかチェックして、正しければファイル名を返す
//		ArrayList<HashMap<String, String>> hmp = this
//				.getDomainsMaps();
//		for (int i = 0; i < hmp.size(); i++) {
//			if (hmp.get(i).get("fullDomainName").equalsIgnoreCase(fullDomain)) {
//				propFileName = hmp.get(i).get("propFileName");
//				break;
//			} else
//				propFileName = null;
//		}
//		return propFileName;
//	}

	public ResourceBundle getEnvBundle() {
		return envBundle;
	}

	public HashMap<String, ResourceBundle> getDomBundles() {
		return domBundles;
	}
}
