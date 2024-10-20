package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eElemdet;
import domain.eElement;
import enums.TypeArt;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.model.Com5t;
import builder.model.ElemMosquit;
import builder.param.ElementDet;
import builder.param.ElementVar;
import builder.model.ElemSimple;
import common.UCom;
import enums.Type;
import java.util.ArrayList;

//TODO ������� ������� ��� ����������
/**
 * �������.
 */
public class SpcElement extends Cal5e {

    private ElementVar elementVar = null;
    private ElementDet elementDet = null;

    public SpcElement(Wincalc winc) {
        super(winc);
        elementVar = new ElementVar(winc);
        elementDet = new ElementDet(winc);
    }

    //���� �� ������ �������� �������, ���� ������, �������� � ���.
    @Override
    public void calc() {
        ArrayList<ElemSimple> listElem = UCom.filter(winc.listElem, Type.FRAME_SIDE, Type.STVORKA_SIDE, 
                Type.IMPOST, Type.SHTULP, Type.STOIKA, Type.GLASS, Type.MOSQUIT); //������ ��������� �����������
        try {
            //���� �� ������ ��������� �����������
            for (ElemSimple elem5e : listElem) {

                if (elem5e.type == Type.MOSQUIT) {
                    ElemMosquit elemMosq = (ElemMosquit) elem5e;
                    //�� id - �������
                    List<Record> elementList4 = List.of(eElement.find(elemMosq.sysprofRec.getInt(eElement.id)));
                    //���� �� ������ ��������� ������ ��������
                    for (int side : List.of(0, 90, 180, 270)) {
                        elemMosq.anglHoriz = side; //�����. ����. ����������� �������
                        detail(elementList4, elemMosq);
                    }
                } else {
                    //�� artikl_id - �������� ��������
                    int artiklID = elem5e.artiklRecAn.getInt(eArtikl.id);
                    List<Record> elementList3 = eElement.filter2(artiklID);
                    detail(elementList3, elem5e);

                    //�� groups1_id - ����� ��������
                    List<Record> elementList2 = eElement.filter4(elem5e.artiklRecAn); //������ ��������� � �����
                    detail(elementList2, elem5e);
                }
            }
        } catch (Exception e) {
            System.err.println("������:SpcElement.calc() " + e);
        }
    }

    protected void detail(List<Record> elementList, ElemSimple elem5e) {
        try {
            //���� �� ���������
            for (Record elementRec : elementList) {

                //������ ���������, ��������� ������������� � ������������ ��������
                if (elementVar.filter(elem5e, elementRec) == true) {

                    //�������� ������������ ��������� (�� ���������)
                    elementVar.listenerFire();

                    UColor.colorRuleFromParam(elem5e); //������� ������� ������� �� ���������
                    List<Record> elemdetList = eElemdet.find(elementRec.getInt(eElement.id)); //������ ����. �����������

                    //���� �� �����������
                    for (Record elemdetRec : elemdetList) {
                        HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //��� ������������� ��������� �����������

                        //������ �����������, ��������� ������������� � mapParam
                        if (elementDet.filter(mapParam, elem5e, elemdetRec) == true) {

                            Record artiklRec = eArtikl.get(elemdetRec.getInt(eElemdet.artikl_id));
                            SpcRecord spcAdd = new SpcRecord("���", elemdetRec, artiklRec, elem5e, mapParam);

                            //������ ��������
                            if (UColor.colorFromElemOrSeri(spcAdd)) {

                                //���� � ������ ����������� ����.���������, 
                                //�������� ������� � ��������� @ � ���. ������.
                                if (TypeArt.isType(artiklRec, TypeArt.X101, TypeArt.X102,
                                        TypeArt.X103, TypeArt.X104, TypeArt.X105)) {
                                    elem5e.spcRec.artiklRec(spcAdd.artiklRec()); //������� �������� � ���.����.
                                    elem5e.spcRec.colorID1 = spcAdd.colorID1;
                                    elem5e.spcRec.colorID2 = spcAdd.colorID2;
                                    elem5e.spcRec.colorID3 = spcAdd.colorID3;
                                    elem5e.addSpecific(elem5e.spcRec); //� ������������ 

                                    //��������� �������� �� ���������� � ����� ������
                                } else if (TypeArt.isType(artiklRec, TypeArt.X520)) {
                                    ElemMosquit alemMosq = (ElemMosquit) elem5e;
                                    if (alemMosq.anglHoriz == 0) {
                                        elem5e.spcRec.artiklRec(spcAdd.artiklRec()); //������� �������� � ���.����.
                                        elem5e.spcRec.colorID1 = spcAdd.colorID1;
                                        elem5e.spcRec.colorID2 = spcAdd.colorID2;
                                        elem5e.spcRec.colorID3 = spcAdd.colorID3;
                                        elem5e.addSpecific(elem5e.spcRec); //� ������������     
                                    }
                                } else {
                                    elem5e.addSpecific(spcAdd); //� ������������
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Elements.detail() " + e);
        }
    }
}
