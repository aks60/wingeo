package common;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.Timer;

public enum ePrefs {

    lookandfeel("Metal", "Windows"),
    genl(System.getProperty("user.home") + "/Avers/Okna",
            "C:\\ProgramData\\Avers\\Okna"), //Аркаим или Arkaim
    url_src("http://localhost:8080/winweb/"),
    web_port("8080"),
    typedb("fb"),
    user("sysdba"),
    port1("3050"),
    port2("3050"),
    port3("3050"),
    server1("31.172.66.46"),
    server2("31.172.66.46"),
    server3("31.172.66.46"),
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
    fontsize("11");

    //Значения по умолчанию
    public String value;

    //Системные переменные
    public static String password = "*";
    public static String role = null;
    public static Locale locale = Locale.of("ru", "RU");
    public static String fb = "fb";
    public static boolean dev = false;      //признак разработки и тестирования
    public static boolean locate = true;    //координаты фрейма разработки и тестирования
    public static String profile = "";      //профиль разработки и тестирования
    private static Timer timer = new Timer(1000, null);

    //Значение по умолчанию
    ePrefs(String value) {
        this.value = value;
    }

    //Значение по умолчанию для конкретной OS
    ePrefs(String value1, String value2) {
        String os = System.getProperty("os.name");
        this.value = os.equals("Linux") ? value1 : value2;
    }

    public String getProp() {
        Preferences pref = Preferences.systemRoot().node(this.getClass().getName());
        System.out.println(pref.absolutePath());
        return pref.get(this.name(), this.value);
    }

    public void putProp(String str) {
        Preferences pref = Preferences.systemRoot().node(this.getClass().getName());
        pref.put(this.name(), str.trim());
    }

    public static void getWin(Window window, JButton btn, ActionListener listener, JComponent... comp) {

        addButtonMouseListener(btn, listener);

        Preferences pref = Preferences.userNodeForPackage(window.getClass()).node(window.getClass().getName());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = window.getSize();

        frameSize.height = pref.getInt("_height", window.getHeight());
        frameSize.width = pref.getInt("_width", window.getWidth());

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        if (window.getName().equals("Setting")) {
            window.setLocation(20, 100);
        } else {
            window.setLocation((screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height - 48) / 2 + 48);
        }
        if (comp != null) {
            for (int i = 0; i < comp.length; ++i) {
                if (comp[i] instanceof JTable) {
                    JTable tab = (JTable) comp[i];
                    pref = pref.node(tab.getClass().getName());

                    tab.getColumnModel().getColumn(0).setMinWidth(40);
                    tab.getColumnModel().getColumn(0).setPreferredWidth(80);
                    tab.getColumnModel().getColumn(0).setMaxWidth(800);
                }
            }
        }

        window.setPreferredSize(frameSize);
        window.pack();
    }

    public static void putWin(Window window, JButton btn, JComponent... comp) {

        btn.setPressedIcon(new javax.swing.ImageIcon(window.getClass().getResource("/resource/img24/c036.gif")));
        Preferences pref = Preferences.userRoot().node(window.getClass().getSimpleName());

        pref.putInt("_height", window.getHeight());
        pref.putInt("_width", window.getWidth());
    }

    public static String getPort(String num) {
        return (num.equals("1")) ? ePrefs.port1.getProp() : (num.equals("2")) ? ePrefs.port2.getProp() : ePrefs.port3.getProp();
    }

    public static void putPort(String num, String name) {
        ePrefs p = (num.equals("1")) ? ePrefs.port1 : (num.equals("2")) ? ePrefs.port2 : ePrefs.port3;
        p.putProp(name);
    }

    public static String getServer(String num) {
        return (num.equals("1")) ? ePrefs.server1.getProp() : (num.equals("2")) ? ePrefs.server2.getProp() : ePrefs.server3.getProp();
    }

    public static void putServer(String num, String name) {
        ePrefs p = (num.equals("1")) ? ePrefs.server1 : (num.equals("2")) ? ePrefs.server2 : ePrefs.server3;
        p.putProp(name);
    }

    public static String getBase(String num) {
        return (num.equals("1")) ? ePrefs.base1.getProp() : (num.equals("2")) ? ePrefs.base2.getProp() : ePrefs.base3.getProp();
    }

    public static void putBase(String num, String name) {
        ePrefs p = (num.equals("1")) ? ePrefs.base1 : (num.equals("2")) ? ePrefs.base2 : ePrefs.base3;
        p.putProp(name);
    }

    public static void addButtonMouseListener(JButton btn, ActionListener listener) {

        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mousePressed(java.awt.event.MouseEvent evt) {
                for (ActionListener al : timer.getActionListeners()) {
                    timer.removeActionListener(al);
                }
                timer.addActionListener(listener);
                timer.start();
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                timer.stop();
                btn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif")));
            }
        });
    }
}
