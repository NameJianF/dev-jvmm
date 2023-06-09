package live.itrip.jvmm.monitor.convey.entity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import live.itrip.jvmm.common.exception.MessageSerializeException;
import live.itrip.jvmm.monitor.convey.enums.GlobalStatus;
import live.itrip.jvmm.monitor.convey.enums.GlobalType;
import live.itrip.jvmm.util.GsonUtils;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 19:00 2021/5/17
 *
 * @author fengjianfeng
 */
public class JvmmResponse {
    private String type;
    private String status;
    private String message;
    private JsonElement data;

    private JvmmResponse() {
    }

    public static JvmmResponse create() {
        return new JvmmResponse();
    }

    public static JvmmResponse parseFrom(String msg) {
        return new Gson().fromJson(msg, JvmmResponse.class);
    }

    public String serialize() {
        if (type == null) {
            throw new MessageSerializeException("Missing required param: 'type'");
        }
        if (status == null) {
            throw new MessageSerializeException("Missing required param: 'status'");
        }
        return GsonUtils.toJson(this);
    }

    public String getType() {
        return type;
    }

    public JvmmResponse setType(String type) {
        this.type = type;
        return this;
    }

    public JvmmResponse setType(GlobalType type) {
        this.type = type.name();
        return this;
    }

    public String getStatus() {
        return status;
    }

    public JvmmResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public JvmmResponse setStatus(GlobalStatus status) {
        this.status = status.name();
        return this;
    }

    public JsonElement getData() {
        return data;
    }

    public JvmmResponse setData(JsonElement data) {
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JvmmResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
