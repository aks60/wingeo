package common;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.Timer;

public enum eProp {

    lookandfeel("Metal", "Windows"),
    genl(System.getProperty("user.home") + "/Avers/Okna",
            "C:\\ProgramData\\Avers\\Okna"),
    url_src("http://sa-okna.ru/winweb/"),
    web_port("80"),
    typedb("fb"),
    user("sysdba"),
    port1("3050"),
    port2("3050"),
    port3("3050"),
    server1("sa-okna.ru"),
    server2("sa-okna.ru"),
    server3("sa-okna.ru"),
    sysprodID("-1"), //выбранный системный шаблон продукта
    prjprodID("-1"), //выбранный продукт для клиента
    orderID("-1"), //выбранный заказ клиента
    base_num("1"),
    old_version("0"), //переключение на пред. версию
    base1("/opt/database/fbase/bimax.fdb?encoding=win1251"),
    base2("/opt/database/fbase/bimax.fdb?encoding=win1251"),
    base3("/opt/database/fbase/bimax.fdb?encoding=win1251"),
    cmd_word("/usr/bin/oowriter ", "cmd /c start winword.exe "),
    cmd_excel("/usr/bin/oocalc ", "cmd /c start excel.exe "),
    cmd_html("/usr/bin/firefox ", "cmd /c start iexplore.exe "),
    fontname("Dialog"),
    font_size("11"),
    row_height("20");

    //Значения по умолчанию
    public String value;

    //Системные переменные
    public static String password = "*";
    public static String role = null;
    public final static Locale locale = Locale.of("ru", "RU");
    public static String typuse = "99"; //"00"-demo, "99"-developer, "01-98"-products
    public final static String version_ap = "2.0";
    public final static String version_db = "2.0";
    public static String profile = ""; //профиль разработки и тестирования
    //public static Timer timer = new Timer(1000, null);

    //Значение по умолчанию
    eProp(String value) {
        this.value = value;
    }

    //Значение по умолчанию для конкретной OS
    eProp(String value1, String value2) {
        String os = System.getProperty("os.name");
        this.value = os.equals("Linux") ? value1 : value2;
    }

    public String getProp() {
        if (typuse.equals("99") == false) {

            if (this == server1 || this == server2 || this == server3) {
                return "sa-okna.ru";
            }
            if (this == base1) {
                return "/opt/database/fbase/" + typuse + "/binet.fdb?encoding=win1251";
            }
            if (this == base2) {
                return "/opt/database/fbase/" + typuse + "/bimax.fdb?encoding=win1251";
            }
            if (this == base3) {
                return "/opt/database/fbase/" + typuse + "/binet.fdb?encoding=win1251";
            }
        }
        Preferences pref = Preferences.userRoot().node(this.getClass().getName());
        return pref.get(this.name(), this.value);
    }

    public void putProp(String str) {
        Preferences pref = Preferences.userRoot().node(this.getClass().getName());
        pref.put(this.name(), str.trim());
    }

    public static String getPort(String num) {
        return (num.equals("1")) ? eProp.port1.getProp() : (num.equals("2")) ? eProp.port2.getProp() : eProp.port3.getProp();
    }

    public static void putPort(String num, String name) {
        eProp p = (num.equals("1")) ? eProp.port1 : (num.equals("2")) ? eProp.port2 : eProp.port3;
        p.putProp(name);
    }

    public static String getServer(String num) {
        return (num.equals("1")) ? eProp.server1.getProp() : (num.equals("2")) ? eProp.server2.getProp() : eProp.server3.getProp();
    }

    public static void putServer(String num, String name) {
        eProp p = (num.equals("1")) ? eProp.server1 : (num.equals("2")) ? eProp.server2 : eProp.server3;
        p.putProp(name);
    }

    public static String getBase(String num) {
        return (num.equals("1")) ? eProp.base1.getProp() : (num.equals("2")) ? eProp.base2.getProp() : eProp.base3.getProp();
    }

    public static void putBase(String num, String name) {
        eProp p = (num.equals("1")) ? eProp.base1 : (num.equals("2")) ? eProp.base2 : eProp.base3;
        p.putProp(name);
    }
}
