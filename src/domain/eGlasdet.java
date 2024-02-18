package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Не менять индекс поля, см. UColor
public enum eGlasdet implements Field {
    up("0", "0", "0", "Специф.групп заполнения", "GLASART"),
    id("4", "10", "0", "Идентификатор", "id"),
    color_us("5", "5", "1", "Подбор текстуры", "CTYPE"),
    color_fk("4", "10", "1", "Текстура", "CLNUM"),
    artikl_id("4", "10", "1", "Артикул", "artikl_id"),
    depth("8", "15", "1", "Толщина", "AFRIC"),
    glasgrp_id("4", "10", "0", "Группы", "glassgrp_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eGlasdet(Object... p) {
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

    public static List<Record> find(int _id, double _depth) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(glasgrp_id) == _id && rec.getDbl(depth) == _depth).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", glasgrp_id, "=", _id, "and", depth, "=", _depth);
        return (recordList.isEmpty() == true) ? new ArrayList<Record>() : recordList;
    }

    public String toString() {
        return meta.descr();
    }
}
