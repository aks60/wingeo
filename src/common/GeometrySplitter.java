
package common;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeometrySplitter {

    /**
     * Splits a given Polygon by a LineString.
     * 
     * @param polygon The target polygon to be split.
     * @param line The splitting line.
     * @return A list of sub-polygons resulting from the split.
     */
    public static List<Polygon> splitPolygon(Polygon polygon, LineString line) {
        List<Polygon> output = new ArrayList<>();

        // 1. Get the boundary of the polygon (handles holes automatically)
        Geometry boundary = polygon.getBoundary();

        // 2. Union the boundary with the line to node the intersections
        Geometry nodedLinework = boundary.union(line);

        // 3. Use Polygonizer to assemble new polygons from the noded line segments
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(nodedLinework);
        
        @SuppressWarnings("unchecked")
        Collection<Polygon> polys = polygonizer.getPolygons();

        // 4. Filter out any polygons that fall outside the original polygon boundary
        for (Polygon p : polys) {
            // Check if the center/interior point of the new polygon lies inside the original
            if (polygon.contains(p.getInteriorPoint())) {
                output.add(p);
            }
        }

        return output;
    }
}
