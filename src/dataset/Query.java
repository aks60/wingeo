package dataset;

import static dataset.Query.INS;
import static domain.eArtikl.level1;
import static domain.eArtikl.level2;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import startup.App;

public class Query extends Table {

    public static String conf = "calc";
    private static String schema = "";
    public static String INS = "INS";
    public static String SEL = "SEL";
    public static String UPD = "UPD";
    public static String DEL = "DEL";
    private Object[] sql = null;
    public static LinkedHashSet<Query> listOpenTable = new LinkedHashSet<Query>();

    public Query(Query query) {
        this.root = query;
    }

    public Query(Field... fields) {
        this.root = this;
        mapQuery.put(fields[0].tname(), this);
        for (Field field : fields) {
            if (!field.name().equals("up")) {
                if (mapQuery.get(field.tname()) == null) {
                    mapQuery.put(field.tname(), new Query(this));
                }
                mapQuery.get(field.tname()).fields.add(field);
            }
        }
    }

    public Query(Field[]... fieldsArr) {
        this.root = this;
        mapQuery.put(fieldsArr[0][0].tname(), this);
        for (Field[] fields : fieldsArr) {
            for (Field field : fields) {
                if (!field.name().equals("up")) {
                    if (mapQuery.get(field.tname()) == null) {
                        mapQuery.put(field.tname(), new Query(this));
                    }
                    mapQuery.get(field.tname()).fields.add(field);
                }
            }
        }
    }

    public Query table(Field field) {
        return root.mapQuery.get(field.tname());
    }

    public Query select(Object... s) {

        String tName = "";
        String sql = String.valueOf(s[0]);
        if (String.valueOf(s[0]).substring(0, 6).equalsIgnoreCase("select") == false) {
            sql = ""; //для улучшения см. класс Scanner
            for (Object p : s) {
                if (p instanceof Field) {
                    Field f = (Field) p;
                    if ("up".equals(f.name())) {
                        tName = f.tname();
                        //System.out.println("start " + tName + "  " + Query.conf);
                        sql = sql + " " + f.tname();
                    } else {
                        sql = sql + " " + f.tname() + "." + f.name();
                    }
                } else {
                    sql = sql + " " + p;
                }
            }
            String str = "";
            for (Map.Entry<String, Query> q : root.mapQuery.entrySet()) {
                Query table = q.getValue();

                table.clear(); //удаляем данные

                for (Field field : table.fields) {
                    str = str + ", " + field.tname() + "." + field.name();
                }
            }
            sql = "select " + str.toLowerCase().substring(1, str.length()) + " from " + sql;
            sql = sql.replace("' ", "'");
            sql = sql.replace(" '", "'");
        }
        System.out.println("SQL-SELECT:" + tName + " - " + sql);
        try {
            Statement statement = Conn.connection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet recordset = statement.executeQuery(sql);
            this.sql = s;
            while (recordset.next()) {
                int selector = 0;
                for (Map.Entry<String, Query> q : root.mapQuery.entrySet()) {
                    Query table = q.getValue();
                    Record record = table.fields.get(0).newRecord(SEL);
                    table.add(record);
                    for (int index = 0; index < table.fields.size(); index++) {
                        Field field = table.fields.get(index);
                        Object value = recordset.getObject(1 + index + selector);
                        record.setNo(field, value);
                    }
                    selector = selector + table.fields.size();
                }
            }
            statement.close();
            Conn.close();
            //System.out.println("stop " + tName);
            return this;

        } catch (SQLException e) {
            System.err.println(e + "  " + sql);
            return null;
        }
    }

    public void insert(Record record) {
        try {
            Statement statement = Conn.connection().createStatement();
            //если нет, генерю сам
            String nameCols = "", nameVals = "";
            //цикл по полям таблицы
            for (int k = 0; k < fields.size(); k++) {
                Field field = fields.get(k);
                if (field.meta().type() != Field.TYPE.OBJ) {
                    nameCols = nameCols + field.name() + ",";
                    nameVals = nameVals + wrapper(record, field) + ",";
                }
            }
            if (nameCols != null && nameVals != null) {
                nameCols = nameCols.substring(0, nameCols.length() - 1);
                nameVals = nameVals.substring(0, nameVals.length() - 1);
                String sql = "insert into " + schema + fields.get(0).tname() + "(" + nameCols + ") values(" + nameVals + ")";
                System.out.println("SQL-INSERT " + sql);
                statement.executeUpdate(sql);
                record.setNo(0, SEL);
            }
            Conn.close();

        } catch (SQLException e) {
            System.err.println("Ошибка:Query.insert() " + e);
        }
    }

    public void insert2(Record record) throws SQLException {
        Statement statement = Conn.connection().createStatement();
        //если нет, генерю сам
        String nameCols = "", nameVals = "";
        //цикл по полям таблицы
        for (int k = 0; k < fields.size(); k++) {
            Field field = fields.get(k);
            if (field.meta().type() != Field.TYPE.OBJ) {
                nameCols = nameCols + field.name() + ",";
                nameVals = nameVals + wrapper(record, field) + ",";
            }
        }
        if (nameCols != null && nameVals != null) {
            nameCols = nameCols.substring(0, nameCols.length() - 1);
            nameVals = nameVals.substring(0, nameVals.length() - 1);
            String sql = "insert into " + schema + fields.get(0).tname() + "(" + nameCols + ") values(" + nameVals + ")";
            System.out.println("SQL-INSERT " + sql);
            statement.executeUpdate(sql);
            record.setNo(0, SEL);
        }
        Conn.close();
    }

    public void update(Record record) {
        try {
            String nameCols = "";
            Statement statement = statement = Conn.connection().createStatement();
            //цикл по полям таблицы
            for (Field field : fields) {
                if (field.meta().type() != Field.TYPE.OBJ) {
                    nameCols = nameCols + field.name() + " = " + wrapper(record, field) + ",";
                }
            }
            Field[] f = fields.get(0).fields();
            if (nameCols.isEmpty() == false) {
                nameCols = nameCols.substring(0, nameCols.length() - 1);
                String sql = "update " + schema + fields.get(0).tname() + " set "
                        + nameCols + " where " + f[1].name() + " = " + wrapper(record, f[1]);
                System.out.println("SQL-UPDATE " + sql);
                statement.executeUpdate(sql);
                record.setNo(0, SEL);
            }
            Conn.close();

        } catch (SQLException e) {
            System.err.println("Ошибка:Query.update() " + e);
        }
    }

    public void update2(Record record) throws SQLException {
        String nameCols = "";
        Statement statement = Conn.connection().createStatement();
        //цикл по полям таблицы
        for (Field field : fields) {
            if (field.meta().type() != Field.TYPE.OBJ) {
                nameCols = nameCols + field.name() + " = " + wrapper(record, field) + ",";
            }
        }
        Field[] f = fields.get(0).fields();
        if (nameCols.isEmpty() == false) {
            nameCols = nameCols.substring(0, nameCols.length() - 1);
            String sql = "update " + schema + fields.get(0).tname() + " set "
                    + nameCols + " where " + f[1].name() + " = " + wrapper(record, f[1]);
            System.out.println("SQL-UPDATE " + sql);
            statement.executeUpdate(sql);
            record.setNo(0, SEL);
        }
        Conn.close();
    }

    public boolean delete(Record record) {
        try {
            Statement statement = Conn.connection().createStatement();
            Field[] f = fields.get(0).fields();
            String sql = "delete from " + schema + fields.get(0).tname() + " where " + f[1].name() + " = " + wrapper(record, f[1]);
            System.out.println("SQL-DELETE " + sql);
            statement.executeUpdate(sql);
            Conn.close();
            return true;

        } catch (SQLException e) {
            System.out.println("Ошибка:Query.delete() " + e);
            if (Conn.web() == false && e.getErrorCode() == 335544466) {
                JOptionPane.showMessageDialog(App.active, "Нельзя удалить запись на которую имеются ссылки из других форм", "SQL предупреждение", JOptionPane.INFORMATION_MESSAGE);
            }
            return false;
        }
    }

    public int delete2(Record record) throws SQLException {
        Statement statement = Conn.connection().createStatement();
        Field[] f = fields.get(0).fields();
        String sql = "delete from " + schema + fields.get(0).tname() + " where " + f[1].name() + " = " + wrapper(record, f[1]);
        System.out.println("SQL-DELETE " + sql);
        int ret = statement.executeUpdate(sql);
        Conn.close();
        return ret;
    }

    public void refresh() {
        select(sql);
    }

    public void execsql() {
        try {
            for (Record record : this) {
                String message = record.validateRec(fields);
                if (record.get(0).equals(Query.UPD) || record.get(0).equals(INS)) {

                    if (record.validateRec(fields) != null) { //проверка на корректность ввода данных                        
                        JOptionPane.showMessageDialog(App.Top.frame, "Таблица <" + fields.get(0).fields()[0].meta().descr + ">.\n" + message
                                + ".\nЗапись не будет сохранена.", "Предупреждение", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        if (Query.INS.equals(record.getStr(0))) {
                            insert(record);
                        } else if (Query.UPD.equals(record.getStr(0))) {
                            update(record);
                        } else if (Query.DEL.equals(record.getStr(0))) {
                            delete(record);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Query.execsql() " + e);
        }
    }

    public String wrapper(Record record, Field field) {
        try {
            if (record.get(field) == null) {
                return null;
            } else if (Field.TYPE.STR.equals(field.meta().type())) {
                return "'" + record.getStr(field) + "'";
            } else if (Field.TYPE.BOOL.equals(field.meta().type())) {
                return "'" + record.getStr(field) + "'";
            } else if (Field.TYPE.DATE.equals(field.meta().type())) {
                if (record.get(field) instanceof java.util.Date) {
                    return " '" + new SimpleDateFormat("dd.MM.yyyy").format(record.getDate(field)) + "' ";
                } else {
                    return " '" + record.getStr(field) + "' ";
                }
            }
            return record.getStr(field);
        } catch (Exception e) {
            System.err.println("Query.vrapper() " + e);
            return null;
        }
    }

    public boolean isUpdate() {
        for (Record record : this) {
            if ("UPD".equals(record.getStr(0)) || "INS".equals(record.getStr(0))) {
                return true;
            }
        }
        return false;
    }

    public Query sql(List<Record> data, Field field) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().collect(Collectors.toList()));
        } else {
            select(field.fields()[0]);

        }
        return this;
    }

    public Query sql(List<Record> data, Field field, Object value) {
        clear();
        if (Query.conf.equals("calc")) {
            if (value instanceof Integer) {
                addAll(data.stream().filter(rec -> rec.getInt(field) == Integer.valueOf(value.toString())).collect(Collectors.toList()));
            } else if (value instanceof String) {
                addAll(data.stream().filter(rec -> rec.getStr(field) == value).collect(Collectors.toList()));
            }
        } else {
            select(field.fields()[0], "where", field, "=", value);

        }
        return this;
    }

    public Query sql(List<Record> data, Field field, int value, Field field2, int value2) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value && rec.getInt(field2) == value2).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "=", value, "and", field2, "=", value2);

        }
        return this;
    }

    public Query sq2(List<Record> data, Field field, int value, Field field2, int value2) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value || rec.getInt(field2) == value2).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "=", value, "or", field2, "=", value2);

        }
        return this;
    }

    public Query sq3(List<Record> data, Field field, int value, Field field2, int value2) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value && rec.getInt(field2) != value2).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "=", value, "and", field2, "!=", value2);

        }
        return this;
    }

    public Query sql(List<Record> data, Field field, int value, Field field2, Field field3) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value && rec.getInt(field2) == rec.getInt(field3)).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "=", value, "and", field2, "=", field3);
        }
        return this;
    }

    public Query sq2(List<Record> data, Field field, int value, Field field2, Field field3) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value && rec.getInt(field2) != rec.getInt(field3)).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "=", value, "and", field2, "!=", field3);
        }
        return this;
    }

    public Query sql(List<Record> data, Field field, int value2, int value3) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value2 || rec.getInt(field) == value3)
                    .collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "in (", value2, ",", value3, ")");

        }
        return this;
    }

    public Query sql(List<Record> data, Field field, int value2, int value3, int value4) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value2 || rec.getInt(field) == value3 || rec.getInt(field) == value4
            ).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "in (", value2, ",", value3, ",", value4 + ")");

        }
        return this;
    }

    public Query sql(List<Record> data, Field field, int value, Field field2, int value2, int value3) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value
                    && (rec.getInt(field2) == value2 || rec.getInt(field2) == value3)
            ).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "=", value, "and", field2, "in (", value2, ",", value3 + ")");

        }
        return this;
    }

    public Query sql(List<Record> data, Field field, int value, Field field2, int value2, int value3, int value4) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value
                    && (rec.getInt(field2) == value2 || rec.getInt(field2) == value3 || rec.getInt(field2) == value4)
            ).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "=", value, "and", field2, "in (", value2, ",", value3, ",", value4 + ")");

        }
        return this;
    }

    public Query sql(List<Record> data, Field field, int value, Field field2, int value2, Field field3, int value3) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value
                    && rec.getInt(field2) == value2 && rec.getInt(field3) == value3
            ).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "=", value, "and", field2, "=", value2, "and", field3, "=", value3);

        }
        return this;
    }

    public Query sq2(List<Record> data, Field field, int value, Field field2, int value2, Field field3, int value3) {
        clear();
        if (Query.conf.equals("calc")) {
            addAll(data.stream().filter(rec -> rec.getInt(field) == value
                    && rec.getInt(field2) > value2 && rec.getInt(field3) > value3
            ).collect(Collectors.toList()));
        } else {
            select(field.fields()[0], "where", field, "=", value, "and", field2, ">", value2, "and", field3, ">", value3);

        }
        return this;
    }

    public void join(List<Record> data, List<Record> data2, Field field, Field field2) {
        clear();
        for (Record rec : data) {
            for (Record rec2 : data2) {
                if (rec.getInt(field) == rec2.getInt(field2)) {
                    add(rec2);
                }
            }
        }
    }

    public Query sort(Field... field) {
        try {
            if (field.length == 1 && field[0].meta().type() == Field.TYPE.INT) {
                this.sort((rec1, rec2) -> rec1.getInt(field[0]) < rec2.getInt(field[0]) ? 1 : -1);

            } else if (field.length == 1 && field[0].meta().type() == Field.TYPE.DBL) {
                this.sort((rec1, rec2) ->  Double.compare(rec1.getDbl(field[0]), rec2.getDbl(field[0])));
                
            } else if (field.length == 1 && field[0].meta().type() == Field.TYPE.STR) {
                this.sort((rec1, rec2) -> rec1.getStr(field[0]).compareTo(rec2.getStr(field[0])));

            } else if (field.length == 1 && field[0].meta().type() == Field.TYPE.DATE) {
                this.sort((rec1, rec2) -> {
                    if (rec1.getDate(field[0]) == null || rec2.getDate(field[0]) == null) {
                        return 0;
                    }
                    return rec1.getDate(field[0]).compareTo(rec2.getDate(field[0]));
                });

            } else if (field.length == 2 && field[0].meta().type() == Field.TYPE.INT && field[1].meta().type() == Field.TYPE.INT) {
                this.sort((rec1, rec2) -> {
                    if (rec1.getInt(field[0]) == rec2.getInt(field[0])) {
                        return (rec1.getInt(field[1]) > rec2.getInt(field[1])) ? 1 : -1;
                    } else {
                        return (rec1.getInt(field[0]) > rec2.getInt(field[0])) ? 1 : -1;
                    }
                });

            } else if (field.length == 2 && field[0].meta().type() == Field.TYPE.INT && field[1].meta().type() == Field.TYPE.STR) {
                this.sort((rec1, rec2) -> {
                    if (rec1.getInt(field[0]) == rec2.getInt(field[0])) {
                        return rec1.getStr(field[1]).compareTo(rec2.getStr(field[1]));
                    } else {
                        return (rec1.getInt(field[0]) > rec2.getInt(field[0])) ? 1 : -1;
                    }
                });
            } else {
                System.err.println("ВНИМАНИЕ! Ошибка сортировки.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Query.sort()");
        }
        return this;
    }
}
