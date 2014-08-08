package logic.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import logic.interfaces.PluginInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginsHolder {

	private static final Logger log = LoggerFactory.getLogger(PluginsHolder.class);

	// コンストラクタ
	public PluginsHolder() {
	}

	// cpathにあるjarファイルを読み込んで全Pluginインスタンスを返す.
	public ArrayList<PluginInterface> holdPlugins() {
		// 複数のプラグインを格納するリスト
		ArrayList<PluginInterface> plugins = new ArrayList<PluginInterface>();
		// プラグイン用の.jarファイルが配置されているパス
		String cpath = System.getProperty("user.dir") + File.separator + "src/main/java/plugins";
		// 使用できるプラグイン全てを得る
		plugins = this.setPlugins(cpath);

		return plugins;
	}

	// 複数のプラグインの中からfunction名に対応するプラグインを返す.
	public PluginInterface selectPlugin(ArrayList<PluginInterface> plugins, String function) {
		return this.getPlugin(plugins, function);
	}

	/**
	 * cpathにあるjarファイルを読み込んでPluginインスタンスとしてArrayListに格納する.
	 * 
	 * @param cpath
	 *            プラグイン用の.jarファイルが配置されているパス
	 * @return 使用できる全てのプラグイン
	 * */
	private ArrayList<PluginInterface> setPlugins(String cpath) {
		ArrayList<PluginInterface> plugins = new ArrayList<PluginInterface>();
		try {
			File f = new File(cpath);
			String[] files = f.list();
			//
			if (files.length == 0) {
				log.error("not exists file in plugins dir.");
				return null;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".jar")) {
					File file = new File(cpath + File.separator + files[i]);
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
							PluginInterface plugin = (PluginInterface) cobj.newInstance();
							plugins.add(plugin);
							break;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return plugins;
	}

	/**
	 * Pluginインスタンスの中からfunctionと一致するPluginインスタンスを1つ返す
	 * */
	private PluginInterface getPlugin(ArrayList<PluginInterface> plugins, String function) {
		for (int i = 0; i < plugins.size(); i++) {
			List<String> pluginFunctions = plugins.get(i).getAvailableFunctionNames();
			for (int j = 0; j < pluginFunctions.size(); j++) {
				if (pluginFunctions.get(j).equalsIgnoreCase(function))
					return plugins.get(i);
				else
					continue;
			}
		}
		return null;
	}
}
