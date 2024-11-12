package builder.geom;

import org.locationtech.jts.geom.Coordinate;

public class Co7e extends Coordinate {

    private double m;

    public Co7e() {
        super();
        this.m = 0.0;
    }

    public Co7e(double x, double y) {
        super(x, y);
         this.m = 0.0;
    }
    
    public Co7e(double x, double y, double m) {
        super(x, y);
        this.m = m;
    }

    public Co7e(double x, double y, double z, double m) {
        super(x, y, z);
        this.m = m;
    }

    public Co7e(Coordinate coord) {
        super(coord);
        if (coord instanceof Co7e) {
            m = ((Co7e) coord).m;
        } else {
            m = Double.NaN;
        }
    }

//    public Co7e(Co7e coord) {
//        super(coord);
//        m = coord.m;
//    }

    public Co7e copy() {
        return new Co7e(this);
    }

    public double getM() {
        return m;
    }

    public void setM(double m) {
        this.m = m;
    }

    public void getCom5e() {
        
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
