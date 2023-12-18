package builder.model;

import common.LinkedCom;
import domain.eArtikl;
import enums.Type;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

/**
 * Утилиты JTS
 */
public class UGeo {

    //Ширина рамки по оси x и y
    public static double[] deltaOnAngl(double anglHoriz, double h) {
        double x = Math.sin(Math.toRadians(anglHoriz));
        double y = Math.cos(Math.toRadians(anglHoriz));
        return new double[]{x * h, y * h};
    }

    //Угол к горизонту
    public static double anglHor(ElemSimple e) {
        return Math.toDegrees(Angle.angle(new Coordinate(e.x1(), e.y1()), new Coordinate(e.x2(), e.y2())));
    }

    //Отображение сегмента на элемент конструкции
    public static ElemSimple segMapElem(List<ElemSimple> elems, LineSegment segm) {
        try {
            Coordinate p = segm.midPoint(); //средн. точка
            LineSegment s = new LineSegment();
            for (ElemSimple elem : elems) {

                s.setCoordinates(new Coordinate(elem.x1(), elem.y1()), new Coordinate(elem.x2(), elem.y2()));
                if (s.distance(p) < .001) {
                    return elem;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.segMapElem()" + e);
        }
        return null;
    }

    //Пересечение сегмента(линии) импоста с сегментами(отрезками) многоугольника
    public static Coordinate[] geoIntersect(Polygon poly, LineSegment line) {
        try {
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
    public static Geometry[] geoSplit(Geometry geo, double x1, double y1, double x2, double y2) {

        Coordinate[] coo = geo.getCoordinates();
        Coordinate linePoint1 = new Coordinate(x1, y1), linePoint2 = new Coordinate(x2, y2);
        List<Coordinate> po1 = new ArrayList(), po2 = new ArrayList(),
                cros = new ArrayList(), exp = new ArrayList(List.of(coo[0]));
        try {
            for (int i = 1; i < coo.length; i++) {
                Coordinate segmPoint1 = coo[i - 1], segmentPoint2 = coo[i];
                Coordinate c3 = Intersection.lineSegment( //точка пересечения segmPoint и lineSegm
                        linePoint1, linePoint2, segmPoint1, segmentPoint2);
                if (c3 != null) {
                    exp.add(c3);
                    cros.add(c3);
                }
                exp.add(coo[i]);
            }
            boolean b = true;
            for (Coordinate c : exp) {
                if (cros.get(0) == c || cros.get(1) == c) {
                    b = !b;
                    po1.add(c);
                    po2.add(c);
                } else {
                    if (b == true) {
                        po1.add(c);
                    } else {
                        po2.add(c);
                    }
                }
            }
            if (y1 != y2) {
                Collections.rotate(po2, 1);
                po2.add(po2.get(0));
            } else {
                po2.add(po2.get(0));
            }
            Geometry p0 = Com5t.gf.createLineString(cros.toArray(new Coordinate[0]));
            Geometry p1 = Com5t.gf.createPolygon(po1.toArray(new Coordinate[0]));
            Geometry p2 = Com5t.gf.createPolygon(po2.toArray(new Coordinate[0]));
            return new Geometry[]{p0, p1, p2};

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.geoSplit()" + e);
            return null;
        }
    }

    //Внутренняя обводка ареа 
    public static Polygon geoPadding(Polygon poly, LinkedCom<ElemSimple> listElem, double amend) {

        Coordinate[] coo = poly.getCoordinates();
        Coordinate[] out = new Coordinate[coo.length];
        List<ElemSimple> listFrame = listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
        try {
            for (int i = 0; i < coo.length; i++) {

                //Сегменты границ полигона
                int j = (i == coo.length - 1) ? 1 : i + 1;
                int k = (i == 0 || i == coo.length - 1) ? coo.length - 2 : i - 1;

                LineSegment segm1 = new LineSegment(coo[k], coo[i]);
                LineSegment segm2 = new LineSegment(coo[i], coo[j]);

                //Получим ширину сегментов
                ElemSimple e1 = UGeo.segMapElem(listFrame, segm1);
                ElemSimple e2 = UGeo.segMapElem(listFrame, segm2);
                double w1 = (e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr)) + amend;
                double w2 = (e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr)) + amend;

                //Смещение сегментов относительно границ
                LineSegment segm3 = segm1.offset(-w1);
                LineSegment segm4 = segm2.offset(-w2);

                //Точка пересечения внутренних сегментов
                out[i] = segm4.lineIntersection(segm3);
            }
            return Com5t.gf.createPolygon(out);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.geoPadding() " + e);
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

    //Список входн. параметров не замыкается начальной точкой как в jts!
    public static Polygon newPolygon(double... d) {
        return Com5t.gf.createPolygon(UGeo.arrCoord(d));
    }

    public static LineSegment segmPolygon(Polygon p, int i, int step) {
        Coordinate[] coo = p.getCoordinates();
        int imax = p.getNumPoints() - 1;
        int j = i;

        if (i < 0) {
            j = i + imax;

        } else if (i > imax) {
            j = i - imax;
        }
        if (step < 0) {
            int k = (j == 0) ? imax - 1 : j + step;
            return new LineSegment(coo[k], coo[j]);

        } else {
            int k = (j == imax) ? 1 : j + step;
            return new LineSegment(coo[j], coo[k]);
        }
    }

    public static int indexPolygon(Polygon p, Com5t e) throws Exception {
        Coordinate coo[] = p.getCoordinates();

        for (int i = 0; i < coo.length - 1; i++) {
            if (e.x1() == coo[i].x && e.y1() == coo[i].y && e.x2() == coo[i + 1].x && e.y2() == coo[i + 1].y) {
                return i;
            }
        }
        throw new Exception("Ошибка:UGeo.indexPolygon()");
    }
// <editor-fold defaultstate="collapsed" desc="TEMP">    
// </editor-fold>    
}
