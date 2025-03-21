package domain;

import dataset.Field;
import dataset.MetaField;
import dataset.Query;
import dataset.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum eElement implements Field {
    up("0", "0", "0", "�������", "VSTALST"),
    id("4", "10", "0", "�������������", "id"),
    name("12", "96", "1", "������������", "VNAME"),
    typset("4", "10", "1", "��� �������", "typset"),
    signset("12", "32", "1", "������� �������", "VSIGN"),
    markup("8", "15", "1", "������� %", "VPERC"),
    todef("16", "5", "1", "������� �� ���������", "todef"),
    toset("16", "5", "1", "��������� ��������������", "toset"),
    artikl_id("4", "10", "1", "�������", "artikl_id"),
    groups1_id("4", "10", "1", "�����", "groups1_id"),
    groups2_id("4", "10", "0", "���������", "groups2_id");

    private MetaField meta = new MetaField(this);
    private static Query query = new Query(values());

    eElement(Object... p) {
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

    public static void sql(Query qElament, Query qArtikl, int categID) {
        qElament.clear();
        qArtikl.clear();
        
        List<Record> artiklList = (categID == -5)
                ? eArtikl.data().stream().filter(rec -> rec.getInt(eArtikl.level1) == 5).collect(Collectors.toList())
                : eArtikl.data().stream().filter(rec -> rec.getInt(eArtikl.level1) != 5).collect(Collectors.toList());
        
        List<Record> groupsList = eGroups.data().stream().filter(rec
                -> rec.getInt(eGroups.npp) == Math.abs(categID)).collect(Collectors.toList());

        for (Record recElem : data()) {
            for (Record recGrp : groupsList) {
                if (recElem.getInt(eElement.groups2_id) == recGrp.getInt(eGroups.id) && recGrp.getInt(eGroups.npp) == Math.abs(categID)) {
                    qElament.add(recElem);
                    qArtikl.add(artiklList.stream().filter(rec
                            -> recElem.getInt(eElement.artikl_id) == rec.getInt(eArtikl.id)).findFirst().get());
                }
            }
        }
    }

    public static Record find(int _id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> _id == rec.getInt(id)).findFirst().orElse(up.newRecord(Query.SEL));
        }
        Query recordList = new Query(values()).select(up, "where", _id, "= id");
        return (recordList.isEmpty() == true) ? up.newRecord(Query.SEL) : recordList.get(0);
    }
    
    public static List<Record> filter(int seriesID) {
        if (seriesID == -1) {
            return new ArrayList<Record>();
        }
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> seriesID == rec.getInt(groups1_id) && rec.getInt(todef) > 0).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", groups1_id, "=", seriesID, "and", todef, "> 0");
    }

    public static List<Record> filter2(int artikl2_id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> artikl2_id == rec.getInt(artikl_id) && rec.getInt(todef) > 0).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", artikl_id, "=", artikl2_id, "and", todef, "> 0");
    }

    public static List<Record> filter3(int artikl2_id, int series2_id) {
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> (artikl2_id == rec.getInt(artikl_id)
                    || series2_id == rec.getInt(groups1_id)) && rec.getInt(todef) > 0).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where (", artikl_id, "=", artikl2_id, "or", groups1_id, "=", series2_id, ") and", todef, "> 0");
    }

    public static List<Record> filter4(Record recordSer) {
        int artiklID = recordSer.getInt(eArtikl.id);
        int seriesID = recordSer.getInt(eArtikl.groups4_id);
        if (seriesID == -1) {
            return new ArrayList<Record>();
        }
        if (Query.conf.equals("NET")) {
            return data().stream().filter(rec -> seriesID == rec.getInt(groups1_id)
                    && artiklID != rec.getInt(artikl_id) && rec.getInt(todef) > 0).collect(Collectors.toList());
        }
        return new Query(values()).select(up, "where", groups1_id, "=", seriesID, "and", artiklID, "!= artikl_id and", todef, "> 0");
    }

    public String toString() {
        return meta.descr();
    }
}
