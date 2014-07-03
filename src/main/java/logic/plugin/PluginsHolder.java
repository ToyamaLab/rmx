package logic.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import logic.interfaces.PluginInterface;
import logic.service.PluginService;

public class PluginsHolder{
	//コンストラクタ
	public PluginsHolder() {}
	
	//
	public ArrayList<PluginInterface> holdPlugins() {
		ArrayList<PluginInterface> plugins = new ArrayList<PluginInterface>();
		String cpath = System.getProperty("user.dir") +
				File.separator + "src/main/java/plugins";
		plugins  = PluginService.setPlugins(cpath);
		return plugins;
	}
	
	public PluginInterface selectPlugin(ArrayList<PluginInterface> plugins, String function) {
		return PluginService.getPlugin(plugins, function);
	}
}
