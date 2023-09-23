package builder.model2.xlam;

import java.awt.geom.Point2D;
import java.util.Arrays;

//Теория см. http://grafika.me/node/161
public class PointPoligon {

    //Квадрат расстояния между двумя точками
    public static double dist2(Point2D a1, Point2D a2) {
        return (a2.getX() - a1.getX()) * (a2.getX() - a1.getX())
                + (a2.getY() - a1.getY()) * (a2.getY() - a1.getY());
    }

    //"Косое" произведение
    public static double cross(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
        return (a2.getX() - a1.getX()) * (b2.getY() - b1.getY())
                - (long) (b2.getX() - b1.getX()) * (a2.getY() - a1.getY());
    }

    //Алгоритм Джарвиса
    //https://informatics.msk.ru/mod/book/view.php?id=11835
    public static Point2D[] passJarvis(Point2D a[]) {

        int m = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i].getX() > a[m].getX()) {
                m = i;
            } else if (a[i].getX() == a[m].getX() && a[i].getY() < a[m].getY()) {
                m = i;
            }
        }

        Point2D p[] = new Point2D[a.length + 1];

        p[0] = a[m];
        a[m] = a[0];
        a[0] = p[0];

        int k = 0;
        int min = 0;

        do {
            for (int j = 1; j < a.length; j++) {
                if (cross(p[k], a[min], p[k], a[j]) < 0
                        || cross(p[k], a[min], p[k], a[j]) == 0
                        && dist2(p[k], a[min]) < dist2(p[k], a[j])) {
                    min = j;
                }
            }
            k++;
            p[k] = a[min];
            min = 0;
        } while (!(p[k].getX() == p[0].getX() && p[k].getY() == p[0].getY()));

        return Arrays.copyOf(p, k);
    }

//Алгоритм Грэхэма
//https://informatics.msk.ru/mod/book/view.php?id=11835    
    public static Point2D[] passGraham(Point2D a[]) {

        int m = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i].getX() < a[m].getX()) {
                m = i;
            } else if (a[i].getX() == a[m].getX() && a[i].getY() < a[m].getY()) {
                m = i;
            }
        }

        Point2D p[] = new Point2D[a.length];

        p[0] = a[m];
        a[m] = a[1];
        a[1] = p[0];
        int k = 0;

        for (int i = 1; i < a.length - 1; i++) {
            for (int j = 1; j < a.length - 1; i++) {
                if (cross(a[1], a[j], a[1], a[j + 1]) < 0
                        || cross(a[1], a[j], a[1], a[j + 1]) == 0
                        && dist2(a[1], a[j]) > dist2(a[1], a[j + 1])) {
                    Point2D t = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = t;
                }
            }
        }

        p[1] = a[1];
        p[2] = a[2];
        k = 2;
        for (int i = 3; i < a.length; i++) {
            while (cross(p[k - 1], p[k], p[k], a[i]) <= 0) {
                k--;
            }
            k = k + 1;
            p[k] = a[i];
        }
        return Arrays.copyOf(p, k);
    }
}
