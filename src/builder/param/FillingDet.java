package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eGlasdet;
import domain.eGlaspar2;
import domain.eSystree;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.model.ElemSimple;
import builder.model.UGeo;
import common.UCom;
import enums.Layout;
import enums.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;

//����������
public class FillingDet extends Par5s {

    public FillingDet(Wincalc winc) {
        super(winc);
    }

    public boolean filter(HashMap<Integer, String> mapParam, ElemSimple elem5e, Record glasdetRec) {

        List<Record> paramList = eGlaspar2.filter(glasdetRec.getInt(eGlasdet.id)); //������ ���������� �����������  
        if (filterParamDef(paramList) == false) {
            return false; //��������� �� ���������
        }
        //���� �� ���������� ����������
        for (Record rec : paramList) {
            if (check(mapParam, elem5e, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(HashMap<Integer, String> mapParam, ElemSimple elem5e, Record rec) {

        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {
                case 14000: //��� ���������������� ���� ����������
                case 15000: //��� ���������������� ���� ���������� 
                {
                    ElemSimple elem = UCom.layout(winc.root.frames, Layout.BOTT);
                    if (!UPar.is_STRING_XX000(rec.getStr(TEXT), elem)) {
                        return false;
                    }
                }
                break;
                case 14001: //���� ������� ������� 
                case 15001: //���� ������� �������    
                {
                    ElemSimple e = UCom.layout(winc.root.frames, Layout.BOTT);
                    if (UPar.is_11001_11002_12001_12002_13001_14001_15001_33001_34001(rec.getStr(TEXT), e) == false) {
                        return false;
                    }
                }
                break;
                case 14005: //��� ������ 
                case 15005: //��� ������
                    if (!UPar.is_13003_14005_15005_37008(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 14008: //����������� ���������� ���., �� 
                case 15008: //����������� ���������� ���., ��                    
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 14009: //������� ���������� 
                case 15009: //������� ����������  
                    if ("��".equals(rec.getStr(TEXT)) && elem5e.owner.type != Type.ARCH) {
                        return false;
                    } else if ("���".equals(rec.getStr(TEXT)) && elem5e.owner.type == Type.ARCH) {
                        return false;
                    }
                    break;
                case 14017: //��� ������� �������� ������ 
                case 15017: //��� ������� �������� ������                    
                    if (UPar.is_13017_14017_24017_25017_31017_33017_34017_37017_38017(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 14030:  //���������� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 14040:  //����� �������, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 14050:  //���, �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 14060:  //���������� �� ��� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 14065:  //����������� ����, � ��� ������ ���� 
                case 15055: //����������� ����, � ��� ������ ����  
                {
                    Geometry g = elem5e.area.getGeometryN(0);
                    for (int i = 0; i < g.getNumPoints(); i++) {
                        LineSegment seg = UGeo.getSegment(g, i);
                        double ang = seg.angle();
                        double angHor = (ang > 0) ? 360 - ang : Math.abs(ang);
                        if (UCom.containsNumbJust(rec.getStr(TEXT), angHor) == true) {
                            return true;
                        }
                    }
                    return false;
                }
                case 14067:  //���� �������� �������� ������� 
                case 15067:  //���� �������� �������� �������    
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID1) == false) {
                        return false;
                    }
                    break;
                case 14068:  //���� �����. �������� ������� 
                case 15068:  //���� �����. �������� �������   
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID2) == false) {
                        return false;
                    }
                    break;
                case 14069:  //���� �����. �������� ������� 
                case 15069:  //���� �����. �������� �������     
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID3) == false) {
                        return false;
                    }
                    break;
                case 14081: //���� ������� ������� ������� 
                case 15081: //���� ������� ������� ������� 
                {
                    ElemSimple elem = (elem5e.owner.frames.isEmpty() == false) ? UCom.layout(elem5e.owner.frames, Layout.BOTT) : UCom.layout(elem5e.root.frames, Layout.BOTT);
                    if (rec.getStr(TEXT).equals(elem.artiklRecAn.getStr(eArtikl.code)) == false) {
                        return false;
                    }
                }
                break;
                case 14095: //���� ������� ������� ����������� 
                case 15095: //���� ������� ������� �����������  
                {
                    Record systreeRec = eSystree.find(winc.nuni);
                    String[] arr = rec.getStr(TEXT).split(";");
                    List<String> arrList = List.of(arr);
                    boolean ret = false;
                    for (String str : arrList) {
                        if (systreeRec.get(eSystree.types) == Integer.valueOf(str) == true) {
                            ret = true;
                        }
                    }
                    if (ret == false) {
                        return false;
                    }
                }
                break;
                case 15010:  //������� ������ ������ 
                case 15011:  //������ ���� ������� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 15013:  //������ ������������� �������� ������� 
                    message(rec.getInt(GRUP));
                    break;
                case 15027:  //������������ ��� ������� 
                    if ("� ������������".equals(rec.getStr(TEXT)) == true && elem5e.artiklRec.getInt(eArtikl.with_seal) == 0) {
                        return false;
                    }
                    break;
                case 15030:  //[ * ����-� ] 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 15040:  //���������� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 15045:  //�����, �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 15050:  //��������, �� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 15051:  //��������� �� ���� ���.�., �� 
                    if (elem5e.spcRec.getParam("0", 31052).equals(rec.getStr(TEXT)) == false) {
                        mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    }
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "��� " + grup + "  �� ���������!!!";
            }
        } catch (Exception e) {
            System.err.println("������:param.FillingDet.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
