/*
 * Copyright (c) 2019 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package builder.model;

import static builder.model.Com5t.gf;
import domain.eArtikl;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.buffer.BufferParameters;
import startup.Test;

/**
 * Creates a buffer polygon with a varying buffer distance at each vertex along
 * a line. Vertex distances may be zero.
 * <p>
 * Only single linestrings are supported as input, since buffer widths are
 * typically specified individually for each line.
 *
 * @author Martin Davis
 *
 */
//
//��. VariableBuffer ������ - 1.20
//
public class VBuffer {

    private static final int MIN_CAP_SEG_LEN_FACTOR = 4;

    /**
     * Creates a buffer polygon along a line with the buffer distance
     * interpolated between a start distance and an end distance.
     *
     * @param line the line to buffer
     * @param startDistance the buffer width at the start of the line
     * @param endDistance the buffer width at the end of the line
     * @return the variable-distance buffer polygon
     */
    public static Geometry buffer(Geometry line, double startDistance, double endDistance) {
        double[] distance = interpolate((LineString) line, startDistance, endDistance);
        VBuffer vb = new VBuffer(line, distance);
        return vb.getResult();
    }

    /**
     * Creates a buffer polygon along a line with the buffer distance
     * interpolated between a start distance, a middle distance and an end
     * distance. The middle distance is attained at the vertex at or just past
     * the half-length of the line. For smooth buffering of a {@link LinearRing}
     * (or the rings of a {@link Polygon}) the start distance and end distance
     * should be equal.
     *
     * @param line the line to buffer
     * @param startDistance the buffer width at the start of the line
     * @param midDistance the buffer width at the middle vertex of the line
     * @param endDistance the buffer width at the end of the line
     * @return the variable-distance buffer polygon
     */
    public static Geometry buffer(Geometry line, double startDistance, double midDistance, double endDistance) {
        double[] distance = interpolate((LineString) line,
                startDistance, midDistance, endDistance);
        VBuffer vb = new VBuffer(line, distance);
        return vb.getResult();
    }

    /**
     * Creates a buffer polygon along a line with the distance specified at each
     * vertex.
     *
     * @param line the line to buffer
     * @param distance the buffer distance for each vertex of the line
     * @return the variable-distance buffer polygon
     */
    public static Geometry buffer(Geometry line, double[] distance) {
        VBuffer vb = new VBuffer(line, distance);
        return vb.getResult();
    }

    //��� ��� �������
    public static Polygon buffer(Geometry line, ArrayList<? extends Com5t> list, double amend, int opt) {

        //Map ���������
        Map<Double, Double> hm = new HashMap();
        for (Com5t el : list) {
            dataset.Record rec = (el.artiklRec == null) ? eArtikl.virtualRec() : el.artiklRec;
            if (opt == 0) {
                hm.put(el.id, rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr) + amend);
            } else if (opt == 1) {
                hm.put(el.id, rec.getDbl(eArtikl.height) - rec.getDbl(eArtikl.size_centr) - rec.getDbl(eArtikl.size_falz) + amend);
            }
        }
        //hm.put(2.0, 8.0);
        //hm.put(3.0, 280.0);

        //Array ���������
        Coordinate coo[] = line.getGeometryN(0).getCoordinates();
        double distance[] = new double[coo.length];
        for (int i = 0; i < coo.length; ++i) {
            distance[i] = hm.get(coo[i].z);
        }
        LineString ls = line.getFactory().createLineString(line.getCoordinates());
        VBuffer vb = new VBuffer(ls, distance);
        Geometry geo = vb.getResult();
        return ringToPolygon(line, geo);
    }

    //��� ��� �������
    public static Polygon ringToPolygon(Geometry line, Geometry geom) {

        Coordinate coo[] = line.getGeometryN(0).getCoordinates();
        LinearRing ring = ((Polygon) geom).getInteriorRingN(0);
        Polygon poly = (Polygon) gf.createPolygon(ring).norm();
        Coordinate cor[] = poly.getCoordinates();
        for (int i = 0; i < cor.length - 1; ++i) {
            cor[i].z = coo[i].z;
        }
        cor[cor.length - 1].z = cor[0].z;
        return (Polygon) gf.createPolygon(cor);
    }

    /**
     * Computes a list of values for the points along a line by interpolating
     * between values for the start and end point. The interpolation is based on
     * the distance of each point along the line relative to the total line
     * length.
     *
     * @param line the line to interpolate along
     * @param startValue the start value
     * @param endValue the end value
     * @return the array of interpolated values
     */
    private static double[] interpolate(LineString line,
            double startValue,
            double endValue) {
        startValue = Math.abs(startValue);
        endValue = Math.abs(endValue);
        double[] values = new double[line.getNumPoints()];
        values[0] = startValue;
        values[values.length - 1] = endValue;

        double totalLen = line.getLength();
        Coordinate[] pts = line.getCoordinates();
        double currLen = 0;
        for (int i = 1; i < values.length - 1; i++) {
            double segLen = pts[i].distance(pts[i - 1]);
            currLen += segLen;
            double lenFrac = currLen / totalLen;
            double delta = lenFrac * (endValue - startValue);
            values[i] = startValue + delta;
        }
        return values;
    }

    /**
     * Computes a list of values for the points along a line by interpolating
     * between values for the start, middle and end points. The interpolation is
     * based on the distance of each point along the line relative to the total
     * line length. The middle distance is attained at the vertex at or just
     * past the half-length of the line.
     *
     * @param line the line to interpolate along
     * @param startValue the start value
     * @param midValue the start value
     * @param endValue the end value
     * @return the array of interpolated values
     */
    private static double[] interpolate(LineString line,
            double startValue,
            double midValue,
            double endValue) {
        startValue = Math.abs(startValue);
        midValue = Math.abs(midValue);
        endValue = Math.abs(endValue);

        double[] values = new double[line.getNumPoints()];
        values[0] = startValue;
        values[values.length - 1] = endValue;

        Coordinate[] pts = line.getCoordinates();
        double lineLen = line.getLength();
        int midIndex = indexAtLength(pts, lineLen / 2);

        double delMidStart = midValue - startValue;
        double delEndMid = endValue - midValue;

        double lenSM = length(pts, 0, midIndex);
        double currLen = 0;
        for (int i = 1; i <= midIndex; i++) {
            double segLen = pts[i].distance(pts[i - 1]);
            currLen += segLen;
            double lenFrac = currLen / lenSM;
            double val = startValue + lenFrac * delMidStart;
            values[i] = val;
        }

        double lenME = length(pts, midIndex, pts.length - 1);
        currLen = 0;
        for (int i = midIndex + 1; i < values.length - 1; i++) {
            double segLen = pts[i].distance(pts[i - 1]);
            currLen += segLen;
            double lenFrac = currLen / lenME;
            double val = midValue + lenFrac * delEndMid;
            values[i] = val;
        }
        return values;
    }

    private static int indexAtLength(Coordinate[] pts, double targetLen) {
        double len = 0;
        for (int i = 1; i < pts.length; i++) {
            len += pts[i].distance(pts[i - 1]);
            if (len > targetLen) {
                return i;
            }
        }
        return pts.length - 1;
    }

    private static double length(Coordinate[] pts, int i1, int i2) {
        double len = 0;
        for (int i = i1 + 1; i <= i2; i++) {
            len += pts[i].distance(pts[i - 1]);
        }
        return len;
    }

    private LineString line;
    private double[] distance;
    private GeometryFactory geomFactory;
    private int quadrantSegs = BufferParameters.DEFAULT_QUADRANT_SEGMENTS;

    /**
     * Creates a generator for a variable-distance line buffer.
     *
     * @param line the linestring to buffer
     * @param distance the buffer distance for each vertex of the line
     */
    public VBuffer(Geometry line, double[] distance) {
        this.line = (LineString) line;
        this.distance = distance;
        geomFactory = line.getFactory();

        if (distance.length != this.line.getNumPoints()) {
            throw new IllegalArgumentException("Number of distances is not equal to number of vertices");
        }
    }

    /**
     * Computes the variable buffer polygon.
     *
     * @return a buffer polygon
     */
    //��� ��� �������
    public Geometry getResult() {
        List<Geometry> parts = new ArrayList<Geometry>();

        Coordinate[] cooShell = line.getCoordinates();
        // construct segment buffers
        for (int i = 1; i < cooShell.length; i++) {
            double dist0 = distance[i - 1];
            if (dist0 > 0) {
                Polygon poly = segmentBuffer(cooShell[i - 1], cooShell[i], dist0, dist0);
                if (poly != null) {
                    parts.add(poly);
                }
            }
        }

        GeometryCollection partsGeom = geomFactory
                .createGeometryCollection(GeometryFactory.toGeometryArray(parts));
        Geometry buffer = partsGeom.union();

        //-- ensure an empty polygon is returned if needed
        if (buffer.isEmpty()) {
            return geomFactory.createPolygon();
        }
        return buffer;
    }

    /**
     * Computes a variable buffer polygon for a single segment, with the given
     * endpoints and buffer distances. The individual segment buffers are
     * unioned to form the final buffer. If one distance is zero, the end cap at
     * that segment end is the endpoint of the segment. If both distances are
     * zero, no polygon is returned.
     *
     * @param p0 the segment start point
     * @param p1 the segment end point
     * @param dist0 the buffer distance at the start point
     * @param dist1 the buffer distance at the end point
     * @return the segment buffer, or null if void
     */
    private Polygon segmentBuffer(Coordinate p0, Coordinate p1, double dist0, double dist1) {
        /**
         * Skip buffer polygon if both distances are zero
         */
        if (dist0 <= 0 && dist1 <= 0) {
            return null;
        }

        /**
         * Generation algorithm requires increasing distance, so flip if needed
         */
        if (dist0 > dist1) {
            return segmentBufferOriented(p1, p0, dist1, dist0);
        }
        return segmentBufferOriented(p0, p1, dist0, dist1);
    }

//��� ��� �������    
    private Polygon segmentBufferOriented(Coordinate p0, Coordinate p1, double dist0, double dist1) {
//        CoordinateList coords = new CoordinateList();
//        LineSegment segm1 = new LineSegment(p0, p1);
//        LineSegment segm2 = segm1.offset(-dist0);
//        coords.addAll(List.of(segm1.p0, segm2.p0,segm2.p1, segm1.p1, segm1.p0));
        
        //-- Assert: dist0 <= dist1

        //-- forward tangent line
        LineSegment tangent = outerTangent(p0, dist0, p1, dist1);

        //-- if tangent is null then compute a buffer for largest circle
        if (tangent == null) {
            Coordinate center = p0;
            double dist = dist0;
            if (dist1 > dist0) {
                center = p1;
                dist = dist1;
            }
            return circle(center, dist);
        }

        //-- reverse tangent line on other side of segment
        LineSegment tangentReflect = reflect(tangent, p0, p1, dist0);

        CoordinateList coords = new CoordinateList();
        //-- end cap
        addCap(p1, dist1, tangent.p1, tangentReflect.p1, coords);
        //-- start cap
        addCap(p0, dist0, tangentReflect.p0, tangent.p0, coords);

        coords.closeRing();

        Coordinate[] pts = coords.toCoordinateArray();
        Polygon polygon = geomFactory.createPolygon(pts);
        return polygon;
    }

    private LineSegment reflect(LineSegment seg, Coordinate p0, Coordinate p1, double dist0) {
        LineSegment line = new LineSegment(p0, p1);
        Coordinate r0 = line.reflect(seg.p0);
        Coordinate r1 = line.reflect(seg.p1);
        //-- avoid numeric jitter if first distance is zero (second dist must be > 0)
        if (dist0 == 0) {
            r0 = p0.copy();
        }
        return new LineSegment(r0, r1);
    }

    /**
     * Returns a circular polygon.
     *
     * @param center the circle center point
     * @param radius the radius
     * @return a polygon, or null if the radius is 0
     */
    private Polygon circle(Coordinate center, double radius) {
        if (radius <= 0) {
            return null;
        }
        int nPts = 4 * quadrantSegs;
        Coordinate[] pts = new Coordinate[nPts + 1];
        double angInc = Math.PI / 2 / quadrantSegs;
        for (int i = 0; i < nPts; i++) {
            pts[i] = projectPolar(center, radius, i * angInc);
        }
        pts[pts.length - 1] = pts[0].copy();
        return geomFactory.createPolygon(pts);
    }

    /**
     * Adds a semi-circular cap CCW around the point p.
     * <>p> The vertices in caps are generated at fixed angles around a point.
     * This allows caps at the same point to share vertices, which reduces
     * artifacts when the segment buffers are merged.
     *
     * @param p the centre point of the cap
     * @param r the cap radius
     * @param t1 the starting point of the cap
     * @param t2 the ending point of the cap
     * @param coords the coordinate list to add to
     */
    private void addCap(Coordinate p, double r, Coordinate t1, Coordinate t2, CoordinateList coords) {
        //-- if radius is zero just copy the vertex
        if (r == 0) {
            coords.add(p.copy(), false);
            return;
        }

        coords.add(t1, false);

        double angStart = Angle.angle(p, t1);
        double angEnd = Angle.angle(p, t2);
        if (angStart < angEnd) {
            angStart += 2 * Math.PI;
        }

        int indexStart = capAngleIndex(angStart);
        int indexEnd = capAngleIndex(angEnd);

        double capSegLen = r * 2 * Math.sin(Math.PI / 4 / quadrantSegs);
        double minSegLen = capSegLen / MIN_CAP_SEG_LEN_FACTOR;

        for (int i = indexStart; i >= indexEnd; i--) {
            //-- use negative increment to create points CW
            double ang = capAngle(i);
            Coordinate capPt = projectPolar(p, r, ang);

            boolean isCapPointHighQuality = true;
            /**
             * Due to the fixed locations of the cap points, a start or end cap
             * point might create a "reversed" segment to the next tangent
             * point. This causes an unwanted narrow spike in the buffer curve,
             * which can cause holes in the final buffer polygon. These checks
             * remove these points.
             */
            if (i == indexStart
                    && Orientation.CLOCKWISE != Orientation.index(p, t1, capPt)) {
                isCapPointHighQuality = false;
            } else if (i == indexEnd
                    && Orientation.COUNTERCLOCKWISE != Orientation.index(p, t2, capPt)) {
                isCapPointHighQuality = false;
            }

            /**
             * Remove short segments between the cap and the tangent segments.
             */
            if (capPt.distance(t1) < minSegLen) {
                isCapPointHighQuality = false;
            } else if (capPt.distance(t2) < minSegLen) {
                isCapPointHighQuality = false;
            }

            if (isCapPointHighQuality) {
                coords.add(capPt, false);
            }
        }

        coords.add(t2, false);
    }

    /**
     * Computes the actual angle for a cap angle index.
     *
     * @param index the cap angle index
     * @return the angle
     */
    private double capAngle(int index) {
        double capSegAng = Math.PI / 2 / quadrantSegs;
        return index * capSegAng;
    }

    /**
     * Computes the canonical cap point index for a given angle. The angle is
     * rounded down to the next lower index.
     * <p>
     * In order to reduce the number of points created by overlapping end caps,
     * cap points are generated at the same locations around a circle. The index
     * is the index of the points around the circle, with 0 being the point at
     * (1,0). The total number of points around the circle is
     * <code>4 * quadrantSegs</code>.
     *
     * @param ang the angle
     * @return the index for the angle.
     */
    private int capAngleIndex(double ang) {
        double capSegAng = Math.PI / 2 / quadrantSegs;
        int index = (int) (ang / capSegAng);
        return index;
    }

    /**
     * Computes the two circumference points defining the outer tangent line
     * between two circles. The tangent line may be null if one circle mostly
     * overlaps the other.
     * <p>
     * For the algorithm see
     * <a href='https://en.wikipedia.org/wiki/Tangent_lines_to_circles#Outer_tangent'>Wikipedia</a>.
     *
     * @param c1 the centre of circle 1
     * @param r1 the radius of circle 1
     * @param c2 the centre of circle 2
     * @param r2 the center of circle 2
     * @return the outer tangent line segment, or null if none exists
     */
    private static LineSegment outerTangent(Coordinate c1, double r1, Coordinate c2, double r2) {
        /**
         * If distances are inverted then flip to compute and flip result back.
         */
        if (r1 > r2) {
            LineSegment seg = outerTangent(c2, r2, c1, r1);
            return new LineSegment(seg.p1, seg.p0);
        }
        double x1 = c1.getX();
        double y1 = c1.getY();
        double x2 = c2.getX();
        double y2 = c2.getY();
        // TODO: handle r1 == r2?
        double a3 = -Math.atan2(y2 - y1, x2 - x1);

        double dr = r2 - r1;
        double d = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

        double a2 = Math.asin(dr / d);
        // check if no tangent exists
        if (Double.isNaN(a2)) {
            return null;
        }

        double a1 = a3 - a2;

        double aa = Math.PI / 2 - a1;
        double x3 = x1 + r1 * Math.cos(aa);
        double y3 = y1 + r1 * Math.sin(aa);
        double x4 = x2 + r2 * Math.cos(aa);
        double y4 = y2 + r2 * Math.sin(aa);

        return new LineSegment(x3, y3, x4, y4);
    }

    private static Coordinate projectPolar(Coordinate p, double r, double ang) {
        double x = p.getX() + r * snapTrig(Math.cos(ang));
        double y = p.getY() + r * snapTrig(Math.sin(ang));
        return new Coordinate(x, y);
    }

    private static final double SNAP_TRIG_TOL = 1e-6;

    /**
     * Snap trig values to integer values for better consistency.
     *
     * @param x the result of a trigonometric function
     * @return x snapped to the integer interval
     */
    private static double snapTrig(double x) {
        if (x > (1 - SNAP_TRIG_TOL)) {
            return 1;
        }
        if (x < (-1 + SNAP_TRIG_TOL)) {
            return -1;
        }
        if (Math.abs(x) < SNAP_TRIG_TOL) {
            return 0;
        }
        return x;
    }
}
