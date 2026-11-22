package builder.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.index.strtree.STRtree;
import org.locationtech.jts.linearref.LengthIndexedLine;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.operation.overlayng.OverlayNG;
import org.locationtech.jts.operation.polygonize.Polygonizer;

public class UJts {

    public static List<Polygon> splitPolygon(Polygon polygon, LineString line) {
        // 1. Получаем внешнюю границу полигона (т.к. по условию дырок нет)
        Geometry boundary = polygon.getBoundary();

        // 2. Объединяем границу полигона и разрезающую линию через OverlayNG.
        // OverlayNG автоматически разобьет линии в точках пересечения (noding)
        // и линейно проинтерполирует Z-координаты для новых вершин.
        Geometry nodedLinework = OverlayNG.overlay(boundary, line, OverlayNG.UNION);

        // 3. Собираем новые полигоны из получившейся сетки линий
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(nodedLinework);

        @SuppressWarnings("unchecked")
        Collection<Polygon> polys = polygonizer.getPolygons();

        // 4. Фильтруем полигоны, оставляя только те, что лежат внутри исходного
        // (на случай, если линия выходила за пределы полигона и образовала петли снаружи)
        List<Polygon> result = new ArrayList<>();
        for (Polygon p : polys) {
            if (polygon.contains(p.getInteriorPoint())) {
                result.add(p);
            }
        }
        return result;
    }

    //Индексируем исходные сегменты с их ключами
    public static STRtree buildSegmentIndex(Polygon originalPolygon) {
        STRtree indexTree = new STRtree();
        List<LineString> originalSegments = List.of(originalPolygon.getExteriorRing());       
        for (LineString seg : originalSegments) {
            indexTree.insert(seg.getEnvelopeInternal(), seg);
        }
        indexTree.build();
        return indexTree;
    }
    public static STRtree buildSegmentIndex2(List<LineString> originalSegments) {
        STRtree index = new STRtree();
        for (LineString seg : originalSegments) {
            // Добавляем сегмент в R-tree по его ограничивающему прямоугольнику (Envelope)
            index.insert(seg.getEnvelopeInternal(), seg);
        }
        index.build();
        return index;
    }

    //После разреза полигона восстанавливаем ключи
    public static void restoreKeysAfterSplit(Polygon newPolygon, STRtree indexTree ){
        Coordinate[] coords = newPolygon.getExteriorRing().getCoordinates();
        for (int i = 0; i < coords.length - 1; i++) {
            
            LineString newSeg = Com5t.gf.createLineString(new Coordinate[]{coords[i], coords[i + 1]});
            Point midPoint = newSeg.getCentroid();          
            @SuppressWarnings("unchecked") //ищем кандидатов в индексе
            List<LineString> candidates = indexTree.query(midPoint.getEnvelopeInternal());
            for (LineString oldSeg : candidates) {

                if (oldSeg.isWithinDistance(midPoint, 1e-6)) {
                    coords[i].setZ(oldSeg.getCoordinates()[0].z);
                    break;
                }
            }
        }
    }
    public static List<LineString> restoreKeysAfterSplit2(Polygon newPolygon, STRtree originalIndex) {
        GeometryFactory gf = newPolygon.getFactory();
        Coordinate[] coords = newPolygon.getExteriorRing().getCoordinates();
        List<LineString> newSegments = new ArrayList<>();

        for (int i = 0; i < coords.length - 1; i++) {
            LineString newSeg = gf.createLineString(new Coordinate[]{coords[i], coords[i + 1]});

            // Берем середину нового сегмента (она гарантированно лежит на старом сегменте, 
            // даже если старый сегмент был разрезан пополам)
            Point midPoint = newSeg.getCentroid();

            // Ищем кандидатов в индексе
            @SuppressWarnings("unchecked")
            List<LineString> candidates = originalIndex.query(midPoint.getEnvelopeInternal());

            String matchedKey = null;
            for (LineString oldSeg : candidates) {
                // Допуск 1e-6 нужен из-за погрешностей вычислений с плавающей точкой при разрезе
                if (oldSeg.isWithinDistance(midPoint, 1e-6)) {
                    matchedKey = (String) oldSeg.getUserData();
                    break;
                }
            }

            // Если matchedKey == null, значит это сегмент самой линии разреза
            newSeg.setUserData(matchedKey != null ? matchedKey : "SPLIT_LINE");
            newSegments.add(newSeg);
        }

        return newSegments;
    }

    /**
     * Находит ближайшую точку на границах списка полигонов в пределах заданного
     * радиуса.
     *
     * @param mouseCoord Координаты курсора (в единицах карты/геометрии)
     * @param polygons Список полигонов без дыр
     * @param snapDistance Максимальная дистанция прилипания
     * @return Координата привязки или исходная координата, если рядом ничего
     * нет
     */
    public static Coordinate snapToPolygons(Coordinate mouseCoord, List<Polygon> polygons, double snapDistance) {
        Coordinate bestSnapCoord = null;
        double minDistance = snapDistance;

        // Создаем поисковый квадрат вокруг курсора один раз (это даже быстрее)
        Envelope searchEnv = new Envelope(mouseCoord);
        searchEnv.expandBy(snapDistance);

        for (Polygon poly : polygons) {
            // Проверяем пересечение bounding box'ов
            if (!poly.getEnvelopeInternal().intersects(searchEnv)) {
                continue;
            }

            LocationIndexedLine indexedLine = new LocationIndexedLine(poly.getExteriorRing());
            LinearLocation loc = indexedLine.project(mouseCoord);
            Coordinate candidate = indexedLine.extractPoint(loc);

            double dist = mouseCoord.distance(candidate);
            if (dist < minDistance) {
                minDistance = dist;
                bestSnapCoord = candidate;
            }
        }

        return bestSnapCoord != null ? bestSnapCoord : mouseCoord;
    }

    //Находит пару ближайших точек между двумя геометриями
    public static Point snapToPolygonEdge(Polygon polygon, Point targetPoint) {
        // 1. Берем только внешний контур (ребра) полигона, так как он без дыр
        LineString exteriorRing = polygon.getExteriorRing();

        // 2. Вычисляем ближайшие точки между контуром и целевой точкой
        DistanceOp distanceOp = new DistanceOp(exteriorRing, targetPoint);
        Coordinate[] closestCoords = distanceOp.nearestPoints();

        // closestCoords[0] — ближайшая координата на контуре полигона
        GeometryFactory factory = polygon.getFactory();
        return factory.createPoint(closestCoords[0]);
    }

    //Находит пару ближайших точек между двумя геометриями
    public static Coordinate snapCoordinate(Polygon polygon, Coordinate mouseCoord) {
        // Оборачиваем внешний контур в LengthIndexedLine
        LengthIndexedLine indexedLine = new LengthIndexedLine(polygon.getExteriorRing());

        // Находим индекс ближайшей точки на линии и получаем её координаты
        double index = indexedLine.project(mouseCoord);
        return indexedLine.extractPoint(index);
    }
}
