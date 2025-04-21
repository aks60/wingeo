package dataset;

import common.eProp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.swing.JOptionPane;
import startup.App;

/**
 * ���������� ����� PostgresSQL
 */
public class Connect {

    public static boolean cryptoCheck = false; //�������� ��������� ���������
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
                    JOptionPane.showMessageDialog(null, "���������� ���������. ���������� "
                            + "\n������� ���� <������->����������� ���������� c �� \n��� ������������� ���������.", "�������", 1);
                    return null;
                } else {
                    return connection;
                }
            } catch (SQLException e) {
                System.err.println("������:getConnection() " + e);
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
                System.err.println("������:Connect.connection() �1 ");
                e.printStackTrace();
                return null;

            } catch (SQLException e) {
                System.err.println("������:Connect.connection() �2 ");
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

    //����������� ����������
    public static void reconnection() throws SQLException {
        eExcep pass = eExcep.noConn;
        try {            
            if (connection.isClosed() == true) {
                //connection.rollback();
                String num_base = eProp.base_num.getProp();
                pass = Connect.connection(eProp.getServer(num_base), eProp.getPort(num_base), eProp.getBase(num_base), eProp.user.getProp(), eProp.password.toCharArray(), null);
                if (pass == eExcep.yesConn) {
                    JOptionPane.showMessageDialog(null, "���������� �������������.", "�����", 1);
                } else {
                    JOptionPane.showMessageDialog(null, pass.mes, "�������", 1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, pass.mes + e, "�������", 1);
        }

    }

    public static eExcep connection(String server, String port, String base, String user, char[] password, String role) {
        //JOptionPane.showMessageDialog(null, server + "-" + base, "SERVER", JOptionPane.OK_OPTION);
        webapp = false;
        try {
            if (eProp.devel.equals("99")) {
                new Crypto().httpAsync("sa-okna.ru"); //������ ������ ������� �� sa-okna.ru
            } else {
                new Crypto().httpAsync(server); //������ ������ �������� �� sa-okna.ru ����� ����
            }
            if (Class.forName(driver) == null) {
                JOptionPane.showMessageDialog(App.Top.frame, eExcep.loadDrive.mes,
                        "������", JOptionPane.ERROR_MESSAGE);
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
            //System.err.println("������1:Connect.connection() " + e);
            JOptionPane.showMessageDialog(null, eExcep.findDrive, "�������", 1);
            return eExcep.findDrive;
        } catch (SQLException e) {
            System.err.println("������2:Connect.connection() " + e);
            return eExcep.noConn;
            //return eExcep.getError(e.getErrorCode());
        }
        if (cryptoCheck == false) {
            //System.out.println("dataset.Connect.connection()");
            return eExcep.noActiv;
        }
        return eExcep.yesConn;
    }

    public static void autocommit(boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            System.err.println("������:Connect.autocommit() " + e);
        }
    }

    //���������� ������ ������������   
    public static void addUser(String user, char[] password, String role) {
        try {
            connection.createStatement().executeUpdate("create user " + user + " password '" + String.valueOf(password) + "'");
            connection.createStatement().executeUpdate("grant " + role + " to " + user);

        } catch (SQLException e) {
            System.err.println("������:Connect.addUser() " + e);
            JOptionPane.showMessageDialog(null, eExcep.getError(e.getErrorCode()).mes, "��������!", 1);
        }
    }

    //�������� ������������   
    public static void deleteUser(String user) {
        try {
            connection.createStatement().executeUpdate("REVOKE TEXNOLOG_RW FROM " + user);
            connection.createStatement().executeUpdate("REVOKE MANAGER_RW FROM " + user);
            connection.createStatement().executeUpdate("REVOKE TEXNOLOG_RO FROM " + user);
            connection.createStatement().executeUpdate("REVOKE MANAGER_RO FROM " + user);
            connection.createStatement().executeUpdate("DROP USER " + user);

        } catch (SQLException e) {
            System.err.println("������:Connect.deleteUser() " + e);
        }
    }

    public static void deleteUser2(String user) throws SQLException {
        connection.createStatement().executeUpdate("REVOKE TEXNOLOG_RW FROM " + user);
        connection.createStatement().executeUpdate("REVOKE MANAGER_RW FROM " + user);
        connection.createStatement().executeUpdate("REVOKE TEXNOLOG_RO FROM " + user);
        connection.createStatement().executeUpdate("REVOKE MANAGER_RO FROM " + user);
        connection.createStatement().executeUpdate("DROP USER " + user);
    }

    //��������� ���������� ������������
    public static void modifyPassword(String user, char[] password) {
        try {
            String sql = "ALTER USER " + user + " PASSWORD '" + String.valueOf(password) + "'";
            connection.createStatement().executeUpdate(sql);
        } catch (Exception e) {
            System.err.println("������:Connect.modifyPassword() " + e);
        }
    }

    //��������� ����� ID
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
            System.err.println("������:Connect.genId() " + e);
            JOptionPane.showMessageDialog(null, "�� ������� ������������� ���� ������. ������������� ���������.", "�������", 1);
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
            System.err.println("������ ��������� ������ " + e);
            return "";
        }
    }
}
