package builder.param;

import dataset.Record;
import domain.eGlasgrp;
import domain.eGlaspar1;
import java.util.List;
import builder.Wincalc;
import builder.model.Com5t;
import builder.model.ElemGlass;
import builder.model.ElemSimple;
import builder.model.UGeo;
import common.UCom;
import domain.eArtikl;
import java.util.Arrays;
import java.util.Collections;
import org.locationtech.jts.geom.LineSegment;

//����������
public class FillingVar extends Par5s {

    private int[] parGrup = {13015, 13017, 13081, 13099};

    public FillingVar(Wincalc winc) {
        super(winc);
    }

    public boolean filter(ElemSimple elem5e, Record glasgrpRec) {

        List<Record> paramList = eGlaspar1.filter(glasgrpRec.getInt(eGlasgrp.id));
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //���� �� ���������� ����������
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
                case 13001:  //���� ������� ������� 
                    
                    if (UPar.is_11001_11002_12001_12002_13001_14001_15001_33001_34001(rec.getStr(TEXT), elem5e) == false) {
                        return false;
                    }
                    break;
                case 13003:  //��� ������ 
                    if (!UPar.is_13003_14005_15005_37008(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 13005: //���������� ���� 
                    if ("������".equals(rec.getStr(TEXT)) && elem5e.artiklRecAn.getInt(eArtikl.level2) != 1) {
                        return false;
                    } else if ("�����������".equals(rec.getStr(TEXT)) && elem5e.artiklRecAn.getInt(eArtikl.level2) != 2) {
                        return false;
                    } else if ("�������".equals(rec.getStr(TEXT)) && elem5e.artiklRecAn.getInt(eArtikl.level2) != 3) {
                        return false;
                    } else if ("�������".equals(rec.getStr(TEXT)) && elem5e.artiklRecAn.getInt(eArtikl.level2) != 4) {
                        return false;
                    } else if ("���������� ����".equals(rec.getStr(TEXT)) && elem5e.artiklRecAn.getInt(eArtikl.level2) != 5) {
                        return false;
                    } else if ("����������� ������".equals(rec.getStr(TEXT)) && elem5e.artiklRecAn.getInt(eArtikl.level2) != 6) {
                        return false;
                    } else if ("�����������".equals(rec.getStr(TEXT)) && elem5e.artiklRecAn.getInt(eArtikl.level2) != 9) {
                        return false;
                    } else if ("������ ������".equals(rec.getStr(TEXT)) && elem5e.artiklRecAn.getInt(eArtikl.level2) != 15) {
                        return false;
                    }
                    break;
                case 13014: //���� ���������� �������, � 
                {
                    List<String> list = Arrays.asList(rec.getStr(TEXT).split(";"));
                    if (list.size() + 1 != elem5e.area.getNumPoints()) {
                        return true;
                    }
                    Collections.rotate(list, 1);
                    for (int i = 0; i < elem5e.area.getNumPoints() - 1; ++i) {

                        LineSegment l = UGeo.getSegment(elem5e.area, i);
                        double ang = Math.toDegrees(l.angle());
                        double angle = (ang > 0) ? 360 - ang : Math.abs(ang);
                        if (UCom.containsNumbJust(list.get(i), angle) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 13015:  //����� ���������� 
                    //"�������������", "�� �������������", "�� �������", "�������" (TypeElem.AREA - �������)
                    if ("�������������".equals(rec.getStr(TEXT)) && elem5e.owner.area.isRectangle() == true) {
                        return false;
                    } else if ("�� �������������".equals(rec.getStr(TEXT)) && elem5e.owner.area.isRectangle() == false) {
                        return false;
                    } else if ("�������".equals(rec.getStr(TEXT)) && elem5e.owner.area.getNumPoints() < Com5t.MAXSIDE) {
                        return false;
                    } else if ("�� �������".equals(rec.getStr(TEXT)) && ((ElemGlass) elem5e).area.getNumPoints() > Com5t.MAXSIDE) {
                        return false;
                    }                    
                    break;
                case 13017: //��� ������� �������� ������ 
                    if (UPar.is_13017_14017_24017_25017_31017_33017_34017_37017_38017(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 13081:  //��� ��������/����������� ���� ���������, � ��� ���. �����. ���� ���������, � 
                    if (UPar.is_13081_13082_13086_13087(elem5e, rec.getStr(TEXT))) {
                        return false;
                    }
                case 13095:  //���� ������� ������� ����������� (��. Systree->���.��������)
                    if (!UPar.is_11095_12095_31095_33095_34095_37095_38095_39095_40095(rec.getStr(TEXT), winc.nuni)) {
                        return false;
                    }
                    break;
                case 13097:  //������������ �� ����� 
                    message(rec.getInt(GRUP));
                    break;
                case 13098:  //�������, �������) 
                    message(rec.getInt(GRUP));
                    break;
                case 13099:  //������������, �/�. 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "��� " + grup + "  �� ���������!!!";
            }
        } catch (Exception e) {
            System.err.println("������:param.FillingVar.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
