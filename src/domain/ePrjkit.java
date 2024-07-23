package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import static domain.ePrjprod.project_id;
import static domain.ePrjprod.up;
import static domain.ePrjprod.values;
import frames.UGui;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

public enum ePrjkit implements Field {
    up("0", "0", "0", "Комплекты изделия", "EMPTY"),
    id("4", "10", "0", "Идентификатор", "id"),
    numb("8", "15", "1", "Количество", "numb"),
    width("8", "15", "1", "Длина", "width"),
    height("8", "15", "1", "Ширина", "height"),
    color1_id("4", "10", "1", "Текстура", "color1_id"),
    color2_id("4", "10", "1", "Текстура", "color2_id"),
    color3_id("4", "10", "1", "Текстура", "color3_id"),
    angl1("8", "15", "1", "Угол", "angl1"),
    angl2("8", "15", "1", "Угол", "angl2"),
    flag("5", "5", "1", "Флаг осн-го элем. комплекта", "flag"),
    artikl_id("4", "10", "0", "Артикул", "artikl_id"),
    prjprod_id("4", "10", "1", "Изделие", "prjprod_id"),
    project_id("4", "10", "0", "Проект", "project_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    ePrjkit(Object... p) {
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

    public Record addRecord() {
        return UGui.addRecord(query, up);
    }
        
    public static Record find(int _id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(id) == _id).findFirst().orElse(up.newRecord());
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? up.newRecord() : recordList.get(0);
    }

    public static List<Record> find2(int _project_id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> _project_id == rec.getInt(project_id)).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", project_id, "=", _project_id);
    }

    public static List<Record> find3(Object _prjprod_id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.get(prjprod_id).equals(_prjprod_id)).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", prjprod_id, "=", _prjprod_id);
    }

    public String toString() {
        return meta.descr();
    }
}
