package live.itrip.jvmm.agent.server.entity.conf;

import com.google.gson.Gson;
import live.itrip.jvmm.agent.common.util.FileUtil;
import live.itrip.jvmm.agent.common.util.GsonUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 16:48 2021/5/22
 *
 * @author fengjianfeng
 */
public final class Configuration {

    private String name = "jvmm-server";
    private ServerConf server = new ServerConf();
    private LogConf log = new LogConf();

    private int workThread = 2;

    /**
     * 从一个地址中载入配置，这个地址需要指定到一个yml文件，配置格式需满足与 Configuration 类一一对应，
     * 这个地址的格式，可以是一个http或https的网络地址，也可以是一个文件路径
     *
     * @param url 文件地址，可以是https(s)地址也可以是本地文件地址
     * @return {@link Configuration}对象
     */
    public static Configuration parseFromUrl(String url) {
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                boolean loaded = FileUtil.readFileFromNet(url, "tmp", "config.yml");
                if (loaded) {
                    return parseFromJsonFile(new File("tmp", "config.yml"));
                } else {
                    throw new RuntimeException("Can not load 'config.yml' from " + url);
                }
            } else {
                File file = new File(url);
                if (file.exists()) {
                    return parseFromJsonFile(file);
                } else {
                    System.err.println("Can not load config from not exist file: " + url);
                    return null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Configuration parseFromStream(InputStream is) {
//        Yaml yaml = new Yaml();
//        yaml.setBeanAccess(BeanAccess.FIELD);
//        return yaml.loadAs(is, Configuration.class);

        return GsonUtils.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), Configuration.class);
    }

    public static Configuration parseFromYamlStr(String jsonStr) {
//        Yaml yaml = new Yaml();
//        yaml.setBeanAccess(BeanAccess.FIELD);
//        return yaml.loadAs(ymlStr, Configuration.class);
        return GsonUtils.fromJson(jsonStr, Configuration.class);
    }

    public static Configuration parseFromJsonFile(File file) throws IOException {
//        Yaml yaml = new Yaml();
//        yaml.setBeanAccess(BeanAccess.FIELD);
//        return yaml.loadAs(Files.newInputStream(file.toPath()), Configuration.class);
        return GsonUtils.fromJson(new FileReader(file), Configuration.class);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getName() {
        return name;
    }

    public Configuration setName(String name) {
        this.name = name;
        return this;
    }

    public ServerConf getServer() {
        return server;
    }

    public Configuration setServer(ServerConf server) {
        this.server = server;
        return this;
    }

    public LogConf getLog() {
        return log;
    }

    public Configuration setLog(LogConf log) {
        this.log = log;
        return this;
    }

    public int getWorkThread() {
        return Math.max(2, workThread);
    }

    public Configuration setWorkThread(int workThread) {
        this.workThread = workThread;
        return this;
    }
}
