package live.itrip.agent.attach;

import com.sun.tools.attach.VirtualMachine;
import sun.jvmstat.monitor.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author fengjianfeng
 * @date 2023/6/13 16:59
 */
public class AttachMain {
    private static final String AGENT_FILE_PATH = "%s/jvmm-agent-bootstrap.jar";

    private static final String AGENT_ATTACH_PARAM = "-Xbootclasspath/a:%s/jvmm-agent-bootstrap.jar";
    private static final String EXT_JAR = "jar";

    public static void main(String[] args) throws Exception {
        System.err.println("------> AttachMain start >-------");
        System.err.println("------> 请在一下JVM进程中选择需要加载Agent的进程ID【PID】，然后输入【Enter】执行");
        System.err.println("------> 或者输入【c】取消");
        // 打印所有 jvm 进程信息
        printAllJvmProcess();

        // 等待用户选择 pid
        Scanner input = new Scanner(System.in);
        String strInput = input.nextLine();
        if (strInput.equalsIgnoreCase("C")) {
            System.err.println("------ AttachMain end -------");
        } else {
            System.err.println("------ input pid -> " + strInput + ",执行Attach任务");

            attachAgent(strInput);
        }

        System.err.println("------ AttachMain end -------");
    }

    private static void printAllJvmProcess() throws URISyntaxException, MonitorException {
        // 获取监控主机
        MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
        // 取得所有在活动的虚拟机集合
        Set<?> vmlist = new HashSet<Object>(local.activeVms());
        // 遍历集合，输出PID和进程名
        for (Object process : vmlist) {
            MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + process));
            // 获取类名
            String processname = MonitoredVmUtil.mainClass(vm, true);
            System.out.println(process + " ------> " + processname);
        }

    }

    private static void attachAgent(String jvmPid) {
        if (jvmPid != null && jvmPid.length() > 0) {
            String currentDir = System.getProperty("user.dir"); //user.dir指定了当前的路径
            System.err.println("------> currentDir: " + currentDir);

            // currentDir = "/Users/fengjianfeng/Desktop/jvmm";

            String pathFile = String.format(AGENT_FILE_PATH, currentDir);
            System.err.println("------> AGENT_FILE_PATH: " + pathFile);
            File agentFile = new File(pathFile);
            if (agentFile.isFile()) {
                String agentFileName = agentFile.getName();
                String agentFileExtension = agentFileName.substring(agentFileName.lastIndexOf(".") + 1);
                if (agentFileExtension.equalsIgnoreCase(EXT_JAR)) {
                    try {
                        System.out.println("Attaching to target JVM with PID: " + jvmPid);

                        System.err.println("------> AGENT_ATTACH_PARAM: " + String.format(AGENT_ATTACH_PARAM, currentDir));
                        VirtualMachine jvm = VirtualMachine.attach(jvmPid);
                        jvm.loadAgent(agentFile.getAbsolutePath());
//                        jvm.loadAgent(agentFile.getAbsolutePath(), String.format(AGENT_ATTACH_PARAM, currentDir));
                        jvm.detach();
                        System.out.println("Attached to target JVM and loaded Java agent successfully");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            System.out.println("Target JVM running demo Java application not found");
        }
    }
}
