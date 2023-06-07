package live.itrip.jvmm.agent.common.exception;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 8:42 下午 2021/5/29
 *
 * @author fengjianfeng
 */
public class InvalidMsgException extends RuntimeException {

    private int seed;

    public InvalidMsgException() {
    }

    public InvalidMsgException(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }
}
