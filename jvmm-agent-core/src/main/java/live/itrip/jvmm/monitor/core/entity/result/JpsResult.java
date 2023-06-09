package live.itrip.jvmm.monitor.core.entity.result;



import live.itrip.jvmm.util.GsonUtils;

import java.util.List;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 17:08 2021/5/12
 *
 * @author fengjianfeng
 */
public class JpsResult {
    private long pid;
    private String mainClass;
    private List<String> arguments;

    private JpsResult(){
    }

    public static JpsResult create(){
        return new JpsResult();
    }

    public long getPid() {
        return pid;
    }

    public String getMainClass() {
        return mainClass;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public JpsResult setPid(long pid) {
        this.pid = pid;
        return this;
    }

    public JpsResult setMainClass(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public JpsResult setArguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
