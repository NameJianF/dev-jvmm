package live.itrip.jvmm.json.iterators;


/**
 * @param <T>
 * @author fengjianfeng
 * @date 2021-08-18
 */
public class OneItemIterator<T> extends PeekableIterator<T> {

    private T element;
    boolean isTaken = false;

    public OneItemIterator(T element) {
        this.element = element;
    }

    @Override
    public boolean hasNext() {
        return !isTaken;
    }

    @Override
    public T next() {
        if (!isTaken) {
            isTaken = true;
            return element;
        } else {
            return null;
        }
    }

    @Override
    public T peek() {
        if (!isTaken) {
            return element;
        } else {
            return null;
        }
    }

}