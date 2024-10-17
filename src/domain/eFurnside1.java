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

public enum eFurnside1 implements Field {
    up("0", "0", "0", "�������� ������ ���������", "FURNLEN"),
    id("4", "10", "0", "�������������", "id"),
    side_num("5", "5", "1", "�������", "FNUMB"),
    side_use("4", "10", "0", "����������", "side_use"), //1-�������, 2-��� ��������, 3-��������� ������
    furniture_id("4", "10", "0", "���������", "furniture_id");
   
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eFurnside1(Object... p) {
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
    
    public static List<Record> filter(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(furniture_id) == _id).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", furniture_id, "=", _id);
        return (recordList.isEmpty() == true) ? new ArrayList<Record>() : recordList;
    }

    public String toString() {
        return meta.descr();
    }
}
