package builder.geom;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;

public class CoordSequence implements CoordinateSequence {

    public static Coord[] copy(Coordinate[] coordinates) {
        Coord[] copy = new Coord[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            copy[i] = new Coord(coordinates[i]);
        }
        return copy;
    }

    public static Coord[] copy(CoordinateSequence coordSeq) {
        Coord[] copy = new Coord[coordSeq.size()];
        for (int i = 0; i < coordSeq.size(); i++) {
            copy[i] = new Coord(coordSeq.getCoordinate(i));
        }
        return copy;
    }

    private Coord[] coordinates;

    public CoordSequence(Coord[] coordinates) {
        this.coordinates = coordinates;
    }

    public CoordSequence(Coordinate[] copyCoords) {
        coordinates = copy(copyCoords);
    }

    public CoordSequence(CoordinateSequence coordSeq) {
        coordinates = copy(coordSeq);
    }

    public CoordSequence(int size) {
        coordinates = new Coord[size];
        for (int i = 0; i < size; i++) {
            coordinates[i] = new Coord();
        }
    }

    public int getDimension() {
        return 4;
    }

    @Override
    public int getMeasures() {
        return 1;
    }

    @Override
    public Coordinate createCoordinate() {
        return new Coord();
    }

    public Coordinate getCoordinate(int i) {
        return coordinates[i];
    }

    public Coordinate getCoordinateCopy(int index) {
        return new Coordinate(coordinates[index]);
    }

    public void getCoordinate(int index, Coordinate coord) {
        coord.x = coordinates[index].x;
        coord.y = coordinates[index].y;
        coord.setZ(coordinates[index].getZ());
        coord.setM(coordinates[index].getM());
    }

    public double getX(int index) {
        return coordinates[index].x;
    }

    public double getY(int index) {
        return coordinates[index].y;
    }

    public double getOrdinate(int index, int ordinateIndex) {
        switch (ordinateIndex) {
            case CoordinateSequence.X:
                return coordinates[index].x;
            case CoordinateSequence.Y:
                return coordinates[index].y;
            case CoordinateSequence.Z:
                return coordinates[index].getZ();
            case CoordinateSequence.M:
                return coordinates[index].getM();
        }
        return Double.NaN;
    }

    /**
     * @see org.locationtech.jts.geom.CoordinateSequence#setOrdinate(int, int,
     * double)
     */
    public void setOrdinate(int index, int ordinateIndex, double value) {
        switch (ordinateIndex) {
            case CoordinateSequence.X:
                coordinates[index].x = value;
                break;
            case CoordinateSequence.Y:
                coordinates[index].y = value;
                break;
            case CoordinateSequence.Z:
                coordinates[index].setZ(value);
                break;
            case CoordinateSequence.M:
                coordinates[index].setM(value);
                break;
        }
    }

    //@deprecated
    public Object clone() {
        return copy();
    }

    public CoordSequence copy() {
        Coord[] cloneCoordinates = new Coord[size()];
        for (int i = 0; i < coordinates.length; i++) {
            cloneCoordinates[i] = coordinates[i].copy();
        }

        return new CoordSequence(cloneCoordinates);
    }

    public int size() {
        return coordinates.length;
    }

    public Coordinate[] toCoordinateArray() {
        return coordinates;
    }

    public Envelope expandEnvelope(Envelope env) {
        for (int i = 0; i < coordinates.length; i++) {
            env.expandToInclude(coordinates[i]);
        }
        return env;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("CoordSequence [");
        for (int i = 0; i < coordinates.length; i++) {
            if (i > 0) {
                strBuf.append(", ");
            }
            strBuf.append(coordinates[i]);
        }
        strBuf.append("]");
        return strBuf.toString();
    }
}
