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
    project_id("4", "10", "0", "Проект", "project_id"); //по идее лишнее поле

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

    public static List<Record> filter2(int projectID) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> projectID == rec.getInt(project_id)).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", project_id, "=", projectID);
    }

    public static List<Record> filter3(Integer prjprodID) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> prjprodID.equals(rec.get(prjprod_id))).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", prjprod_id, "=", prjprodID);
    }
    
    public static List<Record> filter4(Integer projectID) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> projectID.equals(rec.get(project_id))).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", prjprod_id, "=", projectID);
    }
    
    public static List<Record> filter5(Integer projectID) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> projectID.equals(rec.get(project_id)) && rec.get(prjprod_id) == null).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", project_id, "=", projectID, " and prjprod_id is null");
    }

    public String toString() {
        return meta.descr();
    }
}
