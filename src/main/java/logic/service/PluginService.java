package logic.service;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import dao.DBDao;
import data.Message;
import logic.interfaces.PluginInterface;
import logic.parse.User;

public class PluginService {
	/**ユーティリティクラスなのでコンストラクタは呼び出せないように*/
	private PluginService() {}
	
	/**
	 * cpathにあるjarファイルを読み込んでPluginインスタンスとしてArrayListに格納する
	 * */
	public static ArrayList<PluginInterface> setPlugins(String cpath) {
		ArrayList<PluginInterface> plugins = new ArrayList<PluginInterface>();
		try {
			File f = new File(cpath);
			String[] files = f.list();
			for(int i=0;i<files.length;i++) {
				if(files[i].endsWith(".jar")) {
					File file = new File(cpath + File.separator+files[i]);
					JarFile jar = new JarFile(file);
					Manifest mf = jar.getManifest();
					Attributes att = mf.getMainAttributes();
					String cname = att.getValue("Plugin-Class");
					URL url = file.getCanonicalFile().toURI().toURL();
	                URLClassLoader loader = new URLClassLoader(new URL[] { url });
	                Class cobj = loader.loadClass(cname);
	                Class[] ifnames = cobj.getInterfaces();
	                for (int j = 0; j < ifnames.length; j++) {
	                    if (ifnames[j] == PluginInterface.class) {
	                    	System.out.println(ifnames[j]);
	                        System.out.println("load..... " + cname);
	                        PluginInterface plugin =
	                            (PluginInterface)cobj.newInstance();
	                        plugins.add(plugin);
	                        break;
	                    }
	                }
				}
			}
		}catch (Exception ex) {
            ex.printStackTrace();
        }
		return plugins;
	}
	
	/**
	 * Pluginインスタンスの中からfunctionと一致するPluginインスタンスを1つ返す
	 * */
	public static PluginInterface getPlugin(ArrayList<PluginInterface> plugins, String function) {
		for(int i=0;i<plugins.size();i++) {
			ArrayList<String> pluginFunctions = plugins.get(i).getAvailableFunctionNames();
			for(int j=0;j<pluginFunctions.size();j++) {
				if(pluginFunctions.get(j).equalsIgnoreCase(function))
					return plugins.get(i);
				else 
					continue;
			}
		}
		return null;
	}
	
	public static ArrayList<String> getRecipients(Message oMsg,
			ResourceBundle domconfBundle){
		String target = trimSharp(oMsg.getRecipient());
		if(target.indexOf("@")>0) {
			//targetをパースする
			User funcUser = new User();
			funcUser.UserStart(target, domconfBundle);
			
			//クエリとパラメーターを得る
			String query = funcUser.getQuery();
			ListIterator<String> params = funcUser.getPara().listIterator();
			
			//宛先(destinations)を得る
			ArrayList<String> recipients = getMailAddresses(domconfBundle, query, params);
			
			return recipients;
		}else {
			ArrayList<String> returnMe = new ArrayList<String>();
			returnMe.add(oMsg.getSender());
			
			return returnMe;
		}
	}
	
	public static String trimSharp(String str) {
		int start = str.indexOf("#", 1);
		return str.substring(start+1);
	}
	
	public static ArrayList<String> getMailAddresses(
			ResourceBundle domconfBundle,
			String query,
			ListIterator<String> params){
		//
		ArrayList<String> mailAddresses  = new ArrayList<String>();
		try {
			DBDao dbDao = new DBDao(domconfBundle);
			ResultSet rs;
			rs = dbDao.read(query, params);
			while(rs.next()) {mailAddresses.add(rs.getString(1));}
			rs.close();
		} catch (Exception e) {e.printStackTrace();}
		return mailAddresses;
	}
}
