package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import static domain.ePrjkit.up;
import frames.UGui;
import java.util.List;

public enum eRulecalc implements Field {
    up("0", "0", "0", "������� ������� ��������", "RULECLK"),
    id("4", "10", "0", "�������������", "id"),
    name("12", "64", "1", "�������� �������", "RNAME"),
    type("5", "5", "1", "��� ������������ ��������� (502 - ����������)", "RUSED"),
    coeff("8", "15", "1", "�����������", "RKOEF"),
    quant("12", "96", "1", "����������", "RLENG"),
    color1("12", "96", "1", "���� ������� ������� (��������)", "RCODM"),
    color2("12", "96", "1", "���� ������� ������� (??? ����������)", "RCOD1"),
    color3("12", "96", "1", "���� ������� ������� (??? �������)", "RCOD2"),
    common("5", "5", "1", "����� (�� ���������)", "RALLP"),
    form("5", "5", "1", "����� �������", "RISKL"), //0 - �� ��������� �����,  10 - �� �������������, �� ������� ����������,  12 - �� ������������� ���������� � ������"
    size("12", "96", "1", "��������", "RSIZE"),
    suppl("8", "15", "1", "��������", "RPRIC"),
    sebes("16", "5", "1", "���� ������./����", "RSEBE"),
    calk("5", "5", "1", "����������� (�� ���������)", "RCALK"),
    artikl_id("4", "10", "1", "�������", "artikl_id");   
    
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
  
    public static List<Record> filter() {
        if (Query.conf.equals("NET")) {
            return data();
        }
        return new Query(values()).select(up, "order by", id);
    }

    public String toString() {
        return meta.descr();
    }
}
