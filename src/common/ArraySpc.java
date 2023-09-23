package common;

import builder.making.Specific;
import java.util.ArrayList;

public class ArraySpc<E extends Specific> extends ArrayList<E> {

    public ArraySpc() {
        super();
    }

    public Specific find(double id) {
        return this.stream().filter(it -> it.id == id).findFirst().get();
    }
}
