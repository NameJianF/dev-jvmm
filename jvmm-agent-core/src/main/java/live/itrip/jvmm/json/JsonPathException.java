package live.itrip.jvmm.json;

/**
 * @author fengjianfeng
 * @date 2021-08-18
 */
public class JsonPathException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JsonPathException(String message) {
        super(message);
    }
}