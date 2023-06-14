package live.itrip.jvmm.monitor.controller;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import live.itrip.jvmm.logging.AgentLogFactory;
import live.itrip.jvmm.monitor.core.JvmmFactory;
import live.itrip.jvmm.monitor.core.Unsafe;
import live.itrip.jvmm.monitor.core.entity.dto.ThreadInfoDTO;
import live.itrip.jvmm.monitor.core.entity.info.*;
import live.itrip.jvmm.server.handler.AbstractCommandHandler;
import live.itrip.jvmm.util.GsonUtils;
import live.itrip.jvmm.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 5:17 下午 2021/5/30
 *
 * @author fengjianfeng
 */
public class CollectController extends AbstractCommandHandler implements HttpHandler {
    private static final java.util.logging.Logger LOGGER = AgentLogFactory.getLogger(CollectController.class);

    private static final String PATH_COLLECT_PROCESS = "/collect/process";
    private static final String PATH_COLLECT_DISK = "/collect/disk";
    private static final String PATH_COLLECT_DISK_IO = "/collect/disk_io";
    private static final String PATH_COLLECT_CPU = "/collect/cpu";
    private static final String PATH_COLLECT_NETWORK = "/collect/network";
    private static final String PATH_COLLECT_SYS = "/collect/sys";
    private static final String PATH_COLLECT_SYS_MEMORY = "/collect/sys/memory";
    private static final String PATH_COLLECT_SYS_FILE = "/collect/sys/file";
    private static final String PATH_COLLECT_JVM_CLASSLOADING = "/collect/jvm/classloading";
    private static final String PATH_COLLECT_JVM_CLASSLOADER = "/collect/jvm/classloader";
    private static final String PATH_COLLECT_JVM_COMPILATION = "/collect/jvm/compilation";
    private static final String PATH_COLLECT_JVM_GC = "/collect/jvm/gc";
    private static final String PATH_COLLECT_JVM_MEMORY_MANAGER = "/collect/jvm/memory_manager";
    private static final String PATH_COLLECT_JVM_MEMORY_POOL = "/collect/jvm/memory_pool";
    private static final String PATH_COLLECT_JVM_MEMORY = "/collect/jvm/memory";
    private static final String PATH_COLLECT_JVM_THREAD = "/collect/jvm/thread";
    private static final String PATH_COLLECT_JVM_THREAD_STACK = "/collect/jvm/thread_stack";
    private static final String PATH_COLLECT_JVM_THREAD_DETAIL = "/collect/jvm/thread_detail";
    private static final String PATH_COLLECT_JVM_DUMP_THREAD = "/collect/jvm/dump_thread";
    private static final String PATH_COLLECT_JVM_THREAD_POOL = "/collect/jvm/thread_pool";
    private static final String PATH_COLLECT_JVM_BY_OPTIONS = "/collect/jvm/by_options";

    /**
     * 注册 url 路径
     *
     * @param server server
     */
    public void registryPath(HttpServer server) {
        server.createContext(CollectController.PATH_COLLECT_PROCESS, this);
        server.createContext(CollectController.PATH_COLLECT_DISK, this);
        server.createContext(CollectController.PATH_COLLECT_DISK_IO, this);
        server.createContext(CollectController.PATH_COLLECT_CPU, this);
        server.createContext(CollectController.PATH_COLLECT_NETWORK, this);
        server.createContext(CollectController.PATH_COLLECT_SYS, this);
        server.createContext(CollectController.PATH_COLLECT_SYS_MEMORY, this);
        server.createContext(CollectController.PATH_COLLECT_SYS_FILE, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_CLASSLOADING, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_CLASSLOADER, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_COMPILATION, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_GC, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_MEMORY_MANAGER, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_MEMORY_POOL, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_MEMORY, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_THREAD, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_THREAD_STACK, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_THREAD_DETAIL, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_DUMP_THREAD, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_THREAD_POOL, this);
        server.createContext(CollectController.PATH_COLLECT_JVM_BY_OPTIONS, this);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getRawQuery();
        String contextPath = httpExchange.getHttpContext().getPath();
        LOGGER.info("query:" + query);
        LOGGER.info("context path:" + contextPath);
        LOGGER.info("url:" + httpExchange.getRequestURI().getPath());
        String method = httpExchange.getRequestMethod().toUpperCase();
        switch (method) {
            case METHOD_POST:
                handlePost(httpExchange, contextPath);
                break;
            case METHOD_GET:
                handleGet(httpExchange, contextPath, query);
                break;
//            case METHOD_DELETE:
//                handleDelete();
//                break;
//            case METHOD_PUT:
//                handlePut();
//            break;
            default:
                break;
        }
    }

    private void handleGet(HttpExchange httpExchange, String contextPath, String query) throws IOException {
        switch (contextPath) {
            case PATH_COLLECT_PROCESS:
                getProcessInfo(httpExchange);
                break;
            case PATH_COLLECT_DISK:
                getDiskInfo(httpExchange);
                break;
            case PATH_COLLECT_DISK_IO:
                getDiskIOInfo(httpExchange);
                break;
            case PATH_COLLECT_CPU:
                getCPUInfo(httpExchange);
                break;
            case PATH_COLLECT_NETWORK:
                getNetInfo(httpExchange);
                break;
            case PATH_COLLECT_SYS:
                getSysInfo(httpExchange);
                break;
            case PATH_COLLECT_SYS_MEMORY:
                getSysMemoryInfo(httpExchange);
                break;
            case PATH_COLLECT_SYS_FILE:
                getSysFileInfo(httpExchange);
                break;
            case PATH_COLLECT_JVM_CLASSLOADING:
                getJvmClassLoadingInfo(httpExchange);
                break;
            case PATH_COLLECT_JVM_CLASSLOADER:
                getJvmClassLoaders(httpExchange);
                break;
            case PATH_COLLECT_JVM_COMPILATION:
                getJvmCompilationInfo(httpExchange);
                break;
            case PATH_COLLECT_JVM_GC:
                getJvmGCInfo(httpExchange);
                break;
            case PATH_COLLECT_JVM_MEMORY_MANAGER:
                getJvmMemoryManagerInfo(httpExchange);
                break;
            case PATH_COLLECT_JVM_MEMORY_POOL:
                getJvmMemoryPoolInfo(httpExchange);
                break;
            case PATH_COLLECT_JVM_MEMORY:
                getJvmMemoryInfo(httpExchange);
                break;
            case PATH_COLLECT_JVM_THREAD:
                getJvmThreadInfo(httpExchange);
                break;
            case PATH_COLLECT_JVM_THREAD_DETAIL:
                getJvmThreadDetail(httpExchange, query);
                break;
            case PATH_COLLECT_JVM_DUMP_THREAD:
                jvmDumpThread(httpExchange);
                break;
            case PATH_COLLECT_JVM_THREAD_POOL:
                getThreadPoolInfo(httpExchange, query);
                break;
            case PATH_COLLECT_JVM_BY_OPTIONS:
                collectBatch(httpExchange);
                break;
            default:
                break;
        }
    }

    private void handlePost(HttpExchange httpExchange, String contextPath) throws IOException {
        // request body
        StringBuilder requestBody = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), Charsets.UTF_8)) {
            char[] buffer = new char[256];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                requestBody.append(buffer, 0, read);
            }
            // LOGGER.info(" requestBody ---> " + requestBody.toString());
            if (StringUtils.isNotEmpty(requestBody.toString())) {
                if (PATH_COLLECT_JVM_THREAD_STACK.equalsIgnoreCase(contextPath)) {
                    getJvmThreadStack(httpExchange, requestBody.toString());
                    return;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        this.sendFailed(httpExchange);
    }

    public void getProcessInfo(HttpExchange httpExchange) throws IOException {
        ProcessInfo processInfo = JvmmFactory.getCollector().getProcess();
        if (!Objects.isNull(processInfo)) {
            this.sendSucceed(httpExchange, processInfo);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getDiskInfo(HttpExchange httpExchange) throws IOException {
        List<DiskInfo> list = JvmmFactory.getCollector().getDisk();
        if (!Objects.isNull(list)) {
            this.sendSucceed(httpExchange, list);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getDiskIOInfo(HttpExchange httpExchange) throws IOException {
        List<DiskIOInfo> diskIOInfos = JvmmFactory.getCollector().getDiskIO();

        if (!Objects.isNull(diskIOInfos)) {
            this.sendSucceed(httpExchange, diskIOInfos);
        } else {
            this.sendFailed(httpExchange);
        }

        this.sendSucceed(httpExchange);
    }

    public void getCPUInfo(HttpExchange httpExchange) throws IOException {

        CPUInfo info = JvmmFactory.getCollector().getCPU();

        if (!Objects.isNull(info)) {
            this.sendSucceed(httpExchange, info);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getNetInfo(HttpExchange httpExchange) throws IOException {

        NetInfo info = JvmmFactory.getCollector().getNetwork();

        if (!Objects.isNull(info)) {
            this.sendSucceed(httpExchange, info);
        } else {
            this.sendFailed(httpExchange);
        }
        this.sendSucceed(httpExchange);
    }

    public void getSysInfo(HttpExchange httpExchange) throws IOException {
        SysInfo sysInfo = JvmmFactory.getCollector().getSys();
        if (!Objects.isNull(sysInfo)) {
            this.sendSucceed(httpExchange, sysInfo);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getSysMemoryInfo(HttpExchange httpExchange) throws IOException {
        SysMemInfo sysMemInfo = JvmmFactory.getCollector().getSysMem();
        if (!Objects.isNull(sysMemInfo)) {
            this.sendSucceed(httpExchange, sysMemInfo);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getSysFileInfo(HttpExchange httpExchange) throws IOException {
        List<SysFileInfo> sysFileInfoList = JvmmFactory.getCollector().getSysFile();
        if (!Objects.isNull(sysFileInfoList)) {
            this.sendSucceed(httpExchange, sysFileInfoList);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getJvmClassLoadingInfo(HttpExchange httpExchange) throws IOException {
        JvmClassLoadingInfo jvmClassLoadingInfo = JvmmFactory.getCollector().getJvmClassLoading();
        if (!Objects.isNull(jvmClassLoadingInfo)) {
            this.sendSucceed(httpExchange, jvmClassLoadingInfo);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getJvmClassLoaders(HttpExchange httpExchange) throws IOException {
        List<JvmClassLoaderInfo> list = JvmmFactory.getCollector().getJvmClassLoaders();
        if (!Objects.isNull(list)) {
            this.sendSucceed(httpExchange, list);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getJvmCompilationInfo(HttpExchange httpExchange) throws IOException {
        JvmCompilationInfo info = JvmmFactory.getCollector().getJvmCompilation();
        if (!Objects.isNull(info)) {
            this.sendSucceed(httpExchange, info);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getJvmGCInfo(HttpExchange httpExchange) throws IOException {
        List<JvmGCInfo> list = JvmmFactory.getCollector().getJvmGC();
        if (!Objects.isNull(list)) {
            this.sendSucceed(httpExchange, list);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getJvmMemoryManagerInfo(HttpExchange httpExchange) throws IOException {
        List<JvmMemoryManagerInfo> list = JvmmFactory.getCollector().getJvmMemoryManager();
        if (!Objects.isNull(list)) {
            this.sendSucceed(httpExchange, list);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getJvmMemoryPoolInfo(HttpExchange httpExchange) throws IOException {
        List<JvmMemoryPoolInfo> list = JvmmFactory.getCollector().getJvmMemoryPool();
        if (!Objects.isNull(list)) {
            this.sendSucceed(httpExchange, list);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getJvmMemoryInfo(HttpExchange httpExchange) throws IOException {
        JvmMemoryInfo info = JvmmFactory.getCollector().getJvmMemory();
        if (!Objects.isNull(info)) {
            this.sendSucceed(httpExchange, info);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getJvmThreadInfo(HttpExchange httpExchange) throws IOException {
        JvmThreadInfo info = JvmmFactory.getCollector().getJvmThread();
        if (!Objects.isNull(info)) {
            this.sendSucceed(httpExchange, info);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    //    @HttpRequest(value = "/collect/jvm/thread_stack", method = Method.POST)
    public void getJvmThreadStack(HttpExchange httpExchange, String requestBody) throws IOException {
        ThreadInfoDTO data = GsonUtils.fromJson(requestBody, ThreadInfoDTO.class);
        if (data == null) {
            throw new IllegalArgumentException("Missing data");
        }

        if (data.getIdArr() == null || data.getIdArr().length == 0) {
            throw new IllegalArgumentException("Missing a parameter 'id' of type list");
        }

        String[] infos = JvmmFactory.getCollector().getJvmThreadStack(data.getIdArr(), data.getDepth());
//        return new ArrayList<>(Arrays.asList(infos));
        if (!Objects.isNull(infos)) {
            this.sendSucceed(httpExchange, infos);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void getJvmThreadDetail(HttpExchange httpExchange, String query) throws IOException {
        // @RequestParam long[] id
        HashMap<String, Object> params = this.parseQueryParams(query);
        long[] id = (long[]) params.get("id");

        JvmThreadDetailInfo[] array = null;
        if (id == null || id.length == 0) {
            array = JvmmFactory.getCollector().getAllJvmThreadDetailInfo();
        } else {
            array = JvmmFactory.getCollector().getJvmThreadDetailInfo(id);
        }

        if (!Objects.isNull(array)) {
            this.sendSucceed(httpExchange, array);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void jvmDumpThread(HttpExchange httpExchange) throws IOException {
        String[] dump = JvmmFactory.getCollector().dumpAllThreads();
        JsonArray result = new JsonArray();
        for (String info : dump) {
            if (info != null) {
                result.add(info);
            }
        }
        this.sendSucceed(httpExchange, result);
    }

    //    @HttpRequest("/collect/jvm/thread_pool")
    public void getThreadPoolInfo(HttpExchange httpExchange, String query) throws IOException {
//        @RequestParam int classLoaderHash, @RequestParam String clazz,
//        @RequestParam String instanceField, @RequestParam String field
        HashMap<String, Object> params = this.parseQueryParams(query);
        int classLoaderHash = (int) params.get("classLoaderHash");
        String clazz = (String) params.get("clazz");
        String instanceField = (String) params.get("instanceField");
        String field = (String) params.get("field");

        ThreadPoolInfo threadPoolInfo = null;
        ClassLoader classLoader = Unsafe.getClassLoader(classLoaderHash);
        if (classLoader == null) {
            LOGGER.info("Can not found target ClassLoader by hashcode.");
        }
        if (StringUtils.isEmpty(instanceField)) {
            threadPoolInfo = JvmmFactory.getCollector().getThreadPoolInfo(classLoader, clazz, field);
        } else {
            threadPoolInfo = JvmmFactory.getCollector().getThreadPoolInfo(classLoader, clazz, instanceField, field);
        }
        if (!Objects.isNull(threadPoolInfo)) {
            this.sendSucceed(httpExchange, threadPoolInfo);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    //    @HttpRequest(value = "/collect/by_options")
    public void collectBatch(HttpExchange httpExchange) throws IOException {
        // @RequestParam List<CollectionType> options, ResponseFuture future
//        JvmmService.collectByOptions(options, pair -> {
//            if (pair.getLeft().get() <= 0) {
//                future.apply(pair.getRight());
//            }
//        });

        this.sendSucceed(httpExchange);
    }
}