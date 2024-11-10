package builder.geom;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;

public class CoordSequenceFactory implements CoordinateSequenceFactory {

    private static CoordSequenceFactory instance = new CoordSequenceFactory();

    private CoordSequenceFactory() {
    }

    public static CoordSequenceFactory instance() {
        return instance;
    }

    public CoordinateSequence create(Coordinate[] coordinates) {
        return coordinates instanceof Coord[]
                ? new CoordSequence((Coord[]) coordinates)
                : new CoordSequence(coordinates);
    }

    public CoordinateSequence create(CoordinateSequence coordSeq) {
        return coordSeq instanceof CoordSequence
                ? new CoordSequence((CoordSequence) coordSeq)
                : new CoordSequence(coordSeq);
    }

    public CoordinateSequence create(int size, int dimension) {
        return new CoordSequence(size);
    }

    @Override
    public CoordinateSequence create(int size, int dimension, int measures) {
        return new CoordSequence(size);
    }
}
