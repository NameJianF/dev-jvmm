package live.itrip.jvmm.server.handler;

import com.google.common.base.Charsets;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import live.itrip.jvmm.logging.AgentLogFactory;
import live.itrip.jvmm.server.CommandResult;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : ERP --> fengjianfeng4
 * @date : 2021-08-16 21:41
 * description : 操作命令的接收和转发
 * 1。 操作命令
 * 2。 healthy 心跳检测
 * 3。 debug 开关
 **/
public class HttpCommandHandler extends AbstractCommandHandler implements HttpHandler {
    private static final Logger LOGGER = AgentLogFactory.getLogger(HttpCommandHandler.class);

    private static final String CONTEXT_PATH_ROOT = "/";
    private static final String CONTEXT_PATH_COMMAND = "/cmd";
    private static final String CONTEXT_PATH_HEALTHY_CHECK = "/-/healthy";
    private static final String CONTEXT_PATH_DEBUG_OPEN = "/-/openDebug";
    private static final String CONTEXT_PATH_DEBUG_CLOSE = "/-/closeDebug";

    private final CommandListener commandListener;

    public HttpCommandHandler(CommandListener commandListener) {
        this.commandListener = commandListener;
    }

    /**
     * 注册 url 路径
     *
     * @param server server
     */
    @Override
    public void registryPath(HttpServer server) {
        server.createContext(HttpCommandHandler.CONTEXT_PATH_ROOT, this);
        server.createContext(HttpCommandHandler.CONTEXT_PATH_HEALTHY_CHECK, this);
        server.createContext(HttpCommandHandler.CONTEXT_PATH_DEBUG_OPEN, this);
        server.createContext(HttpCommandHandler.CONTEXT_PATH_DEBUG_CLOSE, this);
        server.createContext(HttpCommandHandler.CONTEXT_PATH_COMMAND, this);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getRawQuery();
        String contextPath = httpExchange.getHttpContext().getPath();
        LOGGER.info("query:" + query);
        LOGGER.info("context path:" + contextPath);
        LOGGER.info("url:" + httpExchange.getRequestURI().getPath());

        // token 判断，拦截非法请求
//        List<String> list = httpExchange.getRequestHeaders().get("token");
//        if (list != null && list.size() > 0) {
//            String token = list.get(0);
//            if (Strings.isNullOrEmpty(token) || ClientAgentContext.getContext().getToken().equals(token)) {
//                this.sendFailed(httpExchange, -1, "Agent token error.");
//                return;
//            }
//        }

        if (CONTEXT_PATH_HEALTHY_CHECK.equals(contextPath)) {
            // healthy check
            this.healthyCheck(httpExchange);
        } else if (CONTEXT_PATH_DEBUG_OPEN.equals(contextPath)) {
            // openDebug
            this.openDebug(httpExchange);
        } else if (CONTEXT_PATH_DEBUG_CLOSE.equals(contextPath)) {
            // closeDebug
            this.closeDebug(httpExchange);
        } else if (CONTEXT_PATH_COMMAND.equals(contextPath)) {
            String msg;
            String method = httpExchange.getRequestMethod();
            if (METHOD_POST.equals(method)) {
                // request body
                StringBuilder requestBody = new StringBuilder();
                try (InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), Charsets.UTF_8)) {
                    char[] buffer = new char[256];
                    int read;
                    while ((read = reader.read(buffer)) != -1) {
                        requestBody.append(buffer, 0, read);
                    }
//                    LOGGER.info(" requestBody ---> " + requestBody.toString());

                    CommandResult result = null;
                    if (this.commandListener != null) {
                        result = this.commandListener.handleCommand(requestBody.toString());
                    }
                    if (result == null) {
                        result = CommandResult.createError();
                    }
                    // send response
                    this.sendCommandResult(httpExchange, result);
                    return;
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                    msg = ex.getMessage();
                }
            } else {
                msg = "http method [" + method + "] is not post.";
            }
            // send response
            this.sendFailed(httpExchange, -1, msg);
        } else {
            this.sendFailed(httpExchange);
        }
    }

    private void closeDebug(HttpExchange httpExchange) throws IOException {
        // 打开DEBUG配置
//        ConfigManager.getInstance().getAppConfig().setDebug(true);
//        this.sendSucceed(httpExchange);
    }

    private void openDebug(HttpExchange httpExchange) throws IOException {
        // 关闭DEBUG配置
//        ConfigManager.getInstance().getAppConfig().setDebug(false);
//        this.sendSucceed(httpExchange);
    }

    /**
     * agent server healthy check
     *
     * @param httpExchange http exchange
     * @return boolean
     * @throws IOException
     */
    private void healthyCheck(HttpExchange httpExchange) throws IOException {
        this.sendResponse(httpExchange, null, true);
    }



    private Set<String> parseQuery(String query) throws IOException {
        Set<String> names = new HashSet<String>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx != -1 && URLDecoder.decode(pair.substring(0, idx), "UTF-8").equals("name[]")) {
                    names.add(URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                }
            }
        }
        return names;
    }

}