package startup;

import builder.model.UGeo;
import builder.script.GsonScript;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.eProp;
import dataset.Conn;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.util.GeometricShapeFactory;

public class Test {

    private JFrame frame;
    private Geometry geom;
    private Polygon poly;
    public static Integer numDb = Integer.valueOf(eProp.base_num.read());
    private static GeometryFactory gf = new GeometryFactory();

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
            frame(args);
            //json();
            //uid();
            //script();
            //geom();

        } catch (Exception e) {
            System.err.println("AKSENOV TEST-MAIN: " + e);
        }
    }

    private static void wincalc() throws Exception {

        Conn.connection(Test.connect2());
        builder.Wincalc winc = new builder.Wincalc();
        String _case = "one";

        if (_case.equals("one")) {
            String script = GsonScript.scriptPath(506642);
            winc.build(script);
            //System.out.println(new GsonBuilder().create().toJson(new com.google.gson.JsonParser().parse(script)));
            //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

            winc.constructiv(true);
            //new Joining(winc).calc();
            //winc.bufferImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            //winc.gc2d = winc.bufferImg.createGraphics();
            //winc.root.draw(); //рисую конструкцию

            frames.PSCompare.iwinPs4(winc, true);
            //winc.listElem.forEach(it -> System.out.println(it));
            //winc.listJoin.forEach(it -> System.out.println(it.joiningRec));     

        } else if (_case.equals("min")) {
            List<Integer> prjList = GsonScript.systemList(_case);
            for (int prj : prjList) {
                String script = GsonScript.scriptPath(prj);
                if (script != null) {
                    winc.build(script);
                    winc.constructiv(true);
                    frames.PSCompare.iwinPs4(winc, false);
                }
            }

        } else if (_case.equals("max")) {
            List<Integer> prjList = GsonScript.systemList(_case);
            for (int prj : prjList) {
                String script = GsonScript.scriptPath(prj);
                if (script != null) {
                    winc.build(script);
                    winc.constructiv(true);
                    frames.PSCompare.iwinPs4(winc, false);
                }
            }
        }
    }

    private static void query() {
        try {
            Conn.connection(Test.connect2());

        } catch (Exception e) {
            System.err.println("Ошибка:main.Test.query()");
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

    private void frame() {

        Coordinate[] coo1 = new Coordinate[]{
            new Coordinate(300, 300), new Coordinate(300, 600),
            new Coordinate(600, 600), new Coordinate(600, 300),
            new Coordinate(300, 300)};
        Polygon poly1 = gf.createPolygon(coo1);

        GeometricShapeFactory gsf = new GeometricShapeFactory();
        Double h = 80.0;
        Double H = 200.0;
        Double L = 800.0;
        
        Polygon poly = UGeo.newPolygon(0, 0, 0, H, L, H, L, 0);
        double R = (Math.pow(L / 2, 2) + Math.pow(H, 2)) / (2 * H);  //R = (L2 + H2) / 2H - радиус арки
        gsf.setSize(2*R);
        gsf.setBase(new Coordinate(L/2 - R, 0));
        Polygon arc1 = gsf.createCircle();
        gsf.setBase(new Coordinate(L/2 - R + h/2, h/2));
        gsf.setSize(2*R - h);
        Geometry arc2 = gsf.createCircle();
        geom = arc1.difference(arc2);
        geom = geom.intersection(poly);

        //System.out.println(geom.getNumPoints()); 
        //poly = UGeo.newPolygon(100, 100, 100, 200, 600, 200, 600, 100);
        //geom = geom.intersection(poly);
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

        //System.out.println(Integer.parseInt(str));
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

    public static void geom() {
        //Toolkit.getDefaultToolkit().beep();//ЗВУК!!!!
        GeometryFactory gf = new GeometryFactory(); //JTSFactoryFinder.getGeometryFactory(); 

        Coordinate[] coord1 = new Coordinate[]{
            new Coordinate(0, 0), new Coordinate(0, 1400),
            new Coordinate(900, 1400), new Coordinate(900, 0),
            new Coordinate(0, 0)};
        Coordinate[] coord2 = new Coordinate[]{
            new Coordinate(0, 0), new Coordinate(0, 1000),
            new Coordinate(1000, 1000), new Coordinate(1000, 0),
            new Coordinate(0, 0)};

        Point point1 = gf.createPoint(new Coordinate(499.9, 500));
        Point point2 = gf.createPoint(new Coordinate(0, 500));
        LineString line1 = gf.createLineString(new Coordinate[]{new Coordinate(0, 500), new Coordinate(500, 500)});
        LineString line2 = gf.createLineString(coord2);
        LineSegment segm1 = new LineSegment(100, 100, 0, 0);
        LineSegment segm2 = new LineSegment(20, 20, 200, 120);
        Polygon polygon1 = gf.createPolygon(coord1);
        Polygon polygon2 = gf.createPolygon(coord2);

        System.out.println(120 - 10 - 10 - 10);
    }

    // <editor-fold defaultstate="collapsed" desc="INIT">
    //
    public static void frame(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Test();
            }
        });
    }

    //Конструктор
    public Test() {
        initComponents();
        frame();
    }

    private void initComponents() {

        frame = new JFrame();
        //frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLUE);
                g2.scale(.5, .5);

                if (geom != null) {
                    Shape shape = new ShapeWriter().toShape(geom);
                    g2.draw(shape);
                }
                if (poly != null) {
                    Shape shape = new ShapeWriter().toShape(poly);
                    g2.draw(shape);
                }
            }

            @Override
            public java.awt.Dimension getPreferredSize() {
                return new java.awt.Dimension(600, 600);
            }
        };
        frame.add(p);
        frame.pack();
        frame.setVisible(true);

    }

    // </editor-fold>   
}
