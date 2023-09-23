package builder.param;

import dataset.Record;
import domain.eArtikl;
import domain.eGlasdet;
import domain.eGlaspar2;
import domain.eSetting;
import domain.eSystree;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.model1.ElemSimple;
import common.UCom;
import enums.Layout;
import enums.Type;

//Заполнения
public class FillingDet extends Par5s {

    public FillingDet(Wincalc winc) {
        super(winc);
    }

    public boolean filter(HashMap<Integer, String> mapParam, ElemSimple elem5e, Record glasdetRec) {

        List<Record> paramList = eGlaspar2.find(glasdetRec.getInt(eGlasdet.id)); //список параметров детализации  
        if (filterParamDef(paramList) == false) {
            return false; //параметры по умолчанию
        }
        //Цикл по параметрам заполнения
        for (Record rec : paramList) {
            if (check(mapParam, elem5e, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(HashMap<Integer, String> mapParam, ElemSimple elem5e, Record rec) {

        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {
                case 14000: //Для технологического кода контейнера
                case 15000: //Для технологического кода контейнера 
                {
                    ElemSimple elem = winc.rootArea.frames().get(Layout.BOTT);
                    if (!UPar.is_STRING_XX000(rec.getStr(TEXT), elem)) {
                        return false;
                    }
                }
                break;
                case 14001: //Если признак состава 
                case 15001: //Если признак состава    
                {
                    ElemSimple elem = winc.rootArea.frames().get(Layout.BOTT);
                    if (UPar.is_11001_11002_12001_12002_13001_14001_15001_33001_34001(rec.getStr(TEXT), elem) == false) {
                        return false;
                    }
                }
                break;
                case 14005: //Тип проема 
                case 15005: //Тип проема
                    if (!UPar.is_13003_14005_15005_37008(rec.getStr(TEXT), elem5e)) {
                        return false;
                    }
                    break;
                case 14008: //Эффективное заполнение изд., мм 
                case 15008: //Эффективное заполнение изд., мм                    
                    if (UPar.is_1008_11008_12008_14008_15008_31008_34008_40008(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 14009: //Арочное заполнение 
                case 15009: //Арочное заполнение  
                    if ("Да".equals(rec.getStr(TEXT)) && elem5e.owner().type() != Type.ARCH) {
                        return false;
                    } else if ("Нет".equals(rec.getStr(TEXT)) && elem5e.owner().type() == Type.ARCH) {
                        return false;
                    }
                    break;
                case 14017: //Код системы содержит строку 
                case 15017: //Код системы содержит строку                    
                    if (UPar.is_13017_14017_24017_25017_31017_33017_34017_37017_38017(rec.getStr(TEXT), winc) == false) {
                        return false;
                    }
                    break;
                case 14030:  //Количество 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 14040:  //Порог расчета, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 14050:  //Шаг, мм 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 14060:  //Количество на шаг 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 14065:  //Ограничение угла, ° или Точный угол 
                case 15055:  //Ограничение угла, ° или Точный угол  
                    if ("ps3".equals(eSetting.val(2))) {
                        if (rec.getDbl(TEXT) != elem5e.anglHoriz()) {
                            return false;
                        }
                    } else if (UCom.containsNumbJust(rec.getStr(TEXT), elem5e.anglHoriz()) == false) {
                        return false;
                    }
                    break;
                case 14066: //Исключить угол, °
                case 15056:
                    if ("ps3".equals(eSetting.val(2))) {
                        if (rec.getDbl(TEXT) == elem5e.anglHoriz()) {
                            return false;
                        }
                    }
                    break;
                case 14067:  //Коды основной текстуры изделия 
                case 15067:  //Коды основной текстуры изделия    
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc().colorID1) == false) {
                        return false;
                    }
                    break;
                case 14068:  //Коды внутр. текстуры изделия 
                case 15068:  //Коды внутр. текстуры изделия     
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc().colorID2) == false) {
                        return false;
                    }
                    break;
                case 14069:  //Коды внешн. текстуры изделия 
                case 15069:  //Коды внешн. текстуры изделия     
                    if (UCom.containsColor(rec.getStr(TEXT), elem5e.winc().colorID3) == false) {
                        return false;
                    }
                    break;
                case 14081: //Если артикул профиля контура 
                case 15081: //Если артикул профиля контура 
                {
                    ElemSimple elem = (elem5e.owner().frames().isEmpty() == false) ? elem5e.owner().frames().get(Layout.BOTT) : elem5e.root().frames().get(Layout.BOTT);
                    if (rec.getStr(TEXT).equals(elem.artiklRecAn().getStr(eArtikl.code)) == false) {
                        return false;
                    }
                }
                break;
                case 14095: //Если признак системы конструкции 
                case 15095: //Если признак системы конструкции  
                {
                    Record systreeRec = eSystree.find(winc.nuni());
                    String[] arr = rec.getStr(TEXT).split(";");
                    List<String> arrList = List.of(arr);
                    boolean ret = false;
                    for (String str : arrList) {
                        if (systreeRec.get(eSystree.types) == Integer.valueOf(str) == true) {
                            ret = true;
                        }
                    }
                    if (ret == false) {
                        return false;
                    }
                }
                break;
                case 15010:  //Усекать нижний штапик 
                case 15011:  //Расчет реза штапика 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 15013:  //Подбор дистанционных вставок пролета 
                    message(rec.getInt(GRUP));
                    break;
                case 15027:  //Рассчитывать для профиля 
                    if ("с уплотнителем".equals(rec.getStr(TEXT)) == true && elem5e.artiklRec().getInt(eArtikl.with_seal) == 0) {
                        return false;
                    }
                    break;
                case 15030:  //[ * коэф-т ] 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 15040:  //Количество 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 15045:  //Длина, мм 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 15050:  //Поправка, мм 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                case 15051:  //Удлинение на один пог.м., мм 
                    if (elem5e.spcRec().getParam("0", 31052).equals(rec.getStr(TEXT)) == false) {
                        mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    }
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "Код " + grup + "  не обработан!!!";
            }
        } catch (Exception e) {
            System.err.println("Ошибка:param.FillingDet.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
