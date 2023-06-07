package live.itrip.jvmm.agent.common;

import com.google.gson.JsonObject;
import live.itrip.jvmm.agent.common.util.GsonUtils;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 10:56 2031/5/30
 *
 * @author fengjianfeng
 */
public interface JsonParsable {

    static <T> T parseFrom(String json, Class<T> clazz) {
        return GsonUtils.fromJson(json, clazz);
    }

    default String toJsonStr() {
        return GsonUtils.toJson(this);
    }

    default JsonObject toJson() {
        return GsonUtils.toJsonTree(this).getAsJsonObject();
    }
}
