package live.itrip.jvmm.json.nodetypes;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import live.itrip.jvmm.json.iterators.OneItemIterator;
import live.itrip.jvmm.json.iterators.PeekableIterator;

/**
 * @author fengjianfeng
 * @date 2021-08-18
 */
public class NamedPropertyPathNode implements PathNode {
    private String name;

    public NamedPropertyPathNode(String name) {
        this.name = name;
    }

    @Override
    public PeekableIterator<JsonElement> filter(JsonElement parent) {
        if (parent.isJsonObject()) {
            JsonObject parentObj = parent.getAsJsonObject();
            if (parentObj.has(name)) {
                JsonElement element = parentObj.get(name);
                return new OneItemIterator<JsonElement>(element);
            }
        }
        return EMPTY_ITERATOR;
    }

    @Override
    public String toString() {
        return "\"" + name + "\"";
    }
}