package builder.model;

import domain.eArtikl;
import enums.Layout;
import enums.Type;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

public class UGeo {

    public static double sin(double angl) {
        return Math.sin(Math.toRadians(angl));
    }

    public static double asin(double angl) {
        return Math.toDegrees(Math.asin(angl));
    }

    public static double cos(double angl) {
        return Math.cos(Math.toRadians(angl));
    }

    public static double tan(double angl) {
        return Math.tan(Math.toRadians(angl));
    }

    public static double acos(double angl) {
        return Math.toDegrees(Math.acos(angl));
    }

    public static double atan(double angl) {
        return Math.toDegrees(Math.atan(angl));
    }

    //http://ru.solverbook.com/spravochnik/vektory/ugol-mezhdu-vektorami/
    public static double betweenAngl(ElemSimple e1, ElemSimple e2) {

        double dx1 = e1.x2() - e1.x1();
        double dy1 = e1.y2() - e1.y1();
        double dx2 = e2.x2() - e2.x1();
        double dy2 = e2.y2() - e2.y1();

        double s = dx1 * dx2 + dy1 * dy2;
        double a = Math.sqrt(Math.pow(dx1, 2) + Math.pow(dy1, 2));
        double b = Math.sqrt(Math.pow(dx2, 2) + Math.pow(dy2, 2));
        double c = s / (a * b);
        return 180 - acos(c);
    }

    //https://www.onemathematicalcat.org/Math/Precalculus_obj/horizVertToDirMag.htm
    public static double horizontAngl(ElemSimple e) {

        double x = e.x2() - e.x1();
        double y = e.y1() - e.y2();

        if (x > 0 && y == 0) {
            return 0;
        } else if (x < 0 && y == 0) {
            return 180;
        } else if (x == 0 && y > 0) {
            return 90;
        } else if (x == 0 & y < 0) {
            return 270;
        } else if (x > 0 && y > 0) {
            return atan(y / x);
        } else if (x < 0 && y > 0) {
            return 180 + atan(y / x);
        } else if (x < 0 && y < 0) {
            return 180 + atan(y / x);
        } else if (x > 0 && y < 0) {
            return 360 + atan(y / x);
        } else {
            return 0;
        }
    }

    //Ширина рамки по оси x и y
    public static double[] diffOnAngl(double anglHoriz, double h) {

        double x = -1 * cos(anglHoriz);
        double y = -1 * sin(anglHoriz);

        if (Math.abs(x) >= Math.abs(y)) {
            return new double[]{0, h / x};
        } else {
            return new double[]{h / y, 0};
        }
    }

    //Точка пересечения двух линий 
    //https://habr.com/ru/articles/523440/ 
    public static double[] crossOnLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double n;
        double dot[] = {0, 0};
        if (y2 - y1 != 0) {  // a(y)
            double q = (x2 - x1) / (y1 - y2);
            double sn = (x3 - x4) + (y3 - y4) * q;
            if (sn == 0) {
                return null;
            }  // c(x) + c(y)*q

            double fn = (x3 - x1) + (y3 - y1) * q;   // b(x) + b(y)*q
            n = fn / sn;
        } else {
            if ((y3 - y4) == 0) {
                return null;
            }  // b(y)

            n = (y3 - y1) / (y3 - y4);   // c(y)/b(y)
        }
        dot[0] = x3 + (x4 - x3) * n;  // x3 + (-b(x))*n
        dot[1] = y3 + (y4 - y3) * n;  // y3 +(-b(y))*n
        return dot;
    }

    //https://stackoverflow.com/questions/21941156/shapes-and-segments-in-java
    public static Area[] splitCanvas(Area area, Com5t line) {

        //Вычисление угла линии к оси x
        double dx = line.x2() - line.x1();
        double dy = line.y2() - line.y1();
        double angl = Math.atan2(dy, dx);

        //Выравниваем область так, чтобы линия совпадала с осью x
        AffineTransform at = new AffineTransform();
        at.rotate(-angl);
        at.translate(-line.x1(), -line.y1());
        Area aa = area.createTransformedArea(at);

        //Вычисляем верхнюю и нижнюю половины площади должы пересекаться с...
        Rectangle2D bounds = aa.getBounds2D();

        double half0minY = Math.min(0, bounds.getMinY());
        double half0maxY = Math.min(0, bounds.getMaxY());
        Rectangle2D half0 = new Rectangle2D.Double(
                bounds.getX(), half0minY, bounds.getWidth(), half0maxY - half0minY);

        double half1minY = Math.max(0, bounds.getMinY());
        double half1maxY = Math.max(0, bounds.getMaxY());
        Rectangle2D half1 = new Rectangle2D.Double(
                bounds.getX(), half1minY, bounds.getWidth(), half1maxY - half1minY);

        //Вычисляем получившиеся площади путем пересечения исходной области с 
        //обеими половинками, и возвращаем их в исходное положение
        Area a0 = new Area(aa);
        a0.intersect(new Area(half0));

        Area a1 = new Area(aa);
        a1.intersect(new Area(half1));

        try {
            at.invert();
        } catch (NoninvertibleTransformException event) {
            System.err.println("Ошибка:UGeo.split() " + event);
        }
        a0 = a0.createTransformedArea(at);
        a1 = a1.createTransformedArea(at);

        return new Area[]{a0, a1};
    }

    //Точки пересечение импостом Canvas2D. x = (x2 - x1) * (y - y1) / (y2 - y1) + x1
    //https://www.interestprograms.ru/source-codes-tochka-peresecheniya-dvuh-pryamyh-na-ploskosti#uravnenie-v-programmnyj-kod      
    public static double[] crossCanvas(double x1, double y1, double x2, double y2, double w, double h) {
//        double X1 = (y1 == y2) ? 0 : (((0 - y1) / (y2 - y1)) * (x2 - x1)) + x1;
//        double X2 = (y1 == y2) ? w : (((h - y1) / (y2 - y1)) * (x2 - x1)) + x1;
//        //System.out.println(X1 + "  " + 0 + "  =  " + X2 + "  " + h);
//        return new double[]{X1, 0, X2, h};

        double Y1 = (y1 == y2) ? y1 + 0.0000000001 : y1;
        double Y2 = (y1 == y2) ? y2 - 0.0000000001 : y2;
        double X1 = (x2 - x1) * (0 - Y1) / (Y2 - Y1) + x1;
        double X2 = (x2 - x1) * (h - Y1) / (Y2 - Y1) + x1;
        return new double[]{X1, 0, X2, h};
    }

    public static boolean pointOnLine(double x, double y, double x1, double y1, double x2, double y2) {
        //return (Math.round(((x2 - x1) * (y - y1)) - ((y2 - y1) * (x - x1))) == 0);
        //return (((x2 - x1) * (y - y1)) - ((y2 - y1) * (x - x1)) == 0);
        double d = ((x2 - x1) * (y - y1)) - ((y2 - y1) * (x - x1));
        return (d > -.1 && d < .1);
    }

    public static ElemSimple elemFromSegment(List<ElemSimple> listLine, Line2D.Double segment) {
        for (ElemSimple elem : listLine) {
            if (UGeo.pointOnLine(segment.getX1(), segment.getY1(), elem.x1(), elem.y1(), elem.x2(), elem.y2())
                    && UGeo.pointOnLine(segment.getX2(), segment.getY2(), elem.x1(), elem.y1(), elem.x2(), elem.y2())) {
                return elem;
            }
        }
        return null;
    }

    //Находим предыднщую и последующую линию от совместной между area1 и area2
    /**
     * 0 - сегмент входящий слева 1 - сегмент выходящий слева 2 - общий сегмент
     * 3 - сегмент входящий справа 3 - сегмент выходящий справа
     */
    public static Line2D.Double[] prevAndNextSegment(Area area1, Area area2) {

        ArrayList<Line2D.Double> list1 = UGeo.areaAllSegment(area1);
        ArrayList<Line2D.Double> list2 = UGeo.areaAllSegment(area2);

        //List<Line2D.Double> list1 = listA.stream().filter(line -> !(line.x1 == line.x2 && line.y1 == line.y2)).collect(toList());
        //List<Line2D.Double> list2 = listB.stream().filter(line -> !(line.x1 == line.x2 && line.y1 == line.y2)).collect(toList());
        //Цикл по сегментам area1
        for (int ik = 0; ik < list1.size(); ik++) {
            Line2D.Double line1 = list1.get(ik);
//            if (Math.round(line1.x1) == Math.round(line1.x2) && Math.round(line1.y1) == Math.round(line1.y2)) {
//                continue;
//            }

            //Цикл по сегментам area2
            for (int ij = 0; ij < list2.size(); ij++) {
                Line2D.Double line2 = list2.get(ij);
//                if (Math.round(line2.x1) == Math.round(line2.x2) && Math.round(line2.y1) == Math.round(line2.y2)) {
//                    continue;
//                }

                //Если сегмент area1 и area2 общий
                //if ((line1.x1 == line2.x2 && line1.y1 == line2.y2) && (line1.x2 == line2.x1 && line1.y2 == line2.y1)) {
                if ((Math.round(line1.x1) == Math.round(line2.x1) && Math.round(line1.y1) == Math.round(line2.y1)
                        && Math.round(line1.x2) == Math.round(line2.x2) && Math.round(line1.y2) == Math.round(line2.y2))
                        || (Math.round(line1.x1) == Math.round(line2.x2) && Math.round(line1.y1) == Math.round(line2.y2)
                        && Math.round(line1.x2) == Math.round(line2.x1) && Math.round(line1.y2) == Math.round(line2.y1))) {

                    //Находим предыдущий и последующий сегмент
                    int k1 = (ik == 0) ? list1.size() - 1 : ik - 1;
                    int j1 = (ik == (list1.size() - 1)) ? 0 : ik + 1;
                    Line2D.Double[] l1 = new Line2D.Double[]{list1.get(k1), list1.get(j1)};

                    int k2 = (ij == 0) ? list2.size() - 1 : ij - 1;
                    int j2 = (ij == (list2.size() - 1)) ? 0 : ij + 1;
                    Line2D.Double[] l2 = new Line2D.Double[]{list2.get(k2), list2.get(j2)};

                    return new Line2D.Double[]{l1[0], l1[1], line1, l2[0], l2[1]};
                }
            }
        }
        return null;
    }

    //Внутренняя обводка ареа  
    public static Area areaPadding(Area area, List<ElemSimple> listFrame) {

        ArrayList<Line2D.Double> segmList = areaAllSegment(area);
        List<Double> listPoint = new ArrayList();
        try {
            for (int i = 0; i < segmList.size(); i++) {

                int j = (i == (segmList.size() - 1)) ? 0 : i + 1;
                Line2D.Double segment1 = segmList.get(i);
                Line2D.Double segment2 = segmList.get(j);

                ElemSimple e1 = UGeo.elemFromSegment(listFrame, segment1);
                ElemSimple e2 = UGeo.elemFromSegment(listFrame, segment2);

                if (e1 != null && e2 != null && e1 != e2) {
                    double h1[] = UGeo.diffOnAngl(UGeo.horizontAngl(e1), e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr));
                    double h2[] = UGeo.diffOnAngl(UGeo.horizontAngl(e2), e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr));
                    double p[] = UGeo.crossOnLine( //смещённая внутрь точка пересечения сегментов
                            e1.x1() + h1[0], e1.y1() + h1[1], e1.x2() + h1[0], e1.y2() + h1[1],
                            e2.x1() + h2[0], e2.y1() + h2[1], e2.x2() + h2[0], e2.y2() + h2[1]);

                    listPoint.add(p[0]);
                    listPoint.add(p[1]);
                }
            }
            double[] arrayPoint = listPoint.stream().mapToDouble(i -> i).toArray();
            return UGeo.areaPoly(arrayPoint);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.areaPadding()" + e);
            return null;
        }
    }

    //https://stackoverflow.com/questions/8144156/using-pathiterator-to-return-all-line-segments-that-constrain-an-area        
    public static ArrayList<Line2D.Double> areaAllSegment(Area area) {
        ArrayList<double[]> areaPoints = new ArrayList<double[]>();
        ArrayList<Line2D.Double> areaSegments = new ArrayList<Line2D.Double>();
        double[] coords = new double[6];

        for (PathIterator pi = area.getPathIterator(null); !pi.isDone(); pi.next()) {
            int type = pi.currentSegment(coords);
            double[] pathIteratorCoords = {type, coords[0], coords[1]};
            areaPoints.add(pathIteratorCoords);
        }

        double[] start = new double[3];

        for (int i = 0; i < areaPoints.size(); i++) {
            double[] currentElement = areaPoints.get(i);
            double[] nextElement = {-1, -1, -1};
            if (i < areaPoints.size() - 1) {
                nextElement = areaPoints.get(i + 1);
            }
            if (currentElement[0] == PathIterator.SEG_MOVETO) {
                start = currentElement;
            }

            if (nextElement[0] == PathIterator.SEG_LINETO) {
                areaSegments.add(
                        new Line2D.Double(
                                currentElement[1], currentElement[2],
                                nextElement[1], nextElement[2]
                        )
                );
            } else if (nextElement[0] == PathIterator.SEG_CLOSE) {
                areaSegments.add(
                        new Line2D.Double(
                                currentElement[1], currentElement[2],
                                start[1], start[2]
                        )
                );
            }
        }
        return areaSegments;
    }

    public static Area areaRectangl(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        try {
            GeneralPath p = new GeneralPath();
            p.moveTo(x1, y1);
            p.lineTo(x2, y2);
            p.lineTo(x3, y3);
            p.lineTo(x4, y4);
            p.closePath();
            return new Area(p);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.areaRectangl()" + e);
            return null;
        }
    }

    public static Area areaPoly(double... m) {
        try {
            GeneralPath p = new GeneralPath();
            p.moveTo(Math.round(m[0]), Math.round(m[1]));
            for (int i = 3; i < m.length; i = i + 2) {
                p.lineTo(Math.round(m[i - 1]), Math.round(m[i]));
            }
            p.closePath();
            return new Area(p);

        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.areaPoly()");
            return null;
        }
    }

// <editor-fold defaultstate="collapsed" desc="XLAM">
    public static void PRINT(Coordinate[] coord) {

        List<Double> list = new ArrayList();
        for (Coordinate c : coord) {
            list.add(c.x);
            list.add(c.y);
        }
        double[] arr = new double[2 * coord.length];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);

        }
        UGeo.PRINT(arr);
    }

    public static void PRINT(Area area) {
        int i = 0;
        ArrayList<Line2D.Double> listLine = UGeo.areaAllSegment(area);
        ArrayList<String> listStr = new ArrayList();
        for (Line2D.Double line : listLine) {
            listStr.add("  <" + (++i) + ">" + Math.round(line.x1) + " " + Math.round(line.y1) + ", " + Math.round(line.x2) + " " + Math.round(line.y2));
            //listStr.add("  <" + (++i) + ">" + line.x1 + " " + line.y1 + ", " + line.x2 + " " + line.y2);
            //listStr.add(line.x1 + ", " + line.y1 + ", " + line.x2 + ", " + line.y2 + " = ");
        }
        System.out.println(listStr);
    }

    public static void PRINT(Line2D.Double line) {
        System.out.println((int) line.x1 + ", " + (int) line.y1 + ", " + (int) line.x2 + ", " + (int) line.y2);
    }

    public static void PRINT(double... p) {
        int index = 0;
        String str = "";
        for (int i = 0; i < p.length; i++) {
            double d = p[i];
            if ((i & 1) == 0) {
                str = str + "<" + ++index + ">" + Math.round(d) + " ";
            } else {
                str = str + Math.round(d) + ", ";
            }
        }
        System.out.println(str);
    }

    //https://www.interestprograms.ru/source-codes-tochka-peresecheniya-dvuh-pryamyh-na-ploskosti#uravnenie-v-programmnyj-kod
    //x = (x2 - x1) * (y - y1) / (y2 - y1) + x1   y = (y2 - y1) * (x - x1) / (x2 - x1) + y1
    public static double[] splitCanvas(double x1, double y1, double x2, double y2, double w, double h) {
        try {
            if (y1 == y2) {
                return new double[]{0, 0, w, 0, w, y2, 0, y1};
            }
            double X1 = (x2 - x1) * (0 - y1) / (y2 - y1) + x1; //y=0
            double X2 = (x2 - x1) * (h - y1) / (y2 - y1) + x1; //y=h
            double Y1 = (y2 - y1) * (0 - x1) / (x2 - x1) + y1; //x=0
            double Y2 = (y2 - y1) * (w - x1) / (x2 - x1) + y1; //x=w

            if (X1 < 0 || X1 > w) {
                return new double[]{0, 0, w, 0, w, Y2, 0, Y1};

            } else if (Y1 > 0 && Y1 > 0) {
                return new double[]{X1, 0, w, 0, w, Y2};

            } else {
                return new double[]{0, 0, X1, 0, X2, h, 0, h};
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UGeo.cross2Canvas()" + e);
            return null;
        }
    }

    public static double hypotenuseMax(Area area) {
        double[] c0 = new double[6];
        double s0 = 0;
        PathIterator it1 = area.getPathIterator(null);
        while (!it1.isDone()) {
            it1.currentSegment(c0);
            double s = c0[0] * c0[0] + c0[1] * c0[1];
            if (s > s0) {
                s0 = s;
            }
            it1.next();
        }
        return s0;
    }

    /**
     * Реализует алгоритм отсечения Кируса-Бека по произвольному выпуклому
     * многоугольнику с параметрическим заданием линий
     *
     * int V_CBclip (float *x0, float *y0, float *x1, float *y1)
     *
     * Отсекает отрезок, заданный значениями координат его точек (x0,y0),
     * (x1,y1), по окну отсечения, заданному глобальными скалярами: int Windn -
     * количество вершин в окне отсечения float *Windx, *Windy - массивы X,Y
     * координат вершин float *Wnormx, *Wnormy - массивы координат нормалей к
     * ребрам
     *
     * Возвращает: 0 - отрезок не видим 1 - отрезок видим
     */
    int V_CBclip(float x0, float y0, float x1, float y1, int Windn) {

        //float x0, y0, x1, y1;
        float[] Windx = null, Windy = null, Wnormx = null, Wnormy = null;

        int ii, jj, visible, kw;
        float xn, yn, dx, dy, r;
        float CB_t0, CB_t1;
        /* Параметры концов отрезка */
        float Qx, Qy;
        /* Положение относ ребра */
        float Nx, Ny;
        /* Перпендикуляр к ребру */
        float Pn, Qn;
        /**/

        kw = Windn - 1;
        /* Ребер в окне */
        visible = 1;
        CB_t0 = 0;
        CB_t1 = 1;
        dx = x1 - (xn = x0);
        dy = y1 - (yn = y0);

        for (ii = 0; ii <= kw; ++ii) {
            /* Цикл по ребрам окна */
            Qx = xn - Windx[ii];
            /* Положения относ ребра */
            Qy = yn - Windy[ii];
            Nx = Wnormx[ii];
            /* Перепендикуляр к ребру */
            Ny = Wnormy[ii];
            Pn = dx * Nx + dy * Ny;
            /* Скалярные произведения */
            Qn = Qx * Nx + Qy * Ny;

            /* Анализ расположения */
            if (Pn == 0) {
                /* Паралл ребру или точка */
                if (Qn < 0) {
                    visible = 0;
                    break;
                }
            } else {
                r = -Qn / Pn;
                if (Pn < 0) {
                    /* Поиск верхнего предела t */
                    if (r < CB_t0) {
                        visible = 0;
                        break;
                    }
                    if (r < CB_t1) {
                        CB_t1 = r;
                    }
                } else {
                    /* Поиск нижнего предела t */
                    if (r > CB_t1) {
                        visible = 0;
                        break;
                    }
                    if (r > CB_t0) {
                        CB_t0 = r;
                    }
                }
            }
        }
        if (visible == 1) {
            if (CB_t0 > CB_t1) {
                visible = 0;
            } else {
                if (CB_t0 > 0) {
                    x0 = xn + CB_t0 * dx;
                    y0 = yn + CB_t0 * dy;
                }
                if (CB_t1 < 1) {
                    x1 = xn + CB_t1 * dx;
                    y1 = yn + CB_t1 * dy;
                }
            }
        }
        return (visible);
    }

    //https://www.geeksforgeeks.org/line-clipping-set-2-cyrus-beck-algorithm/
    //https://www.bilee.com/java-%D1%82%D0%BE%D1%87%D0%BA%D0%B0-%D0%BF%D0%B5%D1%80%D0%B5%D1%81%D0%B5%D1%87%D0%B5%D0%BD%D0%B8%D1%8F-%D0%BC%D0%BD%D0%BE%D0%B3%D0%BE%D1%83%D0%B3%D0%BE%D0%BB%D1%8C%D0%BD%D0%B8%D0%BA%D0%B0-%D0%B8.html
    public static Point2D[] cross(final Area poly, final ElemCross elem) { //throws Exception {
        final Line2D.Double line = new Line2D.Double(elem.x1(), elem.y1(), elem.x2(), elem.y2());
        final PathIterator polyIt = poly.getPathIterator(null);
        final double[] coords = new double[6]; //двойной массив длиной 6, необходимый для итератора 
        final double[] firstCoords = new double[2]; //первая точка (необходима для замыкания полигона)
        final double[] lastCoords = new double[2]; //ранее посещенная точка
        final Set<Point2D> intersections = new HashSet(); //список для хранения найденных пересечений 
        polyIt.currentSegment(firstCoords); //Получение первой пары координат 
        lastCoords[0] = firstCoords[0]; //заполнение предыдущей пары координат 
        lastCoords[1] = firstCoords[1];
        polyIt.next();
        while (!polyIt.isDone()) {
            final int type = polyIt.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_LINETO: {
                    final Line2D.Double currentLine = new Line2D.Double(lastCoords[0], lastCoords[1], coords[0], coords[1]);
                    if (currentLine.intersectsLine(line)) {
                        intersections.add(UGeo.cross(currentLine, line));
                    }
                    lastCoords[0] = coords[0];
                    lastCoords[1] = coords[1];
                    break;
                }
                case PathIterator.SEG_CLOSE: {
                    final Line2D.Double currentLine = new Line2D.Double(coords[0], coords[1], firstCoords[0], firstCoords[1]);
                    if (currentLine.intersectsLine(line)) {
                        intersections.add(UGeo.cross(currentLine, line));
                    }
                    break;
                }
                default: {
                    return null;
                    //throw new Exception("Unsupported PathIterator segment type.");
                }
            }
            polyIt.next();
        }
        return intersections.toArray(new Point2D[0]);
    }

    public static double[] cross(Area area[]) {
        List<Double> p = new ArrayList();
        Set hs = new HashSet();
        double[] c1 = new double[6], c2 = new double[6];
        PathIterator i1 = area[0].getPathIterator(null);

        while (!i1.isDone()) {
            i1.currentSegment(c1);
            PathIterator i2 = area[1].getPathIterator(null);

            while (!i2.isDone()) {
                i2.currentSegment(c2);
                if (c1[0] == c2[0] && c1[1] == c2[1]) {
                    if (hs.add(c1[0] + "-" + c1[1])) {
                        p.add(c1[0]);
                        p.add(c1[1]);
                    }
                    break;
                }
                i2.next();
            }
            i1.next();
        }
        if (p.size() > 3) {
            return new double[]{p.get(0), p.get(1), p.get(2), p.get(3)};
        } else {
            return null;
        }
    }

    //https://www.bilee.com/java-%D1%82%D0%BE%D1%87%D0%BA%D0%B0-%D0%BF%D0%B5%D1%80%D0%B5%D1%81%D0%B5%D1%87%D0%B5%D0%BD%D0%B8%D1%8F-%D0%BC%D0%BD%D0%BE%D0%B3%D0%BE%D1%83%D0%B3%D0%BE%D0%BB%D1%8C%D0%BD%D0%B8%D0%BA%D0%B0-%D0%B8.html
    public static Point2D cross(final Line2D.Double line1, final Line2D.Double line2) {
        final double x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = line1.x1;
        y1 = line1.y1;
        x2 = line1.x2;
        y2 = line1.y2;
        x3 = line2.x1;
        y3 = line2.y1;
        x4 = line2.x2;
        y4 = line2.y2;
        final double x = ((x2 - x1) * (x3 * y4 - x4 * y3) - (x4 - x3) * (x1 * y2 - x2 * y1)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        final double y = ((y3 - y4) * (x1 * y2 - x2 * y1) - (y1 - y2) * (x3 * y4 - x4 * y3)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        return new Point2D.Double(x, y);
    }

    //Точка пересечения двух векторов 
    public static double[] cross(ElemSimple e1, ElemSimple e2) {
        return UGeo.crossOnLine(e1.x1(), e1.y1(), e1.x2(), e1.y2(), e2.x1(), e2.y1(), e2.x2(), e2.y2());
    }

    //Точка пересечения двух векторов 
    public static Point cross(Point A, Point B, Point C, Point D) {

        // Line AB represented as a1x + b1y = c1
        double a1 = B.y - A.y;
        double b1 = A.x - B.x;
        double c1 = a1 * (A.x) + b1 * (A.y);

        // Line CD represented as a2x + b2y = c2
        double a2 = D.y - C.y;
        double b2 = C.x - D.x;
        double c2 = a2 * (C.x) + b2 * (C.y);

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            // The lines are parallel. This is simplified
            // by returning a pair of FLT_MAX
            return new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return new Point((int) x, (int) y);
        }
    }

    //Скалярное произведение
    public static int dot(Point2D p0, Point2D p1) {
        return (int) (p0.getX() * p1.getX() + p0.getY() * p1.getY());
    }

    //Вычисления максимума из вектора с плавающей запятой
    private static double max(List<Double> t) {
        double maximum = 0;
        for (int i = 0; i < t.size(); i++) {
            if (t.get(i) > maximum) {
                maximum = t.get(i);
            }
        }
        return maximum;
    }

    //Вычисления минимума из вектора с плавающей запятой
    private static double min(List<Double> t) {
        double minimum = 500;
        for (int i = 0; i < t.size(); i++) {
            if (t.get(i) < minimum) {
                minimum = t.get(i);
            }
        }
        return minimum;
    }

    //Функция Сайруса Бека возвращает пару значений, которые затем 
    //отображаются в виде вершины многоугольника
    public static double[] cut(Area area, Point2D line[], int n) {

        List arr = new ArrayList();
        final double[] dbl = new double[6];
        PathIterator iterator = area.getPathIterator(null);
        while (!iterator.isDone()) {
            final int segmentType = iterator.currentSegment(dbl);
            if (segmentType == PathIterator.SEG_LINETO) {
                arr.add(new Point2D.Double(dbl[0], dbl[1]));
            } else if (segmentType == PathIterator.SEG_MOVETO) {
                arr.add(new Point2D.Double(dbl[0], dbl[1]));
            } else if (segmentType == PathIterator.SEG_CLOSE) {
                arr.add(new Point2D.Double(dbl[0], dbl[1]));
            }
            iterator.next();
        }

        Point2D vertices[] = new Point2D[n];
        for (int i = 0; i < n; i++) {
            vertices[i] = (Point2D) arr.get(i);
        }
        Point2D newPair[] = {new Point2D.Double(), new Point2D.Double()}; //значение временного держателя, которое будет возвращено        
        Point2D normal[] = new Point2D.Double[n]; //нормали инициализируются динамически (можно и статически, не имеет значения)

        //Расчет нормалей
        for (int i = 0; i < n; i++) {
            double y = vertices[(i + 1) % n].getX() - vertices[i].getX();
            double x = vertices[i].getY() - vertices[(i + 1) % n].getY();
            normal[i] = new Point2D.Double();
            normal[i].setLocation(x, y);
        }

        Point2D P1_P0 = new Point2D.Double((int) (line[1].getX() - line[0].getX()), (int) (line[1].getY() - line[0].getY())); //расчет P1 - P0       
        Point2D[] P0_PEi = new Point2D.Double[n];  //инициализация всех значений P0 - PEi

        //Вычисление значений P0 - PEi для всех ребер
        for (int i = 0; i < n; i++) {
            //Вычисление PEi - P0, чтобы знаменатель не умножался на -1
            double P0_PEx = vertices[i].getX() - line[0].getX();
            double P0_PEy = vertices[i].getY() - line[0].getY();
            P0_PEi[i] = new Point2D.Double();
            P0_PEi[i].setLocation(P0_PEx, P0_PEy); //при расчете t 
        }

        int numerator[] = new int[n], denominator[] = new int[n];
        //Вычисление числителя и знаменателя с помощью точечной функции
        for (int i = 0; i < n; i++) {
            numerator[i] = dot(normal[i], P0_PEi[i]);
            denominator[i] = dot(normal[i], P1_P0);
        }

        double t[] = new double[n]; //динамическая инициализация значений t
        //Создание двух векторов, называемых «не входящими» и 
        //«не выходящими», для группировки «t» в соответствии с их знаменателями
        LinkedList<Double> tE = new LinkedList(), tL = new LinkedList();

        // Вычисление 't' и их группировка соответственно
        for (int i = 0; i < n; i++) {
            t[i] = (double) (numerator[i]) / (double) (denominator[i]);
            if (denominator[i] > 0) {
                tE.add(t[i]);
            } else {
                tL.add(t[i]);
            }
        }
        double temp[] = new double[2]; //инициализация последних двух значений 't'      
        tE.add(0.0);  //берем максимум всех «tE» и 0, поэтому нажимаем 0
        temp[0] = max(tE);
        tL.add(1.0); //принимая минимальное значение всех «tL» и 1, поэтому нажмите 1
        temp[1] = min(tL);

        //Ввод значения 't' не может быть больше, чем выход значения 't', 
        //следовательно, это тот случай, когда линия полностью выходит за пределы
        if (temp[0] > temp[1]) {
            newPair[0] = new Point2D.Double(-1, -1);
            newPair[1] = new Point2D.Double(-1, -1);
            return new double[]{newPair[0].getX(), newPair[0].getY(), newPair[1].getX(), newPair[1].getY()};
        }

        //Вычисление координат по x и y
        double newPair0x = line[0].getX() + P1_P0.getX() * temp[0];
        double newPair0y = line[0].getY() + P1_P0.getY() * temp[0];
        double newPair1x = line[0].getX() + P1_P0.getX() * temp[1];
        double newPair1y = line[0].getY() + P1_P0.getY() * temp[1];
        //newPair[0].setLocation(newPair0x, newPair0y);
        //newPair[1].setLocation(newPair1x, newPair1y);

        //System.out.println("(" + newPair[0].getX() + ", " + newPair[0].getY() + ") (" + newPair[1].getX() + ", " + newPair[1].getY() + ")");
        return new double[]{newPair0x, newPair0y, newPair1x, newPair1y};
    }

    //Ширина рамки по оси x и y
    public static double[] diff(ElemSimple e, double dh) {

        double x = -1 * cos(e.anglHoriz);
        double y = -1 * sin(e.anglHoriz);

        if (Math.abs(x) >= Math.abs(y)) {
            return new double[]{0, dh / x};
        } else {
            return new double[]{dh / y, 0};
        }
    }

    public static double[] diff(Area shape, ElemSimple e, double dh) {
        boolean imp = false;
        if (e.type == Type.IMPOST || e.type == Type.STOIKA || e.type == Type.RIGEL_IMP) {
            if (e.layout == Layout.VERT && (shape.getBounds2D().getX() == e.x1() || shape.getBounds2D().getX() == e.x2())) {
                imp = true;
            }
            if (e.layout == Layout.HORIZ && (shape.getBounds2D().getY() == e.y1() || shape.getBounds2D().getY() == e.y2())) {
                imp = true;
            }
        }
        double x = cos(e.anglHoriz);
        double y = sin(e.anglHoriz);

        if (Math.abs(x) >= Math.abs(y)) {
            return (imp) ? new double[]{0, dh / x} : new double[]{0, -dh / x};
        } else {
            return (imp) ? new double[]{dh / y, 0} : new double[]{-dh / y, 0};
        }
    }

    public static Point get_line_intersection(Line2D.Double pLine1, Line2D.Double pLine2) {
        Point result = null;

        double s1_x = pLine1.x2 - pLine1.x1,
                s1_y = pLine1.y2 - pLine1.y1,
                s2_x = pLine2.x2 - pLine2.x1,
                s2_y = pLine2.y2 - pLine2.y1,
                s = (-s1_y * (pLine1.x1 - pLine2.x1) + s1_x * (pLine1.y1 - pLine2.y1)) / (-s2_x * s1_y + s1_x * s2_y),
                t = (s2_x * (pLine1.y1 - pLine2.y1) - s2_y * (pLine1.x1 - pLine2.x1)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            result = new Point(
                    (int) (pLine1.x1 + (t * s1_x)),
                    (int) (pLine1.y1 + (t * s1_y)));
        }   // end if

        return result;
    }

    public static double[] generalSegment(Area area1, Area area2) {
        ArrayList<Line2D.Double> list1 = UGeo.areaAllSegment(area1);
        ArrayList<Line2D.Double> list2 = UGeo.areaAllSegment(area2);

        for (Line2D.Double line1 : list1) {
            if (Math.round(line1.x1) != Math.round(line1.x2) && Math.round(line1.y1) != Math.round(line1.y2)) {

                for (Line2D.Double line2 : list2) {
                    if (Math.round(line2.x1) != Math.round(line2.x2) && Math.round(line2.y1) != Math.round(line2.y2)) {

                        if (Math.round(line1.x1) == Math.round(line2.x2) && Math.round(line1.y1) == Math.round(line2.y2)
                                && Math.round(line1.x2) == Math.round(line2.x1) && Math.round(line1.y2) == Math.round(line2.y1)) {
                            return new double[]{line1.x1, line1.y1, line1.x2, line1.y2};
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Area areaReduc(Area area) {
        if (area != null) {
            ArrayList<Integer> listPoint = new ArrayList();
            try {
                for (Line2D.Double line : UGeo.areaAllSegment(area)) {
                    if (!(line.x1 == line.x2 && line.y1 == line.y2)) { //проверка нулевого сегмета
                        listPoint.add((int) line.x2);
                        listPoint.add((int) line.y2);
                        //System.out.println(line.x1 + ", " + );
                    }
                }
                double[] arr = listPoint.stream().mapToDouble(i -> i).toArray();

                return UGeo.areaPoly(arr);

            } catch (Exception e) {
                System.err.println("Ошибка:UGeo.areaReduc()" + e);
            }
        }
        return area;
    }

// </editor-fold>    
}
