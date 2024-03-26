package common.jts;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

public class LineSegm extends LineSegment {

    public LineSegm(Coordinate c1, Coordinate c2, double z) {
        super(c1.x, c1.y, c2.x, c2.y);
        this.p0.z = z;
    }

    public LineSegm(double x0, double y0, double x1, double y1, double z) {
        super(x0, y0, x1, y1);
        this.p0.z = z;
    }
}
