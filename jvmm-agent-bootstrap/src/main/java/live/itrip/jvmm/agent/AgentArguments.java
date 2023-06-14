package live.itrip.jvmm.agent;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author fengjianfeng
 * @date 2022/1/6
 * 功能描述: 启动参数解析
 */
public class AgentArguments {
    private static final Logger LOGGER = Logger.getLogger(AgentArguments.class.getCanonicalName());

    private static final String EMPTY_STRING = "";
    private static final String LIB_FOLDER = File.separator + "lib";
    private static final String AGENT_CLIENT_NAME = "jvmm-agent-bootstrap.jar";
    public static final String AGENT_SPY_NAME = "jvmm-agent-spy.jar";
    private String agentJarPath;
    private String spyJarPath;
    private String agentRootPath = null;

    /**
     * 获取 jar 路径
     */
    public static AgentArguments analysisArguments(String launchModel) {
        AgentArguments config = new AgentArguments();

        if (AgentLauncher.LAUNCH_MODE_AGENT.equalsIgnoreCase(launchModel)) {
            // premain
            List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
            for (String arg : args) {
                if (arg.startsWith("-javaagent:") && arg.contains(AGENT_CLIENT_NAME)) {
                    config.setAgentRootPath(arg.replace("-javaagent:", "").replace(AGENT_CLIENT_NAME, ""));
                    if (config.getAgentRootPath().endsWith("/")) {
                        config.setAgentRootPath(config.getAgentRootPath().substring(0, config.getAgentRootPath().length() - 1));
                    }
                }
            }
        } else {
            // attach
            config.setAgentRootPath(System.getProperty("user.dir"));
        }
        config.setAgentJarPath(config.getAgentRootPath() + File.separator + AGENT_CLIENT_NAME);
        config.setSpyJarPath(config.getAgentRootPath() + File.separator + AGENT_SPY_NAME);

        LOGGER.info("agentRootPath -> " + config.getAgentRootPath());
        return config;
    }

    public String getAgentRootPath() {
        return agentRootPath;
    }

    public void setAgentRootPath(String agentRootPath) {
        this.agentRootPath = agentRootPath;
    }

    public String getAgentJarPath() {
        return agentJarPath;
    }

    public void setAgentJarPath(String agentJarPath) {
        this.agentJarPath = agentJarPath;
    }

    public void setSpyJarPath(String spyJarPath) {
        this.spyJarPath = spyJarPath;
    }

    public String getSpyJarPath() {
        return this.spyJarPath;
    }
}
