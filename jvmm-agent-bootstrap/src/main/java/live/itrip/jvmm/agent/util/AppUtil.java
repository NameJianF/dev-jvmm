package live.itrip.jvmm.agent.util;

/**
 * @author fengjianfeng
 * @date 2023/6/5 16:01
 */
public class AppUtil {

    private static String HOME_PATH;
    private static String LOG_PATH;
    private static String DATA_PATH;

    static {
        try {
            HOME_PATH = System.getProperty("user.dir").replaceAll("\\\\", "/");

            LOG_PATH = HOME_PATH + "/logs";
            System.setProperty("jvmm.log.path", LOG_PATH);

            DATA_PATH = HOME_PATH + "/data";
            System.setProperty("jvmm.data.path", DATA_PATH);
        } catch (Exception e) {
            System.err.println("Init application failed. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getLogPath() {
        return LOG_PATH;
    }

    public static String getDataPath() {
        return DATA_PATH;
    }

    public static String getHomePath() {
        return HOME_PATH;
    }

    public static String getTempPath() {
        return ".jvmm";
    }
}
