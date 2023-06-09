package live.itrip.jvmm.agent.spy;

/**
 * @author fengjianfeng
 * @date 2022/1/6
 * 功能描述:
 */
public class SpyResult {
    public static final int RET_STATE_NONE = 0;
    public static final int RET_STATE_RETURN = 1;
    public static final int RET_STATE_THROWS = 2;
    public static final SpyResult SPY_RESULT_NONE = new SpyResult(RET_STATE_NONE, null);


    /**
     * 返回状态(0:NONE;1:RETURN;2:THROWS)
     */
    public final int state;
    /**
     * 应答对象
     */
    public final Object respond;

    /**
     * 构造返回结果
     *
     * @param state   返回状态
     * @param respond 应答对象
     */
    private SpyResult(int state, Object respond) {
        this.state = state;
        this.respond = respond;
    }

    public static SpyResult newInstanceForNone() {
        return SPY_RESULT_NONE;
    }

    public static SpyResult newInstanceForReturn(Object object) {
        return new SpyResult(RET_STATE_RETURN, object);
    }

    public static SpyResult newInstanceForThrows(Throwable throwable) {
        return new SpyResult(RET_STATE_THROWS, throwable);
    }
}
