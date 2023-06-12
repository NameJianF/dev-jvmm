package live.itrip.jvmm.agent;


import live.itrip.jvmm.agent.loader.AgentClassLoader;
import live.itrip.jvmm.agent.utils.JarFileUtils;
import live.itrip.jvmm.agent.utils.ServiceUtils;
import live.itrip.jvmm.agent.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
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
    private static final String LAUNCH_MODE_AGENT = "agent";
    /**
     * 启动模式: attach方式加载
     */
    private static final String LAUNCH_MODE_ATTACH = "attach";
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
        // 参数解析
        AgentArguments.analysisArguments();

        // print args
        LOGGER.info(AgentArguments.printString());

        if (StringUtils.isNotEmpty(AgentArguments.getAppName())) {
            // 初始化 agent loader
            String agentJarPath = JarFileUtils.findJavaAgentPath();
            String agentCorePath = JarFileUtils.findAgentCoreJarPath(AgentArguments.getEnv(), agentJarPath);
            LOGGER.info(String.format("agentCorePath [%s], loader [%s] ", agentCorePath, AgentLauncher.class.getClassLoader()));

            LOGGER.info(" system class loader---> " + ClassLoader.getSystemClassLoader().getClass().getName());

            // install spy
            install(inst);

            // 统一为 AgentClassLoader 加载方式
            ClassLoader classLoader = new AgentClassLoader(agentJarPath);

            LOGGER.info("agent install service provider.");
            ServiceUtils.load(classLoader);

            // 设置/保存启动参数
            ServiceUtils.getAgentContext().setInstrumentation(inst);
            ServiceUtils.getAgentContext().setAppName(AgentArguments.getAppName());
            ServiceUtils.getAgentContext().setToken(AgentArguments.getToken());
            ServiceUtils.getAgentContext().setEnv(AgentArguments.getEnv());
            ServiceUtils.getAgentContext().setServerAddress(AgentArguments.getServerAddress());

            // 上报http server 地址、端口
            ServiceUtils.getAgentContext().reportAgentAddress();
        } else {
            LOGGER.info("---> target app name (args) is null. <--- ");
        }
    }

    /**
     * install spy
     *
     * @param inst inst
     */
    private static synchronized void install(final Instrumentation inst) {
        try {
            // SPY_JAR_PATH
            final String spyJarPath = JarFileUtils.findAgentSpyJarPath(AgentArguments.getEnv(), JarFileUtils.findJavaAgentPath());
            // 将Spy注入到BootstrapClassLoader

            inst.appendToBootstrapClassLoaderSearch(new JarFile(new File(spyJarPath)));

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
