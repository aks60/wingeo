package builder.making;

import builder.making.*;
import builder.ICom5t;
import dataset.Record;
import domain.eArtikl;
import domain.eElemdet;
import domain.eElement;
import enums.TypeArtikl;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import builder.Wincalc;
import builder.param.ElementDet;
import builder.param.ElementVar;
import builder.IElem5e;
import builder.model1.UPar;
import common.UCom;
import dataset.Query;
import domain.eSysprof;
import enums.Type;
import enums.UseColor;

/**
 * Составы.
 */
public class Elements extends Cal5e {

    private ElementVar elementVar = null;
    private ElementDet elementDet = null;

    public Elements(Wincalc winc) {
        super(winc);
        elementVar = new ElementVar(winc);
        elementDet = new ElementDet(winc);
    }

    //Идем по списку профилей смотрим, есть аналог, работаем с ним.
    @Override
    public void calc() {
        super.calc();
        LinkedList<IElem5e> listElem = winc.listElem.filter(Type.FRAME_SIDE,
                Type.STVORKA_SIDE, Type.IMPOST, Type.SHTULP, Type.STOIKA, Type.GLASS, Type.MOSKITKA); //список элементов конструкции
        try {
            //Цикл по списку элементов конструкции
            for (IElem5e elem5e : listElem) {

                if (elem5e.type() == Type.MOSKITKA) {
                    //По id - профиля
                    List<Record> elementList4 = List.of(eElement.find4(((ICom5t) elem5e).sysprofRec().getInt(eSysprof.id)));
                    //Цикл по списку элементов сторон маскитки
                    for (int side : List.of(0, 90, 180, 270)) {
                        elem5e.anglHoriz(side); //устан. угол. проверяемой стороны
                        detail(elementList4, elem5e);
                    }
                } else {
                    //По artikl_id - артикула профилей
                    int artiklID = elem5e.artiklRecAn().getInt(eArtikl.id);
                    List<Record> elementList3 = eElement.find2(artiklID);
                    detail(elementList3, elem5e);

                    //По groups1_id - серии профилей
                    int seriesID = elem5e.artiklRecAn().getInt(eArtikl.groups4_id);
                    List<Record> elementList2 = eElement.find(seriesID); //список элементов в серии
                    detail(elementList2, elem5e);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Elements.calc() " + e);
        } finally {
            Query.conf = conf;
        }
    }

    protected void detail(List<Record> elementList, IElem5e elem5e) {
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
                        HashMap<Integer, String> mapParam = new HashMap(); //тут накапливаются параметры детализации

                        //ФИЛЬТР детализации, параметры накапливаются в mapParam
                        if (elementDet.filter(mapParam, elem5e, elemdetRec) == true) {

                            Record artiklRec = eArtikl.get(elemdetRec.getInt(eElemdet.artikl_id));
                            Specific spcAdd = new Specific("ВСТ", elemdetRec, artiklRec, elem5e, mapParam);

                            //Подбор текстуры
                            if (UColor.colorFromProduct(spcAdd)) {

                                //Если (контейнер) в списке детализации, 
                                //например профиль с префиксом @ в осн. специф.
                                //Свойства контейнера менять нельзя!!!
                                if (TypeArtikl.isType(artiklRec, TypeArtikl.X101, TypeArtikl.X102,
                                        TypeArtikl.X103, TypeArtikl.X104, TypeArtikl.X105)) {
                                    elem5e.spcRec().setArtikl(spcAdd.artiklRec); //подмена артикула в основной спецификации
                                    elem5e.spcRec().setColor(1, spcAdd.colorID1);
                                    elem5e.spcRec().setColor(2, spcAdd.colorID2);
                                    elem5e.spcRec().setColor(3, spcAdd.colorID3);
                                    elem5e.addSpecific(elem5e.spcRec());

                                    //Контейнер маскитка не учавствует в цикле сторон
                                } else if (TypeArtikl.isType(artiklRec, TypeArtikl.X520)) {
                                    if (elem5e.anglHoriz() == 0) {
                                        elem5e.spcRec().setArtikl(spcAdd.artiklRec); //подмена артикула в основной спецификации
                                        elem5e.spcRec().setColor(1, spcAdd.colorID1);
                                        elem5e.spcRec().setColor(2, spcAdd.colorID2);
                                        elem5e.spcRec().setColor(3, spcAdd.colorID3);
                                        elem5e.addSpecific(elem5e.spcRec()); //в спецификацию     
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
