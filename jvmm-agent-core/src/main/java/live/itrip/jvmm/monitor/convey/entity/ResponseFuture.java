package live.itrip.jvmm.monitor.convey.entity;

import java.util.function.Consumer;

/**
 * description: TODO
 * date 19:03 2023/1/31
 * @author fengjianfeng
 */
public class ResponseFuture {
    private final Consumer<Object> consumer;

    public ResponseFuture(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    public void apply(Object data) {
        consumer.accept(data);
    }
}
