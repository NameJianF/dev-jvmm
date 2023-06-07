package live.itrip.jvmm.agent.util;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author fengjianfeng
 * @date 2023/6/5 16:01
 */
public class ClassLoaderUtil {

    public static void loadJar(ClassLoader classLoader, URL jar) throws Throwable {
        if (classLoader instanceof URLClassLoader) {
            urlClassloaderLoadJar((URLClassLoader) classLoader, jar);
        } else {
            try {
                Method method = classLoader.getClass().getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
                method.setAccessible(true);
                method.invoke(classLoader, jar.getFile());
            } catch (Throwable e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public static ClassLoader systemLoadJar(URL jar) throws Throwable {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        loadJar(classLoader, jar);
        return classLoader;
    }

    private static void urlClassloaderLoadJar(URLClassLoader classLoader, URL jar) throws Throwable {
        Method addURL = classLoader.getClass().getSuperclass().getDeclaredMethod("addURL", URL.class);
        addURL.setAccessible(true);
        addURL.invoke(classLoader, jar);
    }
}
