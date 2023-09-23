package startup;

import builder.model2.UGeo;
import builder.script.GsonScript;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.eProp;
import dataset.Conn;
import domain.eElement;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class Test {

    public static Integer numDb = Integer.valueOf(eProp.base_num.read());

    // <editor-fold defaultstate="collapsed" desc="Connection[] connect(int numDb)">
    public static Connection connect1() {
        try {
            String db = (numDb == 1) ? eProp.base1.read() : (numDb == 2) ? eProp.base2.read() : eProp.base3.read();
            if (db.toUpperCase().contains("BIMAX")) {
                return java.sql.DriverManager.getConnection("jdbc:firebirdsql:localhost/3050:D:\\Okna\\Database\\ps4\\ITEST.FDB?encoding=win1251", "sysdba", "masterkey");
            } else if (db.toUpperCase().contains("SIAL3")) {
                return java.sql.DriverManager.getConnection("jdbc:firebirdsql:localhost/3055:D:\\Okna\\Database\\ps3\\sial3b.fdb?encoding=win1251", "sysdba", "masterkey");
            } else if (db.toUpperCase().contains("ALUTEX3")) {
                return java.sql.DriverManager.getConnection("jdbc:firebirdsql:localhost/3055:D:\\Okna\\Database\\ps3\\alutex3.fdb?encoding=win1251", "sysdba", "masterkey");
            } else if (db.toUpperCase().contains("ALUTECH3")) {
                return java.sql.DriverManager.getConnection("jdbc:firebirdsql:localhost/3055:D:\\Okna\\Database\\ps3\\alutech3.fdb?encoding=win1251", "sysdba", "masterkey");
            } else if (db.toUpperCase().contains("KRAUSS")) {
                return java.sql.DriverManager.getConnection("jdbc:firebirdsql:localhost/3050:D:\\Okna\\Database\\ps4\\krauss.fdb?encoding=win1251", "sysdba", "masterkey");
            } else if (db.toUpperCase().contains("VIDNAL")) {
                return java.sql.DriverManager.getConnection("jdbc:firebirdsql:localhost/3050:D:\\Okna\\Database\\ps4\\vidnal.fdb?encoding=win1251", "sysdba", "masterkey");
            } else if (db.toUpperCase().contains("SOKOL")) {
                return java.sql.DriverManager.getConnection("jdbc:firebirdsql:localhost/3050:D:\\Okna\\Database\\ps4\\sokol.fdb?encoding=win1251", "sysdba", "masterkey");
            }
            return null;
        } catch (Exception e) {
            System.err.println("Ошибка:Test.connect() " + e);
            return null;
        }
    }

    public static Connection connect2() {
        try {
            eProp.user.write("sysdba");
            eProp.password = String.valueOf("masterkey");
            Conn.connection(eProp.server(numDb.toString()), eProp.port(numDb.toString()), eProp.base(numDb.toString()), eProp.user.read(), eProp.password.toCharArray(), null);
            return Conn.connection();
        } catch (Exception e) {
            System.err.println("Ошибка:Test.connect() " + e);
            return null;
        }
    }

    // </editor-fold>     
    //java -jar C:\\Okna\\winapp\\dist\\winapp.jar dev loc
    public static void main(String[] args) throws Exception {

        eProp.dev = true;
        try {
            //frames.PSConvert.exec();
            //wincalc();
            //query();
            //frame();
            //json();
            //uid();
            //script();
            //intersect();

        } catch (Exception e) {
            System.err.println("TEST-MAIN: " + e);
        }
    }

    private static void wincalc() throws Exception {

        Conn.connection(Test.connect2());
        builder.Wincalc winc = new builder.Wincalc();
        String _case = "min";

        if (_case.equals("one")) {
            winc.build(GsonScript.productJson(508945));
            winc.constructiv(true);
//            winc.bufferImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
//            winc.gc2d = winc.bufferImg.createGraphics();
//            winc.rootArea.draw(); //рисую конструкцию
            //frames.PSCompare.iwinXls(winc, true);
            frames.PSCompare.iwinPs4(winc, true);
            //winc.listJoin.forEach(it -> System.out.println(it));           

        } else if (_case.equals("min")) {
            List<Integer> prjList = GsonScript.productList(_case);
            for (int prj : prjList) {
                String script = GsonScript.productJson(prj);
                if (script != null) {
                    winc.build(script);
                    winc.constructiv(true);
                    //frames.PSCompare.iwinXls(winc, false);
                    frames.PSCompare.iwinPs4(winc, false);
                }
            }

        } else if (_case.equals("max")) {
            List<Integer> prjList = GsonScript.productList(_case);
            for (int prj : prjList) {
                String script = GsonScript.productJson(prj);
                if (script != null) {
                    winc.build(script);
                    winc.constructiv(true);
                    //frames.PSCompare.iwinXls(winc, false);
                    frames.PSCompare.iwinPs4(winc, false);
                }
            }
        }
    }

    private static void frame() throws Exception {
        Main.main(new String[]{"tex"});
//        while (App.Top.frame == null) {
//            Thread.yield();
//        }
        Thread.sleep(1300);
        App.Order.createFrame(App.Top.frame);
    }

    private static void query() {
        try {
            Conn.connection(Test.connect2());
            Object obj = eElement.find3(1386, 33);
            //System.out.println(obj);

        } catch (Exception e) {
            System.out.println("main.Test.query()");
        }
        //Пересчёт
//        try {
//            Statement statement = statement = Conn.connection().createStatement();
//            Query q = new Query(eColor.values()).select(eColor.up, "order by id");
//            int id = 0;
//            for (Record rec : q) {
//                String ID = rec.getStr(eColor.id);
//                statement.executeUpdate("update color set id = " + String.valueOf(++id) + " where id = " + ID);
//            }
//        } catch (SQLException e) {
//            System.out.println("Query.update() " + e);
//        }        
    }

    private static void json() {
        Gson gson = new Gson();
        JsonParser parse = new JsonParser();

        String str1 = "{\"developers\": [{ \"firstName\":777 , \"lastName\":\"Torvalds\" }, "
                + "{ \"firstName\":\"John\" , \"lastName\":\"von Neumann\"}]}";

        String str2 = "\"developers\": [ \"firstName\":\"Linus\" , \"lastName\":\"Torvalds\" }, "
                + "{ \"firstName\":\"John\" , \"lastName\":\"von Neumann\" } ]}";

        String str3 = null; //"{typeOpen:1, \"sysfurnID\":1634}";

        JsonObject obj = gson.fromJson(str3, JsonObject.class);
        Object out = obj.get("sysfurnID");
        System.out.println(obj);

        //boolean firstStringValid = JSONUtils.isJSONValid(str1); //true
        //boolean secondStringValid = JSONUtils.isJSONValid(str2); //false
        //Object obj = gson.fromJson(str1, Object.class);
        //System.out.println(new GsonBuilder().create().toJson(parse.parse(str1)));
//        JSONObject output = new JSONObj;
//        Object obj = gson.toJson(parse.parse(str3));
//        System.out.println(obj);    
        //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(str1)));
        //
        //String p = gson.fromJson(str3, String.class);
        //System.out.println(gson.fromJson(str3, String.class)); 
//        Conn.instanc().connection(Test.connect2());
//        builder.Wincalc winc = new builder.Wincalc();
//        String script = Winscript.test(601004, false);
//        winc.build(script);
//
//        GsonBuilder builder = new GsonBuilder();
//        //builder.registerTypeAdapter(Element.class, new GsonDeserializer<Element>());
//        //builder.setPrettyPrinting();
//        GsonRoot root = builder.create().fromJson(script, GsonRoot.class);
    }

    private static void uid() {

        UUID idOne = UUID.randomUUID();
        String str = "" + idOne;
        int uid = str.hashCode();
        String filterStr = "" + uid;
        str = filterStr.replaceAll("-", "");

        System.out.println(Integer.parseInt(str));
    }

    private static void script() throws Exception {

//        ScriptEngineManager factory = new ScriptEngineManager();
//        ScriptEngine engine = factory.getEngineByName("nashorn"); //factory.getEngineByName("JavaScript");
//        Bindings scope = engine.createBindings();
//        File f = new File("test.js");
//        engine.put("file", f);
//        engine.eval("print(file.getAbsolutePath())");
        //https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/Math/ceil
        //Переменные сценария
//        double B = 3;
//        double L = 1200;
//        double H = 56;
//        engine.put("B", B);
//        engine.put("L", L);
//        engine.put("H", H);
//        engine.eval("var obj = new Object(); obj.length = L; obj.height = H;");
//        engine.eval("print(obj.length + obj.height);");
//        engine.eval("print(Math.ceil(L + 33.3));");
        //Вызов Функций Сценария и Методов
//        String script = "function hello(name) { print('Hello, ' + name); }";
//        engine.eval(script);
//        Invocable inv = (Invocable) engine;
//        inv.invokeFunction("hello", "Аксёнов!!" );
        //Основанным на объектах сценария
//        String script = "var obj = new Object(); obj.hello = function(name) { print('Hello, ' + name); }";
//        engine.eval(script);
//        Invocable inv = (Invocable) engine;
//        Object obj = engine.get("obj");
//        inv.invokeMethod(obj, "hello", "Аксёнов!!" );
        //Реализация Интерфейсов Java Сценариями
//        String script = "function run() { print('run called Аксёнов'); }";
//        engine.eval(script);
//        Invocable inv = (Invocable) engine;
//        Runnable r = inv.getInterface(Runnable.class);
//        Thread th = new Thread(r);
//        th.start();
//
        //На объектах или объектно-ориентирован
//        String script = "var obj = new Object(); obj.run = function() { print('run method called Аксёнов'); }";
//        engine.eval(script);
//        Object obj = engine.get("obj");
//        Invocable inv = (Invocable) engine;
//        Runnable r = inv.getInterface(obj, Runnable.class);
//        Thread th = new Thread(r);
//        th.start(); 
//        
//        double Q = 3;
//        double L = 1200;
//        double H = 56;
//        engine.put("Q", Q);
//        engine.put("L", L);
//        engine.put("H", H);
//    
//        script ="Math.ceil((Q * 2 * L) + 3.3)";
//        Object result = engine.eval(script);
//        System.out.println(result);
    }

    public static void random() {
        int a = 0; // Начальное значение диапазона - "от"
        int b = 10; // Конечное значение диапазона - "до"

        int random_number1 = a + (int) (Math.random() * b); // Генерация 1-го числа
        System.out.println("1-ое случайное число: " + random_number1);

        int random_number2 = a + (int) (Math.random() * b); // Генерация 2-го числа
        System.out.println("2-ое случайное число: " + random_number2);

        int random_number3 = a + (int) (Math.random() * b); // Генерация 3-го числа
        System.out.println("3-е случайное число: " + random_number3);
    }

    //Пример PathIterator
    public static void PathIterator() {
        Area area1 = UGeo.area(0, 0, 0, 900, 600, 899, 0, 0);
        Area area2 = new Area(new Rectangle(0, 0, 200, 900));
        UGeo.PRINT(area2);

        UGeo.PRINT(area1);
        area1.intersect(area2);
        UGeo.PRINT(area1);
    }

    public static void intersect() {
        double a[] = {200.03367570298246, 898.989728910591, 200.0336757029803, 898.989728910591};
        double b[] = {200.16848754883262, 894.9452514648436, 594.0, 885.0};
        if (Math.round(a[0]) != Math.round(a[2]) || Math.round(a[1]) != Math.round(a[3])) {
            System.out.println("a");
        }
        if (Math.round(b[0]) != Math.round(b[2]) || Math.round(b[1]) != Math.round(b[3])) {
            System.out.println("a");
        }
    }
}
