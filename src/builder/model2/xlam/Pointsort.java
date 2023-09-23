package builder.model2.xlam;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Comparator;

public class Pointsort implements Comparator<Point2D> {

    enum ARG {
        ZERO(1),
        PLUS_INF(2),
        PLUS_SPACE(3),
        MINUS_INF(4);
        int id = -1;

        ARG(int v) {
            this.id = v;
        }
    };

    ARG classof(Point2D a) {
        if (a.getX() == 0) {
            if (a.getY() == 0) {
                return ARG.ZERO;
            } else if (a.getY() > 0) {
                return ARG.PLUS_INF;
            } else {
                return ARG.MINUS_INF;
            }
        } else if (a.getX() > 0) {
            return ARG.PLUS_SPACE;
        } else {
            return null; //throw new Exception("мы так не договаривались");
        }
    }

    int compare_num(double a, double b) {
        if (a < b) {
            return -1;
        } else if (a > b) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compare(Point2D a, Point2D b) {
        if (a.getX() == b.getX() && a.getY() == b.getY()) {
            return 0;
        }

        ARG ca = classof(a);
        ARG cb = classof(b);

        if (ca != cb) {
            return compare_num(ca.id, cb.id);
        }

        if (ca == ARG.PLUS_INF || ca == ARG.MINUS_INF) {
            return compare_num(a.getY(), b.getY());
        }

        return compare_num(a.getY() * b.getX(), b.getY() * a.getX());
    }
}
