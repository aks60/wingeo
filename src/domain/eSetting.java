package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.up;
import java.sql.SQLException;

public enum eSetting implements Field {
    up("0", "0", "0", "Системные настройки", "EMPTY"),
    id("4", "10", "0", "Идентификатор", "id"),
    name("12", "64", "1", "Наименование", "name"),
    val("12", "32", "1", "Значение", "value");
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
    
    public Query getQuery() {
        return query;
    }
    
    public static String val(int _id) {
        if (Query.conf.equals("calc")) {
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL)).getStr(val);
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? "" : recordList.getAs(0, val);
    }

    public String toString() {
        return meta.descr();
    }
}

