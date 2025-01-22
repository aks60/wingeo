package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.List;
import static java.util.stream.Collectors.toList;

//�� ������ ������ ����, ��. UColor
public enum eElemdet implements Field {
    up("0", "0", "0", "������������ ��������", "VSTASPC"),
    id("4", "10", "0", "�������������", "id"),
    color_us("5", "5", "1", "������ ��������", "CTYPE"), // 0 - ������� ������� 11 - ������� 31 - ��������
    color_fk("4", "10", "1", "��������", "CLNUM"),
    artikl_id("4", "10", "1", "�������", "artikl_id"),
    element_id("4", "10", "0", "�������", "element_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eElemdet(Object... p) {
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

    public static List<Record> find(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(element_id) == _id).collect(toList());
        }
        return new Query(values()).select(up, "where", element_id.name(), "=", _id);
    }

    public String toString() {
        return meta.descr();
    }
}
