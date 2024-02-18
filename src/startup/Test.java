package startup;

import builder.model.Com5t;
import builder.model.ElemSimple;
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
import java.util.List;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.operation.buffer.VariableBuffer;
import org.locationtech.jts.util.GeometricShapeFactory;
import org.locationtech.jts.algorithm.PointLocation;

public class Test {

    //public ArrayList<ListenerMouse> mouseDragged = new ArrayList<ListenerMouse>();
    private JFrame frame = null;
    private Geometry mlin = null;
    private Geometry mpol = null;

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
            //frame(args);
            //wincalc();
            param();
            //query();
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
        String _case = "min";

        if (_case.equals("one")) {
            String script = GsonScript.scriptPath(601004);
            winc.build(script);
            //System.out.println(new GsonBuilder().create().toJson(new com.google.gson.JsonParser().parse(script)));
            //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

            winc.specific(true);
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
                    winc.specific(true);
                    frames.PSCompare.iwinPs4(winc, false);
                }
            }

        } else if (_case.equals("max")) {
            List<Integer> prjList = GsonScript.systemList(_case);
            for (int prj : prjList) {
                String script = GsonScript.scriptPath(prj);
                if (script != null) {
                    winc.build(script);
                    winc.specific(true);
                    frames.PSCompare.iwinPs4(winc, false);
                }
            }
        }
    }

    private static void param() {

        Conn.connection(Test.connect2());
        WincalcTest.init();
        //WincalcTest.iwin4.listJoin.elem(WincalcTest.stv_right4, 2);
        
//        List<Coordinate> z = new ArrayList<Coordinate>();
//        for (ElemSimple el : WincalcTest.iwin2.listElem) {
//            if (el.type == Type.GLASS) {
//                for (Coordinate c : el.area.getCoordinates()) {
//                    z.add(c);
//                }                
//            }
//        }
//        System.out.println(z);
        
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
            new Coordinate(0, 0), new Coordinate(0, 1400),
            new Coordinate(900, 1400), new Coordinate(900, 0),
            new Coordinate(0, 0)};
        Coordinate[] coord2 = {
            new Coordinate(0, 0, 1),
            new Coordinate(0, 1000, 2),
            new Coordinate(1000, 1000, 3),
            new Coordinate(1000, 0, 4),
            new Coordinate(0, 0, 1)};

        Point point1 = gf.createPoint(new Coordinate(500, 500));
        Point point2 = gf.createPoint(new Coordinate(0, 500));
        LineString line1 = gf.createLineString(new Coordinate[]{new Coordinate(0, 500), new Coordinate(500, 500)});
        LineString line2 = gf.createLineString(coord2);
        LineSegment segm1 = new LineSegment(55.7, 538.99, 55.7, 538.99);
        LineSegment segm2 = new LineSegment(68.0, 500.0, 68.0, 1500.0);
        Polygon polygon1 = gf.createPolygon(coord1);
        Polygon polygon2 = gf.createPolygon(coord2);

        Coordinate p = new Coordinate(200, 501);
        Coordinate c[] = new Coordinate[]{new Coordinate(0, 500), new Coordinate(500, 500)};
        boolean b = PointLocation.isOnLine(p, c);
        System.out.println(b);

    }

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

        frame = new JFrame();
        //frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D gc2d = (Graphics2D) g;
                //gc2d.rotate(Math.toRadians(-180), 0, 0);
                //gc2d.translate(-400, -600);
                gc2d.scale(.3, .3);

                if (mlin != null) {
                    gc2d.setColor(Color.BLUE);
                    Shape shape = new ShapeWriter().toShape(mlin);
                    gc2d.draw(shape);
//                    g2.fill(shape);
                }
                if (mpol != null) {
                    gc2d.setColor(Color.RED);
                    Shape shape = new ShapeWriter().toShape(mpol);
                    gc2d.draw(shape);
                }

                //gc2d.rotate(Math.PI);
//                Font font = gc2d.getFont();
//                int textwidth = (int) (font.getStringBounds(".", gc2d.getFontRenderContext()).getWidth());
//                int textheight = (int) (font.getStringBounds("1", gc2d.getFontRenderContext()).getHeight());
//                System.out.println("textwidth - " + textwidth);
//                System.out.println("textheight - " + textheight);
            }

            @Override
            public java.awt.Dimension getPreferredSize() {
                return new java.awt.Dimension(600, 600);
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

        draw3();
    }

// <editor-fold defaultstate="collapsed" desc="TEMP">  
    private void draw4() {

        GeometryFactory gf = new GeometryFactory();
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        AffineTransformation aff = new AffineTransformation();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>();

        LineSegment s1 = new LineSegment(1300, 300, 0, 300);
        s1.normalize();
        double H = 200.0, DH = s1.p1.y - s1.p0.y, ANG = Math.toDegrees(s1.angle());

        list.add(new Coordinate(0, 300, 1));
        list.add(new Coordinate(0, 1500, 2));
        list.add(new Coordinate(1300, 1500, 3));
        list.add(new Coordinate(1300, 300, 4));
        list.add(new Coordinate(0, 300, 1));

        ArrayList<Coordinate> list2 = new ArrayList<Coordinate>();
        list.forEach(s -> list2.addAll(List.of(s, s)));

        double distance[] = {40, 40, 80, 80, 40};
        double distanc2[] = {40, 40, 80, 80};

        LineString geo1 = gf.createLineString(list.toArray(new Coordinate[0]));
        mpol = geo1;

        Geometry gb = VariableBuffer.buffer(geo1, distance);
        Polygon geo2 = (Polygon) gb;

        mlin = gf.createPolygon(geo2.getInteriorRingN(0));
    }

    private void draw3() {

        GeometryFactory gf = new GeometryFactory(new PrecisionModel(10));
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        AffineTransformation aff = new AffineTransformation();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayCom<Com5t> frame = new ArrayCom();
        frame.add(new Com5t(1, new GsonElem(Type.FRAME_SIDE, 0, 300)));
        frame.add(new Com5t(2, new GsonElem(Type.FRAME_SIDE, 0, 1500)));
        frame.add(new Com5t(3, new GsonElem(Type.FRAME_SIDE, 1300, 1500)));
        frame.add(new Com5t(4, new GsonElem(Type.FRAME_SIDE, 1300, 300, 300)));
        LineSegment s1 = new LineSegment(frame.get(3).x1(), frame.get(3).y1(), frame.get(0).x1(), frame.get(0).y1());
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

        list.add(new Coordinate(frame.get(0).x1(), frame.get(0).y1(), frame.get(0).id));
        list.add(new Coordinate(frame.get(1).x1(), frame.get(1).y1(), frame.get(1).id));
        list.add(new Coordinate(frame.get(2).x1(), frame.get(2).y1(), frame.get(2).id));
        list.addAll(List.of(arr2));

        Polygon geo1 = UGeo.newPolygon(list);
        Polygon geo2 = UGeo.geoPadding(geo1, frame, 0);
        this.mlin = gf.createMultiPolygon(new Polygon[]{geo1, geo2});

//        List<Coordinate> list2 = new ArrayLoop();
//        Coordinate c1[] = geo1.getCoordinates();
//        Coordinate c2[] = geo2.getCoordinates();
//        List<Coordinate> c1a = List.of(c1).stream().filter(c -> c.z == frame.get(3).id).collect(toList());
//        List<Coordinate> c2a = List.of(c2).stream().filter(c -> c.z == frame.get(3).id).collect(toList());
//        list2.addAll(c1a);
//        list2.addAll(c2a);
//        mpol = gf.createLineString(list2.toArray(new Coordinate[0]));
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
