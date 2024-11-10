package builder.geom;

import org.locationtech.jts.geom.Coordinate;

public class Coord extends Coordinate {

    private double m;

    public Coord() {
        super();
        this.m = 0.0;
    }

    public Coord(double x, double y, double m) {
        super(x, y);
        this.m = m;
    }

    public Coord(double x, double y, double z, double m) {
        super(x, y, z);
        this.m = m;
    }

    public Coord(Coordinate coord) {
        super(coord);
        if (coord instanceof Coord) {
            m = ((Coord) coord).m;
        } else {
            m = Double.NaN;
        }
    }

    public Coord(Coord coord) {
        super(coord);
        m = coord.m;
    }

    public Coord copy() {
        return new Coord(this);
    }

    public double getM() {
        return m;
    }

    public void setM(double m) {
        this.m = m;
    }

    @Override
    public void setCoordinate(Coordinate other) {
        x = other.x;
        y = other.y;
        z = other.getZ();
        m = other.getM();
    }

    @Override
    public void setOrdinate(int ordinateIndex, double value) {
        switch (ordinateIndex) {
            case X:
                x = value;
                break;
            case Y:
                y = value;
                break;
            case Z:
                z = value;
                break;
            case M:
                m = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid ordinate index: " + ordinateIndex);
        }
    }

    @Override
    public double getOrdinate(int ordinateIndex) {
        switch (ordinateIndex) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            case M:
                return m;
        }
        throw new IllegalArgumentException("Invalid ordinate index: " + ordinateIndex);
    }

    public String toString() {
        String stringRep = "(" + x + "," + y + "," + getZ() + " m=" + m + ")";
        return stringRep;
    }
}
