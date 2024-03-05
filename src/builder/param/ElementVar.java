package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eElement;
import domain.eElempar1;
import enums.Layout;
import java.util.List;
import builder.Wincalc;
import builder.model.AreaStvorka;
import builder.model.ElemSimple;
import builder.model.UGeo;
import common.UCom;
import common.listener.ListenerParam;
import enums.Type;
import enums.TypeJoin;
import java.util.ArrayList;
import org.locationtech.jts.geom.Envelope;

//Составы 31000, 37000
public class ElementVar extends Par5s {

    public ElementVar(Wincalc winc) {
        super(winc);
        listenerList = new ArrayList<ListenerParam>();
    }

    public boolean filter(ElemSimple elem5e, Record elementRec) {

        listenerList.clear();
        List<Record> paramList = eElempar1.find3(elementRec.getInt(eElement.id)); //список параметров вариантов использования
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //Цикл по параметрам состава
        for (Record rec : paramList) {
            if (check(elem5e, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(ElemSimple elem5e, Record rec) {
        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {

                case 31000: //Для технологического кода контейнера 
                    if (!UPar.is_STRING_XX000(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 31001: //Максимальное заполнение изделия, мм 
                {
                    List<ElemSimple> glassList = winc.listElem.filter(Type.GLASS);
                    double depth = 0;
                    for (ElemSimple glass : glassList) {
                        if (glass.artiklRecAn.getDbl(eArtikl.depth) > depth) {
                            depth = (glass.artiklRecAn.getDbl(eArtikl.depth));
                        }
                    }
                    if (UCom.containsNumbJust(rec.getStr(TEXT), depth) == false) {
                        return false;
                    }
                }
                break;
                case 31002:  //Если профиль 
                    if ("арочный".equals(rec.getStr(TEXT)) == true && (elem5e.owner.type == Type.ARCH && Layout.TOP == elem5e.layout()) == false) {
                        return false;
                    } else if ("прямой".equals(rec.getStr(TEXT)) == true && (elem5e.owner.type == Type.ARCH && Layout.TOP == elem5e.layout()) == true) {
                        return false;
                    }
                    break;
                case 31003:  //Если сосединенный артикул  T-обр.
                    if (rec.getStr(TEXT).equals(winc.listJoin.elem(elem5e, 0).artiklRecAn.getStr(eArtikl.code)) == true) {
                        if (winc.listJoin.join(elem5e, 0).type() != TypeJoin.TIMP && winc.listJoin.join(elem5e, 0).type() != TypeJoin.TCON) {
                            return false;
                        }
                    } else if (rec.getStr(TEXT).equals(winc.listJoin.elem(elem5e, 1).artiklRecAn.getStr(eArtikl.code))) {
                        if (winc.listJoin.join(elem5e, 1).type() != TypeJoin.TIMP && winc.listJoin.join(elem5e, 1).type() != TypeJoin.TCON) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                    break;
                case 31004: //Если прилегающий артикул 
                {
                    boolean ret = false;
                    ElemSimple el = winc.listJoin.elem(elem5e, 2);
                    if (rec.getStr(TEXT).equals(el.artiklRecAn.getStr(eArtikl.code))) {
                        ret = true;
                    }
                    if (ret == false) {
                        return false;
                    }
                }
                break;
                case 31005:  //Коды основной текстуры контейнера 
                case 37005:  //Коды основной текстуры контейнера 
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.colorID1) == false) {
                        return false;
                    }
                    break;
                case 31006:  //Коды внутр. текстуры контейнера 
                case 37006:  //Коды внутр. текстуры контейнера  
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.colorID2) == false) {
                        return false;
                    }
                    break;
                case 31007:  //Коды внешн. текстуры контейнера 
                case 37007:  //Коды внешн. текстуры контейнера  
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.colorID3) == false) {
                        return false;
                    }
                    break;
                case 31008: //Эффективное заполнение изделия, мм 
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 31011: //Толщина внешнего/внутреннего заполнения, мм
                {
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if (glassList.get(0).type == Type.GLASS && glassList.get(1).type == Type.GLASS) {
                        if (UCom.containsNumb(rec.getStr(TEXT),
                                glassList.get(0).artiklRec.getDbl(eArtikl.depth),
                                glassList.get(1).artiklRec.getDbl(eArtikl.depth)) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 31012: //Для внешнего заполнения, мм", только для PS3
                {
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if (glassList.get(1).type == Type.GLASS) {
                        if (UCom.containsNumbJust(rec.getStr(TEXT),
                                glassList.get(1).artiklRec.getDbl(eArtikl.depth)) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 31013: //Для внутреннего заполнения, мм", только для PS3
                {
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if (glassList.get(0).type == Type.GLASS) {
                        if (UCom.containsNumbJust(rec.getStr(TEXT),
                                glassList.get(0).artiklRec.getDbl(eArtikl.depth)) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 31014: //Заполнения одинаковой толщины 
                {
                    List<ElemSimple> glassList = UPar.getGlassDepth(elem5e);
                    if ("Да".equals(rec.getStr(TEXT)) == true) {
                        if (glassList.get(0).artiklRecAn.getDbl(eArtikl.depth) != glassList.get(1).artiklRecAn.getDbl(eArtikl.depth)) {
                            return false;
                        }
                    } else {
                        if (glassList.get(0).artiklRecAn.getDbl(eArtikl.depth) == glassList.get(1).artiklRecAn.getDbl(eArtikl.depth)) {
                            return false;
                        }
                    }
                }
                break;
                case 31015:  //Разбиение профиля по уровням 
                    message(grup);
                    if ("Авто".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 31016:  //Зазор_на_метр,_мм/Размер_,мм терморазрыва 
                    message(grup);
                    break;
                case 31017: //Код системы содержит строку 
                case 37017: //Код системы содержит строку 
                    if (UPar.is_13017_14017_24017_25017_31017_33017_34017_37017_38017(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 31019:  //Правило подбора текстур
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 31020:  //Ограничение угла к горизонту, °
                    if (UCom.containsNumbJust(rec.getStr(TEXT), UGeo.anglHoriz(elem5e.x1(), elem5e.y1(), elem5e.x2(), elem5e.y2())) == false) {
                        return false;
                    }
                    break;
                case 31033: //Если предыдущий артикул 
                    if (rec.getStr(TEXT).equals(winc.listJoin.elem(elem5e, 0).artiklRecAn.getStr(eArtikl.code)) == false) {
                        return false;
                    }
                    break;
                case 31034:  //Если следующий артикул 
                    if (rec.getStr(TEXT).equals(winc.listJoin.elem(elem5e, 1).artiklRecAn.getStr(eArtikl.code)) == false) {
                        return false;
                    }
                    break;
                case 31035:  //Уровень створки 
                    message(grup);
                    break;
                case 31037:  //Название фурнитуры содержит 
                    if (UPar.is_31037_38037_39037_40037(elem5e, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 31040:  //Поправка габарита накладки, мм 
                    message(grup);
                    break;
                case 31041:  //Ограничение длины профиля, мм 
                    if (UCom.containsNumbJust(rec.getStr(TEXT), elem5e.length()) == false) {
                        return false;
                    }
                    break;
                case 31042: //Максимальная длина, мм"
                    message(grup);
                    break;
                case 31050: //Контейнер имеет тип 
                    if (UPar.is_1005x6_2005x6_3005_4005_11005_12005_31050_33071_34071(rec.getStr(TEXT), elem5e) == false) {
                        return false;
                    }
                    break;
                case 31051:  //Если створка фурнитуры 
                    if (elem5e.owner.type == Type.STVORKA) {
                        if ("ведущая".equals(rec.getStr(TEXT)) == true && ((AreaStvorka) elem5e.owner).knobRec.getInt(eArtikl.id) == -3) {
                            return false;
                        } else if ("ведомая".equals(rec.getStr(TEXT)) == true && ((AreaStvorka) elem5e.owner).knobRec.getInt(eArtikl.id) != -3) {
                            return false;
                        }
                    }
                    break;
                case 31052:  //Поправка в спецификацию, мм 
                    listenerList.add(() -> {
                        elem5e.spcRec.width = elem5e.spcRec.width + rec.getDbl(TEXT);
                    });
                    break;
                case 31054:  //Коды основной текстуры изделия
                case 37054:  //Коды основной текстуры изделия    
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID1) == false) {
                        return false;
                    }
                    break;
                case 31055:  //Коды внутр. и внешн. текстуры изд.
                case 37055:  //Коды внутр. и внешн. текстуры изд. 
                    if ((UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID2) == true
                            && UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID3) == true) == false) {
                        return false;
                    }
                    break;
                case 31056:  //Коды внутр. или внеш. текстуры изд. 
                case 37056:  //Коды внут. или внеш. текстуры изд. 
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID2) == false
                            && UCom.containsColor(rec.getStr(TEXT), elem5e.winc.colorID3) == false) {
                        return false;
                    }
                    break;
                case 31057:  //Внутренняя текстура равна внешней 
                    if (elem5e.colorID2 == elem5e.colorID3) {
                        return false;
                    }
                    break;
                case 31060:  //Допустимый угол между плоскостями, ° 
                    if ((UCom.containsNumbJust(rec.getStr(TEXT), winc.listJoin.join(elem5e, 0).angleBetween()) == true
                            || UCom.containsNumbJust(rec.getStr(TEXT), winc.listJoin.join(elem5e, 1).angleBetween()) == true) == false) {
                        return false;
                    }
                    break;
                case 31073:  //Отправочная марка фасада
                    message(grup);
                    break;
                case 31074:  //На прилегающей створке
                    message(grup);
                    break;
                case 31080:  //Сообщение-предупреждение
                    message(grup);
                    break;
                case 31081:  //Для внешнего/внутреннего угла плоскости, ° 
                    message(grup);
                    break;
                case 31085:  //Надпись на элементе 
                case 37085:  //Надпись на элементе   
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 31090:  //Изменение сторон покраски 
                    listenerList.add(() -> {
                        if ("Да".equals(rec.getStr(TEXT))) {
                            int color = elem5e.spcRec.colorID2;
                            elem5e.spcRec.colorID2 = elem5e.spcRec.colorID3;
                            elem5e.spcRec.colorID2 = color;
                        }
                    });
                    break;
                case 31095:  //Если признак системы конструкции 
                case 37095:  //Если признак системы конструкции                    
                    if (!UPar.is_11095_12095_31095_33095_34095_37095_38095_39095_40095(rec.getStr(TEXT), winc.nuni)) {
                        return false;
                    }
                    break;
                case 31098:  //Бригада, участок) 
                    message(grup);
                    break;
                case 31099:  //Трудозатраты, ч/ч. 
                case 37099:  //Трудозатраты, ч/ч.  
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 31097:  //Трудозатраты по длине 
                    message(grup);
                    break;
                case 31800:  //Код обработки 
                    message(grup);
                    break;
                case 31801:  //Доп.обработки
                    message(grup);
                    break;
                case 37001:  //Установка жалюзи 
                    message(grup);
                    break;
                case 37002:  //Если артикул профиля контура
                {
                    Object r = elem5e.root.frames.stream().filter(f -> f.artiklRecAn.getStr(eArtikl.code).equals(rec.getStr(TEXT))).findFirst().orElse(null);
                    if (r == null) {
                        return false;
                    }
                }
                    break;
                case 37008:  //Тип проема 
                    if (!UPar.is_13003_14005_15005_37008(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 37009: //Тип заполнения 
                {
                    if ("Прямоугольное".equals(rec.getStr(TEXT)) && elem5e.area.isRectangle() == false) {
                        return false;

                    } else if ("Арочное".equals(rec.getStr(TEXT)) && elem5e.area.getNumPoints() > 40 == false) {
                        return false;

                    } else if ("Произвольное".equals(rec.getStr(TEXT))
                            && (elem5e.area.isRectangle() == true || elem5e.area.getNumPoints() > 40 == true)) {
                        return false;
                    }
                }
                break;
                case 37010:  //Ограничение ширины/высоты листа, мм 
                    if (UCom.containsNumb(rec.getStr(TEXT), elem5e.width(), elem5e.height()) == false) {
                        return false;
                    }
                    break;
                case 37030:  //Ограничение площади, кв.м. 
                    Envelope env = elem5e.area.getEnvelopeInternal();
                    if (UCom.containsNumbJust(rec.getStr(TEXT), env.getWidth() / 1000 * env.getHeight() / 1000) == false) {
                        return false;
                    }
                    break;
                case 37042: //Допустимое соотношение габаритов б/м
                {
                    double max = (elem5e.width() > elem5e.height()) ? elem5e.width() : elem5e.height();
                    double min = (elem5e.width() > elem5e.height()) ? elem5e.height() : elem5e.width();
                    if (UCom.containsNumbJust(rec.getStr(TEXT), max / min) == false) {
                        return false;
                    }
                }
                break;
                case 37043: //Макс. соотношение габаритов (б/м)
                {
                    double max = (elem5e.width() > elem5e.height()) ? elem5e.width() : elem5e.height();
                    double min = (elem5e.width() > elem5e.height()) ? elem5e.height() : elem5e.width();
                    if (rec.getDbl(TEXT) < max / min) {
                        return false;
                    }
                }
                break;
                case 37080: //Сообщение-предупреждение
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37098:  //Бригада участок
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37097:  //Трудозатраты по 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37108:  //Коэффициенты АКЦИИ 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37310:  //Сопротивление теплопередаче, м2*°С/Вт 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37320:  //Воздухопроницаемость, м3/ ч*м2
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37330:  //Звукоизоляция, дБА 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37340:  //Коэффициент пропускания света 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37350:  //Сопротивление ветровым нагрузкам, Па 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 37351:  //Номер поверхности 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "Код " + grup + "  не обработан!!!";
            }
        } catch (Exception e) {
            System.err.println("Ошибка:param.ElementVar.check()  parametr=" + grup + "    " + e);
            return false;
        }

        return true;
    }
}
