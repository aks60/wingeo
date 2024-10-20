package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eFurndet;
import domain.eFurnpar2;
import enums.Layout;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.making.SpcRecord;
import builder.model.Com5t;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.ElemSimple;
import common.UCom;
import domain.eColor;
import domain.eGroups;
import enums.LayoutKnob;
import enums.Type;
import java.util.Map;

//���������
public class FurnitureDet extends Par5s {

    public FurnitureDet(Wincalc winc) {
        super(winc);
    }

    public boolean filter(HashMap<Integer, String> mapParam, AreaSimple areaStv, Record furndetRec) {

        this.detailRec = furndetRec;
        List<Record> tableList = eFurnpar2.filter(furndetRec.getInt(eFurndet.id));
        if (filterParamDef(tableList) == false) {
            return false; //��������� �� ���������
        }
        //���� �� ���������� ���������
        for (Record rec : tableList) {
            if (check(mapParam, areaStv, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(HashMap<Integer, String> mapParam, AreaSimple areaStv, Record rec) {

        AreaStvorka elemStv = (AreaStvorka) areaStv;
        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {

                case 24001: //����� ������� 
                case 25001: //����� ������� 
                {
                    //"�������������", "�� �������������", "�� �������", "�������" (TypeElem.AREA - �������)
                    if ("�������������".equals(rec.getStr(TEXT)) && Type.RECTANGL.equals(areaStv.type) == false
                            && Type.AREA.equals(areaStv.type) == false && Type.STVORKA.equals(areaStv.type) == false) {
                        return false;
                    } else if ("��������������".equals(rec.getStr(TEXT)) && Type.TRAPEZE.equals(areaStv.type) == false) {
                        return false;
                    } else if ("�������".equals(rec.getStr(TEXT)) && Type.ARCH.equals(areaStv.type) == false) {
                        return false;
                    } else if ("�� �������".equals(rec.getStr(TEXT)) && Type.ARCH.equals(areaStv.type) == true) {
                        return false;
                    }
                    break;
                }
                case 24002:  //���� ������� ������� 
                case 25002:  //���� ������� ������� 
                    if (areaStv.frames.stream().filter(el -> el.artiklRec.getStr(eArtikl.code).equals(rec.getStr(TEXT))).findFirst().orElse(null) == null) {
                        return false;
                    }
                    break;
                case 24003:  //���� ������� ������ 
                case 25003:  //���� ������� ������ 
                    message(rec.getInt(GRUP));
                    break;
                case 24004: //���� ������� ��������� � �������� 
                    if (areaStv.frames.stream().filter(el -> UCom.elem(winc.listJoin, el, 2).artiklRec.getStr(eArtikl.code).equals(rec.getStr(TEXT))).findFirst().orElse(null) == null) {
                        return false;
                    }
                    break;
                case 24005:  //���� �������� ������� 
                case 25005:  //���� �������� ������� 
                    if (areaStv.frames.stream().filter(el -> UCom.containsColor(rec.getStr(TEXT), el.colorID1) == true).findFirst().orElse(null) == null) {
                        return false;
                    }
                    break;
                case 24006:  //���������� ��������
                    if ("�� �������� �����".equals(rec.getStr(TEXT))) {
                        if (elemStv.knobColor != detailRec.getInt(eFurndet.color_fk)) {
                            return false;
                        }
                    } else if ("�� �������� �������".equals(rec.getStr(TEXT))) {
                        if (elemStv.loopColor != detailRec.getInt(eFurndet.color_fk)) {
                            return false;
                        }
                    } else if ("�� �������� �����".equals(rec.getStr(TEXT))) {
                        if (elemStv.lockColor != detailRec.getInt(eFurndet.color_fk)) {
                            return false;
                        }
                    }
                    break;
                case 24007: //���� �������� ����� 
                case 25007: //���� �������� �����                  
                {
                    String name = eColor.find(((AreaStvorka) areaStv).knobColor).getStr(eColor.name);
                    if (name.equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                }
                break;
                case 24008: //���� ����� ������� 
                case 25008: //���� ����� �������   
                {
                    int series_id = UCom.layout(areaStv.frames, Layout.BOTT).artiklRec.getInt(eArtikl.groups4_id);
                    String name = eGroups.find(series_id).getStr(eGroups.name);
                    if (name.equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                }
                break;
                case 24009:  //���� �������� ������� 
                case 25009:  //���� �������� �������                   
                    for (ElemSimple elem : areaStv.frames) {
                        for (SpcRecord spc : elem.spcRec.spcList) {
                            if (spc.artiklRec().getInt(eArtikl.level1) == 2 && spc.artiklRec().getInt(eArtikl.level2) == 12) {
                                String name = eColor.find(spc.colorID1).getStr(eColor.name);
                                if (name.equals(rec.getStr(TEXT)) == false) {
                                    return false;
                                }
                            }
                        }
                    }
                    break;
                case 24010:  //����� ������� 
                case 25010:  //����� �������                   
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24011:  //������ �� ����� ���� 
                case 25011:  //������ �� ����� ���� 
                    message(rec.getInt(GRUP));
                    break;
                case 24012:  //����������� ����������
                    if (elemStv.typeOpen.name.equalsIgnoreCase(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 24013: //������ ���� ������ ������� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24017:  //��� ������� �������� ������ 
                case 25017:  //��� ������� �������� ������                    
                    if (UPar.is_13017_14017_24017_25017_31017_33017_34017_37017_38017(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 24030:  //���������� 
                case 25060:  //����������     
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24032:  //���������� �������� 
                case 25032:  //���������� �������� 
//                    if (winc.rootArea.type == Type.ARCH) {
//                        int k = (int) (winc.rootArea.width() / ((AreaArch) winc.rootArea).radiusArch);
//                        if (k != 2) {
//                            return false;
//                        }
//                    }
                    break;
                case 24033: //��������� ���������� 
                case 25033: //��������� ���������� 

                    if (rec.getStr(TEXT).equals("��")) {
                        for (Com5t entry : areaStv.owner.childs) {
                            if (entry.type == Type.SHTULP) {
                                return true;
                            }
                        }
                        return false;

                    } else if (rec.getStr(TEXT).equals("���")) {
                        for (Com5t entry : areaStv.owner.childs) {
                            if (entry.type == Type.SHTULP) {
                                return false;
                            }
                        }
                    }
                    break;
                case 24036:  //����� �������_X/�������_Y ������ 
                case 25036:  //����� �������_X/�������_Y ������ 
                    message(rec.getInt(GRUP));
                    break;
                case 24037:  //����� ������� �� ��������� ������ 
                case 25037:  //����� ������� �� ��������� ������ 
                    message(rec.getInt(GRUP));
                    break;
                case 24038:  //��������� C������_(L))/C������_(W) 
                case 25038:  //��������� C������_(L)/C������_(W)     
                    //TODO ���������. ��� ������ ���������. �������� ������� �������� ����������� ��� ����� ������
                    mapParamTmp.put(grup, rec.getStr(TEXT));
                    //message(rec.getInt(GRUP));
                    break;
                case 24039:  //������� ������� ����� 
                    message(rec.getInt(GRUP));
                    break;
                case 24040:  //����� �������, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24050:  //���, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24060:  //���������� �� ��� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24063: //�������� ����, �� 
                case 25063: //�������� ����, �� 
                {
                    Com5t glass = areaStv.childs.stream().filter(el -> el.type == Type.GLASS).findFirst().orElse(null);
                    if (glass != null) {
                        double weight = ((glass.width() * glass.height()) / 1000000) * glass.artiklRecAn.getDbl(eArtikl.density);
                        if (UCom.containsNumbExp(rec.getStr(TEXT), weight) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 24064: //����������� ������ �����, �� 
                case 25064: //����������� ������ �����, �� 
                    if (elemStv.knobHeight > rec.getInt(TEXT)) {
                        return false;
                    }
                    break;
                case 24065: //������������ ������ �����, �� 
                {
                    double handl_max = UCom.getDbl(rec.getStr(TEXT));
                    if (handl_max < elemStv.knobHeight) {
                        return false;
                    }
                }
                break;
                case 24067:  //���� �������� �������� ������� 
                case 25067:  //���� �������� �������� ������� 
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID1) == false) {
                        return false;
                    }
                    break;
                case 24068:  //���� �����. �������� ������� 
                case 25068:  //���� �����. �������� ������� 
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID2) == false) {
                        return false;
                    }
                    break;
                case 24069:  //���� �����. �������� ������� 
                case 25069:  //���� �����. �������� �������     
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID3) == false) {
                        return false;
                    }
                    break;
                case 24070: //���� ������ ����� "�� ��������", "�����������", "�� �����������", "�����������"
                case 25070: //���� ������ �����
                    if (LayoutKnob.CONST != elemStv.knobLayout && rec.getStr(TEXT).equals("�����������")) {
                        return false;
                    } else if (LayoutKnob.CONST == elemStv.knobLayout && rec.getStr(TEXT).equals("�� �����������")) {
                        return false;
                    } else if (LayoutKnob.MIDL != elemStv.knobLayout && rec.getStr(TEXT).equals("�� ��������")) {
                        return false;
                    } else if (LayoutKnob.VAR != elemStv.knobLayout && rec.getStr(TEXT).equals("�����������")) {
                        return false;
                    }
                    break;
                case 24072:  //����� �� ���� �������, �� 
                case 25072:  //����� �� ���� �������, ��  
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24073:  //����� �� ���� �������, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24074:  //����� �� ������ ������� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24075:  //����� �� ����� �������, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24077:  //�������� ����� �� �����, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 24078:  //����� �� ���� �������, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 24095:  //���� ������� ������� ����������� 
                case 25095:  //���� ������� ������� ����������� 
                    message(rec.getInt(GRUP));
                    break;
                case 24098:  //�������, �������) 
                case 25098:  //�������, �������) 
                    message(rec.getInt(GRUP));
                    break;
                case 24099:  //������������, �/�. 
                case 25099:  //������������, �/�.                    
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24800:  //��� �������� ���������) 
                case 25800:  //��� �������� ���������
                    message(rec.getInt(GRUP));
                    break;
                case 24801:  //���.�������� ���������
                case 25801:  //���.�������� ���������
                    message(rec.getInt(GRUP));
                    break;
                case 24802:  //��� �������. ��������� 
                case 25802:  //��� �������. ��������� 
                    message(rec.getInt(GRUP));
                    break;
                case 24803:  //���.�������. ���������
                case 25803:  //���.�������. ���������
                    message(rec.getInt(GRUP));
                    break;
                case 25013:  //���������� �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 25030:  //����������, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 25035:  //[ * ����-� ] 
                    message(rec.getInt(GRUP));
                    break;
                case 25040:  //�����, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "��� " + grup + "  �� ���������!!!";
            }
        } catch (Exception e) {
            System.err.println("������:param.FurnitureDet.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
