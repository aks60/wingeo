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
    up("0", "0", "0", "Дерево системы профилей", "SYSPROF"),
    id("4", "10", "0", "Идентиф.системы(NUNI)", "NUNI"),
    name("12", "64", "1", "Наимен. ветки дерева", "TEXT"),
    glas("12", "32", "1", "Заполнение по умолчанию", "ANUMB"),
    depth("12", "128", "1", "Доступные толщины стеклопакетов", "ZSIZE"),
    col1("12", "128", "1", "Доступные основные текстуры", "CCODE"),
    col2("12", "128", "1", "Доступные внутрение текстуры", "CCOD1"),
    col3("12", "128", "1", "Доступные внешние текстуры", "CCOD2"),
    coef("8", "15", "1", "Коэф. рентабельности", "KOEF"),
    imgview("5", "5", "1", "Вид со стороны", "TVIEW"),
    pref("12", "32", "1", "Замена / код", "NPREF"),
    types("5", "5", "1", "Тип конструкции", "TYPEW"), //1-окно; 4,5-двери
    note("12", "256", "1", "Примечание", "NPRIM"),
    parent_id("4", "10", "1", "Родитель", "parent_id");

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

    //Система профилей
    public static String systemProfile(int id) {
        String ret = "";
        try {
            Statement statement = Conn.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet recordset = statement.executeQuery("with recursive tree as (select * from systree where id = "
                    + id + " union all select * from systree a join tree b on a.id = b.parent_id and b.id != b.parent_id) select * from tree");
            while (recordset.next()) {
                ret = recordset.getString("name") + " / " + ret;
            }
            statement.close();

        } catch (SQLException e) {
            System.err.println(e);
        }
        return ret;
    }

    public String toString() {
        return meta.descr();
    }
}
