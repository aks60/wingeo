package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.up;
import static domain.ePrjkit.up;
import frames.UGui;
import java.util.List;
import static java.util.stream.Collectors.toList;

public enum eSyspar1 implements Field {
    up("0", "0", "0", "���������� ������� ��������", "PARSYSP"),
    id("4", "10", "0", "�������������", "id"),
    text("12", "64", "1", "�������� ���������", "PTEXT"),
    groups_id("4", "10", "0", "�������� ���������", "PNUMB"),
    systree_id("4", "10", "0", "�������", "systree_id"),
    fixed("16", "5", "1", "����������", "PFIXX");
  
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eSyspar1(Object... p) {
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
            return query.find(data(), id, _id);
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public static List<Record> filter(int _nuni) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(systree_id) == _nuni).collect(toList());
        }
        return new Query(values()).select(up, "where", systree_id, "=", _nuni);
    }

    public String toString() {
        return meta.descr();
    }
}
