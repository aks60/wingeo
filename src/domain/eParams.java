package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.ePrjkit.up;
import frames.UGui;
import java.util.List;
import static java.util.stream.Collectors.toList;

public enum eParams implements Field {
    up("0", "0", "0", "������ ����������", "PARLIST"),
    id("4", "10", "0", "�������������", "id"),
    text("12", "64", "1", "�������� ���������", "PNAME"),
    joint("16", "5", "1", "��������� ����������", "PCONN"),
    elem("16", "5", "1", "�������� ��������", "PVSTA"),
    glas("16", "5", "1", "��������� ����������", "PGLAS"),
    furn("16", "5", "1", "��������� ���������", "PFURN"),
    otkos("16", "5", "1", "��������� �������", "POTKO"),
    kits("16", "5", "1", "�������� ����������", "PKOMP"),
    label("12", "32", "1", "������� �� ������", "PMACR"),
    groups_id("4", "10", "1", "�������� ���������", "groups_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eParams(Object... p) {
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
            query.select(up, "order by", groups_id, ",", text);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public Query query() {
        return query;
    }
  
    public static Record find(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public static List<Record> filter(int _groups_id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _groups_id == rec.getInt(groups_id)).collect(toList());
        }
        return new Query(values()).select(up, "where", groups_id, "=", _groups_id);
    }

    public static Record newRecord2() {
        Record record = up.newRecord(Query.SEL);
        record.set(text, "");
        return record;
    }

    public String toString() {
        return meta.descr();
    }
}
