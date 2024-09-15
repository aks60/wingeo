package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.List;
import static java.util.stream.Collectors.toList;

//Не менять индекс поля, см. UColor
public enum eElemdet implements Field {
    up("0", "0", "0", "Спецификация составов", "VSTASPC"),
    id("4", "10", "0", "Идентификатор", "id"),
    color_us("5", "5", "1", "Подбор текстуры", "CTYPE"), // 0 - указана вручную 11 - профиль 31 - основная
    color_fk("4", "10", "1", "Текстура", "CLNUM"),
    artikl_id("4", "10", "1", "Артикул", "artikl_id"),
    element_id("4", "10", "0", "Вставка", "element_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eElemdet(Object... p) {
        meta.init(p);
    }

    public MetaField meta() {
        return meta;
    }

    public Field[] fields() {
        return values();
    }

    public static List<Record> find(int elementID) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(element_id) == elementID).collect(toList());
        }
        return new Query(values()).select(up, "where", element_id.name(), "=", elementID);
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

    public String toString() {
        return meta.descr();
    }
}
