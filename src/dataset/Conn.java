package dataset;

import frames.PathToDb;
import frames.UGui;
import frames.swing.FrameToFile;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import startup.App;

/**
 * Соединение через PostgresSQL
 */
public class Conn {

    private static boolean application = false;
    private static Connection connection = null;
    protected static Statement statement = null;
    protected static boolean autoCommit = false;
    public final static String driver = "org.firebirdsql.jdbc.FBDriver";
    public final static String fbserver = "jdbc:firebirdsql:";
    public String url = "";

    public static Connection connection() {

        if (application == true) {
            return connection;

        } else {
            try {
                Context initContext = new InitialContext();
                DataSource dataSource = (DataSource) initContext.lookup("java:/comp/env/jdbc/winweb");
                Connection connection = dataSource.getConnection();
                connection.setAutoCommit(true);
                return connection;

            } catch (NamingException e) {
                System.err.println("Ошибка:Conn.connection() №1 ");
                e.printStackTrace();
                return null;

            } catch (SQLException e) {
                System.err.println("Ошибка:Conn.connection() №2 ");
                e.printStackTrace();
                return null;
            }
        }
    }

    public static boolean web() {
        return application == false;
    }

    public static void close() throws SQLException {
        if (application == false) {
            connection.close();
        }
    }

    public static void connection(Connection connect) {
        connection = connect;
    }

    public static eExcep connection(String server, String port, String base, String user, char[] password, String role) {
        try {
            application = true;

            if (Class.forName(driver) == null) {
                JOptionPane.showMessageDialog(App.Top.frame, "Ошибка загрузки файла драйвера",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
            String url = fbserver + "//" + server + ":" + port + "/" + base;
            Properties props = new Properties();
            props.setProperty("user", user.toLowerCase());
            props.setProperty("password", String.valueOf(password));
            if (role != null) {  //&& user.equalsIgnoreCase("sysdba") == false) {
                props.setProperty("roleName", role);
            }
            props.setProperty("encoding", "win1251");
            connection = DriverManager.getConnection(url, props);
            connection.setAutoCommit(true);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        } catch (ClassNotFoundException e) {
            System.err.println(e);
            return eExcep.findDrive;
        } catch (SQLException e) {
            System.err.println(e);
            return eExcep.getError(e.getErrorCode());
        }
        return eExcep.yesConn;
    }

    public static  void prepareConnectBaseNumb(String num_base) {
        List.of(App.values()).stream().filter(el -> el.frame != null && el != App.Top).forEach(el
                -> UGui.findComponents(el.frame.getRootPane(), JTable.class).forEach(c -> UGui.stopCellEditing(c)));
        List.of(App.values()).stream().filter(el -> el.frame != null && el != App.Top).forEach(el -> el.frame.dispose());
        Query.listOpenTable.forEach(q -> q.execsql());
        Query.listOpenTable.forEach(q -> q.clear());
        
        PathToDb pathToDb = new PathToDb(App.Top.frame, num_base);
        FrameToFile.setFrameSize(pathToDb);
        pathToDb.setVisible(true);
    }  
    
    public static void autocommit(boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            System.err.println("Ошибка:Conn.autocommit() " + e);
        }
    }

    //Добавление нового пользователя   
    public static void addUser(String user, char[] password, String role) {
        try {
            connection.createStatement().executeUpdate("create user " + user + " password '" + String.valueOf(password) + "'");
            connection.createStatement().executeUpdate("grant " + role + " to " + user);

        } catch (SQLException e) {
            System.err.println("Ошибка:Conn.addUser() " + e);
            JOptionPane.showMessageDialog(null, eExcep.getError(e.getErrorCode()).mes, "ВНИМАНИЕ!", 1);
        }
    }

    //Удаление пользователя   
    public static void deleteUser(String user) {
        try {
            connection.createStatement().executeUpdate("REVOKE TEXNOLOG_RW FROM " + user);
            connection.createStatement().executeUpdate("REVOKE MANAGER_RW FROM " + user);
            connection.createStatement().executeUpdate("REVOKE TEXNOLOG_RO FROM " + user);
            connection.createStatement().executeUpdate("REVOKE MANAGER_RO FROM " + user);
            connection.createStatement().executeUpdate("DROP USER " + user);

        } catch (SQLException e) {
            System.err.println("Ошибка:Conn.deleteUser() " + e);
        }
    }

    public static void deleteUser2(String user) throws SQLException {
        connection.createStatement().executeUpdate("REVOKE TEXNOLOG_RW FROM " + user);
        connection.createStatement().executeUpdate("REVOKE MANAGER_RW FROM " + user);
        connection.createStatement().executeUpdate("REVOKE TEXNOLOG_RO FROM " + user);
        connection.createStatement().executeUpdate("REVOKE MANAGER_RO FROM " + user);
        connection.createStatement().executeUpdate("DROP USER " + user);
    }

    //Изменение параметров пользователя
    public static void modifyPassword(String user, char[] password) {
        try {
            String sql = "ALTER USER " + user + " PASSWORD '" + String.valueOf(password) + "'";
            connection.createStatement().executeUpdate(sql);
        } catch (Exception e) {
            System.err.println("Ошибка:Conn.modifyPassword() " + e);
        }
    }

    //Генератор ключа ID
    public static int genId(Field field) {
        try {
            int next_id = 0;
            Connection conn = connection();
            Statement statement = conn.createStatement();
            String sql = "SELECT GEN_ID(gen_" + field.tname() + ", 1) FROM RDB$DATABASE";
            ResultSet rs = statement.executeQuery(sql);
            /*String mySeqv = table_name + "_id_seq";
            ResultSet rs = statement.executeQuery("SELECT nextval('" + mySeqv + "')");*/
            if (rs.next()) {
                next_id = rs.getInt("GEN_ID");
            }
            rs.close();
            close();
            return next_id;
        } catch (SQLException e) {
            System.err.println("Ошибка генерации ключа " + e);
            return 0;
        }
    }

    public static String version() {
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT rdb$get_context('SYSTEM', 'ENGINE_VERSION') as version from rdb$database";
            ResultSet rs = statement.executeQuery(sql);
            rs.next();
            String v = rs.getString("VERSION");
            rs.close();
            return v;

        } catch (SQLException e) {
            System.err.println("Ошибка получения версии " + e);
            return "";
        }
    }
}
