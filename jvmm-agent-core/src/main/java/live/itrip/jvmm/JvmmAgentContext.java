package live.itrip.jvmm;

import com.google.gson.JsonObject;
import live.itrip.jvmm.agent.service.AgentContext;
import live.itrip.jvmm.agent.utils.ServiceUtils;
import live.itrip.jvmm.common.Constants;
import live.itrip.jvmm.logging.AgentLogFactory;
import live.itrip.jvmm.server.AgentHttpServer;
import live.itrip.jvmm.server.CommandWatcher;
import live.itrip.jvmm.server.ServerConfigure;
import live.itrip.jvmm.util.*;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : fengjianfeng
 * @date : 2021-07-23 11:35
 * description : ClientAgentContext
 **/
public class JvmmAgentContext implements AgentContext {
    private static final Logger LOGGER = AgentLogFactory.getLogger(JvmmAgentContext.class);

    private Instrumentation instrumentation;
    /**
     * app name
     */
    private String appName;
    /**
     * agent token
     */
    private String token;
    /**
     * env
     */
    private String env;
    /**
     * 注册中心地址IP
     */
    private String serverAddress;
    private CommandWatcher commandWatcher;
    private ServerConfigure serverConfigure;

    /**
     * http server
     */
    private AgentHttpServer agentHttpServer;
    /**
     * agent client content
     */
    private static final JvmmAgentContext CONTEXT = new JvmmAgentContext();

    public static JvmmAgentContext getContext() {
        return CONTEXT;
    }

    public JvmmAgentContext() {
        this.init();
        LOGGER.info("ClientAgentContext is created.");
    }

    /**
     * 初始化必要数据
     */
    private void init() {
        // 配置数据
    }

    /**
     * start http server
     */
    public void startHttpServer() {
        try {
            // http 命令监控
            commandWatcher = new CommandWatcher();
            // 注册 data mock 命令处理
            // commandWatcher.setDataMockConfigListener(this.dataMockConfigHandler);

            serverConfigure = new ServerConfigure.Builder().build();
            agentHttpServer = new AgentHttpServer(serverConfigure.getServerIp(), serverConfigure.getServerPort(), true, commandWatcher);


            LOGGER.info(String.format("IP[%s],PORT[%s]", serverConfigure.getServerIp(), serverConfigure.getServerPort()));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            // stop
            this.stopHttpServer();
        }
    }

    /**
     * 上报http server 地址、端口
     */
    @Override
    public void reportAgentAddress() {
        JsonObject object = new JsonObject();
        object.addProperty("mockVersion", "1.0");
        object.addProperty("appName", this.getAppName());
        object.addProperty("serverName", IPUtil.getLocalIP());
        object.addProperty("serverIp", IPUtil.getLocalIP());
        object.addProperty("serverPort", "8001");
        object.addProperty("timestamp", System.currentTimeMillis());

        String content = GsonUtils.toJson(object);

        ThreadExecutor.execute(() -> {
            int count = 0;
            while (true) {
                if (count == 10) {
                    break;
                }
                try {
                    String url = String.format(Constants.AGENT_ADDRESS_REPORT_URL
                            , JvmmAgentContext.getContext().getServerAddress()
                            , ServiceUtils.getAgentContext().getAppName());
                    // post data
                    String response = HttpUtils.doPost(url, content);
                    LOGGER.info(String.format(" post data ---> url[%s], data[%s], response[%s]", url, content, response));
                    JsonObject jsonObject = GsonUtils.fromJson(response, JsonObject.class);
                    if (jsonObject != null && jsonObject.has("code") && jsonObject.get("code").getAsInt() == 0) {
                        break;
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                }
                count++;
                ThreadUtils.sleep(5 * 1000);
            }
        });

    }

    @Override
    public String getAppName() {
        return this.appName;
    }

    @Override
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void setEnv(String env) {
        this.env = env;
    }

    /**
     * stop http server
     */
    public void stopHttpServer() {
        if (this.agentHttpServer != null) {
            this.agentHttpServer.stop();
        }
    }

    @Override
    public String getServerAddress() {
        return this.serverAddress;
    }

    @Override
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }


    @Override
    public Instrumentation getInstrumentation() {
        return this.instrumentation;
    }

    @Override
    public void setInstrumentation(Instrumentation inst) {
        this.instrumentation = inst;
    }


    public boolean redefineClass(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {
        if (getInstrumentation() == null) {
            return false;
        }
        getInstrumentation().redefineClasses(definitions);
        return true;
    }
}
