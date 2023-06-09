package live.itrip.jvmm.agent.utils;


import live.itrip.jvmm.agent.service.AgentContext;
import live.itrip.jvmm.agent.service.Launcher;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author : fengjianfeng
 * @date : 2021-07-22 10:39
 * description :
 **/
public class ServiceUtils {

    private static AgentContext AGENT;

    private static ProxyService PROXY_SERVICE;

    public static void load(ClassLoader classLoader) {

        PROXY_SERVICE = loadService(ProxyService.class, classLoader);

        Launcher launcher = loadService(Launcher.class, classLoader);
        AGENT = launcher.launch();
    }


    /**
     * load agent services
     *
     * @param clazz
     * @param classLoader
     * @param <T>
     * @return
     */
    public static <T> T loadService(Class<T> clazz, ClassLoader classLoader) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz, classLoader);
        Iterator<T> iterator = loader.iterator();
        if (iterator.hasNext()) {
            T conn = iterator.next();
            return conn;
        }
        return null;
    }

    public static AgentContext getAgentContext() {
        return AGENT;
    }

    public static ProxyService getProxyService() {
        return PROXY_SERVICE;
    }
}
