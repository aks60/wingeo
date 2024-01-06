package builder.model;

import common.LinkedCom;
import domain.eArtikl;
import enums.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Утилиты JTS
 */
public class UGeo {

    //Угол к горизонту
    public static double anglHor(ElemSimple e) {
        return Math.toDegrees(Angle.angle(new Coordinate(e.x1(), e.y1()), new Coordinate(e.x2(), e.y2())));
    }

    //Отображение сегмента на элемент конструкции
    public static ElemSimple segMapElem(LinkedCom<ElemSimple> elems, LineSegment segm) {
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
    public static Geometry[] geoSplit(Geometry geo, ElemCross elem) {
        try {
            HashSet<Coordinate> hsCheck = new HashSet(), hsPco = new HashSet();
            Coordinate[] coo = geo.getCoordinates();
            Coordinate crosP1 = new Coordinate(elem.x1(), elem.y1()), crosP2 = new Coordinate(elem.x2(), elem.y2());
            List<Coordinate> cooL = new ArrayList(), cooR = new ArrayList(), ext = new ArrayList(List.of(coo[0]));
            for (int i = 1; i < coo.length; i++) {

                //Точка пересечения сегмента и линии
                Coordinate segmP1 = coo[i - 1], segmP2 = coo[i];
                Coordinate pco = Intersection.lineSegment(crosP1, crosP2, segmP1, segmP2);
                
                hsCheck.add(coo[i]);
                if (pco != null) {
                    
                    pco.setZ(coo[i - 1].z);
                    if (elem.x1() == pco.x && elem.y1() == pco.y) {
                        pco.setZ(elem.id); //точку привяжем к импосту
                    }
                    if (hsCheck.add(pco)) {
                        ext.add(pco);
                    }
                    hsPco.add(pco);
                }
                ext.add(coo[i]);
            }

            //Обход сегментов до точек пересечения
            boolean b = true;
            List<Coordinate> list = new ArrayList(hsPco);
            for (Coordinate c : ext) {
                if (list.get(0).equals(c) || list.get(1).equals(c)) {
                    b = !b;
                    cooL.add(c);
                    cooR.add(c);
                } else {
                    if (b == true) {
                        cooL.add(c);
                    } else {
                        cooR.add(c);
                    }
                }
            }
            if (elem.y1() != elem.y2()) {
                Collections.rotate(cooR, 1);
                cooR.add(cooR.get(0));
            } else {
                cooR.add(cooR.get(0));
            }
            Geometry p0 = Com5t.gf.createLineString(hsPco.toArray(new Coordinate[0]));
            Geometry p1 = Com5t.gf.createPolygon(cooL.toArray(new Coordinate[0]));
            Geometry p2 = Com5t.gf.createPolygon(cooR.toArray(new Coordinate[0]));

            PRINT(geo);
            PRINT(p1);
            PRINT(p2);
            
            return new Geometry[]{p0, p1, p2};

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.geoSplit()" + e);
            return null;
        }
    }
    //Внутренняя обводка ареа 

    public static Polygon geoPadding(Geometry poly, LinkedCom<ElemSimple> listElem, double amend) {

        Coordinate[] coo = poly.getCoordinates();
        Coordinate[] out = new Coordinate[coo.length];
        
        LinkedCom<ElemSimple> listFrame = listElem.filter(Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA);
        try {
            for (int i = 0; i < coo.length; i++) {

                //Сегменты границ полигона
                int j = (i == coo.length - 1) ? 1 : i + 1;
                int k = (i == 0 || i == coo.length - 1) ? coo.length - 2 : i - 1;

                LineSegment segm1 = new LineSegment(coo[k], coo[i]);
                LineSegment segm2 = new LineSegment(coo[i], coo[j]);

                //Получим ширину сегментов
                ElemSimple e1 = UGeo.segMapElem(listFrame, segm1); //listElem.find2(coo[k].z); //;
                ElemSimple e2 =  UGeo.segMapElem(listFrame, segm2); //listElem.find2(coo[i].z); // 
                double w1 = (e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr)) + amend;
                double w2 = (e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr)) + amend;

                //Смещение сегментов относительно границ
                LineSegment segm3 = segm1.offset(-w1);
                LineSegment segm4 = segm2.offset(-w2);

                //Точка пересечения внутренних сегментов
                out[i] = segm4.lineIntersection(segm3);
                out[i].z = e2.id;
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

    public static Point newPoint(double x, double y) {
        return Com5t.gf.createPoint(new Coordinate(x, y));
    }

    public static LineString newLineStr(double... d) {
        return Com5t.gf.createLineString(UGeo.arrCoord(d));
    }

    public static LineSegment newLineSegm(double x1, double y1, double x2, double y2) {
        return new LineSegment(x1, y1, x2, y2);
    }

    //Список входн. параметров не замыкается начальной точкой как в jts!
    public static Polygon newPolygon(double... d) {
        return Com5t.gf.createPolygon(UGeo.arrCoord(d));
    }

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

    public static int getIndex(Geometry p, Com5t e) throws Exception {
        Coordinate coo[] = p.getCoordinates();

        for (int i = 0; i < coo.length - 1; i++) {
            if (e.x1() == coo[i].x && e.y1() == coo[i].y && e.x2() == coo[i + 1].x && e.y2() == coo[i + 1].y) {
                return i;
            }
        }
        //throw new Exception("Ошибка:UGeo.getIndex()");
        System.err.println("Ошибка:UGeo.indexPolygon()");
        return -1;
    }

// <editor-fold defaultstate="collapsed" desc="TEMP">    
    public static void PRINT(Geometry g) {
        Coordinate coo[] = g.getCoordinates();
        System.out.println(List.of(coo));
    }
// </editor-fold>    
}
