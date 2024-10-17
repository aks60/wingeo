package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import frames.UGui;

public enum eGroups implements Field {
    up("0", "0", "0", "�����������", "EMPTY"),
    id("4", "10", "0", "�������������", "id"),
    npp("4", "10", "1", "����� �/�", "npp"),
    grup("5", "5", "0", ""
            + "1-��������� �������. \n"
            + "2-������ ������� \n"
            + "3-����� ��  \n"
            + "4-������� �� \n"
            + "5-������� �� \n"
            + "6-��������� �� \n"
            + "7-��������� �����.������ \n"
            + "8-��������� ������� \n"
            + "9-��������� ������ \n"
            + "10-��������� ����������", "grup"),
    name("12", "96", "1", "�������� ������", "name"),
    val("8", "15", "1", "��������", "coeff");
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eGroups(Object... p) {
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
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "='", _id, "'");
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public String toString() {
        return meta.descr();
    }
}
