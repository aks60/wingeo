package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eElement;
import domain.eJoinpar1;
import domain.eJoinvar;
import domain.eSetting;
import domain.eSystree;
import java.util.List;
import builder.Wincalc;
import builder.model1.ElemJoining;
import builder.IElem5e;
import builder.IStvorka;
import common.UCom;
import enums.Layout;
import enums.LayoutJoin;
import enums.Type;
import java.util.ArrayList;

//Соединения
public class JoiningVar extends Par5s {

    public JoiningVar(Wincalc winc) {
        super(winc);
        listenerList = new ArrayList();
    }

    public JoiningVar(Wincalc winc, boolean shortPass) {
        super(winc);
        listenerList = new ArrayList();
        this.shortPass = shortPass;
    }

    //1000 - прилегающее соединение, 2000 - угловое на ус, 3000 - угловое (левое, правое), 4000 - Т образное соединение
    public boolean filter(ElemJoining elemJoin, Record joinvarRec) {

        listenerList.clear();
        List<Record> paramList = eJoinpar1.find(joinvarRec.getInt(eJoinvar.id));
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //цикл по параметрам элементов соединения
        for (Record rec : paramList) {
            if (check(elemJoin, rec, joinvarRec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(ElemJoining elemJoin, Record rec, Record var) {

        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {
                case 1005:  //Контейнер имеет тип Артикула1/Артикула2
                case 2005:  //Контейнер имеет тип Артикула1/Артикула2
                case 3005:  //Контейнер имеет тип Артикула1/Артикула2
                case 4005: //Контейнер имеет тип Артикула1/Артикула2
                    if ("ps3".equals(eSetting.val(2))) { //Контейнер Артикула 1 имеет тип
                        String[] arr = {"коробка", "створка", "импост", "стойка", "эркер"};
                        int[] index = {1, 2, 3, 5, 19};
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].equals(rec.getStr(TEXT)) && UCom.containsNumbJust(String.valueOf(index[i]), elemJoin.elem1.type().id) == false) {
                                return false;
                            }
                        }
                    } else {
                        if (UCom.containsNumb(rec.getStr(TEXT), elemJoin.elem1.type().id, elemJoin.elem2.type().id) == false) {
                            return false;
                        }
                    }
                    break;
                case 1006:  //Контейнер Артикула 2 имеет тип
                case 2006:  //Контейнер Артикула 2 имеет тип
                case 3006:  //Контейнер Артикула 2 имеет тип
                case 4006:  //Контейнер Артикула 2 имеет тип
                    if ("ps3".equals(eSetting.val(2))) {
                        String[] arr = {"коробка", "створка", "импост", "стойка", "эркер"};
                        int[] index = {1, 2, 3, 5, 19};
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].equals(rec.getStr(TEXT)) && UCom.containsNumbJust(String.valueOf(index[i]), elemJoin.elem2.type().id) == false) {
                                return false;
                            }
                        }
                    }
                    break;
                case 1008:  //Эффективное заполнение изд., мм
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 1010:  //Внешнее соединение 
                case 4010:  //Внешнее соединение                     
//                    if ("Да".equals(rec.getStr(TEXT))) {
//                        if (winc.listJoin.get(winc.rootArea.x2() + ":" + winc.rootArea.y1()) != elemJoin
//                                && winc.listJoin.get(winc.rootArea.x2() + ":" + winc.rootArea.y2()) != elemJoin
//                                && winc.listJoin.get(winc.rootArea.x2() + ":" + winc.rootArea.y1()) != elemJoin
//                                && winc.listJoin.get(winc.rootArea.x1() + ":" + winc.rootArea.y1()) != elemJoin) {
//                            return false;
//                        }
//                    }
                    message(rec.getInt(GRUP));
                    break;
                case 1011: //Для Артикула 1 указан состав 
                case 4011: //Для Артикула 1 указан состав     
                {
                    boolean substr = false;
                    List<Record> elementList = eElement.find2(elemJoin.elem1.artiklRec().getInt(eArtikl.code));
                    for (Record elementRec : elementList) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr = true;
                            break;
                        }
                    }
                    if (substr == false) {
                        return false;
                    }
                }
                break;
                case 1012: //Для Артикула 2 указан состав                  
                case 4012: //Для Артикула 2 указан состав     
                {
                    boolean substr = false;
                    List<Record> elementList = eElement.find2(elemJoin.elem2.artiklRec().getInt(eArtikl.code));
                    for (Record elementRec : elementList) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr = true;
                            break;
                        }
                    }
                    if (substr == false) {
                        return false;
                    }
                }
                break;
                case 1013:  //Для Артикулов не указан состав
                case 2013:  //Для Артикулов не указан состав 
                case 3013:  //Для Артикулов не указан состав
                case 4013: //Для Артикулов не указан состав  
                {
                    List<Record> elementList1 = eElement.find2(elemJoin.elem1.artiklRec().getInt(eArtikl.code));
                    boolean substr1 = false;
                    for (Record elementRec : elementList1) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr1 = true;
                            break;
                        }
                    }
                    boolean substr2 = false;
                    List<Record> elementList2 = eElement.find2(elemJoin.elem2.artiklRec().getInt(eArtikl.code));
                    for (Record elementRec : elementList2) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr2 = true;
                            break;
                        }
                    }
                    if (substr1 == true || substr2 == true) {
                        return false;
                    }
                }
                break;
                case 1014:  //Только горизонтальная ориентация
                    if ("ps3".equals(eSetting.val(2))) {
                        if ("Да".equals(rec.getStr(TEXT)) && elemJoin.elem1.layout() != Layout.HORIZ && elemJoin.elem1.layout() != Layout.BOTT && elemJoin.elem1.layout() != Layout.TOP) {
                            return false;
                        } else if ("Нет".equals(rec.getStr(TEXT)) && (elemJoin.elem1.layout() == Layout.HORIZ || elemJoin.elem1.layout() == Layout.BOTT || elemJoin.elem1.layout() == Layout.TOP)) {
                            return false;
                        }
                    }
                    break;
                case 1015:  //Только вертикальная ориентация
                    if ("ps3".equals(eSetting.val(2))) {
                        if ("Да".equals(rec.getStr(TEXT)) && elemJoin.elem1.layout() != Layout.VERT && elemJoin.elem1.layout() != Layout.RIGHT && elemJoin.elem1.layout() != Layout.LEFT) {
                            return false;
                        } else if ("Нет".equals(rec.getStr(TEXT)) && (elemJoin.elem1.layout() == Layout.VERT || elemJoin.elem1.layout() == Layout.RIGHT || elemJoin.elem1.layout() == Layout.LEFT)) {
                            return false;
                        }
                    }
                    break;
                case 1020:  //Ограничение угла к горизонту, °
                    if ("ps3".equals(eSetting.val(2))) { //Угол к горизонту минимальный
                        if (elemJoin.elem1.anglHoriz() < rec.getDbl(TEXT)) {
                            return false;
                        }
                    } else if (UCom.containsNumbJust(rec.getStr(TEXT), elemJoin.elem1.anglHoriz()) == true) {
                        return false;
                    }
                    break;
                case 1030:  //Угол к горизонту максимальный
                    if ("ps3".equals(eSetting.val(2))) {
                        if (elemJoin.elem1.anglHoriz() > rec.getDbl(TEXT)) {
                            return false;
                        }
                    }
                    break;
                case 1031:  //Точный угол к горизонту
                    if ("ps3".equals(eSetting.val(2))) {
                        if (elemJoin.elem1.anglHoriz() == rec.getDbl(TEXT)) {
                            return false;
                        }
                    }
                    break;
                case 1032:  //Исключить угол к горизонту, °
                    if ("ps3".equals(eSetting.val(2))) {
                        if (elemJoin.elem1.anglHoriz() == rec.getDbl(TEXT)) {
                            return false;
                        }
                    }
                    break;
                case 1035:  //Уровень створки 
                    message(rec.getInt(GRUP));
                    break;
                case 1039:  //Для типа открывания 
                    if (UPar.is_1039_38039_39039(elemJoin.elem1, rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 1040:  //Размер, мм (Смещение осей рамы и створки. Наследие ps3)
                    //Параметр вычисляктся на раннем этапе см. конструктор AreaStvorka()
                    //Применяется если сист. константы отсутствуют
                    if (elemJoin.elem1.type() == Type.STVORKA_SIDE) {
                        listenerList.add(() -> {
                            IStvorka stv = (IStvorka) elemJoin.elem1.owner();
                            if (elemJoin.elem1.layout() == Layout.BOTT) {
                                stv.offset()[0] = rec.getDbl(TEXT);
                            } else if (elemJoin.elem1.layout() == Layout.RIGHT) {
                                stv.offset()[1] = rec.getDbl(TEXT);
                            } else if (elemJoin.elem1.layout() == Layout.TOP) {
                                stv.offset()[2] = rec.getDbl(TEXT);
                            } else if (elemJoin.elem1.layout() == Layout.LEFT) {
                                stv.offset()[3] = rec.getDbl(TEXT);
                            }
                        });
                    }
                    break;
                case 1043: //Ограничение габарита контура, мм 
                {
                    double area = winc.rootArea.width() * winc.rootArea.height() / 1000000;
                    if (UCom.containsNumbExp(rec.getStr(TEXT), area) == false) {
                        return false;
                    }
                }
                break;
                case 1090:  //Смещение по толщине, мм 
                    message(rec.getInt(GRUP));
                    break;
                case 1095:  //Если признак системы конструкции 
                case 2095:  //Если признак системы конструкции 
                case 3095:  //Если признак системы конструкции 
                case 4095: //Если признак системы конструкции 
                {
                    Record systreefRec = eSystree.find(winc.nuni());
                    String[] arr = rec.getStr(TEXT).split(";");
                    List<String> arrList = List.of(arr);
                    boolean ret = false;
                    for (String str : arrList) {
                        if (systreefRec.getInt(eSystree.types) == Integer.valueOf(str) == true) {
                            ret = true;
                        }
                    }
                    if (ret == false) {
                        return false;
                    }
                }
                break;
                case 1098:  //Бригада (участок)
                case 2098:  //Бригада (участок) 
                case 3098:  //Бригада (участок)
                case 4098:  //Бригада (участок)
                    message(rec.getInt(GRUP));
                    break;
                case 1099:  //Трудозатраты, ч/ч. 
                case 2099:  //Трудозатраты, ч/ч. 
                case 3099:  //Трудозатраты, ч/ч.
                case 4099:  //Трудозатраты, ч/ч. 
                    elemJoin.costs = rec.getStr(TEXT);
                    break;
                case 1097:  //Трудозатраты по длине 
                    message(rec.getInt(GRUP));
                    break;
                case 1085:  //Надпись на элементе 
                case 2085:  //Надпись на элементе 
                case 3085:  //Надпись на элементе  
                case 4085:  //Надпись на элементе     
                    message(rec.getInt(GRUP));
                    break;
                case 2003:  //Угол варианта 
                case 3003:  //Угол варианта 
                    if (var.getInt(eJoinvar.types) == 30 || var.getInt(eJoinvar.types) == 31) {
                        if ("левый".equals(rec.getStr(TEXT))) {
                            if (elemJoin.layout == LayoutJoin.ANGL && var.getInt(eJoinvar.types) == 31) {
                                return false;
                            } else if (elemJoin.layout == LayoutJoin.ANGL && var.getInt(eJoinvar.types) == 31) {
                                return false;
                            }
                        } else if ("правый".equals(rec.getStr(TEXT))) {
                            if (elemJoin.layout == LayoutJoin.ANGL && var.getInt(eJoinvar.types) == 30) {
                                return false;
                            } else if (elemJoin.layout == LayoutJoin.ANGL && var.getInt(eJoinvar.types) == 30) {
                                return false;
                            }
                        }
                    } else {
                        if ("левый".equals(rec.getStr(TEXT))) {
                            if (elemJoin.layout == LayoutJoin.ANGL || elemJoin.layout == LayoutJoin.ANGL || elemJoin.layout == LayoutJoin.TEE) {
                                return false;
                            }
                        } else { //левый
                            if (elemJoin.layout == LayoutJoin.ANGL || elemJoin.layout == LayoutJoin.ANGL || elemJoin.layout == LayoutJoin.TEE) {
                                return false;
                            }
                        }
                    }
                    break;
                case 2010:  //Угол минимальный, °
                case 3010:  //Угол минимальный, °
                case 4020:  //Ограничение угла, °
                    if ("ps3".equals(eSetting.val(2))) { //Угол минимальный, °
                        if (rec.getDbl(TEXT) < elemJoin.angl) {
                            return false;
                        }
                    } else if (UCom.containsNumbJust(rec.getStr(TEXT), elemJoin.angl) == false) { //Ограничение угла, °
                        return false;
                    }
                    break;
                case 2012: //Для Артикулов указан состав
                case 3012: //Для Артикулов указан состав 
                {
                    List<Record> elementList1 = eElement.find2(elemJoin.elem1.artiklRec().getInt(eArtikl.id));
                    boolean substr1 = false;
                    for (Record elementRec : elementList1) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr1 = true;
                            break;
                        }
                    }
                    boolean substr2 = false;
                    List<Record> elementList2 = eElement.find2(elemJoin.elem2.artiklRec().getInt(eArtikl.id));
                    for (Record elementRec : elementList2) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr1 = true;
                            break;
                        }
                    }
                    if (!(substr1 == true || substr2 == true)) {
                        return false;
                    }
                }
                break;
                case 2015:  //Ориентация Артикула1/Артикула2, ° 
                case 3015:  //Ориентация Артикула1/Артикула2, °
                case 4015:  //Ориентация Артикула1/Артикула2, °
                    if ("ps3".equals(eSetting.val(2))) { //Ориентация Артикула 1
                        List<String> list = ParamList.find(grup).dict();
                        if ("горизонтально".equals(rec.getStr(TEXT)) && (elemJoin.elem1.anglHoriz() == 0 || elemJoin.elem1.anglHoriz() == 180) == false) { //горизонтально
                            return false;
                        } else if ("вертикально".equals(rec.getStr(TEXT)) && (elemJoin.elem1.anglHoriz() == 90 || elemJoin.elem1.anglHoriz() == 270) == false) { //горизонтально
                            return false;
                        }
                    } else {
                        if (UCom.containsNumb(rec.getStr(TEXT), elemJoin.elem1.anglHoriz(), elemJoin.elem2.anglHoriz()) == false) {
                            return false;
                        }
                    }
                    break;
                case 2016:
                case 3016:
                case 4016:
                    if ("ps3".equals(eSetting.val(2))) { //Ориентация Артикула 2 
                        List<String> list = ParamList.find(grup).dict();
                        if ("горизонтально".equals(rec.getStr(TEXT)) && (elemJoin.elem2.anglHoriz() == 0 || elemJoin.elem2.anglHoriz() == 180) == false) { //горизонтально
                            return false;
                        } else if ("вертикально".equals(rec.getStr(TEXT)) && (elemJoin.elem2.anglHoriz() == 90 || elemJoin.elem2.anglHoriz() == 270) == false) { //горизонтально
                            return false;
                        }
                    }
                    break;
                case 2020:  //Ограничение угла, °
                case 3020:  //Ограничение угла, °
                    if (UCom.containsNumbJust(rec.getStr(TEXT), elemJoin.angl) == false) { //Ограничение угла, °
                        return false;
                    }
                    break;
                case 2021: //Точный угол, °
                case 3021: //Точный угол, °
                case 4031: //Точный угол, °
                    if ("ps3".equals(eSetting.val(2))) {
                        if (rec.getDbl(TEXT) != elemJoin.angl) {
                            return false;
                        }
                    }
                    break;
                case 2022: //Исключить угол, °
                case 3022: //Исключить угол, °
                case 4032: //Исключить угол, °
                    if ("ps3".equals(eSetting.val(2))) {
                        if (rec.getDbl(TEXT) == elemJoin.angl) {
                            return false;
                        }
                    }
                    break;
                case 2030:  //Припуск Артикула1/Артикула2, мм 
                case 3050:  //Припуск Артикула1/Артикула2, мм  
                case 4050:
                    listenerList.add(() -> {
                        if ("ps3".equals(eSetting.val(2))) { //Припуск Артикула 1, мм
                            elemJoin.elem1.spcRec().width += rec.getDbl(TEXT);
                        } else {
                            String strTxt = rec.getStr(TEXT);
                            char normal = strTxt.charAt(strTxt.length() - 1);
                            if (normal == '@') {
                                strTxt = strTxt.substring(0, strTxt.length() - 1);
                            }
                            String arr[] = strTxt.split("/");
                            elemJoin.elem1.spcRec().width += UCom.getDbl(arr[0]);
                            elemJoin.elem2.spcRec().width += UCom.getDbl(arr[1]);
                        }
                    });
                    break;
                case 2040:
                case 3060:
                    listenerList.add(() -> {
                        if ("ps3".equals(eSetting.val(2))) { //Припуск Артикула 2, мм
                            elemJoin.elem2.spcRec().width += rec.getDbl(TEXT);
                        }
                    });
                    break;
                case 2055:  //Продолжение общей арки 
                    message(rec.getInt(GRUP));
                    break;
                case 2061:  //Отступ для Артикула1/Артикула2, мм 
                    message(rec.getInt(GRUP));
                    break;
                case 2064: //Поправка для состава Арт.1/Арт.2, мм 
                case 3064: //Поправка для состава Арт.1/Арт.2 , мм 
                    listenerList.add(() -> {
                        String[] arr = rec.getStr(TEXT).replace(",", ".").split("/");
                        elemJoin.elem1.spcRec().width += UCom.getDbl(arr[0]);
                        elemJoin.elem2.spcRec().width += UCom.getDbl(arr[1]);
                    });
                    break;
                case 2066:  //Расчет углов реза профилей 
                    message(rec.getInt(GRUP));
                    break;
                case 2097:  //Трудозатраты по длине 
                    message(rec.getInt(GRUP));
                    break;
                case 3002:  //Вид L-образного варианта 
                    if (elemJoin.vid == 0 && "Простое L-обр".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    } else if (elemJoin.vid == 1 && "Крестовое †-обр".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 4002:  //Вид Т-образного варианта    
                    if (elemJoin.vid == 0 && "Простое Т-обр.".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    } else if (elemJoin.vid == 1 && "Крестовое †-обр".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    } else if (elemJoin.vid == 2 && "Сложное Y-обр".equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                    break;
                case 3030:  //Усечение Артикула1/Артикула2, мм
                case 3031:  //Усечение Артикула1/Артикула2, мм 
                    listenerList.add(() -> {
                        if ("ps3".equals(eSetting.val(2))) { //Усечение Артикула 1, мм
                            IElem5e el9 = winc.listElem.find(5.4f);
                            elemJoin.elem1.spcRec().width -= rec.getDbl(TEXT);

                        } else {
                            String[] arr = rec.getStr(TEXT).replace(",", ".").split("/");
                            elemJoin.elem1.spcRec().width -= UCom.getDbl(arr[0]);
                            elemJoin.elem2.spcRec().width -= UCom.getDbl(arr[1]);
                        }
                    });
                    break;
                case 3040:
                    listenerList.add(() -> {
                        if ("ps3".equals(eSetting.val(2))) { //Усечение Артикула 2, мм
                            elemJoin.elem2.spcRec().width -= rec.getDbl(TEXT);
                        }
                    });
                    break;
                case 3045:  //Расстояние от уровня деления, мм 
                case 4045:  //Расстояние от уровня деления, мм 
                    message(rec.getInt(GRUP));
                    break;
                case 3083:  //Проходит уровень деления 
                case 4083:  //Проходит уровень деления 
                    message(rec.getInt(GRUP));
                    break;
                case 3088:  //Вариант соединения для стойки 
                    message(rec.getInt(GRUP));
                    break;
                case 3097:  //Трудозатраты по длине 
                case 4097:  //Трудозатраты по длине 
                    message(rec.getInt(GRUP));
                    break;
                case 4018: //От ручки не менее, мм 
                {
                    IStvorka stv = (IStvorka) elemJoin.elem1.owner();
                    IElem5e imp = elemJoin.elem1;
                    if (Math.abs(imp.y2() - stv.handleHeight()) < rec.getDbl(TEXT)) {
                        return false;
                    }
                }
                break;
                case 4030:  //Угол максимальный, °                      
                    if ("ps3".equals(eSetting.val(2))) {
                        if (elemJoin.angl > rec.getDbl(TEXT)) {
                            return false;
                        }
                    }
                    break;
                case 4040:  //Размер от оси профиля, мм.
                case 4044:  //Размер от края пакета, мм 
                    listenerList.add(() -> {
                        elemJoin.elem1.spcRec().width -= rec.getDbl(TEXT);
                    });
                    break;
                case 4046:  //Длина Артикула 1, мм 
                    listenerList.add(() -> {
                        elemJoin.elem1.spcRec().width += rec.getDbl(TEXT);
                    });
                    break;
                case 4061:  //Максимальный размер шва, мм 
                    message(rec.getInt(GRUP));
                    break;
                case 4064:  //Поправка для состава, мм 
                    message(rec.getInt(GRUP));
                    break;
                case 4090:  //Зеркальный вариант соединения
                    message(rec.getInt(GRUP));
                    break;
                case 4800:  //Код обработки 
                    message(rec.getInt(GRUP));
                    break;
                case 4801:  //Доп.обработки
                    message(rec.getInt(GRUP));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "Код " + grup + "  не обработан!!!";
            }
        } catch (Exception e) {
            System.err.println("Ошибка:JoiningVar.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }

    public boolean check(ElemJoining elemJoin, Record rec) {
        return check(elemJoin, rec, null);
    }
}
