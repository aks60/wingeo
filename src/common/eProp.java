package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 *
 * ��������� ���������
 */
public enum eProp {

    lookandfeel("Metal", "Windows"),
    url_src("http://localhost:8080/winweb/"),
    web_port("8080"),
    web_start("false"),
    typedb("fb"),
    user("sysdba"),
    port1("3050"),
    port2("3050"),
    port3("3050"),
    server1("localhost"),
    server2("localhost"),
    server3("localhost"),
    sysprodID("-1"), //��������� ��������� ������ ��������
    prjprodID("-1"), //��������� ������� ��� �������
    orderID("-1"), //��������� ����� �������
    base_num("1"),
    old("0"), //������������ �� ����. ������
    base1("C:\\Okna\\fbase\\BASE.FDB?encoding=win1251", "C:\\Okna\\fbase\\BASE.FDB?encoding=win1251"),
    base2("C:\\Okna\\fbase\\BASE.FDB?encoding=win1251", "C:\\Okna\\fbase\\BASE2.FDB?encoding=win1251"),
    base3("C:\\Okna\\fbase\\BASE.FDB?encoding=win1251", "C:\\Okna\\fbase\\BASE3.FDB?encoding=win1251"),
    path_app(System.getProperty("user.home") + "/Avers/Okna", "C:\\Users\\aksenov\\Desktop\\winapp.jar"),
    path_prop(System.getProperty("user.home") + "/Avers/Okna", "C:\\ProgramData\\Avers\\Okna"), //������ ��� Arkaim
    cmd_def("I", "I"),
    cmd_word("/usr/bin/oowriter ", "cmd /c start winword.exe "),
    cmd_excel("/usr/bin/oocalc ", "cmd /c start excel.exe "),
    cmd_html("/usr/bin/firefox ", "cmd /c start iexplore.exe "),
    //    cmd_word("libreoffice -writer ", "cmd /c start winword.exe "), 
    //    cmd_excel("libreoffice -calc ", "cmd /c start excel.exe "),
    //    cmd_html("firefox ", "cmd /c start iexplore.exe "),
    fontname("Dialog"),
    fontsize("11");
    private static Properties prop = null;
    public final static String filename = "okna.property"; //��� ����� properties

    //�������� �� ���������
    public String value;

    public static String password = "*";
    public static String role = null;
    public static Locale locale = new Locale("ru", "RU");
    public static String fb = "fb";
    public static boolean dev = false;      //������� ���������� � ������������
    public static boolean locate = true;    //���������� ������ ���������� � ������������
    public static String profile = "";      //������� ���������� � ������������

    //�������� �� ���������
    eProp(String value) {
        this.value = value;
    }

    //�������� �� ��������� ��� ���������� OS
    eProp(String value1, String value2) {
        String os = System.getProperty("os.name");
        this.value = os.equals("Linux") ? value1 : value2;
    }

    //���������� ���������� �������� �� ���������� ���������� enum
    public String read() {
        load();
        if (prop.getProperty(this.name()) != null && prop.getProperty(this.name()).equals("")) { //�������� �� ��������           
            return this.value; //�� ���������
        } else {
            return prop.getProperty(this.name(), this.value); //������ � �����
        }
    }

    //������ str � Property
    public void write(String str) {
        load();
        prop.setProperty(this.name(), str.trim());
    }

    //������ property �� �����
    public static Properties load() {
        if (prop == null) {
            prop = new Properties();
            try {
                File file = new File(System.getProperty("user.dir"), filename);
                if (file.exists() == true) {

                    path_prop.value = System.getProperty("user.dir"); //�������� ���� � ����� � path_prop
                } else {
                    file = new File(path_prop.value, filename); //���� ����� ���, �������� ���
                }
                if (file.exists() == false) {
                    File mydir = new File(path_prop.value); //���� ���� ������� ��� � �� �������
                    mydir.mkdirs();
                    file.createNewFile();

                } else {
                    FileInputStream inStream = new FileInputStream(file); //������ ����� ������� ����
                    prop.load(inStream);
                    inStream.close();
                }
            } catch (FileNotFoundException e) {
                System.err.println("��� ����� ���������� " + e);
            } catch (IOException e) {
                System.err.println("������ �������� ����� property " + e);
            }
        }
        return prop;
    }

    //���������� property � ����
    public static void save() {
        try {
            File file = new File(path_prop.value, filename);
            FileOutputStream outStream = new FileOutputStream(file);
            prop.store(outStream, "");
            outStream.close();
        } catch (IOException e) {
            System.err.println("������ ���������� property � ����� " + e);
        }
    }

    public static String port(String num) {
        return (num.equals("1")) ? eProp.port1.read() : (num.equals("2")) ? eProp.port2.read() : eProp.port3.read();
    }

    public static void port(String num, String name) {
        eProp p = (num.equals("1")) ? eProp.port1 : (num.equals("2")) ? eProp.port2 : eProp.port3;
        p.write(name);
    }

    public static String server(String num) {
        return (num.equals("1")) ? eProp.server1.read() : (num.equals("2")) ? eProp.server2.read() : eProp.server3.read();
    }

    public static void server(String num, String name) {
        eProp p = (num.equals("1")) ? eProp.server1 : (num.equals("2")) ? eProp.server2 : eProp.server3;
        p.write(name);
    }

    public static String base(String num) {
        return (num.equals("1")) ? eProp.base1.read() : (num.equals("2")) ? eProp.base2.read() : eProp.base3.read();
    }

    public static void base(String num, String name) {
        eProp p = (num.equals("1")) ? eProp.base1 : (num.equals("2")) ? eProp.base2 : eProp.base3;
        p.write(name);
    }
}
