package dataset;

import common.UCom;
import common.eProp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Record<E> extends ArrayList<E> {

    public static boolean DIRTY = false;
    private Table table = null;

    public Record() {
        super();
    }

    public Record(Table table) {
        super();
        this.table = table;
    }

    public Record(int initialCapacity) {
        super(initialCapacity);
    }

    public Record(Collection<? extends E> c) {
        super(c);
    }

    //ИЗМЕНЕНИЯ СТАТУСА ЗАПИСИ
    public E set(int index, E element) {
        if (index != 0 && Query.SEL.equals(get(0))) {
            super.set(0, (E) Query.UPD);
            DIRTY = true;
        }
        return super.set(index, element);
    }

    //ИЗМЕНЕНИЯ СТАТУСА ЗАПИСИ
    public E set(Field field, E element) {
        return (E) set(field.ordinal(), element);
    }

    //ИЗМЕНЕНИЯ СТАТУСА ЗАПИСИ
    public E setNo(int index, E element) {
        return super.set(index, element);
    }

    //ЗАПИСЬ БЕЗ ИЗМЕНЕНИЯ СТАТУСА
    public E setNo(Field field, E element) {
        return super.set(field.ordinal(), element);
    }

    //При тестировании для разработчика
    public void setDev(Field field, String element) {
//        if (eProp.dev == true) {
//            String val = element + " " + super.get(1);            
////            String val = element + " #" + super.get(1);
//            super.set(field.ordinal(), (E) val);
//        } else {
        super.set(field.ordinal(), (E) element);
//        }

    }

    public Object getDev(Field field, Object val) {
//        if (eProp.dev == true) {
//            return val + " " + get(field.ordinal());
//            //return val + " " + get(field.ordinal()) + " #" + val;
//        } else {
        return get(field.ordinal());
//        }
    }

    public static Object getDev(Object num, Object val) {
        if (eProp.dev == true) {
            return num + " " + val;
            //String v = (Integer.parseInt(num.toString()) < 0) ? num.toString() : " #" + num;
            //return val + " " + v;
        } else {
            return val;
        }
    }

    public Object get(Field field) {
        return super.get(field.ordinal());
    }

    public String getStr(int index) {
        return (super.get(index) == null) ? "" : String.valueOf(super.get(index));
    }

    public int getInt(int index) {
        try {
            Object obj = super.get(index);
            return (obj == null) ? -1 : Integer.parseInt(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public double getDbl(int index) {
        try {
            Object obj = super.get(index);
            return (obj == null) ? -1 : Double.valueOf(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public Date getDate(int index) {
        return (super.get(index) == null) ? null : (Date) super.get(index);
    }

    public String getStr(Field field) {
        return (super.get(field.ordinal()) == null) ? "" : String.valueOf(super.get(field.ordinal()));
    }

    public int getInt(Field field) {
        return getInt(field, -1);
    }

    public int getInt(Field field, int def) {
        try {
            Object obj = super.get(field.ordinal());
            return (obj == null) ? def : Integer.valueOf(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public float getFloat(Field field) {
        Object obj = super.get(field.ordinal());
        return getFloat(field, -1);
    }

    public float getFloat(Field field, float def) {
        Object obj = super.get(field.ordinal());
        return (obj == null) ? def : UCom.getFloat(String.valueOf(obj));
    }

    public double getDbl(Field field, double def) {
        Object obj = super.get(field.ordinal());
        return (obj == null) ? def : UCom.getDbl(String.valueOf(obj));
    }

    public double getDbl(Field field) {
        Object obj = super.get(field.ordinal());
        return (obj == null) ? -1 : Double.valueOf(String.valueOf(obj));
    }

    public Date getDate(Field field) {
        return (super.get(field.ordinal()) == null) ? null : (Date) super.get(field.ordinal());
    }

    public boolean isVirtual() {
        if (this.get(1) == null || this.getInt(1) == -3) {
            return true;
        }
        return false;
    }

    public boolean equals(Object obj) {
        return (this.get(1) == ((Record) obj).get(1));
    }

    //Проверка на корректность ввода
    public String validateRec(ArrayList<Field> fields) {
        for (int index = 1; index < fields.size(); index++) {
            MetaField meta = fields.get(index).meta();
            Object value = super.get(fields.get(index).ordinal());
            Object mes = meta.validateField(value);
            if (mes != null) {
                return mes.toString();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String str = "";
        for (Object o : this) {
            str += "  " + o;
        }
        return super.toString() + str;
    }
}
