package common;

import builder.model.AreaSimple;
import builder.model.Com5t;
import builder.script.GsonElem;
import enums.Layout;
import enums.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArrayCom<E extends Com5t> extends ArrayList<E> {

    AreaSimple areaSimple = null;
    HashMap<Double, E> hm = new HashMap<Double, E>();

    public ArrayCom() {
    }

    public ArrayCom(AreaSimple area) {
        super();
        this.areaSimple = area;
    }

    public GsonElem gson(double id) {
        Com5t com5t = get(id);
        if (com5t != null) {
            return com5t.gson;
        }
        return null;
    }

    public boolean add(E e) {
        hm.put(e.id, e);
        return super.add(e);
    }

    public E get(double key) {
        return hm.get(key);
    }

    public E get(Layout layout) {
        try {
            for (Com5t el : this) {
                if(el.layout() == layout) {
                    return (E) el;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:ArrayCom.get()");
        }
        return null;
    }

    public ArrayCom<E> filter(Type... type) {
        List tp = List.of(type);
        ArrayCom<E> list2 = new ArrayCom(null);
        for (E el : this) {
            if (tp.contains(el.type)) {
                list2.add(el);
            }
        }
        return list2;
    }

    public ArrayCom<E> filterNo(Type... type) {
        List tp = List.of(type);
        ArrayCom<E> list2 = new ArrayCom(null);
        for (E el : this) {
            if (tp.contains(el.type) == false) {
                list2.add(el);
            }
        }
        return list2;
    }

//    public E get(int index) {
//        index = (index < 0) ? index + size() : (index > size() - 1) ? index - size() : index;
//        return super.get(index);
//    }    
}
