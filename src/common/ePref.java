package common;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.Timer;

public enum ePref {

    lookandfeel("Metal", "Windows"),
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
    orderID("-1"),   //выбранный заказ клиента
    base_num("1"),
    old_version("0"), //переключение на пред. версию
    base1("/opt/database/fbase/bimax.fdb?encoding=win1251"),
    base2("/opt/database/fbase/bimax.fdb?encoding=win1251"),
    base3("/opt/database/fbase/bimax.fdb?encoding=win1251"),
    path_pref(System.getProperty("user.home") + "/Avers/Okna", "C:\\ProgramData\\Avers\\Okna"), //Аркаим или Arkaim
    cmd_def("I", "I"),
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
    ePref(String value) {
        this.value = value;
    }

    //Значение по умолчанию для конкретной OS
    ePref(String value1, String value2) {
        String os = System.getProperty("os.name");
        this.value = os.equals("Linux") ? value1 : value2;
    }

    public String get() {
        Preferences pref = Preferences.userRoot().node(this.getClass().getSimpleName());
        return pref.get(this.name(), this.value);
    }
   
    public static void get(Window window, JButton btn, ActionListener listener, JComponent... comp) {

        addButtonMouseListener(btn, listener);

        Preferences pref = Preferences.userRoot().node(window.getClass().getSimpleName());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = window.getSize();

        frameSize.height = pref.getInt("_height", window.getHeight());
        frameSize.width  = pref.getInt("_width", window.getWidth());

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

        window.setPreferredSize(frameSize);
        window.pack();
    }
    
     public static String getPort(String num) {
        return (num.equals("1")) ? ePref.port1.get() : (num.equals("2")) ? ePref.port2.get() : ePref.port3.get();
    }   
 
    public static String getServer(String num) {
        return (num.equals("1")) ? ePref.server1.get() : (num.equals("2")) ? ePref.server2.get() : ePref.server3.get();
    }    

    public static String getBase(String num) {
        return (num.equals("1")) ? ePref.base1.get() : (num.equals("2")) ? ePref.base2.get() : ePref.base3.get();
    }     
     
    public void put(String str) {
        Preferences pref = Preferences.userRoot().node(this.getClass().getSimpleName());
        pref.put(this.name(), str.trim());
    }

    public static void put(Window window, JButton btn, JComponent... comp) {

        btn.setPressedIcon(new javax.swing.ImageIcon(window.getClass().getResource("/resource/img24/c036.gif")));
        Preferences pref = Preferences.userRoot().node(window.getClass().getSimpleName());

        pref.putInt("_height", window.getHeight());
        pref.putInt("_width", window.getWidth());
    }

    public static void putPort(String num, String name) {
        ePref p = (num.equals("1")) ? ePref.port1 : (num.equals("2")) ? ePref.port2 : ePref.port3;
        p.put(name);
    }

    public static void putServer(String num, String name) {
        ePref p = (num.equals("1")) ? ePref.server1 : (num.equals("2")) ? ePref.server2 : ePref.server3;
        p.put(name);
    }

    public static void putBase(String num, String name) {
        ePref p = (num.equals("1")) ? ePref.base1 : (num.equals("2")) ? ePref.base2 : ePref.base3;
        p.put(name);
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
