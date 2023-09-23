package builder.param;

import builder.IArea5e;
import dataset.Record;
import domain.eArtikl;
import domain.eFurndet;
import domain.eFurnpar2;
import enums.Layout;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.making.Specific;
import builder.model1.AreaArch;
import builder.ICom5t;
import builder.IElem5e;
import builder.IStvorka;
import common.UCom;
import domain.eColor;
import domain.eGroups;
import enums.LayoutHandle;
import enums.Type;
import java.util.Map;

//Фурнитура
public class FurnitureDet extends Par5s {

    public FurnitureDet(Wincalc winc) {
        super(winc);
    }

    public boolean filter(HashMap<Integer, String> mapParam, IArea5e areaStv, Record furndetRec) {

        this.detailRec = furndetRec;
        List<Record> tableList = eFurnpar2.find(furndetRec.getInt(eFurndet.id));
        if (filterParamDef(tableList) == false) {
            return false; //параметры по умолчанию
        }
        //Цикл по параметрам фурнитуры
        for (Record rec : tableList) {
            if (check(mapParam, areaStv, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(HashMap<Integer, String> mapParam, IArea5e areaStv, Record rec) {

        IStvorka elemStv = (IStvorka) areaStv;
        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {

                case 24001: //Форма контура 
                case 25001: //Форма контура 
                {
                    //"Прямоугольное", "Не прямоугольное", "Не арочное", "Арочное" (TypeElem.AREA - глухарь)
                    if ("прямоугольная".equals(rec.getStr(TEXT)) && Type.RECTANGL.equals(areaStv.type()) == false
                            && Type.AREA.equals(areaStv.type()) == false && Type.STVORKA.equals(areaStv.type()) == false) {
                        return false;
                    } else if ("трапециевидная".equals(rec.getStr(TEXT)) && Type.TRAPEZE.equals(areaStv.type()) == false) {
                        return false;
                    } else if ("арочная".equals(rec.getStr(TEXT)) && Type.ARCH.equals(areaStv.type()) == false) {
                        return false;
                    } else if ("не арочная".equals(rec.getStr(TEXT)) && Type.ARCH.equals(areaStv.type()) == true) {
                        return false;
                    }
                    break;
                }
                case 24002:  //Если артикул створки 
                case 25002:  //Если артикул створки 
                    if (areaStv.frames().entrySet().stream().filter(el -> el.getValue().artiklRec().getStr(eArtikl.code).equals(rec.getStr(TEXT))).findFirst().orElse(null) == null) {
                        return false;
                    }
                    break;
                case 24003:  //Если артикул цоколя 
                case 25003:  //Если артикул цоколя 
                    message(rec.getInt(GRUP));
                    break;
                case 24004: //Если створка прилегает к артикулу 
                    if (areaStv.frames().entrySet().stream().filter(el -> winc.listJoin.elem(el.getValue(), 2).artiklRec().getStr(eArtikl.code).equals(rec.getStr(TEXT))).findFirst().orElse(null) == null) {
                        return false;
                    }
                    break;
                case 24005:  //Коды текстуры створки 
                case 25005:  //Коды текстуры створки 
                    if (areaStv.frames().entrySet().stream().filter(el -> UCom.containsColor(rec.getStr(TEXT), el.getValue().colorID1()) == true).findFirst().orElse(null) == null) {
                        return false;
                    }
                    break;
                case 24006:  //Установить текстуру
                    if ("по текстуре ручки".equals(rec.getStr(TEXT))) {
                        if (elemStv.handleColor() != detailRec.getInt(eFurndet.color_fk)) {
                            return false;
                        }
                    } else if ("по текстуре подвеса".equals(rec.getStr(TEXT))) {
                        if (elemStv.loopColor() != detailRec.getInt(eFurndet.color_fk)) {
                            return false;
                        }
                    } else if ("по текстуре замка".equals(rec.getStr(TEXT))) {
                        if (elemStv.lockColor() != detailRec.getInt(eFurndet.color_fk)) {
                            return false;
                        }
                    }
                    break;
                case 24007: //Коды текстуры ручки 
                case 25007: //Коды текстуры ручки                  
                {
                    String name = eColor.find(areaStv.colorID1()).getStr(eColor.name);
                    if (name.equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                }
                break;
                case 24008: //Если серия створки 
                case 25008: //Если серия створки   
                {
                    int series_id = areaStv.frames().get(Layout.BOTT).artiklRec().getInt(eArtikl.groups4_id);
                    String name = eGroups.find(series_id).getStr(eGroups.name);
                    if (name.equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                }
                break;
                case 24009:  //Коды текстуры подвеса 
                case 25009:  //Коды текстуры подвеса                   
                    for (Map.Entry<Layout, IElem5e> elem : areaStv.frames().entrySet()) {
                        for (Specific spc : elem.getValue().spcRec().spcList) {
                            if (spc.artiklRec.getInt(eArtikl.level1) == 2 && spc.artiklRec.getInt(eArtikl.level2) == 12) {
                                String name = eColor.find(spc.colorID1).getStr(eColor.name);
                                if (name.equals(rec.getStr(TEXT)) == false) {
                                    return false;
                                }
                            }
                        }
                    }
                    break;
                case 24010:  //Номер стороны 
                case 25010:  //Номер стороны                   
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24011:  //Расчет по общей арке 
                case 25011:  //Расчет по общей арке 
                    message(rec.getInt(GRUP));
                    break;
                case 24012:  //Направление открывания
                    if (elemStv.typeOpen().name.equalsIgnoreCase(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 24013: //Выбран авто расчет подвеса 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24017:  //Код системы содержит строку 
                case 25017:  //Код системы содержит строку                    
                    if (UPar.is_13017_14017_24017_25017_31017_33017_34017_37017_38017(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 24030:  //Количество 
                case 25060:  //Количество     
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24032:  //Правильная полуарка 
                case 25032:  //Правильная полуарка 
                    if (winc.rootArea.type() == Type.ARCH) {
                        int k = (int) (winc.rootArea.width() / ((AreaArch) winc.rootArea).radiusArch);
                        if (k != 2) {
                            return false;
                        }
                    }
                    break;
                case 24033: //Фурнитура штульповая 
                case 25033: //Фурнитура штульповая 
                {
                    if (rec.getStr(TEXT).equals("Да")) {
                        boolean ret = false;
                        for (Map.Entry<Layout, IElem5e> entry : areaStv.frames().entrySet()) {
                            if (winc.listJoin.elem(entry.getValue(), 2).type() == Type.SHTULP) {
                                ret = true;
                            }
                        }
                        if (ret == false) {
                            return false;
                        }
                    } else if (rec.getStr(TEXT).equals("Нет")) {
                        boolean ret = false;
                        for (Map.Entry<Layout, IElem5e> entry : areaStv.frames().entrySet()) {
                            if (winc.listJoin.elem(entry.getValue(), 2).type() == Type.SHTULP) {
                                ret = true;
                            }
                        }
                        if (ret == true) {
                            return false;
                        }
                    }
                }
                break;
                case 24036:  //Номер Стороны_X/Стороны_Y набора 
                case 25036:  //Номер Стороны_X/Стороны_Y набора 
                    message(rec.getInt(GRUP));
                    break;
                case 24037:  //Номер стороны по параметру набора 
                case 25037:  //Номер стороны по параметру набора 
                    message(rec.getInt(GRUP));
                    break;
                case 24038:  //Проверять Cторону_(L))/Cторону_(W) 
                case 25038:  //Проверять Cторону_(L)/Cторону_(W)     
                    //TODO Параметры. Тут полные непонятки. Возможно сторона проверки назначается для всего набора
                    mapParamTmp.put(grup, rec.getStr(TEXT));
                    //message(rec.getInt(GRUP));
                    break;
                case 24039:  //Створка заднего плана 
                    message(rec.getInt(GRUP));
                    break;
                case 24040:  //Порог расчета, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24050:  //Шаг, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24060:  //Количество на шаг 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24063: //Диапазон веса, кг 
                case 25063: //Диапазон веса, кг 
                {
                    ICom5t glass = areaStv.childs().stream().filter(el -> el.type() == Type.GLASS).findFirst().orElse(null);
                    if (glass != null) {
                        double weight = ((glass.width() * glass.height()) / 1000000) * glass.artiklRecAn().getDbl(eArtikl.density);
                        if (UCom.containsNumbExp(rec.getStr(TEXT), weight) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 24064: //Ограничение высоты ручки, мм 
                case 25064: //Ограничение высоты ручки, мм 
                {
                    String handl[] = rec.getStr(TEXT).split("-");
                    if (handl.length > 1) {
                        double handl_min = UCom.getDbl(handl[0]);
                        double handl_max = UCom.getDbl(handl[1]);
                        if (handl_min > elemStv.handleHeight() || elemStv.handleHeight() > handl_max) {
                            return false;
                        }
                    }
                    if ("ps3".equals(versionPs)) { //Минимальная высота ручки, мм
                        double handl_min = UCom.getDbl(rec.getStr(TEXT));
                        if (handl_min > elemStv.handleHeight()) {
                            return false;
                        }
                    }
                }
                break;
                case 24065: //Максимальная высота ручки, мм 
                {
                    double handl_max = UCom.getDbl(rec.getStr(TEXT));
                    if (handl_max < elemStv.handleHeight()) {
                        return false;
                    }
                }
                break;
                case 24067:  //Коды основной текстуры изделия 
                case 25067:  //Коды основной текстуры изделия 
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID1) == false) {
                        return false;
                    }
                    break;
                case 24068:  //Коды внутр. текстуры изделия 
                case 25068:  //Коды внутр. текстуры изделия 
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID2) == false) {
                        return false;
                    }
                    break;
                case 24069:  //Коды внешн. текстуры изделия 
                case 25069:  //Коды внешн. текстуры изделия     
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID3) == false) {
                        return false;
                    }
                    break;
                case 24070: //Если высота ручки "по середине", "константная", "не константная", "установлена"
                case 25070: //Если высота ручки
                    if (LayoutHandle.CONST != elemStv.handleLayout() && rec.getStr(TEXT).equals("константная")) {
                        return false;
                    } else if (LayoutHandle.CONST == elemStv.handleLayout() && rec.getStr(TEXT).equals("не константная")) {
                        return false;
                    } else if (LayoutHandle.MIDL != elemStv.handleLayout() && rec.getStr(TEXT).equals("по середине")) {
                        return false;
                    } else if (LayoutHandle.VARIAT != elemStv.handleLayout() && rec.getStr(TEXT).equals("установлена")) {
                        return false;
                    }
                    break;
                case 24072:  //Ручка от низа створки, мм 
                case 25072:  //Ручка от низа створки, мм  
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24073:  //Петля от низа створки, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24074:  //Петля по центру стороны 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24075:  //Петля от верха створки, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24077:  //Смещение замка от ручки, мм 
                    message(rec.getInt(GRUP));
                    break;
                case 24078:  //Замок от края профиля, мм 
                    message(rec.getInt(GRUP));
                    break;
                case 24095:  //Если признак системы конструкции 
                case 25095:  //Если признак системы конструкции 
                    message(rec.getInt(GRUP));
                    break;
                case 24098:  //Бригада, участок) 
                case 25098:  //Бригада, участок) 
                    message(rec.getInt(GRUP));
                    break;
                case 24099:  //Трудозатраты, ч/ч. 
                case 25099:  //Трудозатраты, ч/ч.                    
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 24800:  //Код основной обработки) 
                case 25800:  //Код основной обработки
                    message(rec.getInt(GRUP));
                    break;
                case 24801:  //Доп.основная обработка
                case 25801:  //Доп.основная обработка
                    message(rec.getInt(GRUP));
                    break;
                case 24802:  //Код симметр. обработки 
                case 25802:  //Код симметр. обработки 
                    message(rec.getInt(GRUP));
                    break;
                case 24803:  //Доп.симметр. обработка
                case 25803:  //Доп.симметр. обработка
                    message(rec.getInt(GRUP));
                    break;
                case 25013:  //Укорочение от 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 25030:  //Укорочение, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 25035:  //[ * коэф-т ] 
                    message(rec.getInt(GRUP));
                    break;
                case 25040:  //Длина, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "Код " + grup + "  не обработан!!!";
            }
        } catch (Exception e) {
            System.err.println("Ошибка:param.FurnitureDet.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
