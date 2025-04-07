package dataset;

import common.eProp;
import frames.PathToDb;
import frames.UGui;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
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
public class Conntct {

    public static boolean httpcheck = false; //проверка активации программы
    public static boolean webapp = true;
    private static Connection connection = null;
    protected static Statement statement = null;
    protected static boolean autoCommit = false;
    public final static String driver = "org.firebirdsql.jdbc.FBDriver";
    public final static String fbserver = "jdbc:firebirdsql:";
    public String url = "";

    public static void setConnection(Connection connect) {
        connection = connect;
    }

    public static Connection getConnection() {
        if (webapp == false) {
            try {
                if (connection.isClosed() == true) {
                    reconnection();
                }
                if (connection.isClosed() == true) {
                    JOptionPane.showMessageDialog(null, "Соединение разорвано. Попробуйте "
                            + "\nглавное меню <Сервис->Возобновить соединение c БД \nили перезагрузите программу.", "НЕУДАЧА", 1);
                    return null;
                } else {
                    return connection;
                }
            } catch (SQLException e) {
                System.err.println("Ошибка:getConnection() " + e);
                return null;
            }

        } else {
            try {
                Context initContext = new InitialContext();
                DataSource dataSource = (DataSource) initContext.lookup("java:/comp/env/jdbc/winweb");
                //dataSource = (DataSource) initContext.lookup("java:/comp/env/jdbc/winnet");
                connection = dataSource.getConnection();
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

    public static void close() throws SQLException {
        if (webapp == true) {
            connection.close();
        }
    }

    //Возобновить соединение
    public static void reconnection() throws SQLException {
        if (connection.isClosed() == true) {
            String num_base = eProp.base_num.getProp();
            Conntct.connection(eProp.getServer(num_base), eProp.getPort(num_base), eProp.getBase(num_base), eProp.user.getProp(), eProp.password.toCharArray(), null);
        }
        if (connection.isClosed() == false) {
            JOptionPane.showMessageDialog(null, "Соединение восстановлено.", "УСПЕХ", 1);
        } else {
            JOptionPane.showMessageDialog(null, "Соединение не восстановлено.", "НЕУДАЧА", 1);
        }
    }

    public static eExcep connection(String server, String port, String base, String user, char[] password, String role) {
        webapp = false;
        try {
            //new Crypto().httpAsync(server); //сервер должен смотреть на авторизацию инвче швах
            new Crypto().httpAsync("sa-okna.ru");

            if (Class.forName(driver) == null) {
                JOptionPane.showMessageDialog(App.Top.frame, eExcep.loadDrive.mes,
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
            //System.err.println("Ошибка1:Conn.connection() " + e);
            JOptionPane.showMessageDialog(null, eExcep.findDrive, "НЕУДАЧА", 1);
            return eExcep.findDrive;
        } catch (SQLException e) {
            //System.err.println("Ошибка2:Conn.connection() " + e);
            JOptionPane.showMessageDialog(null, "Ошибка соединения SQLException", "НЕУДАЧА", 1);
            return eExcep.getError(e.getErrorCode());
        }
        if (httpcheck == false) {
            //System.out.println("dataset.Conn.connection()");
            return eExcep.noActiv;
        }
        return eExcep.yesConn;
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
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            String sql = "SELECT GEN_ID(gen_" + field.tname() + ", 1) FROM RDB$DATABASE";
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                next_id = rs.getInt("GEN_ID");
            }
            rs.close();
            close();
            return next_id;

        } catch (SQLException e) {
            System.err.println("Ошибка:Conn.genId() " + e);
            JOptionPane.showMessageDialog(null, "Не удалось сгенерировать ключ записи. Перезагрузите программу.", "НЕУДАЧА", 1);
            return -1;
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
