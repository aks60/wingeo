package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;

public enum eFurniture implements Field {
    up("0", "0", "0", "���������", "FURNLST"),
    id("4", "10", "0", "�������������", "id"),
    name("12", "96", "1", "��������", "FNAME"),
    max_p2("8", "15", "1", "����. P/2, ��", "FMAXP"),
    max_height("8", "15", "1", "����.���., ��", "FMAXH"),
    max_width("8", "15", "1", "����.���., ��", "FMAXL"),
    max_weight("8", "15", "1", "����.���, ��", "FMAXM"),
    hand_set1("16", "5", "1", "�� ��������", "hand_set1"),
    hand_set2("16", "5", "1", "�����������", "hand_set2"),
    hand_set3("16", "5", "1", "������������", "hand_set3"),
    hand_side("5", "5", "1", "������� �����", "FHAND"),
    types("5", "5", "1", "��� ���������", "FTYPE"), //0 - ��������, 1 - ��������������, -1 - ����������� ������
    ways_use("5", "5", "1", "������������ ������� ���...", "FWAYS"),
    view_open("5", "5", "1", "��� ����������", "view_open"), //(����������, ����������, �������)
    pars("12", "96", "1", "�������. ���������", "FPARS"),
    coord_lim("12", "128", "1", "���������� �����������", "FORML");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eFurniture(Object... p) {
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
    
    public String toString() {
        return meta.descr();
    }
}
