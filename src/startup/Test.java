package startup;

import builder.model.Com5t;
import builder.model.UGeo;
import builder.param.check.ElementTest;
import builder.param.check.FillingTest;
import builder.param.check.FurnitureTest;
import builder.param.check.JoiningTest;
import builder.param.check.WincalcTest;
import builder.script.GsonElem;
import builder.script.GsonScript;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.ArrayCom;
import common.GeoBuffer;
import common.eProp;
import dataset.Conn;
import enums.Type;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.util.GeometricShapeFactory;

public class Test {

    //public ArrayList<ListenerMouse> mouseDragged = new ArrayList<ListenerMouse>();
    private JFrame frame = null;
    public Geometry mlin = null;
    public Geometry mpol = null;

    public static Integer numDb = Integer.valueOf(eProp.base_num.read());
    private static GeometryFactory gf = new GeometryFactory();
    AffineTransformation aff = new AffineTransformation();


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
            frame(args);
            //wincalc();
            //param();
            //query();
            //json();
            //uid();
            //script();
            //geom();

        } catch (Exception e) {
            System.err.println("TEST-MAIN: " + e);
        }
    }

    //Конструктор
    public Test() {

        frame = new JFrame();
        //frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paincomp(g);
            }

            @Override
            public java.awt.Dimension getPreferredSize() {
                return new java.awt.Dimension(800, 600);
            }
        };
        frame.add(p);
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
            }

            public void mouseReleased(MouseEvent event) {
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent event) {
            }
        });
        frame.pack();
        frame.setVisible(true);

        //draw6();
    }
        
    public static void frame(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Test();
            }
        });
    }
    
    public void paincomp(Graphics g) {

        Graphics2D gc2d = (Graphics2D) g;
        gc2d.translate(20, -840);
        gc2d.scale(4, 4);
        //gc2d.translate(10, -10);
        //gc2d.scale(.4, .4);

        if (mlin != null) {
            gc2d.setColor(Color.BLUE);
            Shape shape = new ShapeWriter().toShape(mlin);
            gc2d.draw(shape);
        }
        if (mpol != null) {
            gc2d.setColor(Color.RED);
            Shape shape = new ShapeWriter().toShape(mpol);
            gc2d.draw(shape);
        }
    }
    
    private static void wincalc() throws Exception {

        Conn.connection(Test.connect2());
        builder.Wincalc winc = new builder.Wincalc();
        String _case = "min";

        if (_case.equals("one")) {
            String script = GsonScript.scriptPath(604005);
            winc.build(script);
            //System.out.println(new GsonBuilder().create().toJson(new com.google.gson.JsonParser().parse(script)));
            //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

            winc.specification(true);
            //new Joining(winc).calc();
            //winc.bufferImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            //winc.gc2d = winc.bufferImg.createGraphics();
            //winc.draw(); //рисую конструкцию

            frames.PSCompare.iwinPs4(winc, true);
            //winc.listElem.forEach(it -> System.out.println(it));
            //winc.listJoin.forEach(it -> System.out.println(it.joiningRec));     
            //winc.listJoin.forEach(it -> System.out.println(it));     

        } else if (_case.equals("min")) {
            List<Integer> prjList = GsonScript.systemList(_case);
            for (int prj : prjList) {
                String script = GsonScript.scriptPath(prj);
                if (script != null) {
                    winc.build(script);
                    winc.specification(true);
                    frames.PSCompare.iwinPs4(winc, false);
                }
            }

        } else if (_case.equals("max")) {
            List<Integer> prjList = GsonScript.systemList(_case);
            for (int prj : prjList) {
                String script = GsonScript.scriptPath(prj);
                if (script != null) {
                    winc.build(script);
                    winc.specification(true);
                    frames.PSCompare.iwinPs4(winc, false);
                }
            }
        }
    }

    private static void param() {

        Conn.connection(Test.connect2());
        WincalcTest.init();

        ElementTest t1 = new ElementTest();
        t1.elementVar();
        t1.elementDet();

        JoiningTest t2 = new JoiningTest();
        t2.joiningVar();
        t2.joiningDet();

        FillingTest t3 = new FillingTest();
        t3.fillingVar();
        t3.fillingDet();

        FurnitureTest t4 = new FurnitureTest();
        t4.furnitureVar();
        t4.furnitureDet();

//        Set set = new HashSet();
//        Map<String, Set> map = new HashMap<String, Set>();
//        for (Enam en : ParamList.values()) {
//            Set set2 = map.getOrDefault(en.text(), new HashSet());
//            set2.add(en.numb());
//            map.put(en.text(), set2);
//        }
//        for (Map.Entry<String, Set> entry : map.entrySet()) {
//            String key = entry.getKey();
//            Set value = entry.getValue();
//            System.out.println(key + " " + value);
//        }
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
        double X1 = 862.743578 - 862.743567;
        double X2 = Math.round(X1 * 1000);
        double X3 = X2 / 1000;
//        int a = 0; // Начальное значение диапазона - "от"
//        int b = 10; // Конечное значение диапазона - "до"
//
//        int random_number1 = a + (int) (Math.random() * b); // Генерация 1-го числа
//        System.out.println("1-ое случайное число: " + random_number1);
//
//        int random_number2 = a + (int) (Math.random() * b); // Генерация 2-го числа
//        System.out.println("2-ое случайное число: " + random_number2);
//
//        int random_number3 = a + (int) (Math.random() * b); // Генерация 3-го числа
//        System.out.println("3-е случайное число: " + random_number3);
    }

    public static void geom() {
        //Toolkit.getDefaultToolkit().beep();//ЗВУК!!!!
        GeometryFactory gf = new GeometryFactory(); //JTSFactoryFinder.getGeometryFactory(); 

        Coordinate[] coord1 = new Coordinate[]{
            new Coordinate(0, 0), new Coordinate(0, 100),
            new Coordinate(100, 100), new Coordinate(100, 0),
            new Coordinate(0, 0)};
        Coordinate[] coord2 = {
            new Coordinate(100, 0, 1),
            new Coordinate(100, 200, 2),
            new Coordinate(300, 200, 3),
            new Coordinate(300, 0, 4),
            new Coordinate(100, 0, 1)};

        Point point1 = gf.createPoint(new Coordinate(500, 500));
        Point point2 = gf.createPoint(new Coordinate(0, 500));
        LineString line1 = gf.createLineString(new Coordinate[]{new Coordinate(200, 0), new Coordinate(200, 500)});
        LineString line2 = gf.createLineString(coord2);
        LineSegment segm1 = new LineSegment(100, 600, 100, 0);
        LineSegment segm2 = new LineSegment(0, 10, 12, 10);
        Polygon poly1 = gf.createPolygon(coord1);
        Polygon poly2 = gf.createPolygon(coord2);

        Geometry p = poly1.union(poly2);

        Geometry geo1 = UGeo.newPolygon(0, 300, 0, 380, 50, 380, 50, 300, 0, 300);
        Geometry geo2 = UGeo.newPolygon(0, 300, 0, 380, 150, 380, 150, 300, 0, 300);

        Polygon p1 = gf.createPolygon(new Coordinate[]{
            new Coordinate(0, 300, 8), new Coordinate(0, 380, 8),
            new Coordinate(50, 380, 8), new Coordinate(50, 300, 8), new Coordinate(0, 300, 8)});
        Polygon p2 = gf.createPolygon(new Coordinate[]{
            new Coordinate(0, 300, 9), new Coordinate(0, 380, 9),
            new Coordinate(150, 380, 9), new Coordinate(150, 300, 9), new Coordinate(0, 300, 9)});

//        Geometry geo1 = UGeo.newPolygon(0, 300, 0, 1370, 68, 1370, 68, 300, 0, 300);
//        Geometry geo2 = UGeo.newPolygon(0, 1370, 1300, 1370, 1300, 1302, 0, 1302, 0, 1370);
        //LineSegment seg = segm1.offset(-68);
        // LineSegment se2 = segm1.offset(-68);
        Geometry p3 = p1.union(p2);
        Coordinate coo[] = p3.getCoordinates();
        new Test().mpol = p3;

        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        try {
            LineString line = (LineString) reader.read("LINESTRING(0 2, 2 0, 8 6)");
        } catch (Exception e) {
            System.err.println("Ошибка:Test.geom " + e);
        };
    }

// <editor-fold defaultstate="collapsed" desc="TEMP"> 

    private void draw7() {
        double M = 1500;
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayCom<Com5t> frames = new ArrayCom();
        frames.add(new Com5t(1, new GsonElem(Type.FRAME_SIDE, 0.0, 300.0)));
        frames.add(new Com5t(2, new GsonElem(Type.FRAME_SIDE, 0.0, M)));
        frames.add(new Com5t(3, new GsonElem(Type.FRAME_SIDE, 1300.0, M)));
        frames.add(new Com5t(4, new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0)));

        LineSegment s1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
        LineString arc1 = UGeo.newLineArch(s1.p1.x, s1.p0.x, s1.p0.y, 300, 4);
        Coordinate arr[] = arc1.getCoordinates();
        List.of(arr).forEach(c -> c.z = 4);

        list.add(new Coordinate(frames.get(0).x1(), frames.get(0).y1(), frames.get(0).id));
        list.add(new Coordinate(frames.get(1).x1(), frames.get(1).y1(), frames.get(1).id));
        list.add(new Coordinate(frames.get(2).x1(), frames.get(2).y1(), frames.get(2).id));
        list.addAll(List.of(arr));

        Map<Double, Double[]> hm = new HashMap();
        hm.put(1.0, new Double[]{68.0, .0, .0});
        hm.put(2.0, new Double[]{68.0, .0, .0});
        hm.put(3.0, new Double[]{68.0, .0, .0});
        hm.put(4.0, new Double[]{68.0, .0, .0});

        Polygon geo1 = UGeo.newPolygon(list);
        //Polygon geo2 = UGeo.geoBuffer(geo1, hm, 0);

        this.mpol = geo1;
        //this.mlin = geo1.intersection(geo2);
    }

    private void draw6() {
        double M = 420;
        //double M = 1500;
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayCom<Com5t> frames = new ArrayCom();

        frames.add(new Com5t(1, new GsonElem(Type.FRAME_SIDE, 0.0, 300.0)));
        frames.add(new Com5t(2, new GsonElem(Type.FRAME_SIDE, 0.0, M)));
        frames.add(new Com5t(3, new GsonElem(Type.FRAME_SIDE, 1300.0, M)));
        //frames.add(new Com5t(4, new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0)));
        frames.add(new Com5t(4, new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0)));

        LineSegment s1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
        LineString arc1 = UGeo.newLineArch(s1.p1.x, s1.p0.x, s1.p0.y, 300, 4);
        Coordinate arr[] = arc1.getCoordinates();

        list.add(new Coordinate(frames.get(0).x1(), frames.get(0).y1(), frames.get(0).id));
        list.add(new Coordinate(frames.get(1).x1(), frames.get(1).y1(), frames.get(1).id));
        list.add(new Coordinate(frames.get(2).x1(), frames.get(2).y1(), frames.get(2).id));
        //list.add(new Coordinate(frames.get(3).x1(), frames.get(3).y1(), frames.get(3).id));
        list.addAll(List.of(arr));
        list.add(new Coordinate(frames.get(0).x1(), frames.get(0).y1(), frames.get(0).id));

        LineString geo1 = Com5t.gf.createLineString(list.toArray(new Coordinate[0]));
        Polygon geo2 = GeoBuffer.buffer(geo1, frames, 0);
        Polygon geo3 = UGeo.bufferCross(geo1, frames, 0);
        Polygon geo4 = UGeo.bufferUnion(geo1, frames, 0);

        Coordinate coo1[] = geo1.getCoordinates();
        Coordinate coo2[] = geo2.getCoordinates();
        Coordinate coo3[] = geo3.getCoordinates();
        Coordinate coo4[] = geo4.getCoordinates();

        mlin = gf.createMultiLineString(new LineString[]{geo1});
        mpol = geo4;

    }

    private void draw5() {
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayCom<Com5t> frames = new ArrayCom();
        frames.add(new Com5t(1, new GsonElem(Type.FRAME_SIDE, 0.0, 300.0)));
        frames.add(new Com5t(2, new GsonElem(Type.FRAME_SIDE, 0.0, 350.0)));
        frames.add(new Com5t(3, new GsonElem(Type.FRAME_SIDE, 1300.0, 350.0)));
        frames.add(new Com5t(4, new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0)));

        //Траесформация линии в горизонт
        LineSegment s1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
        //s1.normalize();
        //double H = 300.0, ANG = Math.toDegrees(s1.angle());
        //aff.setToRotation(Math.toRadians(-ANG), s1.p0.x, s1.p0.y); //угол ротации 
        //LineString l1 = (LineString) aff.transform(s1.toGeometry(gf));
        //LineString arc1 = UGeo.newLineArch(l1.getCoordinateN(0).x, l1.getCoordinateN(1).x, l1.getCoordinateN(0).y, H, 4);  //созд. арки на гортзонтали 
        LineString arc1 = UGeo.newLineArch(s1.p1.x, s1.p0.x, s1.p0.y, 300, 4);
        Coordinate arr1[] = arc1.getCoordinates();
        List.of(arr1).forEach(c -> c.z = 4);

        //Обратная трансформация арки
//        aff.setToRotation(Math.toRadians(ANG), s1.p0.x, s1.p0.y); //угол ротации  
//        Geometry arc2 = aff.transform(arc1);
//        Coordinate arr2[] = arc2.getCoordinates(); //Arrays.copyOf(arc2.getCoordinates(), arc2.getCoordinates().length);
//        List.of(arr2).forEach(c -> c.z = 4);
        list.add(new Coordinate(frames.get(0).x1(), frames.get(0).y1(), frames.get(0).id));
        list.add(new Coordinate(frames.get(1).x1(), frames.get(1).y1(), frames.get(1).id));
        list.add(new Coordinate(frames.get(2).x1(), frames.get(2).y1(), frames.get(2).id));
        list.addAll(List.of(arr1));

        this.mpol = UGeo.newPolygon(list);
        this.mlin = UGeo.bufferPaddin(this.mpol, frames, 20);

    }

    private void draw4() {

        ArrayList<Coordinate> list = new ArrayList<Coordinate>();

        list.add(new Coordinate(0, 600, 1));
        list.add(new Coordinate(0, 1500, 2));
        list.add(new Coordinate(1300, 1500, 3));
        list.add(new Coordinate(1300, 300, 4));
        list.add(new Coordinate(0, 600, 1));

        Map<Double, Double> hm = new HashMap();
        hm.put(1.0, 32.0);
        hm.put(2.0, 68.0);
        hm.put(3.0, 32.0);
        hm.put(4.0, 68.0);

//        Geometry geo1 = UGeo.newLineStr(0, 300, 0, 1370, 68, 1370, 68, 300, 0, 300);
//        Geometry geo2 = UGeo.newLineStr(0, 1370, 1300, 1370, 1300, 1302, 0, 1302, 0, 1370);
        Geometry geo1 = Com5t.gf.createLineString(list.toArray(new Coordinate[0]));
        Geometry geo2 = GeoBuffer.buffer(geo1, hm);

        mpol = geo1.union(geo2);
        // mlin = geo2;
    }

    private void draw3() {

        double M = 1500;
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(10));
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayCom<Com5t> frames = new ArrayCom();
        frames.add(new Com5t(1, new GsonElem(Type.FRAME_SIDE, 0.0, 300.0)));
        frames.add(new Com5t(2, new GsonElem(Type.FRAME_SIDE, 0.0, M)));
        frames.add(new Com5t(3, new GsonElem(Type.FRAME_SIDE, 1300.0, M)));
        frames.add(new Com5t(4, new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0)));
        LineSegment s1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
        s1.normalize();
        double H = 200.0, DH = s1.p1.y - s1.p0.y, ANG = Math.toDegrees(s1.angle());

        //Траесформация линии в горизонт
        aff.setToRotation(Math.toRadians(-ANG), s1.p0.x, s1.p0.y); //угол ротации      
        LineString l1 = (LineString) aff.transform(s1.toGeometry(gf));
        LineString arc1 = UGeo.newLineArch(l1.getCoordinateN(0).x, l1.getCoordinateN(1).x, l1.getCoordinateN(0).y, H, 4);  //созд. арки на гортзонтали  

        //Обратная трансформация арки
        aff.setToRotation(Math.toRadians(ANG), s1.p0.x, s1.p0.y); //угол ротации  
        Geometry arc2 = aff.transform(arc1);
        Coordinate arr2[] = arc2.getCoordinates(); //Arrays.copyOf(arc2.getCoordinates(), arc2.getCoordinates().length);
        List.of(arr2).forEach(c -> c.z = 4);

        list.add(new Coordinate(frames.get(0).x1(), frames.get(0).y1(), frames.get(0).id));
        list.add(new Coordinate(frames.get(1).x1(), frames.get(1).y1(), frames.get(1).id));
        list.add(new Coordinate(frames.get(2).x1(), frames.get(2).y1(), frames.get(2).id));
        list.addAll(List.of(arr2));

        Polygon geo1 = UGeo.newPolygon(list);
        Polygon geo2 = UGeo.bufferPaddin(geo1, frames, 0);
        this.mlin = gf.createMultiPolygon(new Polygon[]{geo1, geo2});

        this.mpol = null;
    }

    private void draw2() {

        GeometricShapeFactory gsf = new GeometricShapeFactory();
        Double dH = 64.0;
        Double H = 300.0;
        Double L = 1300.0;
        double R = (Math.pow(L / 2, 2) + Math.pow(H, 2)) / (2 * H);  //R = (L2 + H2) / 2H - радиус арки

        //Угол реза
        double rad1 = Math.acos((L / 2) / R);
        double rad2 = Math.acos((L - 2 * dH) / ((R - dH) * 2));
        double a1 = R * Math.sin(rad1);
        double a2 = (R - dH) * Math.sin(rad2);
        double ang3 = 90 - Math.toDegrees(Math.atan((a1 - a2) / dH)); //угол реза рамы
        double ang4 = 90 - (Math.toDegrees(rad1) - (90 - ang3)); //угол реза арки

        Polygon p1 = UGeo.newPolygon(0, 0, 0, H, L, H, L, 0);
        Polygon p2 = UGeo.newPolygon(0, 0, 0, H + L, dH, H + L, dH, 0);
        Polygon p3 = UGeo.newPolygon(L - dH, 0, L - dH, H + L, L, H + L, L, 0);

        double ang1 = Math.PI / 2 - Math.asin(L / (R * 2));
        gsf.setNumPoints(1000);
        gsf.setSize(2 * R);
        gsf.setBase(new Coordinate(L / 2 - R, 0));
        LineString arc1 = gsf.createArc(Math.PI + ang1, Math.PI - 2 * ang1);
        for (int i = 0; i < arc1.getCoordinates().length; i++) {
            arc1.getCoordinateN(i).setZ(777);
        }

        double ang2 = Math.PI / 2 - Math.asin((L - 2 * dH) / ((R - dH) * 2));
        gsf.setSize(2 * R - 2 * dH);
        gsf.setBase(new Coordinate(L / 2 - R + dH, dH));
        LineString arc2 = gsf.createArc(Math.PI + ang2, Math.PI - 2 * ang2);

        mpol = gf.createMultiPolygon(new Polygon[]{p1, p2, p3});

        List<Coordinate> list1 = new ArrayList<Coordinate>(List.of(arc1.getCoordinates()));
        List<Coordinate> list2 = new ArrayList<Coordinate>(List.of(arc2.reverse().getCoordinates()));
        list2.add(list1.get(0));
        list1.addAll(list2);
        mlin = gf.createLineString(list1.toArray(new Coordinate[0]));

        // System.out.println(list1);
    }
// </editor-fold>        
}
