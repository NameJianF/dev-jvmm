package live.itrip.jvmm.service;

import live.itrip.jvmm.JvmmAgentContext;
import live.itrip.jvmm.logging.AgentLogFactory;
import live.itrip.jvmm.agent.service.AgentContext;
import live.itrip.jvmm.agent.service.Launcher;

import java.util.logging.Logger;

/**
 * @author : fengjianfeng
 * @date : 2021-07-23 11:34
 * description : LauncherImpl
 **/
public class LauncherImpl implements Launcher {
    private static final Logger LOGGER = AgentLogFactory.getLogger(LauncherImpl.class);

    /**
     * Agent launcher
     *
     * @return context
     */
    @Override
    public AgentContext launch() {
        LOGGER.info(" create AgentContext by launch.");
        JvmmAgentContext context = JvmmAgentContext.getContext();

        // 启动 http server
        context.startHttpServer();

        return (AgentContext) context;
    }
}
