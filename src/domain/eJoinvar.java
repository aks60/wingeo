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
    up("0", "0", "0", "�������� ����������", "CONNVAR"),
    id("4", "10", "0", "�������������", "id"),
    prio("5", "5", "1", "���������", "CPRIO"),
    name("12", "64", "1", "�������� ��������", "CNAME"),
    types("5", "5", "1", "��� ��������", "CTYPE"),
    mirr("16", "5", "1", "���������� �������������", "CMIRR"), //1 - ������������ ���������, 0 - ������ ������������ ���������
    joining_id("4", "10", "0", "����������", "joining_id");

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

    public static Query data() {
        if (query.size() == 0) {
            query.select(up, "order by", prio);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public Query query() {
        return query;
    }
    
    public static List<Record> filter(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(joining_id) == _id).collect(toList());
        }
        return new Query(values()).select(up, "where", joining_id, "=", _id, "order by", prio);
    }
    
    public String toString() {
        return meta.descr();
    }
}
