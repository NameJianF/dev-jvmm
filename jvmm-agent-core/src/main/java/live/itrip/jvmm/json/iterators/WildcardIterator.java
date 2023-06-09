package live.itrip.jvmm.json.iterators;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import live.itrip.jvmm.json.nodetypes.WildcardPathNode;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author fengjianfeng
 * @date 2021-08-18
 */
public class WildcardIterator extends live.itrip.jvmm.json.iterators.PeekableIterator<JsonElement> {
    private static class WildcardObjectIterator extends live.itrip.jvmm.json.iterators.PeekableIterator<JsonElement> {

        private Iterator<Entry<String, JsonElement>> iterator;
        private boolean isNextTaken;
        private JsonElement next;

        public WildcardObjectIterator(JsonObject parent) {
            this.iterator = parent.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            if (!isNextTaken) {
                isNextTaken = true;
                next = takeNext();
            }
            return next != null;
        }

        @Override
        public JsonElement next() {
            if (!isNextTaken) {
                next = takeNext();
            } else {
                isNextTaken = false;
            }
            return next;
        }

        @Override
        public JsonElement peek() {
            if (!isNextTaken) {
                isNextTaken = true;
                next = takeNext();
            }
            return next;
        }

        private JsonElement takeNext() {
            if (iterator.hasNext()) {
                return iterator.next().getValue();
            }
            return null;
        }

    }

    private static class WildcardArrayIterator extends live.itrip.jvmm.json.iterators.PeekableIterator<JsonElement> {

        private JsonArray parent;
        private int arrIndex;

        public WildcardArrayIterator(JsonArray parent) {
            this.parent = parent;
            this.arrIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return parent.size() > arrIndex;
        }

        @Override
        public JsonElement next() {
            if (parent.size() > arrIndex) {
                return parent.get(arrIndex++);
            }
            return null;
        }

        @Override
        public JsonElement peek() {
            if (parent.size() > arrIndex) {
                return parent.get(arrIndex);
            }
            return null;
        }

    }

    private PeekableIterator<JsonElement> iterator;

    public WildcardIterator(JsonElement parent) {
        if (parent.isJsonArray()) {
            this.iterator = new WildcardArrayIterator(parent.getAsJsonArray());
        } else if (parent.isJsonObject()) {
            this.iterator = new WildcardObjectIterator(parent.getAsJsonObject());
        } else {
            this.iterator = WildcardPathNode.EMPTY_ITERATOR;
        }
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public JsonElement next() {
        return this.iterator.next();
    }

    @Override
    public JsonElement peek() {
        return this.iterator.peek();
    }

}