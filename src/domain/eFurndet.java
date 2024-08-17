package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.up;
import static domain.eColor.up;
import static domain.eGroups.up;
import frames.UGui;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Не менять индекс поля, см. UColor
public enum eFurndet implements Field {
    up("0", "0", "0", "Спецификация фурнитуры", "FURNSPC"),
    id("4", "10", "0", "Идентификатор", "FINCB"),
    color_us("5", "5", "1", "Подбор текстуры", "CTYPE"),
    color_fk("4", "10", "1", "Текстура", "CLNUM"),
    artikl_id("4", "10", "1", "Артикул", "artikl_id"),
    furniture_id1("4", "10", "1", "Фурнитура", "furniture_id1"),
    furniture_id2("4", "10", "1", "Набор", "furniture_id2"),
    pk("4", "10", "0", "Идентификатор родителя", "FINCB"),
    furndet_pk("4", "10", "0", "Ссылка на родителя", "FINCS");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eFurndet(Object... p) {
        meta.init(p);
    }

    public MetaField meta() {
        return meta;
    }

    public Field[] fields() {
        return values();
    }

    public static Query data() {
        if (query.size() == 0) {
            query.select(up, "order by", id);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public Query query() {
        return query;
    }

    public static void sql(Query q, int furnID, int lev1, int lev2) {
        if (Query.conf.equals("NET")) {
            List<Integer> out = new ArrayList();
            List<Record> listArt = eArtikl.data().stream().filter(rec -> rec.getInt(eArtikl.level1) == lev1
                    && rec.getInt(eArtikl.level2) == lev2).collect(Collectors.toList());
            for (Record recordFurn : data()) {
                if (recordFurn.getInt(furniture_id1) == furnID) {
                    for (Record recordArt : listArt) {
                        if (recordFurn.getInt(artikl_id) == recordArt.getInt(eArtikl.id)) {
                            q.add(recordFurn);
                            q.table(eArtikl.up).add(recordArt);
                        }
                    }
                }
            }
        } else {
            q.select(up, "left join", eArtikl.up, "on", eArtikl.id, "=", artikl_id,
                    "where", furniture_id1, "=", furnID, "and", eArtikl.level1, "=", lev1, "and", eArtikl.level2, "=", lev2);
        }
    }

    public static List<Record> find(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(furniture_id1) == _id).collect(Collectors.toList());
        }
        Query recordList = new Query(values()).select(up, "where", furniture_id1, "=", _id);
        return (recordList.isEmpty() == true) ? new ArrayList<Record>() : recordList;
    }

    public String toString() {
        return meta.descr();
    }
}
