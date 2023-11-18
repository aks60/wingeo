package common;

import builder.model.Com5t;
import builder.model.ElemSimple;
import builder.script.GsonElem;
import enums.Layout;
import enums.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LinkedCom<E extends Com5t> extends LinkedList<E> {

    public LinkedCom() {
        super();
    }
    
    public LinkedCom(Collection<? extends E> c) {
        super(c);
    }

    public GsonElem gson(double id) {
        Com5t com5t = this.stream().filter(it -> it.id == id).findFirst().orElse(null);
        if (com5t != null) {
            return com5t.gson;
        }
        return null;
    }
    
    public E get(Layout layout) {
        double angl = 0;
        if(layout == Layout.LEFT) {
            angl = 90;
        } else if(layout == Layout.BOTT) {
            angl = 0;
        } else if(layout == Layout.RIGHT) {
            angl = -90;
        } else if(layout == Layout.TOP) {
            angl = 180;
        }
        for (E el : this) {
            if (angl == ((ElemSimple) el).anglHoriz()) {
                return (E) el;
            }
        }
        return null;
    }

    public <T extends Com5t> LinkedCom<T> filter(Type... type) {
        List tp = List.of(type);
        LinkedCom<T> list2 = new LinkedCom();
        for (E el : this) {
            if (tp.contains(el.type)) {
                list2.add((T) el);
            }
        }
        return list2;
    }

    public E find2(double id) {
        return this.stream().filter(it -> it.id == id).findFirst().get();
    }

    public E find3(double angl) {

        for (E el : this) {
            if (angl == ((ElemSimple) el).anglHoriz()) {
                return (E) el;
            }
        }
        return null;
    }

    public E find4(Type type) {

        for (E el : this) {
            if (type == el.type) {
                return (E) el;
            }
        }
        return null;
    }

}
