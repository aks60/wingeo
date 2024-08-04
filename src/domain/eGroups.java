package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import enums.TypeGrup;
import java.util.stream.Collectors;

public enum eGroups implements Field {
    up("0", "0", "0", "Справочники", "EMPTY"),
    id("4", "10", "0", "Идентификатор", "id"),
    npp("4", "10", "1", "Номер п/п", "npp"),
    grup("5", "5", "0", ""
            + "1-Параметры пользов. \n"
            + "2-Группы текстур \n"
            + "3-Серии МЦ  \n"
            + "4-Наценки МЦ \n"
            + "5-ССкидки МЦ \n"
            + "6-Категогии МЦ \n"
            + "7-Параметры соотв.цветов \n"
            + "8-Категории вставок \n"
            + "9-Расчётные данные \n"
            + "10-Категории комплектов", "grup"),
    name("12", "96", "1", "Название группы", "name"),
    val("8", "15", "1", "Значение", "coeff");
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eGroups(Object... p) {
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

    public Query getQuery() {
        return query;
    }

    public static void select2(Query q) {
        q.clear();
        if (Query.conf.equals("calc")) {
            q.addAll(query().stream().sorted((o1, o2) -> {
                return o1.getStr(name).compareTo(o2.getStr(name));
            }).collect(Collectors.toList()));
        } else {
            q.select(up, "order by", name);
        }
    }

    public static void select3(Query q) {
        q.clear();
        if (Query.conf.equals("calc")) {
            q.addAll(query().stream().filter(rec -> rec.getInt(grup) == TypeGrup.COLOR_GRP.id).sorted((o1, o2) -> {
                if (o1.getInt(npp) == o2.getInt(npp)) {
                    return o1.getStr(name).compareTo(o2.getStr(name));
                } else {
                    return (o1.getInt(npp) > o2.getInt(npp)) ? 1 : -1;
                }
            }).collect(Collectors.toList()));
        } else {
            q.select(up, "where", eGroups.grup, "=", TypeGrup.COLOR_GRP.id, "order by", eGroups.npp, ",", eGroups.name);
        }
    }

    public static void select4(Query q) {
        q.clear();
        if (Query.conf.equals("calc")) {
            q.addAll(query().stream().filter(rec -> rec.getInt(grup) == TypeGrup.PARAM_USER.id
                    || rec.getInt(grup) == TypeGrup.COLOR_MAP.id).collect(Collectors.toList()));
        } else {
            q.select(up, "where", grup, "=", TypeGrup.PARAM_USER.id, "or", grup, "=", TypeGrup.COLOR_MAP.id);
        }
    }

    public static void select5(Query q) {
        q.clear();
        if (Query.conf.equals("calc")) {
            q.addAll(query().stream().collect(Collectors.toList()));
        } else {
            q.select(up);
        }
    }

    public static void select6(Query q) {
        q.clear();
        if (Query.conf.equals("calc")) {
            q.addAll(query().stream().filter(rec -> rec.getInt(grup) == TypeGrup.COLOR_GRP.id).sorted((o1, o2) -> {
                if (o1.getInt(npp) == o2.getInt(npp)) {
                    return o1.getStr(name).compareTo(o2.getStr(name));
                } else {
                    return (o1.getInt(npp) > o2.getInt(npp)) ? 1 : -1;
                }
            }).collect(Collectors.toList()));
        } else {
            q.select(up, "where", grup, "=", TypeGrup.CATEG_VST.id, "order by", npp, ",", name);
        }
    }

    public static void select7(Query q) {
        q.clear();
        if (Query.conf.equals("calc")) {
            q.addAll(query().stream().filter(rec -> 
                    rec.getInt(grup) == TypeGrup.SERI_ELEM.id ||
                    rec.getInt(grup) == TypeGrup.PARAM_USER.id ||
                    rec.getInt(grup) == TypeGrup.COLOR_MAP.id).sorted((o1, o2) -> {
                if (o1.getInt(npp) == o2.getInt(npp)) {
                    return o1.getStr(name).compareTo(o2.getStr(name));
                } else {
                    return (o1.getInt(npp) > o2.getInt(npp)) ? 1 : -1;
                }
            }).collect(Collectors.toList()));
        } else {
            q.select(up, "where", eGroups.grup, "in (", TypeGrup.SERI_ELEM.id,
                ",", TypeGrup.PARAM_USER.id, ",", TypeGrup.COLOR_MAP.id, ") order by", eGroups.npp, ",", eGroups.name);
        }
    }
    
    public static void select8(Query q) {
        q.clear();
        if (Query.conf.equals("calc")) {
            q.addAll(query().stream().filter(rec -> 
                    rec.getInt(grup) == TypeGrup.PARAM_USER.id ||
                    rec.getInt(grup) == TypeGrup.COLOR_MAP.id).collect(Collectors.toList()));
        } else {
            q.select(up, "where", grup, "in(", TypeGrup.COLOR_MAP.id, ",", TypeGrup.PARAM_USER.id, ")");
        }
    }
    
//    public static void select9(Query q) {
//        q.clear();
//        if (Query.conf.equals("calc")) {
//            q.addAll(query().stream().filter(rec -> 
//                    rec.getInt(grup) == TypeGrup.PARAM_USER.id ||
//                    rec.getInt(grup) == TypeGrup.COLOR_MAP.id).collect(Collectors.toList()));
//        } else {
//            q.select(up, "where", eGroups.grup, " in (" + TypeGrup.PARAM_USER.id, ",", TypeGrup.COLOR_MAP.id + ")");
//        }
//    }
    
    public static Record find(int _id) {
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "='", _id, "'");
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public String toString() {
        return meta.descr();
    }
}
