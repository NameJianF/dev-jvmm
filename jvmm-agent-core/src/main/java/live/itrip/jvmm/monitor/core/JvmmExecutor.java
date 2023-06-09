package live.itrip.jvmm.monitor.core;

import live.itrip.jvmm.util.meta.PairKey;
import live.itrip.jvmm.monitor.core.entity.result.JpsResult;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 11:18 2021/5/12
 *
 * @author fengjianfeng
 */
public interface JvmmExecutor {

    void gc();

    void setClassLoadingVerbose(boolean verbose);

    void setMemoryVerbose(boolean verbose);

    void setThreadCpuTimeEnabled(boolean enable);

    void setThreadContentionMonitoringEnabled(boolean enable);

    void resetPeakThreadCount();

    PairKey<List<String>, Boolean> executeJvmTools(String command) throws IOException, TimeoutException, InterruptedException;

    PairKey<List<JpsResult>, String> listJavaProcess();

    File flameProfile(int pid, int sampleSeconds) throws IOException;

    File flameProfile(int pid, int sampleSeconds, String mode) throws IOException;

    void flameProfile(File to, int pid, int sampleSeconds) throws IOException;

    void flameProfile(File to, int pid, int sampleSeconds, String mode) throws IOException;
}
