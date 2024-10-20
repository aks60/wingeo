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

//TODO Сделать составы для заполнений
/**
 * Составы.
 */
public class SpcElement extends Cal5e {

    private ElementVar elementVar = null;
    private ElementDet elementDet = null;

    public SpcElement(Wincalc winc) {
        super(winc);
        elementVar = new ElementVar(winc);
        elementDet = new ElementDet(winc);
    }

    //Идем по списку профилей смотрим, есть аналог, работаем с ним.
    @Override
    public void calc() {
        ArrayList<ElemSimple> listElem = UCom.filter(winc.listElem, Type.FRAME_SIDE, Type.STVORKA_SIDE, 
                Type.IMPOST, Type.SHTULP, Type.STOIKA, Type.GLASS, Type.MOSQUIT); //список элементов конструкции
        try {
            //Цикл по списку элементов конструкции
            for (ElemSimple elem5e : listElem) {

                if (elem5e.type == Type.MOSQUIT) {
                    ElemMosquit elemMosq = (ElemMosquit) elem5e;
                    //По id - профиля
                    List<Record> elementList4 = List.of(eElement.find(elemMosq.sysprofRec.getInt(eElement.id)));
                    //Цикл по списку элементов сторон маскитки
                    for (int side : List.of(0, 90, 180, 270)) {
                        elemMosq.anglHoriz = side; //устан. угол. проверяемой стороны
                        detail(elementList4, elemMosq);
                    }
                } else {
                    //По artikl_id - артикула профилей
                    int artiklID = elem5e.artiklRecAn.getInt(eArtikl.id);
                    List<Record> elementList3 = eElement.filter2(artiklID);
                    detail(elementList3, elem5e);

                    //По groups1_id - серии профилей
                    List<Record> elementList2 = eElement.filter4(elem5e.artiklRecAn); //список элементов в серии
                    detail(elementList2, elem5e);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:SpcElement.calc() " + e);
        }
    }

    protected void detail(List<Record> elementList, ElemSimple elem5e) {
        try {
            //Цикл по вариантам
            for (Record elementRec : elementList) {

                //ФИЛЬТР вариантов, параметры накапливаются в спецификации элемента
                if (elementVar.filter(elem5e, elementRec) == true) {

                    //Выполним установочные параметры (не успользую)
                    elementVar.listenerFire();

                    UColor.colorRuleFromParam(elem5e); //правило подбора текстур по параметру
                    List<Record> elemdetList = eElemdet.find(elementRec.getInt(eElement.id)); //список элем. детализации

                    //Цикл по детализации
                    for (Record elemdetRec : elemdetList) {
                        HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //тут накапливаются параметры детализации

                        //ФИЛЬТР детализации, параметры накапливаются в mapParam
                        if (elementDet.filter(mapParam, elem5e, elemdetRec) == true) {

                            Record artiklRec = eArtikl.get(elemdetRec.getInt(eElemdet.artikl_id));
                            SpcRecord spcAdd = new SpcRecord("ВСТ", elemdetRec, artiklRec, elem5e, mapParam);

                            //Подбор текстуры
                            if (UColor.colorFromElemOrSeri(spcAdd)) {

                                //Если в списке детализации элем.контейнер, 
                                //например профиль с префиксом @ в осн. специф.
                                if (TypeArt.isType(artiklRec, TypeArt.X101, TypeArt.X102,
                                        TypeArt.X103, TypeArt.X104, TypeArt.X105)) {
                                    elem5e.spcRec.artiklRec(spcAdd.artiklRec()); //подмена артикула в осн.спец.
                                    elem5e.spcRec.colorID1 = spcAdd.colorID1;
                                    elem5e.spcRec.colorID2 = spcAdd.colorID2;
                                    elem5e.spcRec.colorID3 = spcAdd.colorID3;
                                    elem5e.addSpecific(elem5e.spcRec); //в спецификацию 

                                    //Контейнер маскитка не учавствует в цикле сторон
                                } else if (TypeArt.isType(artiklRec, TypeArt.X520)) {
                                    ElemMosquit alemMosq = (ElemMosquit) elem5e;
                                    if (alemMosq.anglHoriz == 0) {
                                        elem5e.spcRec.artiklRec(spcAdd.artiklRec()); //подмена артикула в осн.спец.
                                        elem5e.spcRec.colorID1 = spcAdd.colorID1;
                                        elem5e.spcRec.colorID2 = spcAdd.colorID2;
                                        elem5e.spcRec.colorID3 = spcAdd.colorID3;
                                        elem5e.addSpecific(elem5e.spcRec); //в спецификацию     
                                    }
                                } else {
                                    elem5e.addSpecific(spcAdd); //в спецификацию
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Elements.detail() " + e);
        }
    }
}
