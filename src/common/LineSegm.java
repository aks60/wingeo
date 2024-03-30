package common;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

public class LineSegm extends LineSegment {

    public LineSegm() {
        super();
    }

    public LineSegm(Coordinate c1, Coordinate c2, double z) {
        super(c1.x, c1.y, c2.x, c2.y);
        this.p0.z = z;
        this.p1.z = z;
    }

    public LineSegm(double x0, double y0, double x1, double y1, double z) {
        super(x0, y0, x1, y1);
        this.p0.z = z;
        this.p1.z = z;
    }

    public void setCoordinates(Coordinate p0, Coordinate p1, double z) {
        this.p0.x = p0.x;
        this.p0.y = p0.y;
        this.p0.z = p0.z;
        this.p1.x = p1.x;
        this.p1.y = p1.y;
        this.p1.z = p1.z;
    }

    @Override
    public LineSegment offset(double offsetDistance) {
        LineSegment seg = super.offset(offsetDistance);
        seg.p0.z = this.p0.z;
        seg.p1.z = this.p1.z;
        return seg;
    }

    @Override
    public Coordinate intersection(LineSegment line) {
        Coordinate c = super.intersection(line);
        c.z = line.p0.z;
        return c;
    }
}
