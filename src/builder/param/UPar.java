package builder.param;

import builder.Wincalc;
import builder.model.AreaStvorka;
import builder.model.ElemSimple;
import builder.model.UGeo;
import common.UCom;
import dataset.Record;
import domain.eArtikl;
import domain.eElement;
import domain.eFurniture;
import domain.eSetting;
import domain.eSysfurn;
import domain.eSysprof;
import domain.eSystree;
import enums.Layout;
import enums.Type;
import enums.TypeOpen1;
import java.util.List;
import org.locationtech.jts.geom.LineSegment;

/**
 * Участвует в допустимости элемента в конструкции через параметр
 */
class UPar {

    //Толщина внешнего/внутреннего заполнения, мм
    static List<ElemSimple> getGlassDepth(ElemSimple elem5e) {
        ElemSimple glass1 = null, glass2 = null;
        //Цыкл по списку элементов
        for (ElemSimple el : elem5e.winc.listElem) {
            if (el.type == Type.GLASS) {

                if (elem5e.layout() == Layout.VERT) {
                    if (UGeo.isInRing(elem5e.x1() - 200, elem5e.y1() + elem5e.height() / 2, el.area)) {
                        glass1 = el;
                    }
                    if (UGeo.isInRing(elem5e.x2() + 200, elem5e.y1() + elem5e.height() / 2, el.area)) {
                        glass2 = el;
                    }
                }
                if (elem5e.layout() == Layout.HORIZ) {
                    if (UGeo.isInRing(elem5e.y1() - 200, elem5e.x1() + elem5e.width() / 2, el.area)) {
                        glass1 = el;
                    }
                    if (UGeo.isInRing(elem5e.y2() + 200, elem5e.x1() + elem5e.width() / 2, el.area)) {
                        glass2 = el;
                    }
                }
            }
        }
        return List.of((ElemSimple) glass1, (ElemSimple) glass2);
    }

    //Тип проема 
    static boolean is_13003_14005_15005_37008(String txt, ElemSimple elem5e) {
        if ("глухой".equals(txt) == true && elem5e.owner.type == Type.STVORKA == true) {
            return false;
        } else if ("не глухой".equals(txt) == true && elem5e.owner.type == Type.STVORKA == false) {
            return false;
        }
        return true;
    }

    //Контейнер типа
    static boolean is_1005x6_2005x6_3005_4005_11005_12005_31050_33071_34071(String txt, ElemSimple elem5e) {

        if (UCom.containsNumbJust(txt, elem5e.type.id) == false) {
            return false;
        }
        return true;
    }

    //Для технологического кода контейнера 
    static boolean is_STRING_XX000(String txt, ElemSimple elem5e) {
        Record sysprofRec = elem5e.sysprofRec;
        if (elem5e.type == Type.GLASS) {
            sysprofRec = elem5e.owner.frames.get(Layout.BOTT).sysprofRec;
        }
        Record artiklRecAn = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false);
        if (artiklRecAn.get(eArtikl.tech_code) == null) {
            return false;
        }
        String[] strList = txt.split(";");
        String[] strList2 = artiklRecAn.getStr(eArtikl.tech_code).split(";");
        boolean ret2 = false;
        for (String str : strList) {
            for (String str2 : strList2) {
                if (str.equals("*")) {
                    ret2 = true;
                } else if (str.equals(str2)) {
                    ret2 = true;
                }
            }
        }
        return ret2;
//        if (ret2 == false) {
//            return false;
//        }
//        return true;
    }

    //Если признак системы конструкции
    static boolean is_11095_12095_31095_33095_34095_37095_38095_39095_40095(String txt, int nuni) {
        Record systreefRec = eSystree.find(nuni);
        String[] arr = txt.split(";");
        List<String> arrList = List.of(arr);
        boolean ret = false;
        for (String str : arrList) {
            if (systreefRec.getInt(eSystree.types) == UCom.getInt(str) == true) {
                ret = true;
            }
        }
        if (ret == false) {
            return false;
        }
        return true;
    }

    //Если номер стороны в контуре
    static boolean is_INT_33066_34066(String txt, ElemSimple elem5e) {
        if ("1".equals(txt) == true && Layout.BOTT != elem5e.layout()) {
            return false;
        } else if ("2".equals(txt) == true && Layout.RIGHT != elem5e.layout()) {
            return false;
        } else if ("3".equals(txt) == true && Layout.TOP != elem5e.layout()) {
            return false;
        } else if ("4".equals(txt) == true && Layout.LEFT != elem5e.layout()) {
            return false;
        }
        return true;
    }

    //Перспектива
    static boolean is_13081_13082_13086_13087(ElemSimple elem5e, String txt) {
        return true;
    }

    //Эффективное заполнение изделия, мм 
    static boolean is_1008_11008_12008_14008_15008_31008_34008_40008(String txt, Wincalc winc) {
        double depth = 0;
        for (ElemSimple elem : winc.listElem) {
            if (elem.type == Type.GLASS) {
                depth = (elem.artiklRecAn.getDbl(eArtikl.depth) > depth) ? elem.artiklRecAn.getDbl(eArtikl.depth) : depth;
            }
        }
        if (UCom.containsNumbJust(txt, depth) == false) {
            return false;
        }
        return true;
    }

    //Номер стороны 
    static boolean is_38010_39002(ElemSimple elem5e, String txt) {
        if (elem5e.anglHoriz() == 0 && "1".equals(txt) == false) {
            return false;
        } else if (elem5e.anglHoriz() == 90 && "2".equals(txt) == false) {
            return false;
        } else if (elem5e.anglHoriz() == 180 && "3".equals(txt) == false) {
            return false;
        } else if (elem5e.anglHoriz() == 270 && "4".equals(txt) == false) {
            return false;
        }
        return true;
    }

    //Название фурнитуры содержит 
    static boolean is_31037_38037_39037_40037(ElemSimple elem5e, String txt) {
        if (Type.STVORKA == elem5e.owner.type) {
            AreaStvorka stv = (AreaStvorka) elem5e.owner;
            String name = eFurniture.find(stv.sysfurnRec.getInt(eSysfurn.furniture_id)).getStr(eFurniture.name);
            if ((name.equals(txt)) == false) {
                return false;
            }
        } else {
            return false; //если это не створка, то и название нет  
        }
        return true;
    }

    //Для типа открывания
    static boolean is_1039_38039_39039(ElemSimple elem5e, String txt) {
        if (elem5e.owner.type == Type.STVORKA) {
            AreaStvorka stv = (AreaStvorka) elem5e.owner;
            if (!"фрамуга".equals(txt) && stv.typeOpen == TypeOpen1.UPPER) { //фрамуга
                return false;
            } else if (!"поворотное".equals(txt) && (stv.typeOpen == TypeOpen1.LEFT || stv.typeOpen == TypeOpen1.RIGH)) { //поворотное
                return false;
            } else if (!"поворотно-откидное".equals(txt) && (stv.typeOpen == TypeOpen1.LEFTUP || stv.typeOpen == TypeOpen1.RIGHUP)) { //поворотно-откидное
                return false;
            } else if (!"раздвижное".equals(txt) && (stv.typeOpen == TypeOpen1.LEFMOV || stv.typeOpen == TypeOpen1.RIGMOV)) { //раздвижное
                return false;
            }
        }
        return true;
    }

    //Внешнее соединение
    static boolean is_31010_4010_11009_12009(String txt) {

        return true;
    }

    //Если признак состава 
    static boolean is_11001_11002_12001_12002_13001_14001_15001_33001_34001(String txt, ElemSimple elem5e) {
        Record record = eElement.query().stream().filter(rec
                -> elem5e.artiklRecAn.getInt(eArtikl.id) == rec.getInt(eElement.artikl_id)
                && txt.equals(rec.get(eElement.signset))).findFirst().orElse(null);
        if (record == null) {
            return false;
        }
        return true;
    }

    //Код системы содержит строку 
    static boolean is_13017_14017_24017_25017_31017_33017_34017_37017_38017(String txt, Wincalc winc) {
        Record systreeRec = eSystree.find(winc.nuni);
        String[] s = txt.split("/");
        String s2 = (s.length == 1) ? s[0] : s[1];
        if (systreeRec.getStr(eSystree.pref).contains(s2) == false) {
            return false;
        }
        return true;
    }

    static boolean is_21010_21011_21012_21013(String txt, ElemSimple elem5e) {
        String[] arr = txt.split("-");
        if (arr.length == 1) { //Минимальная длина, мм
            if (UCom.getInt(txt) < elem5e.length()) {
                return false;
            }
        } else {
            if (UCom.getInt(arr[0]) > elem5e.length() || UCom.getInt(arr[1]) < elem5e.length()) {
                return false;
            }
        }
        return true;
    }
}
