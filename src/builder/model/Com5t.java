package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import common.listener.ListenerKey;
import common.listener.ListenerMouse;
import dataset.Record;
import domain.eArtikl;
import enums.Layout;
import enums.Type;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;

public class Com5t {

    public static GeometryFactory gf = new GeometryFactory();
    public double id;
    public Wincalc winc = null;
    public AreaSimple owner = null; //владелец
    public AreaSimple root = null; //главный класс конструкции
    public GsonElem gson = null; //gson object конструкции    
    public Type type = Type.NONE; //тип элемента или окна
    public Geometry area = null; //ареа компонента
    private boolean pass[] = {false, false};
    private Point pointPress = null;
    public int colorID1 = -1, colorID2 = -1, colorID3 = -1; //1-базовый 2-внутренний 3-внешний 
    public Record sysprofRec = null, artiklRec = null, artiklRecAn = null; //профиль системы, мат.средства, аналог.мат.средств

    public Com5t(Type type) {
        this.type = type;
    }

    public Com5t(Wincalc winc, double id, GsonElem gson, AreaSimple owner) {
        this.id = id;
        this.winc = winc;
        this.owner = owner;
        this.gson = gson;
        this.root = winc.root;
        this.type = gson.type;
    }

    public void paint() {
    }

    public void events() {

        ListenerKey keyPressed = (evt) -> {
            if (this.area != null) {
                double W = winc.canvas.getWidth();
                double H = winc.canvas.getHeight();
                double dX = 0;
                double dY = 0;
                if (pass[0] == true || pass[1] == true) {
                    if (evt.getKeyCode() == KeyEvent.VK_UP) {
                        dY = -.1;
                    } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                        dY = .1;
                    } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                        dX = -.1;
                    } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                        dX = .1;
                    }
                    if (pass[0] == true) {
                        double X1 = dX / winc.scale + x1();
                        double Y1 = dY / winc.scale + y1();
                        if (X1 >= 0 && X1 <= W / winc.scale && Y1 >= 0 && Y1 <= H / winc.scale) { //контроль выхода за канву
                            x1(X1);
                            y1(Y1);
                            //pointPress.setLocation(x1(), y1());
                        }
                    } else if (pass[1] == true) {
                        double X2 = dX / winc.scale + x2();
                        double Y2 = dY / winc.scale + y2();
                        if (X2 >= 0 && X2 <= W / winc.scale && Y2 >= 0 && Y2 <= H / winc.scale) { //контроль выхода за канву
                            x2(X2);
                            y2(Y2);
                            //pointPress.setLocation(x2(), y2());
                        }
                    }
                }
            }
        };
        ListenerMouse mousePressed = (evt) -> {
            if (this.area != null) {
                pointPress = evt.getPoint();
                //Если клик внутри контура
                boolean b = this.area.contains(gf.createPoint(new Coordinate(evt.getX() / winc.scale, evt.getY() / winc.scale)));
                if (b == true) {
                    LineSegment segm = new LineSegment(x1(), y1(), x2(), y2());
                    double coef = segm.segmentFraction(new Coordinate(evt.getX() / winc.scale, evt.getY() / winc.scale));
                    if (coef < .33) {
                        pass[0] = true; //кликнул ближе к началу вектора
                    } else if (coef > .67) {
                        pass[1] = true; //кликнул ближе к концу вектора
                    }
                }
            }
        };
        ListenerMouse mouseReleased = (evt) -> {

            pass[0] = false;
            pass[1] = false;
        };
        ListenerMouse mouseDragge = (evt) -> {
            if (this.area != null) {
                double W = winc.canvas.getWidth();
                double H = winc.canvas.getHeight();
                double dX = evt.getX() - pointPress.getX(); //прирощение по горизонтали
                double dY = evt.getY() - pointPress.getY(); //прирощение по вертикали 

                if (pass[0] == true) {
                    double X1 = dX / winc.scale + x1();
                    double Y1 = dY / winc.scale + y1();
                    if (X1 >= 0 && X1 <= W / winc.scale && Y1 >= 0 && Y1 <= H / winc.scale) { //контроль выхода за канву
                        x1(X1);
                        y1(Y1);
                        pointPress = evt.getPoint();
                    }
                } else if (pass[1] == true) {
                    double X2 = dX / winc.scale + x2();
                    double Y2 = dY / winc.scale + y2();
                    if (X2 >= 0 && X2 <= W / winc.scale && Y2 >= 0 && Y2 <= H / winc.scale) { //контроль выхода за канву                       
                        x2(X2);
                        y2(Y2);
                        pointPress = evt.getPoint();
                    }
                }
            }
        };

        this.winc.keyboardPressed.add(keyPressed);
        this.winc.mousePressed.add(mousePressed);
        this.winc.mouseReleased.add(mouseReleased);
        this.winc.mouseDragged.add(mouseDragge);
    }

    /**
     * Длина компонента
     */
    public double length() {
        return new LineSegment(this.x1(), this.y1(), this.x2(), this.y2()).getLength();
    }

    public Layout layout() {
        return Layout.FULL;
    }

    public boolean isJson(JsonObject jso, String key) {
        if (key == null) {
            if (jso == null || "".equals(jso)) {
                return false;
            }
            return !jso.isJsonNull();

        } else if (jso == null) {
            return false;

        } else if (jso.isJsonNull()) {
            return false;

        } else if (jso.get(key) == null) {
            return false;
        }
        return true;
    }

    public boolean equals(Object obj) {
        return (this.id == ((Com5t) obj).id);
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    public void setDimension(double x1, double y1, double x2, double y2) {
        if (pass[0] == false && pass[1] == false) {
            gson.x1 = x1;
            gson.y1 = y1;
            gson.x2 = x2;
            gson.y2 = y2;
        }
    }

    public double x1() {
        return (gson.x1 != null) ? gson.x1 : -1;
    }

    public double y1() {
        return (gson.y1 != null) ? gson.y1 : -1;
    }

    public Double h() {
        return (gson.h != null) ? gson.h : null;
    }

    public double x2() {
        if (gson.x2 != null) {
            return gson.x2;
        } else {
//            Coordinate[] coordArr = this.geom.getCoordinates();
//            for (int i = coordArr.length; i > 0; --i) {
//                if (coordArr[i].x == x1()) {
//                    return coordArr[i - 1].x;
//                }
//            }
            return -1;
        }
    }

    public double y2() {
        if (gson.y2 != null) {
            return gson.y2;
        } else {
//            Coordinate[] coordArr = this.geom.getCoordinates();
//            for (int i = coordArr.length; i > 0; --i) {
//                if (coordArr[i].y == y1()) {
//                    return coordArr[i - 1].y;
//                }
//            }
            return -1;
        }
    }

    public void x1(double v) {
        gson.x1 = v;
    }

    public void y1(double v) {
        gson.y1 = v;
    }

    public void x2(double v) {
        gson.x2 = v;
    }

    public void h(double v) {
        gson.h = v;
    }

    public void y2(double v) {
        gson.y2 = v;
    }

    public Double width() {
        return (x2() > x1()) ? x2() - x1() : x1() - x2();
    }

    public Double height() {
        return (y2() > y1()) ? y2() - y1() : y1() - y2();
    }

    public boolean inside(double x, double y) {
        int X = (int) x, Y = (int) y;
        int X1 = (int) x1(), Y1 = (int) y1(), X2 = (int) x2(), Y2 = (int) y2();

        if ((X2 | Y2) < 0) {
            return false;
        }

        if (x1() > x2()) {
            X1 = (int) x2();
            X2 = (int) x1();
        }

        if (y1() > y2()) {
            Y1 = (int) y2();
            Y2 = (int) y1();
        }

        if (X < X1 || Y < Y1) {
            return false;
        }
        return ((X2 >= X) && (Y2 >= Y));
    }

    @Override
    public String toString() {
        String art = (artiklRecAn == null) ? "null" : artiklRecAn.getStr(eArtikl.code);
        double ownerID = (owner == null) ? -1 : owner.id;
        return " art=" + art + ", type=" + type + ", layout=" + layout() + ", owner=" + ownerID + ", id=" + id
                + ", x1=" + x1() + ", y1=" + y1() + ", x2=" + x2() + ", y2=" + y2();
    }

    public static void PRINT(Geometry g) {
        Coordinate coo[] = g.getCoordinates();
        System.out.println(List.of(coo));
    }
    
    public static void PRINT(String s, Geometry g) {
        Coordinate coo[] = g.getCoordinates();
        System.out.println(s + " " + List.of(coo));
    }

    public static void PRINT(Coordinate... coo) {
        System.out.println(List.of(coo));
    }

    public static void PRINT(String s, Coordinate... coo) {
        System.out.println(s + " " + List.of(coo));
    }
    // </editor-fold>
}
