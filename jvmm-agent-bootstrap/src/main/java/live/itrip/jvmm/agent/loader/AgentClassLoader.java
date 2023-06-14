package live.itrip.jvmm.agent.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author fengjianfeng
 * @date 2021-11-17
 */
public class AgentClassLoader extends URLClassLoader {
    private static final Logger LOGGER = Logger.getLogger(AgentClassLoader.class.getCanonicalName());

    private static final String LIB_FOLDER = File.separator + "lib";
    private static final String CONF_FOLDER = File.separator + "conf";

    private static final String[] RUNTIME_DEPENDENCY = new String[]{"com.alibaba.dubbo.", "com.weibo.api.motan.", "cn.sunline.edsp."};

    public AgentClassLoader(String agentPath) {
        super(findJar(agentPath), (ClassLoader.getSystemClassLoader() == null) ? null : ClassLoader.getSystemClassLoader().getParent());
        LOGGER.log(Level.INFO, "start client class loader with {0}, parent: {1}"
                , new Object[]{agentPath, (ClassLoader.getSystemClassLoader() == null) ? null : ClassLoader.getSystemClassLoader().getParent()});
        try {
            addURL((new File(findRoot(agentPath) + CONF_FOLDER)).toURI().toURL());
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // LOGGER.log(Level.INFO, "loadClass ---> " + name + ",class loader ---> " + getClass());
        try {
            return super.loadClass(name);
        } catch (ClassNotFoundException e) {
            if (name != null && isRuntimeDependency(name)) {
                ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
                if (threadClassLoader != null && !(threadClassLoader instanceof AgentClassLoader)) {
                    return threadClassLoader.loadClass(name);
                }
            }
            throw e;
        }
    }

    private static boolean isRuntimeDependency(String className) {
        for (String dependency : RUNTIME_DEPENDENCY) {
            if (className.startsWith(dependency)) {
                return true;
            }
        }
        return false;
    }

    private static URL[] findJar(String agentPath) {
        String root = findRoot(agentPath);
        URL[] url = null;
        String lib = root + LIB_FOLDER;
        File folder = new File(lib);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });

            if (files != null) {
                url = new URL[files.length];
                for (int i = 0; i < files.length; i++) {
                    try {
                        url[i] = files[i].toURI().toURL();
                        // if (LOGGER.isLoggable(Level.FINE))
                        {
                            LOGGER.log(Level.FINE, "jvmm client class loader load jar url: {0}", new Object[]{url[i]});
                        }
                    } catch (Throwable e) {
                        LOGGER.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
        }
        if (url == null) {
            LOGGER.log(Level.WARNING, "can not found jar in library: {0}", new Object[]{lib});
            url = new URL[0];
        }
        return url;
    }

    private static String findRoot(String agentPath) {
        return substringBeforeLast(agentPath, File.separator);
    }

    private static String substringBeforeLast(String str, String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }
    private static boolean isEmpty(CharSequence cs) {
        return (cs == null || cs.length() == 0);
    }
}