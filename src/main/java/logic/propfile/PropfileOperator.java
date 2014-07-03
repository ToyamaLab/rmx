package logic.propfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import logic.service.PropfileService;
import dao.PropfileDao;
/*
 * env.propertiesのチェックを行う.キー(domain)から値を取り出す.(domain = ドメイン名:ホスト名)
 * 次にキー(ドメイン名)から値を取り出し、propertiesファイルを決定する.
 * (ex)env.propertiesファイルのキー(domain)の値がrmxdev:rmxdev.keio.jpのとき、キーとして
 * rmxdevが必要となる.その値testkを取得したとき、用意するpropertiesファイルはtestk_rmxdevである.
 */
public class PropfileOperator {
	//メンバ変数
	private String envPropFileName = "env";
	
	//コンストラクタ
	public PropfileOperator() { }
	
	/**env.propertiesファイルを読み込み、fulldomain,domain,.propertiesファイルをHashMapに格納*/
	/**(ex)fullDomainName->testk.rmxdev.db.ics.keio.ac,
	 *domainName->rmxdev.db.ics.keio.ac,
	 *propFileName->rmxdev_testk 
	 * */
	public ArrayList<HashMap<String, String>> getDomainsMaps() {
		//
		ArrayList<HashMap<String, String>> setOfDomconfObj = new ArrayList<HashMap<String,String>>();
		
		ResourceBundle envBundle = PropfileDao.readPropFile(envPropFileName);
		
		setOfDomconfObj = PropfileService.setDomainsAndPropName(envBundle);
		
		return setOfDomconfObj;
	}
	
	//メールアドレスを引数として、env.propertiesファイルから適切な.propertiesファイル名を返す
	//ex) testk_rmxdevなど
	public String getPropFileName(String recipient) {
		
		//宛先の@までを切り取る ex)test@test.com -> test.com
		String fullDomain = PropfileService.strExtract(recipient, "@");
		
		//env.propertiesファイルから適切なドメインかチェックして、正しければファイル名を返す
		ArrayList<HashMap<String, String>> tmpHashMap = this.getDomainsMaps();
		String propFileName = PropfileService.checkPropFileExistence(fullDomain, tmpHashMap);
		return propFileName;
	}
}
