package live.itrip.jvmm.agent;

import live.itrip.jvmm.agent.loader.AgentClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author : fengjianfeng
 * @date : 2021-07-23 10:15
 * description : AgentMain
 **/
public final class AgentLauncher {
    private static final Logger LOGGER = Logger.getLogger(AgentLauncher.class.getCanonicalName());

    /**
     * 启动模式: agent方式加载
     */
    public static final String LAUNCH_MODE_AGENT = "agent";
    /**
     * 启动模式: attach方式加载
     */
    public static final String LAUNCH_MODE_ATTACH = "attach";
    private static final String SERVER_MAIN_CLASS = "live.itrip.jvmm.service.Launcher";
    /**
     * 启动默认
     */
    private static String LAUNCH_MODE;

    /**
     * premain，应用启动时加载
     *
     * @param agentArgs args
     * @param inst      instrumentation
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            LOGGER.info("---> jvmm agent premain start <--- ");
            LAUNCH_MODE = LAUNCH_MODE_AGENT;

            main(agentArgs, inst);

            LOGGER.info("---> jvmm agent premain finished <--- ");
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, " premain start agent error :", ex);
        }
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            LOGGER.log(Level.INFO, "---< java agent agentmain start >---");
            LAUNCH_MODE = LAUNCH_MODE_ATTACH;

            main(agentArgs, inst);

            LOGGER.info("---> jvmm agent agentmain finished <--- ");
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, " agentmain start agent error :", ex);
        }
    }

    private static synchronized void main(String args, final Instrumentation inst) {
        LOGGER.info(" jvmm agent args ---> " + args);

        // load agent config
        AgentArguments agentConfig = AgentArguments.analysisArguments(LAUNCH_MODE);

        // 初始化 agent loader
        LOGGER.info(" system class loader---> " + ClassLoader.getSystemClassLoader().getClass().getName());

        // install spy
        install(inst, agentConfig);

        // 统一为 AgentClassLoader 加载方式
        ClassLoader classLoader = new AgentClassLoader(agentConfig.getAgentJarPath());

        LOGGER.info("start Agent server.");
        bootServer(inst, classLoader, agentConfig.getAgentRootPath());
    }

    private static void bootServer(Instrumentation inst, ClassLoader classLoader, String rootPath) {
        try {
            Class<?> bootClazz = classLoader.loadClass(SERVER_MAIN_CLASS);
            Object boot = bootClazz.getMethod("getInstance", Instrumentation.class, String.class)
                    .invoke(null, inst, rootPath);
            Function<Object, Object> callback = o -> {
                LOGGER.info("bootServer callback ---> " + o.toString());
                return null;
            };
            bootClazz.getMethod("launch", Function.class)
                    .invoke(boot, callback);
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * install spy
     *
     * @param inst inst
     */
    private static synchronized void install(final Instrumentation inst, final AgentArguments agentConfig) {

        try {
            // SPY_JAR_PATH
            // 将Spy注入到BootstrapClassLoader
            inst.appendToBootstrapClassLoaderSearch(new JarFile(new File(agentConfig.getSpyJarPath())));

            // CORE_JAR_PATH
//            final String coreJarPath = FileUtils.findAgentCoreJarPath(AgentArguments.getEnv(), FileUtils.findJavaAgentPath());

            // namespace
//            final String namespace = "";

            // 构造自定义的类加载器，尽量减少Sandbox对现有工程的侵蚀
//            final ClassLoader sandboxClassLoader = loadOrDefineClassLoader(namespace, coreJarPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
