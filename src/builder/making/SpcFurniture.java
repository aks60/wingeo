package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eFurndet;
import domain.eFurniture;
import domain.eFurnside1;
import domain.eFurnside2;
import domain.eSysfurn;
import enums.Layout;
import java.util.List;
import builder.Wincalc;
import builder.param.FurnitureDet;
import builder.param.FurnitureVar;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.ElemSimple;
import common.UCom;
import dataset.Query;
import enums.PKjson;
import enums.Type;
import java.util.ArrayList;
import java.util.HashMap;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import org.locationtech.jts.geom.Envelope;

/**
 * ���������
 */
public class SpcFurniture extends Cal5e {

    private FurnitureVar furnitureVar = null;
    private FurnitureDet furnitureDet = null;
    private final List list = List.of(9, 11, 12); //�����, �����, ����� 
    private boolean max_size_message = true;

    public SpcFurniture(Wincalc winc) {
        super(winc);
        furnitureVar = new FurnitureVar(winc);
        furnitureDet = new FurnitureDet(winc);
    }

    public SpcFurniture(Wincalc winc, boolean shortPass) {
        super(winc);
        furnitureVar = new FurnitureVar(winc);
        furnitureDet = new FurnitureDet(winc);
        this.shortPass = shortPass;
    }

    @Override
    public void calc() {
        ArrayList<AreaSimple> stvorkaList = UCom.filter(winc.listArea, Type.STVORKA);
        try {
            //������ ��������� �� ����������
            List<Record> sysfurnList = eSysfurn.filter(winc.nuni); //������ �������� � �������
            if (sysfurnList.isEmpty() == false) {
                Record sysfurnRec = sysfurnList.get(0); //�������� �� ���������, ������ SYSFURN � ������ �������

                //���� �� ��������      
                for (AreaSimple areaStv : stvorkaList) {
                    AreaStvorka stv = (AreaStvorka) areaStv;

                    //����� �� ������ ����.����. ��������� ������� ����������� � �������                 
                    sysfurnRec = sysfurnList.stream().filter(rec -> rec.getInt(eSysfurn.id) == stv.sysfurnRec.getInt(eSysfurn.id)).findFirst().orElse(sysfurnRec);
                    Record furnityreRec = eFurniture.find(sysfurnRec.getInt(eSysfurn.furniture_id));

                    //�������� � ��������������� �� max ������, ������, ��������
                    Envelope env = stv.area.getEnvelopeInternal();
                    double stv_width = env.getWidth();
                    double stv_height = env.getHeight();
                    boolean p2_max = (furnityreRec.getDbl(eFurniture.max_p2) < (stv_width * 2 + stv_height * 2) / 2);
                    if (p2_max || furnityreRec.getDbl(eFurniture.max_height) < stv_height
                            || furnityreRec.getDbl(eFurniture.max_width) < stv_width) {
                        if (max_size_message == true) {
                            JOptionPane.showMessageDialog(null, "������ ������� ��������� ������������ ������ ���������.", "��������!", 1);
                        }
                        max_size_message = false;
                    }
                    variant(stv, furnityreRec, 1); //�������� ���������
                }
            }
        } catch (Exception e) {
            System.err.println("������:Furniture.calc() " + e);
        } 
    }

    protected void variant(AreaSimple areaStv, Record furnitureRec, int count) {
        try {
            List<Record> furndetList1 = eFurndet.filter(furnitureRec.getInt(eFurniture.id)); //����������� ������ �������
            List<Record> furndetList2 = furndetList1.stream().filter(rec -> 
                    rec.getInt(eFurndet.id) != rec.getInt(eFurndet.furndet_pk)).collect(toList()); //����������� ������ �������

            //���� �� �������� ������ ���������
            List<Record> furnsidetList = eFurnside1.filter(furnitureRec.getInt(eFurniture.id)); //������ �������� ������
            for (Record furnside1Rec : furnsidetList) {
                Layout layout = (Layout) Layout.ANY.find(furnside1Rec.getInt(eFurnside1.side_num));
                ElemSimple elemFrame = areaStv.frames.stream().filter(e -> e.layout() == layout).findFirst().get();

                //������ ��������� � ������ �������
                if (furnitureVar.filter(elemFrame, furnside1Rec) == false) {
                    return;
                }
            }

            //���� �� ����������� (������ �������)        
            for (Record furndetRec1 : furndetList1) {
                if (furndetRec1.getInt(eFurndet.furndet_pk) == furndetRec1.getInt(eFurndet.id)) {
                    if (detail(areaStv, furndetRec1, count) == true) {

                        //���� �� ����������� (������ �������)
                        for (Record furndetRec2 : furndetList2) {
                            if (furndetRec2.getInt(eFurndet.furndet_pk) == furndetRec1.getInt(eFurndet.pk)) {
                                if (detail(areaStv, furndetRec2, count) == true) {

                                    //���� �� ����������� (������ �������)
                                    for (Record furndetRec3 : furndetList2) {
                                        if (furndetRec3.getInt(eFurndet.furndet_pk) == furndetRec2.getInt(eFurndet.pk)) {
                                            detail(areaStv, furndetRec3, count);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Furniture.variant() " + e);
        }
    }

    protected boolean detail(AreaSimple areaStv, Record furndetRec, int countKit) {
        try {
            Record artiklRec = eArtikl.find(furndetRec.getInt(eFurndet.artikl_id), false);
            HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //��� ������������� ��������� element � specific

            //������� ��� ���������� ������ �����, 
            //�������, ����� ��� ��������������� ����
            if (shortPass == true) {
                if (furndetRec.getInt(eFurndet.furndet_pk) == furndetRec.getInt(eFurndet.id) && furndetRec.get(eFurndet.furniture_id2) == null) {
                    if ((artiklRec.getInt(eArtikl.level1) == 2 && list.contains(artiklRec.getInt(eArtikl.level2)) == false)
                            || artiklRec.getInt(eArtikl.level1) != 2) { //�.�. �����, �������, ����� �� ���� ������ ���
                        return false;
                    }
                }
            }
            furnitureDet.detailRec = furndetRec; //������� ������� �����������

            //������ ���������� ����������� 
            if (furnitureDet.filter(mapParam, areaStv, furndetRec) == false) {
                return false;
            }

            //�������� �� ����������� ������
            //���� �� ����������� ������ ���������
            List<Record> furnside2List = eFurnside2.filter(furndetRec.getInt(eFurndet.id));
            for (Record furnside2Rec : furnside2List) {
                ElemSimple el;
                double length = 0;
                int side = furnside2Rec.getInt(eFurnside2.side_num);

                if (side < 0) {
                    String txt = (furnitureDet.mapParamTmp.getOrDefault(24038, null) == null)
                            ? furnitureDet.mapParamTmp.getOrDefault(25038, "*/*")
                            : furnitureDet.mapParamTmp.getOrDefault(24038, "*/*");
                    String[] par = txt.split("/");
                    if (side == -1) {
                        side = (par[0].equals("*") == true) ? 99 : Integer.valueOf(par[0]);
                    } else if (side == -2) {
                        side = (par[1].equals("*") == true) ? 99 : Integer.valueOf(par[1]);
                    }
                }
                if (side == 1) {
                    el = UCom.layout(areaStv.frames, Layout.BOTT);
                    length = el.length() - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                } else if (side == 2) {
                    el = UCom.layout(areaStv.frames, Layout.RIGHT);
                    length = el.length() - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                } else if (side == 3) {
                    el = UCom.layout(areaStv.frames, Layout.TOP);
                    length = el.length() - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                } else if (side == 4) {
                    el = UCom.layout(areaStv.frames, Layout.LEFT);
                    length = el.length() - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                }
                if (length >= furnside2Rec.getDbl(eFurnside2.len_max) || (length < furnside2Rec.getDbl(eFurnside2.len_min))) {

                    return false; //�� ������ ����������� ������
                }
            }

            //�� ����� (������� �� ���. ��������)
            if (furndetRec.get(eFurndet.furniture_id2) == null) {
                if (artiklRec.getInt(eArtikl.id) != -1) { //������� ����

                    ElemSimple sideStv = determOfSide(mapParam, areaStv);
                    SpcRecord spcAdd = new SpcRecord("����", furndetRec, artiklRec, sideStv, mapParam);

                    //����� �����, �����, ����� � 
                    //����������� ����. � �������� �������
                    if (spcAdd.artiklRec().getInt(eArtikl.level1) == 2
                            && list.contains(spcAdd.artiklRec().getInt(eArtikl.level2)) == true) {
                        setPropertyStv(areaStv, spcAdd);
                    } else {
                        UColor.colorFromElemOrSeri(spcAdd);
                    }
                    //������� ������������ � �������
                    if (shortPass == false) {
                        spcAdd.count = UCom.getDbl(spcAdd.getParam(spcAdd.count, 24030));
                        spcAdd.count = spcAdd.count * countKit; //������� �� ���������� ����������
                        sideStv.addSpecific(spcAdd);
                    }
                }

                //��� ����� 
            } else {
                int countKi2 = (mapParam.get(24030) == null) ? 1 : Integer.valueOf((mapParam.get(24030)));
                Record furnitureRec2 = eFurniture.find(furndetRec.getInt(eFurndet.furniture_id2));

                variant(areaStv, furnitureRec2, countKi2); //�������� ��������� �������
            }
            return true;

        } catch (Exception e) {
            System.err.println("������:Furniture.detail() " + e);
            return false;
        }
    }

    //����� �����, ������, ����� � 
    //����������� ����. � �������    
    private void setPropertyStv(AreaSimple areaStv, SpcRecord spcAdd) {
        AreaStvorka stv = (AreaStvorka) areaStv;

        if (spcAdd.artiklRec().getInt(eArtikl.level1) == 2) {
            //�����
            if (spcAdd.artiklRec().getInt(eArtikl.level2) == 11) {
                if (stv.isJson(stv.gson.param, PKjson.artiklKnob)) {
                    spcAdd.artiklRec(stv.knobRec); //����. �������
                } else {
                    stv.knobRec = spcAdd.artiklRec(); //�� ����������� ����
                }
                //����
                spcAdd.color(stv.knobColor, -3, -3);  //����. ������ � �������� ��������� ��� ����. �������
                if (stv.isJson(stv.gson.param, PKjson.colorKnob) == false) {
                    if (UColor.colorFromElemOrSeri(spcAdd) == true) { //������ �� �����
                        stv.knobColor = spcAdd.colorID1;
                    }
                }
                //������
            } else if (spcAdd.artiklRec().getInt(eArtikl.level2) == 12) {
                if (stv.isJson(stv.gson.param, PKjson.artiklLoop)) {
                    spcAdd.artiklRec(stv.loopRec); //����. �������
                } else {
                    stv.loopRec = spcAdd.artiklRec(); //�� ����������� ����
                }
                //����
                spcAdd.color(stv.loopColor, -3, -3);  //����. ������ � �������� ��������� ��� ����. �������
                if (stv.isJson(stv.gson.param, PKjson.colorLoop) == false) {
                    if (UColor.colorFromElemOrSeri(spcAdd) == true) { //������ �� �����
                        stv.loopColor = spcAdd.colorID1;
                    }
                }
                //�����  
            } else if (spcAdd.artiklRec().getInt(eArtikl.level2) == 9) {
                if (stv.isJson(stv.gson.param, PKjson.artiklLock)) {
                    spcAdd.artiklRec(stv.lockRec); //����. �������
                } else {
                    //stv.lockRec = spcAdd.artiklRec; //�� ����������� ����
                }
                //����
                spcAdd.color(stv.lockColor, -3, -3);  //����. ������ � �������� ��������� ��� ����. �������
                if (stv.isJson(stv.gson.param, PKjson.colorLock) == false) {
                    if (UColor.colorFromElemOrSeri(spcAdd) == true) { //������ �� �����
                        stv.lockColor = spcAdd.colorID1;
                    }
                }
            }
        }
    }

    public ElemSimple determOfSide(HashMap<Integer, String> mapParam, AreaSimple area5e) {

        //����� ��������
        if ("1".equals(mapParam.get(25010))) {
            return UCom.layout(area5e.frames, Layout.BOTT);
        } else if ("2".equals(mapParam.get(25010))) {
            return UCom.layout(area5e.frames, Layout.RIGHT);
        } else if ("3".equals(mapParam.get(25010))) {
            return UCom.layout(area5e.frames, Layout.TOP);
        } else if ("4".equals(mapParam.get(25010))) {
            return UCom.layout(area5e.frames, Layout.LEFT);
        } else {
            //��� ��� �������� �����
            return determOfSide(area5e);
        }
    }

    //��� ��� �������� �����
    public static ElemSimple determOfSide(AreaSimple area5e) {
        if (area5e instanceof AreaStvorka) {
            int id = ((AreaStvorka) area5e).typeOpen.id;
            if (List.of(1, 3, 11).contains(id)) {
                return UCom.layout(area5e.frames, Layout.LEFT);
            } else if (List.of(2, 4, 12).contains(id)) {
                return UCom.layout(area5e.frames, Layout.RIGHT);
            } else {
                return UCom.layout(area5e.frames, Layout.BOTT);
            }
        }
        return area5e.frames.stream().findFirst().get();  //������ ����������        
    }
}
