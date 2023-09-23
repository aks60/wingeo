package common;

import builder.ICom5t;
import builder.script.GsonElem;
import enums.Type;
import java.util.LinkedList;
import java.util.List;

public class LinkedCom<E extends ICom5t> extends LinkedList<E> {

    public LinkedCom() {
        super();
    }

    public E find(double id) {
        return this.stream().filter(it -> it.id() == id).findFirst().get();
    }

    public GsonElem gson(double id) {
        ICom5t com5t = this.stream().filter(it -> it.id() == id).findFirst().orElse(null);
        if (com5t != null) {
            return com5t.gson();
        }
        return null;
    }

    public <T extends ICom5t> LinkedCom<T> filter(Type... type) {
        List tp = List.of(type);
        LinkedCom<T> list2 = new LinkedCom();
        for (E el : this) {
            if (tp.contains(el.type())) {
                list2.add((T) el);
            }
        }
        return list2;
    }

    public E find(Type type) {

        for (E el : this) {
            if (type == el.type()) {
                return (E) el;
            }
        }
        return null;
    }
}
