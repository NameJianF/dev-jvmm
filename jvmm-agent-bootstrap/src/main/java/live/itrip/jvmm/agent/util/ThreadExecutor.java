package live.itrip.jvmm.agent.util;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : fengjianfeng
 * time : 2019/3/22 14:28
 * desc : 线程池
 * update :
 */
public class ThreadExecutor {
    private static ThreadPoolExecutor cachedThreadPool;

    static {
        cachedThreadPool = new ThreadPoolExecutor(
                50,
                200,
                50L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
//                new ThreadFactoryBuilder().build());
                Executors.defaultThreadFactory()
                ,new ThreadPoolExecutor.AbortPolicy());
    }

    public static ThreadPoolExecutor getCachedThreadPool() {
        return cachedThreadPool;
    }

    /**
     * 需要设置 Runnable Name 使用 Thread.currentThread().setName("threadName");
     *
     * @param runnable Runnable
     */
    public static void execute(Runnable runnable) {
        cachedThreadPool.execute(runnable);
    }
}
