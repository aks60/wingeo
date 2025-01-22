package common;

import java.util.Locale;
import java.util.Properties;
import java.util.prefs.Preferences;

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
    sysprodID("-1"), //выбранный системный шаблон продукта
    prjprodID("-1"), //выбранный продукт для клиента
    orderID("-1"), //выбранный заказ клиента
    base_num("1"),
    old("0"), //переключение на пред. версию
    base1("C:\\Okna\\fbase\\BASE.FDB?encoding=win1251", "C:\\Okna\\fbase\\BASE.FDB?encoding=win1251"),
    base2("C:\\Okna\\fbase\\BASE.FDB?encoding=win1251", "C:\\Okna\\fbase\\BASE2.FDB?encoding=win1251"),
    base3("C:\\Okna\\fbase\\BASE.FDB?encoding=win1251", "C:\\Okna\\fbase\\BASE3.FDB?encoding=win1251"),
    path_app(System.getProperty("user.home") + "/Avers/Okna", "C:\\Users\\aksenov\\Desktop\\winapp.jar"),
    path_prop(System.getProperty("user.home") + "/Avers/Okna", "C:\\ProgramData\\Avers\\Okna"), //Аркаим или Arkaim
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
    public final static String filename = "okna.property"; //имя файла properties

    //Значения по умолчанию
    public String value;

    public static String password = "*";
    public static String role = null;
    public static Locale locale = new Locale("ru", "RU");
    public static String fb = "fb";
    public static boolean dev = false;      //признак разработки и тестирования
    public static boolean locate = true;    //координаты фрейма разработки и тестирования
    public static String profile = "";      //профиль разработки и тестирования

    //Значение по умолчанию
    ePref(String value) {
        this.value = value;
    }

    //Значение по умолчанию для конкретной OS
    ePref(String value1, String value2) {
        String os = System.getProperty("os.name");
        this.value = os.equals("Linux") ? value1 : value2;
    }
    
}
