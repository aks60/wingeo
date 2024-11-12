package builder.geom;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;

public class Co7eSequenceFactory implements CoordinateSequenceFactory {

    private static Co7eSequenceFactory instance = new Co7eSequenceFactory();

    private Co7eSequenceFactory() {
    }

    public static Co7eSequenceFactory instance() {
        return instance;
    }

    public CoordinateSequence create(Coordinate[] coordinates) {
        return coordinates instanceof Co7e[]
                ? new Co7eSequence((Co7e[]) coordinates)
                : new Co7eSequence(coordinates);
    }

    public CoordinateSequence create(CoordinateSequence coordSeq) {
        return coordSeq instanceof Co7eSequence
                ? new Co7eSequence((Co7eSequence) coordSeq)
                : new Co7eSequence(coordSeq);
    }

    public CoordinateSequence create(int size, int dimension) {
        return new Co7eSequence(size);
    }

    @Override
    public CoordinateSequence create(int size, int dimension, int measures) {
        return new Co7eSequence(size);
    }
}
