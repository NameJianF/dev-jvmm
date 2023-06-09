package live.itrip.jvmm.agent.utils;


/**
 * @author : fengjianfeng
 * @date : 2021-08-18 16:08
 * description : 本地代理服务，用于RPC
 **/
public interface ProxyService {

    /**
     * new proxy instance
     *
     * @param ownerMethodSignature
     * @param interfaceClass
     * @param targetMethodName
     * @param <T>
     * @return
     */
    <T> T newProxyInstance(String ownerMethodSignature, Class interfaceClass, String targetMethodName);
}
