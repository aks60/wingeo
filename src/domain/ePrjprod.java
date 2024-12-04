package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.up;
import static domain.eArtikl.values;
import java.util.List;
import java.util.stream.Collectors;
import static domain.eArtikl.groups4_id;

public enum ePrjprod implements Field {
    up("0", "0", "0", "������� �����a", "EMPTY"),
    id("4", "10", "0", "�������������", "id"),
    num("5", "5", "1", "���������� ������", "num"),
    name("12", "128", "1", "�������� �������", "name"),
    script("12", "4096", "0", "������ ���������� ����", "script"),
    project_id("4", "10", "1", "�����", "project_id"),
    systree_id("4", "10", "1", "�������", "systree_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    ePrjprod(Object... p) {
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
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(null);
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? null : recordList.get(0);
    }

    public static List<Record> filter(int _project_id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _project_id == rec.getInt(project_id)).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", project_id, "=", _project_id);
    }
    
    public String toString() {
        return meta.descr();
    }
}

