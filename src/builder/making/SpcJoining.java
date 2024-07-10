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

//Соединения
public class SpcJoining extends Cal5e {

    private JoiningVar joiningVar = null;
    private JoiningDet joiningDet = null;
    private ElementDet elementDet = null;
    private HashMap<ElemJoining, Integer> mapJoinvar = new HashMap<ElemJoining, Integer>();
    private boolean ps3 = "ps3".equals(eSetting.val(2));

    public SpcJoining(Wincalc winc) {
        super(winc);
        joiningVar = new JoiningVar(winc);
        joiningDet = new JoiningDet(winc);
        elementDet = new ElementDet(winc);
    }

    public SpcJoining(Wincalc winc, boolean shortPass) {
        super(winc);
        joiningVar = new JoiningVar(winc);
        joiningDet = new JoiningDet(winc);
        elementDet = new ElementDet(winc);
        this.shortPass = shortPass;
    }

    @Override
    public void calc() {
        super.calc();
        try {

            //Цикл по списку соединений
            for (ElemJoining elemJoin : winc.listJoin) {

                ElemSimple joinElem1 = elemJoin.elem1;
                ElemSimple joinElem2 = elemJoin.elem2;

                int id1 = joinElem1.artiklRecAn.getInt(eArtikl.id);
                int id2 = joinElem2.artiklRecAn.getInt(eArtikl.id);
                Record joiningRec1 = eJoining.find(id1, id2);
                Record joiningRec2 = null;

                //Список вариантов соединения для артикула1 и артикула2
                List<Record> joinvarList = eJoinvar.find(joiningRec1.getInt(eJoining.id));

                //Если неудача, ищем в аналоге соединения
                if (joinvarList.isEmpty() == true && joiningRec1.getStr(eJoining.analog).isEmpty() == false) {
                    joiningRec2 = eJoining.find2(joiningRec1.getStr(eJoining.analog));
                    joinvarList = eJoinvar.find(joiningRec2.getInt(eJoining.id));
                }
                //Если неудача то ищем зеркальность (только для дверей)
                if (winc.root.type == Type.DOOR && joinvarList.isEmpty()) {
                    joiningRec1 = eJoining.find(id2, id1);
                    joinvarList = eJoinvar.find(joiningRec1.getInt(eJoining.id));
                }
                Collections.sort(joinvarList, (connvar1, connvar2) -> connvar1.getInt(eJoinvar.prio) - connvar2.getInt(eJoinvar.prio));

                //Цикл по вариантам соединения
                for (Record joinvarRec : joinvarList) {
                    boolean go = false;
                    int typeID = joinvarRec.getInt(eJoinvar.types);

                    if (elemJoin.type().id == typeID) { //если варианты соединения совпали
                        go = true;
                    } else if (joinvarRec.getInt(eJoinvar.mirr) == 1) { //когда включена зеркальность
                        if (winc.root.type == Type.DOOR && (typeID == 30 || typeID == 31)
                                && (elemJoin.type().id == 30 || elemJoin.type().id == 31)) {
                            go = true;
                        }
                    }
                    if (go == true) {
                        //ФИЛЬТР вариантов  
                        if (joiningVar.filter(elemJoin, joinvarRec) == true) {

                            //Если в проверочных парам. успех,
                            //выполним установочные параметры
                            joiningVar.listenerFire();

                            //Накопление данных для запуска детализации
                            mapJoinvar.put(elemJoin, joinvarRec.getInt(eJoinvar.id));

                            //Сохраним подхоящий вариант соединения из таблиц bd                           
                            elemJoin.type(TypeJoin.get(joinvarRec.getInt(eJoinvar.types)));
                            elemJoin.joiningRec = joiningRec1;
                            elemJoin.joinvarRec = joinvarRec;

                            break; //если текущий вариант совпал
                        }
                    }
                }
            }

            //Детализация
            if (shortPass == false) {
                detal();
            }

        } catch (Exception e) {
            System.err.println("Ошибка:Joining.calc() " + e);
        } finally {
            Query.conf = conf;
        }
    }

    public void detal() {

        for (Map.Entry<ElemJoining, Integer> entry : mapJoinvar.entrySet()) {

            ElemJoining elemJoin = entry.getKey();
            Integer key = entry.getValue();
            List<Record> joindetList = eJoindet.find(key);

            //Цикл по детализации соединений
            for (Record joindetRec : joindetList) {
                HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //тут накапливаются параметры

                //ФИЛЬТР детализации 
                if (joiningDet.filter(mapParam, elemJoin, joindetRec) == true) {
                    Record artiklRec = eArtikl.find(joindetRec.getInt(eJoindet.artikl_id), false);
                    SpcRecord spcAdd = new SpcRecord("СОЕД", joindetRec, artiklRec, elemJoin.elem1, mapParam);

                    //Подбор текстуры
                    if (UColor.colorFromProduct(spcAdd)) {
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
        //Список вариантов соединения для артикула1 и артикула2
        List<Record> joinvarList = eJoinvar.find(joiningRec.getInt(eJoining.id));
        //Если неудача, ищем в аналоге соединения
        if (joinvarList.isEmpty() == true && joiningRec.getStr(eJoining.analog).isEmpty() == false) {
            joiningRec = eJoining.find2(joiningRec.getStr(eJoining.analog));
            joinvarList = eJoinvar.find(joiningRec.getInt(eJoining.id));
        }
        Collections.sort(joinvarList, (connvar1, connvar2) -> connvar1.getInt(eJoinvar.prio) - connvar2.getInt(eJoinvar.prio));

        //Цикл по вариантам соединения
        for (Record joinvarRec : joinvarList) {
            //Если варианты соединения совпали
            if (elemJoin.type().id == joinvarRec.getInt(eJoinvar.types)) {
                //ФИЛЬТР вариантов  
                if (joiningVar.filter(elemJoin, joinvarRec) == true) {
                    joinvarRec.setNo(eJoinvar.name, joinvarRec.getStr(eJoinvar.name) + " (" + joiningRec.getStr(eJoining.name) + ")");
                    list.add(joinvarRec);
                }
            }
        }
        return list;
    }
}
