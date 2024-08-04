package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.up;
import static domain.eColor.code;
import static domain.eColor.groups_id;
import static domain.eColor.name;
import static domain.eColor.up;
import static domain.eParams.up;
import frames.UGui;
import java.util.ArrayList;
import java.util.stream.Collectors;

public enum eGlasgrp implements Field {
    up("0", "0", "0", "Группы заполнения", "GLASGRP"),
    id("4", "10", "0", "Идентификатор", "id"),
    name("12", "96", "1", "Название группы", "GNAME"),
    gap("8", "15", "1", "Зазор", "GZAZO"),
    depth("12", "128", "1", "Доступные толщины", "BFRIC");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eGlasgrp(Object... p) {
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

    public Query getQuery() {
        return query;
    }
    
    public static void select2(Query q) {
        q.clear();
        if (Query.conf.equals("calc")) {
            q.addAll(query().stream().sorted((o1, o2) -> o1.getStr(name)
                    .compareTo(o2.getStr(name))).collect(Collectors.toList()));
        } else {
            q.select(up, "order by", name);
        }
    }
    
    public static ArrayList<Record>  findAll() {
        if (Query.conf.equals("calc")) {
            return query();
        }
        return new Query(values()).select(up);
    }
    
    public static Record find(int glasgrpId) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> rec.getInt(id) == glasgrpId).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", glasgrpId);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public String toString() {
        return meta.descr();
    }
}
