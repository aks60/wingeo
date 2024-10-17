package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;

public enum eKits implements Field {
    up("0", "0", "0", "���������", "KOMPLST"),
    id("4", "10", "0", "�������������", "id"),
    name("12", "64", "1", "�������� ���������", "KNAME"), //��� ������������    
    groups_id("4", "10", "0", "���������", "groups_id");
   
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eKits(Object... p) {
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
    
    public String toString() {
        return meta.descr();
    }
}
