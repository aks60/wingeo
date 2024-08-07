package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Не менять индекс поля
public enum eFurnpar2 implements Field {
    up("0", "0", "0", "Парам.спецификаций фурнитуры", "PARFURS"),
    id("4", "10", "0", "Идентификатор", "id"),
    text("12", "64", "1", "Значения параметр", "PTEXT"),
    groups_id("4", "10", "0", "Название параметра", "PNUMB"),
    furndet_id("4", "10", "0", "Детализация", "furndet_id");
    
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eFurnpar2(Object... p) {
        meta.init(p);
    }

    public static Query data() {
        if (query.size() == 0) {
            query.select(up, "order by", id);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public MetaField meta() {
        return meta;
    }

    public Field[] fields() {
        return values();
    }

    public Query getQuery() {
        return query;
    }
    
    public static List<Record> find(int _id) {
        if (Query.conf.equals("calc")) {
            return data().stream().filter(rec -> rec.getInt(furndet_id) == _id).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", furndet_id, "=", _id);
        return (recordList.isEmpty() == true) ? new ArrayList<Record>() : recordList;
    }
    
    public String toString() {
        return meta.descr();
    }
}
