package live.itrip.jvmm.service;

import io.netty.util.concurrent.Promise;
import live.itrip.jvmm.util.meta.PairKey;
import live.itrip.jvmm.monitor.core.CollectionType;
import live.itrip.jvmm.monitor.core.JvmmCollector;
import live.itrip.jvmm.monitor.core.JvmmFactory;
import live.itrip.jvmm.monitor.core.entity.JvmmData;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 09:51 2022/9/7
 *
 * @author fengjianfeng
 */
public interface JvmmService {

    interface ShutdownListener {
        void onShutdown();
    }

    void start(Promise<Integer> promise);

    void shutdown();

    <T extends JvmmService> T addShutdownListener(ShutdownListener listener);

    default int getPort() {
        return -1;
    }

    /**
     * 根据采集项收集数据
     *
     * @param options  {@link CollectionType}采集项
     * @param consumer 异步回调，采集结束后回调返回{@link JvmmData}，其中有 left 和 right 两个值：
     *                 left  - 为当前还需要等待的异步项数量，如果小于等于0则表示已采集完；
     *                 right - 为当前采集的数据，当采集完时该数据才是有效数据。
     */
    static void collectByOptions(List<CollectionType> options, Consumer<PairKey<AtomicInteger, JvmmData>> consumer) {
        JvmmData res = new JvmmData();
        JvmmCollector collector = JvmmFactory.getCollector();
        AtomicInteger asyncNum = new AtomicInteger(0);
        for (CollectionType type : options) {
            if (type == null) {
                continue;
            }
            switch (type) {
                case process:
                    res.setProcess(collector.getProcess());
                    break;
                case disk:
                    res.setDisk(collector.getDisk());
                    break;
                case disk_io:
                    asyncNum.incrementAndGet();
                    collector.getDiskIO(info -> {
                        res.setDiskIO(info);
                        asyncNum.decrementAndGet();
                        consumer.accept(PairKey.of(asyncNum, res));
                    });
                    break;
                case cpu:
                    asyncNum.incrementAndGet();
                    collector.getCPU(info -> {
                        res.setCpu(info);
                        asyncNum.decrementAndGet();
                        consumer.accept(PairKey.of(asyncNum, res));
                    });
                    break;
                case network:
                    asyncNum.incrementAndGet();
                    collector.getNetwork(info -> {
                        res.setNetwork(info);
                        asyncNum.decrementAndGet();
                        consumer.accept(PairKey.of(asyncNum, res));
                    });
                    break;
                case sys:
                    res.setSys(collector.getSys());
                    break;
                case sys_memory:
                    res.setSysMem(collector.getSysMem());
                    break;
                case sys_file:
                    res.setSysFile(collector.getSysFile());
                    break;
                case jvm_classloading:
                    res.setJvmClassLoading(collector.getJvmClassLoading());
                    break;
                case jvm_classloader:
                    res.setJvmClassLoader(collector.getJvmClassLoaders());
                    break;
                case jvm_compilation:
                    res.setJvmCompilation(collector.getJvmCompilation());
                    break;
                case jvm_gc:
                    res.setJvmGc(collector.getJvmGC());
                    break;
                case jvm_memory:
                    res.setJvmMemory(collector.getJvmMemory());
                    break;
                case jvm_memory_manager:
                    res.setJvmMemoryManager(collector.getJvmMemoryManager());
                    break;
                case jvm_memory_pool:
                    res.setJvmMemoryPool(collector.getJvmMemoryPool());
                    break;
                case jvm_thread:
                    res.setJvmThread(collector.getJvmThread());
                    break;
                case jvm_thread_stack:
                    res.setJvmStack(collector.dumpAllThreads());
                    break;
                case jvm_thread_detail:
                    res.setJvmThreadDetail(collector.getAllJvmThreadDetailInfo());
                    break;
                default:
                    break;
            }
        }
        if (asyncNum.get() <= 0) {
            consumer.accept(PairKey.of(asyncNum, res));
        }
    }
}
