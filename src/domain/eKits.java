package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;

public enum eKits implements Field {
    up("0", "0", "0", "Комплекты", "KOMPLST"),
    id("4", "10", "0", "Идентификатор", "id"),
    name("12", "64", "1", "Название комплекта", "KNAME"), //для пользователя    
    groups_id("4", "10", "0", "Категории", "groups_id");
   
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

    public static Query query() {
        if (query.size() == 0) {
            query.select(up, "order by", id);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public String toString() {
        return meta.descr();
    }
}
