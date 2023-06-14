package live.itrip.jvmm.service;

import live.itrip.jvmm.JvmmAgentContext;
import live.itrip.jvmm.logging.AgentLogFactory;

import java.lang.instrument.Instrumentation;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author : fengjianfeng
 * @date : 2021-07-23 11:34
 * description : LauncherImpl
 **/
public class Launcher {
    private static final Logger LOGGER = AgentLogFactory.getLogger(Launcher.class);
    private final Instrumentation instrumentation;
    private final String rootPath;

    private Launcher(Instrumentation inst, String rootPath) {
        this.instrumentation = inst;
        this.rootPath = rootPath;
    }

    private static volatile Launcher launcher;

    public static Launcher getInstance(Instrumentation inst, String rootPath) {
        if (launcher == null) {
            synchronized (Launcher.class) {
                if (launcher == null) {
                    launcher = new Launcher(inst, rootPath);
                }
            }
        }

        return launcher;
    }


    /**
     * Agent launcher
     *
     * @return context
     */
    public JvmmAgentContext launch(Function<Object, Object> callback) {
        LOGGER.info(" create JvmmAgentContext by launch.");
        JvmmAgentContext context = JvmmAgentContext.getInstance();

        // 初始化数据
        context.init(instrumentation, rootPath);

        // 启动 http server
        context.startHttpServer();

        // 上报数据
        context.reportAgentAddress();
        
        callback.apply("start succeed.");
        return context;
    }
}
