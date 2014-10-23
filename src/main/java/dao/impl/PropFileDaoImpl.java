package dao.impl;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import dao.PropFileDao;

public class PropFileDaoImpl implements PropFileDao {
	
	@Override
	public ResourceBundle read(String propFileName) {
		try{
			return ResourceBundle.getBundle(propFileName);
		} catch (MissingResourceException e){
			return null;
		}
	}
}
