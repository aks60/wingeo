package builder.model;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.index.strtree.STRtree;
import java.util.*;

public class SplitPolygonWithKeys {

    // 1. Сначала индексируем исходные сегменты с их ключами
    public static STRtree buildSegmentIndex(List<LineString> originalSegments) {
        STRtree index = new STRtree();
        for (LineString seg : originalSegments) {
            // Добавляем сегмент в R-tree по его ограничивающему прямоугольнику (Envelope)
            index.insert(seg.getEnvelopeInternal(), seg);
        }
        index.build();
        return index;
    }

    // 2. После разреза полигона восстанавливаем ключи для новых сегментов
    public static List<LineString> restoreKeysAfterSplit(Polygon newPolygon, STRtree originalIndex) {
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
}