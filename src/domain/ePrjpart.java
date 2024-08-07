package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eColor.up;
import static domain.ePrjkit.up;
import frames.UGui;
import java.util.List;
import static java.util.stream.Collectors.toList;

public enum ePrjpart implements Field {
    up("0", "0", "0", "Контрагент", "CLIENTS"),
    id("4", "10", "0", "Идентификатор", "id"),
    npp("4", "10", "1", "Номер п/п", "npp"),
    partner("12", "120", "1", "Контрагент (Физ.лицо/Организация)", "KNAME"),
    login("12", "32", "1", "Менеджер (login)", "PNAME"), //Фильтр по login user
    category("12", "32", "1", "Категория", "KPREF"), //Заказчик, поставщик, офис, дилер, специальный   
    note("12", "256", "1", "Примечание", "note"),   
    flag2("16", "5", "1", "Физ.лицо/Организация", "KCHAS"),
  
    addr_leve1("12", "64", "1", "Адрес 1го уровня", "KADRP"),
    addr_leve2("12", "192", "1", "Адрес 2го уровня.", "KADDR"),
    addr_phone("12", "32", "1", "Телефон", "KTELE"),
    addr_email("12", "64", "1", "E-mail", "KMAIL"),
   
    org_contact("12", "64", "1", "Контактное лицо", "KKNAM"),
    org_leve1("12", "64", "1", "Адрес 1го уровня", "KADRP"),
    org_leve2("12", "192", "1", "Адрес 2го уровня..", "KADDR"),
    org_phone("12", "32", "1", "Телефон", "KVTEL"),
    org_email("12", "32", "1", "E-mail", "KMAIL"),
    org_fax("12", "32", "1", "Факс", "KFAXX"),  
    
    bank_name("12", "128", "1", "Банк", "KBANK"),
    bank_inn("12", "64", "1", "ИНН", "KBAN1"),
    bank_rs("12", "64", "1", "Р/С", "KBAN2"),
    bank_bik("12", "64", "1", "БИК", "KBAN3"),
    bank_ks("12", "64", "1", "К/С", "KBAN4"),
    bank_kpp("12", "64", "1", "КПП", "KBAN5"),
    bank_ogrn("12", "32", "1", "ОГРН", "KOGRN"), 
    
    flag1("16", "5", "1", "Скидка менеджера", "KFLAG"), 
    desc1("8", "15", "1", "Скидки на профиль %", "DESC1"),
    desc2("8", "15", "1", "Скидки на аксессуары", "DESC2"),
    desc3("8", "15", "1", "Скидка на уплотнение", "DESC3"),
    desc5("8", "15", "1", "Скидка на заполнение", "DESC5"),
    disc6("8", "15", "1", "Скидки по умолчанию", "CDESC");     
   
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    ePrjpart(Object... p) {
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
            return up.newRecord(Query.SEL);
        }
        if (Query.conf.equals("calc")) {
            return data().stream().filter(rec -> rec.getInt(id) == _id).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public String toString() {
        return meta.descr();
    }
}
