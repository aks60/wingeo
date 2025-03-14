package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import enums.UseUnit;
import enums.TypeArt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import builder.model.ElemSimple;
import builder.model.UPar;
import java.util.List;

/**
 * ������������ �������� ����
 */
public class TRecord {

    public ArrayList<TRecord> spcList = new ArrayList<TRecord>(); //������ ��������, �������� � �. �.  
    public HashMap<Integer, String> mapParam = null;  //��������� ������������
    public ElemSimple elem5e = null;  //������� ����������� ������������ (���������)
    public Record variantRec = null;  //������� � ������������
    public Record detailRec = null;  //����������� � ������������
    
    public Record artiklRec = null;  //������� ��������
    public Record artdetRec[] = {null, null, null};  //�������� ��������

    public double id = -1; //ID
    public String place = "---";  //����� ����������
    public String name = "-";  //������������
    public String artikl = "-";  //�������
    public int colorID1 = -3;  //���.��������
    public int colorID2 = -3;  //�����.��������
    public int colorID3 = -3;  //�����.��������
    public double width = 0;  //�����
    public double height = 0;  //������
    public double weight = 0;  //�����
    public double anglCut0 = 0;  //����1
    public double anglCut1 = 0;  //����2
    public double anglHoriz = 0; // ���� � ���������    
    public double count = 1;  //���. ������
    public int unit = 0;  //��.���   
    public double waste = 0;  //������� ������ ��. eArtikl.otx_norm     
    public double quant1 = 0;  //���������� ��� ������
    public double quant2 = 0;  //���������� (� ���./��� ���.)
    public double costprice = 0;  //�������������   
    public double price = 0;  //���� �� ������� ���������
    public double cost1 = 0;  //��������� ��� ��������������� ������
    public double cost2 = 0;  //��������� � ��������������� �������

    public TRecord() {
    }

    public TRecord(TRecord spec) {
        this.id = spec.id;
        this.place = spec.place;
        this.artikl = spec.artikl;
        this.artiklRec = spec.artiklRec;
        this.detailRec = spec.detailRec;
        this.name = spec.name;
        this.colorID1 = spec.colorID1;
        this.colorID2 = spec.colorID2;
        this.colorID3 = spec.colorID3;
        this.width = spec.width;
        this.height = spec.height;
        this.weight = spec.weight;
        this.anglCut0 = spec.anglCut0;
        this.anglCut1 = spec.anglCut1;
        this.count = spec.count;
        this.unit = spec.unit;
        this.quant1 = spec.quant1;
        this.waste = spec.waste;
        this.quant2 = spec.quant2;
        this.costprice = spec.costprice;
        this.price = spec.price;
        this.cost1 = spec.cost1;
        this.cost2 = spec.cost2;
        this.anglHoriz = spec.anglHoriz;
        this.mapParam = spec.mapParam;
        this.elem5e = spec.elem5e;
    }

    public TRecord(double id, ElemSimple elem5e) {
        ++elem5e.winc.nppID;
        this.id = id;
        this.elem5e = elem5e;
        this.mapParam = new HashMap<Integer, String>();
    }

    public TRecord(String place, Record detailRec, Record artiklRec, ElemSimple elem5e, HashMap<Integer, String> mapParam) {
        this.id = ++elem5e.winc.nppID;
        this.elem5e = elem5e;
        this.mapParam = mapParam;
        this.detailRec = detailRec;
        this.place = place;
        artiklRec(artiklRec);
    }

    public TRecord(String place, double id, Record detailRec, Record artiklRec, HashMap<Integer, String> mapParam) {
        this.id = id;
        this.mapParam = mapParam;
        this.detailRec = detailRec;
        this.place = place;
        artiklRec(artiklRec);
    }

    public void artiklRec(Record artiklRec) {
        this.artikl = artiklRec.getStr(eArtikl.code);
        this.name = artiklRec.getStr(eArtikl.name);
        this.waste = artiklRec.getDbl(eArtikl.otx_norm);
        this.unit = artiklRec.getInt(eArtikl.unit);
        this.artiklRec = artiklRec;
    }

    public void color(int colorID1, int colorID2, int colorID3) {
        this.colorID1 = colorID1;
        this.colorID2 = colorID2;
        this.colorID3 = colorID3;
    }

    public void copy(TRecord spec) {
        this.id = spec.id;
        this.place = spec.place;
        this.artikl = spec.artikl;
        this.artiklRec = spec.artiklRec;
        this.detailRec = spec.detailRec;
        this.name = spec.name;
        this.colorID1 = spec.colorID1;
        this.colorID2 = spec.colorID2;
        this.colorID3 = spec.colorID3;
        this.width = spec.width;
        this.height = spec.height;
        this.weight = spec.weight;
        this.anglCut0 = spec.anglCut0;
        this.anglCut1 = spec.anglCut1;
        this.count = spec.count;
        this.unit = spec.unit;
        this.quant1 = spec.quant1;
        this.waste = spec.waste;
        this.quant2 = spec.quant2;
        this.costprice = spec.costprice;
        this.price = spec.price;
        this.cost1 = spec.cost1;
        this.cost2 = spec.cost2;
        this.anglHoriz = spec.anglHoriz;
        this.mapParam = spec.mapParam;
        this.elem5e = spec.elem5e;
    }

    public Vector getVector(int npp) {
        double elemID = (elem5e == null) ? 0 : elem5e.id;
        double height2 = (elem5e != null && elem5e.h() != null && artiklRec != null && artiklRec.getInt(eArtikl.level1) == 1 && artiklRec.getInt(eArtikl.level2) == 1)
                ? (Math.pow(elem5e.winc.width() / 2, 2) + Math.pow(elem5e.h(), 2)) / (2 * elem5e.h()) : height;

        return new Vector(List.of(npp, id, elemID, place, artikl, name, eColor.find(colorID1).getStr(eColor.name), eColor.find(colorID2).getStr(eColor.name),
                eColor.find(colorID3).getStr(eColor.name), width, height2, weight, anglCut0, anglCut1, anglHoriz,
                count, UseUnit.getName(unit), waste, quant2, costprice, price, cost1, cost2));
    }

    public void setAnglCut(double angl0, double angl1) {
        this.anglCut0 = angl0;
        this.anglCut1 = angl1;
    }

    public String getParam(Object def, int... p) {
        return UPar.getParam(def, mapParam, p);
    }

    public static void write_csv(ArrayList<TRecord> spcList) {
        //��. �������
    }

    public static void write_txt(ArrayList<TRecord> specList) {
        //��. �������
    }

    public boolean equals(Object obj) {
        if (obj instanceof TRecord) {
            return this.id == ((TRecord) obj).id;
        } else {
            return super.equals(obj);
        }
    }

//    public String toString() {
//        return artikl + " - " + name;
//    }
}
