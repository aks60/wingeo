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
import common.eProp;
import dataset.Conn;
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
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.noding.IntersectionAdder;
import org.locationtech.jts.noding.MCIndexNoder;
import org.locationtech.jts.noding.NodedSegmentString;
import org.locationtech.jts.noding.SegmentString;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.operation.polygonize.Polygonizer;
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
            System.err.println("������:Test.connect() " + e);
            return null;
        }
    }

    public static Connection connect2() {
        eProp.user.write("sysdba");
        eProp.password = String.valueOf("masterkey");
        Conn.connection(eProp.server(numDb.toString()), eProp.port(numDb.toString()), eProp.base(numDb.toString()), eProp.user.read(), eProp.password.toCharArray(), null);
        return Conn.getConnection();
    }

    // </editor-fold>     
    //java -jar C:\\Okna\\winapp\\dist\\winapp.jar dev loc
    public static void main(String[] args) throws Exception {

        eProp.dev = true;
        try {
            //clearDataDB();
            //frames.PSConvert.exec();
            frame(args);
            //wincalc("min");
            //param();
            //query();
            //json();
            //uid();
            //script();
            //geom();
            //new Crypto().httpAsync("31.172.66.46");       

        } catch (Exception e) {
            System.err.println("TEST-MAIN: " + e);
        }
    }

    //�����������
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

        draw8();
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
        //gc2d.translate(-80, -1840);
        //gc2d.scale(4, 4);
        gc2d.translate(10, 10);
        gc2d.scale(.4, .4);

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

        Conn.setConnection(Test.connect2());
        builder.Wincalc winc = new builder.Wincalc();

        if (_case.equals("one")) {
            String script = GsonScript.scriptPath(604005);
            winc.build(script);
            //System.out.println(new com.google.gson.GsonBuilder().create().toJson(new com.google.gson.JsonParser().parse(script)));
            //System.out.println(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

            winc.specification(true);
            //new Joining(winc).calc();
            //winc.bufferImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            //winc.gc2d = winc.bufferImg.createGraphics();
            //winc.draw(); //����� �����������

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

        Conn.setConnection(Test.connect2());
        WincalcTest.init(); //��. -601004,-604005,-700027

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
                Conn.setConnection(Test.connect2());

            } catch (Exception e) {
                System.err.println("������:main.Test.query()");
            }
        }
        {
            //��������
            try {
                java.sql.Statement statement = Conn.getConnection().createStatement();
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
        //���������� ��������
//        double B = 3;
//        double L = 1200;
//        double H = 56;
//        engine.put("B", B);
//        engine.put("L", L);
//        engine.put("H", H);
//        engine.eval("var obj = new Object(); obj.length = L; obj.height = H;");
//        engine.eval("print(obj.length + obj.height);");
//        engine.eval("print(Math.ceil(L + 33.3));");
        //����� ������� �������� � �������
//        String script = "function hello(name) { print('Hello, ' + name); }";
//        engine.eval(script);
//        Invocable inv = (Invocable) engine;
//        inv.invokeFunction("hello", "������!!" );
        //���������� �� �������� ��������
//        String script = "var obj = new Object(); obj.hello = function(name) { print('Hello, ' + name); }";
//        engine.eval(script);
//        Invocable inv = (Invocable) engine;
//        Object obj = engine.get("obj");
//        inv.invokeMethod(obj, "hello", "������!!" );
        //���������� ����������� Java ����������
//        String script = "function run() { print('run called ������'); }";
//        engine.eval(script);
//        Invocable inv = (Invocable) engine;
//        Runnable r = inv.getInterface(Runnable.class);
//        Thread th = new Thread(r);
//        th.start();
//
        //�� �������� ��� ��������-������������
//        String script = "var obj = new Object(); obj.run = function() { print('run method called ������'); }";
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
//        int a = 0; // ��������� �������� ��������� - "��"
//        int b = 10; // �������� �������� ��������� - "��"
//
//        int random_number1 = a + (int) (Math.random() * b); // ��������� 1-�� �����
//        System.out.println("1-�� ��������� �����: " + random_number1);
//
//        int random_number2 = a + (int) (Math.random() * b); // ��������� 2-�� �����
//        System.out.println("2-�� ��������� �����: " + random_number2);
//
//        int random_number3 = a + (int) (Math.random() * b); // ��������� 3-�� �����
//        System.out.println("3-� ��������� �����: " + random_number3);
    }

    public static void geom() {
        //Toolkit.getDefaultToolkit().beep();//����!!!!
        GeometryFactory gf = new GeometryFactory();

        Coordinate[] coord1 = new Coordinate[]{
            new Coordinate(200, 200, 1), new Coordinate(200, 800, 2),
            new Coordinate(800, 800, 3), new Coordinate(800, 200, 4),
            new Coordinate(200, 200, 1)};

        LineString line = gf.createLineString(new Coordinate[]{new Coordinate(600, 100, -1), new Coordinate(800, 900, -1)});
        Polygon poly = gf.createPolygon(coord1);        
        Geometry intersection = poly.intersection( line );
        System.out.println(intersection);

//        Geometry geom = UGeo.splitPolyLine7(poly, line);
//
//        geom.getGeometryN(0).normalize();
//        geom.getGeometryN(1).normalize();
//
//        System.out.println(List.of(geom.getGeometryN(0).getCoordinates()));
//        System.out.println(List.of(geom.getGeometryN(1).getCoordinates()));

//        new Test().mpol = intersection;
    }

    public static void clearDataDB() {

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

// <editor-fold defaultstate="collapsed" desc="TEMP">
   public void draw10() {
        //POLYGON ((0 0, 0 1400, 1300 1363.013698630137, 1300 0, 0 0))
        //LINESTRING (650 1381.5068493150684, 650 0)
            List<SegmentString> segmentStrings = new ArrayList<>();
            segmentStrings.add(new NodedSegmentString(
                    new Coordinate[]{
                            new Coordinate(0, 0),
                            new Coordinate(0, 1400),
                            new Coordinate(1300, 1363.013698630137),
                            new Coordinate(1300, 0),
                            new Coordinate(1, 0),
                            new Coordinate(0, 0)
                    }, null));

            segmentStrings.add(new NodedSegmentString(
                    new Coordinate[]{
                            new Coordinate(650, 0),
                            new Coordinate(650, 1381.5068493150684)
                    }, null));

            
            
            Collection<Polygon> polygons = getPolygons(segmentStrings);
            for (Polygon polygon : polygons) {
                System.out.println(polygon);
            }
            List<Polygon> lst = new ArrayList<Polygon>(polygons);
            this.mpol = lst.get(0);
    }
   
    private void draw9() {
//GEOMETRYCOLLECTION (POLYGON ((100 150, 100 340, 350 340, 350 150, 100 150)), LINESTRING (220 340, 220 150))

        Coordinate[] coord = new Coordinate[]{
            new Coordinate(100, 150), new Coordinate(100, 340),
            new Coordinate(350, 340), new Coordinate(350, 150),
            new Coordinate(100, 150)};
        Polygon poly = gf.createPolygon(coord);
        Coordinate[] coo = poly.copy().getCoordinates();

        LineString line = gf.createLineString(new Coordinate[]{new Coordinate(220, 340), new Coordinate(220, 150)});
        //LineString[] lineStringArray = line.getLineStringArray();

        LineMerger merge = new LineMerger();
        Geometry nodedLinework = poly.getBoundary().union(line);
//        merge.add(poly);
//        merge.add(line);
//        Collection<LineString> coll = merge.getMergedLineStrings();
//        LineString[] larr = coll.toArray(new LineString[0]);
//        MultiLineString mls = gf.createMultiLineString(larr);
//        System.out.println(mls);
    }

    private void draw8() {           
        //POLYGON ((0 0, 0 1400, 1300 1363.013698630137, 1300 0, 0 0))
        //LINESTRING (650 1381.5068493150684, 650 0)
        Coordinate[] coord1 = new Coordinate[]{
            new Coordinate(0, 0, 1), new Coordinate(0, 1400, 2),
            new Coordinate(1300, 1363.013698630137, 3), new Coordinate(1300, 0, 4),
            new Coordinate(0, 0, 1)};
        Polygon poly = gf.createPolygon(coord1);
        Coordinate[] coo = poly.copy().getCoordinates();

        LineString line = gf.createLineString(new Coordinate[]{new Coordinate(650, 1381.5068493150684, 0), new Coordinate(650, 0, -1)});
        
       // LineMerger merge = new LineMerger();
       // merge.add(poly);
       // merge.add(line);
       // Collection<LineString> coll = merge.getMergedLineStrings();
      //gf.createLineString(coord1);
        //LineString[] larr = coll.toArray(new LineString[0]);
        //MultiLineString mls = gf.createMultiLineString(larr);        
        //Geometry nodedLinework = poly.getBoundary().union(line);
        //Geometry polys = polygonize(coll);        

        
        
        LineString line2 = UGeo.expandLine7(poly.getEnvelope(), line);
        Geometry geom = UGeo.splitPolyLine7(poly, line2);
        
        //System.out.println(poly);
        //System.out.println(line);        
        System.out.println(geom);

        // mlin = line;
        //mpol = poly;
        mpol = geom;
    }
    
    private static Collection<Polygon> getPolygons(List<SegmentString> segmentStrings) {
        GeometryFactory factory = new GeometryFactory();
        MCIndexNoder noder = new MCIndexNoder();
        noder.setSegmentIntersector(new IntersectionAdder(new RobustLineIntersector()));
        noder.computeNodes(segmentStrings);

        Collection<NodedSegmentString> nodedSubstrings = noder.getNodedSubstrings();

        List<LineString> lineStrings = new ArrayList<>();
        for (NodedSegmentString nss : nodedSubstrings) {
            lineStrings.add(factory.createLineString(nss.getCoordinates()));
        }

        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(lineStrings);

        return polygonizer.getPolygons();
    }
    
    public static Geometry polygonize(Collection<LineString> coll) {
        //List lines = LineStringExtracter.getLines(geometry);
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(coll);
        Collection polys = polygonizer.getPolygons();
        System.out.println(polys);
        Polygon[] polyArray = GeometryFactory.toPolygonArray(polys);
        return gf.createGeometryCollection(polyArray);
    }
    
    private void draw7() {
        double M = 1500;
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayList<Com5t> frames = new ArrayList();
        frames.add(new Com5t(1, new GsonElem(Type.FRAME_SIDE, 0.0, 300.0)));
        frames.add(new Com5t(2, new GsonElem(Type.FRAME_SIDE, 0.0, M)));
        frames.add(new Com5t(3, new GsonElem(Type.FRAME_SIDE, 1300.0, M)));
        frames.add(new Com5t(4, new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0)));

        LineSegment segm1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
        LineString line1 = gf.createLineString(new Coordinate[]{new Coordinate(1100, 0), new Coordinate(0, 500)});
        LineString arc1 = UGeo.newLineArch(segm1.p1.x, segm1.p0.x, segm1.p0.y, 300, 4);
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
        //Polygon geo2 = UGeo.bufferCross(geo1, hm, 0);
        Geometry geo2 = UGeo.splitPolyLine7(geo1, line1).getGeometryN(0);

        //this.mpol = geo1;
        this.mlin = geo1;
        this.mpol = geo2;
    }

    private void draw6() {
        double M = 420;
        //double M = 1500;
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayList<Com5t> frames = new ArrayList();

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
        Polygon geo2 = UGeo.buffer(geo1, frames, 0);
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
        ArrayList<Com5t> frames = new ArrayList();
        frames.add(new Com5t(1, new GsonElem(Type.FRAME_SIDE, 0.0, 300.0)));
        frames.add(new Com5t(2, new GsonElem(Type.FRAME_SIDE, 0.0, 350.0)));
        frames.add(new Com5t(3, new GsonElem(Type.FRAME_SIDE, 1300.0, 350.0)));
        frames.add(new Com5t(4, new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0)));

        //������������� ����� � ��������
        LineSegment s1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
        //s1.normalize();
        //double H = 300.0, ANG = Math.toDegrees(s1.angle());
        //aff.setToRotation(Math.toRadians(-ANG), s1.p0.x, s1.p0.y); //���� ������� 
        //LineString l1 = (LineString) aff.transform(s1.toGeometry(gf));
        //LineString arc1 = UGeo.newLineArch(l1.getCoordinateN(0).x, l1.getCoordinateN(1).x, l1.getCoordinateN(0).y, H, 4);  //����. ���� �� ����������� 
        LineString arc1 = UGeo.newLineArch(s1.p1.x, s1.p0.x, s1.p0.y, 300, 4);
        Coordinate arr1[] = arc1.getCoordinates();
        List.of(arr1).forEach(c -> c.z = 4);

        //�������� ������������� ����
//        aff.setToRotation(Math.toRadians(ANG), s1.p0.x, s1.p0.y); //���� �������  
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
        Geometry geo2 = UGeo.buffer(geo1, hm);

        mpol = geo1.union(geo2);
        // mlin = geo2;
    }

    private void draw3() {

        double M = 1500;
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(10));
        GeometricShapeFactory gsf = new GeometricShapeFactory();
        ArrayList<Coordinate> list = new ArrayList<Coordinate>(), list2 = new ArrayList<Coordinate>();
        ArrayList<Com5t> frames = new ArrayList();
        frames.add(new Com5t(1, new GsonElem(Type.FRAME_SIDE, 0.0, 300.0)));
        frames.add(new Com5t(2, new GsonElem(Type.FRAME_SIDE, 0.0, M)));
        frames.add(new Com5t(3, new GsonElem(Type.FRAME_SIDE, 1300.0, M)));
        frames.add(new Com5t(4, new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0)));
        LineSegment s1 = new LineSegment(frames.get(3).x1(), frames.get(3).y1(), frames.get(0).x1(), frames.get(0).y1());
        s1.normalize();
        double H = 200.0, DH = s1.p1.y - s1.p0.y, ANG = Math.toDegrees(s1.angle());

        //������������� ����� � ��������
        aff.setToRotation(Math.toRadians(-ANG), s1.p0.x, s1.p0.y); //���� �������      
        LineString l1 = (LineString) aff.transform(s1.toGeometry(gf));
        LineString arc1 = UGeo.newLineArch(l1.getCoordinateN(0).x, l1.getCoordinateN(1).x, l1.getCoordinateN(0).y, H, 4);  //����. ���� �� �����������  

        //�������� ������������� ����
        aff.setToRotation(Math.toRadians(ANG), s1.p0.x, s1.p0.y); //���� �������  
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
        double R = (Math.pow(L / 2, 2) + Math.pow(H, 2)) / (2 * H);  //R = (L2 + H2) / 2H - ������ ����

        //���� ����
        double rad1 = Math.acos((L / 2) / R);
        double rad2 = Math.acos((L - 2 * dH) / ((R - dH) * 2));
        double a1 = R * Math.sin(rad1);
        double a2 = (R - dH) * Math.sin(rad2);
        double ang3 = 90 - Math.toDegrees(Math.atan((a1 - a2) / dH)); //���� ���� ����
        double ang4 = 90 - (Math.toDegrees(rad1) - (90 - ang3)); //���� ���� ����

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
