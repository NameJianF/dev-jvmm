package live.itrip.jvmm.json.iterators;


import java.util.ArrayList;
import java.util.ListIterator;

/**
 * @author fengjianfeng
 * @date 2021-08-18
 */
public class ArrayListPeekableIterator<T> extends PeekableIterator<T> {

    private ListIterator<T> iter;

    public ArrayListPeekableIterator(ArrayList<T> in) {
        this.iter = in.listIterator();
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = iter.hasNext();
        return hasNext;
    }

    @Override
    public T next() {
        return iter.next();
    }

    @Override
    public void remove() {
        iter.remove();
    }

    /**
     * Take next item but not shift position.
     */
    @Override
    public T peek() {
        if (hasNext()) {
            T res = iter.next();
            iter.previous();// return back
            return res;
        } else {
            return null;
        }
    }
}