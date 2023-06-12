package live.itrip.jvmm.agent.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : fengjianfeng
 * @date : 2021-07-29 19:23
 * description : jar file path utils
 **/
public class JarFileUtils {
    private static final Logger LOGGER = Logger.getLogger(JarFileUtils.class.getCanonicalName());
    private static final String LIB_FOLDER = File.separator + "lib";
    private static final String[] TARGET_LIBS = new String[]{"gson-2.8.7.jar", "guava-29.0-jre.jar", "javassist-3.27.0-GA.jar"};
    private static final String AGENT_CLIENT_NAME = "jvmm-agent";
    private static final String AGENT_CORE_NAME = "jvmm-agent-core";
    private static final String AGENT_SPY_NAME = "jvmm-agent-spy";
    private static final String ENV_TEST = "test";

    /**
     * -javaagent:/Users/fengjianfeng/Documents/project/mock-trace/jvmm-agent/jvmm-agent-core/target/jvmmAgent-jar-with-dependencies.jar
     *
     * @return String
     */
    public static String findJavaAgentPath() {
        String root = null;
        List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : args) {
            if (arg.startsWith("-javaagent:") && arg.contains(AGENT_CLIENT_NAME)) {
                root = arg.replace("-javaagent:", "");
//                root = root.substring(0, root.indexOf("="));
            }
        }
        return root;
    }

    public static String findAgentCoreJarPath(String env, String agentJarPath) {
        String root = findRoot(agentJarPath);
        String lib = null;
        if (env == null || "".equalsIgnoreCase(env) || ENV_TEST.equalsIgnoreCase(env)) {
            lib = root + LIB_FOLDER;
        } else {
            // agentJarPath相同目录下
            lib = root;
        }

        File folder = new File(lib);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar") && name.startsWith(AGENT_CORE_NAME);
                }
            });

            if (files != null && files.length == 1) {
                String path = files[0].getPath();
                LOGGER.info("AgentCoreJarPath ---> " + path);
                return path;
            }
        }

        return "";
    }


    public static String findAgentSpyJarPath(String env, String agentJarPath) {
        String root = findRoot(agentJarPath);
        String lib = null;
        if (env == null || "".equalsIgnoreCase(env) || ENV_TEST.equalsIgnoreCase(env)) {
            lib = root + LIB_FOLDER;
        } else {
            // agentJarPath相同目录下
            lib = root;
        }

        File folder = new File(lib);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar") && name.startsWith(AGENT_SPY_NAME);
                }
            });

            if (files != null && files.length == 1) {
                String path = files[0].getPath();
                LOGGER.info("AgentSpyJarPath ---> " + path);
                return path;
            }
        }

        return "";
    }

    public static URL[] findLibJar(String env, String agentJarPath) {
        String root = findRoot(agentJarPath);
        URL[] url = null;
        String lib = null;
        if (env == null || "".equalsIgnoreCase(env) || ENV_TEST.equalsIgnoreCase(env)) {
            lib = root + LIB_FOLDER;
        } else {
            // agentJarPath相同目录下
            lib = root;
        }

        File folder = new File(lib);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
//                    return name.equalsIgnoreCase(".jar");
                    for (String item : TARGET_LIBS) {
                        if (item.equalsIgnoreCase(name)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
            url = new URL[files.length];
            for (int i = 0; i < files.length; i++) {
                try {
                    url[i] = files[i].toURI().toURL();
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, "sgm client class loader load jar url: {0}", new Object[]{url[i]});
                    }
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        if (url == null) {
            LOGGER.log(Level.WARNING, "can not found jar in library: {0}", new Object[]{lib});
            url = new URL[0];
        }
        return url;
    }

    public static String findRoot(String agentJarPath) {
        return StringUtils.substringBeforeLast(agentJarPath, File.separator);
    }
}
