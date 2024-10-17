package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;

public enum eJoining implements Field {
    up("0", "0", "0", "Соединения", "CONNLST"), //или CONNECT"),
    id("4", "10", "0", "Идентификатор", "id"),
    name("12", "96", "1", "Название", "CNAME"),
    is_main("16", "5", "1", " Битовая маска", "CVARF"), //0x100=256 - установлен флаг Основное соединение. Смысл других бит пока неизвестен.
    analog("12", "32", "1", "Аналоги", "CEQUV"),
    artikl_id1("4", "10", "1", "Артикул", "artikl_id1"),
    artikl_id2("4", "10", "1", "Артикул", "artikl_id2");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eJoining(Object... p) {
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
    
    public static Record find(int ID1, int ID2) {

        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> (ID1 == rec.getInt(artikl_id1) || ID1 == rec.getInt(artikl_id2))
                    && (ID2 == rec.getInt(artikl_id1) || ID2 == rec.getInt(artikl_id2))
                    && rec.getInt(artikl_id1) != rec.getInt(artikl_id2)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where (", artikl_id1, "=", ID1, "or", artikl_id1, "=", ID2, ") and (",
                artikl_id2, "=", ID2, "or", artikl_id2, "=", ID1, ") and", artikl_id1, "!=", artikl_id2);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public static Record find2(String _analog) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _analog.equals(rec.getStr(analog)) && (rec.getInt(is_main) & 0x100) != 0).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", analog, "='", _analog, "'");
        return recordList.stream().filter(rec -> (rec.getInt(is_main) & 0x100) != 0).findFirst().orElse(up.newRecord(Query.SEL));
    }

    public String toString() {
        return meta.descr();
    }
}
