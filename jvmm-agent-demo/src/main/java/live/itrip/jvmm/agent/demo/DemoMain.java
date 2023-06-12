package live.itrip.jvmm.agent.demo;

/**
 * @author fengjianfeng
 * @version v1.0
 * @date 2021/2/19 17:03
 * description demo项目入口
 */
public class DemoMain {

  // -javaagent:/Users/fengjianfeng/Desktop/jvmm/jvmm-agent-1.0.0.jar -Xbootclasspath/a:/Users/fengjianfeng/Desktop/jvmm/jvmm-agent-1.0.0.jar -Djvmm.app.env=test -Djvmm.app.name=jvmm-demo -Djvmm.server.address=10.222.76.29:8080
    private static String jvmPid = null;
    private static final String AGENT_FILE_PATH = "-javaagent:/Users/fengjianfeng/Documents/test-gateway/cube-agent-client-1.0.jar=jdd-gateway";
    private static final String EXT_JAR = "jar";

    public static void main(String[] args) throws Exception {
        System.err.println("------ demo start -------");
        for (int i = 0; i < 2000; i++) {
            System.err.println("------< count " + i + " >------ ");

            Thread.sleep(1000);
        }

        System.err.println("------ end -------");
    }

    private static void attachAgent() {
//        if (jvmPid != null) {
//            File agentFile = new File(AGENT_FILE_PATH);
//            if (agentFile.isFile()) {
//                String appName = "jr-gateway";
//                String agentFileName = agentFile.getName();
//                String agentFileExtension = agentFileName.substring(agentFileName.lastIndexOf(".") + 1);
//                if (agentFileExtension.equalsIgnoreCase(EXT_JAR)) {
//                    try {
//                        System.out.println("Attaching to target JVM with PID: " + jvmPid);
//                        VirtualMachine jvm = VirtualMachine.attach(jvmPid);
//                        jvm.loadAgent(agentFile.getAbsolutePath(), appName);
//                        jvm.detach();
//                        System.out.println("Attached to target JVM and loaded Java agent successfully");
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        } else {
//            System.out.println("Target JVM running demo Java application not found");
//        }
    }
}
