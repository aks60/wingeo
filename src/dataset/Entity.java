package dataset;

import domain.eArtdet;
import domain.eArtikl;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Aksenov Sergey
 *
 * <p>
 * Генерация enum кода </p>
 */
public enum Entity {

    type, size, nullable, comment, fname;

    private static String table = null;
    private static HashMap<String, ArrayList<String>> columns = new HashMap<String, ArrayList<String>>();
    private static ArrayList<String> rangcol = new ArrayList<String>();

    private static void script() { //throws FileNotFoundException, UnsupportedEncodingException{

        try {
            String ename = "_e" + table.substring(0, 1).toUpperCase() + table.substring(1).toLowerCase();
            PrintWriter writer = new PrintWriter("src/domain/" + ename + ".java", "windows-1251");
            writer.println("");
            writer.println("package domain;");
            writer.println("import dataset.Field;");
            writer.println("import dataset.MetaField;");
            writer.println("import dataset.Record;");
            writer.println("public enum " + ename + " implements Field {");
            writer.println("up(\"0\", \"0\", \"0\", \"0\", \"" + table + "\"),");
            writer.println("id(\"4\", \"10\", \"0\", \"Идентификатор\", \"id\"),");
            int index = 0;
            for (String colname : rangcol) {
                String end = (index++ < columns.size() - 1) ? ")," : ");";
                String str = "";
                for (String attr : columns.get(colname.toLowerCase())) {
                    str = str + "\"" + attr + "\",";
                }
                writer.println(colname.toLowerCase() + "(" + str.substring(0, str.length() - 1) + end);
            }
            writer.println("private MetaField meta = new MetaField(this);");
            writer.println(ename + "(Object... p) { meta.init(p); }");
            writer.println("public void selectSql() {}");
            writer.println("public String updateSql(Record record) { return null; }");
            writer.println("public String insertSql(Record record) { return null; }");
            writer.println("public String deleteSql(Record record) { return null; }");
            writer.println("public MetaField meta() { return meta; }");
            writer.println("public Field[] fields() { return values(); }");
            writer.println("public String toString() { return meta.getDescr(); }");
            writer.println("}");
            writer.close();

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void msserver(Connection connection, String _table) {
        try {
            table = _table;
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSetMetaData md = statement.executeQuery("select a.* from " + table + " a").getMetaData();
            Statement statement2 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            for (int col = 1; col <= md.getColumnCount(); col++) {

                rangcol.add(null);
                String colname = md.getColumnName(col).toLowerCase();
                ResultSet rs = statement2.executeQuery("select CAST(sep.value as varchar(256)) as dis from sys.tables st "
                        + "inner join sys.columns sc on st.object_id = sc.object_id "
                        + "left join sys.extended_properties sep on st.object_id = sep.major_id  "
                        + "and sc.column_id = sep.minor_id  and sep.name = 'MS_Description' "
                        + "where st.name = '" + table + "' and  sc.name = '" + colname + "'");
                rs.first();

                ArrayList<String> column = newcol();
                column.set(comment.ordinal(), rs.getString("dis"));
                columns.put(colname, column);
            }
            meta(connection);

        } catch (SQLException e) {
            System.err.println(e);
        }
        script();
    }

    public static void firebird(Connection connection, String _table) {
        try {
            table = _table;
            //Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //statement.execute("ALTER TABLE " + table + " ADD ID INTEGER DEFAULT 0 NOT NULL");
            //statement.execute("CREATE GENERATOR gen_" + table);
            //statement.execute("UPDATE " + table + " SET id = gen_id(gen_" + table + ", 1)");
            //statement.execute("ALTER TABLE " + table + " ADD CONSTRAINT UNQ_" + table + " UNIQUE (ID)");
            Statement statement2 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement2.executeQuery("SELECT rf.rdb$field_position num, rf.RDB$FIELD_NAME as name, "
                    + "f.rdb$field_type as typ, f.rdb$null_flag as notnull, rf.rdb$description as comment, "
                    + "f.rdb$db_key as primary_key, f.rdb$default_value as defval FROM rdb$relation_fields rf "
                    + "JOIN rdb$fields f ON f.rdb$field_name = rf.rdb$field_source WHERE rf.rdb$relation_name = '" + table + "'");

            while (rs.next()) {
                rangcol.add(null);
                ArrayList<String> column = newcol();
                column.set(comment.ordinal(), rs.getString("comment"));
                columns.put(rs.getString("name").trim().toLowerCase(), column);;
            }
            meta(connection);

        } catch (SQLException e) {
            System.err.println(e);
        }
        script();
    }

    public static void postgres(Connection connection, String _table) {
        try {
            table = _table;
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery("SELECT DISTINCT a.attnum as num, a.attname as name,  format_type(a.atttypid, "
                    + " a.atttypmod) as typ,  a.attnotnull as notnull,  com.description as comment, coalesce(i.indisprimary,false) as primary_key, "
                    + " def.adsrc as defval FROM pg_attribute a  JOIN pg_class pgc ON pgc.oid = a.attrelid "
                    + " LEFT JOIN pg_index i ON (pgc.oid = i.indrelid AND i.indkey[0] = a.attnum) LEFT JOIN pg_description com on "
                    + " (pgc.oid = com.objoid AND a.attnum = com.objsubid) LEFT JOIN pg_attrdef def ON  (a.attrelid = def.adrelid "
                    + " AND a.attnum = def.adnum) WHERE a.attnum > 0 AND pgc.oid = a.attrelid AND pg_table_is_visible(pgc.oid) "
                    + " AND NOT a.attisdropped AND pgc.relname = '" + table + "'  ORDER BY a.attnum ");
            while (rs.next()) {
                rangcol.add(null);
                ArrayList<String> column = newcol();
                column.set(comment.ordinal(), rs.getString("comment"));
                columns.put(rs.getString("name"), column);
            }
            meta(connection);

        } catch (SQLException e) {
            System.err.println(e);
        }
        script();
    }

    public static void meta(Connection connection) {
        try {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rsColumns = md.getColumns(null, null, table, null);
            while (rsColumns.next()) {
                ArrayList<String> column = columns.get(rsColumns.getString("COLUMN_NAME").toLowerCase());
                if (column.get(comment.ordinal()) != null) {
                    String str = column.get(comment.ordinal());
                    column.set(comment.ordinal(), str.replaceAll("\n|\r\n|\"", " "));
                }
                column.set(type.ordinal(), rsColumns.getString("DATA_TYPE"));
                column.set(size.ordinal(), rsColumns.getString("COLUMN_SIZE"));
                column.set(nullable.ordinal(), rsColumns.getString("NULLABLE"));
                column.set(fname.ordinal(), rsColumns.getString("COLUMN_NAME"));
                rangcol.set(rsColumns.getInt("ORDINAL_POSITION") - 1, rsColumns.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    private static ArrayList<String> newcol() {
        ArrayList<String> column = new ArrayList<String>();
        for (Object en : Entity.values()) {
            column.add(null);
        }
        return column;
    }

    public static void printJSO(Field up) {
        try {
            //Field up = eArtdet.up;            
            System.out.println(up.tname() + " = {");
            for (int i = 0; i < up.fields().length; ++i) {
                System.out.println(up.fields()[i].name() + ": " + i + ", //" + up.fields()[i].meta().descr());
            }
            System.out.println("};");

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
