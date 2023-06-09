package live.itrip.jvmm.util;


/**
 * @author : fengjianfeng
 * @date : 2021-06-18 14:37
 * description : thread sleep
 **/
public class ThreadUtils {

    /**
     * current thread sleep
     *
     * @param millis millis
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
