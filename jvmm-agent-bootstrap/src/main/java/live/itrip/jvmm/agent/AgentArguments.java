package live.itrip.jvmm.agent;

import live.itrip.jvmm.agent.utils.StringUtils;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengjianfeng
 * @date 2022/1/6
 * 功能描述: 启动参数解析
 */
class AgentArguments {
    private static final String EMPTY_STRING = "";
    /**
     * 应用 namespace
     */
    private static final String DEFAULT_NAMESPACE = "default";
    /**
     * agent token
     */
    private static final String KEY_TOKEN = "token";
    /**
     * app name
     */
    private static final String KEY_APP_NAME = "-Djvmm.app.name";
    /**
     * agent 部署的环境
     */
    private static final String KEY_APP_ENV = "-Djvmm.app.env";
    /**
     * 注册中心地址IP
     */
    private static final String KEY_SERVER_ADDRESS = "-Djvmm.server.address";

    /**
     * 解析后的参数 KV
     */
    private static Map<String, String> featureMap = new LinkedHashMap<>(4);

    public static Map<String, String> getFeatureMap() {
        return featureMap;
    }

    public static String getToken() {
        return featureMap.get(KEY_TOKEN);
    }

    public static String getAppName() {
        return featureMap.get(KEY_APP_NAME);
    }

    public static String getEnv() {
        return featureMap.get(KEY_APP_ENV);
    }

    public static String getServerAddress() {
        return featureMap.get(KEY_SERVER_ADDRESS);
    }

    public static String printString() {
        return String.format("Arguments ---> token[%s],appName[%s],env[%s],serverAddress[%s],"
                , getToken(), getAppName(), getEnv(), getServerAddress());
    }

    /**
     * 解析参数
     */
    public static void analysisArguments() {

        List<String> featureString = ManagementFactory.getRuntimeMXBean().getInputArguments();

        // 不对空字符串进行解析
        if (featureString == null || featureString.size() == 0) {
            return;
        }

        for (String kvPairSegmentString : featureString) {
            if (StringUtils.isEmpty(kvPairSegmentString)) {
                continue;
            }
            final String[] kvSegmentArray = kvPairSegmentString.split("=");
            if (kvSegmentArray.length != 2
                    || StringUtils.isEmpty(kvSegmentArray[0])
                    || StringUtils.isEmpty(kvSegmentArray[1])) {
                continue;
            }
            featureMap.put(kvSegmentArray[0], kvSegmentArray[1]);
        }
    }
}
