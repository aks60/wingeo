package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public enum eArtikl implements Field {
    up("0", "0", "0", "Материальные ценности", "ARTIKLS"),
    id("4", "10", "0", "Идентификатор", "id"),
    code("12", "32", "1", "Артикул", "ANUMB"),
    level1("5", "5", "1", "Главный тип", "ATYPM"),
    level2("5", "5", "1", "Подтип артикула", "ATYPP"),
    name("12", "96", "1", "Название", "ANAME"),
    supplier("12", "64", "1", "У поставщика", "ANAMP"),
    tech_code("12", "64", "1", "Технолог.код контейнера", "ATECH"),
    size_furn("8", "15", "1", "Фальц внешний или Фурнитурный паз", "ASIZF"),
    size_falz("8", "15", "1", "Фальц внутренний или наплав(полка)", "ASIZN"),
    size_tech("8", "15", "1", "Размер технолог. или толщина наплава(полки)", "ASIZV"),
    size_centr("8", "15", "1", "B - Смещение оси от центра", "ASIZB"),
    size_frez("8", "15", "1", "Толщина фрезы", "AFREZ"),
    len_unit("8", "15", "1", "Длина ед. поставки", "ALENG"),
    height("8", "15", "1", "Ширина", "AHEIG"),
    depth("8", "15", "1", "Толщина", "AFRIC"),
    unit("5", "5", "1", "Ед. измерения", "ATYPI"),
    density("8", "15", "1", "Удельный вес", "AMASS"),
    section("8", "15", "1", "Сечение", "ASECH"),
    noopt("5", "5", "1", "Не оптимизировать", "NOOPT"),
    ost_delov("8", "15", "1", "Деловой остаток", "AOSTD"),
    cut_perim("8", "15", "1", "Периметр сечения", "APERI"),
    min_rad("8", "15", "1", "Мин.радиус гиба", "AMINR"),
    nokom("5", "5", "1", "Доступ для выбора", "NOKOM"), // ( -2 - Только в комплектах, -1 - Только в комплектации, 0- Доступен везде, 1 - Не доступен, 2 - Только в изделиях и ввод блоков, 4 - Только в изделиях)
    noskl("5", "5", "1", "Не для склада", "NOSKL"),
    sel_color("5", "5", "1", "Подбор текстур", "ACOLL"),
    with_seal("5", "5", "1", "С уплотнением", "AWORK"),
    otx_norm("8", "15", "1", "Норма отхода", "AOUTS"),
    groups1_id("4", "10", "1", "Группы наценок", "groups1_id"),
    groups2_id("4", "10", "1", "Группы скидок", "groups2_id"),
    groups3_id("4", "10", "1", "Категория профилей", "groups3_id"),
    groups4_id("4", "10", "1", "Серия профиля", "groups4_id"),
    syssize_id("4", "10", "1", "Системные константы", "syssize_id"),
    currenc1_id("4", "10", "1", "Основная валюта", "CNUMB"),
    currenc2_id("4", "10", "1", "Неосновная валюта", "CNUMT"),
    analog_id("4", "10", "1", "Аналог профиля", "analog_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());
    private static HashMap<Integer, Record> map = new HashMap();

    eArtikl(Object... p) {
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
            map.clear();
            query.stream().forEach(rec -> map.put(rec.getInt(id), rec));
        }
        return query;
    }

    public static Record get(int id) {
        if (id == -3) {
            return virtualRec();
        }
        query();
        Record rec = map.get(id);
        return (rec == null) ? virtualRec() : rec;
    }

    public static Record find(int _id, boolean _analog) {
        if (_id == -3) {
            return virtualRec();
        }
        if (Query.conf.equals("calc")) {
            Record recordRec = query().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord());
            if (_analog == true && recordRec.get(analog_id) != null) {

                int _analog_id = recordRec.getInt(analog_id);
                recordRec = query().stream().filter(rec -> _analog_id == rec.getInt(id)).findFirst().orElse(up.newRecord());
            }
            return recordRec;
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        if (_analog == true && recordList.isEmpty() == false && recordList.get(0, analog_id) != null) {

            int _analog_id = recordList.getAs(0, analog_id);
            recordList = new Query(values()).select(up, "where", id, "=", _analog_id);
        }
        return (recordList.isEmpty() == true) ? up.newRecord() : recordList.get(0);
    }

    public static Record find2(String _code) {
        if (_code.equals("0x0x0x0")) {
            return virtualRec();
        }
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> _code.equals(rec.getStr(code))).findFirst().orElse(up.newRecord());
        }
        Query recordList = new Query(values()).select(up, "where", code, "='", _code, "'");
        return (recordList.isEmpty() == true) ? up.newRecord() : recordList.get(0);
    }

    public static List<Record> find3(int seriesID) {
        if (seriesID == -1) {
            return List.of();
        }
        if (Query.conf.equals("calc")) {
            return query().stream().filter(rec -> seriesID == rec.getInt(groups4_id)).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", groups4_id, "=", seriesID, "");
    }

    public static Record virtualRec() {
        Record record = up.newRecord();
        record.setNo(id, -3);
        record.setNo(code, "");
        record.setNo(name, "");
        //для построения типовых конструкций
        record.setNo(height, 80);
        record.setNo(size_centr, 40);
        record.setNo(tech_code, "");
        record.setNo(size_falz, 20);
        record.setNo(syssize_id, -3);
        return record;
    }

    public String toString() {
        return meta.descr();
    }
}
