package live.itrip.jvmm.agent.convey.entity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import live.itrip.jvmm.agent.common.JsonParsable;
import live.itrip.jvmm.agent.common.exception.MessageSerializeException;
import live.itrip.jvmm.agent.convey.enums.GlobalType;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 19:00 2021/5/17
 *
 * @author fengjianfeng
 */
public class JvmmRequest implements JsonParsable {
    private String type;
    private JsonElement data;

    private JvmmRequest() {
    }

    public static JvmmRequest create() {
        return new JvmmRequest();
    }

    public static JvmmRequest parseFrom(String msg) {
        return new Gson().fromJson(msg, JvmmRequest.class);
    }

    public boolean isHeartbeat() {
        return GlobalType.JVMM_TYPE_HEARTBEAT.name().equalsIgnoreCase(type);
    }

    public String serialize() {
        if (type == null) {
            throw new MessageSerializeException("Missing required param: 'type'");
        }
        return toJsonStr();
    }

    public String getType() {
        return type;
    }

    public JvmmRequest setType(String type) {
        this.type = type;
        return this;
    }

    public JvmmRequest setType(GlobalType type) {
        this.type = type.name();
        return this;
    }

    public JsonElement getData() {
        return data;
    }

    public JvmmRequest setData(JsonElement data) {
        this.data = data;
        return this;
    }
}
