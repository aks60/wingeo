package common;

import builder.model.AreaSimple;
import builder.model.Com5t;
import builder.model.UGeo;
import builder.script.GsonElem;
import enums.Layout;
import enums.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArrayCom<E extends Com5t> extends ArrayList<E> {

    AreaSimple areaSimple = null;
    HashMap<Double, E> hm = new HashMap();

    public ArrayCom() {        
    }
    
    public ArrayCom(AreaSimple area) {
        super();
        this.areaSimple = area;
    }

    public GsonElem gson(double id) {
        Com5t com5t = this.stream().filter(it -> it.id == id).findFirst().orElse(null);
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
    
    public E get(int index) {
        index = (index < 0) ? index + size() : (index > size() - 1) ? index - size() : index;
        return super.get(index);
    }

    public E get(Layout layout) {
        try {
            for (Com5t el : this) {
                int index = UGeo.getIndex(areaSimple.area, el);
                if (areaSimple.area.getGeometryN(0).getNumPoints() == 4
                        || areaSimple.area.getGeometryN(0).getNumPoints() == 5) {

                    if (index == 0 && layout == Layout.LEFT) {
                        return (E) el;
                    } else if (index == 1 && layout == Layout.BOTT) {
                        return (E) el;
                    } else if (index == 2 && layout == Layout.RIGHT) {
                        return (E) el;
                    } else if (index == 3 && layout == Layout.TOP) {
                        return (E) el;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:LinkedCom.get()");
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

    public E find(double id) {
        return this.stream().filter(it -> it.id == id).findFirst().get();
    }
}
