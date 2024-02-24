package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eFurnpar1;
import domain.eFurnside1;
import domain.eSetting;
import java.util.List;
import builder.Wincalc;
import builder.model.AreaStvorka;
import builder.model.ElemSimple;
import common.UCom;
import domain.eSystree;
import enums.LayoutKnob;
import enums.Type;

//Фурнитура
public class FurnitureVar extends Par5s {

    public FurnitureVar(Wincalc winc) {
        super(winc);
    }

    public boolean filter(ElemSimple elem5e, Record furnside1Rec) {

        List<Record> paramList = eFurnpar1.find(furnside1Rec.getInt(eFurnside1.id));
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //Цикл по параметрам фурнитуры
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

                case 21001:  //Форма контура 
                    //"Прямоугольное", "Не прямоугольное", "Не арочное", "Арочное" (Type.AREA - глухарь)
                    if ("прямоугольная".equals(rec.getStr(TEXT)) && Type.RECTANGL.equals(winc.root.type) == false
                            && Type.AREA.equals(winc.root.type) == false && Type.STVORKA.equals(winc.root.type) == false) {
                        return false;
                    } else if ("трапециевидная".equals(rec.getStr(TEXT)) && Type.TRAPEZE.equals(winc.root.type) == false) {
                        return false;
                    } else if ("арочная".equals(rec.getStr(TEXT)) && Type.ARCH.equals(winc.root.type) == false) {
                        return false;
                    } else if ("не арочная".equals(rec.getStr(TEXT)) && Type.ARCH.equals(winc.root.type) == true) {
                        return false;
                    }
                    break;
                case 21004: //Артикул створки 
                {
                    ElemSimple stv = winc.listElem.filter(Type.STVORKA_SIDE).get(0);
                    if (stv.artiklRecAn.getStr(eArtikl.code).equals(rec.getStr(TEXT)) == false) {
                        return false;
                    }
                }
                break;
                case 21005: //Артикул заполнения по умолчанию 
                {
                    Record sysreeRec = eSystree.find(winc.nuni); //по умолчанию стеклопакет
                    if (rec.getStr(TEXT).equals(sysreeRec.getStr(eSystree.glas)) == false) {
                        return false;
                    }
                }
                break;
                case 21010: //Ограничение длины стороны, мм 
                    if (UPar.is_21010_21011_21012_21013(rec.getStr(TEXT), elem5e) == false) {
                        return false;
                    }
                    break;
                case 21011: //Ограничение длины ручка константа, мм 
                {
                    AreaStvorka stv = (AreaStvorka) elem5e.owner;
                    if (stv.knobLayout == LayoutKnob.CONST) {
                        if (UPar.is_21010_21011_21012_21013(rec.getStr(TEXT), elem5e) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 21012: //Ограничение длины ручка вариацион, мм 
                {
                    AreaStvorka stv = (AreaStvorka) elem5e.owner;
                    if (stv.knobLayout == LayoutKnob.VAR) {
                        if (UPar.is_21010_21011_21012_21013(rec.getStr(TEXT), elem5e) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 21013: //Ограничение длины ручка по середине, мм 
                {
                    AreaStvorka stv = (AreaStvorka) elem5e.owner;
                    if (stv.knobLayout == LayoutKnob.MIDL) {
                        if (UPar.is_21010_21011_21012_21013(rec.getStr(TEXT), elem5e) == false) {
                            return false;
                        }
                    }
                }
                break;
                case 21016:  //Допустимое соотношение габаритов б/м) 
                    double max = (elem5e.owner.width() > elem5e.owner.height()) ? elem5e.owner.width() : elem5e.owner.height();
                    double min = (elem5e.owner.width() > elem5e.owner.height()) ? elem5e.owner.height() : elem5e.owner.width();
                    if (UCom.containsNumbJust(rec.getStr(TEXT), max / min) == false) {
                        return false;
                    }
                    break;
                case 21037: //Диапазон высоты вариационной ручки, мм 
                {
                    AreaStvorka stv = (AreaStvorka) elem5e.owner;
                    if (stv.knobLayout == LayoutKnob.VAR) {
                        String[] arr = rec.getStr(TEXT).split("-");
                        if (UCom.getInt(arr[0]) > stv.knobHeight || UCom.getInt(arr[1]) < stv.knobHeight) {
                            return false;
                        }
                    }
                }
                break;
                case 21040:  //Ограничение угла, ° или Угол максимальный, ° для ps3 
                    if (UCom.containsNumbJust(rec.getStr(TEXT), elem5e.anglHoriz()) == false) {
                        return false;
                    }
                    break;
                case 21050:  //Ориентация стороны, ° 
                    if (UCom.containsNumbJust(rec.getStr(TEXT), elem5e.anglHoriz()) == false) {
                        return false;
                    }
                    break;
                case 21085:  //Надпись на эскизе 
                    elem5e.spcRec.mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 21088:  //Уравнивание складных створок 
                    message(rec.getInt(GRUP));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "Код " + grup + "  не обработан!!!";
            }
        } catch (Exception e) {
            System.err.println("Ошибка: param.FurnitureVar.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
