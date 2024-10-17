package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public enum eArtikl implements Field {
    up("0", "0", "0", "������������ ��������", "ARTIKLS"),
    id("4", "10", "0", "�������������", "id"),
    code("12", "32", "1", "�������", "ANUMB"),
    level1("5", "5", "1", "������� ���", "ATYPM"),
    level2("5", "5", "1", "������ ��������", "ATYPP"),
    name("12", "96", "1", "��������", "ANAME"),
    supplier("12", "64", "1", "� ����������", "ANAMP"),
    tech_code("12", "64", "1", "��������.��� ����������", "ATECH"),
    size_furn("8", "15", "1", "����� ������� ��� ����������� ���", "ASIZF"),
    size_falz("8", "15", "1", "����� ���������� ��� ������(�����)", "ASIZN"),
    size_tech("8", "15", "1", "������ ��������. ��� ������� �������(�����)", "ASIZV"),
    size_centr("8", "15", "1", "B - �������� ��� �� ������", "ASIZB"),
    size_frez("8", "15", "1", "������� �����", "AFREZ"),
    len_unit("8", "15", "1", "����� ��. ��������", "ALENG"),
    height("8", "15", "1", "������", "AHEIG"),
    depth("8", "15", "1", "�������", "AFRIC"),
    unit("5", "5", "1", "��. ���������", "ATYPI"),
    density("8", "15", "1", "�������� ���", "AMASS"),
    section("8", "15", "1", "�������", "ASECH"),
    noopt("5", "5", "1", "�� ��������������", "NOOPT"),
    ost_delov("8", "15", "1", "������� �������", "AOSTD"),
    cut_perim("8", "15", "1", "�������� �������", "APERI"),
    min_rad("8", "15", "1", "���.������ ����", "AMINR"),
    nokom("5", "5", "1", "������ ��� ������", "NOKOM"), // ( -2 - ������ � ����������, -1 - ������ � ������������, 0- �������� �����, 1 - �� ��������, 2 - ������ � �������� � ���� ������, 4 - ������ � ��������)
    noskl("5", "5", "1", "�� ��� ������", "NOSKL"),
    sel_color("5", "5", "1", "������ �������", "ACOLL"),
    with_seal("5", "5", "1", "� �����������", "AWORK"),
    otx_norm("8", "15", "1", "����� ������", "AOUTS"),
    groups1_id("4", "10", "1", "������ �������", "groups1_id"),
    groups2_id("4", "10", "1", "������ ������", "groups2_id"),
    groups3_id("4", "10", "1", "��������� ��������", "groups3_id"),
    groups4_id("4", "10", "1", "����� �������", "groups4_id"),
    syssize_id("4", "10", "1", "��������� ���������", "syssize_id"),
    currenc1_id("4", "10", "1", "�������� ������", "CNUMB"),
    currenc2_id("4", "10", "1", "���������� ������", "CNUMT"),
    analog_id("4", "10", "1", "������ �������", "analog_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());
    private static HashMap<Integer, Record> map = new HashMap<Integer, Record>();

    eArtikl(Object... p) {
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
            map.clear();
            query.stream().forEach(rec -> map.put(rec.getInt(id), rec));
        }
        return query;
    }

    public Query query() {
        return query;
    }

    public static Record get(int id) {
        if (id == -3) {
            return virtualRec();
        }
        data();
        Record rec = map.get(id);
        return (rec == null) ? virtualRec() : rec;
    }

    public static Query sql(Query query, Field field, int value) {
        query.clear();
        HashSet hs = new HashSet();
        if (Query.conf.equals("NET")) {
              query.addAll(data().stream().filter(rec -> rec.getInt(field) == value && hs.add(rec.getDbl(depth))).collect(Collectors.toList()));
        } else {
            query.select("select distinct " + depth.name()
                    + " from " + up.tname() + " where " + level1.name() + " = 5" + " order by " + depth.name());
        }
        return query;
    }

    public static Record find(int _id, boolean _analog) {
        if (_id == -3) {
            return virtualRec();
        }
        if (Query.conf.equals("NET")) {
            Record recordRec = data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
            if (_analog == true && recordRec.get(analog_id) != null) {

                int _analog_id = recordRec.getInt(analog_id);
                recordRec = data().stream().filter(rec -> _analog_id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
            }
            return recordRec;
        }
        Query recordList = new Query(values()).select(up, "where", id, "=", _id);
        if (_analog == true && recordList.isEmpty() == false && recordList.get(0, analog_id) != null) {

            int _analog_id = recordList.getAs(0, analog_id);
            recordList = new Query(values()).select(up, "where", id, "=", _analog_id);
        }
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public static Record find2(String _code) {
        if ("0x0x0x0".equals(_code)) { //|| "-".equals(_code)) {
            return virtualRec();
        }
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _code.equals(rec.getStr(code))).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", code, "='", _code, "'");
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }

    public static List<Record> filter(int seriesID) {
        if (seriesID == -1) {
            return List.of();
        }
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> seriesID == rec.getInt(groups4_id)).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", groups4_id, "=", seriesID, "");
    }

    public static Record virtualRec() {
        Record record = up.newRecord(Query.SEL);
        record.setNo(id, -3);
        record.setNo(code, "@");
        record.setNo(name, "virtual");
        //��� ���������� ������� �����������
        record.setNo(height, 68);
        record.setNo(size_centr, 0);
        record.setNo(tech_code, "");
        record.setNo(size_falz, 20);
        record.setNo(syssize_id, -3);
        return record;
    }

    public String toString() {
        return meta.descr();
    }
}
