package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum eElement implements Field {
    up("0", "0", "0", "Вставки", "VSTALST"),
    id("4", "10", "0", "Идентификатор", "id"),
    name("12", "96", "1", "Наименование", "VNAME"),
    typset("4", "10", "1", "Тип состава", "typset"),
    signset("12", "32", "1", "Признак состава", "VSIGN"),
    markup("8", "15", "1", "Наценка %", "VPERC"),
    todef("16", "5", "1", "Ставить по умолчанию", "todef"),
    toset("16", "5", "1", "Установка обязательности", "toset"),
    artikl_id("4", "10", "1", "Артикул", "artikl_id"),
    groups1_id("4", "10", "1", "Серия", "groups1_id"),
    groups2_id("4", "10", "0", "Категории", "groups2_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eElement(Object... p) {
        meta.init(p);
    }

    public MetaField meta() {
        return meta;
    }

    public Field[] fields() {
        return values();
    }

    public static Query query() {
        if (query.size() == 0) {
            query.select(up, "order by", id);
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public static List<Record> find(int seriesID) {
        if (seriesID == -1) {
            return new ArrayList<Record>();
        }
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> seriesID == rec.getInt(groups1_id) && rec.getInt(todef) > 0).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", groups1_id, "=", seriesID, "and", todef, "> 0");
    }

    public static List<Record> find2(int artikl2_id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> artikl2_id == rec.getInt(artikl_id) && rec.getInt(todef) > 0).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", artikl_id, "=", artikl2_id, "and", todef, "> 0");
    }

    public static List<Record> find3(int artikl2_id, int series2_id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> (artikl2_id == rec.getInt(artikl_id)
                    || series2_id == rec.getInt(groups1_id)) && rec.getInt(todef) > 0).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where (", artikl_id, "=", artikl2_id, "or", groups1_id, "=", series2_id, ") and", todef, "> 0");
    }

    public static Record find4(int _id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord());
        }
        Query recordList = new Query(values()).select(up, "where", _id, "= id");
        return (recordList.isEmpty() == true) ? up.newRecord() : recordList.get(0);
    }

    public static List<Record> find5(Record recordSer) {
        int artiklID = recordSer.getInt(eArtikl.id);
        int seriesID = recordSer.getInt(eArtikl.groups4_id);
        if (seriesID == -1) {
            return new ArrayList<Record>();
        }
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> seriesID == rec.getInt(groups1_id)
                    && artiklID != rec.getInt(artikl_id) && rec.getInt(todef) > 0).collect(Collectors.toList());
        }
        return  new Query(values()).select(up, "where", groups1_id, "=", seriesID, "and", artiklID, "!= artikl_id and", todef, "> 0");
    }

    public String toString() {
        return meta.descr();
    }
}
