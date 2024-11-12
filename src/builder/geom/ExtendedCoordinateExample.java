/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package builder.geom;

import builder.model.Com5t;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

/**
 * @version 1.7
 */
public class ExtendedCoordinateExample {

    public ExtendedCoordinateExample() {
    }

    public static void main(String args[]) {
        Co7eSequenceFactory seqFact = Co7eSequenceFactory.instance();
        
        Co7e[] array1 = new Co7e[]{
            new Co7e(0, 0, 0, 91),
            new Co7e(10, 0, 0, 92),
            new Co7e(10, 10, 0, 93),
            new Co7e(0, 10, 0, 94),
            new Co7e(0, 0, 0, 91),};
        CoordinateSequence seq1 = seqFact.create(array1);

        CoordinateSequence seq2 = seqFact.create(
                new Co7e[]{
                    new Co7e(5, 5, 0, 91),
                    new Co7e(15, 5, 0, 92),
                    new Co7e(15, 15, 0, 93),
                    new Co7e(5, 15, 0, 94),
                    new Co7e(5, 5, 0, 91),});

        GeometryFactory fact = new GeometryFactory(
                Co7eSequenceFactory.instance());

        Geometry g1 = fact.createPolygon(fact.createLinearRing(seq1), null);
        Geometry g2 = fact.createPolygon(fact.createLinearRing(seq2), null);
        Coordinate cx[] = g2.getCoordinates();
        Coordinate c = cx[0];
        c.setM(777);
        System.out.println("M = " + c.getM());

        System.out.println("WKT for g1: " + g1);
        System.out.println("Internal rep for g1: " + ((Polygon) g1).getExteriorRing().getCoordinateSequence());

        System.out.println("WKT for g2: " + g2);
        System.out.println("Internal rep for g2: " + ((Polygon) g2).getExteriorRing().getCoordinateSequence());

        Geometry gInt = g1.intersection(g2);

        System.out.println("WKT for gInt: " + gInt);
        System.out.println("Internal rep for gInt: " + ((Polygon) gInt).getExteriorRing().getCoordinateSequence());
    }
}
