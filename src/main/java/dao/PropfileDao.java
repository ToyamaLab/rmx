package dao;

import java.util.ResourceBundle;
import dao.PropfileDao;

public class PropfileDao{
	//コンストラクタ
		public PropfileDao() {}
		
		public static ResourceBundle readPropFile(String propFileName) {
			return ResourceBundle.getBundle(propFileName);
		}
}
