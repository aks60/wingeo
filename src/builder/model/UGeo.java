package builder.model;

import builder.Wincalc;
import builder.making.TRecord;
import static builder.model.Com5t.gf;
import common.UCom;
import domain.eArtikl;
import enums.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.algorithm.PointLocation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;
import java.util.Map;
import java.util.Set;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.operation.polygonize.Polygonizer;

/**
 * Утилиты JTS
 */
public class UGeo {

    public static LineSegment segRighShell = new LineSegment(), segRighInner = null;
    public static LineSegment segLeftShell = new LineSegment(), segLeftInner = null;
    public static Coordinate cross = new Coordinate();

    //public static DecimalFormat df = new DecimalFormat("#.###");
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

    //Угол реза
    public static double anglCut(Coordinate tip1, Coordinate tail, Coordinate tip2) {
        double ang = Math.toDegrees(Angle.angleBetween(tip1, tail, tip2));
        ang = (ang > 90) ? 180 - ang : ang;
        ang = Math.round(ang * 100.0) / 100.0;
        return ang;
    }

    //Угол реза
    public static double anglCut(TRecord spcRec, Geometry area, int index1, int index2, char direction) {
        LineSegment s1a = UGeo.getSegment(area, index1);
        LineSegment s2a = UGeo.getSegment(area, index2);
        LineSegment s1b = s1a.offset(-spcRec.height);
        LineSegment s2b = s2a.offset(-spcRec.height);
        Coordinate cross = s1b.intersection(s2b);
        try {
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
        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.anglCut()  " + e);
        }
        double gip = (direction == '-') ? s2a.p0.distance(cross) : s1a.p1.distance(cross);
        return Math.toDegrees(Math.asin(spcRec.height / gip));
    }

    public static Geometry polyCurve(Geometry geoShell, Geometry geoInner, double ID) {

        Coordinate cooShell[] = geoShell.getCoordinates();
        Coordinate cooInner[] = geoInner.getCoordinates();
        List<Coordinate> listFrame = new ArrayList<Coordinate>();

        for (int j = 0; j < cooShell.length; j++) {
            if (cooShell[j].z == ID) {
                listFrame.add(cooShell[j]);
            }
        }
        listFrame.add(cooInner[0]); //посл.точка арки
        for (int k = cooInner.length - 1; k >= 0; k--) {
            if (cooInner[k].z == ID) {
                listFrame.add(cooInner[k]);
            }
        }

        listFrame.add(listFrame.get(0));
        return gf.createPolygon(listFrame.toArray(new Coordinate[0])); //полигон рамы арки
    }

    public static double lengthCurve(Wincalc winc, Geometry geoArea, double id) {

        Coordinate[] coo = geoArea.getCoordinates();
        double width = 0;
        for (int j = 1; j < coo.length; j++) {
            if (coo[j - 1].z == id) {
                width += coo[j - 1].distance(coo[j]);
            }
        }
        return width;
    }

    public static Coordinate prcDbl(Coordinate c) {
        c.x = Math.round(c.x * 10000.0) / 10000.0;
        c.y = Math.round(c.y * 10000.0) / 10000.0;
        return c;
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
    public static Geometry[] splitPolygon(Geometry geom, LineSegment segment) {
        try {
            boolean b = true;
            HashSet<Coordinate> checkHs = new HashSet<Coordinate>();
            Coordinate[] coo = geom.getGeometryN(0).copy().getCoordinates();
            List<Coordinate> cooL = new ArrayList<Coordinate>(), cooR = new ArrayList<Coordinate>();
            List<Coordinate> crosTwo = new ArrayList<Coordinate>(), listExt = new ArrayList<Coordinate>(List.of(coo[0]));
            LineSegment segmImp = normalizeSegm(new LineSegment(
                    new Coordinate(segment.p0.x, segment.p0.y, segment.p0.z),
                    new Coordinate(segment.p1.x, segment.p1.y, segment.p1.z)));

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

    public static List<Geometry> split2Polygon(Wincalc winc, Polygon geom, LineString edge) {
        LineSegment lineSeg = new LineSegment();
        ArrayList<Geometry> outList = new ArrayList<Geometry>();
        try {
            Coordinate[] coo = geom.getCoordinates();
            LinearRing linearRing = geom.getExteriorRing();
            //Союз геометрий
            Geometry union = edge.union(linearRing);
            //Сборка двух полигонов
            Polygonizer polygonizer = new Polygonizer();
            polygonizer.add(union);
            Collection<Polygon> geoms = polygonizer.getPolygons();
            //Выходной список
            geoms.stream().forEach(el -> outList.add(el));
            //Получим array точек пересечения
            LineString lineImp = (LineString) outList.get(0).intersection(outList.get(1));
            ArrayList<Coordinate> crosList = new ArrayList();
            for (int i = 1; i < coo.length; ++i) {
                lineSeg.setCoordinates(coo[i - 1], coo[i]);
                Coordinate cross = lineSeg.intersection(getSegment(lineImp));
                if (cross != null) {
                    crosList.add(coo[i - 1]);
                }
            }
            //Востонавление z координаты
            int trigger = 0;
            Coordinate coo1[] = outList.get(0).getCoordinates();
            for (int i = 0; i < coo1.length - 1; ++i) {
                if (lineImp.getCoordinateN(0).z == coo1[i].z) {
                    System.out.println("TRIGGER1");
                    if (trigger == 1) {
                        coo1[i].z = crosList.get(0).z;
                    }
                    ++trigger;
                }
            }
            trigger = 0;
            Coordinate coo2[] = outList.get(1).getCoordinates();
            for (int i = 0; i < coo2.length - 1; ++i) {
                if (lineImp.getCoordinateN(0).z == coo2[i].z) {
                    if (trigger == 1) {
                        coo2[i].z = crosList.get(1).z;
                    }
                    ++trigger;
                }
            }
            outList.stream().forEach(el -> el.normalize());
            outList.add(lineImp);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.split2Polygon()" + e);
        }
        return outList;
    }

    public static Polygon bufferGeometry(Geometry geoShell, ArrayList<? extends Com5t> listElem, double amend, int opt) {

        Coordinate[] cooShell = geoShell.getCoordinates();
        Map<Double, Double> hmDist = new HashMap();
        try {
            //Смещения сегментов
            for (Com5t el : listElem) {
                dataset.Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
                if (opt == 0) {
                    hmDist.put(el.id, rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr) + amend);
                } else if (opt == 1) {
                    hmDist.put(el.id, rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr) - rec.getDbl(eArtikl.size_falz) + amend);
                }
            }
            if (cooShell.length > Com5t.MAXSIDE) {
                double id = cooShell[geoShell.getCoordinates().length / 2].z;
                Polygon polyCurv = bufferCurve(geoShell, hmDist.get(id));
                Polygon polyRect = bufferRectangl(geoShell, hmDist);
                Polygon polyArch = (Polygon) polyRect.union(polyCurv);

                LinearRing ring = polyArch.getInteriorRingN(0);
                Polygon poly = gf.createPolygon(ring);
                poly.normalize();
                //Test.init(polyRect); 
                updateZet(poly, polyRect);
                return poly;

            } else {
                Polygon poly1 = bufferPolygon(geoShell, hmDist);
                return poly1;
            }
            //Test.init(poly1, poly2); 
        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.buffer() " + e);
        }
        return null;
    }

    //TODO Гадкая функция. Надо переписать!
    //При слиянии двух полигонов появляются точки соединения с непонятным Z значением
    public static void updateZet(Polygon arc, Polygon rec) {
        boolean pass = false;
        Coordinate cooArc[] = arc.getCoordinates();
        Coordinate cooRec[] = rec.getCoordinates();

        for (int i = 0; i < cooArc.length - 1; i++) {
            if (cooArc[i].z % 1 != 0) {
                for (int j = 1; j < cooRec.length; j++) {

                    if (PointLocation.isOnSegment(cooArc[i], cooRec[j - 1], cooRec[j])) {
                        if (pass == false) {

                            cooArc[i].z = cooRec[j].z;
                        } else {
                            cooArc[i].z = cooRec[j - 1].z;
                        }
                        pass = true;
                        break;
                    }
                }
            }
        }
        cooArc[cooArc.length - 1].z = cooArc[0].z;
    }

    public static Polygon bufferRectangl(Geometry geoShell, Map<Double, Double> hmDist) {

        Polygon result = gf.createPolygon();
        Set<Double> set = new HashSet();
        List<Coordinate> listBuffer = new ArrayList<Coordinate>();
        List<Coordinate> listShell = new ArrayList<Coordinate>();
        Coordinate[] cooShell = geoShell.getCoordinates();
        try {
            for (int i = 0; i < cooShell.length; i++) {
                if (set.add(cooShell[i].z)) {
                    listShell.add(cooShell[i]);
                }
            }
            hmDist.put(4.0, 0.0); //такая вот фича!

            for (int i = 0; i < listShell.size(); i++) {

                //Перебор левого и правого сегмента от точки пересечения 
                int j = (i == 0) ? listShell.size() - 1 : i - 1;
                final double id1 = listShell.get(j).z;
                segRighShell.setCoordinates(listShell.get(j), listShell.get(i));
                segRighInner = segmentOffset(segRighShell, -hmDist.get(id1));

                segRighInner.p0.z = segRighShell.p0.z;
                segRighInner.p1.z = segRighShell.p1.z;

                int k = (i == listShell.size() - 1) ? 0 : i + 1;
                final double id2 = listShell.get(i).z;
                segLeftShell.setCoordinates(listShell.get(i), listShell.get(k));
                segLeftInner = segmentOffset(segLeftShell, -hmDist.get(id2));

                //Точка пересечения сегментов
                cross = segLeftInner.lineIntersection(segRighInner);

                if (cross != null) {
                    cross.z = listShell.get(i).z;
                    listBuffer.add(cross);
                }
            }
            Collections.reverse(listBuffer);
            List<Coordinate> listOut = new ArrayList(listShell);
            listOut.addAll(listBuffer);
            listOut.add(listOut.get(0));
            Polygon geoBuffer = gf.createPolygon(listOut.toArray(new Coordinate[0]));

            result = geoBuffer;

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.bufferRectangl() " + e);
        }
        return result;
    }

    public static Polygon bufferCurve(Geometry geoShell, double dist) {

        Polygon result = gf.createPolygon();
        Coordinate[] cooShell = geoShell.getCoordinates();
        double ID = cooShell[cooShell.length / 2].z;

        List<Coordinate> listInner = new ArrayList<Coordinate>();
        List<Coordinate> listShell = List.of(cooShell).stream().filter(c -> c.z == ID).toList();

        try {
            for (int i = 1; i < listShell.size(); i++) {

                //Перебор левого и правого сегмента от точки пересечения
                if (i > Com5t.MAXSIDE || (cross != null && i < Com5t.MAXSIDE)) {
                    segRighShell.setCoordinates(listShell.get(i - 1), listShell.get(i));
                    segRighInner = segmentOffset(segRighShell, -dist);
                }
                if (i < Com5t.MAXSIDE || (cross != null && i > Com5t.MAXSIDE)) {
                    int j = (i == listShell.size() - 1) ? 1 : i + 1;
                    segLeftShell.setCoordinates(listShell.get(i), listShell.get(j));
                    segLeftInner = segmentOffset(segLeftShell, -dist);
                }

                //Коррекция первой и последней точки дуги
                if (i == 1) {
                    segRighInner.p0.z = ID;
                    listInner.add(segRighInner.p0);
                }
                //Точка пересечения сегментов
                cross = segLeftInner.intersection(segRighInner);

                if (cross != null) { //заполнение очереди
                    cross.z = ID;
                    listInner.add(cross);
                }
                if (i == listShell.size() - 2) {
                    segLeftInner.p1.z = ID;
                    listInner.add(segLeftInner.p1);
                }
            }
            //Test.init(gf.createLineString(listInner.toArray(new Coordinate[0]);
            Collections.reverse(listInner);
            listInner.addAll(listShell);
            listInner.add(0, listInner.get(listInner.size() - 1));
            result = gf.createPolygon(listInner.toArray(new Coordinate[0]));

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.bufferCurve() " + e);
        }
        return result;
    }

    public static Polygon bufferPolygon(Geometry geoShell, Map<Double, Double> hmDist) {

        Polygon result = gf.createPolygon();
        List<Coordinate> listBuffer = new ArrayList<Coordinate>();
        List<Coordinate> listShell = List.of(geoShell.getCoordinates());
        try {
            for (int i = 0; i < listShell.size() - 1; i++) {

                //Перебор левого и правого сегмента от точки пересечения 
                int j = (i == 0) ? listShell.size() - 2 : i - 1;
                final double id1 = listShell.get(j).z;
                segRighShell.setCoordinates(listShell.get(j), listShell.get(i));
                segRighInner = segmentOffset(segRighShell, -hmDist.get(id1));

                int k = (i == listShell.size() - 1) ? 0 : i + 1;
                final double id2 = listShell.get(i).z;
                segLeftShell.setCoordinates(listShell.get(i), listShell.get(k));
                segLeftInner = segmentOffset(segLeftShell, -hmDist.get(id2));

                //Точка пересечения сегментов
                cross = segLeftInner.intersection(segRighInner);

                if (cross != null) {
                    cross.z = listShell.get(i).z;
                    listBuffer.add(cross);
                }
            }
            listBuffer.add(listBuffer.get(0));
            Polygon geoBuffer = gf.createPolygon(listBuffer.toArray(new Coordinate[0]));

            result = geoBuffer;

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.bufferPolygon() " + e);
        }
        return result;
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
            Com5t.gsf.setNumPoints(Com5t.MAXPOINT);
            Coordinate coord = new Coordinate(x1 + (x2 - x1) / 2 - R, y - h);
            Com5t.gsf.setBase(coord);
            LineString ls = Com5t.gsf.createArc(Math.PI + angl, Math.PI - 2 * angl).reverse();
            Coordinate lm[] = ls.getCoordinates();
            //Coordinate lm[] = ls.copy().getCoordinates();
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

    public static int getIndex(Geometry geo, double id) {
        Coordinate coo[] = geo.getGeometryN(0).getCoordinates();
        for (int i = 0; i < coo.length - 1; i++) {
            if (coo[i].z == id) {
                return i;
            }
        }
        System.err.println("Ошибка:UGeo.getIndex()");
        return -1;
    }

    public static int getIndex(int count, int index) {

        if (index > count - 1) {
            return index - count;
        }
        if (index < 0) {
            return count + index - 1;
        }
        return index;
    }

    public static ElemSimple getES(Wincalc winc, double x, double y) {

        for (ElemSimple el : winc.listElem) {
            if (el.x1() != null || el.y1() != null || el.x2() != null || el.y2() != null) {
                if ((el.x1() == x && el.y1() == y) || (el.x2() == x && el.y2() == y)) {
                    return el;
                }
            }
        }
        throw new IllegalArgumentException("Ошибка: UGeo.getID() Возврат не может быть null");
    }

    public static double getID(Wincalc winc, double x, double y) {

        for (ElemSimple el : winc.listElem) {
            if (el.x1() != null || el.y1() != null || el.x2() != null || el.y2() != null) {
                if ((el.x1() == x && el.y1() == y) || (el.x2() == x && el.y2() == y)) {
                    return el.id;
                }
            }
        }
        throw new IllegalArgumentException("Ошибка: UGeo.getID() Возврат не может быть null");
    }

    public static double getID(Wincalc winc, Coordinate co) {
        Object o1 = 0, o2 = 0, id = -1;
        Type typ;
        for (ElemSimple el : winc.listElem) {
            if (el.x1() != null || el.y1() != null || el.x2() != null || el.y2() != null) {
                if ((el.x1() == co.x && el.y1() == co.y) || (el.x2() == co.x && el.y2() == co.y)) {
                    return el.id;
                }
            }
        }
        throw new IllegalArgumentException("Ошибка: UGeo.getID() Возврат не может быть null");
    }

    public static LineSegment normalizeSegm(LineSegment segm) {
        segm.normalize();
        return segm;
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

    public static void PRINT(Geometry g) {
        Coordinate coo[] = g.getCoordinates();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < coo.length; i++) {
            list.add("{" + UCom.format(coo[i].x, 2) + " " + UCom.format(coo[i].y, 2) + " " + UCom.format(coo[i].z, 2) + "}");
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

    public static void PRINT(Map<Integer, Coordinate> map) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<Integer, Coordinate> coo : map.entrySet()) {
            list.add("{" + UCom.format(coo.getValue().x, 2) + " " + UCom.format(coo.getValue().y, 2) + " " + UCom.format(coo.getValue().z, 2) + "}");
        }
        System.out.println(list);
    }

    public static LineSegment segmentOffset(LineSegment segShell, double dxy) {
        LineSegment segInner = segShell.offset(dxy);
        segInner.p0.z = segShell.p0.z;
        segInner.p1.z = segShell.p1.z;
        return segInner;
    }

    public static Geometry polygonize(Geometry geometry) {
        List lines = LineStringExtracter.getLines(geometry);
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(lines);
        Collection polys = polygonizer.getPolygons();
        Polygon[] polyArray = GeometryFactory.toPolygonArray(polys);
        return geometry.getFactory().createGeometryCollection(polyArray);
    }

    public static Geometry split3Polygon(Geometry poly, Geometry line) {
        Geometry nodedLinework = poly.getBoundary().union(line);
        Geometry polys = polygonize(nodedLinework);

        // Only keep polygons which are inside the input
        List output = new ArrayList();
        for (int i = 0; i < polys.getNumGeometries(); i++) {
            Polygon candpoly = (Polygon) polys.getGeometryN(i);
            if (poly.contains(candpoly.getInteriorPoint())) {
                output.add(candpoly);
            }
        }
        return poly.getFactory().createGeometryCollection(GeometryFactory.toGeometryArray(output));
    }

// <editor-fold defaultstate="collapsed" desc="TEMP">
// </editor-fold>   
}
