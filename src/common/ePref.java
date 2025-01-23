package common;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.AbstractButton;
import javax.swing.JComponent;

public enum ePref {
    
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
    
    //private static Preferences prop = null; //Preferences.userRoot().node("frames." + this.getName());
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
    private static AbstractButton btn;

    //�������� �� ���������
    ePref(String value) {
        this.value = value;
    }

    //�������� �� ��������� ��� ���������� OS
    ePref(String value1, String value2) {
        String os = System.getProperty("os.name");
        this.value = os.equals("Linux") ? value1 : value2;
    }
    
    public static void read(Preferences pref, Window window, JComponent... comp) {
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = window.getSize();

        int dy = pref.getInt("window.height", window.getHeight());
        int dx = pref.getInt("window.width", window.getWidth());

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

    public static void write(Preferences pref, Window frame, JComponent... comp) {
       pref.putInt("window.height", frame.getHeight()); 
       pref.putInt("window.width", frame.getWidth());
    }
    
    public static void actionPerformed(java.awt.event.ActionEvent evt) {
        System.out.println("common.ePref.actionPerformed()");
//        Dimension frameSize = frame.getSize();
        try {
//            btn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c036.gif")));
//            String dy = String.valueOf(frame.getSize().height);
//            String dx = String.valueOf(frame.getSize().width);
//            eProp.load().setProperty(frame.getClass().getName() + "_height", dy);
//            eProp.load().setProperty(frame.getClass().getName() + "_width", dx);
//            eProp.save();
        } finally {
//            stop();
        }
    }

    public static void initListener() {
        //super.addActionListener(this);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mousePressed(java.awt.event.MouseEvent evt) {
                //start();
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                //stop();
                btn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif")));
            }
        });
    }
}
