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
    url_src("http://localhost/winweb/"),
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
    fontsize("11");

    //Значения по умолчанию
    public String value;

    //Системные переменные
    public static String password = "*";
    public static String role = null;
    public final static Locale locale = Locale.of("ru", "RU");
    public final static String fb = "fb";
    public static boolean dev = false; //признак разработки и тестирования
    public final static boolean demo = true; //признак demo базы
    public final static String versionApp = "2.0";
    public static String profile = ""; //профиль разработки и тестирования
    public static Timer timer = new Timer(1000, null);

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
        if (demo == true) {
            if(this == server1 || this == server2 || this == server3) {
                return "sa-okna.ru";
            }
            if (this == base1) {
                return "/opt/database/fdemo/binet.fdb?encoding=win1251";
            }
            if (this == base2 || this == base3) {
                return "/opt/database/fdemo/bimax.fdb?encoding=win1251";
            }
        }
        Preferences pref = Preferences.userRoot().node(this.getClass().getName());
        return pref.get(this.name(), this.value);
    }

    public void putProp(String str) {
        Preferences pref = Preferences.userRoot().node(this.getClass().getName());
        pref.put(this.name(), str.trim());
    }

    public static void getWin(Window window, JButton btn, ActionListener listener, JComponent... comp) {

        addButtonMouseListener(btn, listener);

        Preferences pref = Preferences.userNodeForPackage(window.getClass()).node(window.getClass().getSimpleName());

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

        if (window.getClass().getSimpleName().equals("Setting")) {
            window.setLocation(20, 100);
        } else {
            window.setLocation((screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height - 48) / 2 + 48);
        }
        if (comp != null) {
            for (int i = 0; i < comp.length; ++i) {

                if (comp[i] instanceof JTable) {
                    JTable tab = (JTable) comp[i];
                    pref = pref.node(tab.getName());
                    for (int k = 0; k < tab.getColumnCount(); ++k) {
                        tab.getColumnModel().getColumn(k).setPreferredWidth(
                                pref.getInt("colWidth" + k, tab.getColumnModel().getColumn(k).getPreferredWidth()));
                    }
                } else if (comp[i] instanceof JSplitPane) {
                    JSplitPane split = (JSplitPane) comp[i];
                    pref = pref.node(split.getName());
                    int v = pref.getInt("dividerLocation", split.getDividerLocation());
                    split.setDividerLocation(v);
                }
            }
        }

        window.setPreferredSize(frameSize);
        window.pack();
    }

    public static void putWin(Window window, JButton btn, JComponent... comp) {
        timer.stop();
        btn.setPressedIcon(new javax.swing.ImageIcon(window.getClass().getResource("/resource/img24/c036.gif")));
        Preferences pref = Preferences.userNodeForPackage(window.getClass()).node(window.getClass().getSimpleName());

        pref.putInt("_height", window.getHeight());
        pref.putInt("_width", window.getWidth());

        if (comp != null) {
            for (int i = 0; i < comp.length; ++i) {

                if (comp[i] instanceof JTable) {
                    JTable tab = (JTable) comp[i];
                    pref = pref.node(tab.getName());
                    for (int k = 0; k < tab.getColumnCount(); ++k) {
                        pref.putInt("colWidth" + k, tab.getColumnModel().getColumn(k).getPreferredWidth());
                    }

                } else if (comp[i] instanceof JSplitPane) {
                    JSplitPane split = (JSplitPane) comp[i];
                    pref = pref.node(split.getName());
                    pref.putInt("dividerLocation", split.getDividerLocation());
                }
            }
        }
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
