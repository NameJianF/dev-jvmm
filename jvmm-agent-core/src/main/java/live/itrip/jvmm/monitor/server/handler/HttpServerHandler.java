package live.itrip.jvmm.monitor.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;
import live.itrip.jvmm.agent.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import live.itrip.jvmm.monitor.convey.handler.HttpChannelHandler;
import live.itrip.jvmm.monitor.server.ServerContext;
import live.itrip.jvmm.monitor.server.entity.conf.AuthOptionConf;
import live.itrip.jvmm.monitor.server.entity.conf.HttpServerConf;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 18:37 2022/9/7
 *
 * @author fengjianfeng
 */
public class HttpServerHandler extends HttpChannelHandler {

    @Override
    public Logger logger() {
        return LoggerFactory.getLogger(HttpServerHandler.class);
    }

    @Override
    protected boolean handleBefore(ChannelHandlerContext ctx, String uri, FullHttpRequest msg) {
        HttpServerConf conf = ServerContext.getConfiguration().getServer().getHttp();
        AuthOptionConf auth = conf.getAuth();
        if (auth.isEnable()) {
            String authStr = msg.headers().get("Authorization");
            if (StringUtils.isEmpty(authStr) || !authStr.startsWith("Basic")) {
                response401(ctx);
                return false;
            }
            try {
                String[] up = new String(Base64.getDecoder().decode(authStr.split("\\s")[1]), StandardCharsets.UTF_8).split(":");
                if (!Objects.equals(auth.getUsername(), up[0]) || !Objects.equals(auth.getPassword(), up[1])) {
                    response401(ctx);
                    return false;
                }
            } catch (Exception e) {
                response401(ctx);
                return false;
            }
        }
        return true;
    }
}
