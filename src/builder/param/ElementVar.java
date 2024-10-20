package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eElement;
import domain.eElempar1;
import enums.Layout;
import java.util.List;
import builder.Wincalc;
import builder.model.AreaStvorka;
import builder.model.Com5t;
import builder.model.ElemSimple;
import builder.model.UGeo;
import common.UCom;
import common.listener.ListenerAction;
import enums.Type;
import enums.TypeJoin;
import java.util.ArrayList;
import org.locationtech.jts.geom.Envelope;

//������� 31000, 37000
public class ElementVar extends Par5s {

    public ElementVar(Wincalc winc) {
        super(winc);
        listenerList = new ArrayList<ListenerAction>();
    }

    public boolean filter(ElemSimple elem5e, Record elementRec) {

        listenerList.clear();
        List<Record> paramList = eElempar1.filter(elementRec.getInt(eElement.id)); //������ ���������� ��������� �������������
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //���� �� ���������� �������
        for (Record rec : paramList) {
            if (check(elem5e, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(ElemSimple elem5e, Record rec) {
        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {

                case 31000: //��� ���������������� ���� ���������� 
                    if (!UPar.is_STRING_XX000(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 31001: //������������ ���������� �������, �� 
                {
                    List<ElemSimple> glassList = UCom.filter(winc.listElem, Type.GLASS);
                    double depth = 0;
                    for (ElemSimple glass : glassList) {
                        if (glass.artiklRecAn.getDbl(eArtikl.depth) > depth) {
                            depth = (glass.artiklRecAn.getDbl(eArtikl.depth));
                        }
                    }
                    if (UCom.containsNumbJust(rec.getStr(TEXT), depth) == false) {
                        return false;
                    }
                }
                break;
                case 31002:  //���� ������� 
                    if ("�������".equals(rec.getStr(TEXT)) == true && elem5e.area.getNumPoints() < Com5t.MAXSIDE) {
                        return false;
                    } else if ("������".equals(rec.getStr(TEXT)) == true && elem5e.area.getNumPoints() > Com5t.MAXSIDE) {
                        return false;
                    }
                    break;
                case 31003:  //���� ������������ �������  T-���.
                    if (rec.getStr(TEXT).equals(winc.listJoin.elem(elem5e, 0).artiklRecAn.getStr(eArtikl.code)) == true) {
                        if (winc.listJoin.join(elem5e, 0).type() != TypeJoin.TIMP && winc.listJoin.join(elem5e, 0).type() != TypeJoin.TCON) {
                            return false;
                        }
                    } else if (rec.getStr(TEXT).equals(winc.listJoin.elem(elem5e, 1).artiklRecAn.getStr(eArtikl.code))) {
                        if (winc.listJoin.join(elem5e, 1).type() != TypeJoin.TIMP && winc.listJoin.join(elem5e, 1).type() != TypeJoin.TCON) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                    break;
                case 31004: //���� ����������� ������� 
                {
                    ElemSimple el = winc.listJoin.elem(elem5e, 2);
                    if (el != null) {
                        if (rec.getStr(TEXT).equals(el.artiklRecAn.getStr(eArtikl.code)) == false) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                break;
                case 31005:  //���� �������� �������� ���������� 
                case 37005:  //���� �������� �������� ���������� 
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.colorID1) == false) {
                        return false;
                    }
                    break;
                case 31006:  //���� �����. �������� ���������� 
                case 37006:  //���� �����. �������� ����������  
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.colorID2) == false) {
                        return false;
                    }
                    break;
                case 31007:  //���� �����. �������� ���������� 
                case 37007:  //���� �����. �������� ����������  
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.colorID3) == false) {
                        return false;
                    }
                    break;
                case 31008: //����������� ���������� �������, �� 
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 31011: //������� ��������/����������� ����������, ��
                {
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if (glassList.get(0).type == Type.GLASS && glassList.get(1).type == Type.GLASS) {
                        if (UCom.containsNumb(rec.getStr(TEXT),
                                glassList.get(0).artiklRec.getDbl(eArtikl.depth),
                                glassList.get(1).artiklRec.getDbl(eArtikl.depth)) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 31012: //��� �������� ����������, ��", ������ ��� PS3
                {
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if (glassList.get(1).type == Type.GLASS) {
                        if (UCom.containsNumbJust(rec.getStr(TEXT),
                                glassList.get(1).artiklRec.getDbl(eArtikl.depth)) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 31013: //��� ����������� ����������, ��", ������ ��� PS3
                {
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if (glassList.get(0).type == Type.GLASS) {
                        if (UCom.containsNumbJust(rec.getStr(TEXT),
                                glassList.get(0).artiklRec.getDbl(eArtikl.depth)) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 31014: //���������� ���������� ������� 
                {
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if ("��".equals(rec.getStr(TEXT)) == true) {
                        if (glassList.get(0).artiklRecAn.getDbl(eArtikl.depth) != glassList.get(1).artiklRecAn.getDbl(eArtikl.depth)) {
                            return false;
                        }
                    } else {
                        if (glassList.get(0).artiklRecAn.getDbl(eArtikl.depth) == glassList.get(1).artiklRecAn.getDbl(eArtikl.depth)) {
                            return false;
                        }
                    }
                }
                break;
                case 31015:  //��������� ������� �� ������� 
                    message(grup);
                    if ("����".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 31016:  //�����_��_����,_��/������_,�� ������������ 
                    message(grup);
                    break;
                case 31017: //��� ������� �������� ������ 
                case 37017: //��� ������� �������� ������ 
                    if (UPar.is_13017_14017_24017_25017_31017_33017_34017_37017_38017(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 31019:  //������� ������� �������
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 31020:  //����������� ���� � ���������, �
                    if (UCom.containsNumbJust(rec.getStr(TEXT), UGeo.anglHor(elem5e.x1(), elem5e.y1(), elem5e.x2(), elem5e.y2())) == false) {
                        return false;
                    }
                    break;
                case 31033: //���� ���������� ������� 
                    if (rec.getStr(TEXT).equals(winc.listJoin.elem(elem5e, 0).artiklRecAn.getStr(eArtikl.code)) == false) {
                        return false;
                    }
                    break;
                case 31034:  //���� ��������� ������� 
                    if (rec.getStr(TEXT).equals(winc.listJoin.elem(elem5e, 1).artiklRecAn.getStr(eArtikl.code)) == false) {
                        return false;
                    }
                    break;
                case 31035:  //������� ������� 
                    message(grup);
                    break;
                case 31037:  //�������� ��������� �������� 
                    if (UPar.is_31037_38037_39037_40037(elem5e, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 31040:  //�������� �������� ��������, �� 
                    message(grup);
                    break;
                case 31041:  //����������� ����� �������, �� 
                    if (UCom.containsNumbJust(rec.getStr(TEXT), elem5e.length()) == false) {
                        return false;
                    }
                    break;
                case 31042: //������������ �����, ��"
                    message(grup);
                    break;
                case 31050: //��������� ����� ��� 
                    if (UPar.is_1005x6_2005x6_3005_4005_11005_12005_31050_33071_34071(rec.getStr(TEXT), elem5e) == false) {
                        return false;
                    }
                    break;
                case 31051:  //���� ������� ��������� 
                    if (elem5e.owner.type == Type.STVORKA) {
                        if ("�������".equals(rec.getStr(TEXT)) == true && ((AreaStvorka) elem5e.owner).knobRec.getInt(eArtikl.id) == -3) {
                            return false;
                        } else if ("�������".equals(rec.getStr(TEXT)) == true && ((AreaStvorka) elem5e.owner).knobRec.getInt(eArtikl.id) != -3) {
                            return false;
                        }
                    }
                    break;
                case 31052:  //�������� � ������������, �� 
                    listenerList.add(() -> {
                        elem5e.spcRec.width = elem5e.spcRec.width + rec.getDbl(TEXT);
                    });
                    break;
                case 31054:  //���� �������� �������� �������
                case 37054:  //���� �������� �������� �������    
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID1) == false) {
                        return false;
                    }
                    break;
                case 31055:  //���� �����. � �����. �������� ���.
                case 37055:  //���� �����. � �����. �������� ���. 
                    if ((UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID2) == true
                            && UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID3) == true) == false) {
                        return false;
                    }
                    break;
                case 31056:  //���� �����. ��� ����. �������� ���. 
                case 37056:  //���� ����. ��� ����. �������� ���. 
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID2) == false
                            && UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID3) == false) {
                        return false;
                    }
                    break;
                case 31057:  //���������� �������� ����� ������� 
                    if (elem5e.colorID2 != elem5e.colorID3) {
                        return false;
                    }
                    break;
                case 31060:  //���������� ���� ����� �����������, � 
                    if ((UCom.containsNumbJust(rec.getStr(TEXT), winc.listJoin.join(elem5e, 0).angleBetween()) == true
                            || UCom.containsNumbJust(rec.getStr(TEXT), winc.listJoin.join(elem5e, 1).angleBetween()) == true) == false) {
                        return false;
                    }
                    break;
                case 31073:  //����������� ����� ������
                    message(grup);
                    break;
                case 31074:  //�� ����������� �������
                    message(grup);
                    break;
                case 31080:  //���������-��������������
                    message(grup);
                    break;
                case 31081:  //��� ��������/����������� ���� ���������, � 
                    message(grup);
                    break;
                case 31085:  //������� �� �������� 
                case 37085:  //������� �� ��������   
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 31090:  //��������� ������ �������� 
                    listenerList.add(() -> {
                        if ("��".equals(rec.getStr(TEXT))) {
                            int color = elem5e.spcRec.colorID2;
                            elem5e.spcRec.colorID2 = elem5e.spcRec.colorID3;
                            elem5e.spcRec.colorID2 = color;
                        }
                    });
                    break;
                case 31095:  //���� ������� ������� ����������� 
                case 37095:  //���� ������� ������� �����������                    
                    if (!UPar.is_11095_12095_31095_33095_34095_37095_38095_39095_40095(rec.getStr(TEXT), winc.nuni)) {
                        return false;
                    }
                    break;
                case 31098:  //�������, �������) 
                    message(grup);
                    break;
                case 31099:  //������������, �/�. 
                case 37099:  //������������, �/�.  
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 31097:  //������������ �� ����� 
                    message(grup);
                    break;
                case 31800:  //��� ��������� 
                    message(grup);
                    break;
                case 31801:  //���.���������
                    message(grup);
                    break;
                case 37001:  //��������� ������ 
                    message(grup);
                    break;
                case 37002: //���� ������� ������� �������
                {
                    Object r = elem5e.root.frames.stream().filter(f -> f.artiklRecAn.getStr(eArtikl.code).equals(rec.getStr(TEXT))).findFirst().orElse(null);
                    if (r == null) {
                        return false;
                    }
                }
                break;
                case 37008:  //��� ������ 
                    if (!UPar.is_13003_14005_15005_37008(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 37009: //��� ���������� 
                {
                    if ("�������������".equals(rec.getStr(TEXT)) && elem5e.area.isRectangle() == false) {
                        return false;

                    } else if ("�������".equals(rec.getStr(TEXT)) && elem5e.area.getNumPoints() > Com5t.MAXSIDE == false) {
                        return false;

                    } else if ("������������".equals(rec.getStr(TEXT))
                            && (elem5e.area.isRectangle() == true || elem5e.area.getNumPoints() > Com5t.MAXSIDE == true)) {
                        return false;
                    }
                }
                break;
                case 37010:  //����������� ������/������ �����, �� 
                    if (UCom.containsNumb(rec.getStr(TEXT), elem5e.width(), elem5e.height()) == false) {
                        return false;
                    }
                    break;
                case 37030:  //����������� �������, ��.�. 
                    Envelope env = elem5e.area.getEnvelopeInternal();
                    if (UCom.containsNumbJust(rec.getStr(TEXT), env.getWidth() / 1000 * env.getHeight() / 1000) == false) {
                        return false;
                    }
                    break;
                case 37042: //���������� ����������� ��������� �/�
                {
                    double max = (elem5e.width() > elem5e.height()) ? elem5e.width() : elem5e.height();
                    double min = (elem5e.width() > elem5e.height()) ? elem5e.height() : elem5e.width();
                    if (UCom.containsNumbJust(rec.getStr(TEXT), max / min) == false) {
                        return false;
                    }
                }
                break;
                case 37043: //����. ����������� ��������� (�/�)
                {
                    double max = (elem5e.width() > elem5e.height()) ? elem5e.width() : elem5e.height();
                    double min = (elem5e.width() > elem5e.height()) ? elem5e.height() : elem5e.width();
                    if (rec.getDbl(TEXT) < max / min) {
                        return false;
                    }
                }
                break;
                case 37080: //���������-��������������
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37098:  //������� �������
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37097:  //������������ �� 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37108:  //������������ ����� 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37310:  //������������� �������������, �2*��/�� 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37320:  //��������������������, �3/ �*�2
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37330:  //�������������, ��� 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37340:  //����������� ����������� ����� 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37350:  //������������� �������� ���������, �� 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37351:  //����� ����������� 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "��� " + grup + "  �� ���������!!!";
            }
        } catch (Exception e) {
            System.err.println("������:param.ElementVar.check()  parametr=" + grup + "    " + e);
            return false;
        }

        return true;
    }
}
