package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum eFurnside2 implements Field {
    up("0", "0", "0", "Ограничения сторон для спецификации фурнитуры", "FURNLES"),
    id("4", "10", "0", "Идентификатор", "id"),
    side_num("5", "5", "1", "Номер стороны", "LNUMB"), //-2 - горизонтальная, -1 - вертикальная, 1 - нижняя, 2 - правая, 3 - верхняя, 4 - левая, 5 - нижняя(для трапеции), 6 - Нижняя(для арки)"
    len_min("8", "15", "1", "Мин.длина, мм", "LMINL"),
    len_max("8", "15", "1", "Макс.длина, мм", "LMAXL"),
    ang_min("8", "15", "1", "Мин.угол, градусы", "LMINU"),
    ang_max("8", "15", "1", "Макс.угол, градусы", "LMAXU"),
    furndet_id("4", "10", "0", "Детализация", "furndet_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eFurnside2(Object... p) {
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

    public static List<Record> find(int _id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(furndet_id) == _id).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", furndet_id, "=", _id);
        return (recordList.isEmpty() == true) ? new ArrayList() : recordList;
    }

    public String toString() {
        return meta.descr();
    }
}
