package builder.param;

import dataset.Record;
import domain.eElemdet;
import domain.eElempar2;
import domain.eSetting;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.model.Com5t;
import builder.model.ElemSimple;
import common.UCom;
import domain.eArtikl;
import enums.Type;

//������� 33000, 34000, 38000, 39000, 40000
public class ElementDet extends Par5s {

    public ElementDet(Wincalc winc) {
        super(winc);
    }

    public boolean filter(HashMap<Integer, String> mapParam, ElemSimple elem5e, Record elemdetRec) {

        List<Record> paramList = eElempar2.filter(elemdetRec.getInt(eElemdet.id)); //������ ���������� ����������� 
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //���� �� ���������� ��������
        for (Record rec : paramList) {
            if (ElementDet.this.check(mapParam, elem5e, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(HashMap<Integer, String> mapParam, ElemSimple elem5e, Record rec) {
        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {

                case 33000: //��� ���������������� ���� ���������� 
                case 34000: //��� ���������������� ���� ���������� 
                    if (!UPar.is_STRING_XX000(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 33001:  //���� ������� ������� 
                case 34001:  //���� ������� ������� 
                    if (UPar.is_11001_11002_12001_12002_13001_14001_15001_33001_34001(rec.getStr(TEXT), elem5e) == false) {
                        return false;
                    }
                    break;
                case 33002:  //������ �������� ���������� 
                case 34002:  //������ �������� ���������� 
                    message(grup);
                    break;
                case 33003:  //������ �������� ����������, �� 
                case 34003:  //������ �������� ����������, �� 
                    message(grup);
                    break;
                case 33004:  //������ �� ����� ������� ������ 
                case 34004:  //������ �� ����� ������� ������                   
                    message(grup);
                    break;
                case 33005:  //���� �������� �������� ���������� 
                case 34005:  //���� �������� �������� ����������
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID1) == false) {
                        return false;
                    }
                    break;
                case 33006:  //���� �����. �������� ����������
                case 34006:  //���� �����. �������� ���������� 
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.colorID2) == false) {
                        return false;
                    }
                    break;
                case 33007:  //���� �����. �������� ���������� 
                case 34007:  //���� �����. �������� ����������     
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.colorID3) == false) {
                        return false;
                    }
                    break;
                case 33008:  //����������� ���������� ���., �� 
                case 34008:  //����������� ���������� �������, �� 
                case 40008:  //����������� ���������� ���., ��                    
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 33011: //������� ��������/����������� ����������, ��
                case 34011: //������� ��������/����������� ����������, ��
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if (glassList.get(0).type == Type.GLASS && glassList.get(1).type == Type.GLASS) {
                        if (UCom.containsNumb(rec.getStr(TEXT),
                                glassList.get(0).artiklRec.getDbl(eArtikl.depth),
                                glassList.get(1).artiklRec.getDbl(eArtikl.depth)) == false) {
                            return false;
                        }
                    }
                    break;
                case 33017: //��� ������� �������� ������ 
                case 34017: //��� ������� �������� ������ 
                case 38017: //��� ������� �������� ������     
                case 39017: //��� ������� �������� ������ 
                case 40017: //��� ������� �������� ������                 
                    if (UPar.is_13017_14017_24017_25017_31017_33017_34017_37017_38017(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 33030:  //���������� 
                case 38030:  //����������   
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33031:  //������ ���������� 
                case 34061:  //������ ����������                 
                    message(grup);
                    break;
                case 33032:  //�������� �������� �� ��������� 
                    message(grup);
                    break;
                case 33033:  //������ �� �������
                    message(grup);
                    break;
                case 33034:  //�������� ��������, �� 
                    message(grup);
                    break;
                case 33035:  //TODO ������ �� ����������� �� ��.�. 
                    message(grup);
                    break;
                case 33040:  //����� �������, �� 
                case 38040:  //����� �������, ��     
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33050:  //���, �� 
                case 38050:  //���, ��     
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33060:  //���������� �� ��� 
                case 38060:  //���������� �� ���                    
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33062:  //���� ������ �������� 
                case 34062:  //���� ������ �������� 
                    message(grup);
                    break;
                case 33063: //�������� ���� �������, �� 
                case 34063: //�������� ���� �������, �� 
                {
                    Com5t glass = elem5e.owner.childs.stream().filter(el -> el.type == Type.GLASS).findFirst().orElse(null);
                    if (glass != null) {
                        double weight = ((glass.width() * glass.height()) / 1000000) * glass.artiklRecAn.getDbl(eArtikl.density);
                        if (UCom.containsNumbExp(rec.getStr(TEXT), weight) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 33066:  //���� ����� ������� � �������
                case 34066:  //���� ����� ������� � �������    
                    if (!UPar.is_INT_33066_34066(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 33067:  //���� �������� �������� �������    
                case 34067:  //���� �������� �������� ������� 
                case 38067:  //���� �������� �������� �������    
                case 39067:  //���� �������� �������� �������
                case 40067:  //���� �������� �������� �������                     
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID1) == false) {
                        return false;
                    }
                    break;
                case 33068:  //���� �����. �������� �������    
                case 34068:  //���� �����. �������� ������� 
                case 38068:  //���� �����. �������� ������� 
                case 39068:  //���� �����. �������� �������
                case 40068:  //���� �����. �������� �������    
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID2) == false) {
                        return false;
                    }
                    break;
                case 33069:  //���� �����. �������� �������    
                case 34069:  //���� �����. �������� ������� 
                case 38069:  //���� �����. �������� ������� 
                case 39069:  //���� �����. �������� ������� 
                case 40069:  //���� �����. �������� �������  
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID3) == false) {
                        return false;
                    }
                    break;
                case 33071:  //��������� ���� 
                case 34071:  //��������� ����
                    if (UPar.is_1005x6_2005x6_3005_4005_11005_12005_31050_33071_34071(rec.getStr(TEXT), elem5e) == false) {
                        return false;
                    }
                    break;
                case 33073:  //��� ��������� (;)
                    message(grup);
                    break;
                case 33074:  //�� ����������� �������
                    message(grup);
                    break;
                case 33078:  //������� ���������� 
                case 34078:  //������� ���������� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33081:  //��� ��������/����������� ���� ���������, � 
                case 34081:  //��� ��������/����������� ���� ���������, �                        
                    message(grup);
                    break;
                case 33083:  //������ �����. ���� ���������, � 
                case 34083:  //������ �����. ���� ���������, � 
                    message(grup);
                    break;
                case 33088:  //������ ������� ���� ���������, �
                case 34088:  //������ ������� ���� ���������, �
                    message(grup);
                    break;
                case 33095:  //���� ������� ������� ����������� 
                case 34095:  //���� ������� ������� �����������
                case 38095:  //���� ������� ������� �����������
                case 39095:  //���� ������� ������� �����������
                case 40095:  //���� ������� ������� ����������� 
                    if (!UPar.is_11095_12095_31095_33095_34095_37095_38095_39095_40095(rec.getStr(TEXT), winc.nuni)) {
                        return false;
                    }
                    break;
                case 33099:  //������������, �/�. 
                case 34099:  //������������, �/�.
                case 38099:  //������������, �/�. 
                case 39099:  //������������, �/�. 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34009: //���� ��� �������������� �������� 
                    message(grup);
                    break;
                case 34010:  //������ ����������� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34012:  //��� �������� ���������� � (*)
                    message(grup);
                    break;
                case 34013:  //������ ������������� �������� �� �������� 
                    message(grup);
                    break;
                case 34015:  //������ ����� �� 
                    message(grup);
                    break;
                case 34016:  //���������� ������� ������� 
                    message(grup);
                    break;
                case 34030:  //[ * ����-� ] 
                case 39030:  //[ * ����-� ]     
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34049:  //�������� �� ������� �� ������/�����, �� 
                    message(grup);
                    break;
                case 34050: //��������, ��
                    message(grup);
                    break;
                case 34051:  //��������, �� 
                case 39020:  //��������, �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34052:  //�������� �� ������� ���� �������, �� 
                    message(grup);
                    if (elem5e.spcRec.getParam("0", 31052).equals(rec.getStr(TEXT)) == false) {
                        mapParam.put(grup, rec.getStr(TEXT));
                    }
                    break;
                case 34060:  //����������
                case 39060:  //����������
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34064:  //���� �������� ���������� ��� �������� 
                    message(grup);
                    break;
                case 34070:  //�����, �� 
                case 39070:  //�����, ��
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34072:  //�������� �� ������ �������, �� 
                    message(grup);
                    break;
                case 34075:  //���� ����
                case 39075:  //���� ���� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34077:  //������ ����_����_1/����_����_2, � 
                case 39077:  //������ ����_����_1/����_����_2, � 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34079:  //����� ����������� �� ������, �� 
                    message(grup);
                    break;
                case 34080:  //����� ����������� � �����, �� 
                    message(grup);
                    break;
                case 34097:  //������������ �� ����� 
                    message(grup);
                    break;
                case 38004:  //������ 
                case 39005:  //������ 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 38010:  //����� ������� 
                case 39002:  //����� ������� 
                    if (UPar.is_38010_39002(elem5e, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 38037:  //�������� ��������� �������� 
                case 39037:  //�������� ��������� �������� 
                case 40037:  //�������� ��������� �������� 
                    if (UPar.is_31037_38037_39037_40037(elem5e, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 38039:  //��� ���� ���������� 
                case 39039:  //��� ���� ���������� 
                    if (UPar.is_1039_38039_39039(elem5e, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 38081:  //���� ������� ������� ������� 
                case 39081:  //���� ������� ������� ������� 
                    if (elem5e.artiklRecAn.getStr(eArtikl.code).equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 38108:  //��������� ������������ ����� ��� �� 
                case 39108:  //��������� ������������ ����� ��� �� 
                case 40108:  //��������� ������������ ����� ��� �� 
                    message(grup);
                    break;
                case 38109:  //��������� ���������� ������ 
                case 39109:  //��������� ���������� ������ 
                case 40109:  //��������� ���������� ������ 
                    message(grup);
                    break;
                case 38113:  //���������� �������� �� 
                case 39113:  //���������� �������� �� 
                case 40113:  //���������� �������� �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 39063:  //��������� ���������� �� ���������� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 39080:  //��� ������� ������, �� 
                    message(grup);
                    break;
                case 39093:  //���������� ������� :
                    mapParam.put(grup, rec.getStr(TEXT));
                    message(grup);
                    break;
                case 39097:  //������������ �� ��������� 
                    message(grup);
                    break;
                case 40004:  //������ ����������, �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 40005:  //�������� ������/������, �� 
                case 40010:  //�������� �� ������� ������/��������, �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 40006:  //������ ����������, �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 40007:  //������ ������� ������ 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "��� " + grup + "  �� ���������!!!";
            }
        } catch (Exception e) {
            System.err.println("������:param.ElementDet.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
