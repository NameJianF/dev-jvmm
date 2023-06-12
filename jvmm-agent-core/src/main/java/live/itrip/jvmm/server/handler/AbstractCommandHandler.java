package live.itrip.jvmm.server.handler;

import com.google.common.base.Charsets;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import live.itrip.jvmm.server.CommandResult;
import live.itrip.jvmm.util.GsonUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;

/**
 * @author fengjianfeng
 * @date 2023/6/9 17:21
 */
public abstract class AbstractCommandHandler {
    protected final String METHOD_POST = "POST";
    protected static final String METHOD_GET = "GET";
    protected static final String METHOD_DELETE = "DELETE";
    protected static final String METHOD_PUT = "PUT";
    private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    private final static String HEALTHY_RESPONSE = "{\"code\": 0,\"message\": \"Agent Server is Healthy.\"}";
    private final AbstractCommandHandler.LocalByteArray response = new AbstractCommandHandler.LocalByteArray();


    public abstract void registryPath(HttpServer server);


    /**
     * 解析请求地址中的参数
     *
     * @param query path
     * @return parameters
     * @throws UnsupportedEncodingException
     */
    protected HashMap<String, Object> parseQueryParams(String query)
            throws UnsupportedEncodingException {
        HashMap<String, Object> parameters = new HashMap<String, Object>(8);
        if (query != null) {
            String pairs[] = query.split("[&]");

            for (String pair : pairs) {
                String param[] = pair.split("[=]");

                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                            System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);
                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }

        return parameters;
    }


    /**
     * send failed response
     *
     * @param httpExchange http exchange
     * @return boolean
     * @throws IOException
     */
    protected void sendFailed(HttpExchange httpExchange) throws IOException {
        String res = getResponseBody(-1, "failed.", null);
        this.sendResponse(httpExchange, res, false);
    }

    /**
     * send failed response
     *
     * @param httpExchange http exchange
     * @param code         code
     * @param message      message
     * @return boolean
     * @throws IOException
     */
    protected void sendFailed(HttpExchange httpExchange, int code, String message) throws IOException {
        String res = getResponseBody(code, message, null);
        this.sendResponse(httpExchange, res, false);
    }

    /**
     * send succeed response
     *
     * @param httpExchange http exchange
     * @return boolean
     * @throws IOException
     */
    protected void sendSucceed(HttpExchange httpExchange) throws IOException {
        String res = getResponseBody(0, "succeed.", null);
        this.sendResponse(httpExchange, res, false);
    }

    /**
     * send succeed response
     *
     * @param httpExchange http exchange
     * @param data         data
     * @return boolean
     * @throws IOException
     */
    protected boolean sendSucceed(HttpExchange httpExchange, Object data) throws IOException {
        String res = getResponseBody(0, "succeed.", data);
        return sendResponse(httpExchange, res, false);
    }

    protected void sendCommandResult(HttpExchange httpExchange, CommandResult result) throws IOException {
        this.sendResponse(httpExchange, GsonUtils.toJson(result), false);
    }

    /**
     * create response body
     *
     * @param code    code
     * @param message message
     * @param data    data
     * @return json string
     */
    private String getResponseBody(int code, String message, Object data) {
        CommandResult jsonObject = new CommandResult();
        jsonObject.setCode(code);
        jsonObject.setMessage(message);
        if (data != null) {
            jsonObject.setData(data);
        }
        return GsonUtils.toJson(jsonObject);
    }

    /**
     * send response body
     *
     * @param httpExchange http exchange
     * @param responseBody response body
     * @return boolean
     * @throws IOException
     */
    protected boolean sendResponse(HttpExchange httpExchange, String responseBody, boolean healthyCheck) throws IOException {
        // Send response
        ByteArrayOutputStream response = this.response.get();
        response.reset();
        OutputStreamWriter osw = new OutputStreamWriter(response, Charsets.UTF_8);

        if (healthyCheck) {
            osw.write(HEALTHY_RESPONSE);
        } else {
            // responseBody
            httpExchange.getResponseHeaders().set("Content-Type", CONTENT_TYPE_JSON);
            osw.write(responseBody);
        }

        osw.close();

        if (shouldUseCompression(httpExchange)) {
            // gzip
            httpExchange.getResponseHeaders().set("Content-Encoding", "gzip");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            final GZIPOutputStream os = new GZIPOutputStream(httpExchange.getResponseBody());
            try {
                response.writeTo(os);
            } finally {
                os.close();
            }
        } else {
            // normal
            httpExchange.getResponseHeaders().set("Content-Length", String.valueOf(response.size()));
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.size());
            response.writeTo(httpExchange.getResponseBody());
        }

        httpExchange.close();
        return true;
    }

    private boolean shouldUseCompression(HttpExchange exchange) {
        List<String> encodingHeaders = exchange.getRequestHeaders().get("Accept-Encoding");
        if (encodingHeaders == null) {
            return false;
        }

        for (String encodingHeader : encodingHeaders) {
            String[] encodings = encodingHeader.split(",");
            for (String encoding : encodings) {
                if (encoding.trim().equalsIgnoreCase("gzip")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static class LocalByteArray extends ThreadLocal<ByteArrayOutputStream> {
        @Override
        protected ByteArrayOutputStream initialValue() {
            return new ByteArrayOutputStream(1 << 20);
        }
    }

}
