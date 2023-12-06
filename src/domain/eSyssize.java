package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.id;
import static domain.eArtikl.up;
import static domain.eArtikl.virtualRec;
import java.sql.SQLException;
import java.util.HashMap;

public enum eSyssize implements Field {
    up("0", "0", "0", "Системные константы", "SYSSIZE"),
    id("4", "10", "0", "Идентификатор", "id"),
    name("12", "32", "1", "Система артикулов", "SNAME"),
    prip("8", "15", "1", "Припуск на сварку", "SSIZP"),    
    naxl("8", "15", "1", "Нахлест створки", "SSIZF"),
    falz("8", "15", "1", "T - Глубина до фальца", "SSIZN"),
    zax("8", "15", "1", "Заход импоста", "SSIZI");
    //sunic("4", "10", "1", "ID системы", "SUNIC"),
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());
    private static HashMap<Integer, Record> map = new HashMap();

    eSyssize(Object... p) {
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
            map.clear();
            query.stream().forEach(rec -> map.put(rec.getInt(id), rec));            
        }
        return query;
    }
    public static Record get(Record artiklRec) {
        int id = artiklRec.getInt(eArtikl.syssize_id);
        if (id == -3) {
            return eArtikl.virtualRec();
        }
        query();
        Record rec = map.get(id);
        return (rec == null) ? eSyssize.virtualRec() : rec;
    }
    
    public static Record find(Record artiklRec) {
        int _id = artiklRec.getInt(eArtikl.syssize_id);
        if (_id == -3) { //если арт. вирт. то return virtualRec();
            return virtualRec();
        }
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(defaultRec());
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? defaultRec() : recordList.get(0);
    }

    //Cистемные переменные по умолчанию
    public static Record defaultRec() {
        Record record = up.newRecord();
        record.setNo(id, -1);
        record.setNo(prip, 0);
        record.setNo(naxl, 0);
        record.setNo(zax, 0);
        return record;
    }
    
    //Виртуал. системные переменные
    public static Record virtualRec() {
        Record record = up.newRecord();
        record.setNo(id, -3);
        record.setNo(prip, 0);
        record.setNo(naxl, 0);
        record.setNo(zax, 0);
        return record;
    }

    public String toString() {
        return meta.descr();
    }
}
