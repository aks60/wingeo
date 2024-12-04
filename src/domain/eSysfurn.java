package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.ePrjkit.up;
import frames.UGui;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum eSysfurn implements Field {
    up("0", "0", "0", "��������� ������� ��������", "SYSPROS"),
    id("4", "10", "0", "�������������", "id"),
    npp("5", "5", "1", "��������� �������", "FPORN"),
    replac("5", "5", "1", "������", "FWHAT"),
    side_open("5", "10", "1", "������� ���������� �� ���������", "side_open"),
    hand_pos("5", "10", "1", "������������ ����� �� ���������", "hand_pos"),
    furniture_id("4", "10", "1", "���������", "furniture_id"),
    artikl_id1("4", "10", "1", "������� ����� �� ���������", "artikl_id1"),
    artikl_id2("4", "10", "1", "�������� ������� (�����) �� ���������", "artikl_id2"),
    systree_id("4", "10", "0", "������� ��������", "systree_id");
   
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

    public static Query data() {
        if (query.size() == 0) {
            query.select(up, "order by", npp, ",", id); //id - �.�. ��� �����������. �����. ������� ����.
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public Query query() {
        return query;
    }
    
    public static Record find2(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(id) == _id).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id, "order by", npp);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public static Record find3(int _nuni) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(systree_id) == _nuni).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", systree_id, "=", _nuni, "order by", npp, ",", id); //id - �.�. ��� ����������. �����. ������� ����.
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public static Record find4(int _nuni, int _side_open) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(systree_id) == _nuni && rec.getInt(side_open) == _side_open).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", systree_id, "=", _nuni, "and", side_open, "=", _side_open, "order by", npp);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
  
    public static List<Record> filter(int _nuni) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(systree_id) == _nuni).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", systree_id, "=", _nuni, "order by", npp);
        return (recordList.isEmpty() == true) ? new ArrayList<Record>() : recordList;
    }

    public String toString() {
        return meta.descr();
    }
}
