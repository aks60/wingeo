package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.ePrjkit.up;
import frames.UGui;
import java.util.List;
import static java.util.stream.Collectors.toList;

public enum eParams implements Field {
    up("0", "0", "0", "Список параметров", "PARLIST"),
    id("4", "10", "0", "Идентификатор", "id"),
    text("12", "64", "1", "Значения параметра", "PNAME"),
    joint("16", "5", "1", "Параметры соединений", "PCONN"),
    elem("16", "5", "1", "Парметры составов", "PVSTA"),
    glas("16", "5", "1", "Параметры заполнений", "PGLAS"),
    furn("16", "5", "1", "Параметры фурнитуры", "PFURN"),
    otkos("16", "5", "1", "Параметры откосов", "POTKO"),
    kits("16", "5", "1", "Парметры комплектов", "PKOMP"),
    label("12", "32", "1", "Надпись на эскизе", "PMACR"),
    groups_id("4", "10", "1", "Название параметра", "groups_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eParams(Object... p) {
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
            query.select(up, "order by", groups_id, ",", text);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public Query query() {
        return query;
    }
  
    public static Record find(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public static List<Record> filter(int _groups_id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _groups_id == rec.getInt(groups_id)).collect(toList());
        }
        return new Query(values()).select(up, "where", groups_id, "=", _groups_id);
    }

    public static Record newRecord2() {
        Record record = up.newRecord(Query.SEL);
        record.set(text, "");
        return record;
    }

    public String toString() {
        return meta.descr();
    }
}
