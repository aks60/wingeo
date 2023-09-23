package builder.param;

import dataset.Record;
import domain.eElemdet;
import domain.eElempar2;
import domain.eSetting;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.model.Com5t;
import builder.model.ElemSimple;
import common.UCom;
import domain.eArtikl;
import enums.Type;

//Составы 33000, 34000, 38000, 39000, 40000
public class ElementDet extends Par5s {

    public ElementDet(Wincalc winc) {
        super(winc);
    }

    public boolean filter(HashMap<Integer, String> mapParam, ElemSimple elem5e, Record elemdetRec) {

        List<Record> paramList = eElempar2.find3(elemdetRec.getInt(eElemdet.id)); //список параметров детализации 
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //Цикл по параметрам составов
        for (Record rec : paramList) {
            if (ElementDet.this.check(mapParam, elem5e, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(HashMap<Integer, String> mapParam, ElemSimple elem5e, Record rec) {
        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {

                case 33000: //Для технологического кода контейнера 
                case 34000: //Для технологического кода контейнера 
                    if (!UPar.is_STRING_XX000(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 33001:  //Если признак состава 
                case 34001:  //Если признак состава 
                    if (UPar.is_11001_11002_12001_12002_13001_14001_15001_33001_34001(rec.getStr(TEXT), elem5e) == false) {
                        return false;
                    }
                    break;
                case 33002:  //Расчет пролетов соединений 
                case 34002:  //Расчет пролетов соединений 
                    message(grup);
                    break;
                case 33003:  //Расчет пролетов заполнений, мм 
                case 34003:  //Расчет пролетов заполнений, мм 
                    message(grup);
                    break;
                case 33004:  //Расчет от длины профиля стойки 
                case 34004:  //Расчет от длины профиля стойки                   
                    message(grup);
                    break;
                case 33005:  //Коды основной текстуры контейнера 
                case 34005:  //Коды основной текстуры контейнера
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc().colorID1) == false) {
                        return false;
                    }
                    break;
                case 33006:  //Коды внутр. текстуры контейнера
                case 34006:  //Коды внутр. текстуры контейнера 
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc().colorID2) == false) {
                        return false;
                    }
                    break;
                case 33007:  //Коды внешн. текстуры контейнера 
                case 34007:  //Коды внешн. текстуры контейнера     
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc().colorID3) == false) {
                        return false;
                    }
                    break;
                case 33008:  //Эффективное заполнение изд., мм 
                case 34008:  //Эффективное заполнение изделия, мм 
                case 40008:  //Эффективное заполнение изд., мм                    
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 33011: //Толщина внешнего/внутреннего заполнения, мм
                case 34011: //Толщина внешнего/внутреннего заполнения, мм
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if (glassList.get(0).type() == Type.GLASS && glassList.get(1).type() == Type.GLASS) {
                        if ("ps3".equals(eSetting.val(2))) { //Толщина заполнения, мм
                            if (UCom.containsNumbAny(rec.getStr(TEXT),
                                    glassList.get(0).artiklRec().getDbl(eArtikl.depth),
                                    glassList.get(1).artiklRec().getDbl(eArtikl.depth)) == false) {
                                return false;
                            }
                        } else if (UCom.containsNumb(rec.getStr(TEXT),
                                glassList.get(0).artiklRec().getDbl(eArtikl.depth),
                                glassList.get(1).artiklRec().getDbl(eArtikl.depth)) == false) {
                            return false;
                        }
                    }
                    break;
                case 33017: //Код системы содержит строку 
                case 34017: //Код системы содержит строку 
                case 38017: //Код системы содержит строку     
                case 39017: //Код системы содержит строку 
                case 40017: //Код системы содержит строку                 
                    if (UPar.is_13017_14017_24017_25017_31017_33017_34017_37017_38017(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 33030:  //Количество 
                case 38030:  //Количество   
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33031:  //Расчет количества 
                case 34061:  //Расчет количества                 
                    message(grup);
                    break;
                case 33032:  //Периметр покраски по периметру 
                    message(grup);
                    break;
                case 33033:  //Расход по профилю
                    message(grup);
                    break;
                case 33034:  //Периметр покраски, мм 
                    message(grup);
                    break;
                case 33035:  //TODO Расход по поверхности на кв.м. 
                    message(grup);
                    break;
                case 33040:  //Порог расчета, мм 
                case 38040:  //Порог расчета, мм     
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33050:  //Шаг, мм 
                case 38050:  //Шаг, мм     
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33060:  //Количество на шаг 
                case 38060:  //Количество на шаг                    
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33062:  //Если стойка удлинена 
                case 34062:  //Если стойка удлинена 
                    message(grup);
                    break;
                case 33063: //Диапазон веса створки, кг 
                case 34063: //Диапазон веса створки, кг 
                {
                    Com5t glass = elem5e.owner().childs().stream().filter(el -> el.type() == Type.GLASS).findFirst().orElse(null);
                    if (glass != null) {
                        double weight = ((glass.width() * glass.height()) / 1000000) * glass.artiklRecAn().getDbl(eArtikl.density);
                        if (UCom.containsNumbExp(rec.getStr(TEXT), weight) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 33066:  //Если номер стороны в контуре
                case 34066:  //Если номер стороны в контуре    
                    if (!UPar.is_INT_33066_34066(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 33067:  //Коды основной текстуры изделия    
                case 34067:  //Коды основной текстуры изделия 
                case 38067:  //Коды основной текстуры изделия    
                case 39067:  //Коды основной текстуры изделия
                case 40067:  //Коды основной текстуры изделия                     
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc().colorID1) == false) {
                        return false;
                    }
                    break;
                case 33068:  //Коды внутр. текстуры изделия    
                case 34068:  //Коды внутр. текстуры изделия 
                case 38068:  //Коды внутр. текстуры изделия 
                case 39068:  //Коды внутр. текстуры изделия
                case 40068:  //Коды внутр. текстуры изделия    
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc().colorID2) == false) {
                        return false;
                    }
                    break;
                case 33069:  //Коды внешн. текстуры изделия    
                case 34069:  //Коды внешн. текстуры изделия 
                case 38069:  //Коды внешн. текстуры изделия 
                case 39069:  //Коды внешн. текстуры изделия 
                case 40069:  //Коды внешн. текстуры изделия  
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc().colorID3) == false) {
                        return false;
                    }
                    break;
                case 33071:  //Контейнер типа 
                case 34071:  //Контейнер типа
                    if (UPar.is_1005x6_2005x6_3005_4005_11005_12005_31050_33071_34071(rec.getStr(TEXT), elem5e) == false) {
                        return false;
                    }
                    break;
                case 33073:  //Код обработки (;)
                    message(grup);
                    break;
                case 33074:  //На прилегающей створке
                    message(grup);
                    break;
                case 33078:  //Ставить однократно 
                case 34078:  //Ставить однократно 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 33081:  //Для внешнего/внутреннего угла плоскости, ° 
                case 34081:  //Для внешнего/внутреннего угла плоскости, °                        
                    message(grup);
                    break;
                case 33083:  //Точный внутр. угол плоскости, ° 
                case 34083:  //Точный внутр. угол плоскости, ° 
                    message(grup);
                    break;
                case 33088:  //Точный внешний угол плоскости, °
                case 34088:  //Точный внешний угол плоскости, °
                    message(grup);
                    break;
                case 33095:  //Если признак системы конструкции 
                case 34095:  //Если признак системы конструкции
                case 38095:  //Если признак системы конструкции
                case 39095:  //Если признак системы конструкции
                case 40095:  //Если признак системы конструкции 
                    if (!UPar.is_11095_12095_31095_33095_34095_37095_38095_39095_40095(rec.getStr(TEXT), winc.nuni())) {
                        return false;
                    }
                    break;
                case 33099:  //Трудозатраты, ч/ч. 
                case 34099:  //Трудозатраты, ч/ч.
                case 38099:  //Трудозатраты, ч/ч. 
                case 39099:  //Трудозатраты, ч/ч. 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34009: //Если два присоединенных артикула 
                    message(grup);
                    break;
                case 34010:  //Расчет армирования 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34012:  //Для варианта соединения Т (*)
                    message(grup);
                    break;
                case 34013:  //Подбор дистанционных вставок по пролетам 
                    message(grup);
                    break;
                case 34015:  //Расчет длины по 
                    message(grup);
                    break;
                case 34016:  //Прилегание контура створки 
                    message(grup);
                    break;
                case 34030:  //[ * коэф-т ] 
                case 39030:  //[ * коэф-т ]     
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34049:  //Поправка по нормали от начала/конца, мм 
                    message(grup);
                    break;
                case 34050: //Поправка, мм
                    message(grup);
                    break;
                case 34051:  //Поправка, мм 
                case 39020:  //Поправка, мм 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34052:  //Поправка не прямого угла импоста, мм 
                    message(grup);
                    if (elem5e.spcRec().getParam("0", 31052).equals(rec.getStr(TEXT)) == false) {
                        mapParam.put(grup, rec.getStr(TEXT));
                    }
                    break;
                case 34060:  //Количество
                case 39060:  //Количество
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34064:  //Учёт поправок соединений для составов 
                    message(grup);
                    break;
                case 34070:  //Длина, мм 
                case 39070:  //Длина, мм
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34072:  //Смещение от уровня деления, мм 
                    message(grup);
                    break;
                case 34075:  //Углы реза
                case 39075:  //Углы реза 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34077:  //Задать Угол_реза_1/Угол_реза_2, ° 
                case 39077:  //Задать Угол_реза_1/Угол_реза_2, ° 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 34079:  //Длина подбирается из списка, мм 
                    message(grup);
                    break;
                case 34080:  //Длина округляется с шагом, мм 
                    message(grup);
                    break;
                case 34097:  //Трудозатраты по длине 
                    message(grup);
                    break;
                case 38004:  //Расчет 
                case 39005:  //Расчет 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 38010:  //Номер стороны 
                case 39002:  //Номер стороны 
                    if (UPar.is_38010_39002(elem5e, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 38037:  //Название фурнитуры содержит 
                case 39037:  //Название фурнитуры содержит 
                case 40037:  //Название фурнитуры содержит 
                    if (UPar.is_31037_38037_39037_40037(elem5e, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 38039:  //Для типа открывания 
                case 39039:  //Для типа открывания 
                    if (UPar.is_1039_38039_39039(elem5e, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 38081:  //Если артикул профиля контура 
                case 39081:  //Если артикул профиля контура 
                    if (elem5e.artiklRecAn().getStr(eArtikl.code).equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 38108:  //Применять коэффициенты АКЦИИ для МЦ 
                case 39108:  //Применять коэффициенты АКЦИИ для МЦ 
                case 40108:  //Применять коэффициенты АКЦИИ для МЦ 
                    message(grup);
                    break;
                case 38109:  //Возможное управление жалюзи 
                case 39109:  //Возможное управление жалюзи 
                case 40109:  //Возможное управление жалюзи 
                    message(grup);
                    break;
                case 38113:  //Установить текстуру по 
                case 39113:  //Установить текстуру по 
                case 40113:  //Установить текстуру по 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 39063:  //Округлять количество до ближайшего 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 39080:  //Шаг вагонки ламели, мм 
                    message(grup);
                    break;
                case 39093:  //Поперечину ставить :
                    mapParam.put(grup, rec.getStr(TEXT));
                    message(grup);
                    break;
                case 39097:  //Трудозатраты по периметру 
                    message(grup);
                    break;
                case 40004:  //Ширина заполнения, мм 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 40005:  //Поправка ширины/высоты, мм 
                case 40010:  //Поправка на стороны четные/нечетные, мм 
                       mapParam.put(grup, rec.getStr(TEXT));                   
                    break;
                case 40006:  //Высота заполнения, мм 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 40007:  //Высоту сделать длиной 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "Код " + grup + "  не обработан!!!";
            }
        } catch (Exception e) {
            System.err.println("Ошибка:param.ElementDet.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
