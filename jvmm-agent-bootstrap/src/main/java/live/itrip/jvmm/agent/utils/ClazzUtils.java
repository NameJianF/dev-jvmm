package live.itrip.jvmm.agent.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : fengjianfeng
 * @date : 2021-07-29 19:20
 * description : 加载指定class，并创建实例
 **/
public class ClazzUtils {
    private static final Logger LOGGER = Logger.getLogger(ClazzUtils.class.getCanonicalName());

    /**
     * 加载指定class，并创建实例
     *
     * @param classLoader class loader
     * @param className   class name
     * @param <T>
     * @return
     */
    public static <T> T newInstance(ClassLoader classLoader, String className) {
        try {
            LOGGER.info("AGENT_CLASS_LOADER ---> " + classLoader.getClass().getCanonicalName());
            LOGGER.info("loadClass  ---> " + className);
            return (T) classLoader.loadClass(className).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 加载指定class，并创建实例
     *
     * @param classLoader    class loader
     * @param className      class name
     * @param parameterTypes 参数类型
     * @param initArgs       入参
     * @param <T>
     * @return
     */
    public static <T> T newInstance(ClassLoader classLoader, String className, Class<?>[] parameterTypes, Object... initArgs) {
        try {
            return (T) classLoader.loadClass(className).getConstructor(parameterTypes).newInstance(initArgs);
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }
}
