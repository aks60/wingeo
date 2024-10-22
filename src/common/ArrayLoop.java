package common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ArrayLoop<E> extends ArrayList<E> {

    public ArrayLoop() {
        super();
    }

    public ArrayLoop(Collection<? extends E> c) {
        super(c);
    }

    public E get(int index) {
        int i = (index < 0) ? index + size() : (index > size() - 1) ? index - size() : index;
        return super.get(i);
    }
}
