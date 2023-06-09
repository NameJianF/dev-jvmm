package live.itrip.jvmm.monitor.server.handler;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import live.itrip.jvmm.util.SignatureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import live.itrip.jvmm.common.exception.AuthenticationFailedException;
import live.itrip.jvmm.monitor.convey.entity.JvmmRequest;
import live.itrip.jvmm.monitor.convey.entity.JvmmResponse;
import live.itrip.jvmm.monitor.convey.enums.GlobalStatus;
import live.itrip.jvmm.monitor.convey.enums.GlobalType;
import live.itrip.jvmm.monitor.convey.handler.JvmmChannelHandler;
import live.itrip.jvmm.monitor.server.ServerContext;
import live.itrip.jvmm.monitor.server.entity.conf.JvmmServerConf;

import java.util.Objects;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 9:39 2021/5/18
 *
 * @author fengjianfeng
 */
public class JvmmServerHandler extends JvmmChannelHandler {
    private static final Logger logger = LoggerFactory.getLogger(JvmmServerHandler.class);

    private boolean authed = !ServerContext.getConfiguration().getServer().getJvmm().getAuth().isEnable();

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    protected boolean handleBefore(ChannelHandlerContext ctx, JvmmRequest reqMsg) throws Exception {
        JvmmServerConf conf = ServerContext.getConfiguration().getServer().getJvmm();

        if (Objects.equals(reqMsg.getType(), GlobalType.JVMM_TYPE_AUTHENTICATION.name())) {
            auth(ctx, reqMsg, conf);
            return false;
        } else {
            if (conf.getAuth().isEnable() && !authed) {
                throw new AuthenticationFailedException();
            }
        }
        return true;
    }

    private void auth(ChannelHandlerContext ctx, JvmmRequest req, JvmmServerConf conf) throws Exception {
        if (conf.getAuth().isEnable()) {
            try {
                JsonObject data = req.getData().getAsJsonObject();

                String account = data.get("account").getAsString();
                String password = data.get("password").getAsString();
                if (Objects.equals(SignatureUtil.MD5(conf.getAuth().getUsername()), account)
                        && Objects.equals(SignatureUtil.MD5(conf.getAuth().getPassword()), password)) {
                    logger().debug("Auth successful. channelId: {}", ctx.channel().hashCode());
                } else {
                    throw new AuthenticationFailedException();
                }
            } catch (IllegalStateException | NullPointerException e) {
                throw new AuthenticationFailedException();
            }
        }
        authed = true;
        JvmmResponse response = JvmmResponse.create().setType(GlobalType.JVMM_TYPE_AUTHENTICATION)
                .setStatus(GlobalStatus.JVMM_STATUS_OK.name());
        ctx.writeAndFlush(response.serialize());
    }

}
