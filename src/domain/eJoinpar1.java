package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;
import java.util.List;
import static java.util.stream.Collectors.toList;

//Не менять индекс поля
public enum eJoinpar1 implements Field {
    up("0", "0", "0", "Параметры вар.соединений", "PARCONV"),
    id("4", "10", "0", "Идентификатор", "id"),
    text("12", "64", "1", "Значения параметра", "PTEXT"),
    groups_id("4", "10", "0", "Название параметра", "PNUMB"),
    joinvar_id("4", "10", "0", "Варианты соед.", "joinvar_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eJoinpar1(Object... p) {
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
            return data().stream().filter(rec -> rec.getInt(joinvar_id) == _id).collect(toList());
        }
        return new Query(values()).select(up, "where", joinvar_id, "=", _id, "order by", id);
    }

    public String toString() {
        return meta.descr();
    }
}
