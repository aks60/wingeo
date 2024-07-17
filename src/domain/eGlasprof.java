package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.up;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum eGlasprof implements Field {
    up("0", "0", "0", "Профили в группе заполнения", "GLASPRO"),
    id("4", "10", "0", "Идентификатор", "id"),
    gsize("8", "15", "1", "Размер от оси до стеклопакета, мм", "ASIZE"),
    inside("16", "5", "1", "Внутреннее заполнение", "toin"),
    outside("16", "5", "1", "Внншнее заполнение", "toout"),
    artikl_id("4", "10", "1", "Артикул", "artikl_id"),
    glasgrp_id("4", "10", "0", "Группа", "glasgrp_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eGlasprof(Object... p) {
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

    public static List<Record> findAll() {
        if (Query.conf.equals("calc")) {
            return query();
        }
        Query recordList = new Query(values()).select(up);
        return (recordList.isEmpty() == true) ? new ArrayList<Record>() : recordList;
    }

    public static List<Record> find(int glasgrpId) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(glasgrp_id) == glasgrpId).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", glasgrp_id, "=", glasgrpId);
        return (recordList.isEmpty() == true) ? new ArrayList<Record>() : recordList;
    }

    public static Record find2(int artiklId) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(artikl_id) == artiklId).findFirst().orElse(up.newRecord());
        }
        Query recordList = new Query(values()).select(up, "where", artikl_id, "=", artiklId);
        return (recordList.isEmpty() == true) ? up.newRecord() : recordList.get(0);
    }

    public String toString() {
        return meta.descr();
    }
}
