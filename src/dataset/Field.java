package dataset;

import domain.eGroups;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

//Поле таблицы
public interface Field {

    public static enum TYPE {

        OBJ(Object.class, 0), INT(Integer.class, 4, 5), NPP(Integer.class), STR(String.class, -9, -1, 1, 12),
        DBL(Double.class, 8), FLT(Double.class, 6), LONG(Long.class, -5), DATE(Date.class, 91, 92, 93),
        BOOL(Boolean.class, 16), BLOB(String.class, -2, -3, -4, 2004);
        Class type;
        public HashSet<Integer> hsConv = new HashSet<Integer>();

        TYPE(Class type, Integer... v) {
            this.type = type;
            for (Integer i : v) {
                hsConv.add(i);
            }
        }

        public static TYPE type(Object index) {
            index = Integer.valueOf(index.toString());
            for (TYPE type : values()) {
                if (type.hsConv.contains(index)) {
                    return type;
                }
            }
            return TYPE.OBJ;
        }
    };

    public static enum EDIT {

        TRUE(true), FALSE(false);
        Boolean edit;

        EDIT(boolean value) {
            this.edit = value;
        }
    };

    default String tname() {

        String str = this.getClass().toString().substring(14);
        StringBuffer str2 = new StringBuffer(str);
        int sep = 0;
        for (int index = 1; index < str.length(); ++index) {
            if (Character.isUpperCase(str.charAt(index))) {
                str2.insert(index + sep++, "_");
            }
        }
        return str2.toString().toUpperCase();
    }

    public String name();

    public int ordinal();

    default Record newRecord() {
        return newRecord(Query.SEL);
    }

    default Record newRecord(String up) {
        Record record = new Record();
        record.add(this);
        for (int index = 1; index < fields().length; ++index) {
            record.add(null);
        }
        record.set(0, up);
        return record;
    }

    public MetaField meta();

    default Field[] fields() {
        return null;
    }

    public static String wrapper(Object value, Field.TYPE type) {
        try {
            if (value == null) {
                return null;
            } else if (Field.TYPE.STR.equals(type)) {
                return "'" + value + "'";
            } else if (Field.TYPE.BLOB.equals(type)) {
                return "'" + value + "'";
            } else if (Field.TYPE.BOOL.equals(type)) {
                return "'" + value + "'";
            } else if (Field.TYPE.DATE.equals(type)) {
                if (value instanceof java.util.Date) {
                    return " '" + new SimpleDateFormat("dd.MM.yyyy").format(value) + "' ";
                } else {
                    return " '" + value + "' ";
                }
            }
            return value.toString();

        } catch (Exception e) {
            System.err.println("Ошибка:Ашудв.vrapper() " + e);
            return null;
        }
    }
    
    public boolean equals(Object other);
}
