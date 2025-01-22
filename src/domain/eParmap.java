package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.ePrjkit.up;
import frames.UGui;
import java.util.List;
import static java.util.stream.Collectors.toList;

public enum eParmap implements Field {
    up("0", "0", "0", "Парметры текстур", "PARCOLS"),
    id("4", "10", "0", "Идентификатор", "id"),
    joint("16", "5", "1", "Параметр соединений", "joint"),
    elem("16", "5", "1", "Параметр составов", "elem"),
    glas("16", "5", "1", "Параметр стеклопакетов", "glas"),
    furn("16", "5", "1", "Параметр фурнитуры", "furn"),
    otkos("16", "5", "1", "Параметр откосов", "otkos"),
    komp("16", "5", "1", "Параметр комплектов", "PKOMP"),
    groups_id("4", "10", "1", "Название параметра", "groups_id"),
    color_id1("4", "10", "1", "Текстура профиля", "color_id1"),
    color_id2("4", "10", "1", "Текстура элемента", "color_id2");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eParmap(Object... p) {
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
  
    public static Record find(int parmapID) {

        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(id) == parmapID).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", parmapID);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public static Record find3(String name, int groupsID) {

        if (Query.conf.equals("NET")) {
            List<Record> list = data().stream().filter(rec -> rec.getInt(groups_id) == groupsID).collect(toList());
            return list.stream().filter(rec -> name.equals(eColor.find(rec.getInt(eParmap.color_id1)).getStr(eColor.name))).findFirst().orElse(up.newRecord(Query.SEL));
        }
        List<Record> list = new Query(values()).select(up, "where", groups_id, "=", groupsID);
        return list.stream().filter(rec -> name.equals(eColor.find(rec.getInt(eParmap.color_id1)).getStr(eColor.name))).findFirst().orElse(up.newRecord(Query.SEL));
    }

    public static List<Record> filter(int colorID) {

        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(color_id1) == colorID).collect(toList());
        }
        return new Query(values()).select(up, "where", color_id1, "=", colorID);
    }

    public static List<Record> filter2(int colorID, int groupsID) {

        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(color_id1) == colorID && rec.getInt(groups_id) == groupsID).collect(toList());
        }
        return new Query(values()).select(up, "where", color_id1, "=", colorID, "and", groups_id, "=", groupsID);
    }
      
    public static List<Record> filter3(int groupsID) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(groups_id) == groupsID).collect(toList());
        }
        return new Query(values()).select(up, "where", groups_id, "=", groupsID);
    }    
    
    public String toString() {
        return meta.descr();
    }
}
