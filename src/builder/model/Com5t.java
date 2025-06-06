package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import enums.Layout;
import enums.Type;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.swing.Timer;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.util.GeometricShapeFactory;
import startup.Test;

public class Com5t {

    public static final int MAXSIDE = 200;
    public static final int MAXPOINT = 1000;

    //public static GeometryFactory gf = new GeometryFactory(new PrecisionModel(1000));
    //public static GeometryFactory gf = new GeometryFactory(Co7eSequenceFactory.instance());
    //public static Co7eSequenceFactory sf = Co7eSequenceFactory.instance();    
    public static GeometryFactory gf = new GeometryFactory(new PrecisionModel());
    public static GeometricShapeFactory gsf = new GeometricShapeFactory(gf);
    public static AffineTransformation aff = new AffineTransformation();

    public double id;
    public Wincalc winc = null;
    public AreaSimple owner = null; //��������
    public AreaSimple root = null; //������� ����� �����������
    public GsonElem gson = null; //gson object �����������    
    public Type type = Type.NONE; //��� �������� ��� ����
    public Geometry area = null; //���� ���������� 
    public Timer timer = new javax.swing.Timer(1200, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            winc.canvas.repaint();
        }
    }); //��������� �������� �����������
    public int colorID1 = -1, colorID2 = -1, colorID3 = -1; //1-������� 2-���������� 3-������� 
    public Record sysprofRec = null, artiklRec = null, artiklRecAn = null; //������� �������, ���.��������, ������.���.�������

    public Com5t() {
    }

    public Com5t(Type type) {
        this.type = type;
        timer.setRepeats(false);
    }

    public Com5t(double id, GsonElem gson) {
        this.id = id;
        this.type = gson.type;
        this.gson = gson;
        timer.setRepeats(false);
    }

    public Com5t(Wincalc winc, double id, GsonElem gson, AreaSimple owner) {
        this.id = id;
        this.winc = winc;
        this.owner = owner;
        this.gson = gson;
        this.root = winc.root;
        this.type = gson.type;
        timer.setRepeats(false);
    }

    public void paint() {
    }

    /**
     * ����� ����������
     */
    public double length() {

        if (this.h() == null) {
            return new LineSegment(this.x1(), this.y1(), this.x2(), this.y2()).getLength();
        } else {
            return UGeo.lengthCurve(owner.area.getGeometryN(0), this.id);
        }
    }

    public Layout layout() {
        return Layout.FULL;
    }

    public java.awt.Color color() {
        return (timer.isRunning() == true) ? new java.awt.Color(255, 120, 0) : new java.awt.Color(eColor.find(this.colorID2).getInt(eColor.rgb));
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

    public void paintSceleton() {
        Shape shape1 = new ShapeWriter().toShape(this.area.getGeometryN(0));
        Shape shape2 = new ShapeWriter().toShape(this.area.getGeometryN(1));
        Shape shape3 = new ShapeWriter().toShape(this.area.getGeometryN(2));

        //winc.gc2d.setColor(new java.awt.Color(eColor.find(this.colorID2).getInt(eColor.rgb)));
        //winc.gc2d.fill(shape1);
        //winc.gc2d.fill(shape2);
        //winc.gc2d.fill(shape3);
        winc.gc2d.setColor(new java.awt.Color(000, 000, 255));
        winc.gc2d.draw(shape1);
        winc.gc2d.draw(shape2);
        winc.gc2d.draw(shape3);
    }

    public static Polygon buffer(Geometry poly, ArrayList<? extends Com5t> list, double amend, int op) {
        //return VBuffer.buffer(poly, list, amend, op);
        return UGeo.bufferGeometry(poly, list, amend, op);
    }

    public boolean equals(Object obj) {
        return (this.id == ((Com5t) obj).id);
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    public Double x1() {
        return gson.x1;
    }

    public Double y1() {
        return gson.y1;
    }

    public Double h() {
        return gson.h;
    }

    public Double x2() {
        return gson.x2;
    }

    public Double y2() {
        return gson.y2;
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

    public LineSegment segment() {
        return UGeo.normalizeSegm(new LineSegment(new Coordinate(this.x1(), this.y1(), this.id), new Coordinate(this.x2(), this.y2(), this.id)));
    }

    @Override
    public String toString() {
        String art = (artiklRecAn == null) ? "null" : artiklRecAn.getStr(eArtikl.code);
        double ownerID = (owner == null) ? -1 : owner.id;
        return " art=" + art + ", type=" + type + ", layout=" + layout() + ", owner=" + ownerID + ", id=" + id
                + ", x1=" + x1() + ", y1=" + y1() + ", x2=" + x2() + ", y2=" + y2();
    }
    // </editor-fold>
}
