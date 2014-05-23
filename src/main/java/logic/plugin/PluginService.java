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

public class PluginService {
	//コンストラクタ
	public PluginService() {}
	
	//
	public static ArrayList<PluginInterface> getPlugins() {
		ArrayList<PluginInterface> plugins = new ArrayList<PluginInterface>();
		String cpath = System.getProperty("user.dir") +
				File.separator + "src/plugins";
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
}
