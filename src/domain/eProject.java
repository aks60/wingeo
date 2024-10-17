package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.eGroups.up;
import frames.UGui;

public enum eProject implements Field {
    up("0", "0", "0", "������ ��������", "LISTPRJ"),
    id("4", "10", "0", "�������������", "id"),
    num_ord("12", "32", "1", "����� ������", "ZNUMB"),
    num_acc("12", "32", "1", "����� �����", "INUMB"),         
    manager("12", "64", "1", "��������", "MNAME"), //��� user ������� ������ ������
    square("8", "15", "1", "������� �������", "PSQRA"),
    weight("8", "15", "1", "��� �������", "EMPTY"),   
    type_calc("5", "5", "1", "��� ��������", "PTYPE"),
    type_norm("5", "5", "1", "���� ���� �������", "CTYPE"),    
    costpric("8", "15", "1", "C������������ ������� � �������", "PSEBE"),   
    
    price2("8", "15", "1", "C�������� ����������� ��� ������", "KPRIC"),   
    price3("8", "15", "1", "C�������� ������������ ��� ������", "APRIC"), 
    price4("8", "15", "1", "C�������� ������� ��� ������", "PPRIC"),
            
    cost2("8", "15", "1", "C�������� ����������� �� ��������", "KPRCD"),
    cost3("8", "15", "1", "C�������� ������������ �� ��������", "APRCD"), 
    cost4("8", "15", "1", "C�������� ������� �� ��������", "PPRCD"),
    
    disc2("8", "15", "1", "C����� �� �����������", "KDESC"),
    disc3("8", "15", "1", "C����� �� ������������", "ADESC"),
    disc4("8", "15", "1", "C����� �����", "PDESC"),
    
    incr1("8", "15", "1", "������� %", "PMARG"),  
    flag1("5", "5", "1", "�������� � ������������", "PWORK"),   
    date4("93", "19", "1", "���� ����������� ������", "PDATE"),
    date5("93", "19", "1", "���� ������� ������", "CDATE"),
    date6("93", "19", "1", "���� ����. � ������������", "WDATE"),
    owner("12", "32", "1",  "User", "owner"),
    currenc_id("4", "10", "1", "������", "CNUMB"),
    prjpart_id("4", "10", "1", "������", "prjpart_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eProject(Object... p) {
        meta.init(p);
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
    
    public String toString() {
        return meta.descr();
    }
}
