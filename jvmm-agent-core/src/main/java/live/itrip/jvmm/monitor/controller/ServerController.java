//package live.itrip.jvmm.monitor.controller;
//
//import live.itrip.jvmm.agent.common.util.AssertUtil;
//import live.itrip.jvmm.agent.convey.annotation.HttpController;
//import live.itrip.jvmm.agent.convey.annotation.HttpRequest;
//import live.itrip.jvmm.agent.convey.annotation.JvmmController;
//import live.itrip.jvmm.agent.convey.annotation.JvmmMapping;
//import live.itrip.jvmm.agent.convey.annotation.RequestParam;
//import live.itrip.jvmm.agent.convey.enums.GlobalType;
//import live.itrip.jvmm.agent.server.ServerContext;
//import live.itrip.jvmm.agent.server.enums.ServerType;
//
///**
// * <p>
// * Description: TODO
// * </p>
// * <p>
// * Created in 10:58 下午 2021/5/31
// *
// * @author fengjianfeng
// */
//@JvmmController
//@HttpController
//public class ServerController {
//
//    @JvmmMapping(typeEnum = GlobalType.JVMM_TYPE_HEARTBEAT)
//    public void heartbeat() {
//    }
//
//    @JvmmMapping(typeEnum = GlobalType.JVMM_TYPE_SERVER_SHUTDOWN)
//    @HttpRequest("/server/shutdown")
//    public String shutdown(@RequestParam String target) {
//        AssertUtil.checkArguments(target != null, "Missing required param 'target'");
//        ServerType type = ServerType.of(target);
//        if (ServerContext.stop(type)) {
//            return "ok";
//        } else {
//            return "Service not running";
//        }
//    }
//}
