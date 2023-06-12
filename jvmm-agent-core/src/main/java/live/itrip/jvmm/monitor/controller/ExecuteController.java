package live.itrip.jvmm.monitor.controller;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import live.itrip.jvmm.JvmmAgentContext;
import live.itrip.jvmm.agent.service.AgentContext;
import live.itrip.jvmm.agent.utils.StringUtils;
import live.itrip.jvmm.logging.AgentLogFactory;
import live.itrip.jvmm.monitor.core.JvmmFactory;
import live.itrip.jvmm.monitor.core.Unsafe;
import live.itrip.jvmm.monitor.core.entity.dto.PatchDTO;
import live.itrip.jvmm.monitor.core.entity.result.JpsResult;
import live.itrip.jvmm.monitor.core.entity.vo.PatchVO;
import live.itrip.jvmm.server.handler.AbstractCommandHandler;
import live.itrip.jvmm.util.CodingUtil;
import live.itrip.jvmm.util.CommonUtil;
import live.itrip.jvmm.util.GsonUtils;
import live.itrip.jvmm.util.meta.PairKey;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.ClassDefinition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 5:55 下午 2021/5/30
 *
 * @author fengjianfeng
 */
public class ExecuteController extends AbstractCommandHandler implements HttpHandler {
    private static final Logger LOGGER = AgentLogFactory.getLogger(ExecuteController.class);

    private static final String PATH_EXECUTE_GC = "/execute/gc";
    private static final String PATH_EXECUTE_SET_CLASSLOADING_VERBOSE = "/execute/set_classloading_verbose";
    private static final String PATH_EXECUTE_SET_MEMORY_VERBOSE = "/execute/set_memory_verbose";
    private static final String PATH_EXECUTE_SET_THREAD_CPU_TIME_ENABLED = "/execute/set_thread_cpu_time_enabled";
    private static final String PATH_EXECUTE_SET_THREAD_CONTENTION_MONITOR_ENABLED = "/execute/set_thread_contention_monitor_enabled";
    private static final String PATH_EXECUTE_RESET_PEAK_THREAD_COUNT = "/execute/reset_peak_thread_count";
    private static final String PATH_EXECUTE_JPS = "/execute/jps";
    private static final String PATH_EXECUTE_JVM_TOOL = "/execute/jvm_tool";
    private static final String PATH_EXECUTE_JAD = "/execute/jad";
    private static final String PATH_EXECUTE_LOAD_PATCH = "/execute/load_patch";

    /**
     * 注册 url 路径
     *
     * @param server server
     */
    public void registryPath(HttpServer server) {
        server.createContext(PATH_EXECUTE_GC, this);
        server.createContext(PATH_EXECUTE_SET_CLASSLOADING_VERBOSE, this);
        server.createContext(PATH_EXECUTE_SET_MEMORY_VERBOSE, this);
        server.createContext(PATH_EXECUTE_SET_THREAD_CPU_TIME_ENABLED, this);
        server.createContext(PATH_EXECUTE_SET_THREAD_CONTENTION_MONITOR_ENABLED, this);
        server.createContext(PATH_EXECUTE_RESET_PEAK_THREAD_COUNT, this);
        server.createContext(PATH_EXECUTE_JPS, this);
        server.createContext(PATH_EXECUTE_JVM_TOOL, this);
        server.createContext(PATH_EXECUTE_JAD, this);
        server.createContext(PATH_EXECUTE_LOAD_PATCH, this);
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
                handlePost(httpExchange, contextPath, query);
                break;
            case METHOD_GET:
                try {
                    handleGet(httpExchange, contextPath, query);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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

    private void handleGet(HttpExchange httpExchange, String contextPath, String query) throws Exception {
        switch (contextPath) {
            case PATH_EXECUTE_GC:
                gc(httpExchange);
                break;
            case PATH_EXECUTE_SET_CLASSLOADING_VERBOSE:
                setClassLoadingVerbose(httpExchange, query);
                break;
            case PATH_EXECUTE_SET_MEMORY_VERBOSE:
                setMemoryVerbose(httpExchange, query);
                break;
            case PATH_EXECUTE_SET_THREAD_CPU_TIME_ENABLED:
                setThreadCpuTimeEnabled(httpExchange, query);
                break;
            case PATH_EXECUTE_SET_THREAD_CONTENTION_MONITOR_ENABLED:
                setThreadContentionMonitoringEnabled(httpExchange, query);
                break;
            case PATH_EXECUTE_RESET_PEAK_THREAD_COUNT:
                resetPeakThreadCount(httpExchange);
                break;
            case PATH_EXECUTE_JPS:
                listJavaProcess(httpExchange);
                break;
            default:
                break;
        }
    }

    private void handlePost(HttpExchange httpExchange, String contextPath, String query) throws IOException {
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
                if (PATH_EXECUTE_JVM_TOOL.equalsIgnoreCase(contextPath)) {
                    jvmTool(httpExchange, requestBody.toString());
                    return;
                } else if (PATH_EXECUTE_JAD.equalsIgnoreCase(contextPath)) {
                    jad(httpExchange, requestBody.toString(), query);
                    return;
                } else if (PATH_EXECUTE_LOAD_PATCH.equalsIgnoreCase(contextPath)) {
                    loadPatch(httpExchange, requestBody.toString());
                    return;
                }

            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        this.sendFailed(httpExchange);
    }

    public void gc(HttpExchange httpExchange) throws IOException {
        JvmmFactory.getExecutor().gc();
        this.sendSucceed(httpExchange);
    }

    public void setClassLoadingVerbose(HttpExchange httpExchange, String query) throws IOException {
        HashMap<String, Object> params = this.parseQueryParams(query);
        boolean verbose = (boolean) params.get("verbose");
        JvmmFactory.getExecutor().setClassLoadingVerbose(verbose);
        this.sendSucceed(httpExchange);
    }

    public void setMemoryVerbose(HttpExchange httpExchange, String query) throws IOException {
        HashMap<String, Object> params = this.parseQueryParams(query);
        boolean verbose = (boolean) params.get("verbose");
        JvmmFactory.getExecutor().setMemoryVerbose(verbose);
        this.sendSucceed(httpExchange);
    }

    public void setThreadCpuTimeEnabled(HttpExchange httpExchange, String query) throws IOException {
        HashMap<String, Object> params = this.parseQueryParams(query);
        boolean verbose = (boolean) params.get("verbose");
        JvmmFactory.getExecutor().setThreadCpuTimeEnabled(verbose);
        this.sendSucceed(httpExchange);
    }

    public void setThreadContentionMonitoringEnabled(HttpExchange httpExchange, String query) throws IOException {
        HashMap<String, Object> params = this.parseQueryParams(query);
        boolean verbose = (boolean) params.get("verbose");
        JvmmFactory.getExecutor().setThreadContentionMonitoringEnabled(verbose);
        this.sendSucceed(httpExchange);
    }

    public void resetPeakThreadCount(HttpExchange httpExchange) throws IOException {
        JvmmFactory.getExecutor().resetPeakThreadCount();
        this.sendSucceed(httpExchange);
    }

    public void listJavaProcess(HttpExchange httpExchange) throws Exception {
        JsonArray result = new JsonArray();
        PairKey<List<JpsResult>, String> pair = JvmmFactory.getExecutor().listJavaProcess();
        if (pair.getRight() == null) {
            pair.getLeft().forEach(o -> result.add(GsonUtils.toJson(o)));
        } else {
            throw new Exception(pair.getRight());
        }

        if (result.size() > 0) {
            this.sendSucceed(httpExchange, result);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    public void jvmTool(HttpExchange httpExchange, String requestBody) throws Exception {
        Object result;
        PairKey<List<String>, Boolean> pair = JvmmFactory.getExecutor().executeJvmTools(requestBody);
        if (pair.getRight()) {
            result = pair.getLeft();
        } else {
            result = CommonUtil.join("\n", pair.getLeft());
        }

        this.sendSucceed(httpExchange, result);
    }

    public void jad(HttpExchange httpExchange, String query, String requestBody) throws IOException {
//        String ret = null;
//        try {
//            HashMap<String, Object> params = this.parseQueryParams(query);
//            String className = (String) params.get("className");
//            String methodName = (String) params.get("methodName");
//            this.sendSucceed(httpExchange, ret);
//
//            ret = JvmmFactory.getExecutor().jad(JvmmAgentContext.getContext().getInstrumentation(), className, methodName);
//            this.sendSucceed(httpExchange, ret);
//        } catch (UnsupportedEncodingException e) {
////            throw new RuntimeException(e);
//            ret = e.getMessage();
//            this.sendFailed(httpExchange, -1, ret);
//        } catch (IOException e) {
////            throw new RuntimeException(e);
//            ret = e.getMessage();
//            this.sendFailed(httpExchange, -1, ret);
//        }

        this.sendSucceed(httpExchange);
    }

    public void loadPatch(HttpExchange httpExchange, String requestBody) throws Exception {
        List<PatchDTO> patchList = GsonUtils.fromJson(requestBody, new TypeToken<List<PatchDTO>>() {
        }.getType());

        List<ClassDefinition> definitions = new ArrayList<>(patchList.size());
        List<PatchVO> resp = new ArrayList<>(patchList.size());
        for (PatchDTO patch : patchList) {
            byte[] classBytes = CodingUtil.hexStr2Bytes(patch.getHex());
            if (patch.getClassLoaderHash() == null) {
                List<Class<?>> loadedClass = Unsafe.findLoadedClasses(patch.getClassName());
                for (Class<?> clazz : loadedClass) {
                    definitions.add(new ClassDefinition(clazz, classBytes));
                    resp.add(new PatchVO().setClassName(patch.getClassName()).setClassLoaderHash(clazz.getClassLoader().hashCode()));
                }
            } else {
                Class<?> clazz = Unsafe.findLoadedClass(patch.getClassLoaderHash(), patch.getClassName());
                if (clazz != null) {
                    definitions.add(new ClassDefinition(clazz, classBytes));
                    resp.add(new PatchVO().setClassName(patch.getClassName()).setClassLoaderHash(clazz.getClassLoader().hashCode()));
                }
            }
        }
        if (JvmmAgentContext.getContext().redefineClass(definitions.toArray(new ClassDefinition[0]))) {
            // return resp;
            this.sendSucceed(httpExchange, resp);
        }
//        return new ArrayList<>();
        this.sendSucceed(httpExchange, new ArrayList<>());
    }
}
