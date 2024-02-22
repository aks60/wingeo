package common;

import builder.making.SpcRecord;
import java.util.ArrayList;

public class ArraySpc<E extends SpcRecord> extends ArrayList<E> {

    public ArraySpc() {
        super();
    }

    public SpcRecord find(double id) {
        return this.stream().filter(it -> it.id == id).findFirst().get();
    }
}
