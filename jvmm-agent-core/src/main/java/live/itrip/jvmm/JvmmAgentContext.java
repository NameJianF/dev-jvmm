package live.itrip.jvmm;

import com.google.gson.JsonObject;
import live.itrip.jvmm.common.Constants;
import live.itrip.jvmm.logging.AgentLogFactory;
import live.itrip.jvmm.monitor.server.ServerBootstrap;
import live.itrip.jvmm.monitor.server.entity.conf.Configuration;
import live.itrip.jvmm.util.*;
import live.itrip.jvmm.agent.service.AgentContext;
import live.itrip.jvmm.agent.utils.ServiceUtils;

import java.lang.instrument.Instrumentation;
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
     * 注册中心地址IP
     */
    private String serverAddress;

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
        String serverAddress = this.getServerAddress();
        Configuration config = new Configuration();

        ServerBootstrap.getInstance(config).start();
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
        object.addProperty("serverPort", "9090");
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

    /**
     * stop http server
     */
    public void stopHttpServer() {
//        if (this.agentHttpServer != null) {
//            this.agentHttpServer.stop();
//        }
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
}
