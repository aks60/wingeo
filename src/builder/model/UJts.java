package builder.model;

import domain.eArtikl;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
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

    public static ArrayList<LineSegment> polyAllSegment(Polygon area) {

        ArrayList<LineSegment> list = new ArrayList();
        Coordinate[] c = area.getCoordinates();
        for (int i = 1; i < c.length; ++i) {
            list.add(new LineSegment(c[i - 1].x, c[i - 1].y, c[i].x, c[i].y));
        }
        return list;
    }

    public static ElemSimple elemFromSegment(List<ElemSimple> listLine, LineString line) {
        for (ElemSimple elem : listLine) {
            if (UGeo.pointOnLine(line.getCoordinateN(0).x, line.getCoordinateN(0).y, elem.x1(), elem.y1(), elem.x2(), elem.y2())
                    && UJts.pointOnLine(line.getCoordinateN(1).x, line.getCoordinateN(1).y, elem.x1(), elem.y1(), elem.x2(), elem.y2())) {
                return elem;
            }
        }
        return null;
    }

    //Точки пересечение импостом Canvas2D. x = (x2 - x1) * (y - y1) / (y2 - y1) + x1   y = (y2 - y1) * (x - x1) / (x2 - x1) + y1
    //https://www.interestprograms.ru/source-codes-tochka-peresecheniya-dvuh-pryamyh-na-ploskosti#uravnenie-v-programmnyj-kod     
    public static double[] crossCanvas(double x1, double y1, double x2, double y2, double width, double height) {
//        double X1 = (y1 == y2) ? 0 : (((0 - y1) / (y2 - y1)) * (x2 - x1)) + x1;
//        double X2 = (y1 == y2) ? w : (((h - y1) / (y2 - y1)) * (x2 - x1)) + x1;
//        //System.out.println(X1 + "  " + 0 + "  =  " + X2 + "  " + h);
//        return new double[]{X1, 0, X2, h};

        double Y1 = (y1 == y2) ? y1 + 0.0000000001 : y1;
        double Y2 = (y1 == y2) ? y2 - 0.0000000001 : y2;
        double X1 = (x2 - x1) * (0 - Y1) / (Y2 - Y1) + x1;
        double X2 = (x2 - x1) * (height - Y1) / (Y2 - Y1) + x1;
        return new double[]{X1, 0, X2, height};
    }

    //Находим предыднщую и последующую линию от совместной между area1 и area2
    /**
     * 0 - сегмент входящий слева 1 - сегмент выходящий слева 2 - общий сегмент
     * 3 - сегмент входящий справа 3 - сегмент выходящий справа
     */
    public static LineSegment[] prevAndNextSegment(Polygon area1, Polygon area2) {

        Coordinate[] c1 = area1.getCoordinates();
        Coordinate[] c2 = area2.getCoordinates();

        //Цикл по сегментам area1
        for (int ik = 1; ik < c1.length; ++ik) {
            //Цикл по сегментам area2
            for (int ij = 1; ij < c2.length; ++ij) {

                LineSegment s1 = new LineSegment(c1[ik - 1].x, c1[ik].y, c1[ik - 1].x, c1[ik].y);
                LineSegment s2 = new LineSegment(c2[ij - 1].x, c2[ij].y, c2[ij - 1].x, c2[ij].y);
                if (s1.equalsTopo(s2)) { //Если сегмент area1 и area2 общий

                    //Находим предыдущий и последующий сегмент
                    int k1 = (ik == 1) ? c1.length - 2 : ik - 2;
                    int j1 = (ik == (c1.length - 2)) ? 1 : ik + 2;

                    int k2 = (ij == 0) ? c2.length - 1 : ij - 1;
                    int j2 = (ij == (c2.length - 1)) ? 0 : ij + 1;

                    return new LineSegment[]{
                        new LineSegment(c1[k1 - 1].x, c1[k1 - 1].y, c1[k1].x, c1[k1].y),
                        new LineSegment(c1[j1 - 1].x, c1[j1 - 1].y, c1[j1].x, c1[j1].y),
                        s1,
                        new LineSegment(c2[k1 - 1].x, c2[k1 - 1].y, c2[k1].x, c2[k1].y),
                        new LineSegment(c2[j1 - 1].x, c2[j1 - 1].y, c2[j1].x, c2[j1].y)};
                }
            }
        }
        return null;
    }

    //Внутренняя обводка ареа 
    public static Polygon areaPadding(Polygon area, List<ElemSimple> listFrame) {

        List<Coordinate> listCoord = new ArrayList();
        Coordinate[] coordArray = new Coordinate[area.getNumPoints()];
        try {
            for (int i = 1; i < area.getExteriorRing().getNumPoints(); i++) {
                LineString segm1 = (LineString) area.getGeometryN(i - 1);
                LineString segm2 = (LineString) area.getGeometryN(i);

                ElemSimple e1 = UJts.elemFromSegment(listFrame, segm1);
                ElemSimple e2 = UJts.elemFromSegment(listFrame, segm2);

                if (e1 != null && e2 != null && e1 != e2) {
                    //Получим ширину сегментов в цыкле
                    double h1[] = UGeo.diffOnAngl(UGeo.horizontAngl(e1), e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr));
                    double h2[] = UGeo.diffOnAngl(UGeo.horizontAngl(e2), e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr));
                    double p[] = UGeo.crossOnLine( //смещённая внутрь точка пересечения сегментов
                            e1.x1() + h1[0], e1.y1() + h1[1], e1.x2() + h1[0], e1.y2() + h1[1],
                            e2.x1() + h2[0], e2.y1() + h2[1], e2.x2() + h2[0], e2.y2() + h2[1]);

                    coordArray[i] = new Coordinate(p[0], p[1]);
                }
            }
            return Com5t.gf.createPolygon(coordArray);

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

    public static Coordinate[] arrCoord(double... d) {
        List<Coordinate> list = new ArrayList();
        for (int i = 1; i < d.length; i = i + 2) {
            list.add(new Coordinate(d[i - 1], d[i]));
        } 
        return list.toArray(new Coordinate[0]);
    }
    
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
// </editor-fold>    
}
