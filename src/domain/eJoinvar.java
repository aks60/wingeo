package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;

public enum eJoinvar implements Field {
    up("0", "0", "0", "Варианты соединений", "CONNVAR"),
    id("4", "10", "0", "Идентификатор", "id"),
    prio("5", "5", "1", "Приоритет", "CPRIO"),
    name("12", "64", "1", "Название варианта", "CNAME"),
    types("5", "5", "1", "Тип варианта", "CTYPE"),
    mirr("16", "5", "1", "Зеркальное использование", "CMIRR"), //1 - использовать зеркально, 0 - нельзя использовать зеркально
    joining_id("4", "10", "0", "Соединения", "joining_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eJoinvar(Object... p) {
        meta.init(p);
    }

    public MetaField meta() {
        return meta;
    }

    public Field[] fields() {
        return values();
    }

    public static Query query() {
        if (query.size() == 0) {
            query.select(up, "order by", prio);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public Record addRecord() {
        return UGui.addRecord(query, up);
    }
    
    public static List<Record> find(int _id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(joining_id) == _id).collect(toList());
        }
        return new Query(values()).select(up, "where", joining_id, "=", _id, "order by", prio);
    }

    public static List<Record> find2(Set<Integer> setVariant) {
        
        String sql = setVariant.toString();
        return null;
    }
    
    public String toString() {
        return meta.descr();
    }
}
