package builder.model;

import domain.eArtikl;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.Distance;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.operation.polygonize.Polygonizer;

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
    public static double[] diffOnAngl(double anglHoriz, double h) {

        double x = Math.sin(Math.toRadians(anglHoriz));
        double y = Math.cos(Math.toRadians(anglHoriz));
        return new double[]{x * h, y * h};
    }

    public static double anglHor(ElemSimple e) {
        return Math.toDegrees(Angle.angle(new Coordinate(e.x1(), e.y1()), new Coordinate(e.x2(), e.y2())));
    }

    public static boolean pointOnLine(double x, double y, double x1, double y1, double x2, double y2) {
        //return (((x2 - x1) * (y - y1)) - ((y2 - y1) * (x - x1)) == 0);
        double d = Math.abs(((x2 - x1) * (y - y1)) - ((y2 - y1) * (x - x1)));
        boolean b = d < 0.1;
        return b;
    }

    public static boolean pointOnLine2(double x, double y, double x1, double y1, double x2, double y2) {
        Coordinate p0 = new Coordinate(x, y);
        Coordinate p1 = new Coordinate(x1, y1);
        Coordinate p2 = new Coordinate(x2, y2);
        double dist = Distance.pointToSegment(p0, p1, p2);
        return Math.abs(dist) < 0.1;
    }

    public static ElemSimple elemFromSegment(List<ElemSimple> listLine, LineSegment line) {
        for (ElemSimple elem : listLine) {
            if (UJts.pointOnLine(line.p0.x, line.p0.y, elem.x1(), elem.y1(), elem.x2(), elem.y2())
                    && UJts.pointOnLine(line.p1.x, line.p1.y, elem.x1(), elem.y1(), elem.x2(), elem.y2())) {
                return elem;
            }
        }
        return null;
    }

    //Точка пересечения двух линий 
    //https://habr.com/ru/articles/523440/ 
    public static double[] crossLine(double x1, double y1,
            double x2, double y2, double x3, double y3, double x4, double y4) {
        try {
            double n;
            double dot[] = {0, 0};
            if (y2 - y1 != 0) {  // a(y)
                double q = (x2 - x1) / (y1 - y2);
                double sn = (x3 - x4) + (y3 - y4) * q;
                if (sn == 0) {
                    System.err.println("Ошибка: UJts.crossLine() 1");
                    return null;
                }  // c(x) + c(y)*q

                double fn = (x3 - x1) + (y3 - y1) * q;   // b(x) + b(y)*q
                n = fn / sn;
            } else {
                if ((y3 - y4) == 0) {
                    System.err.println("Ошибка: UJts.crossLine() 2");
                    return null;
                }  // b(y)

                n = (y3 - y1) / (y3 - y4);   // c(y)/b(y)
            }
            dot[0] = x3 + (x4 - x3) * n;  // x3 + (-b(x))*n
            dot[1] = y3 + (y4 - y3) * n;  // y3 +(-b(y))*n
            return dot;
        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.crossLine()" + e);
        }
        return null;
    }

    public static Coordinate[] intersectPoligon(Polygon poly, double x1, double y1, double x2, double y2) {
        try {
            List<Coordinate> out = new ArrayList();
            Coordinate imp1 = new Coordinate(x1, y1);
            Coordinate imp2 = new Coordinate(x2, y2);
            Coordinate[] c = poly.getCoordinates();
            for (int i = 1; i < c.length; i++) {

                Coordinate segm1 = c[i - 1];
                Coordinate segm2 = c[i];
                Coordinate c3 = Intersection.lineSegment(imp1, imp2, segm1, segm2);
                if (c3 != null) {
                    out.add(c3);
                }
            }
            return out.toArray(new Coordinate[0]);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.crossPoly()" + e);
        }
        return null;
    }

    public static Geometry[] splitCanvas(double x1, double y1, double x2, double y2, double w, double h) {

        Coordinate l1 = new Coordinate(x1, y1), l2 = new Coordinate(x2, y2);
        Coordinate[] p = {new Coordinate(0, 0), new Coordinate(0, h), new Coordinate(w, h), new Coordinate(w, 0), new Coordinate(0, 0)};

        List<Coordinate> poly = new ArrayList(), cros = new ArrayList();
        List<Coordinate> rect = new ArrayList(List.of(new Coordinate(0, 0)));

        for (int i = 1; i < p.length; i++) {
            Coordinate s1 = p[i - 1], s2 = p[i];
            Coordinate c3 = Intersection.lineSegment(l1, l2, s1, s2);
            if (c3 != null) {
                rect.add(c3);
                cros.add(c3);
            }
            rect.add(p[i]);
        }
        boolean b = true;
        for (Coordinate c : rect) {
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
        Geometry p2 = Com5t.gf.createPolygon(p).difference(p1);
        
        return new Geometry[] {p0, p1, p2};
    }

    //Внутренняя обводка ареа 
    public static Polygon areaPadding(Polygon poly, List<ElemSimple> listFrame) {

        Coordinate[] c = poly.getCoordinates();
        Coordinate[] c2 = new Coordinate[c.length];
        try {
            for (int i = 0; i < c.length; i++) {

                int j = (i == c.length - 1) ? 1 : i + 1;
                int k = (i == 0 || i == c.length - 1) ? c.length - 2 : i - 1;
                LineSegment segm1 = new LineSegment(c[i], c[j]);
                LineSegment segm2 = new LineSegment(c[k], c[i]);
                ElemSimple e1 = UJts.elemFromSegment(listFrame, segm1);
                ElemSimple e2 = UJts.elemFromSegment(listFrame, segm2);

                //Получим ширину сегментов в цыкле
                double h1[] = UJts.diffOnAngl(UJts.anglHor(e1), e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr));
                double h2[] = UJts.diffOnAngl(UJts.anglHor(e2), e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr));

                //Смещённая внутрь точка пересечения сегментов
                LineSegment segm3 = new LineSegment(e1.x1() + h1[0], e1.y1() - h1[1], e1.x2() + h1[0], e1.y2() - h1[1]);
                LineSegment segm4 = new LineSegment(e2.x1() + h2[0], e2.y1() - h2[1], e2.x2() + h2[0], e2.y2() - h2[1]);

                c2[i] = segm3.lineIntersection(segm4);
            }

            return Com5t.gf.createPolygon(c2);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.areaPadding()" + e);
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
        return Com5t.gf.createPolygon(UJts.arrCoord(d));
    }

// <editor-fold defaultstate="collapsed" desc="XLAM">
    //Разделить произвольный многоугольник линией
    //https://gis.stackexchange.com/questions/189976/jts-split-arbitrary-polygon-by-a-line    
    public static Geometry splitPolygon(Geometry poly, double x1, double y1, double x2, double y2) {
        try {
            Geometry line = Com5t.gf.createLineString(new Coordinate[]{new Coordinate(x1, y1), new Coordinate(x2, y2)});
            Geometry nodedLinework = poly.getBoundary().union(line);
            Geometry polys = polygonize(nodedLinework);

            List output = new ArrayList();
            for (int i = 0; i < polys.getNumGeometries(); i++) {
                Polygon candpoly = (Polygon) polys.getGeometryN(i);
                if (poly.contains(candpoly.getInteriorPoint())) {
                    output.add(candpoly);
                }
            }
            return poly.getFactory().createGeometryCollection(GeometryFactory.toGeometryArray(output));

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.splitPolygon()" + e);
        }
        return null;
    }

    public static Geometry polygonize(Geometry geometry) {

        List lines = LineStringExtracter.getLines(geometry);
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(lines);
        Collection polys = polygonizer.getPolygons();
        Polygon[] polyArray = GeometryFactory.toPolygonArray(polys);
        return geometry.getFactory().createGeometryCollection(polyArray);
    }

    public static Coordinate[] expImpost(double x1, double y1, double x2, double y2, double w, double h) {
        try {
            List<Coordinate> list = new ArrayList();
            LineSegment segm = new LineSegment();
            LineSegment imp = new LineSegment(x1, y1, x2, y2);

            segm.setCoordinates(new Coordinate(0, 0), new Coordinate(0, h));
            Coordinate c1 = imp.lineIntersection(segm);
            if (c1 != null && c1.y >= 0 && c1.y < h) {
                list.add(c1);
            }
            segm.setCoordinates(new Coordinate(0, h), new Coordinate(w, h));
            Coordinate c2 = imp.lineIntersection(segm);
            if (c2 != null && c2.x >= 0 && c2.x < w) {
                list.add(c2);
            }
            segm.setCoordinates(new Coordinate(w, h), new Coordinate(w, 0));
            Coordinate c3 = imp.lineIntersection(segm);
            if (c3 != null && c3.y <= h && c3.y > 0) {
                list.add(c3);
            }
            segm.setCoordinates(new Coordinate(w, 0), new Coordinate(0, 0));
            Coordinate c4 = imp.lineIntersection(segm);
            if (c4 != null && c4.x <= w && c4.x > 0) {
                list.add(c4);
            }
            if (list.size() != 2) {
                System.out.println("Ошибка+++++++++++++++++++");
            }
            return list.toArray(new Coordinate[0]);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.splitCanvas()" + e);
            return null;
        }
    }

// </editor-fold>    
}
