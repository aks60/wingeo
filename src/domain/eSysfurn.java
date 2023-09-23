package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum eSysfurn implements Field {
    up("0", "0", "0", "Фурнитуры системы профилей", "SYSPROS"),
    id("4", "10", "0", "Идентификатор", "id"),
    npp("5", "5", "1", "Приоритет обвязки", "FPORN"),
    replac("5", "5", "1", "Замена", "FWHAT"),
    side_open("5", "10", "1", "Сторона открывания по умолчанию", "side_open"),
    hand_pos("5", "10", "1", "Расположение ручки по умолчанию", "hand_pos"),
    furniture_id("4", "10", "1", "Фурнитура", "furniture_id"),
    artikl_id1("4", "10", "1", "Артикул ручки по умолчанию", "artikl_id1"),
    artikl_id2("4", "10", "1", "Артиккул подвеса (петли) по умолчанию", "artikl_id2"),
    systree_id("4", "10", "0", "Система профилей", "systree_id");
   
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eSysfurn(Object... p) {
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
            query.select(up, "order by", npp, ",", id); //id - т.к. при конвертиров. наруш. порядок след.
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public static List<Record> find(int _nuni) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(systree_id) == _nuni).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", systree_id, "=", _nuni, "order by", npp);
        return (recordList.isEmpty() == true) ? new ArrayList() : recordList;
    }

    public static Record find2(int _id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(id) == _id).findFirst().orElse(up.newRecord());
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id, "order by", npp);
        return (recordList.isEmpty() == true) ? up.newRecord() : recordList.get(0);
    }

    public static Record find3(int _nuni) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(systree_id) == _nuni).findFirst().orElse(up.newRecord());
        }
        Query recordList = new Query(values()).select(up, "where", systree_id, "=", _nuni, "order by", npp, ",", id); //id - т.к. при ковертиров. наруш. порядок след.
        return (recordList.isEmpty() == true) ? up.newRecord() : recordList.get(0);
    }
    
    public static Record find4(int _nuni, int _side_open) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(systree_id) == _nuni && rec.getInt(side_open) == _side_open).findFirst().orElse(up.newRecord());
        }
        Query recordList = new Query(values()).select(up, "where", systree_id, "=", _nuni, "and", side_open, "=", _side_open, "order by", npp);
        return (recordList.isEmpty() == true) ? up.newRecord() : recordList.get(0);
    }

    public String toString() {
        return meta.descr();
    }
}
