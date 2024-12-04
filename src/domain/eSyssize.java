package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.List;
import java.util.stream.Collectors;

public enum eSyssize implements Field {
    up("0", "0", "0", "Системные константы", "SYSSIZE"),
    id("4", "10", "0", "Идентификатор", "id"),
    name("12", "32", "1", "Система артикулов", "SNAME"),
    prip("8", "15", "1", "Припуск на сварку", "SSIZP"),
    naxl("8", "15", "1", "Нахлест створки", "SSIZF"),
    falz("8", "15", "1", "T- Глубина до фальца, полка", "SSIZN"),
    zax("8", "15", "1", "Заход импоста", "SSIZI");
    //sunic("4", "10", "1", "ID системы", "SUNIC"),
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eSyssize(Object... p) {
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

    public static Record find(Record artiklRec) {
        int _id = artiklRec.getInt(eArtikl.syssize_id);
        if (_id == -3) { //если арт. вирт. то return virtualRec();
            return virtualRec();
        }
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(null);
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? virtualRec() : recordList.get(0);
    }

    public static Query sql(Query q) {
        if (Query.conf.equals("NET")) {
            q.addAll(data().stream().sorted((o1, o2) -> o1.getStr(name)
                    .compareTo(o2.getStr(name))).collect(Collectors.toList()));
            return q;
        }
        return q.select(up, "order by", name);
    }

    //Виртуал. системные переменные
    public static Record virtualRec() {
        Record record = up.newRecord(Query.SEL);
        record.setNo(id, -3);
        record.setNo(name, "virtual");
        record.setNo(prip, 3);
        record.setNo(naxl, 6);
        record.setNo(falz, 14);
        record.setNo(zax, 4);
        return record;
    }

    public String toString() {
        return meta.descr();
    }
}
