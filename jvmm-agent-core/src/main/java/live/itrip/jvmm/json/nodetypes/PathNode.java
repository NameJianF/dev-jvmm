package live.itrip.jvmm.json.nodetypes;

import com.google.gson.JsonElement;
import live.itrip.jvmm.json.iterators.ArrayListPeekableIterator;
import live.itrip.jvmm.json.iterators.PeekableIterator;

import java.util.ArrayList;
/**
 * @author fengjianfeng
 * @date 2021-08-18
 */
public abstract interface PathNode {
    final PeekableIterator<JsonElement> EMPTY_ITERATOR = new ArrayListPeekableIterator<JsonElement>(new ArrayList<JsonElement>());

    abstract PeekableIterator<JsonElement> filter(JsonElement parent);
}