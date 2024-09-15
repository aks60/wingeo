package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGlaspar2.glasdet_id;
import static domain.eGlaspar2.up;
import static domain.eGlaspar2.values;
import static domain.eGroups.up;
import frames.UGui;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Не менять индекс поля
public enum eKitpar2 implements Field {
    up("0", "0", "0", "Парметры комплектов", "PARKOMP"),
    id("4", "10", "0", "Идентификатор", "id"),
    text("12", "64", "1", "Значения параметра", "PTEXT"),
    groups_id("4", "10", "0", "Название параметра", "PNUMB"),
    kitdet_id("4", "10", "0", "Комплекты", "kitdet_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eKitpar2(Object... p) {
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
    
    public static List<Record> filter(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(kitdet_id) == _id).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", kitdet_id, "=", _id);
        return (recordList.isEmpty() == true) ? new ArrayList() : recordList;
    }
    
    public String toString() {
        return meta.descr();
    }
}
