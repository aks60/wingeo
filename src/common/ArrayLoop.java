package common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ArrayLoop<E> extends ArrayList<E> {

    public ArrayLoop() {        
    }
    
    public ArrayLoop(Collection<? extends E> c) {
        super(c);
    }
    
    public E get(int index) {
        int i = (index >= size()) ? index - size() : (index < 0) ? index + size() : index;
        return super.get(i);
    }
}
