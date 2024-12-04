package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.up;
import frames.UGui;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

public enum eArtdet implements Field {
    up("0", "0", "0", "Тариф.мат.ценности", "ARTSVST"),
    id("4", "10", "0", "Идентификатор", "id"),
    cost_unit("8", "15", "1", "Тариф единицы измерения", "CLPRV"),
    cost_c1("8", "15", "1", "Тариф основной текстуры", "CLPRC"),
    cost_c2("8", "15", "1", "Тариф внутренний текстуры", "CLPR1"),
    cost_c3("8", "15", "1", "Тариф внешний текстуры", "CLPR2"),
    cost_c4("8", "15", "1", "Тариф двухсторонний текстуры", "CLPRA"),
    mark_c1("16", "5", "1", "Галочка основной текстуры", "mark_c1"),
    mark_c2("16", "5", "1", "Галочка внутренний текстуры", "mark_c2"),
    mark_c3("16", "5", "1", "Галочка внешний текстуры", "mark_c3"),
    coef("8", "15", "1", "Ценовой коэффицент", "KNAKL"),
    cost_min("8", "15", "1", "Минимальный тариф", "CMINP"),
    artikl1("12", "32", "1", "Артикул склада", "ASKL1"),
    artikl2("12", "32", "1", "Артикул 1С", "ASKL2"),
    color_fk("4", "10", "1", "Текстура на id_Группы или id_Текстуры", "color_fk"),
    artikl_id("4", "10", "0", "Артикул", "artikl_id");

    //000-0 - нет галочек (по всем текстурам этот цвет не основной для материала), 
    //001-1 - галочка на внутренней текстуре, 
    //010-2 - галочка на внешней текстуре, 
    //011-3 - галочки на внутренней и внешней текстурах, 
    //100-4 - галочка на основной текстуре, 
    //101-5 - галочки на основной и внутреней текстурах, 
    //110-6 - галочки на основной и внешней текстурах, 
    //111-7 - галочки на всех текстурах (по всем текстурам основной цвет).       
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eArtdet(Object... p) {
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
    
    public static Record find(int _id) {
        if (_id == -3) {
            return record();
        }
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(artikl_id) == _id).findFirst().orElse(record());
        }
        List<Record> record = new Query(values()).select("select first 1 * from " + up.tname() + " where " + artikl_id.name() + " = " + _id);
        return (record.size() == 0) ? record() : record.get(0);
    }
    
    public static List<Record> filter(int artiklID) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> rec.getInt(artikl_id) == artiklID).collect(toList());
        }
        return new Query(values()).select(up, "where", artikl_id, "=", artiklID, "order by", id);
    }
    
    public static List<Record> filter2(int artiklID) {

        return eArtdet.data().stream().filter(rec
                -> artiklID == rec.getInt(eArtdet.artikl_id)
                && rec.getInt(eArtdet.color_fk) > 0).collect(Collectors.toList());
    }

    public static Record record() {
        Record record = up.newRecord(Query.SEL);
        record.setNo(id, -3);
        record.setNo(artikl_id, -3);
        record.setNo(color_fk, -3);
        return record;
    }

    public String toString() {
        return meta.descr();
    }
}
