package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;

public enum eSetting implements Field {
    up("0", "0", "0", "��������� ���������", "EMPTY"),
    id("4", "10", "0", "�������������", "id"),
    name("12", "64", "1", "������������", "name"),
    val("12", "32", "1", "��������", "value");
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eSetting(Object... p) {
        meta.init(p);
    }

    public MetaField meta() {
        return meta;
    }

    public Field[] fields() {
        return values();
    }

    public static Query data() {
        if (query.size() == 0) {
            query.select(up, "order by", id);
            Query.listOpenTable.add(query);
        }
        return query;
    }
    
    public Query query() {
        return query;
    }
    
    public static String find(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL)).getStr(val);
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? "" : recordList.getAs(0, val);
    }

    public String toString() {
        return meta.descr();
    }
}

