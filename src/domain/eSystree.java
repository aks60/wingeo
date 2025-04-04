package domain;

import dataset.Conn;
import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public enum eSystree implements Field {
    up("0", "0", "0", "������ ������� ��������", "SYSPROF"),
    id("4", "10", "0", "�������.�������(NUNI)", "NUNI"),
    npp("4", "10", "1", "����� �/�", "TEXT"),
    name("12", "64", "1", "������. ����� ������", "TEXT"),
    glas("12", "32", "1", "���������� �� ���������", "ANUMB"),
    depth("12", "128", "1", "��������� ������� �������������", "ZSIZE"),
    col1("12", "128", "1", "��������� �������� ��������", "CCODE"),
    col2("12", "128", "1", "��������� ��������� ��������", "CCOD1"),
    col3("12", "128", "1", "��������� ������� ��������", "CCOD2"),
    coef("8", "15", "1", "����. ��������������", "KOEF"),
    imgview("5", "5", "1", "��� �� �������", "TVIEW"),
    pref("12", "32", "1", "������ / ���", "NPREF"),
    types("5", "5", "1", "��� �����������", "TYPEW"), //1-����; 4,5-�����
    note("12", "256", "1", "����������", "NPRIM"),
    parent_id("4", "10", "1", "��������", "parent_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eSystree(Object... p) {
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

    public static String patch(int _nuni, String patch) {
        List<Record> recordList = null;
        if (Query.conf.equals("NET")) {
            recordList = data().stream().filter(rec -> _nuni == rec.getInt(id)).collect(Collectors.toList());
        } else {
            recordList = new Query(values()).select(up, "where", id, "=", _nuni);
        }
        Record record = recordList.get(0);
        if (record.getInt(id) == record.getInt(parent_id)) {
            return patch + record.getStr(name);
        }
        patch = patch + patch(record.getInt(parent_id), patch);
        return patch + " / " + record.getStr(name);
    }

    public static Record find(int _nuni) {
        if (_nuni == -3) {
            return record();
        }
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _nuni == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _nuni);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public static Record record() {
        Record record = up.newRecord(Query.SEL);
        record.setNo(id, -3);
        record.setNo(glas, "0x0x0x0");
        return record;
    }

    //������� ��������
    public static String nameSysprof(int ID) {
        String str = "";
        if (Query.conf.equals("NET")) {
            try {

                Record systreeRec = find(ID);
                systreeRec = find(systreeRec.getInt(parent_id));
                while (systreeRec.getInt(id) != systreeRec.getInt(parent_id)) {
                    str = systreeRec.getStr(name) + "/" + str;
                    systreeRec = find(systreeRec.getInt(parent_id));
                }
                str = systreeRec.getStr(name) + "/" + str;
                return str.substring(0, str.length() - 1);

            } catch (Exception e) {
                System.err.println(e);
                return "";
            }
        }
        try {
            Statement statement = Conn.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet recordset = statement.executeQuery("with recursive tree as (select * from systree where id = "
                    + ID + " union all select * from systree a join tree b on a.id = b.parent_id and b.id != b.parent_id) select * from tree");
            recordset.next();
            while (recordset.next()) {
                if (str.isEmpty() == true) {
                    str = recordset.getString("name");
                } else {
                    str = recordset.getString("name") + "/" + str;
                }
            }
            statement.close();

        } catch (SQLException e) {
            System.err.println(e);
        }
        return str;
    }

    public String toString() {
        return meta.descr();
    }
}
