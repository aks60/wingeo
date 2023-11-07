package builder.model;

import domain.eArtikl;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.algorithm.LineIntersector;
import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

/**
 * GeometryFixer - Исправляет геометрию LineStringExtracter.getLines(geometry) -
 * Извлекает LineString
 *
 */
public class UJts {

    public static double sin(double angl) {
        return Math.toDegrees(Math.sin(Math.toRadians(angl)));
    }

    public static double cos(double angl) {
        return Math.toDegrees(Math.cos(Math.toRadians(angl)));
    }

    //Ширина рамки по оси x и y
    public static double[] deltaOnAngl(double anglHoriz, double h) {
        double x = Math.sin(Math.toRadians(anglHoriz));
        double y = Math.cos(Math.toRadians(anglHoriz));
        return new double[]{x * h, y * h};
    }

    public static double anglHor(ElemSimple e) {
        return Math.toDegrees(Angle.angle(new Coordinate(e.x1(), e.y1()), new Coordinate(e.x2(), e.y2())));
    }

    //Отображение сегмента на компонент
    public static ElemSimple segMapElem(List<ElemSimple> listLine, LineSegment segm) {
        try {
            Coordinate p = segm.midPoint();
            LineSegment s = new LineSegment();
            for (ElemSimple elem : listLine) {

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
    public static Coordinate[] intersectPoligon(Polygon poly, LineSegment line1, LineSegment line2) {
        try {
            Coordinate c2 = null;
            List<Coordinate> out1 = new ArrayList();
            List<Coordinate> out2 = new ArrayList();
            Coordinate[] c = poly.getCoordinates();
            for (int i = 1; i < c.length; i++) {

                Coordinate seg1 = c[i - 1];
                Coordinate seg2 = c[i];
                Coordinate c3 = Intersection.lineSegment(line1.p0, line1.p1, seg1, seg2);
                if (c3 != null) {
                    out1.add(c3);
                }
                Coordinate c4 = Intersection.lineSegment(line2.p0, line2.p1, seg1, seg2);
                if (c4 != null) {
                    out2.add(c4);
                }
            }
            c2 = Intersection.lineSegment(out1.get(0), out1.get(1), out2.get(0), out2.get(1));
            if (c2 != null) {
                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            }
            return out1.toArray(new Coordinate[0]);

        } catch (Exception e) {
            System.err.println("intersectPoligon" + e);
        }
        return null;
    }

    public static Coordinate[] intersectPoligon(Polygon poly, LineSegment segm) {
        try {
            List<Coordinate> out = new ArrayList();
            Coordinate[] c = poly.getCoordinates();
            for (int i = 1; i < c.length; i++) {

                Coordinate segm1 = c[i - 1];
                Coordinate segm2 = c[i];
                Coordinate c3 = Intersection.lineSegment(segm.p0, segm.p1, segm1, segm2);
                if (c3 != null) {
                    out.add(c3);
                }
            }
            return out.toArray(new Coordinate[0]);

        } catch (Exception e) {
            System.err.println("intersectPoligon" + e);
        }
        return null;
    }

    //Пилим многоугольник
    public static Geometry[] splitPolygon(Geometry geo, double x1, double y1, double x2, double y2) {

        Coordinate[] coo = geo.getCoordinates();
        Coordinate linePoint1 = new Coordinate(x1, y1), linePoint2 = new Coordinate(x2, y2);
        List<Coordinate> poly = new ArrayList(), cros = new ArrayList();
        List<Coordinate> exp = new ArrayList(List.of(coo[0]));
        try {
            for (int i = 1; i < coo.length; i++) {
                Coordinate segmPoint1 = coo[i - 1], segmentPoint2 = coo[i];
                Coordinate c3 = Intersection.lineSegment(
                        linePoint1, linePoint2, segmPoint1, segmentPoint2);
                if (c3 != null) {
                    exp.add(c3);
                    cros.add(c3);
                }
                exp.add(coo[i]);
            }
            boolean b = true;
            for (Coordinate c : exp) {
                if (b == true) {
                    poly.add(c);
                }
                if (cros.contains(c)) {
                    if (b == false) {
                        poly.add(c);
                    }
                    b = !b;
                }
            }
            Geometry p0 = Com5t.gf.createLineString(cros.toArray(new Coordinate[0]));
            Geometry p1 = Com5t.gf.createPolygon(poly.toArray(new Coordinate[0]));
            Geometry p2 = Com5t.gf.createPolygon(coo).difference(p1);
            return new Geometry[]{p0, p1, p2};

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.splitPolygon()" + e);
            return null;
        }
    }

    //Внутренняя обводка ареа 
    public static Polygon areaPadding2(Polygon poly, List<ElemSimple> listFrame) {

        List list = new ArrayList();
        Coordinate[] coo = poly.getCoordinates();
        Coordinate[] out = new Coordinate[coo.length];
        out[0] = coo[0];
        LineIntersector robus = new RobustLineIntersector();
        try {
            for (int i = 1; i < coo.length; i++) {

                //int j = (i == coo.length - 1) ? 0 : i;
                LineSegment segm1 = new LineSegment(coo[i - 1], coo[i]);
                LineSegment segm2 = new LineSegment(coo[i], coo[i + 1]);
                ElemSimple e1 = UJts.segMapElem(listFrame, segm1);
                ElemSimple e2 = UJts.segMapElem(listFrame, segm2);

                //Получим ширину сегментов
                double w1[] = UJts.deltaOnAngl(UJts.anglHor(e1), e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr));
                double w2[] = UJts.deltaOnAngl(UJts.anglHor(e2), e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr));

                //Смещённая внутрь точка пересечения сегментов
                LineSegment segm1a = new LineSegment(e1.x1() + w1[0], e1.y1() - w1[1], e1.x2() + w1[0], e1.y2() - w1[1]);
                LineSegment segm2a = new LineSegment(e2.x1() + w2[0], e2.y1() - w2[1], e2.x2() + w2[0], e2.y2() - w2[1]);

                //Точка пересечения внутренних сегментов
                out[i] = segm1a.lineIntersection(segm2a);
            }
            return Com5t.gf.createPolygon(out);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.areaPadding()" + e);
            return null;
        }
    }

    public static Polygon areaPadding(Polygon poly, List<ElemSimple> listFrame) {

        Coordinate[] coo = poly.getCoordinates();
        Coordinate[] out = new Coordinate[coo.length];
        try {
            for (int i = 0; i < coo.length; i++) {

                int j = (i == coo.length - 1) ? 1 : i + 1;
                int k = (i == 0 || i == coo.length - 1) ? coo.length - 2 : i - 1;
                LineSegment segm1 = new LineSegment(coo[i], coo[j]);
                LineSegment segm2 = new LineSegment(coo[k], coo[i]);
                ElemSimple e1 = UJts.segMapElem(listFrame, segm1);
                ElemSimple e2 = UJts.segMapElem(listFrame, segm2);

                //Получим ширину сегментов
                double w1[] = UJts.deltaOnAngl(UJts.anglHor(e1), e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr));
                double w2[] = UJts.deltaOnAngl(UJts.anglHor(e2), e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr));

                //Смещённая внутрь точка пересечения сегментов
                LineSegment segm1a = new LineSegment(e1.x1() + w1[0], e1.y1() - w1[1], e1.x2() + w1[0], e1.y2() - w1[1]);
                LineSegment segm2a = new LineSegment(e2.x1() + w2[0], e2.y1() - w2[1], e2.x2() + w2[0], e2.y2() - w2[1]);

                //Точка пересечения внутренних сегментов
                out[i] = segm1a.lineIntersection(segm2a);
            }
            return Com5t.gf.createPolygon(out);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.areaPadding()" + e);
            return null;
        }
    }

    //Вырождение полигона
    public static Polygon areaReduc(Polygon poly) {
        Coordinate[] coo = poly.getCoordinates();
        Coordinate[] out = new Coordinate[coo.length];
        try {
            List<Coordinate> list = new ArrayList(List.of(out[0]));
            for (int i = 1; i < out.length; i++) {
                LineSegment ls = new LineSegment(out[i - 1], out[i]);
                if (ls.getLength() > 1) {
                    list.add(out[i]);
                }
            }
            return Com5t.gf.createPolygon(list.toArray(new Coordinate[0]));
        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.areaReduc()" + e);
        }
        return null;
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
        return Com5t.gf.createPolygon(UJts.arrCoord(d));
    }

// <editor-fold defaultstate="collapsed" desc="XLAM">
// </editor-fold>    
}
