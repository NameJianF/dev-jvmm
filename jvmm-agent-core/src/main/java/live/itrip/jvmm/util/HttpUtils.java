package live.itrip.jvmm.util;

import com.google.common.base.Charsets;
import live.itrip.jvmm.logging.AgentLogFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * http request
 *
 * @author fengjianfeng
 * @date http request
 */
public class HttpUtils {

    private static final Logger LOGGER = AgentLogFactory.getLogger(HttpUtils.class);

    /**
     * post json data
     *
     * @param url  url
     * @param json json
     * @return
     */
    public static String doPost(String url, String json) {
        LOGGER.info(String.format("url-->%s \njson-->%s", url, json));
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        String content = null;
        StringBuilder sbf = new StringBuilder();
        try {
            URL u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setReadTimeout(50000);
            conn.setConnectTimeout(60000);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("content-Type", "application/json");

            writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), Charsets.UTF_8));
            writer.print(json);
            writer.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((content = reader.readLine()) != null) {
                sbf.append(content);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            assert conn != null;
            conn.disconnect();
        }
        return sbf.toString();

    }

    public static String doGet(String url) {
        StringBuilder sbf = new StringBuilder();
        HttpURLConnection conn = null;
        BufferedReader br = null;
        String content = null;
        try {
            URL u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setReadTimeout(50000);
            conn.setConnectTimeout(60000);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            if (conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charsets.UTF_8));
                while ((content = br.readLine()) != null) {
                    sbf.append(content);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            assert conn != null;
            conn.disconnect();
        }
        return sbf.toString();
    }

    public static void doPost(String url, String body, Map<String, String> headers) throws IOException{

    }
}