package logic.plugin;

import java.io.File;
import java.util.ArrayList;

import logic.interfaces.PluginInterface;
import logic.utils.PluginUtils;

public class PluginsHolder{
	//コンストラクタ
	public PluginsHolder() {}
	
	//cpathにあるjarファイルを読み込んで全Pluginインスタンスを返す.
	public ArrayList<PluginInterface> holdPlugins() {
		//複数のプラグインを格納するリスト
		ArrayList<PluginInterface> plugins = new ArrayList<PluginInterface>();
		//プラグイン用の.jarファイルが配置されているパス
		String cpath = System.getProperty("user.dir") + File.separator + "src/main/java/plugins";
		//使用できるプラグイン全てを得る
		plugins  = PluginUtils.setPlugins(cpath);
		
		return plugins;
	}
	
	//複数のプラグインの中からfunction名に対応するプラグインを返す.
	public PluginInterface selectPlugin(ArrayList<PluginInterface> plugins, String function) {
		return PluginUtils.getPlugin(plugins, function);
	}
}
