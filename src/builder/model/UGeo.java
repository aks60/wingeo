package builder.model;

import builder.making.TRecord;
import static builder.model.Com5t.gf;
import common.LineSegm;
import common.UCom;
import dataset.Record;
import domain.eArtikl;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import org.locationtech.jts.algorithm.Angle;
import static org.locationtech.jts.algorithm.Angle.angle;
import static org.locationtech.jts.algorithm.Angle.diff;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.algorithm.PointLocation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;
import java.util.HashMap;
import java.util.Map;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.operation.buffer.VariableBuffer;
import org.locationtech.jts.operation.polygonize.Polygonizer;

/**
 * Утилиты JTS
 */
public class UGeo {

    public static boolean isInRing(double x, double y, Geometry g) {
        try {
            Coordinate c = new Coordinate(x, y);
            return PointLocation.isInRing(c, g.getGeometryN(0).getCoordinates());

        } catch (Exception e) {
            System.err.println("Ошибка:Com5t.inside()");
            return false;
        }
    }

    //Угол неориентированный к горизонту. Угол нормируется в диапазоне [0, 2PI].
    public static double anglHor(double x1, double y1, double x2, double y2) {
        double ang = Math.toDegrees(Angle.angle(new Coordinate(x1, y1), new Coordinate(x2, y2)));
        return (ang > 0) ? 360 - ang : Math.abs(ang);
    }

    //Угол ориентированный к горизонту. Угол нормируется в диапазоне [-Pi, Pi].
    public static double anglHor(ElemSimple e) {
        double ang = Math.toDegrees(Angle.angle(new Coordinate(e.x1(), e.y1()), new Coordinate(e.x2(), e.y2())));
        return (ang > 0) ? 360 - ang : Math.abs(ang);
    }

    //Угол реза
    public static double anglCut(TRecord spcRec, Geometry area, int index1, int index2, char direction) {
        LineSegment s1a = UGeo.getSegment(area, index1);
        LineSegment s2a = UGeo.getSegment(area, index2);
        LineSegment s1b = s1a.offset(-spcRec.height);
        LineSegment s2b = s2a.offset(-spcRec.height);
        Coordinate cross = s1b.intersection(s2b);
        while (cross == null) {
            if (direction == '-') {
                s1a = UGeo.getSegment(area, --index1);
                s1b = s1a.offset(-spcRec.height);
                cross = s1b.intersection(s2b);
            } else {
                s2a = UGeo.getSegment(area, ++index2);
                s2b = s2a.offset(-spcRec.height);
                cross = s1b.intersection(s2b);
            }
        }
        double gip = (direction == '-') ? s2a.p0.distance(cross) : s1a.p1.distance(cross);
        return Math.toDegrees(Math.asin(spcRec.height / gip));
    }

    public static double lengthArc(double L, double R, double prip, double angCut1, double angCut2) {
        
        double angl = Math.toDegrees(Math.asin((L / 2) / R));
        double lengthArc = ((2 * Math.PI * R) / 360 * angl * 2); //*2
        
        double prip0 = prip * Math.sin(Math.toRadians(45));
        double prip1 = prip0 / Math.sin(Math.toRadians(angCut1));
        double prip2 = prip0 / Math.sin(Math.toRadians(angCut2));
        
        return lengthArc + prip1 + prip2;
    }

    //Угол неориентированный неомежду профилями
    public static double anglBetbeeem(ElemSimple e1, ElemSimple e2) {

        double c1 = angle(new Coordinate(e1.x2(), e1.y2()), new Coordinate(e1.x1(), e1.y1()));
        double c2 = angle(new Coordinate(e2.x2(), e2.y2()), new Coordinate(e2.x1(), e2.y1()));

        return Math.toDegrees(diff(c1, c2));
    }

    //Пересечение сегмента(линии) импоста с сегментами(отрезками) многоугольника
    public static Coordinate[] geoCross(Geometry poly, LineSegment line) {
        try {
            poly = poly.getGeometryN(0);
            List<Coordinate> out = new ArrayList<Coordinate>();
            Coordinate[] c = poly.getCoordinates();
            for (int i = 1; i < c.length; i++) {

                Coordinate segm1 = c[i - 1];
                Coordinate segm2 = c[i];
                Coordinate c3 = Intersection.lineSegment(line.p0, line.p1, segm1, segm2);
                if (c3 != null) {
                    out.add(c3);
                }
            }
            if (out.get(0).compareTo(out.get(1)) < 0) {
                Coordinate temp = out.get(0);
                out.set(0, out.get(1));
                out.set(1, temp);
            }
            return out.toArray(new Coordinate[0]);

        } catch (Exception e) {
            System.err.println("geoIntersect" + e);
        }
        return null;
    }

    //Пилим многоугольник
    public static Geometry[] splitPolygon(Geometry geom, ElemCross impost) {
        try {
            boolean b = true;
            HashSet<Coordinate> checkHs = new HashSet<Coordinate>();
            Coordinate[] coo = geom.getGeometryN(0).copy().getCoordinates();
            List<Coordinate> cooL = new ArrayList<Coordinate>(), cooR = new ArrayList<Coordinate>();
            List<Coordinate> crosTwo = new ArrayList<Coordinate>(), listExt = new ArrayList<Coordinate>(List.of(coo[0]));
            LineSegment segmImp = normalizeSegm(new LineSegment(
                    new Coordinate(impost.x1(), impost.y1(), impost.id),
                    new Coordinate(impost.x2(), impost.y2(), impost.id)));

            //Вставим точки пересечения в список коорд. см.exten
            for (int i = 1; i < coo.length; i++) {
                Coordinate crosP = Intersection.lineSegment(segmImp.p0, segmImp.p1, coo[i - 1], coo[i]); //точка пересечения сегмента и линии                
                checkHs.add(coo[i]);
                //Вставим точку в сегмент
                if (crosP != null) {
                    crosTwo.add(crosP);
                    if (checkHs.add(crosP)) {
                        listExt.add(crosP);
                    }
                }
                listExt.add(coo[i]);
            }

            //Обход сегментов до и после точек пересечения
            for (int i = 0; i < listExt.size(); ++i) {
                Coordinate crd = listExt.get(i);

                //Проход через точку пересечения
                if (Double.isNaN(crd.z)) {
                    b = !b; //первая точка пройдена
                    Coordinate cL = new Coordinate(crd.x, crd.y, segmImp.p0.z);
                    Coordinate cR = new Coordinate(crd.x, crd.y);

                    if (crosTwo.get(0).equals(crd)) {
                        cL.z = segmImp.p0.z;
                        cR.z = listExt.get(i - 1).z;
                    } else {
                        cL.z = listExt.get(i - 1).z;
                        cR.z = segmImp.p0.z;
                    }
                    cooL.add(cL);
                    cooR.add(cR);

                } else { //Построение координат слева и справа от импоста
                    ((b == true) ? cooL : cooR).add(crd);
                }
            }
            //Построение 'пятой' точки
            if (segmImp.p0.y != segmImp.p1.y) {
                Collections.rotate(cooR, 1);
                cooR.add(cooR.get(0));
            } else {
                cooR.add(cooR.get(0));
            }
            return new Geometry[]{
                Com5t.gf.createLineString(crosTwo.toArray(new Coordinate[0])),
                Com5t.gf.createPolygon(cooL.toArray(new Coordinate[0])),
                Com5t.gf.createPolygon(cooR.toArray(new Coordinate[0]))
            };

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.splitPolygon()" + e);
            return null;
        }
    }

    //Для вычисления стеклопакетов, штапиков...
    public static Polygon buffer(Geometry line, Map<Double, Double> hm) {
        try {
            Coordinate coo[] = line.getCoordinates();
            double distance[] = new double[coo.length];
            for (int i = 0; i < coo.length; ++i) {
                distance[i] = hm.get(coo[i].z);
            }
            LineString ls = line.getFactory().createLineString(line.getCoordinates());
            VariableBuffer geoBuffer = new VariableBuffer(ls, distance);
            Geometry geom = geoBuffer.getResult();
            return ringToPolygon(line, geom);
        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.buffer() " + e);
            return null;
        }
    }

    //Обводка полигона, работает быстро. При вырождении полигона загибы на концах арки
    public static Polygon bufferCross(Geometry str, ArrayList<? extends Com5t> list, double amend) {
        int i = 0;
        Polygon result = gf.createPolygon();
        Com5t e1 = null, e2 = null;
        Deque<Coordinate> deqList = new ArrayDeque<Coordinate>();
        List<Coordinate> cooList = new ArrayList<Coordinate>();
        LineSegment seg1a = new LineSegm(), seg2a = new LineSegm(), seg1b = null, seg2b = null;
        Coordinate[] coo = str.getCoordinates();
        Coordinate cross = new Coordinate();
        Map<Double, Double> hm = new HashMap();
        try {
            for (Com5t el : list) {
                Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
                hm.put(el.id, rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr) + amend);
            }
            for (i = 1; i < coo.length; i++) {

                //Перебор сегментов для вычисления точки пересечения
                if (i > Com5t.MAXSIDE || (cross != null && i < Com5t.MAXSIDE)) {
                    final double ID = coo[i - 1].z;
                    e1 = list.stream().filter(e -> e.id == ID).findFirst().get();
                    seg1a.setCoordinates(coo[i - 1], coo[i]);
                    seg1b = seg1a.offset(-hm.get(e1.id));
                }
                if (i < Com5t.MAXSIDE || (cross != null && i > Com5t.MAXSIDE)) {
                    int j = (i == coo.length - 1) ? 1 : i + 1;
                    final double ID = coo[i].z;
                    e2 = list.stream().filter(e -> e.id == ID).findFirst().get();
                    seg2a.setCoordinates(coo[i], coo[j]);
                    seg2b = seg2a.offset(-hm.get(e2.id));
                }

                //Точка пересечения сегментов
                cross = seg2b.intersection(seg1b);

                if (cross != null) { //заполнение очереди
                    deqList.addLast(cross);
                    cross.z = coo[i].z;

                } else {
                    if (e2.h() == null) { //обрезание хвоста слева
                        List<Coordinate> loop = new ArrayList(deqList);
                        for (int k = loop.size() - 1; k >= 0; --k) {

                            seg1b = new LineSegm(loop.get(k), loop.get(k - 1), loop.get(k).z);
                            cross = seg2b.intersection(seg1b);

                            if (cross != null) {
                                deqList.addLast(cross);
                                cross.z = coo[i].z;
                                break;
                            } else {
                                deqList.pollLast();
                            }
                        }
                    }
                }
                while (deqList.size() > 200) {
                    cooList.add(deqList.pollFirst());
                }
            }
            while (deqList.isEmpty() == false) {
                cooList.add(deqList.pollFirst());
            }
            cooList.add(0, cooList.get(cooList.size() - 1));
            result = gf.createPolygon(cooList.toArray(new Coordinate[0]));

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.buffeCross() " + e);
        }
        return result;
    }
    public static Polygon buffer2Cross(Geometry str, ArrayList<? extends Com5t> list, double amend) {
        int i = 0;
        Polygon result = gf.createPolygon();
        Com5t e1 = null, e2 = null;
        Deque<Coordinate> deqList = new ArrayDeque<Coordinate>();
        List<Coordinate> cooList = new ArrayList<Coordinate>();
        LineSegment segLShell = new LineSegm(), segLInner = null, segRShell = new LineSegm(), segRInner = null;
        Coordinate[] coo = str.getCoordinates();
        Coordinate cross = new Coordinate();
        Map<Double, Double> hm = new HashMap();
        try {
            for (Com5t el : list) {
                Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
                hm.put(el.id, rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr) + amend);
            }
            for (i = 1; i < coo.length; i++) {

                //Перебор сегментов для вычисления точки пересечения
                if (i > Com5t.MAXSIDE || (cross != null && i < Com5t.MAXSIDE)) {
                    final double ID = coo[i - 1].z;
                    e1 = list.stream().filter(e -> e.id == ID).findFirst().get();
                    segLShell.setCoordinates(coo[i - 1], coo[i]);
                    segLInner = segLShell.offset(-hm.get(e1.id));
                }
                if (i < Com5t.MAXSIDE || (cross != null && i > Com5t.MAXSIDE)) {
                    int j = (i == coo.length - 1) ? 1 : i + 1;
                    final double ID = coo[i].z;
                    e2 = list.stream().filter(e -> e.id == ID).findFirst().get();
                    segRShell.setCoordinates(coo[i], coo[j]);
                    segRInner = segRShell.offset(-hm.get(e2.id));
                }

                //Точка пересечения сегментов
                cross = segRInner.intersection(segLInner);

                if (cross != null) { //заполнение очереди
                    deqList.addLast(cross);
                    cross.z = coo[i].z;

                } else {
                    if (e2.h() == null) { //обрезание хвоста слева
                        List<Coordinate> loop = new ArrayList(deqList);
                        for (int k = loop.size() - 1; k >= 0; --k) {

                            segLInner = new LineSegm(loop.get(k), loop.get(k - 1), loop.get(k).z);
                            cross = segRInner.intersection(segLInner);

                            if (cross != null) {
                                deqList.addLast(cross);
                                cross.z = coo[i].z;
                                break;
                            } else {
                                deqList.pollLast();
                            }
                        }
                    }
                }
                while (deqList.size() > 200) {
                    cooList.add(deqList.pollFirst());
                }
            }
            while (deqList.isEmpty() == false) {
                cooList.add(deqList.pollFirst());
            }
            cooList.add(0, cooList.get(cooList.size() - 1));
            result = gf.createPolygon(cooList.toArray(new Coordinate[0]));

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.buffeCross() " + e);
        }
        return result;
    }

    public static Polygon ringToPolygon(Geometry line, Geometry geom) {

        Coordinate coo1[] = line.getGeometryN(0).getCoordinates();
        LinearRing ring = ((Polygon) geom).getInteriorRingN(0);
        Polygon geom2 = (Polygon) line.getFactory().createPolygon(ring).norm();
        Coordinate coo2[] = geom2.getCoordinates();
        for (int i = 0; i < coo2.length - 1; ++i) {
            coo2[i].z = coo1[i].z;
        }
        coo2[coo2.length - 1].z = coo2[0].z;
        return (Polygon) line.getFactory().createPolygon(coo2);
    }

    //Список входн. параметров не замыкается начальной точкой как в jts!
    public static Coordinate[] arrCoord(double... d) {
        List<Coordinate> list = new ArrayList<Coordinate>();
        for (int i = 1; i < d.length; i = i + 2) {
            list.add(new Coordinate(d[i - 1], d[i]));
        }
        list.add(new Coordinate(d[0], d[1]));

        return list.toArray(new Coordinate[0]);
    }

    public static Point newPoint(double x, double y) {
        return Com5t.gf.createPoint(new Coordinate(x, y));
    }

    public static LineString newLineStr(double... d) {
        return Com5t.gf.createLineString(UGeo.arrCoord(d));
    }

    public static LineString newLineArch(double x1, double x2, double y, double h, double z) {
        try {
            double R = (Math.pow((x2 - x1) / 2, 2) + Math.pow(h, 2)) / (2 * h);  //R = (L2 + H2) / 2H - радиус арки
            double angl = Math.PI / 2 - Math.asin((x2 - x1) / (R * 2));
            Com5t.gsf.setSize(2 * R);
            Com5t.gsf.setNumPoints(1000);
            Com5t.gsf.setBase(new Coordinate(x1 + (x2 - x1) / 2 - R, y - h));
            LineString ls = Com5t.gsf.createArc(Math.PI + angl, Math.PI - 2 * angl).reverse();
            Coordinate lm[] = Arrays.copyOf(ls.getCoordinates(), ls.getCoordinates().length);
            List.of(lm).forEach(c -> c.z = z);
            return gf.createLineString(lm);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.newLineArch()");
            return null;
        }
    }

    //Список входн. параметров не замыкается начальной точкой как в jts!
    public static Polygon newPolygon(double... d) {
        return Com5t.gf.createPolygon(UGeo.arrCoord(d));
    }

    public static Polygon newPolygon(List<Coordinate> list) {
        if (list.get(0).equals2D(list.get(list.size() - 1)) == false) {
            list.add(list.get(0));
        }
        return Com5t.gf.createPolygon(list.toArray(new Coordinate[0]));
    }

    public static LineSegment getSegment(LineString line) {
        return new LineSegment(line.getCoordinateN(0), line.getCoordinateN(1));
    }

    public static LineSegment getSegment(Geometry poly, int index) {
        poly = poly.getGeometryN(0);
        Coordinate[] coo = Arrays.copyOf(poly.getCoordinates(), poly.getNumPoints() - 1);
        index = (index >= coo.length) ? index - coo.length : index;
        int j = (index < 0) ? index + coo.length : index;
        int k = (j + 1 >= coo.length) ? j + 1 - coo.length : j + 1;

        return new LineSegment(coo[j], coo[k]);
    }

    public static List<Coordinate> getSegmentArch(Coordinate coo[], ElemSimple elem) {
        int index = 0;
        List<Coordinate> list = new ArrayList<Coordinate>();
        for (int i = 0; i < coo.length; i++) {
            if (coo[i].z == elem.id) {
                list.add(coo[i]);
                index = i;
            }
        }
        list.add(coo[++index]);
        return list;
    }

    public static int getIndex(Geometry geo, double id) {
        Coordinate coo[] = geo.getGeometryN(0).getCoordinates();
        for (int i = 0; i < coo.length - 1; i++) {
            if (coo[i].z == id) {
                return i;
            }
        }
//        }
        //throw new Exception("Ошибка:UGeo.getIndex()");
        System.err.println("Ошибка:UGeo.getIndex()");
        return -1;
    }

    public static int getIndex(Coordinate[] coo, int index) {

        if (index > coo.length - 1) {
            return index - coo.length;
        }
        if (index < 0) {
            return coo.length + index - 1;
        }
        return index;
    }

    public static LineSegment normalizeSegm(LineSegment segm) {
        segm.normalize();
        return segm;
    }

    public static Coordinate offset(LineSegment s1, LineSegment s2, double d) {
        LineSegment s3 = s1.offset(d);
        LineSegment s4 = s2.offset(d);
        Coordinate c = s3.intersection(s4);
        return c;
    }

    /**
     * Размерные линии
     *
     * @param midle
     * @param tipX - точка поворота
     * @param tipY - точка поворота
     * @param angl - угол поворота
     * @param length - длина линии
     * @return
     */
    public static Geometry lineTip(boolean midle, double tipX, double tipY, double angl, double length) {

        double dx = (midle == false) ? 0 : 16;
        Geometry tip = gf.createLineString(new Coordinate[]{
            new Coordinate(tipX - length, tipY), new Coordinate(tipX, tipY),
            new Coordinate(tipX - dx, tipY - 16), new Coordinate(tipX, tipY),
            new Coordinate(tipX - dx, tipY + 16)});
        AffineTransformation aff = new AffineTransformation();
        aff.setToRotation(Math.toRadians(angl), tipX, tipY);
        return aff.transform(tip);
    }

// <editor-fold defaultstate="collapsed" desc="TEMP">  
    public static Geometry normalizeGeo(Geometry geo) {
        geo.normalize();
        return geo;
    }

    public static int normalizeElem(Com5t c) {

        if (c.x2() < c.x1()) {
            double x = c.x1(), y = c.y1();
            c.x1(c.x2());
            c.y1(c.y2());
            c.x2(x);
            c.y2(y);
            return -1;
        }
        if (c.x2() > c.x1()) {
            return 1;
        }
        if (c.y2() < c.y1()) {
            double x = c.x1(), y = c.y1();
            c.x1(c.x2());
            c.y1(c.y2());
            c.x2(x);
            c.y2(y);
            return -1;
        }
        if (c.y2() > c.y1()) {
            return 1;
        }
        return 0;
    }

    //Новая реализация с использованием Envelope но неработает!!!
    public static Geometry[] splitPolygon6(Geometry poly, LineString line) {
        double impID = 8;
        boolean f = true;
        List<Coordinate> ls = new ArrayList<Coordinate>();

        Envelope env = poly.getEnvelopeInternal();
        Coordinate coo[] = {new Coordinate(env.getMinX(), env.getMinY()),
            new Coordinate(env.getMinX(), env.getMaxY()), new Coordinate(env.getMaxX(), env.getMaxY()),
            new Coordinate(env.getMaxX(), env.getMinY()), new Coordinate(env.getMinX(), env.getMinY()),};

        for (int i = 0; i < 4; i++) {
            if (f) {
                ls.add(coo[i]);
            }
            Coordinate cross = Intersection.lineSegment(line.getCoordinateN(0), line.getCoordinateN(1), coo[i], coo[i + 1]);

            if (cross != null) {
                ls.add(cross);
                f = !f;
            }
        }
        ls.add(coo[0]);
        Geometry poly0 = gf.createPolygon(ls.toArray(new Coordinate[0]));
        Geometry poly1 = poly.intersection(poly0);
        Geometry poly2 = poly.difference(poly0);
        poly1.normalize();
        poly2.normalize();
        Coordinate coo1[] = poly1.getCoordinates();
        Coordinate coo2[] = poly2.getCoordinates();

        for (int i = 1; i < coo1.length; i++) {
            for (int k = 1; k < coo2.length; k++) {
                if (coo1[i].equals(coo2[k])) {
                    if ((f = !f) == false) {
                        coo2[k].z = coo1[i - 1].z;
                        coo1[i].z = impID;
                    } else {
                        coo1[i].z = coo2[k - 1].z;
                        coo2[k].z = impID;
                    }
                }
            }
        }
        coo1[0].z = coo1[coo1.length - 1].z;
        coo2[0].z = coo2[coo2.length - 1].z;

        //UGeo.PRINT("poly1 ", poly1);
        //UGeo.PRINT("poly2 ", poly2);
        return new Geometry[]{poly1, poly2};
    }

    //Новая реализация с использованием Envelope        
    public static Geometry[] splitPolygonEnvelope(Geometry poly, ElemCross impost) {
        boolean f = true;
        List<Coordinate> cooEnvL = new ArrayList<Coordinate>(); //левый конверт
        List<Coordinate> cooImp = new ArrayList<Coordinate>();

        LineSegment imp = normalizeSegm(new LineSegment(new Coordinate(impost.x1(), impost.y1()), new Coordinate(impost.x2(), impost.y2())));
        Envelope env = poly.getEnvelopeInternal();
        Coordinate cooPoly[] = poly.getCoordinates();
        Coordinate cooEnv[] = {new Coordinate(env.getMinX(), env.getMinY()),
            new Coordinate(env.getMinX(), env.getMaxY()), new Coordinate(env.getMaxX(), env.getMaxY()),
            new Coordinate(env.getMaxX(), env.getMinY()), new Coordinate(env.getMinX(), env.getMinY()),};

        //Координаты левого конверта
        for (int i = 0; i < 4; i++) {
            if (f) {
                cooEnvL.add(cooEnv[i]);
            }
            Coordinate cross = Intersection.lineSegment(imp.p0, imp.p1, cooEnv[i], cooEnv[i + 1]);

            if (cross != null) {
                cooEnvL.add(cross);
                f = !f;
            }
        }
        cooEnvL.add(cooEnv[0]);

        //Левый, правый полигон
        Geometry envelope = gf.createPolygon(cooEnvL.toArray(new Coordinate[0])); //левый конверт
        Geometry polyL = poly.intersection(envelope); //левый полигон
        Geometry polyR = poly.difference(envelope); //правый полигон
        polyL.normalize();
        polyR.normalize();

        //Заполним z ключи
        Coordinate cooL[] = polyL.getCoordinates();
        Coordinate cooR[] = polyR.getCoordinates();

        for (int i = 1; i < cooL.length; i++) {
            for (int k = 1; k < cooR.length; k++) {
                if (cooL[i].equals(cooR[k])) {

                    if (cooImp.isEmpty()) {
                        cooImp.add(cooL[i]); //координаты импоста
                        cooL[i].z = impost.id;
                        cooR[k].z = cooL[i - 1].z;
                    } else {
                        cooImp.add(cooL[i]); //координаты импоста
                        cooL[i].z = cooPoly[k - 1].z;
                        cooR[k].z = impost.id;
                    }
                }
            }
        }

        //Test проверка
        for (int i = 0; i < cooL.length; i++) {
            if (cooL[i].z % 1 != 0) {
                PRINT("Ошибка*:UGeo.splitPolygon: ", cooL);
            }
        }
        for (int i = 0; i < cooR.length; i++) {
            if (cooR[i].z % 1 != 0) {
                PRINT("Ошибка*:UGeo.splitPolygon: ", cooR);
            }
        }

        return new Geometry[]{Com5t.gf.createLineString(cooImp.toArray(new Coordinate[0])), polyL, polyR};
    }

    //Пилим многоугольник 
    public static Geometry[] splitPolyImp7(Geometry geom, ElemCross impost) {
        try {
            LineString lineImp = gf.createLineString(new Coordinate[]{
                new Coordinate(impost.x1(), impost.y1()),
                new Coordinate(impost.x2(), impost.y2())});

            LineString line = expandLine7(geom, lineImp);
            line.normalize();
            LineString line2 = expandLine7(geom.getEnvelope(), lineImp);
            line2.normalize();

            //Делим полигон линией
            Geometry gm = UGeo.splitPolyLine7(geom, line2);

            gm.getGeometryN(0).normalize();
            gm.getGeometryN(1).normalize();

            for (Coordinate c : gm.getGeometryN(0).getCoordinates()) {
                if (c.equals2D(line.getCoordinateN(0)) || c.equals2D(line.getCoordinateN(1))) {
                    c.z = impost.id;
                }
            }

            for (Coordinate c : gm.getGeometryN(1).getCoordinates()) {
                if (c.equals2D(line.getCoordinateN(0)) || c.equals2D(line.getCoordinateN(1))) {
                    c.z = impost.id;
                }
            }

            return new Geometry[]{line, gm.getGeometryN(0), gm.getGeometryN(1)};

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.splitPolygonImp() " + e);
            return null;
        }
    }

    //см. https://gis.stackexchange.com/questions/189976/jts-split-arbitrary-polygon-by-a-line
    public static Geometry splitPolyLine7(final Geometry poly, final Geometry line) {

        Geometry nodedLinework = poly.getBoundary().union(line);
        Geometry polys = polygonize7(nodedLinework);

        //Оставить только полигоны, находящиеся внутри входных данных
        List<Polygon> output = new ArrayList();
        for (int i = 0; i < polys.getNumGeometries(); i++) {
            Polygon candpoly = (Polygon) polys.getGeometryN(i);
            if (poly.contains(candpoly.getInteriorPoint())) {
                output.add(candpoly);
            }
        }
        GeometryCollection geometryCollection = gf.createGeometryCollection(GeometryFactory.toGeometryArray(output));
        return geometryCollection;
        //return gf.createGeometryCollection(new Geometry[] {polys.getGeometryN(0), polys.getGeometryN(1)});
    }

    public static LineString expandLine7(Geometry geom, LineString impost) {
        List<Coordinate> lineCross = new ArrayList<Coordinate>();
        Coordinate impP0 = new Coordinate(impost.getPointN(0).getX(), impost.getPointN(0).getY());
        Coordinate impP1 = new Coordinate(impost.getPointN(1).getX(), impost.getPointN(1).getY());
        Coordinate[] coo = geom.copy().getCoordinates();

        for (int i = 1; i < coo.length; i++) {
            //Точка пересечения линии и сегмента
            Coordinate segmP0 = coo[i - 1], segmP1 = coo[i];
            Coordinate crosP = Intersection.lineSegment(impP0, impP1, segmP0, segmP1);
            if (crosP != null) {
                lineCross.add(new Coordinate(crosP.x, crosP.y, segmP0.z));
            }
        }
        LineString line = Com5t.gf.createLineString(lineCross.toArray(new Coordinate[0]));
        return line;
    }

    public static Geometry polygonize7(Geometry geometry) {
        List lines = LineStringExtracter.getLines(geometry);
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(lines);
        Collection polys = polygonizer.getPolygons();
        Polygon[] polyArray = GeometryFactory.toPolygonArray(polys);
        return geometry.getFactory().createGeometryCollection(polyArray);
    }

    //При вырождении полигона загибы на концах арки
    public static Polygon bufferPaddin(Geometry poly, ArrayList<? extends Com5t> list, double amend) {
        LineSegment segm1, segm2, segm1a = null, segm2a = null, segm1b, segm2b, segm1c, segm2c;
        Coordinate cros1 = null, cros2 = null;
        List<Coordinate> outList = new ArrayList<Coordinate>();
        try {
            poly = poly.getGeometryN(0);
            int j = 999, k = 999;
            Coordinate[] coo = poly.copy().getCoordinates();
            for (int i = 0; i < coo.length; i++) {

                //Сегменты границ полигона
                segm1 = UGeo.getSegment(poly, i - 1);
                segm2 = UGeo.getSegment(poly, i);

                //Получим ширину сегментов             
                final double ID1 = segm1.p0.z, ID2 = segm2.p0.z;
                Com5t e1 = list.stream().filter(e -> e.id == ID1).findFirst().get();
                Com5t e2 = list.stream().filter(e -> e.id == ID2).findFirst().get();
                Record rec1 = (e1.artiklRec == null) ? eArtikl.virtualRec() : e1.artiklRec;
                Record rec2 = (e2.artiklRec == null) ? eArtikl.virtualRec() : e2.artiklRec;
                double w1 = (rec1.getDbl(eArtikl.height) - rec1.getDbl(eArtikl.size_centr)) - amend;
                double w2 = (rec2.getDbl(eArtikl.height) - rec2.getDbl(eArtikl.size_centr)) - amend;

                //Смещение сегментов относительно границ
                if (segm1.getLength() != 0 && segm2.getLength() != 0) {
                    segm1a = segm1.offset(-w1);
                    segm2a = segm2.offset(-w2);

                    //Точка пересечения внутренних сегментов
                    Coordinate cross = segm2a.intersection(segm1a);

                    if (cross != null && i < j - 1) {
                        cross.z = e2.id;
                        outList.add(cross);

                    } else { //обрезаем концы арки

                        if (cros1 == null && e1.h() != null) { //хвост
                            j = i - 1;
                            do {
                                segm1b = UGeo.getSegment(poly, --j);
                                segm1c = segm1b.offset(-w1);
                                cros1 = segm2a.intersection(segm1c);

                            } while (cros1 == null);
                            cros1.z = e2.id;
                            outList.add(cros1);
                            j = (j < 0) ? --j + coo.length : --j; //для обрезания кончика арки

                        }
                        if (cros2 == null && e2.h() != null) {  //кончик
                            k = i;
                            do {
                                segm2b = UGeo.getSegment(poly, ++k);
                                segm2c = segm2b.offset(-w2);
                                cros2 = segm2c.intersection(segm1a);

                            } while (cros2 == null);
                            i = k;
                            cros2.z = e2.id;
                            outList.add(cros2);
                        }
                    }
                }
            }
            if (outList.get(0).equals(outList.get(outList.size() - 1)) == false) {
                outList.add(outList.get(0));
            }
            Polygon geo = Com5t.gf.createPolygon(outList.toArray(new Coordinate[0]));
            return geo;

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.bufferPadding() " + e);
            return null;
        }
    }

    public static Polygon buffer(Geometry line, ArrayList<? extends Com5t> list, double amend) {

        //Map дистанций
        Map<Double, Double> hm = new HashMap();
        for (Com5t el : list) {
            Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
            Double delta1 = rec.getDbl(eArtikl.height);
            Double delta2 = rec.getDbl(eArtikl.size_centr);
            hm.put(el.id, delta1 - delta2 + amend);
        }
        //Array дистанций
        Coordinate coo[] = line.getGeometryN(0).getCoordinates();
        double distance[] = new double[coo.length];
        for (int i = 0; i < coo.length; ++i) {
            distance[i] = hm.get(coo[i].z);
        }
        LineString ls = line.getFactory().createLineString(line.getCoordinates());
        VariableBuffer vb = new VariableBuffer(ls, distance);
        Geometry geo = vb.getResult();
        return ringToPolygon(line, geo);
    }

    //Обводка полигона, работает быстро. При вырождении полигона теряются p.z
    public static Polygon bufferUnion(Geometry str, ArrayList<? extends Com5t> list, Map<Double, Double> hm) {
        try {
            List<Geometry> geoList = new ArrayList();
            List<Coordinate> arcStr = new ArrayList<Coordinate>(),
                    arcTop = new ArrayList<Coordinate>(),
                    arcBot = new ArrayList<Coordinate>();

            Coordinate[] coo = str.getCoordinates();
            for (int i = 1; i < coo.length; i++) {

                final double ID1 = coo[i - 1].z, ID2 = coo[i].z;
                Com5t e1 = list.stream().filter(e -> e.id == ID1).findFirst().get();
                Com5t e2 = list.stream().filter(e -> e.id == ID2).findFirst().get();
                LineSegment seg1a = new LineSegm(coo[i - 1], coo[i], coo[i - 1].z);
                LineSegment seg1b = seg1a.offset(-hm.get(e1.id));

                if (e1.h() != null && e2.h() != null) {
                    LineSegment seg2a = new LineSegm(coo[i], coo[i + 1], coo[i].z);
                    LineSegment seg2b = seg2a.offset(-hm.get(e2.id));
                    Coordinate cross = seg2b.intersection(seg1b);
                    arcTop.add(seg2a.p0);
                    arcBot.add(cross);

                } else if (e1.h() == null) {
                    Polygon ls = gf.createPolygon(new Coordinate[]{seg1a.p0, seg1a.p1, seg1b.p1, seg1b.p0, seg1a.p0});
                    geoList.add(ls);
                }
            }
            Geometry arcGeo = gf.createLineString();
            if (arcBot.isEmpty() == false) {
                Collections.reverse(arcBot);
                arcStr.addAll(arcBot);
                arcStr.addAll(arcTop);
                arcStr.add(arcStr.get(0));
                arcGeo = gf.createPolygon(arcStr.toArray(new Coordinate[0]));
            }
            GeometryCollection partsGeom = gf.createGeometryCollection(GeometryFactory.toGeometryArray(geoList));
            Geometry buffer = partsGeom.union().union(arcGeo);

            Geometry ggg = (Polygon) buffer.getGeometryN(0);

            LinearRing ring = ((Polygon) buffer).getInteriorRingN(0);
            Polygon poly = (Polygon) gf.createPolygon(ring).norm();
            Coordinate cor[] = poly.getCoordinates();
            for (int i = 0; i < cor.length - 1; ++i) {
                cor[i].z = coo[i].z;
            }
            cor[cor.length - 1].z = cor[0].z;
            return gf.createPolygon(cor);
            //return (Polygon) arcGeo;

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.bufferUnion()  " + e);
            return gf.createPolygon();
        }
    }

    //Ошибка если арка правильная! см. 604004 
    public static Polygon bufferUnion(Geometry str, ArrayList<? extends Com5t> list, double amend) {
        try {
            Map<Double, Double> hm = new HashMap();
            for (Com5t el : list) {
                Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
                hm.put(el.id, rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr) + amend);
            }
            List<Geometry> geoList = new ArrayList();
            List<Coordinate> arcStr = new ArrayList<Coordinate>(),
                    arcTop = new ArrayList<Coordinate>(),
                    arcBot = new ArrayList<Coordinate>();

            Coordinate[] coo = str.getCoordinates();
            for (int i = 1; i < coo.length; i++) {

                final double ID1 = coo[i - 1].z, ID2 = coo[i].z;
                Com5t e1 = list.stream().filter(e -> e.id == ID1).findFirst().get();
                Com5t e2 = list.stream().filter(e -> e.id == ID2).findFirst().get();
                LineSegment seg1a = new LineSegm(coo[i - 1], coo[i], coo[i - 1].z);
                LineSegment seg1b = seg1a.offset(-hm.get(e1.id));

                if (e1.h() != null && e2.h() != null) {
                    LineSegment seg2a = new LineSegm(coo[i], coo[i + 1], coo[i].z);
                    LineSegment seg2b = seg2a.offset(-hm.get(e2.id));
                    Coordinate cross = seg2b.intersection(seg1b);
                    arcTop.add(seg2a.p0);
                    arcBot.add(cross);

                } else if (e1.h() == null) {
                    Polygon ls = gf.createPolygon(new Coordinate[]{seg1a.p0, seg1a.p1, seg1b.p1, seg1b.p0, seg1a.p0});
                    geoList.add(ls);
                }
            }
            Geometry arcGeo = gf.createLineString();
            if (arcBot.isEmpty() == false) {
                Collections.reverse(arcBot);
                arcStr.addAll(arcBot);
                arcStr.addAll(arcTop);
                arcStr.add(arcStr.get(0));
                arcGeo = gf.createPolygon(arcStr.toArray(new Coordinate[0]));
            }
            GeometryCollection partsGeom = gf.createGeometryCollection(GeometryFactory.toGeometryArray(geoList));
            Geometry buffer = partsGeom.union().union(arcGeo);

            LinearRing ring = ((Polygon) buffer).getInteriorRingN(0);
            Polygon poly = (Polygon) gf.createPolygon(ring).norm();
            Coordinate cor[] = poly.getCoordinates();
            for (int i = 0; i < cor.length - 1; ++i) {
                cor[i].z = coo[i].z;
            }
            cor[cor.length - 1].z = cor[0].z;
            return gf.createPolygon(cor);
            //return (Polygon) arcGeo;

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.bufferUnion() " + e);
            return gf.createPolygon();
        }
    }

    //Угол реза
    public static double angCut(Coordinate c1, Coordinate c2, Coordinate c3, double delta) {
        LineSegment seg1 = new LineSegment(c1, c2);
        LineSegment seg2 = new LineSegment(c2, c3);
        Coordinate c4 = seg1.offset(delta).lineIntersection(seg2.offset(delta));
        LineSegment seg3 = new LineSegment(c2, c4);
        return Math.toDegrees(Math.asin(delta / seg3.getLength()));
    }

    public static void PRINT(Geometry g) {
        Coordinate coo[] = g.getCoordinates();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < coo.length; i++) {
            list.add("{" + UCom.format(coo[i].x, 2) + " " + UCom.format(coo[i].y, 2) + " " + UCom.format(coo[i].z, 2) + "}");
        }
        System.out.println(list);
    }

    public static void PRINT(String s, Geometry g) {
        Coordinate coo[] = g.getCoordinates();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < coo.length; i++) {
            list.add("{" + UCom.format(coo[i].x, 2) + " " + UCom.format(coo[i].y, 2) + " " + UCom.format(coo[i].z, 2) + "}");
        }
        System.out.println(s + " " + list);
    }

    public static void PRINT(Map<Integer, Coordinate> map) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<Integer, Coordinate> coo : map.entrySet()) {
            list.add("{" + UCom.format(coo.getValue().x, 2) + " " + UCom.format(coo.getValue().y, 2) + " " + UCom.format(coo.getValue().z, 2) + "}");
        }
        System.out.println(list);
    }

    public static void PRINT(Coordinate... coo) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < coo.length; i++) {
            list.add("{" + UCom.format(coo[i].x, 2) + " " + UCom.format(coo[i].y, 2) + " " + UCom.format(coo[i].z, 2) + "}");
        }
        System.out.println(list);
    }

    public static void PRINT(String s, Coordinate... coo) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < coo.length; i++) {
            list.add("{" + UCom.format(coo[i].x, 2) + " " + UCom.format(coo[i].y, 2) + " " + UCom.format(coo[i].z, 2) + "}");
        }
        System.out.println(s + " " + list);
    }

    public static List<Coordinate> formatVal(Geometry g) {
        List<Coordinate> list = new ArrayList();
        Coordinate coo[] = g.getCoordinates();
        List.of(coo).forEach(c -> list.add(new Coordinate((int) c.x, (int) c.y, c.z)));
        return list;
    }
// </editor-fold>    
}
