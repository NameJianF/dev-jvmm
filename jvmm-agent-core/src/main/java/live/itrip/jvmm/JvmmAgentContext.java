package live.itrip.jvmm;

import com.google.gson.JsonObject;
import live.itrip.jvmm.common.Constants;
import live.itrip.jvmm.conf.AgentConfig;
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
public class JvmmAgentContext {
    private static final Logger LOGGER = AgentLogFactory.getLogger(JvmmAgentContext.class);
    private Instrumentation instrumentation;
    private AgentConfig agentConfig;
    /**
     * http server
     */
    private AgentHttpServer agentHttpServer;

    private static volatile JvmmAgentContext context;

    public static JvmmAgentContext getInstance() {
        if (context == null) {
            synchronized (JvmmAgentContext.class) {
                if (context == null) {
                    context = new JvmmAgentContext();
                }
            }
        }

        return context;
    }

    private JvmmAgentContext() {

    }

    /**
     * 初始化必要数据
     */
    public void init(Instrumentation inst, String rootPath) {
        this.instrumentation = inst;

        // 配置数据
        LOGGER.info("ClientAgentContext is created.");
        this.agentConfig = AgentConfig.analysisArguments(rootPath);
    }

    /**
     * start http server
     */
    public void startHttpServer() {
        try {
            // http 命令监控
            CommandWatcher commandWatcher = new CommandWatcher();
            // 注册 data mock 命令处理
            // commandWatcher.setDataMockConfigListener(this.dataMockConfigHandler);

            ServerConfigure serverConfigure = new ServerConfigure.Builder().build();
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
    public void reportAgentAddress() {
        JsonObject object = new JsonObject();
        object.addProperty("version", "1.0");
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
                            , this.getServerAddress()
                            , this.getAppName());
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

    public String getAppName() {
        return this.agentConfig.getAppName();
    }

    public String getToken() {
        return this.agentConfig.getToken();
    }

    /**
     * stop http server
     */
    public void stopHttpServer() {
        if (this.agentHttpServer != null) {
            this.agentHttpServer.stop();
        }
    }

    public String getServerAddress() {
        if (StringUtils.isEmpty(this.agentConfig.getServerHttpPort())) {
            // 127.0.0.1
            return this.agentConfig.getServerHttpAddress();
        }
        // 127.0.0.1:8080
        return this.agentConfig.getServerHttpAddress() + ":" + this.agentConfig.getServerHttpPort();
    }

    public Instrumentation getInstrumentation() {
        return this.instrumentation;
    }

    public boolean redefineClass(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {
        if (getInstrumentation() == null) {
            return false;
        }
        getInstrumentation().redefineClasses(definitions);
        return true;
    }
}
