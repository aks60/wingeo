package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;
import java.util.List;
import static java.util.stream.Collectors.toList;

//�� ������ ������ ����
public enum eJoinpar2 implements Field {
    up("0", "0", "0", "��������� ������������ ���������", "PARCONS"),
    id("4", "10", "0", "�������������", "id"),
    text("12", "64", "1", "�������� ���������", "PTEXT"),
    groups_id("4", "10", "0", "�������� ���������", "PNUMB"),
    joindet_id("4", "10", "1", "�����������", "joindet_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eJoinpar2(Object... p) {
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
            return data().stream().filter(rec -> rec.getInt(joindet_id) == _id).collect(toList());
        }
        return new Query(values()).select(up, "where", joindet_id, "=", _id, "order by", id);
    }

    public String toString() {
        return meta.descr();
    }
}
