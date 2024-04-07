package builder.model;

import static builder.model.Com5t.gf;
import common.ArrayCom;
import common.LineSegm;
import dataset.Record;
import domain.eArtikl;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.util.AffineTransformation;
import java.util.HashMap;
import java.util.Map;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import startup.Test;

/**
 * Утилиты JTS
 */
public class UGeo {

    public static PrecisionModel pm = null;

    public static void precisionModel(PrecisionModel pm) {
        pm = new PrecisionModel(pm);
    }

    public static boolean isInRing(double x, double y, Geometry g) {
        try {
            Coordinate c = new Coordinate(x, y);
            return PointLocation.isInRing(c, g.getGeometryN(0).getCoordinates());

        } catch (Exception e) {
            System.err.println("Ошибка:Com5t.inside()");
            return false;
        }
    }

    //Угол неориентированный к горизонту. Угол нормируется в диапазоне [0, 2Pi].
    public static double anglHor(double x1, double y1, double x2, double y2) {
        double ang = Math.toDegrees(Angle.angle(new Coordinate(x1, y1), new Coordinate(x2, y2)));
        return (ang > 0) ? 360 - ang : Math.abs(ang);
    }

    //Угол ориентированный к горизонту. Угол нормируется в диапазоне [-Pi, Pi].
    public static double anglHor(ElemSimple e) {
        return Math.toDegrees(Angle.angle(new Coordinate(e.x1(), e.y1()), new Coordinate(e.x2(), e.y2())));
    }

    //Угол реза
    public static double angCut(Coordinate c1, Coordinate c2, Coordinate c3, double delta) {
        LineSegment seg1 = new LineSegment(c1, c2);
        LineSegment seg2 = new LineSegment(c2, c3);
        Coordinate c4 = seg1.offset(delta).lineIntersection(seg2.offset(delta));
        LineSegment seg3 = new LineSegment(c2, c4);
        return Math.toDegrees(Math.asin(delta / seg3.getLength()));
    }

    //Угол неориентированный неомежду профилями
    public static double anglBetbeeem(ElemSimple e1, ElemSimple e2) {

        double c1 = angle(new Coordinate(e1.x2(), e1.y2()), new Coordinate(e1.x1(), e1.y1()));
        double c2 = angle(new Coordinate(e2.x2(), e2.y2()), new Coordinate(e2.x1(), e2.y1()));

        return Math.toDegrees(diff(c1, c2));
    }

    //Обводка полигона, работает быстро. При вырождении полигона загибы на концах арки
    public static Polygon bufferCross(Geometry str, ArrayCom<? extends Com5t> list, double amend) {
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
                    e1 = list.get(coo[i - 1].z);
                    seg1a.setCoordinates(coo[i - 1], coo[i]);
                    seg1b = seg1a.offset(-hm.get(e1.id));
                }
                if (i < Com5t.MAXSIDE || (cross != null && i > Com5t.MAXSIDE)) {
                    int j = (i == coo.length - 1) ? 1 : i + 1;
                    e2 = list.get(coo[i].z);
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
            System.err.println("Ошибка:UGeo.buffeCrossr() " + e);
        }
        return result;
    }

    //Обводка полигона, работает быстро. При вырождении полигона теряются p.z
    public static Polygon bufferUnion(Geometry str, ArrayCom<? extends Com5t> list, double amend) {
        try {
            Map<Double, Double> hm = new HashMap();
            for (Com5t el : list) {
                Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
                hm.put(el.id, rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr) + amend);
            }
            List<Geometry> geoList = new ArrayList();
            List<Coordinate> cooList = new ArrayList<Coordinate>(), arcList = new ArrayList<Coordinate>();

            Coordinate[] coo = str.getCoordinates();
            for (int i = 1; i < coo.length; i++) {

                Com5t e1 = list.get(coo[i - 1].z);
                Com5t e2 = list.get(coo[i].z);
                LineSegment seg1a = new LineSegm(coo[i - 1], coo[i], coo[i - 1].z);
                LineSegment seg1b = seg1a.offset(-hm.get(e1.id));

                if (e1.h() != null && e2.h() != null) {
                    LineSegment seg2a = new LineSegm(coo[i], coo[i + 1], coo[i].z);
                    LineSegment seg2b = seg2a.offset(-hm.get(e2.id));
                    Coordinate cross = seg2b.intersection(seg1b);
                    cooList.add(seg2a.p0);
                    arcList.add(cross);

                } else if (e1.h() == null) {
                    Polygon ls = gf.createPolygon(new Coordinate[]{seg1a.p0, seg1a.p1, seg1b.p1, seg1b.p0, seg1a.p0});
                    geoList.add(ls);
                }
            }
            Geometry arc = gf.createLineString();
            if (arcList.isEmpty() == false) {
                Collections.reverse(arcList);
                cooList.addAll(arcList);
                cooList.add(cooList.get(0));
                arc = gf.createPolygon(cooList.toArray(new Coordinate[0]));
            }
            GeometryCollection partsGeom = gf.createGeometryCollection(GeometryFactory.toGeometryArray(geoList));
            Geometry buffer = partsGeom.union().union(arc);

            LinearRing ring = ((Polygon) buffer).getInteriorRingN(0);
            Coordinate coord[] = ring.getCoordinates();
            for (int i = 0; i < coord.length; i++) {
                coord[i].z = coo[i].z;
            }
            return gf.createPolygon(coord);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.bufferUnion() " + e);
            return gf.createPolygon();
        }
    }

    //При вырождении полигона загибы на концах арки
    public static Polygon bufferPaddin(Geometry poly, ArrayCom<? extends Com5t> list, double amend) {
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
                Com5t e1 = list.get(segm1.p0.z), e2 = list.get(segm2.p0.z);
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
    public static Geometry[] geoSplit(Geometry geom, ElemCross impost) {
        try {
            Geometry poly = geom.getGeometryN(0);
            HashSet<Coordinate> hsCheck = new HashSet<Coordinate>();
            Coordinate[] coo = poly.copy().getCoordinates();
            LineSegment imp = new LineSegment(new Coordinate(impost.x1(), impost.y1()), new Coordinate(impost.x2(), impost.y2()));
            imp.normalize();
            List<Coordinate> cooL = new ArrayList<Coordinate>(), cooR = new ArrayList<Coordinate>();
            List<Coordinate> crosP = new ArrayList<Coordinate>(), exten = new ArrayList<Coordinate>(List.of(coo[0]));

            //Вставим точки пересечения в список координат
            for (int i = 1; i < coo.length; i++) {

                //Точка пересечения сегмента и линии
                Coordinate segmP0 = coo[i - 1], segmP1 = coo[i];
                Coordinate crosC = Intersection.lineSegment(imp.p0, imp.p1, segmP0, segmP1);
                hsCheck.add(coo[i]);

                //Вставим точки
                if (crosC != null) {
                    crosP.add(crosC);
                    if (hsCheck.add(crosC)) {
                        exten.add(crosC);
                    }
                }
                exten.add(coo[i]);
            }
            //Обход сегментов до и после точек пересечения
            boolean b = true;
            for (int i = 0; i < exten.size(); ++i) {
                Coordinate c = exten.get(i);
                if (Double.isNaN(c.z)) {
                    b = !b;
                    Coordinate cL = new Coordinate(c.x, c.y);
                    Coordinate cR = new Coordinate(c.x, c.y);
                    if (crosP.get(0).equals(c)) {

                        cL.z = impost.id;
                        cR.z = exten.get(i - 1).z;
                    } else {
                        cL.z = exten.get(i - 1).z;
                        cR.z = impost.id;
                    }
                    cooL.add(cL);
                    cooR.add(cR);

                } else { //обход координат
                    if (b == true) { //ареа слева
                        cooL.add(c);
                    } else { //ареа справа
                        cooR.add(c);
                    }
                }
            }
            if (impost.y1() != impost.y2()) {
                Collections.rotate(cooR, 1);
                cooR.add(cooR.get(0));
            } else {
                cooR.add(cooR.get(0));
            }

            Geometry p0 = Com5t.gf.createLineString(crosP.toArray(new Coordinate[0]));
            Geometry p1 = Com5t.gf.createPolygon(cooL.toArray(new Coordinate[0]));
            Geometry p2 = Com5t.gf.createPolygon(cooR.toArray(new Coordinate[0]));
            p1.normalize();
            p2.normalize();
            return new Geometry[]{p0, p1, p2};

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.geoSplit()" + e);
            return null;
        }
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
            Coordinate lm[] = Arrays.copyOf(ls.getCoordinates(), ls.getCoordinates().length - 1);
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

    public static int getIndex(Geometry p, double id) {
        Coordinate coo[] = p.getGeometryN(0).getCoordinates();
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

    public static LineSegment normalize(LineSegment segm) {
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
// </editor-fold>    
}
