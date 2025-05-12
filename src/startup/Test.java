package startup;

import builder.model.Com5t;
import builder.model.UGeo;
import builder.model.VBuffer;
import builder.param.check.ElementTest;
import builder.param.check.FillingTest;
import builder.param.check.FurnitureTest;
import builder.param.check.JoiningTest;
import builder.param.check.WincalcTest;
import builder.script.GsonElem;
import builder.script.GsonScript;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import common.eProp;
import dataset.Connect;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eColor;
import enums.Type;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.locationtech.jts.algorithm.PointLocation;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.util.GeometricShapeFactory;

///
public class Test {

    private JFrame frame = null;
    public Geometry mlin = null;
    public Geometry mpol = null;

    public static Integer numDb = Integer.valueOf(eProp.base_num.getProp());
    private static GeometryFactory gf = new GeometryFactory();
    AffineTransformation aff = new AffineTransformation();

    // <editor-fold defaultstate="collapsed" desc="Connection[] connect(int numDb)">
    public static Connection connect1() {
        try {
            String db = (numDb == 1) ? eProp.base1.getProp() : (numDb == 2) ? eProp.base2.getProp() : eProp.base3.getProp();
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
        eProp.user.putProp("sysdba");
        eProp.password = String.valueOf("masterkey");
        Connect.connection(eProp.getServer(numDb.toString()), eProp.getPort(numDb.toString()), eProp.getBase(numDb.toString()), eProp.user.getProp(), eProp.password.toCharArray(), null);
        return Connect.getConnection();
    }

    // </editor-fold>     
    public static void main(String[] args) throws Exception {

        eProp.devel = "99";
        try {
            //clearDB();
            //PSConvert.exec();
            //frame();
            //wincalc("min");
            //param();
            //query();
            //json();
            //uid();
            //script();
            //random();
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
                return new java.awt.Dimension(800, 800);
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
    }

    public static void init(Geometry... p) {
        Test t = new Test();
        t.mpol(p);
    }

    public void mpol(Geometry... p) {
        Polygon poly[] = new Polygon[p.length];
        for (int i = 0; i < p.length; ++i) {
            poly[i] = (Polygon) p[i];
        }
        mpol = gf.createMultiPolygon(poly);
    }

    public void mlin(LineString... p) {
        LineString line[] = new LineString[p.length];
        for (int i = 0; i < p.length; ++i) {
            line[i] = (LineString) p[i];
        }
        mpol = gf.createMultiLineString(line);
    }

    public static void frame() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Test().draw();
            }
        });
    }

    public void paincomp(Graphics g) {

        Graphics2D gc2d = (Graphics2D) g;

        gc2d.translate(10, 10);
        gc2d.scale(.4, .4);
        //
        //gc2d.translate(-4500, -900);
        //gc2d.translate(80, -940);
        //gc2d.scale(4, 4);

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

    private static void wincalc(String _case) throws Exception {

        Connect.setConnection(Test.connect2());
        builder.Wincalc winc = new builder.Wincalc();

        if (_case.equals("min")) {
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
        } else {

            String script = GsonScript.scriptPath(Integer.valueOf(_case));
            winc.build(script);
            //System.out.println(new com.google.gson.GsonBuilder().create().toJson(new com.google.gson.JsonParser().parse(script)));
            //System.out.println(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

            winc.specific(true);
            //new Joining(winc).calc();
            //winc.bufferImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            //winc.gc2d = winc.bufferImg.createGraphics();
            //winc.draw(); //рисую конструкцию

            frames.PSCompare.iwinPs4(winc, true);
            //winc.listElem.forEach(it -> System.out.println(it));
            //winc.listJoin.forEach(it -> System.out.println(it.joiningRec));     
            //winc.listJoin.forEach(it -> System.out.println(it));   
        }

    }

    private static void param() {

        Connect.setConnection(Test.connect2());
        WincalcTest.init(); //см. -601004,-604005,-700027

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
        {
            try {
                Connect.setConnection(Test.connect2());

            } catch (Exception e) {
                System.err.println("Ошибка:main.Test.query()");
            }
        }
        {
            //Пересчёт
            try {
                java.sql.Statement statement = Connect.getConnection().createStatement();
                Query q = new Query(eColor.values()).sql(eColor.data(), eColor.up).sort(eColor.id);
                int id = 0;
                for (Record rec : q) {
                    String ID = rec.getStr(eColor.id);
                    statement.executeUpdate("update color set id = " + String.valueOf(++id) + " where id = " + ID);
                }
            } catch (java.sql.SQLException e) {
                System.out.println("Query.update() " + e);
            }
        }
    }

    private static void json() {
        try {

            URL url = Test.class.getResource("/resource/json/param_desc.json");
            Path path = Paths.get(url.toURI());
            FileReader fileReader = new FileReader(path.toFile());
            
            List<Record> list = new ArrayList<>();
            JsonObject jsonObj = new GsonBuilder().create().fromJson(fileReader, JsonObject.class);
            JsonArray jsonArr = jsonObj.getAsJsonArray("records");
            
            for (JsonElement elem : jsonArr) {
                JsonObject jsonObect = new GsonBuilder().create().fromJson(elem, JsonObject.class);
                int id = jsonObect.get("id").getAsInt();
                String name = jsonObect.get("name").getAsString();
                String desc = jsonObect.get("desc").getAsString();
                Record record = new Record(List.of(id, name, desc));
                list.add(record);
            }            
            System.out.println(jsonArr);


//        Gson gson = new Gson();
//        //JsonParser parse = new JsonParser();
//
//        String str1 = "{\"developers\": [{ \"firstName\":777 , \"lastName\":\"Torvalds\" }, "
//                + "{ \"firstName\":\"John\" , \"lastName\":\"von Neumann\"}]}";
//
//        String str2 = "\"developers\": [ \"firstName\":\"Linus\" , \"lastName\":\"Torvalds\" }, "
//                + "{ \"firstName\":\"John\" , \"lastName\":\"von Neumann\" } ]}";
//
//        String str3 = null; //"{typeOpen:1, \"sysfurnID\":1634}";
//
//        JsonObject obj = gson.fromJson(str3, JsonObject.class);
//        Object out = obj.get("sysfurnID");
//        System.out.println(obj);
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
        } catch (Exception e) {
            System.err.println("TEST-MAIN: " + e);
        }
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
//        try {
//            double Q = 9;
//            double L = 8;
//            double H = 7;
//            String script = "5";
//            
//            JexlEngine jexl = new JexlBuilder().create();
//            JexlExpression expression = jexl.createExpression(script);
//            MapContext context = new MapContext();
//            context.set("Q", Q);
//            context.set("L", L);
//            context.set("H", H);
//            Object result = expression.evaluate(context);
//            
//            System.out.println("Result: " + result);
//
//        } catch (Exception e) {
//            System.err.println(e);
//        }
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
        Coordinate[] coord1 = new Coordinate[]{
            new Coordinate(0, 0, 1), new Coordinate(0, 1400, 2),
            new Coordinate(900, 1400, 3), new Coordinate(900, 0, 4),
            new Coordinate(0, 0, 1)};
        Polygon poly = gf.createPolygon(coord1);
        poly.setUserData(null);
        Coordinate[] coo = poly.copy().getCoordinates();

        LineString line = gf.createLineString(new Coordinate[]{new Coordinate(450, 0, 8), new Coordinate(450, 1400, 8)});
        Coordinate c1 = new Coordinate(0.0, 0.0);
        Coordinate c2 = new Coordinate(45.0, 45.0);
        Coordinate c3 = new Coordinate(45.0, 45.0);

        LineString line2 = gf.createLineString(new Coordinate[]{c1, c2});

        Point p0 = gf.createPoint(new Coordinate(0, 0, 1));
        p0.setUserData(null);

        Geometry geo = poly.union(line);
        //if (line2.contains(gf.createPoint(c3)) || (c1.x == c3.x && c1.y == c3.y)) {
        if (PointLocation.isOnSegment(c3, c1, c2)) {
            System.out.println("OK");
        } else {
            System.out.println("NO");
        }

        //Geometry polys = UGeo.polygonize7(geo);
        //System.out.println(polys);
//        Test.init(intersection);
    }

    public static void clearDB() {

        if (JOptionPane.showConfirmDialog(null, "СОЗДАНИЕ СКРИПТА ИЗ БД № " + eProp.base_num.getProp(), "СКРИПТ УДАЛЕНИЯ ДАННЫХ",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
            for (Field field : App.db) {
                if (field.tname().equals("GROUPS") == true) {
                    System.out.println("delete from " + field.tname() + " where id < 0;");
                } else if (field.tname().equals("SYSTREE") == false) {
                    System.out.println("delete from " + field.tname() + ";");
                }
            }
            for (Field field : App.db) {
                if (field.tname().equals("SYSTREE") == false) {
                    System.out.println("set generator gen_" + field.tname() + " to " + "0;");
                }
                if (field.tname().equals("GROUPS") == true) {
                    System.out.println("set generator gen_" + field.tname() + " to " + "-9999;");
                }
            }
        }
    }

// <editor-fold defaultstate="collapsed" desc="TEMP">
    public void draw10() {
        try {
            WKTReader reader = new WKTReader();
            String wkt1 = "POLYGON ((0 0, 0 1400, 308.2278481012648 1400, 1220 257.8191734498005, 1220 0, 0 0))";
            Polygon poly1 = (Polygon) reader.read(wkt1);

            String wkt2 = " POLYGON ((1220 43, 1220 0, 1177 0, 43 0, 0 0, 0 43, 0 1357, 0 1400, 43 1400, 308.2278481012648 1400, 1220 257.8191734498005, 1220 43), (43 43, 1177 43, 1177 257.8191734498005, 1189.930651845747 257.8191734498005, 308.2278481012648 1362.3320005363962, 308.2278481012648 1357, 43 1357, 43 43))";
            Polygon poly2 = (Polygon) reader.read(wkt2);

            mpol = gf.createMultiPolygon(new Polygon[]{poly1, poly2});

        } catch (Exception e) {
            System.err.println("startup.Test.draw6() " + e);
        }
    }

    private void draw9() {
//    WKTReader2 reader = new WKTReader2();
//    String wkt = "Polygon ((578250.83733634161762893 -5529552.83186665736138821, 578250.83733634161762893 -5529552.83186665736138821, 610067.62374519626609981 -5530111.02110190037637949, 612579.47530379006639123 -5498294.23469304572790861, 651373.62715318310074508 -5496898.76160493772476912, 652646.66333545825909823 -5465547.4130439143627882, 655839.14103512768633664 -5429357.86414052732288837, 709983.49685370502993464 -5428520.58028766233474016, 709115.27472816628869623 -5461139.35142331290990114, 709445.62126647483091801 -5510588.09887696336954832, 706076.17220700345933437 -5551601.30665875878185034, 574064.41807201865594834 -5558299.57748167496174574, 578250.83733634161762893 -5529552.83186665736138821))";
//    Polygon poly = (Polygon) reader.read(wkt);

        Coordinate[] coord = new Coordinate[]{
            new Coordinate(100, 150), new Coordinate(100, 340),
            new Coordinate(350, 340), new Coordinate(350, 150),
            new Coordinate(100, 150)};
        Polygon poly = gf.createPolygon(coord);
        Coordinate[] coo = poly.copy().getCoordinates();

        LineSegment seg1 = new LineSegment(0, 0, 200, 200);
        LineSegment seg2 = seg1.offset(-50);

        LineString line1 = gf.createLineString(new Coordinate[]{seg1.p0, seg1.p1});
        LineString line2 = gf.createLineString(new Coordinate[]{seg2.p0, seg2.p1});

        mlin = gf.createMultiLineString(new LineString[]{line1, line2});
    }

    private void draw() {
        double TOP = 400.0;
        double BOT = 400.1;
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayList<Com5t> frames = new ArrayList();

        frames.add(new Com5t(1, new GsonElem(Type.BOX_SIDE, 0.0, TOP)));
        frames.add(new Com5t(7, new GsonElem(Type.BOX_SIDE, 0.0, BOT)));
        frames.add(new Com5t(3, new GsonElem(Type.BOX_SIDE, 800.0, BOT)));
        frames.add(new Com5t(4, new GsonElem(Type.BOX_SIDE, 800.0, TOP, TOP)));

        LineSegment segm1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
        LineString arc1 = UGeo.newLineArch(segm1.p1.x, segm1.p0.x, segm1.p0.y, TOP, 4);
        Coordinate arr[] = arc1.getCoordinates();
        List.of(arr).forEach(c -> c.z = 4);

        list.add(new Coordinate(frames.get(0).x1(), frames.get(0).y1(), frames.get(0).id));
        list.add(new Coordinate(frames.get(1).x1(), frames.get(1).y1(), frames.get(1).id));
        list.add(new Coordinate(frames.get(2).x1(), frames.get(2).y1(), frames.get(2).id));
        list.addAll(List.of(arr));

        Polygon geoShell = UGeo.newPolygon(list);
        Polygon geoFalz = Com5t.buffer(geoShell, frames, 0, 1);

        Coordinate[] cooShell = geoShell.getCoordinates();
        Coordinate[] cooFalz = geoFalz.getCoordinates();

        Map<Double, Double> hm = new HashMap();
        hm.put(1.0, 64.0 - 20.0);
        hm.put(7.0, 64.0 - 20.0);
        hm.put(3.0, 64.0 - 20.0);
        hm.put(4.0, 64.0 - 20.0);
        Polygon polyRect = UGeo.bufferRectangl(geoShell, hm);
        Coordinate[] coo7 = polyRect.getCoordinates();

        mpol(geoShell, geoFalz, polyRect);

        double length = UGeo.lengthCurve(geoFalz, 1);
        System.out.println(length);
    }

    private void draw7() {
        double TOP = 300.0;
        double BOT = 342.0;
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayList<Com5t> frames = new ArrayList();
        frames.add(new Com5t(1, new GsonElem(Type.BOX_SIDE, 0.0, TOP)));
        frames.add(new Com5t(2, new GsonElem(Type.BOX_SIDE, 0.0, BOT)));
        frames.add(new Com5t(3, new GsonElem(Type.BOX_SIDE, 1300.0, BOT)));
        frames.add(new Com5t(4, new GsonElem(Type.BOX_SIDE, 1300.0, TOP, TOP)));

        LineSegment segm1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
        LineString arc1 = UGeo.newLineArch(segm1.p1.x, segm1.p0.x, segm1.p0.y, TOP, 4);
        Coordinate arr[] = arc1.getCoordinates();
        List.of(arr).forEach(c -> c.z = 4);

        list.add(new Coordinate(frames.get(0).x1(), frames.get(0).y1(), frames.get(0).id));
        list.add(new Coordinate(frames.get(1).x1(), frames.get(1).y1(), frames.get(1).id));
        list.add(new Coordinate(frames.get(2).x1(), frames.get(2).y1(), frames.get(2).id));
        list.addAll(List.of(arr));

        Map<Double, Double> hm = new HashMap();
        hm.put(1.0, 63.0 - 21.0);
        hm.put(2.0, 42.0 - 21.0);
        hm.put(3.0, 63.0 - 21.0);
        hm.put(4.0, 63.0 - 21.0);

        Polygon geoShell = UGeo.newPolygon(list);
        Polygon geo3 = Com5t.buffer(geoShell, frames, 0, 0);

        //this.mlin = geoShell;
        mpol(geo3);
    }

    private void draw6() {
        try {
            double M = 360;
            ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
            ArrayList<Com5t> frames = new ArrayList();
            frames.add(new Com5t(1, new GsonElem(Type.BOX_SIDE, 0.0, 300.0)));
            frames.add(new Com5t(2, new GsonElem(Type.BOX_SIDE, 0.0, M)));
            frames.add(new Com5t(3, new GsonElem(Type.BOX_SIDE, 1300.0, M)));
            frames.add(new Com5t(4, new GsonElem(Type.BOX_SIDE, 1300.0, 300.0, 300.0)));

            LineSegment s1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
            LineString arc1 = UGeo.newLineArch(s1.p1.x, s1.p0.x, s1.p0.y, 300, 4);
            Coordinate arr[] = arc1.getCoordinates();

            list.add(new Coordinate(frames.get(0).x1(), frames.get(0).y1(), frames.get(0).id));
            list.add(new Coordinate(frames.get(1).x1(), frames.get(1).y1(), frames.get(1).id));
            list.add(new Coordinate(frames.get(2).x1(), frames.get(2).y1(), frames.get(2).id));
            //list.add(new Coordinate(frames.get(3).x1(), frames.get(3).y1(), frames.get(3).id));
            list.addAll(List.of(arr));
            list.add(new Coordinate(frames.get(0).x1(), frames.get(0).y1(), frames.get(0).id));

            //POLYGON ((0 0, 0 1400, 218.37657479725863 1400, 1220 87.23202097634623, 1220 0, 0 0))
            //POLYGON ((0 0, 0 1400, 218.4 1400, 1220 87.2, 1220 0, 0 0))
            LineString geo1 = Com5t.gf.createLineString(list.toArray(new Coordinate[0]));
            //Polygon geo2 = VBuffer.buffer(geo1, frames, 0, 0);
            //Polygon geo4 = UGeo.bufferCurve(geo1, 40.0);
            Polygon geo4 = Com5t.buffer(geo1, frames, 0, 0);

            //Coordinate coo1[] = geo1.getCoordinates();
            //Coordinate coo2[] = geo2.getCoordinates();
            //Coordinate coo3[] = geo3.getCoordinates();
            //Coordinate coo4[] = geo4.getCoordinates();
            //mlin = gf.createMultiLineString(new LineString[]{geo1});
            mpol(geo4);

        } catch (Exception e) {
            System.err.println("startup.Test.draw6() " + e);
        }
    }

    private void draw5() {
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayList<Com5t> frames = new ArrayList();
        frames.add(new Com5t(1, new GsonElem(Type.BOX_SIDE, 0.0, 300.0)));
        frames.add(new Com5t(2, new GsonElem(Type.BOX_SIDE, 0.0, 350.0)));
        frames.add(new Com5t(3, new GsonElem(Type.BOX_SIDE, 1300.0, 350.0)));
        frames.add(new Com5t(4, new GsonElem(Type.BOX_SIDE, 1300.0, 300.0, 300.0)));

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
        //this.mlin = UGeo.bufferPaddin(this.mpol, frames, 20);

    }

    private void draw4() {

        ArrayList<Coordinate> list = new ArrayList<Coordinate>();

        list.add(new Coordinate(0, 600, 1));
        list.add(new Coordinate(0, 1500, 2));
        list.add(new Coordinate(0, 1500, 2));
        list.add(new Coordinate(1300, 1500, 3));
        list.add(new Coordinate(1300, 1500, 3));
        list.add(new Coordinate(1300, 300, 4));
        list.add(new Coordinate(0, 600, 1));

        Map<Double, Double> hm = new HashMap();
        hm.put(1.0, 32.0);
        hm.put(2.0, 68.0);
        hm.put(3.0, 32.0);
        hm.put(4.0, 68.0);

        double dist[] = {32, 32, 68, 68, 32, 68, 68};
        Geometry geo1 = Com5t.gf.createLineString(list.toArray(new Coordinate[0]));
        //VariableBuffer vb = new  VariableBuffer(geo1, distance);
        Geometry geo2 = VBuffer.buffer(geo1, dist);

        //this.mpol = geo1;
        this.mpol = geo2;
    }

    private void draw3() {

        double M = 1500;
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(10));
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayList<Com5t> frames = new ArrayList();
        frames.add(new Com5t(1, new GsonElem(Type.BOX_SIDE, 0.0, 300.0)));
        frames.add(new Com5t(2, new GsonElem(Type.BOX_SIDE, 0.0, M)));
        frames.add(new Com5t(3, new GsonElem(Type.BOX_SIDE, 1300.0, M)));
        frames.add(new Com5t(4, new GsonElem(Type.BOX_SIDE, 1300.0, 300.0, 300.0)));
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
        //Polygon geo2 = UGeo.bufferPaddin(geo1, frames, 0);
        //this.mlin = gf.createMultiPolygon(new Polygon[]{geo1, geo2});

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

    public static void colorRAR() {

        /*
        String str[] = {
            "RAL 1000", "RAL 1001", "RAL 1002", "RAL 1003", "RAL 1004", "RAL 1005", "RAL 1006", "RAL 1007", "RAL 1011", "RAL 1012", "RAL 1013",
            "RAL 1014", "RAL 1015", "RAL 1016", "RAL 1017", "RAL 1018", "RAL 1019", "RAL 1020", "RAL 1021", "RAL 1023", "RAL 1024", "RAL 1026",
            "RAL 1027", "RAL 1028", "RAL 1032", "RAL 1033", "RAL 1034", "RAL 1035", "RAL 1036", "RAL 1037", "RAL 2000", "RAL 2001", "RAL 2002",
            "RAL 2003", "RAL 2004", "RAL 2005", "RAL 2007", "RAL 2008", "RAL 2009", "RAL 2010", "RAL 2011", "RAL 2012", "RAL 2013", "RAL 2017",
            "RAL 3000", "RAL 3001", "RAL 3002", "RAL 3003", "RAL 3004", "RAL 3005", "RAL 3007", "RAL 3009", "RAL 3011", "RAL 3012", "RAL 3013",
            "RAL 3014", "RAL 3015", "RAL 3016", "RAL 3017", "RAL 3018", "RAL 3020", "RAL 3022", "RAL 3024", "RAL 3026", "RAL 3027", "RAL 3028",
            "RAL 3031", "RAL 3032", "RAL 3033", "RAL 4001", "RAL 4002", "RAL 4003", "RAL 4004", "RAL 4005", "RAL 4006", "RAL 4007", "RAL 4008",
            "RAL 4009", "RAL 4010", "RAL 4011", "RAL 4012", "RAL 5000", "RAL 5001", "RAL 5002", "RAL 5003", "RAL 5004", "RAL 5005", "RAL 5007",
            "RAL 5008", "RAL 5009", "RAL 5010", "RAL 5011", "RAL 5012", "RAL 5013", "RAL 5014", "RAL 5015", "RAL 5017", "RAL 5018", "RAL 5019",
            "RAL 5020", "RAL 5021", "RAL 5022", "RAL 5023", "RAL 5024", "RAL 5025", "RAL 5026", "RAL 6000", "RAL 6001", "RAL 6002", "RAL 6003",
            "RAL 6004", "RAL 6005", "RAL 6006", "RAL 6007", "RAL 6008", "RAL 6009", "RAL 6010", "RAL 6011", "RAL 6012", "RAL 6013", "RAL 6014",
            "RAL 6015", "RAL 6016", "RAL 6017", "RAL 6018", "RAL 6019", "RAL 6020", "RAL 6021", "RAL 6022", "RAL 6024", "RAL 6025", "RAL 6026",
            "RAL 6027", "RAL 6028", "RAL 6029", "RAL 6032", "RAL 6033", "RAL 6034", "RAL 6035", "RAL 6036", "RAL 6037", "RAL 6038", "RAL 6039",
            "RAL 7000", "RAL 7001", "RAL 7002", "RAL 7003", "RAL 7004", "RAL 7005", "RAL 7006", "RAL 7008", "RAL 7009", "RAL 7010", "RAL 7011",
            "RAL 7012", "RAL 7013", "RAL 7015", "RAL 7016", "RAL 7021", "RAL 7022", "RAL 7023", "RAL 7024", "RAL 7026", "RAL 7030", "RAL 7031",
            "RAL 7032", "RAL 7033", "RAL 7034", "RAL 7035", "RAL 7036", "RAL 7037", "RAL 7038", "RAL 7039", "RAL 7040", "RAL 7042", "RAL 7043",
            "RAL 7044", "RAL 7045", "RAL 7046", "RAL 7047", "RAL 7048", "RAL 8000", "RAL 8001", "RAL 8002", "RAL 8003", "RAL 8004", "RAL 8007",
            "RAL 8008", "RAL 8011", "RAL 8012", "RAL 8014", "RAL 8015", "RAL 8016", "RAL 8017", "RAL 8019", "RAL 8022", "RAL 8023", "RAL 8024",
            "RAL 8025", "RAL 8028", "RAL 8029", "RAL 9001", "RAL 9002", "RAL 9003", "RAL 9004", "RAL 9005", "RAL 9006", "RAL 9007", "RAL 9010",
            "RAL 9011", "RAL 9012", "RAL 9016", "RAL 9017", "RAL 9018", "RAL 9022", "RAL 9023"};

        Object obj[] = {
            0xCDBA88, 0xCDBA88, 0xD0B084, 0xD2AA6D, 0xF9A900, 0xE49E00, 0xCB8F00, 0xE19000, 0xE88C00, 0xAF8050, 0xDDAF28, 0xE3D9C7, 0xDDC49B, 0xE6D2B5,
            0xF1DD39, 0xF6A951, 0xFACA31, 0xA48F7A, 0xA08F65, 0xF6B600, 0xF7B500, 0xBA8F4C, 0xFFFF00, 0xA77F0F, 0xFF9C00, 0xE2A300, 0xF99A1D, 0xEB9C52,
            0x8F8370, 0x806440, 0xF09200, 0xDA6E00, 0xBA481C, 0xBF3922, 0xF67829, 0xE25304, 0xFF4D08, 0xFFB200, 0xEC6B22, 0xDE5308, 0xD05D29, 0xE26E0F,
            0xD5654E, 0x923E25, 0xFC5500, 0xA72920, 0x9B2423, 0x9B2321, 0x861A22, 0x6B1C23, 0x59191F, 0x3E2022, 0x6D342D, 0x782423, 0xC5856D, 0x972E25,
            0xCB7375, 0xD8A0A6, 0xA63D30, 0xCA555D, 0xC63F4A, 0xBB1F11, 0xCF6955, 0xFF2D21, 0xFF2A1C, 0xAB273C, 0xCC2C24, 0xA63437, 0x701D24, 0xA53A2E,
            0x816183, 0x8D3C4B, 0xC4618C, 0x651E38, 0x76689A, 0x903373, 0x47243C, 0x844C82, 0x9D8692, 0xBB4077, 0x6E6387, 0x6A6B7F, 0x304F6E, 0x0E4C64,
            0x00387A, 0x1F3855, 0x191E28, 0x005387, 0x376B8C, 0x2B3A44, 0x215F78, 0x004F7C, 0x1A2B3C, 0x0089B6, 0x193153, 0x637D96, 0x007CAF, 0x005B8C,
            0x048B8C, 0x005E83, 0x00414B, 0x007577, 0x222D5A, 0x41698C, 0x6093AC, 0x20697C, 0x0F3052, 0x3C7460, 0x366735, 0x325928, 0x50533C, 0x024442,
            0x114232, 0x3C392E, 0x2C3222, 0x36342A, 0x27352A, 0x4D6F39, 0x6B7C59, 0x2F3D3A, 0x7C765A, 0x474135, 0x3D3D36, 0x00694C, 0x587F40, 0x60993B,
            0xB9CEAC, 0x37422F, 0x8A9977, 0x3A3327, 0x008351, 0x5E6E3B, 0x005F4E, 0x7EBAB5, 0x315442, 0x006F3D, 0x237F52, 0x45877F, 0x7AADAC, 0x194D25,
            0x04574B, 0x008B29, 0x00B51B, 0xB3C43E, 0x7A888E, 0x8C979C, 0x817863, 0x797669, 0x9A9B9B, 0x6B6E6B, 0x766A5E, 0x745F3D, 0x5D6058, 0x585C56,
            0x52595D, 0x575D5E, 0x575044, 0x4F5358, 0x383E42, 0x2F3234, 0x4C4A44, 0x808076, 0x45494E, 0x374345, 0x928E85, 0x5B686D, 0xB5B0A1, 0x7F8274,
            0x92886F, 0xC5C7C4, 0x979392, 0x7A7B7A, 0xB0B0A9, 0x6B665E, 0x989EA1, 0x8E9291, 0x4F5250, 0xB7B3A8, 0x8D9295, 0x7E868A, 0xC8C8C7, 0x817B73,
            0x89693F, 0x9D622B, 0x794D3E, 0x7E4B27, 0x8D4931, 0x70462B, 0x724A25, 0x5A3827, 0x66332B, 0x4A3526, 0x5E2F26, 0x4C2B20, 0x442F29, 0x3D3635,
            0x1A1719, 0xA45729, 0x795038, 0x755847, 0x513A2A, 0x7F4031, 0xE9E0D2, 0xD6D5CB, 0xECECE7, 0x2B2B2C, 0x0E0E10, 0xA1A1A0, 0x868581, 0xF1EDE1,
            0x27292B, 0xF8F2E1, 0xF1F1EA, 0x29292A, 0xC8CBC4, 0x858583, 0x787B7A
        };
         */
    }

// </editor-fold> 
}
