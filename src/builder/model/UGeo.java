package builder.model;

import common.LinkedCom;
import dataset.Record;
import domain.eArtikl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.locationtech.jts.algorithm.Angle;
import static org.locationtech.jts.algorithm.Angle.angle;
import static org.locationtech.jts.algorithm.Angle.diff;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.util.GeometricShapeFactory;

/**
 * Утилиты JTS
 */
public class UGeo {

    //Угол к горизонту. Угол нормируется в диапазоне [-Pi, Pi].
    public static double anglHor(ElemSimple e) {
        return Math.toDegrees(Angle.angle(new Coordinate(e.x1(), e.y1()), new Coordinate(e.x2(), e.y2())));
    }

    //Угол неориентированный неомежду профилями
    public static double anglBetbeeem(ElemSimple e1, ElemSimple e2) {

        double c1 = angle(new Coordinate(e1.x2(), e1.y2()), new Coordinate(e1.x1(), e1.y1()));
        double c2 = angle(new Coordinate(e2.x2(), e2.y2()), new Coordinate(e2.x1(), e2.y1()));

        return Math.toDegrees(diff(c1, c2));
    }

    //Пересечение сегмента(линии) импоста с сегментами(отрезками) многоугольника
    public static Coordinate[] geoIntersect(Geometry poly, LineSegment line) {
        try {
            poly = poly.getGeometryN(0);
            List<Coordinate> out = new ArrayList();
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
    public static Geometry[] geoSplit(Geometry poly, ElemCross cross) {
        try {
            poly = poly.getGeometryN(0);
            HashSet<Coordinate> hsCheck = new HashSet();
            Coordinate[] coo = poly.copy().getCoordinates();
            Coordinate crosP1 = new Coordinate(cross.x1(), cross.y1());
            Coordinate crosP2 = new Coordinate(cross.x2(), cross.y2());
            List<Coordinate> cooL = new ArrayList(), cooR = new ArrayList();
            List<Coordinate> crosP = new ArrayList(), exten = new ArrayList(List.of(coo[0]));

            //Вставим точки пересецения в список координат
            for (int i = 1; i < coo.length; i++) {

                //Точка пересечения сегмента и линии
                Coordinate segmP1 = coo[i - 1], segmP2 = coo[i];
                Coordinate crosC = Intersection.lineSegment(crosP1, crosP2, segmP1, segmP2);
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

                        cL.z = cross.id;
                        cR.z = exten.get(i - 1).z;
                    } else {
                        cL.z = exten.get(i - 1).z;
                        cR.z = cross.id;
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
            if (cross.y1() != cross.y2()) {
                Collections.rotate(cooR, 1);
                cooR.add(cooR.get(0));
            } else {
                cooR.add(cooR.get(0));
            }
            Geometry p0 = Com5t.gf.createLineString(crosP.toArray(new Coordinate[0]));
            Geometry p1 = Com5t.gf.createPolygon(cooL.toArray(new Coordinate[0]));
            Geometry p2 = Com5t.gf.createPolygon(cooR.toArray(new Coordinate[0]));
            return new Geometry[]{p0, p1, p2};

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.geoSplit()" + e);
            return null;
        }
    }

    //Внутренняя обводка ареа 
    public static Polygon geoPadding(Geometry poly, LinkedCom<ElemSimple> list, double amend) {
        LineSegment segm1, segm2, segm1a, segm2a, segm1b, segm2b, segm1c, segm2c;
        List<Coordinate> out = new ArrayList();
        try {
            poly = poly.getGeometryN(0);
            int j = 999, k = 999;
            Coordinate[] coo = poly.copy().getCoordinates();
            for (int i = 0; i < coo.length; i++) {

                //Сегменты границ полигона
                segm1 = UGeo.newSegment(poly, i - 1);
                segm2 = UGeo.newSegment(poly, i);

                //Получим ширину сегментов             
                ElemSimple e1 = list.find(segm1.p0.z), e2 = list.find(segm2.p0.z);
                Record rec1 = (e1.artiklRec == null) ? eArtikl.virtualRec() : e1.artiklRec;
                Record rec2 = (e2.artiklRec == null) ? eArtikl.virtualRec() : e2.artiklRec;
                double w1 = (rec1.getDbl(eArtikl.height) - rec1.getDbl(eArtikl.size_centr)) - amend;
                double w2 = (rec2.getDbl(eArtikl.height) - rec2.getDbl(eArtikl.size_centr)) - amend;

                //Смещение сегментов относительно границ
                segm1a = segm1.offset(-w1);
                segm2a = segm2.offset(-w2);

                //Точка пересечения внутренних сегментов
                Coordinate cross = (coo.length < 100) ? segm2a.lineIntersection(segm1a) : segm2a.intersection(segm1a);

                if (cross != null && i < j - 1) {
                    cross.z = e2.id;
                    out.add(cross);

                } else { //обрезаем концы арки

                    if (e1.h() != null) { //слева
                        Coordinate cros1 = null;
                        j = i - 1;
                        do {
                            segm1b = UGeo.newSegment(poly, --j);
                            segm1c = segm1b.offset(-w1);
                            cros1 = segm2a.intersection(segm1c);

                        } while (cros1 == null);
                        cros1.z = e2.id;
                        out.add(cros1);
                        j = (j < 0) ? --j + coo.length : --j;

                    }
                    if (e2.h() != null) {  //справа
                        Coordinate cros2 = null;
                        k = i;
                        do {
                            segm2b = UGeo.newSegment(poly, ++k);
                            segm2c = segm2b.offset(-w2);
                            cros2 = segm2c.intersection(segm1a);

                        } while (cros2 == null);
                        i = k;
                        cros2.z = e2.id;
                        out.add(cros2);
                    }
                }
            }
            if (out.get(0).equals(out.get(out.size() - 1)) == false) {
                out.add(out.get(0));
            }
            Polygon g = Com5t.gf.createPolygon(out.toArray(new Coordinate[0]));
            return g;

        } catch (Exception e) {
            System.err.println("AKS Ошибка:UGeo.geoPadding() " + e);
            return null;
        }
    }

    //Список входн. параметров не замыкается начальной точкой как в jts!
    public static Coordinate[] arrCoord(double... d) {
        List<Coordinate> list = new ArrayList();
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
            Com5t.gsf.setBase(new Coordinate(x1 + (x2 - x1) / 2 - R, y - h));
            LineString ls = Com5t.gsf.createArc(Math.PI + angl, Math.PI - 2 * angl).reverse();
            List.of(ls.getCoordinates()).forEach(c -> c.z = z);
            return ls;

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.newLineArch()");
            return null;
        }
    }

    //Список входн. параметров не замыкается начальной точкой как в jts!
    public static Polygon newPolygon(double... d) {
        return Com5t.gf.createPolygon(UGeo.arrCoord(d));
    }

    public static LineSegment newSegment(Geometry poly, int index) {
        poly = poly.getGeometryN(0);
        Coordinate[] coo = Arrays.copyOf(poly.getCoordinates(), poly.getNumPoints() - 1);
        index = (index >= coo.length) ? index - coo.length : index;
        int j = (index < 0) ? index + coo.length : index;
        int k = (j + 1 >= coo.length) ? j + 1 - coo.length : j + 1;

        return new LineSegment(coo[j], coo[k]);
    }

    public static int getIndex(Geometry p, Com5t e) throws Exception {
        Coordinate coo[] = p.getGeometryN(0).getCoordinates();
//        if (coo.length > 5) {
//            System.out.println("AKS builder.model.UGeo.getIndex()");
//        } else {
        for (int i = 0; i < coo.length - 1; i++) {
            //if (e.x1() == coo[i].x && e.y1() == coo[i].y && e.x2() == coo[i + 1].x && e.y2() == coo[i + 1].y) {
            if (coo[i].z == e.id) {
                return i;
            }
        }
//        }
        //throw new Exception("Ошибка:UGeo.getIndex()");
        System.err.println("Ошибка:UGeo.getIndex()");
        return -1;
    }

// <editor-fold defaultstate="collapsed" desc="TEMP">   
    //Угол к горизонту. Угол нормируется в диапазоне [-Pi, Pi].
    //@deprecated
    public static double anglHor(Coordinate p0, Coordinate p1) {
        return Math.toDegrees(Angle.angle(p0, p1));
    }  
    
    //@deprecated
    public static LineSegment getSegment(Geometry p, int mid, int step) {

        Coordinate[] coo = p.getCoordinates();
        int i = mid + coo.length - 1;
        List<Coordinate> list = new ArrayList(List.of(coo));
        list.addAll(List.of(Arrays.copyOfRange(coo, 1, coo.length)));
        list.addAll(List.of(Arrays.copyOfRange(coo, 1, coo.length)));

        if (step == 0) {
            return new LineSegment(list.get(i), list.get(i + 1));
        } else if (step == -1) {
            return new LineSegment(list.get(i - 1), list.get(i));
        } else if (step == 1) {
            return new LineSegment(list.get(i + 1), list.get(i + 2));
        }
        return null;
    }
// </editor-fold>    
}
