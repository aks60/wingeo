package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.up;
import static domain.eColor.up;
import static domain.eGroups.up;
import frames.UGui;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;

//Не менять индекс поля
public enum eElempar2 implements Field {
    up("0", "0", "0", "Параметры специф.составов", "PARVSTS"),
    id("4", "10", "0", "Идентификатор", "id"),   
    text("12", "64", "1", "Значения параметра", "PTEXT"),
    groups_id("4", "10", "0", "Название параметра", "PNUMB"),
    elemdet_id("4", "10", "0", "Спецификацмя", "element_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eElempar2(Object... p) {
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
            
    public static Record find(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public static Record find2(int _grup) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _grup == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _grup);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public static List<Record> filter(int _elemdet_id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _elemdet_id == rec.getInt(elemdet_id)).collect(toList());
        }
        Query recordList = new Query(values()).select(up, "where", elemdet_id, "=", _elemdet_id);
        return (recordList.isEmpty() == true) ? new ArrayList<Record>() : recordList;
    }
    
    public String toString() {
        return meta.descr();
    }
}
