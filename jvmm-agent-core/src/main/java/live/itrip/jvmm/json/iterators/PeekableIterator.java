package live.itrip.jvmm.json.iterators;

import java.util.Iterator;

/**
 * @author fengjianfeng
 * @date 2021-08-18
 */
public abstract class PeekableIterator<T> implements Iterator<T> {
    abstract public T peek();

    @Override
    public void remove() {
    }
}