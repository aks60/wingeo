package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;

public enum eFurniture implements Field {
    up("0", "0", "0", "Фурнитура", "FURNLST"),
    id("4", "10", "0", "Идентификатор", "id"),
    name("12", "96", "1", "Название", "FNAME"),
    max_p2("8", "15", "1", "Макс. P/2, мм", "FMAXP"),
    max_height("8", "15", "1", "Макс.выс., мм", "FMAXH"),
    max_width("8", "15", "1", "Макс.шир., мм", "FMAXL"),
    max_weight("8", "15", "1", "Макс.вес, кг", "FMAXM"),
    hand_set1("16", "5", "1", "По середине", "hand_set1"),
    hand_set2("16", "5", "1", "Константная", "hand_set2"),
    hand_set3("16", "5", "1", "Вариационная", "hand_set3"),
    hand_side("5", "5", "1", "Сторона ручки", "FHAND"),
    types("5", "5", "1", "Тип фурнитуры", "FTYPE"), //0 - основная, 1 - дополнительная, -1 - фурнитурные наборы
    ways_use("5", "5", "1", "Использовать створку как...", "FWAYS"),
    view_open("5", "5", "1", "Вид открывания", "view_open"), //(поворотная, раздвижная, фрамуга)
    pars("12", "96", "1", "Использ. параметры", "FPARS"),
    coord_lim("12", "128", "1", "Координаты ограничений", "FORML");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eFurniture(Object... p) {
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
            return data().stream().filter(rec -> rec.getInt(id) == _id).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public String toString() {
        return meta.descr();
    }
}
