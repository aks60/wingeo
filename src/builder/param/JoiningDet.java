package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eJoindet;
import domain.eJoinpar2;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.model1.ElemJoining;
import builder.IElem5e;
import common.UCom;
import enums.Type;
import java.util.LinkedList;

//Cоединения
public class JoiningDet extends Par5s {

    public JoiningDet(Wincalc winc) {
        super(winc);
    }

    public boolean filter(HashMap<Integer, String> mapParam, ElemJoining elemJoin, Record joindetRec) {

        List<Record> paramList = eJoinpar2.find(joindetRec.getInt(eJoindet.id));
        if (filterParamDef(paramList) == false) {
            return false;
        }
        //Цикл по параметрам соединения
        for (Record rec : paramList) {
            if (check(mapParam, elemJoin, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(HashMap<Integer, String> mapParam, ElemJoining elemJoin, Record rec) {

        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {

                case 11000: //Для технологического кода контейнера 1/2
                case 12000: //Для технологического кода контейнера 1/2 
                {
                    String[] arr = rec.getStr(TEXT).split("/");
                    if (UPar.is_STRING_XX000(arr[0], elemJoin.elem1) == false) {
                        return false;
                    }
                    if (arr.length > 1 && UPar.is_STRING_XX000(arr[1], elemJoin.elem2) == false) {
                        return false;
                    }
                }
                break;
                case 11001: //Если признак состава Арт.1 
                case 12001: //Если признак состава Арт.1 
                    if (UPar.is_11001_11002_12001_12002_13001_14001_15001_33001_34001(rec.getStr(TEXT), elemJoin.elem1) == false) {
                        return false;
                    }
                    break;
                case 11002:  //Если признак состава Арт.2 
                case 12002:  //Если признак состава Арт.2 
                    if (UPar.is_11001_11002_12001_12002_13001_14001_15001_33001_34001(rec.getStr(TEXT), elemJoin.elem2) == false) {
                        return false;
                    }
                    break;
                case 11005:  //Контейнер типа 
                case 12005:  //Контейнер типа 
                    if (UPar.is_1005x6_2005x6_3005_4005_11005_12005_31050_33071_34071(rec.getStr(TEXT), elemJoin.elem1) == false) {
                        return false;
                    }
                    break;
                case 11008:  //Эффективное заполнение изд., мм 
                case 12008:  //Эффективное заполнение изд., мм 
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 11009:  //Внешнее соединение 
                case 12009:  //Внешнее соединение                      
                    message(rec.getInt(GRUP)); //У SA всегда внутреннее
                    break;
                case 11010:  //Рассчитывать с Артикулом 1 
                case 12010:  //Рассчитывать с Артикулом 1                    
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11020:  //Рассчитывать с Артикулом 2 
                case 12020:  //Рассчитывать с Артикулом 2 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11028: //Диапазон веса заполнения, кг 
                case 12028: //Диапазон веса заполнения, кг 
                {
                    double weight = 0;
                    LinkedList<IElem5e> glassList = winc.listElem.filter(Type.GLASS);
                    for (IElem5e glass : glassList) {
                        if (glass.artiklRecAn().getDbl(eArtikl.density) > 0) {
                            weight += glass.width() * glass.height() * glass.artiklRecAn().getDbl(eArtikl.density) / 1000000;
                        }
                    }
                    if (UCom.containsNumbExp(rec.getStr(TEXT), weight) == false) {
                        return false;
                    }
                }
                break;
                case 11029:  //Расстояние узла от ручки, мм 
                    message(rec.getInt(GRUP));
                    break;
                case 11030:  //Количество 
                case 12060:  //Количество 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11040:  //Порог расчета, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11050:  //Шаг, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 12050:  //Поправка, мм    
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11060:  //Количество на шаг   
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11066:  //Если текстура профиля Арт.1 
                    if (UCom.containsColor(rec.getStr(TEXT), elemJoin.elem1.colorID1()) == false) {
                        return false;
                    }
                    break;
                case 11067:  //Коды основной текстуры изделия 
                case 12067:  //Коды основной текстуры изделия
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID1) == false) {
                        return false;
                    }
                    break;
                case 11068:  //Коды внутр. текстуры изделия 
                case 12068:  //Коды внутр. текстуры изделия 
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID2) == false) {
                        return false;
                    }
                    break;
                case 11069:  //Коды внешн. текстуры изделия
                case 12069:  //Коды внешн. текстуры изделия     
                    if (UCom.containsColor(rec.getStr(TEXT), winc.colorID3) == false) {
                        return false;
                    }
                    break;
                case 11070:  //Ставить однократно 
                case 12070:  //Ставить однократно    
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11072:  //Расчет по стороне 
                case 12072:  //Расчет по стороне 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 11095: //Если признак системы конструкции 
                case 12095: //Если признак системы конструкции 
                    if (!UPar.is_11095_12095_31095_33095_34095_37095_38095_39095_40095(rec.getStr(TEXT), winc.nuni())) {
                        return false;
                    }
                    break;
                case 12027:  //Рассчитывать для профиля 
                    if ("с уплотнителем".equals(rec.getStr(TEXT)) == true && elemJoin.elem1.artiklRec().getInt(eArtikl.with_seal) == 0) {
                        return false;
                    }
                    break;
                case 12030:  //[ * коэф-т ] 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 12063:  //Углы реза по плоскости ригеля 
                    message(rec.getInt(GRUP));
                    break;
                case 12064:  //Учёт в длине углов плоскостей 
                    message(rec.getInt(GRUP));
                    break;
                case 12065:  //Длина, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 12075:  //Углы реза 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "Код " + grup + "  не обработан!!!";
            }
        } catch (Exception e) {
            System.err.println("Ошибка:param.JoiningDet.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
