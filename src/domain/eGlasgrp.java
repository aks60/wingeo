package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.up;
import static domain.eColor.up;
import frames.UGui;
import java.util.ArrayList;

public enum eGlasgrp implements Field {
    up("0", "0", "0", "������ ����������", "GLASGRP"),
    id("4", "10", "0", "�������������", "id"),
    name("12", "96", "1", "�������� ������", "GNAME"),
    gap("8", "15", "1", "�����", "GZAZO"),
    depth("12", "128", "1", "��������� �������", "BFRIC");

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
    
    public static ArrayList<Record>  filter() {
        if (Query.conf.equals("NET")) {
            return data();
        }
        return new Query(values()).select(up);
    }
    
    public String toString() {
        return meta.descr();
    }
}
