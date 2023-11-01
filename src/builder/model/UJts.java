package builder.model;

import domain.eArtikl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public static boolean pointOnLine(double x, double y, double x1, double y1, double x2, double y2) {
        return (((x2 - x1) * (y - y1)) - ((y2 - y1) * (x - x1)) == 0);
        //return (((x2 - x1) * (y - y1)) - ((y2 - y1) * (x - x1)) == 0);
        //double d = ((x2 - x1) * (y - y1)) - ((y2 - y1) * (x - x1));
        //return (d > -.1 && d < .1);
    }

    public static ElemSimple elemFromSegment(List<ElemSimple> listLine, LineSegment line) {
        for (ElemSimple elem : listLine) {
            if (UGeo.pointOnLine(line.p0.x, line.p0.y, elem.x1(), elem.y1(), elem.x2(), elem.y2())
                    && UJts.pointOnLine(line.p1.x, line.p1.y, elem.x1(), elem.y1(), elem.x2(), elem.y2())) {
                return elem;
            }
        }
        return null;
    }

    public static Coordinate[] crossPoly(Polygon poly, double x1, double y1, double x2, double y2) {

        List<Coordinate> out = new ArrayList();
        LineSegment s1 = new LineSegment(x1, y1, x2, y2);
        LineSegment s2 = new LineSegment();

        Coordinate[] c = poly.getCoordinates();
        for (int i = 1; i < c.length; i++) {
            Coordinate c1 = c[i - 1];
            Coordinate c2 = c[i];
            s2.setCoordinates(c1, c2);
            Coordinate c3 = s2.lineIntersection(s1);
            boolean b = Com5t.gf.createLineString(new Coordinate[]{c1, c2}).contains(Com5t.gf.createPoint(c3));
            if (c3 != null && b == true) {
                out.add(c3);
            }
        }
        if (out.size() == 2) {
            return out.toArray(new Coordinate[0]);
        }
        return null;
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
                double h1[] = UGeo.diffOnAngl(UGeo.horizontAngl(e1), e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr));
                double h2[] = UGeo.diffOnAngl(UGeo.horizontAngl(e2), e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr));

                //Смещённая внутрь точка пересечения сегментов
                double p[] = UGeo.crossOnLine(
                        e1.x1() + h1[0], e1.y1() + h1[1], e1.x2() + h1[0], e1.y2() + h1[1],
                        e2.x1() + h2[0], e2.y1() + h2[1], e2.x2() + h2[0], e2.y2() + h2[1]);

                c2[i] = new Coordinate(p[0], p[1]);
            }

            return Com5t.gf.createPolygon(c2);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.areaPadding()" + e);
            return null;
        }
    }

    //Разделить произвольный многоугольник линией
    //https://gis.stackexchange.com/questions/189976/jts-split-arbitrary-polygon-by-a-line    
    public static Geometry splitPolygon(Geometry poly, double x1, double y1, double x2, double y2) {

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

// <editor-fold defaultstate="collapsed" desc="ADD">
    public static Geometry polygonize(Geometry geometry) {

        List lines = LineStringExtracter.getLines(geometry);
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(lines);
        Collection polys = polygonizer.getPolygons();
        Polygon[] polyArray = GeometryFactory.toPolygonArray(polys);
        return geometry.getFactory().createGeometryCollection(polyArray);
    }

    //Находим предыднщую и последующую линию от совместной между area1 и area2
    /**
     * 0 - сегмент входящий слева 1 - сегмент выходящий слева 2 - общий сегмент
     * 3 - сегмент входящий справа 4 - сегмент выходящий справа
     */
    public static LineSegment[] prevAndNextSegmentTest(Polygon area1, Polygon area2) {
//
//        Coordinate[] c1 = area1.getCoordinates();
//        Coordinate[] c2 = area2.getCoordinates();
//
//        //Цикл по сегментам area1
//        for (int ik = 1; ik < c1.length; ++ik) {
//            //Цикл по сегментам area2
//            for (int ij = 1; ij < c2.length; ++ij) {
//
//                LineSegment s1 = new LineSegment(c1[ik - 1].x, c1[ik].y, c1[ik - 1].x, c1[ik].y);
//                LineSegment s2 = new LineSegment(c2[ij - 1].x, c2[ij].y, c2[ij - 1].x, c2[ij].y);
//                if (s1.equalsTopo(s2)) { //Если сегмент area1 и area2 общий
//
//                    //Находим предыдущий и последующий сегмент
//                    int k1 = (ik == 1) ? c1.length - 2 : ik - 2;
//                    int j1 = (ik == (c1.length - 2)) ? 1 : ik + 2;
//
//                    int k2 = (ij == 0) ? c2.length - 1 : ij - 1;
//                    int j2 = (ij == (c2.length - 1)) ? 0 : ij + 1;
//
//                    return new LineSegment[]{
//                        new LineSegment(c1[k1 - 1].x, c1[k1 - 1].y, c1[k1].x, c1[k1].y),
//                        new LineSegment(c1[j1 - 1].x, c1[j1 - 1].y, c1[j1].x, c1[j1].y),
//                        s1,
//                        new LineSegment(c2[k1 - 1].x, c2[k1 - 1].y, c2[k1].x, c2[k1].y),
//                        new LineSegment(c2[j1 - 1].x, c2[j1 - 1].y, c2[j1].x, c2[j1].y)};
//                }
//            }
//        }
        return null;
    }

    public static ArrayList<LineSegment> polyAllSegmentTest(Polygon area) {

        ArrayList<LineSegment> list = new ArrayList();
        Coordinate[] c = area.getCoordinates();
        for (int i = 1; i < c.length; ++i) {
            list.add(new LineSegment(c[i - 1].x, c[i - 1].y, c[i].x, c[i].y));
        }
        return list;
    }

    //параллельную линию для данного сегмента, которая находится выше и ниже исходной
    //https://stackoverflow.com/questions/46319815/how-to-find-a-parallel-line-for-a-given-line-segment-both-the-parallel-line-whi
    public static void test() {
//        // source line from given start and end coordinate
//        LineSegment sourceLine = new LineSegment(startCoordinate, endCoordinate)       
//        // left from start- to end-point (note negative offset distance!)
//        Coordinate startLeft = sourceLine.pointAlongOffset(0, -parallelDistance);
//        Coordinate endLeft = sourceLine.pointAlongOffset(1, -parallelDistance);
//        LineString leftLine = new GeometryFactory().createLineString(new Coordinate[]{startLeft, endLeft});
//        // right from start- to end-point (note positive offset distance!)
//        Coordinate startRight = sourceLine.pointAlongOffset(0, parallelDistance);
//        Coordinate endRight = sourceLine.pointAlongOffset(1, parallelDistance);
//        LineString rightLine = new GeometryFactory().createLineString(new Coordinate[]{startRight, endRight});
    }

// </editor-fold>    
}
