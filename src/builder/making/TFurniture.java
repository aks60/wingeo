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
import builder.model.UPar;
import common.UCom;
import enums.PKjson;
import enums.Type;
import java.util.ArrayList;
import java.util.HashMap;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import org.locationtech.jts.geom.Envelope;
import startup.App;

/**
 * Фурнитура
 */
public class TFurniture extends Cal5e {

    private FurnitureVar furnitureVar = null;
    private FurnitureDet furnitureDet = null;
    private final List LEVEL = List.of(9, 11, 12); //замок, ручка, петля 
    private boolean max_size_message = true;

    public TFurniture(Wincalc winc) {
        super(winc);
        furnitureVar = new FurnitureVar(winc);
        furnitureDet = new FurnitureDet(winc);
    }

    public TFurniture(Wincalc winc, boolean shortPass) {
        super(winc);
        furnitureVar = new FurnitureVar(winc);
        furnitureDet = new FurnitureDet(winc);
        this.shortPass = shortPass;
    }

    public void furn() {
        ArrayList<AreaSimple> stvorkaList = UCom.filter(winc.listArea, Type.STVORKA);
        try {
            //Подбор фурнитуры по параметрам
            List<Record> sysfurnList = eSysfurn.filter(winc.nuni); //список фурнитур в системе
            if (sysfurnList.isEmpty() == false) {

                //Цикл по створкам      
                for (AreaSimple areaSimple : stvorkaList) {
                    AreaStvorka areaStv = (AreaStvorka) areaSimple;

                    if (areaStv.sysfurnRec == null) {  //то первая по умолч.
                        areaStv.sysfurnRec = sysfurnList.get(0); //значение по умолчанию, первая SYSFURN в списке системы
                    }
                    Record furnitureRec = eFurniture.find(areaStv.sysfurnRec.getInt(eSysfurn.furniture_id));

                    //Проверка с предупреждением на max высоту, ширину, периметр
                    Envelope env = areaStv.area.getEnvelopeInternal();
                    double stv_width = env.getWidth();
                    double stv_height = env.getHeight();
                    boolean p2_max = (furnitureRec.getDbl(eFurniture.max_p2) < (stv_width * 2 + stv_height * 2) / 2);
                    if (p2_max || furnitureRec.getDbl(eFurniture.max_height) < stv_height
                            || furnitureRec.getDbl(eFurniture.max_width) < stv_width) {
                        if (max_size_message == true) {
                            JOptionPane.showMessageDialog(App.active, "Размер створки превышает максимальный размер фурнитуры.", "ВНИМАНИЕ!", 1);
                            max_size_message = false;
                            return;
                        }
                    }
                    //Основная фурнитура
                    variant(areaStv, furnitureRec, 1);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Furniture.furn() " + e);
        }
    }

    protected void variant(AreaSimple areaStv, Record furnitureRec, int count) {
        try {
            List<Record> furndetList1 = eFurndet.filter(furnitureRec.getInt(eFurniture.id)); //детализация первый уровень
            List<Record> furndetList2 = furndetList1.stream().filter(rec
                    -> rec.getInt(eFurndet.id) != rec.getInt(eFurndet.furndet_id)).collect(toList()); //детализация второй уровень

            //TODO Реализовать описание сторон фурнитуры
            //Цикл по описанию сторон фурнитуры
            List<Record> furnsidetList = eFurnside1.filter(furnitureRec.getInt(eFurniture.id)); //список описания сторон
            for (Record furnside1Rec : furnsidetList) {
                Layout layout = (Layout) Layout.ANY.find(furnside1Rec.getInt(eFurnside1.side_num));
                ElemSimple elemFrame = areaStv.frames.stream().filter(e -> e.layout() == layout).findFirst().get();

                //ФИЛЬТР вариантов с учётом стороны
                if (furnitureVar.filter(elemFrame, furnside1Rec) == false) {
                    return;
                }
            }
            //Цикл по детализации (первый уровень)        
            for (Record furndetRec1 : furndetList1) {
                if (furndetRec1.getInt(eFurndet.furndet_id) == furndetRec1.getInt(eFurndet.id)) {
                    if (detail(areaStv, furndetRec1, count) == true) {

                        //Цикл по детализации (второй уровень)
                        for (Record furndetRec2 : furndetList2) {
                            if (furndetRec2.getInt(eFurndet.furndet_id) == furndetRec1.getInt(eFurndet.id)) {
                                if (detail(areaStv, furndetRec2, count) == true) {

                                    //Цикл по детализации (третий уровень)
                                    for (Record furndetRec3 : furndetList2) {
                                        if (furndetRec3.getInt(eFurndet.furndet_id) == furndetRec2.getInt(eFurndet.id)) {
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
            System.err.println("Ошибка:Furniture.variant() " + e);
        }
    }

    protected boolean detail(AreaSimple areaStv, Record furndetRec, int countKit) {
        try {
            Record artiklRec = eArtikl.find(furndetRec.getInt(eFurndet.artikl_id), false);
            HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //тут накапливаются параметры element и specific

            //ФИЛЬТР сделано для убыстрения поиска ручки, 
            //подвеса, замка при конструировании окна
            if (shortPass == true) {
                if (furndetRec.getInt(eFurndet.furndet_id) == furndetRec.getInt(eFurndet.id) && furndetRec.get(eFurndet.furniture_id2) == null) {
                    if (artiklRec.getInt(eArtikl.level1) != 2
                            || (artiklRec.getInt(eArtikl.level1) == 2 && LEVEL.contains(artiklRec.getInt(eArtikl.level2)) == false)) {
                        return false;  //т.к. ручки, подвеса, замка на этих уровнях нет
                    }
                }
            }
            furnitureDet.detailRec = furndetRec; //текущий элемент детализации

            //ФИЛЬТР параметров детализации 
            if (furnitureDet.filter(mapParam, areaStv, furndetRec) == false) {
                return false;
            }

            //Проверка по ограничению сторон
            //Цикл по ограничению сторон фурнитуры
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
                    el = UCom.layout(areaStv.frames, Layout.BOT);
                    length = el.length() - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                } else if (side == 2) {
                    el = UCom.layout(areaStv.frames, Layout.RIG);
                    length = el.length() - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                } else if (side == 3) {
                    el = UCom.layout(areaStv.frames, Layout.TOP);
                    length = el.length() - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                } else if (side == 4) {
                    el = UCom.layout(areaStv.frames, Layout.LEF);
                    length = el.length() - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                }
                if (length >= furnside2Rec.getDbl(eFurnside2.len_max) || (length < furnside2Rec.getDbl(eFurnside2.len_min))) {
                    return false; //не прошли ограничение сторон
                }
            }

            //Не НАБОР (элемент из мат. ценности)
            if (furndetRec.get(eFurndet.furniture_id2) == null) {
                if (artiklRec.getInt(eArtikl.id) != -1) { //артикул есть

                    ElemSimple sideStv = determOfSide(mapParam, areaStv);
                    TRecord spcAdd = new TRecord("ФУРН", furndetRec, artiklRec, sideStv, mapParam);

                    //Ловим ручку, петлю, замок и присваиваем 
                    //артикул и цвет в spcAdd и в свойства створки
                    //если level2 = 13 идет только в тарификацию 
                    if (shortPass == true && spcAdd.artiklRec.getInt(eArtikl.level1) == 2
                            && LEVEL.contains(artiklRec.getInt(eArtikl.level2)) == true) {
                        settingStvAndSpc(areaStv, spcAdd);

                    } else { //цвет элемента в spcAdd
                        UColor.choiceFromArtOrSeri(spcAdd);
                    }
                    //Добавим спецификацию в элемент
                    if (shortPass == false) {
                        spcAdd.count = UCom.getDbl(spcAdd.getParam(spcAdd.count, 24030));
                        spcAdd.count = spcAdd.count * countKit; //умножаю на количество комплектов
                        sideStv.addSpecific(spcAdd);
                    }
                }

                //Это НАБОР (зацикливание)
            } else {
                int countKi2 = (mapParam.get(24030) == null) ? 1 : Integer.valueOf((mapParam.get(24030)));
                Record furnitureRec2 = eFurniture.find(furndetRec.getInt(eFurndet.furniture_id2));
                variant(areaStv, furnitureRec2, countKi2); //рекурсия для обработки наборов
            }
            return true;

        } catch (Exception e) {
            System.err.println("Ошибка:Furniture.detail() " + e);
            return false;
        }
    }

    //Ловим ручку, подвес, замок и 
    //присваиваем знач. в spcAdd и створку    
    private void settingStvAndSpc(AreaSimple stvArea, TRecord spcAdd) {
        AreaStvorka areaStv = (AreaStvorka) stvArea;

        if (spcAdd.artiklRec.getInt(eArtikl.level1) == 2) {
            //РУЧКА
            if (spcAdd.artiklRec.getInt(eArtikl.level2) == 11) {

                //Артикл
                if (UPar.isFinite(areaStv.gson.param, PKjson.artiklHand)) { //если есть параметр то устан. вручную
                    spcAdd.artiklRec(areaStv.handRec[0]); //выбр. вручную
                } else {
                    areaStv.handRec[1] = spcAdd.artiklRec; //из детализации подбор
                }
                //Цвет
                spcAdd.color(areaStv.handColor[0], -3, -3);  //перв. запись в текстуре артикулов или выбр. вручную
                if (UPar.isFinite(areaStv.gson.param, PKjson.colorHand) == false) { //если нет параметра то подбор
                    if (UColor.choiceFromArtOrSeri(spcAdd) == true) { //подбор по цвету
                        areaStv.handColor[0] = spcAdd.colorID1; //из детализации подбор
                        areaStv.handColor[1] = spcAdd.colorID1; //из детализации подбор
                    }
                }
                //ПОДВЕС
            } else if (spcAdd.artiklRec.getInt(eArtikl.level2) == 12) {

                //Артикл
                if (UPar.isFinite(areaStv.gson.param, PKjson.artiklLoop)) { //если есть параметр то устан. вручную
                    spcAdd.artiklRec(areaStv.loopRec[0]); //выбр. вручную
                } else {
                    areaStv.loopRec[1] = spcAdd.artiklRec; //из детализации подбор
                }
                //Цвет
                spcAdd.color(areaStv.loopColor[0], -3, -3);  //перв. запись в текстуре артикулов или выбр. вручную
                if (UPar.isFinite(areaStv.gson.param, PKjson.colorLoop) == false) { //если нет параметра то подбор
                    if (UColor.choiceFromArtOrSeri(spcAdd) == true) { //подбор по цвету
                        areaStv.loopColor[0] = spcAdd.colorID1;
                    }
                }
                //ЗАМОК
            } else if (spcAdd.artiklRec.getInt(eArtikl.level2) == 9) {

                //Артикл
                if (UPar.isFinite(areaStv.gson.param, PKjson.artiklLock)) {
                    spcAdd.artiklRec(areaStv.lockRec[0]); //выбр. вручную
                } else {
                    areaStv.lockRec[1] = spcAdd.artiklRec; //из детализации подбор
                }
                //Цвет
                spcAdd.color(areaStv.lockColor[0], -3, -3);  //перв. запись в текстуре артикулов или выбр. вручную
                if (UPar.isFinite(areaStv.gson.param, PKjson.colorLock) == false) { //если нет параметра то подбор
                    if (UColor.choiceFromArtOrSeri(spcAdd) == true) { //подбор по цвету
                        areaStv.lockColor[1] = spcAdd.colorID1; //из детализации подбор
                    }
                }
            }
        }
    }

    public ElemSimple determOfSide(HashMap<Integer, String> mapParam, AreaSimple area5e) {

        //Через параметр
        if ("1".equals(mapParam.get(25010))) {
            return UCom.layout(area5e.frames, Layout.BOT);
        } else if ("2".equals(mapParam.get(25010))) {
            return UCom.layout(area5e.frames, Layout.RIG);
        } else if ("3".equals(mapParam.get(25010))) {
            return UCom.layout(area5e.frames, Layout.TOP);
        } else if ("4".equals(mapParam.get(25010))) {
            return UCom.layout(area5e.frames, Layout.LEF);
        } else {
            //Там где крепится ручка
            return determOfSide(area5e);
        }
    }

    //Там где крепится ручка
    public static ElemSimple determOfSide(AreaSimple area5e) {
        if (area5e instanceof AreaStvorka) {
            int id = ((AreaStvorka) area5e).typeOpen.id;
            if (List.of(1, 3, 11).contains(id)) {
                return UCom.layout(area5e.frames, Layout.LEF);
            } else if (List.of(2, 4, 12).contains(id)) {
                return UCom.layout(area5e.frames, Layout.RIG);
            } else {
                return UCom.layout(area5e.frames, Layout.BOT);
            }
        }
        return area5e.frames.getFirst();  //первая попавшаяся        
    }
}
