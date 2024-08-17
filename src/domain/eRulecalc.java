package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.ePrjkit.up;
import frames.UGui;
import java.util.List;

public enum eRulecalc implements Field {
    up("0", "0", "0", "Правила расчёта проектов", "RULECLK"),
    id("4", "10", "0", "Идентификатор", "id"),
    name("12", "64", "1", "Название правила", "RNAME"),
    type("5", "5", "1", "Тип материальных ценностей (502 - заполнение)", "RUSED"),
    coeff("8", "15", "1", "Коэффициент", "RKOEF"),
    quant("12", "96", "1", "Количество", "RLENG"),
    color1("12", "96", "1", "Коды текстур позиции (основная)", "RCODM"),
    color2("12", "96", "1", "Коды текстур позиции (??? внутренняя)", "RCOD1"),
    color3("12", "96", "1", "Коды текстур позиции (??? внешняя)", "RCOD2"),
    common("5", "5", "1", "Общее", "RALLP"),
    form("5", "5", "1", "Форма позиций", "RISKL"), //0 - не проверять форму,  10 - не прямоугольное, не арочное заполнение,  12 - не прямоугольное заполнение с арками"
    size("12", "96", "1", "Размер", "RSIZE"),
    incr("8", "15", "1", "Надбавка", "RPRIC"),
    sebes("5", "5", "1", "Флаг себестоимости", "RSEBE"),
    calk("5", "5", "1", "Калькуляция", "RCALK"),
    artikl_id("4", "10", "1", "Артикул", "artikl_id");   
    
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eRulecalc(Object... p) {
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
  
    public static List<Record> list() {
        if (Query.conf.equals("NET")) {
            return data();
        }
        return new Query(values()).select(up, "order by", id);
    }

    public String toString() {
        return meta.descr();
    }
}
