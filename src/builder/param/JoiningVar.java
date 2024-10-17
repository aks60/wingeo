package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eElement;
import domain.eJoinpar1;
import domain.eJoinvar;
import domain.eSystree;
import java.util.List;
import builder.Wincalc;
import builder.model.AreaStvorka;
import builder.model.ElemJoining;
import builder.model.ElemSimple;
import builder.model.UGeo;
import common.UCom;
import common.listener.ListenerAction;
import enums.Layout;
import enums.Type;
import enums.TypeJoin;
import java.util.ArrayList;

//����������
public class JoiningVar extends Par5s {

    public JoiningVar(Wincalc winc) {
        super(winc);
        listenerList = new ArrayList<ListenerAction>();
    }

    public JoiningVar(Wincalc winc, boolean shortPass) {
        super(winc);
        listenerList = new ArrayList<ListenerAction>();
        this.shortPass = shortPass;
    }

    //1000 - ����������� ����������, 2000 - ������� �� ��, 3000 - ������� (�����, ������), 4000 - � �������� ����������
    public boolean filter(ElemJoining elemJoin, Record joinvarRec) {

        listenerList.clear();
        List<Record> paramList = eJoinpar1.filter(joinvarRec.getInt(eJoinvar.id));
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //���� �� ���������� ��������� ����������
        for (Record rec : paramList) {
            if (check(elemJoin, rec, joinvarRec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(ElemJoining elemJoin, Record rec, Record var) {

        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {
                case 1005:  //��������� ����� ��� ��������1/��������2
                case 2005:  //��������� ����� ��� ��������1/��������2
                case 3005:  //��������� ����� ��� ��������1/��������2
                case 4005:  //��������� ����� ��� ��������1/��������2
                    if (UCom.containsNumb(rec.getStr(TEXT), elemJoin.elem1.type.id, elemJoin.elem2.type.id) == false) {
                        return false;
                    }
                    break;
                case 1008:  //����������� ���������� ���., ��
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 1010:  //������� ���������� 
                case 4010:  //������� ����������   
                    if ("��".equals(rec.getStr(TEXT))) {
                        if ((winc.root.frames.contains(elemJoin.elem1) && winc.root.frames.contains(elemJoin.elem2)) == false) {
                            return false;
                        }

                    } else if ("���".equals(rec.getStr(TEXT))) {
                        if ((winc.root.frames.contains(elemJoin.elem1) && winc.root.frames.contains(elemJoin.elem2)) == false) {
                            return false;
                        }
                    }
                    break;
                case 1011: //��� �������� 1 ������ ������ 
                case 4011: //��� �������� 1 ������ ������     
                {
                    boolean substr = false;
                    List<Record> elementList = eElement.filter2(elemJoin.elem1.artiklRec.getInt(eArtikl.code));
                    for (Record elementRec : elementList) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr = true;
                            break;
                        }
                    }
                    if (substr == false) {
                        return false;
                    }
                }
                break;
                case 1012: //��� �������� 2 ������ ������                  
                case 4012: //��� �������� 2 ������ ������     
                {
                    boolean substr = false;
                    List<Record> elementList = eElement.filter2(elemJoin.elem2.artiklRec.getInt(eArtikl.code));
                    for (Record elementRec : elementList) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr = true;
                            break;
                        }
                    }
                    if (substr == false) {
                        return false;
                    }
                }
                break;
                case 1013:  //��� ��������� �� ������ ������
                case 2013:  //��� ��������� �� ������ ������ 
                case 3013:  //��� ��������� �� ������ ������
                case 4013: //��� ��������� �� ������ ������  
                {
                    List<Record> elementList1 = eElement.filter2(elemJoin.elem1.artiklRec.getInt(eArtikl.code));
                    boolean substr1 = false;
                    for (Record elementRec : elementList1) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr1 = true;
                            break;
                        }
                    }
                    boolean substr2 = false;
                    List<Record> elementList2 = eElement.filter2(elemJoin.elem2.artiklRec.getInt(eArtikl.code));
                    for (Record elementRec : elementList2) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr2 = true;
                            break;
                        }
                    }
                    if (substr1 == true || substr2 == true) {
                        return false;
                    }
                }
                break;
                case 1020:  //����������� ���� � ���������, �
                    if (UCom.containsNumbJust(rec.getStr(TEXT),
                            UGeo.anglHor(elemJoin.elem1.x1(), elemJoin.elem1.y1(), elemJoin.elem1.x2(), elemJoin.elem1.y2())) == true) {
                        return false;
                    }
                    break;
                case 1035:  //������� ������� 
                    message(rec.getInt(GRUP));
                    break;
                case 1039:  //��� ���� ���������� 
                    if (UPar.is_1039_38039_39039(elemJoin.elem1, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 1040:  //������, �� (�������� ���� ���� � �������. �������� ps3)
                    //�������� ����������� �� ������ ����� ��. ����������� AreaStvorka()
                    //����������� ���� ����. ��������� �����������
                    if (elemJoin.elem1.type == Type.STVORKA_SIDE) {
                        listenerList.add(() -> {
                            AreaStvorka stv = (AreaStvorka) elemJoin.elem1.owner;
                            if (elemJoin.elem1.layout() == Layout.BOTT) {
                                stv.offset[0] = rec.getDbl(TEXT);
                            } else if (elemJoin.elem1.layout() == Layout.RIGHT) {
                                stv.offset[1] = rec.getDbl(TEXT);
                            } else if (elemJoin.elem1.layout() == Layout.TOP) {
                                stv.offset[2] = rec.getDbl(TEXT);
                            } else if (elemJoin.elem1.layout() == Layout.LEFT) {
                                stv.offset[3] = rec.getDbl(TEXT);
                            }
                        });
                    }
                    break;
                case 1043: //����������� �������� �������, �� 
                {
                    double area = winc.width() * winc.height() / 1000000;
                    if (UCom.containsNumbExp(rec.getStr(TEXT), area) == false) {
                        return false;
                    }
                }
                break;
                case 1090:  //�������� �� �������, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 1095:  //���� ������� ������� ����������� 
                case 2095:  //���� ������� ������� ����������� 
                case 3095:  //���� ������� ������� ����������� 
                case 4095: //���� ������� ������� ����������� 
                {
                    Record systreefRec = eSystree.find(winc.nuni);
                    String[] arr = rec.getStr(TEXT).split(";");
                    List<String> arrList = List.of(arr);
                    boolean ret = false;
                    for (String str : arrList) {
                        if (systreefRec.getInt(eSystree.types) == Integer.valueOf(str) == true) {
                            ret = true;
                        }
                    }
                    if (ret == false) {
                        return false;
                    }
                }
                break;
                case 1098:  //������� (�������)
                case 2098:  //������� (�������) 
                case 3098:  //������� (�������)
                case 4098:  //������� (�������)
                    message(rec.getInt(GRUP));
                    break;
                case 1099:  //������������, �/�. 
                case 2099:  //������������, �/�. 
                case 3099:  //������������, �/�.
                case 4099:  //������������, �/�. 
                    elemJoin.costs = rec.getStr(TEXT);
                    break;
                case 1097:  //������������ �� ����� 
                    message(rec.getInt(GRUP));
                    break;
                case 1085:  //������� �� �������� 
                case 2085:  //������� �� �������� 
                case 3085:  //������� �� ��������  
                case 4085:  //������� �� ��������     
                    message(rec.getInt(GRUP));
                    break;
                case 2003:  //���� �������� 
                case 3003:  //���� �������� 
                    if (var.getInt(eJoinvar.types) == 30 || var.getInt(eJoinvar.types) == 31) {
                        if ("�����".equals(rec.getStr(TEXT))) {
                            if (elemJoin.type() != TypeJoin.ANG1) {
                                return false;
                            }
                        } else if ("������".equals(rec.getStr(TEXT))) {
                            if (elemJoin.type() != TypeJoin.ANG2) {
                                return false;
                            }
                        }
                    }
                    break;
                case 2010:  //���� �����������, �
                case 3010:  //���� �����������, �
                case 4020:  //����������� ����, �
                    if (UCom.containsNumbJust(rec.getStr(TEXT), elemJoin.angleBetween()) == false) { //����������� ����, �
                        return false;
                    }
                    break;
                case 2012: //��� ��������� ������ ������
                case 3012: //��� ��������� ������ ������ 
                {
                    List<Record> elementList1 = eElement.filter2(elemJoin.elem1.artiklRec.getInt(eArtikl.id));
                    boolean substr1 = false;
                    for (Record elementRec : elementList1) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr1 = true;
                            break;
                        }
                    }
                    boolean substr2 = false;
                    List<Record> elementList2 = eElement.filter2(elemJoin.elem2.artiklRec.getInt(eArtikl.id));
                    for (Record elementRec : elementList2) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr1 = true;
                            break;
                        }
                    }
                    if (!(substr1 == true || substr2 == true)) {
                        return false;
                    }
                }
                break;
                case 2015:  //���������� ��������1/��������2, � 
                case 3015:  //���������� ��������1/��������2, �
                case 4015:  //���������� ��������1/��������2, �
                    if (UCom.containsNumb(rec.getStr(TEXT),
                            UGeo.anglHor(elemJoin.elem1.x1(), elemJoin.elem1.y1(), elemJoin.elem1.x2(), elemJoin.elem1.y2()),
                            UGeo.anglHor(elemJoin.elem2.x1(), elemJoin.elem2.y1(), elemJoin.elem2.x2(), elemJoin.elem2.y2())) == false) {
                        return false;
                    }
                    break;
                case 2020:  //����������� ����, �
                case 3020:  //����������� ����, �
                    if (UCom.containsNumbJust(rec.getStr(TEXT), elemJoin.angleBetween()) == false) { //����������� ����, �
                        return false;
                    }
                    break;
                case 2030:  //������� ��������1/��������2, �� 
                case 3050:  //������� ��������1/��������2, ��  
                case 4050:
                    listenerList.add(() -> {
                        String strTxt = rec.getStr(TEXT);
                        char normal = strTxt.charAt(strTxt.length() - 1);
                        if (normal == '@') {
                            strTxt = strTxt.substring(0, strTxt.length() - 1);
                        }
                        String arr[] = strTxt.split("/");
                        elemJoin.elem1.spcRec.width += UCom.getDbl(arr[0]);
                        elemJoin.elem2.spcRec.width += UCom.getDbl(arr[1]);
                    });
                    break;
                case 2055:  //����������� ����� ���� 
                    message(rec.getInt(GRUP));
                    break;
                case 2061:  //������ ��� ��������1/��������2, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 2064: //�������� ��� ������� ���.1/���.2, �� 
                case 3064: //�������� ��� ������� ���.1/���.2 , �� 
                    listenerList.add(() -> {
                        String[] arr = rec.getStr(TEXT).replace(",", ".").split("/");
                        elemJoin.elem1.spcRec.width += UCom.getDbl(arr[0]);
                        elemJoin.elem2.spcRec.width += UCom.getDbl(arr[1]);
                    });
                    break;
                case 2066:  //������ ����� ���� �������� 
                    message(rec.getInt(GRUP));
                    break;
                case 2097:  //������������ �� ����� 
                    message(rec.getInt(GRUP));
                    break;
                case 3002:  //��� L-��������� �������� 
                    if (elemJoin.vid == 0 && "������� L-���".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    } else if (elemJoin.vid == 1 && "��������� �-���".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 4002:  //��� �-��������� ��������    
                    if (elemJoin.vid == 0 && "������� �-���.".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    } else if (elemJoin.vid == 1 && "��������� �-���".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    } else if (elemJoin.vid == 2 && "������� Y-���".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 3030:  //�������� ��������1/��������2, ��
                case 3031:  //�������� ��������1/��������2, �� 
                    listenerList.add(() -> {
                        String[] arr = rec.getStr(TEXT).replace(",", ".").split("/");
                        elemJoin.elem1.spcRec.width -= UCom.getDbl(arr[0]);
                        elemJoin.elem2.spcRec.width -= UCom.getDbl(arr[1]);
                    });
                    break;
                case 3045:  //���������� �� ������ �������, �� 
                case 4045:  //���������� �� ������ �������, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 3083:  //�������� ������� ������� 
                case 4083:  //�������� ������� ������� 
                    message(rec.getInt(GRUP));
                    break;
                case 3088:  //������� ���������� ��� ������ 
                    message(rec.getInt(GRUP));
                    break;
                case 3097:  //������������ �� ����� 
                case 4097:  //������������ �� ����� 
                    message(rec.getInt(GRUP));
                    break;
                case 4018: //�� ����� �� �����, �� 
                {
                    AreaStvorka stv = (AreaStvorka) elemJoin.elem1.owner;
                    ElemSimple imp = elemJoin.elem1;
                    if (Math.abs(imp.y2() - stv.knobHeight) < rec.getDbl(TEXT)) {
                        return false;
                    }
                }
                break;
                case 4040:  //������ �� ��� �������, ��.
                case 4044:  //������ �� ���� ������, �� 
                    listenerList.add(() -> {
                        elemJoin.elem1.spcRec.width -= rec.getDbl(TEXT);
                    });
                    break;
                case 4046:  //����� �������� 1, �� 
                    listenerList.add(() -> {
                        elemJoin.elem1.spcRec.width += rec.getDbl(TEXT);
                    });
                    break;
                case 4061:  //������������ ������ ���, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 4064:  //�������� ��� �������, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 4090:  //���������� ������� ����������
                    message(rec.getInt(GRUP));
                    break;
                case 4800:  //��� ��������� 
                    message(rec.getInt(GRUP));
                    break;
                case 4801:  //���.���������
                    message(rec.getInt(GRUP));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "��� " + grup + "  �� ���������!!!";
            }
        } catch (Exception e) {
            System.err.println("������:JoiningVar.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }

    public boolean check(ElemJoining elemJoin, Record rec) {
        return check(elemJoin, rec, null);
    }
}
