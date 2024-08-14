package dataset;

import static domain.eArtikl.code;
import static domain.eArtikl.level1;
import static domain.eArtikl.level2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Sql {

    public static Query select(List<Record> data, Query query, Field field) {
        query.clear();
        if (Query.conf.equals("calc")) {
            query.addAll(data.stream().collect(Collectors.toList()));
        } else {
            query.select(field.fields()[0]);

        }
        return query;
    }

    public static Query select2(List<Record> data, Query query, Field field, int value) {
        query.clear();
        if (Query.conf.equals("calc")) {
            query.addAll(data.stream().filter(rec -> rec.getInt(field) == value).collect(Collectors.toList()));
        } else {
            query.select(field.fields()[0], "where", field, "=", value);

        }
        return query;
    }

    public static Query select3(List<Record> data, Query query, Field field, int value, Field field2, int value2) {
        query.clear();
        if (Query.conf.equals("calc")) {
            query.addAll(data.stream().filter(rec -> rec.getInt(field) == value && rec.getInt(field2) == value2).collect(Collectors.toList()));
        } else {
            query.select(field.fields()[0], "where", field, "=", value, "and", field2, "=", value2);

        }
        return query;
    }

    public static Query select4(List<Record> data, Query query, Field field, Field field2) {
        query.clear();
        if (Query.conf.equals("calc")) {
            query.addAll(data.stream().filter(rec
                    -> rec.getInt(level1) == 2 && (rec.getInt(level2) == 11 || rec.getInt(level2) == 12)
            ).collect(Collectors.toList()));
        } else {
            query.select(field.fields()[0], "where", field, "= 2 and", field2, "in (11,12)");

        }
        return query;
    }
}
