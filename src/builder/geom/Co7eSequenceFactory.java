package builder.geom;

import java.util.List;
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

    public CoordinateSequence create(Co7e coordinate) {
        return this.create(new Co7e[]{coordinate});
    }

    public CoordinateSequence create(List<Co7e> cooList) {
        return this.create(cooList.toArray(new Co7e[0]));
    }

    public CoordinateSequence create(Coordinate[] coordinates) {
        return coordinates instanceof Co7e[]
                ? new Co7eSequence((Co7e[]) coordinates)
                : new Co7eSequence(coordinates);
//        if (coordinates instanceof Co7e[]) {
//            return new Co7eSequence((Co7e[]) coordinates);
//        } else {
//            throw new IllegalArgumentException("Ошибка:CoordinateSequenceFactory.create(), instanceof == false ");
//        }
    }

    public CoordinateSequence create(CoordinateSequence coordSeq) {
//        return coordSeq instanceof Co7eSequence
//                ? new Co7eSequence((Co7eSequence) coordSeq)
//                : new Co7eSequence(coordSeq);
        if (coordSeq instanceof Co7eSequence) {
            return new Co7eSequence((Co7eSequence) coordSeq);
        } else {
            throw new IllegalArgumentException("Ошибка:CoordinateSequenceFactory.create(), instanceof == false ");
        }
    }

    public CoordinateSequence create(int size, int dimension) {
        return new Co7eSequence(size);
    }

    @Override
    public CoordinateSequence create(int size, int dimension, int measures) {
        return new Co7eSequence(size);
    }
}
