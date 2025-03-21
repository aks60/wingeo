package builder.model;

import builder.Wincalc;
import dataset.Record;
import enums.TypeJoin;
import builder.making.TRecord;
import dataset.Query;
import domain.eArtikl;
import domain.eJoining;
import domain.eJoinvar;
import enums.Layout;
import java.util.List;
import static org.locationtech.jts.algorithm.Angle.angle;
import static org.locationtech.jts.algorithm.Angle.diff;
import org.locationtech.jts.geom.Coordinate;
import startup.Test;

public class ElemJoining {

    public Record joiningRec = eJoining.up.newRecord(Query.SEL); //��� �������� �����������
    public Record joinvarRec = eJoinvar.up.newRecord(Query.SEL); //��� �������� �����������    

    public double id = -1; //������������� ����������
    public Wincalc winc;
    private TypeJoin type = TypeJoin.NONE;      //��� ���������� (�� ��� ����� )
    public int vid = 0; //��� ���������� ("0-������� L-���", "1-��������� �-���") ��� ("0-������� T-���", "1-��������� �-���", "2-������� Y-���)
    public ElemSimple elem1 = null;  //������� ���������� 1
    public ElemSimple elem2 = null;  //������� ���������� 2
    public String costs = "";     //������������, �/�.

    public ElemJoining(Wincalc winc, TypeJoin type, ElemSimple elem1, ElemSimple elem2) {
        this.id = ++winc.nppID;
        this.winc = winc;
        this.type = type; //���� �������� ������. ����������� ��. type();
        this.elem1 = elem1;
        this.elem2 = elem2;
        //this.angl = angleBetween();
    }

    public void addSpecific(TRecord spcAdd) { //���������� ������������ ��������� ���������
        try {
            if(spcAdd.artiklRec.getStr(eArtikl.code).substring(0, 1).equals("@")) {
                return;
            }            
            TRecord spcRec = elem1.spcRec;
            String sideCalc = spcAdd.getParam("null", 11072, 12072);
            if (sideCalc != null && "�������".equals(sideCalc)) {
                spcAdd.width = (elem1.length() > elem2.length()) ? elem1.length() : elem2.length();
            } else if (sideCalc != null && "�������".equals(sideCalc)) {
                spcAdd.width = (elem1.length() > elem2.length()) ? elem2.length() : elem1.length();
            } else if (sideCalc != null && "�����".equals(sideCalc)) {
                if (elem1.layout() == Layout.HORIZ || elem1.layout() == Layout.BOTT || elem1.layout() == Layout.TOP) {
                    spcAdd.width = (elem1.x1() > elem2.x1()) ? elem1.x1() - elem2.x2() : elem2.x1() - elem1.x2();
                } else if (elem1.layout() == Layout.VERT || elem1.layout() == Layout.RIGHT || elem1.layout() == Layout.LEFT) {
                    spcAdd.width = (elem1.y1() > elem2.y1()) ? elem1.y1() - elem2.y2() : elem2.y1() - elem1.y2();
                }
            }
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //���. ��. � ������ �����. 
            spcAdd.count += UPar.to_11050(spcAdd, this); //���. ��. � �����
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //�������� ��

            if (List.of(1, 3, 5).contains(spcAdd.artiklRec.getInt(eArtikl.level1))) {
                //spcAdd.width += elem1.length();
                spcAdd.width += spcRec.width;
            }
            UPar.to_12075_34075_39075(elem1, spcAdd); //���� ����
            spcAdd.width = UPar.to_12065_15045_25040_34070_39070(spcAdd); //����� ��       
            spcAdd.width = spcAdd.width * UPar.to_12030_15030_25035_34030_39030(spcAdd);//"[ * ����-� ]"
            spcAdd.width = spcAdd.width / UPar.to_12040_15031_25036_34040_39040(spcAdd);//"[ / ����-� ]"
            spcAdd.count = UPar.to_11070_12070_33078_34078(spcAdd); //������� ����������

            elem1.spcRec.spcList.add(spcAdd);

        } catch (Exception e) {
            System.err.println("������:ElemJoinning.addSpecific() " + e);
        }
    }

    public String name() {
        if (joiningRec.get(1) != null) {
            String name1 = eArtikl.data().stream().filter(rec -> rec.getInt(eArtikl.id) == elem1.artiklRecAn.getInt(eArtikl.id)).findFirst().orElse(eArtikl.up.newRecord(Query.SEL)).getStr(eArtikl.code);
            String name2 = eArtikl.data().stream().filter(rec -> rec.getInt(eArtikl.id) == elem2.artiklRecAn.getInt(eArtikl.id)).findFirst().orElse(eArtikl.up.newRecord(Query.SEL)).getStr(eArtikl.code);
            return name1 + " ? " + name2;
        }
        return "";
    }

    //��� ����������
    public TypeJoin type() {
        if (type == TypeJoin.ANGL) {
            int lev1 = elem1.artiklRec.getInt(eArtikl.level1);
            int lev2 = elem2.artiklRec.getInt(eArtikl.level2);

            if ((lev1 == 1 && (lev2 == 1 || lev2 == 2)) == false) {
                double ang1 = UGeo.anglHor(elem1.x1(), elem1.y1(), elem1.x2(), elem1.y2());
                double ang2 = UGeo.anglHor(elem2.x1(), elem2.y1(), elem2.x2(), elem2.y2());

                if ((ang1 == -90 && ang2 == 180) || (ang1 == 90 && ang2 == 0)) {
                    return TypeJoin.ANG1;

                } else if ((ang1 == 0 && ang2 == -90) || (ang1 == 180 && ang2 == 90)) {
                    return TypeJoin.ANG2;
                } else {
                    return TypeJoin.ANGL;
                }
            }
        }
        return type;
    }

    public void type(TypeJoin v) {
        this.type = v;
    }

    //���� ����������������� �������� ���������
    public Double angleBetween() {
        double c1 = angle(new Coordinate(elem1.x2(), elem1.y2()), new Coordinate(elem1.x1(), elem1.y1()));
        double c2 = angle(new Coordinate(elem2.x2(), elem2.y2()), new Coordinate(elem2.x1(), elem2.y1()));
        return Math.toDegrees(diff(c1, c2));        
    }

    public String toString() {
        return "id=" + id + ",  type=" + type  +  " name=" + type.name
                + ",  elem1=(" + elem1.id + " " + elem1.type + " " + elem1.layout() + ")"
                + ",  elem2=(" + elem2.id + " " + elem2.type + " " + elem2.layout() + ")";
    }
}
