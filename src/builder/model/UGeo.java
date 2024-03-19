package builder.model;

import static builder.model.Com5t.gf;
import common.ArrayCom;
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
import org.locationtech.jts.algorithm.PointLocation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.util.AffineTransformation;
import dataset.Field;
import java.util.HashMap;
import java.util.Map;
import org.locationtech.jts.geom.LinearRing;

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
    public static double anglHoriz(double x1, double y1, double x2, double y2) {
        double ang = Math.toDegrees(Angle.angle(new Coordinate(x1, y1), new Coordinate(x2, y2)));
        return (ang > 0) ? 360 - ang : Math.abs(ang);
    }

    //Угол ориентированный к горизонту. Угол нормируется в диапазоне [-Pi, Pi].
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
    public static Geometry[] geoSplit(Geometry poly, ElemCross impost) {
        try {
            Geometry poly2 = poly.getGeometryN(0);
            HashSet<Coordinate> hsCheck = new HashSet<Coordinate>();
            Coordinate[] coo = poly2.copy().getCoordinates();
            LineSegment imp = new LineSegment(new Coordinate(impost.x1(), impost.y1()), new Coordinate(impost.x2(), impost.y2()));
            imp.normalize();
            List<Coordinate> cooL = new ArrayList<Coordinate>(), cooR = new ArrayList<Coordinate>();
            List<Coordinate> crosP = new ArrayList<Coordinate>(), exten = new ArrayList<Coordinate>(List.of(coo[0]));

            //Вставим точки пересецения в список координат
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
    //Внутренняя обводка горизонтальной ареа 
    public static Polygon geoBuffer(Geometry geom, ArrayCom<? extends Com5t> list, double amend) {

        double arcID = 4;
        List<Geometry> geoList = new ArrayList();
        LineSegment segm1, segm2, segm1a = null, segm2a = null;
        List<Coordinate> coo1List = new ArrayList<Coordinate>(), coo2List = new ArrayList();;
        try {
            Coordinate[] coo = geom.getCoordinates();
            for (int i = 1; i < coo.length; i++) {
                segm1 = UGeo.getSegment(geom, i - 1);
                segm1a = segm1.offset(-amend);

                if (coo[i].z == arcID && coo[i - 1].z == arcID) {
                    coo1List.add(segm1.p0);
                    segm2 = UGeo.getSegment(geom, i);
                    segm2a = segm2.offset(-amend);
                    Coordinate cross = segm2a.intersection(segm1a);
                    cross.z = arcID;
                    coo2List.add(cross);

                } else if (coo[i - 1].z != arcID) {
                    segm1a.p0.z = arcID;
                    segm1a.p1.z = arcID;
                    Polygon ls = gf.createPolygon(new Coordinate[]{segm1.p0, segm1.p1, segm1a.p1, segm1a.p0, segm1.p0});
                    geoList.add(ls);
                }
            }
            Collections.reverse(coo2List);
            coo1List.addAll(coo2List);
            coo1List.add(coo1List.get(0));
            Geometry geo2 = gf.createPolygon(coo1List.toArray(new Coordinate[0]));

            for (Geometry g : geoList) {
                geo2 = geo2.union(g);
            }
            LinearRing ring = ((Polygon) geo2).getInteriorRingN(0);
            Polygon poly = (Polygon) gf.createPolygon(ring);
            return poly;

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.geoPadding() " + e);
            return null;
        }
    }
    
    public static Polygon geoBuffer2(Geometry geom, ArrayCom<? extends Com5t> list, double amend) {

        double arcID = 4;
        List<Geometry> geoList = new ArrayList();
        LineSegment segm1, segm2, segm1a = null, segm2a = null;
        List<Coordinate> coo1List = new ArrayList<Coordinate>(), coo2List = new ArrayList();;
        try {
            Coordinate[] coo = geom.getCoordinates();
            for (int i = 1; i < coo.length; i++) {
                segm1 = UGeo.getSegment(geom, i - 1);
                segm1a = segm1.offset(-amend);

                if (coo[i].z == arcID && coo[i - 1].z == arcID) {
                    coo1List.add(segm1.p0);
                    segm2 = UGeo.getSegment(geom, i);
                    segm2a = segm2.offset(-amend);
                    Coordinate cross = segm2a.intersection(segm1a);
                    cross.z = arcID;
                    coo2List.add(cross);

                } else if (coo[i - 1].z != arcID) {
                    segm1a.p0.z = arcID;
                    segm1a.p1.z = arcID;
                    Polygon ls = gf.createPolygon(new Coordinate[]{segm1.p0, segm1.p1, segm1a.p1, segm1a.p0, segm1.p0});
                    geoList.add(ls);
                }
            }
            Collections.reverse(coo2List);
            coo1List.addAll(coo2List);
            coo1List.add(coo1List.get(0));
            Geometry geo2 = gf.createPolygon(coo1List.toArray(new Coordinate[0]));

            for (Geometry g : geoList) {
                geo2 = geo2.union(g);
            }
            LinearRing ring = ((Polygon) geo2).getInteriorRingN(0);
            Polygon poly = (Polygon) gf.createPolygon(ring);
            return poly;

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.geoPadding() " + e);
            return null;
        }
    }

    public static Polygon geoPadding(Geometry poly, ArrayCom<? extends Com5t> list, double amend) {
        LineSegment segm1, segm2, segm1a = null, segm2a = null, segm1b, segm2b, segm1c, segm2c;
        Coordinate cros1 = null, cros2 = null;
        List<Coordinate> out = new ArrayList<Coordinate>();
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
                        out.add(cross);

                    } else { //обрезаем концы арки

                        if (cros1 == null && e1.h() != null) { //хвост
                            j = i - 1;
                            do {
                                segm1b = UGeo.getSegment(poly, --j);
                                segm1c = segm1b.offset(-w1);
                                cros1 = segm2a.intersection(segm1c);

                            } while (cros1 == null);
                            cros1.z = e2.id;
                            out.add(cros1);
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
                            out.add(cros2);
                        }
                    }
                }
            }
            if (out.get(0).equals(out.get(out.size() - 1)) == false) {
                out.add(out.get(0));
            }
            Polygon g = Com5t.gf.createPolygon(out.toArray(new Coordinate[0]));
            return g;

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.geoPadding() " + e);
            return null;
        }
    }

    public static Map<Double, Double[]> geoOffset2(ArrayCom<ElemSimple> listElem) {
        Map<Double, Double[]> hm = new HashMap();
        for (ElemSimple el : listElem) {
            Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
            hm.put(el.id, new Double[]{rec.getDbl(eArtikl.height), rec.getDbl(eArtikl.size_centr)});
        }
        return hm;
    }

    //@deprecated
    public static LineSegment getSegment(Geometry p, int mid, int step) {

        Coordinate[] coo = p.getCoordinates();
        int i = mid + coo.length - 1;
        List<Coordinate> list = new ArrayList<Coordinate>(List.of(coo));
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

    public static Map<Double, Double[]> geoOffset(ArrayCom<? extends Com5t> listElem, Field... field) {
        Map<Double, Double[]> hm = new HashMap();
        for (Com5t el : listElem) {
            Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
            Double data[] = {.0, .0, .0};
            for (int i = 0; i < field.length; ++i) {
                data[i] = rec.getDbl(field[i]);
            }
            hm.put(el.id, data);
        }
        return hm;
    }

// </editor-fold>    
}
