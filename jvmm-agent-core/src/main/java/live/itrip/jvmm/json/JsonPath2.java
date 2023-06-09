package live.itrip.jvmm.json;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import live.itrip.jvmm.json.iterators.ArrayListPeekableIterator;
import live.itrip.jvmm.json.iterators.ExecIterator;
import live.itrip.jvmm.json.iterators.PeekableIterator;
import live.itrip.jvmm.json.nodetypes.PathNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengjianfeng
 * @date 2021-08-18
 */
public class JsonPath2 {

    static public class Expression {

        final List<PathNode> nodes;

        public Expression(List<PathNode> nodes) {
            this.nodes = nodes;
        }

        // XXX: for using same expression with few objects in same time this list should be immutable.
        // Possible solutions:
        // 1) make list unchangeable and all PathNodes immutable
        // 2) make list unchangeable and return deep copy of obtained object each time
        public List<PathNode> getNodes() {
            return nodes;
        }

        public List<JsonElement> exec(String strJson) {
            return exec(new JsonParser().parse(strJson));
        }

        List<JsonElement> exec(JsonElement obj) {
            ArrayList<JsonElement> list = new ArrayList<JsonElement>();
            list.add(obj);
            int filterPosition = 0;
            PeekableIterator<JsonElement> iterator = exec(new ArrayListPeekableIterator<JsonElement>(list), filterPosition);
            List<JsonElement> res = new ArrayList<JsonElement>();
            while (iterator.hasNext()) {
                res.add(iterator.next());
            }
            return res;
        }

        PeekableIterator<JsonElement> exec(final PeekableIterator<JsonElement> in, final int filterPosition) {

            // System.out.println(getStackOffset()+"start exec on iter:" + in+ "filter is:"+filterPosition + " val is:" +(filterPosition>=nodes.size()?"-":nodes.get(filterPosition)));
            // in -
            //
            return new ExecIterator(this, in, filterPosition);
        }

    }

}
