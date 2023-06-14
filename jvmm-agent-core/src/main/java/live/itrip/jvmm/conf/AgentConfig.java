package live.itrip.jvmm.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author fengjianfeng
 * @date 2022/1/6
 * 功能描述: 启动参数解析
 */
public class AgentConfig {
    private static final Logger LOGGER = Logger.getLogger(AgentConfig.class.getCanonicalName());

    private static final String EMPTY_STRING = "";
    private static final String LIB_FOLDER = File.separator + "lib";
    private static final String AGENT_CLIENT_NAME = "jvmm-agent-bootstrap.jar";
    public static final String AGENT_SPY_NAME = "jvmm-agent-spy.jar";

    private String token;
    private String appName;
    private String appEnv;
    private String serverType;
    private String serverHttpAddress;
    private String serverHttpPort;
    private String serverHttpAuthEnable;
    private String serverHttpAuthUserName;
    private String serverHttpAuthPassword;

    private String agentJarPath;
    private String spyJarPath;

    public String printString() {
        return String.format("agent config ---> token[%s]" +
                        ",appName[%s]" +
                        ",env[%s]" +
                        ",serverType[%s]" +
                        ",serverAddress[%s]" +
                        ",serverPort[%s]"
                , getToken()
                , getAppName()
                , getAppEnv()
                , getServerType()
                , getServerHttpAddress()
                , getServerHttpPort());
    }

    /**
     * 加载配置
     */
    public static AgentConfig analysisArguments(String agentRootPath) {
        final String KEY_TOKEN = "token";
        final String KEY_NAME = "name";
        final String KEY_ENV = "env";
        final String KEY_SERVER_TYPE = "server.type";
        final String KEY_SERVER_HTTP_ADDRESS = "server.http.address";
        final String KEY_SERVER_HTTP_PORT = "server.http.port";
        final String KEY_SERVER_HTTP_AUTH_ENABLE = "server.http.auth.enable";
        final String KEY_SERVER_HTTP_AUTH_USERNAME = "server.http.auth.username";
        final String KEY_SERVER_HTTP_AUTH_PASSWORD = "server.http.auth.password";

        AgentConfig config = new AgentConfig();

        config.setAgentJarPath(agentRootPath + File.separator + AGENT_CLIENT_NAME);
        config.setSpyJarPath(agentRootPath + File.separator + AGENT_SPY_NAME);

        try {
            String configPath = agentRootPath + File.separator + "conf" + File.separator + "server.properties";
            File configFile = new File(configPath);
            if (configFile.exists()) {
                Properties properties = new Properties();
                // 使用InPutStream流读取properties文件
                BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));
                properties.load(bufferedReader);
                // 获取key对应的value值
                config.setToken(properties.getProperty(KEY_TOKEN));
                config.setAppName(properties.getProperty(KEY_NAME));
                config.setAppEnv(properties.getProperty(KEY_ENV));
                config.setServerType(properties.getProperty(KEY_SERVER_TYPE));
                config.setServerHttpAddress(properties.getProperty(KEY_SERVER_HTTP_ADDRESS));
                config.setServerHttpPort(properties.getProperty(KEY_SERVER_HTTP_PORT));
                config.setServerHttpAuthEnable(properties.getProperty(KEY_SERVER_HTTP_AUTH_ENABLE));
                config.setServerHttpAuthUserName(properties.getProperty(KEY_SERVER_HTTP_AUTH_USERNAME));
                config.setServerHttpAuthPassword(properties.getProperty(KEY_SERVER_HTTP_AUTH_PASSWORD));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return config;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppEnv() {
        return appEnv;
    }

    public void setAppEnv(String appEnv) {
        this.appEnv = appEnv;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServerHttpAddress() {
        return serverHttpAddress;
    }

    public void setServerHttpAddress(String serverHttpAddress) {
        this.serverHttpAddress = serverHttpAddress;
    }

    public String getServerHttpPort() {
        return serverHttpPort;
    }

    public void setServerHttpPort(String serverHttpPort) {
        this.serverHttpPort = serverHttpPort;
    }

    public String getServerHttpAuthEnable() {
        return serverHttpAuthEnable;
    }

    public void setServerHttpAuthEnable(String serverHttpAuthEnable) {
        this.serverHttpAuthEnable = serverHttpAuthEnable;
    }

    public String getServerHttpAuthUserName() {
        return serverHttpAuthUserName;
    }

    public void setServerHttpAuthUserName(String serverHttpAuthUserName) {
        this.serverHttpAuthUserName = serverHttpAuthUserName;
    }

    public String getServerHttpAuthPassword() {
        return serverHttpAuthPassword;
    }

    public void setServerHttpAuthPassword(String serverHttpAuthPassword) {
        this.serverHttpAuthPassword = serverHttpAuthPassword;
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
