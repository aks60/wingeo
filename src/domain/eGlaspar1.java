package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//�� ������ ������ ����
public enum eGlaspar1 implements Field {
    up("0", "0", "0", "�����.����� ����������", "PARGRUP"),
    id("4", "10", "0", "�������������", "id"),    
    text("12", "64", "1", "�������� ���������", "PTEXT"),
    groups_id("4", "10", "0", "�������� ���������", "PNUMB"),
    glasgrp_id("4", "10", "0", "������", "glasgrp_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eGlaspar1(Object... p) {
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
    
    public static List<Record> filter(int _glasgrp_id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(glasgrp_id) == _glasgrp_id).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", glasgrp_id, "=", _glasgrp_id);
        return (recordList.isEmpty() == true) ? new ArrayList<Record>() : recordList;
    }

    public String toString() {
        return meta.descr();
    }
}
