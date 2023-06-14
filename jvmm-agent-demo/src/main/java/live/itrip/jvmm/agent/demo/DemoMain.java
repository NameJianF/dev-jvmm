package live.itrip.jvmm.agent.demo;

/**
 * @author fengjianfeng
 * @version v1.0
 * @date 2021/2/19 17:03
 * description demo项目入口
 */
public class DemoMain {

  // -javaagent:/Users/fengjianfeng/Desktop/jvmm/jvmm-agent-1.0.0.jar -Xbootclasspath/a:/Users/fengjianfeng/Desktop/jvmm/jvmm-agent-1.0.0.jar -Djvmm.app.env=test -Djvmm.app.name=jvmm-demo -Djvmm.server.address=10.222.76.29:8080
    public static void main(String[] args) throws Exception {
        System.err.println("------ demo start -------");
        for (int i = 0; i < 2000; i++) {
            System.err.println("------< count " + i + " >------ ");

            Thread.sleep(1000);
        }

        System.err.println("------ end -------");
    }

}
