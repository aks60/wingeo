package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.ePrjkit.up;
import enums.UseSideTo;
import enums.UseArtiklTo;
import frames.UGui;
import java.util.HashMap;
import java.util.Map;

public enum eSysprof implements Field {
    up("0", "0", "0", "������� �������", "SYSPROA"),
    id("4", "10", "0", "�������������", "id"),
    npp("4", "10", "1", "���������", "APRIO"), //0 - 1 -  2 -  3 -  4 -  5 -  6 - 10 -  1000 - "
    use_type("5", "5", "1", "��� �������������", "ATYPE"),
    use_side("5", "5", "1", "������� �������������", "ASETS"),
    artikl_id("4", "10", "0", "�������", "artikl_id"),
    systree_id("4", "10", "0", "�������", "systree_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eSysprof(Object... p) {
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
            query.select(up, "order by", npp);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public Query query() {
        return query;
    }

    public static Record find2(int _nuni, UseArtiklTo _type) {
        if (_nuni == -3) {
            return virtualRec(_type.id);
        }
        if (Query.conf.equals("NET")) {
            HashMap<Integer, Record> mapPrio = new HashMap<Integer, Record>();
            data().stream().filter(rec -> rec.getInt(systree_id) == _nuni && rec.getInt(use_type) == _type.id)
                    .forEach(rec -> mapPrio.put(rec.getInt(npp), rec));
            int minLevel = 32767;
            for (Map.Entry<Integer, Record> entry : mapPrio.entrySet()) {

                if (entry.getKey() == 0) { //���� ������� ���������
                    return entry.getValue();
                }
                if (minLevel > entry.getKey()) { //����������� ����� �� ����������
                    minLevel = entry.getKey();
                }
            }
            if (mapPrio.size() == 0) {
                return up.newRecord(Query.SEL);
            }
            return mapPrio.get(minLevel);
        }
        Query recordList = new Query(values()).select("select first 1 * from " + up.tname() + " where "
                + systree_id.name() + " = " + _nuni + " and " + use_type.name() + " = " + _type.id + " order by " + npp.name());
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public static Record find3(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(id) == _id).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public static Record find4(int nuni, int typ, UseSideTo us1) {
        if (nuni == -3) {
            return virtualRec(typ);
        }
        return data().stream().filter(rec -> rec.getInt(systree_id) == nuni
                && rec.getInt(use_type) == typ
                && UseSideTo.MANUAL.id != rec.getInt(use_side)
                && (us1.id == rec.getInt(use_side) || UseSideTo.ANY.id == rec.getInt(use_side))
        ).findFirst().orElse(up.virtualRec(typ));
    }
    
    public static Record find5(int nuni, int type, UseSideTo us1, UseSideTo us2) {
        if (nuni == -3) {
            return virtualRec(type);
        }
        return data().stream().filter(rec -> rec.getInt(systree_id) == nuni
                && rec.getInt(use_type) == type
                && UseSideTo.MANUAL.id != rec.getInt(use_side)
                && (us1.id == rec.getInt(use_side) || us2.id == rec.getInt(use_side) || UseSideTo.ANY.id == rec.getInt(use_side))
        ).findFirst().orElse(up.virtualRec(type));
    }

    public static Record virtualRec(int typeId) {

        Record record = up.newRecord(Query.SEL);
        record.setNo(id, -3);
        record.setNo(use_type, typeId);
        record.setNo(use_side, UseSideTo.ANY.id);
        record.setNo(systree_id, -3);
        record.setNo(artikl_id, -3);
        return record;
    }

    public String toString() {
        return meta.descr();
    }
}
