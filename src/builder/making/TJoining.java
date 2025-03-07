package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eJoindet;
import domain.eJoining;
import domain.eJoinvar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import builder.Wincalc;
import builder.model.ElemFrame;
import builder.model.ElemJoining;
import builder.param.ElementDet;
import builder.param.JoiningDet;
import builder.param.JoiningVar;
import builder.model.ElemSimple;
import dataset.Query;
import domain.eSetting;
import enums.TypeJoin;
import enums.Type;
import java.util.ArrayList;
import org.locationtech.jts.geom.Coordinate;

//����������
public class TJoining extends Cal5e {

    private JoiningVar joiningVar = null;
    private JoiningDet joiningDet = null;
    private ElementDet elementDet = null;
    private HashMap<ElemJoining, Integer> mapJoinvar = new HashMap<ElemJoining, Integer>();
    private boolean ps3 = "ps3".equals(eSetting.find(2));

    public TJoining(Wincalc winc) {
        super(winc);
        joiningVar = new JoiningVar(winc);
        joiningDet = new JoiningDet(winc);
        elementDet = new ElementDet(winc);
    }

    public TJoining(Wincalc winc, boolean shortPass) {
        super(winc);
        joiningVar = new JoiningVar(winc);
        joiningDet = new JoiningDet(winc);
        elementDet = new ElementDet(winc);
        this.shortPass = shortPass;
    }

    public void join() {
        try {

            //���� �� ������ ����������
            for (ElemJoining elemJoin : winc.listJoin) {

                ElemSimple joinElem1 = elemJoin.elem1;
                ElemSimple joinElem2 = elemJoin.elem2;

                int id1 = joinElem1.artiklRecAn.getInt(eArtikl.id);
                int id2 = joinElem2.artiklRecAn.getInt(eArtikl.id);
                Record joiningRec1 = eJoining.find(id1, id2);
                Record joiningRec2 = null;

                //������ ��������� ���������� ��� ��������1 � ��������2
                List<Record> joinvarList = eJoinvar.filter(joiningRec1.getInt(eJoining.id));

                //���� �������, ���� � ������� ����������
                if (joinvarList.isEmpty() == true && joiningRec1.getStr(eJoining.analog).isEmpty() == false) {
                    joiningRec2 = eJoining.find2(joiningRec1.getStr(eJoining.analog));
                    joinvarList = eJoinvar.filter(joiningRec2.getInt(eJoining.id));
                }
                //���� ������� �� ���� ������������ (������ ��� ������)
                if (winc.root.type == Type.DOOR && joinvarList.isEmpty()) {
                    joiningRec1 = eJoining.find(id2, id1);
                    joinvarList = eJoinvar.filter(joiningRec1.getInt(eJoining.id));
                }
                Collections.sort(joinvarList, (connvar1, connvar2) -> connvar1.getInt(eJoinvar.prio) - connvar2.getInt(eJoinvar.prio));

                //���� �� ��������� ����������
                for (Record joinvarRec : joinvarList) {
                    boolean go = false;
                    int typeID = joinvarRec.getInt(eJoinvar.types);

                    if (elemJoin.type().id == typeID) { //���� �������� ���������� �������
                        go = true;
                    } else if (joinvarRec.getInt(eJoinvar.mirr) == 1) { //����� �������� ������������
                        if (winc.root.type == Type.DOOR && (typeID == 30 || typeID == 31)
                                && (elemJoin.type().id == 30 || elemJoin.type().id == 31)) {
                            go = true;
                        }
                    }
                    if (go == true) {
                        //������ ���������  
                        if (joiningVar.filter(elemJoin, joinvarRec) == true) {

                            //���� � ����������� �����. �����,
                            //�������� ������������ ���������
                            joiningVar.listenerFire();

                            //���������� ������ ��� ������� �����������
                            mapJoinvar.put(elemJoin, joinvarRec.getInt(eJoinvar.id));

                            //�������� ��������� ������� ���������� �� ������ bd                           
                            elemJoin.type(TypeJoin.get(joinvarRec.getInt(eJoinvar.types)));
                            elemJoin.joiningRec = joiningRec1;
                            elemJoin.joinvarRec = joinvarRec;

                            break; //���� ������� ������� ������
                        }
                    }
                }
            }

            //�����������
            if (shortPass == false) {
                detal();
            }

        } catch (Exception e) {
            System.err.println("������:Joining.join() " + e);
        } 
    }

    public void detal() {

        for (Map.Entry<ElemJoining, Integer> entry : mapJoinvar.entrySet()) {

            ElemJoining elemJoin = entry.getKey();
            Integer key = entry.getValue();
            List<Record> joindetList = eJoindet.filter(key);

            //���� �� ����������� ����������
            for (Record joindetRec : joindetList) {
                HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //��� ������������� ���������

                //������ ����������� 
                if (joiningDet.filter(mapParam, elemJoin, joindetRec) == true) {
                    Record artiklRec = eArtikl.find(joindetRec.getInt(eJoindet.artikl_id), false);
                    TRecord spcAdd = new TRecord("����", joindetRec, artiklRec, elemJoin.elem1, mapParam);

                    //������ ��������
                    if (UColor.colorFromElemOrSeri(spcAdd)) {
                        elemJoin.addSpecific(spcAdd);
                    }
                }
            }
        }
    }

    public List<Record> varList(ElemJoining elemJoin) {
        List<Record> list = new ArrayList<Record>();

        //Record joiningRec = eJoining.find(elemJoin.elem1.artiklRecAn, elemJoin.elem2.artiklRecAn);
        int id1 = elemJoin.elem1.artiklRecAn.getInt(eArtikl.id);
        int id2 = elemJoin.elem2.artiklRecAn.getInt(eArtikl.id);
        Record joiningRec = eJoining.find(id1, id2);
        //������ ��������� ���������� ��� ��������1 � ��������2
        List<Record> joinvarList = eJoinvar.filter(joiningRec.getInt(eJoining.id));
        //���� �������, ���� � ������� ����������
        if (joinvarList.isEmpty() == true && joiningRec.getStr(eJoining.analog).isEmpty() == false) {
            joiningRec = eJoining.find2(joiningRec.getStr(eJoining.analog));
            joinvarList = eJoinvar.filter(joiningRec.getInt(eJoining.id));
        }
        Collections.sort(joinvarList, (connvar1, connvar2) -> connvar1.getInt(eJoinvar.prio) - connvar2.getInt(eJoinvar.prio));

        //���� �� ��������� ����������
        for (Record joinvarRec : joinvarList) {
            //���� �������� ���������� �������
            if (elemJoin.type().id == joinvarRec.getInt(eJoinvar.types)) {
                //������ ���������  
                if (joiningVar.filter(elemJoin, joinvarRec) == true) {
                    joinvarRec.setNo(eJoinvar.name, joinvarRec.getStr(eJoinvar.name) + " (" + joiningRec.getStr(eJoining.name) + ")");
                    list.add(joinvarRec);
                }
            }
        }
        return list;
    }
}
