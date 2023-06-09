package live.itrip.jvmm.monitor.convey.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import live.itrip.jvmm.monitor.convey.handler.HandlerProvider;

import java.nio.charset.StandardCharsets;

import static live.itrip.jvmm.monitor.convey.channel.ChannelInitializers.CHUNKED_WRITE_HANDLER;
import static live.itrip.jvmm.monitor.convey.channel.ChannelInitializers.IDLE_STATE_HANDLER;
import static live.itrip.jvmm.monitor.convey.channel.ChannelInitializers.LEN_DECODER_HANDLER;
import static live.itrip.jvmm.monitor.convey.channel.ChannelInitializers.LEN_ENCODER_HANDLER;
import static live.itrip.jvmm.monitor.convey.channel.ChannelInitializers.STRING_DECODER_HANDLER;
import static live.itrip.jvmm.monitor.convey.channel.ChannelInitializers.STRING_ENCODER_HANDLER;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 17:31 2021/5/17
 *
 * @author fengjianfeng
 */
public class JvmmServerChannelInitializer extends ChannelInitializer<Channel> {

    public static final String JVMM_BUBBLE_DECODER = "jvmmBubbleDecoder";
    public static final String JVMM_BUBBLE_ENCODER = "jvmmBubbleEncoder";

    private final HandlerProvider provider;

    public JvmmServerChannelInitializer(HandlerProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (provider.getReaderIdle() > 0) {
            p.addLast(provider.getGroup(), IDLE_STATE_HANDLER, new IdleStateHandler(provider.getReaderIdle(), 0, 0));
        }
        p.addLast(provider.getGroup(), LEN_DECODER_HANDLER, new LengthFieldBasedFrameDecoder(0xFFFFFF, 0, 4, 0, 4));
        p.addLast(provider.getGroup(), LEN_ENCODER_HANDLER, new LengthFieldPrepender(4));
        p.addLast(provider.getGroup(), CHUNKED_WRITE_HANDLER, new ChunkedWriteHandler());
        p.addLast(provider.getGroup(), STRING_ENCODER_HANDLER, new StringEncoder(StandardCharsets.UTF_8));
        p.addLast(provider.getGroup(), STRING_DECODER_HANDLER, new StringDecoder(StandardCharsets.UTF_8));
        p.addLast(provider.getGroup(), provider.getName(), provider.getHandler());
    }
}
