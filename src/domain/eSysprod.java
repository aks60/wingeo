package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.grup;
import static domain.eGroups.name;
import static domain.eGroups.npp;
import static domain.eGroups.up;
import enums.TypeGrup;
import java.util.stream.Collectors;

public enum eSysprod implements Field {
    up("0", "0", "0", "Системные типовые изделия", "EMPTY"),
    id("4", "10", "0", "Идентификатор", "id"),
    npp("4", "10", "0", "Номер п/п", "npp"),
    name("12", "128", "1", "Название изделия", "name"),
    script("12", "4096", "0", "Скрипт построения окна", "script"),
    systree_id("4", "10", "0", "Система", "systree_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eSysprod(Object... p) {
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
            query.select(up, "order by", id);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public Query getQuery() {
        return query;
    }
    

    public static void select2(Query q, int systreeID) {
        q.clear();
        if (Query.conf.equals("calc")) {
            q.addAll(query().stream().filter(rec -> rec.getInt(systree_id) == systreeID).sorted((o1, o2) -> {
                if (o1.getInt(npp) == o2.getInt(npp)) {
                    return (o1.getInt(id) > o2.getInt(id)) ? 1 : -1;
                } else {
                    return (o1.getInt(npp) > o2.getInt(npp)) ? 1 : -1;
                }
            }).collect(Collectors.toList()));
        } else {
            q.select(up, "where", systree_id, "=", systreeID, "order by", npp, ",", eSysprod.id);
        }
    }
    
    public static Record find(int _id) { //если не нашол надо возвр. null
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(null);
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? null : recordList.get(0); 
    }

    public String toString() {
        return meta.descr();
    }
}
