package common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.operation.polygonize.Polygonizer;

//https://gis.stackexchange.com/questions/189976/jts-split-arbitrary-polygon-by-a-line
public class PolygonTools {

  public static Geometry polygonize(Geometry geometry) {
      List lines = LineStringExtracter.getLines(geometry);
      Polygonizer polygonizer = new Polygonizer();
      polygonizer.add(lines);
      Collection polys = polygonizer.getPolygons();
      Polygon[] polyArray = GeometryFactory.toPolygonArray(polys);
      return geometry.getFactory().createGeometryCollection(polyArray);
  }

  public static Geometry splitPolygon(Geometry poly, Geometry line) {
      Geometry nodedLinework = poly.getBoundary().union(line);
      Geometry polys = polygonize(nodedLinework);

      // Only keep polygons which are inside the input
      List output = new ArrayList();
      for (int i = 0; i < polys.getNumGeometries(); i++) {
          Polygon candpoly = (Polygon) polys.getGeometryN(i);
          if (poly.contains(candpoly.getInteriorPoint())) {
              output.add(candpoly);
          }
      }
      return poly.getFactory().createGeometryCollection(GeometryFactory.toGeometryArray(output));
  }
} 
