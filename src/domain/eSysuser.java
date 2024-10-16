package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eArtikl.code;
import static domain.eArtikl.height;
import static domain.eArtikl.id;
import static domain.eArtikl.name;
import static domain.eArtikl.size_centr;
import static domain.eArtikl.size_falz;
import static domain.eArtikl.syssize_id;
import static domain.eArtikl.tech_code;
import static domain.eArtikl.up;

public enum eSysuser implements Field {

    up("0", "0", "0", "������������ �������", "EMPTY"),
    id("4", "10", "0", "�������������", "id"),
    role("12", "16", "1", "����", "role"), 
    login("12", "24", "0", "������������", "login"), 
    fio("12", "64", "1", "��� ������������", "fio"),
    phone("12", "16", "1", "�������", "phone"),
    email("12", "32", "1", "�����", "email"),
    desc("12", "64", "1", "��������", "desc"),      
    openkey("12", "512", "1", "�������� ����", "openkey");
    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eSysuser(Object... p) {
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
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(null);
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        return (recordList.isEmpty() == true) ? null : recordList.get(0);
    }

    public static Record find2(String _login) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _login.equalsIgnoreCase(rec.getStr(login))).findFirst().orElse(null);
        }
        Query recordList = new Query(values()).select(up, "where", login, "= '", _login + "'");
        return (recordList.isEmpty() == true) ? null : recordList.get(0);
    }
    
    public static Record virtualRec() {
        Record record = up.newRecord(Query.SEL);
        record.setNo(id, -3);
        record.setNo(role, "xxx");
        record.setNo(login, "xxx");
        record.setNo(fio, "");
        record.setNo(phone, "");
        record.setNo(email, "");
        record.setNo(desc, "");
        record.setNo(openkey, "xxx");
        return record;
    }
    
    public String toString() {
        return meta.descr();
    }
}
