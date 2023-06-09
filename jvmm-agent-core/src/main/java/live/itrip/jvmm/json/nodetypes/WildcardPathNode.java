package live.itrip.jvmm.json.nodetypes;


import com.google.gson.JsonElement;
import live.itrip.jvmm.json.iterators.PeekableIterator;
import live.itrip.jvmm.json.iterators.WildcardIterator;

/**
 * @author fengjianfeng
 * @date 2021-08-18
 */
public class WildcardPathNode implements PathNode {
    @Override
    public PeekableIterator<JsonElement> filter(JsonElement parent) {
        return new WildcardIterator(parent);
    }

    @Override
    public String toString() {
        return "*";
    }
}