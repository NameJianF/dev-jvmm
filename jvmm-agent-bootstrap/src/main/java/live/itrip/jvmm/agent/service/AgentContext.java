package live.itrip.jvmm.agent.service;

import java.lang.instrument.Instrumentation;

/**
 * @author : fengjianfeng
 * @date : 2021-07-23 10:37
 * description : AgentContext
 **/
public interface AgentContext {
    /**
     * report agent http server IP&Port
     */
    void reportAgentAddress();

    /**
     * get app name
     *
     * @return app name
     */
    String getAppName();

    /**
     * set app name
     *
     * @param appName app name
     */
    void setAppName(String appName);

    /**
     * get token
     *
     * @return token
     */
    String getToken();

    /**
     * set token
     *
     * @param token token
     */
    void setToken(String token);
    /**
     * set env
     *
     * @param env env
     */
    void setEnv(String env);

    /**
     * 获取注册中心IP地址
     *
     * @return ip
     */
    String getServerAddress();

    /**
     * set server address
     *
     * @param serverAddress address
     */
    void setServerAddress(String serverAddress);

    /**
     * get Instrumentation
     *
     * @return Instrumentation
     */
    Instrumentation getInstrumentation();

    /**
     * set Instrumentation
     *
     * @param inst Instrumentation
     */
    void setInstrumentation(Instrumentation inst);
}
