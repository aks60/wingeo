package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

//�� ������ ������ ����, ��. UColor
public enum eJoindet implements Field {
    up("0", "0", "0", "������������ ���.����������", "CONNSPC"),
    id("4", "10", "0", "�������������", "id"),
    color_us("5", "5", "1", "������ ��������", "CTYPE"),
    color_fk("4", "10", "1", "��������", "CLNUM"),
    artikl_id("4", "10", "1", "�������", "artikl_id"),
    joinvar_id("4", "10", "0", "�������� ����.", "joinvar_id");
   
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eJoindet(Object... p) {
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
    
    public static List<Record> filter(int _joinvar_id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(joinvar_id) == _joinvar_id).collect(toList());
        }
        return new Query(values()).select(up, "where", joinvar_id, "=", _joinvar_id, "order by", id);
    }

    public String toString() {
        return meta.descr();
    }
}
