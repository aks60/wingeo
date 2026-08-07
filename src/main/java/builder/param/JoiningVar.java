package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eElement;
import domain.eJoinpar1;
import domain.eJoinvar;
import domain.eSystree;
import java.util.List;
import builder.Wincalc;
import builder.model.AreaStvorka;
import builder.model.ElemJoining;
import builder.model.ElemSimple;
import builder.model.UGeo;
import common.UCom;
import common.listener.ListenerAction;
import enums.Layout;
import enums.Type;
import enums.TypeJoin;
import java.util.ArrayList;

//Соединения
public class JoiningVar extends Par5s {

    public JoiningVar(Wincalc winc) {
        super(winc);
        listenerList = new ArrayList<ListenerAction>();
    }

    public JoiningVar(Wincalc winc, boolean shortPass) {
        super(winc);
        listenerList = new ArrayList<ListenerAction>();
        this.shortPass = shortPass;
    }

    //1000 - прилегающее соединение, 2000 - угловое на ус, 3000 - угловое (левое, правое), 4000 - Т образное соединение
    public boolean filter(ElemJoining elemJoin, Record joinvarRec) {

        listenerList.clear();
        List<Record> paramList = eJoinpar1.filter(joinvarRec.getInt(eJoinvar.id));
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
                case 4005:  //Контейнер имеет тип Артикула1/Артикула2
                    if (UCom.containsNumb(rec.getStr(TEXT), elemJoin.elem1.type.id, elemJoin.elem2.type.id) == false) {
                        return false;
                    }
                    break;
                case 1008:  //Эффективное заполнение изд., мм
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 1010:  //Внешнее соединение 
                case 4010:  //Внешнее соединение   
                    if ("Да".equals(rec.getStr(TEXT))) {
                        if ((winc.root.frames.contains(elemJoin.elem1) && winc.root.frames.contains(elemJoin.elem2)) == false) {
                            return false;
                        }

                    } else if ("Нет".equals(rec.getStr(TEXT))) {
                        if ((winc.root.frames.contains(elemJoin.elem1) && winc.root.frames.contains(elemJoin.elem2)) == false) {
                            return false;
                        }
                    }
                    break;
                case 1011: //Для Артикула 1 указан состав 
                case 4011: //Для Артикула 1 указан состав     
                {
                    boolean substr = false;
                    List<Record> elementList = eElement.filter2(elemJoin.elem1.artiklRec.getInt(eArtikl.code));
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
                    List<Record> elementList = eElement.filter2(elemJoin.elem2.artiklRec.getInt(eArtikl.code));
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
                    List<Record> elementList1 = eElement.filter2(elemJoin.elem1.artiklRec.getInt(eArtikl.code));
                    boolean substr1 = false;
                    for (Record elementRec : elementList1) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr1 = true;
                            break;
                        }
                    }
                    boolean substr2 = false;
                    List<Record> elementList2 = eElement.filter2(elemJoin.elem2.artiklRec.getInt(eArtikl.code));
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
                case 1020:  //Ограничение угла к горизонту, °
                    if (UCom.containsNumbJust(rec.getStr(TEXT),
                            UGeo.anglHor(elemJoin.elem1.x1(), elemJoin.elem1.y1(), elemJoin.elem1.x2(), elemJoin.elem1.y2())) == true) {
                        return false;
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
                    if (elemJoin.elem1.type == Type.STV_SIDE) {
                        listenerList.add(() -> {
                            AreaStvorka stv = (AreaStvorka) elemJoin.elem1.owner;
                            if (elemJoin.elem1.layout() == Layout.BOT) {
                                stv.offset[0] = rec.getDbl(TEXT);
                            } else if (elemJoin.elem1.layout() == Layout.RIG) {
                                stv.offset[1] = rec.getDbl(TEXT);
                            } else if (elemJoin.elem1.layout() == Layout.TOP) {
                                stv.offset[2] = rec.getDbl(TEXT);
                            } else if (elemJoin.elem1.layout() == Layout.LEF) {
                                stv.offset[3] = rec.getDbl(TEXT);
                            }
                        });
                    }
                    break;
                case 1043: //Ограничение габарита контура, мм 
                {
                    double area = winc.width() * winc.height() / 1000000;
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
                    Record systreefRec = eSystree.find(winc.nuni);
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
                            if (elemJoin.type() != TypeJoin.ANG1) {
                                return false;
                            }
                        } else if ("правый".equals(rec.getStr(TEXT))) {
                            if (elemJoin.type() != TypeJoin.ANG2) {
                                return false;
                            }
                        }
                    }
                    break;
                case 2010:  //Угол минимальный, °
                case 3010:  //Угол минимальный, °
                case 4020:  //Ограничение угла, °
                    if (UCom.containsNumbJust(rec.getStr(TEXT), elemJoin.angleBetween()) == false) { //Ограничение угла, °
                        return false;
                    }
                    break;
                case 2012: //Для Артикулов указан состав
                case 3012: //Для Артикулов указан состав 
                {
                    List<Record> elementList1 = eElement.filter2(elemJoin.elem1.artiklRec.getInt(eArtikl.id));
                    boolean substr1 = false;
                    for (Record elementRec : elementList1) {
                        if (elementRec.getStr(eElement.name).contains(rec.getStr(TEXT))) {
                            substr1 = true;
                            break;
                        }
                    }
                    boolean substr2 = false;
                    List<Record> elementList2 = eElement.filter2(elemJoin.elem2.artiklRec.getInt(eArtikl.id));
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
                    if (UCom.containsNumb(rec.getStr(TEXT),
                            UGeo.anglHor(elemJoin.elem1.x1(), elemJoin.elem1.y1(), elemJoin.elem1.x2(), elemJoin.elem1.y2()),
                            UGeo.anglHor(elemJoin.elem2.x1(), elemJoin.elem2.y1(), elemJoin.elem2.x2(), elemJoin.elem2.y2())) == false) {
                        return false;
                    }
                    break;
                case 2020:  //Ограничение угла, °
                case 3020:  //Ограничение угла, °
                    if (UCom.containsNumbJust(rec.getStr(TEXT), elemJoin.angleBetween()) == false) { //Ограничение угла, °
                        return false;
                    }
                    break;
                case 2030:  //Припуск Артикула1/Артикула2, мм 
                case 3050:  //Припуск Артикула1/Артикула2, мм  
                case 4050:
                    listenerList.add(() -> {
                        String strTxt = rec.getStr(TEXT);
                        char normal = strTxt.charAt(strTxt.length() - 1);
                        if (normal == '@') {
                            strTxt = strTxt.substring(0, strTxt.length() - 1);
                        }
                        String arr[] = strTxt.split("/");
                        elemJoin.elem1.spcRec.width += UCom.getDbl(arr[0]);
                        elemJoin.elem2.spcRec.width += UCom.getDbl(arr[1]);
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
                        elemJoin.elem1.spcRec.width += UCom.getDbl(arr[0]);
                        elemJoin.elem2.spcRec.width += UCom.getDbl(arr[1]);
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
                        String[] arr = rec.getStr(TEXT).replace(",", ".").split("/");
                        elemJoin.elem1.spcRec.width -= UCom.getDbl(arr[0]);
                        elemJoin.elem2.spcRec.width -= UCom.getDbl(arr[1]);
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
                    AreaStvorka stv = (AreaStvorka) elemJoin.elem1.owner;
                    ElemSimple imp = elemJoin.elem1;
                    if (Math.abs(imp.y2() - stv.handHeight) < rec.getDbl(TEXT)) {
                        return false;
                    }
                }
                break;
                case 4040:  //Размер от оси профиля, мм.
                case 4044:  //Размер от края пакета, мм 
                    listenerList.add(() -> {
                        elemJoin.elem1.spcRec.width -= rec.getDbl(TEXT);
                    });
                    break;
                case 4046:  //Длина Артикула 1, мм 
                    listenerList.add(() -> {
                        elemJoin.elem1.spcRec.width += rec.getDbl(TEXT);
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
