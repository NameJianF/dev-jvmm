package live.itrip.jvmm.json.nodetypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import live.itrip.jvmm.json.iterators.OneItemIterator;
import live.itrip.jvmm.json.iterators.PeekableIterator;

/**
 * @author fengjianfeng
 * @date 2021-08-18
 */
public class ArrayIndexPathNode implements PathNode {
	
	private final int index;

	public ArrayIndexPathNode(int index) {
		this.index = index;
	}

	@Override
	public PeekableIterator<JsonElement> filter(JsonElement parent) {
		if(parent.isJsonArray()){
			JsonArray parentArr = parent.getAsJsonArray();
			int size = parentArr.size();
			if(index>=0 && index<size){
				JsonElement element = parentArr.get(index);
				return new OneItemIterator<JsonElement>(element);
			} else if(index<0 && Math.abs(index)<=size){
				JsonElement element = parentArr.get(size+index);// so [0..size)
				return new OneItemIterator<JsonElement>(element);
			}
		}
		return EMPTY_ITERATOR;
	}
	
	@Override
	public String toString() {
		return ""+index;
	}
}