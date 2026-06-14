package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;

public enum eProject implements Field {
    up("0", "0", "0", "Заказы проектов", "LISTPRJ"),
    id("4", "10", "0", "Идентификатор", "id"),
    num_ord("12", "32", "1", "Номер заказа", "ZNUMB"),
    num_acc("12", "32", "1", "Номер счета", "INUMB"),         
    login("12", "64", "1", "User", "MNAME"), //это user который создаёт проект
    square("8", "15", "1", "0", "Площадь изделий", "PSQRA"),
    weight("8", "15", "1", "0", "Вес изделий", "EMPTY"),   
    type_calc("5", "5", "1", "Тип расчтета", "PTYPE"),
    type_norm("5", "5", "1", "Учет норм отходов", "CTYPE"),         
    disc_win("8", "15", "1", "0", "Cкидка на конструкции", "KDESC"),
    disc_kit("8", "15", "1", "0", "Cкидка на комплектацию", "ADESC"),
    disc_all("8", "15", "1", "0", "Cкидка общая", "PDESC"),    
    cost1_win("8", "15", "1", "0", "Cтоимость конструкций без скидки", "KPRIC"),   
    cost2_win("8", "15", "1", "0", "Cтоимость конструкции со скидками", "KPRCD"),    
    cost1_kit("8", "15", "1", "0", "Cтоимость комплектации без скидки", "APRIC"),    
    cost2_kit("8", "15", "1", "0", "Cтоимость комплектации со скидками", "APRCD"),     
    costpric("8", "15", "1", "0", "Cебестоимость проекта с отходом", "PSEBE"),     
    markup("8", "15", "1", "0", "Наценка %", "PMARG"),  
    flag1("5", "5", "1", "Отправка в производство", "PWORK"),   
    date4("93", "19", "1", "Дата регистрации заказа", "PDATE"),
    date5("93", "19", "1", "Дата расчета заказа", "CDATE"),
    date6("93", "19", "1", "Дата отпр. в производство", "WDATE"),
    currenc_id("4", "10", "1", "Валюта", "CNUMB"),
    prjpart1_id("4", "10", "1", "Диллер,офис,спец.", "prjpart1_id"),
    prjpart2_id("4", "10", "1", "Зуказчик", "prjpart2_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eProject(Object... p) {
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
//            query.select(up, "order by", id);
            query.select(up, "order by", id, "desc");
            Query.listOpenTable.add(query);
        }
        return query;
    }

    public Query query() {
        return query;
    }
    
    public static Record find(int _id) {
        if (_id == -3) {
            return up.newRecord(Query.SEL);
        }
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "='", _id, "'");
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public String toString() {
        return meta.descr();
    }
}
