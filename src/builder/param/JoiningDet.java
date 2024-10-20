package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eJoindet;
import domain.eJoinpar2;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.model.ElemJoining;
import builder.model.ElemSimple;
import common.UCom;
import enums.Type;
import java.util.ArrayList;
import java.util.LinkedList;

//C���������
public class JoiningDet extends Par5s {

    public JoiningDet(Wincalc winc) {
        super(winc);
    }

    public boolean filter(HashMap<Integer, String> mapParam, ElemJoining elemJoin, Record joindetRec) {

        List<Record> paramList = eJoinpar2.filter(joindetRec.getInt(eJoindet.id));
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //���� �� ���������� ����������
        for (Record rec : paramList) {
            if (check(mapParam, elemJoin, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(HashMap<Integer, String> mapParam, ElemJoining elemJoin, Record rec) {

        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {

                case 11000: //��� ���������������� ���� ���������� 1/2
                case 12000: //��� ���������������� ���� ���������� 1/2 
                {
                    String[] arr = rec.getStr(TEXT).split("/");
                    if (UPar.is_STRING_XX000(arr[0], elemJoin.elem1) == false) {
                        return false;
                    }
                    if (arr.length > 1 && UPar.is_STRING_XX000(arr[1], elemJoin.elem2) == false) {
                        return false;
                    }
                }
                break;
                case 11001: //���� ������� ������� ���.1 
                case 12001: //���� ������� ������� ���.1 
                    if (UPar.is_11001_11002_12001_12002_13001_14001_15001_33001_34001(rec.getStr(TEXT), elemJoin.elem1) == false) {
                        return false;
                    }
                    break;
                case 11002:  //���� ������� ������� ���.2 
                case 12002:  //���� ������� ������� ���.2 
                    if (UPar.is_11001_11002_12001_12002_13001_14001_15001_33001_34001(rec.getStr(TEXT), elemJoin.elem2) == false) {
                        return false;
                    }
                    break;
                case 11005:  //��������� ���� 
                case 12005:  //��������� ���� 
                    if (UPar.is_1005x6_2005x6_3005_4005_11005_12005_31050_33071_34071(rec.getStr(TEXT), elemJoin.elem1) == false) {
                        return false;
                    }
                    break;
                case 11008:  //����������� ���������� ���., �� 
                case 12008:  //����������� ���������� ���., �� 
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 11009:  //������� ���������� 
                case 12009:  //������� ����������                      
                    message(rec.getInt(GRUP)); //� SA ������ ����������
                    break;
                case 11010:  //������������ � ��������� 1 
                case 12010:  //������������ � ��������� 1                    
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11020:  //������������ � ��������� 2 
                case 12020:  //������������ � ��������� 2 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11028: //�������� ���� ����������, �� 
                case 12028: //�������� ���� ����������, �� 
                {
                    double weight = 0;
                    ArrayList<ElemSimple> glassList = UCom.filter(winc.listElem, Type.GLASS);
                    for (ElemSimple glass : glassList) {
                        if (glass.artiklRecAn.getDbl(eArtikl.density) > 0) {
                            weight += glass.width() * glass.height() * glass.artiklRecAn.getDbl(eArtikl.density) / 1000000;
                        }
                    }
                    if (UCom.containsNumbExp(rec.getStr(TEXT), weight) == false) {
                        return false;
                    }
                }
                break;
                case 11029:  //���������� ���� �� �����, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 11030:  //���������� 
                case 12060:  //���������� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11040:  //����� �������, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11050:  //���, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 12050:  //��������, ��    
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11060:  //���������� �� ���   
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11066:  //���� �������� ������� ���.1 
                    if (UCom.containsColor(rec.getStr(TEXT), elemJoin.elem1.colorID1) == false) {
                        return false;
                    }
                    break;
                case 11067:  //���� �������� �������� ������� 
                case 12067:  //���� �������� �������� �������
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID1) == false) {
                        return false;
                    }
                    break;
                case 11068:  //���� �����. �������� ������� 
                case 12068:  //���� �����. �������� ������� 
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID2) == false) {
                        return false;
                    }
                    break;
                case 11069:  //���� �����. �������� �������
                case 12069:  //���� �����. �������� �������     
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID3) == false) {
                        return false;
                    }
                    break;
                case 11070:  //������� ���������� 
                case 12070:  //������� ����������    
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11072:  //������ �� ������� 
                case 12072:  //������ �� ������� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11095: //���� ������� ������� ����������� 
                case 12095: //���� ������� ������� ����������� 
                    if (!UPar.is_11095_12095_31095_33095_34095_37095_38095_39095_40095(rec.getStr(TEXT), winc.nuni)) {
                        return false;
                    }
                    break;
                case 12027:  //������������ ��� ������� 
                    if ("� ������������".equals(rec.getStr(TEXT)) == true && elemJoin.elem1.artiklRec.getInt(eArtikl.with_seal) == 0) {
                        return false;
                    }
                    break;
                case 12030:  //[ * ����-� ] 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 12063:  //���� ���� �� ��������� ������ 
                    message(rec.getInt(GRUP));
                    break;
                case 12064:  //���� � ����� ����� ���������� 
                    message(rec.getInt(GRUP));
                    break;
                case 12065:  //�����, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 12075:  //���� ���� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "��� " + grup + "  �� ���������!!!";
            }
        } catch (Exception e) {
            System.err.println("������:param.JoiningDet.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
