package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.code;
import static domain.eArtikl.name;
import static domain.eGroups.up;
import frames.UGui;

public enum eCurrenc implements Field {
    up("0", "0", "0", "������", "CORRENC"),
    id("4", "10", "0", "�������������", "cnumb"),
    npp("4", "10", "1", "����� �/�", "npp"),
    name("12", "32", "1", "�������� ������", "CNAME"),
    par_case1("12", "32", "1", "�����.����� ��.�.", "CRODE"),
    par_case2("12", "32", "1", "�����.����� ��.�.", "CRODM"),
    design("12", "8", "1", "�����������", "CSHOR"),
    precis("5", "5", "1", "��������", "CSIZE"),
    cross_cour("8", "15", "1", "����� ����", "CKURS"),
    check1("5", "5", "1", "�������� ", "CSETS"),
    check2("5", "5", "1", "���������� ", "CINTO");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eCurrenc(Object... p) {
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
            return data().stream().filter(rec -> rec.getInt(id) == _id).findFirst().orElse(virtualRec());
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? virtualRec() : recordList.get(0);
    }

    public static Record virtualRec() {
        Record record = up.newRecord(Query.SEL);
        record.setNo(id, -3);
        record.setNo(name, "virtual");
        record.setNo(cross_cour, 1);
        record.setNo(name, "");
        return record;
    }

    public String toString() {
        return meta.descr();
    }
}
