package builder.model;

import builder.making.Specific;
import domain.eArtikl;
import builder.param.ParamList;
import enums.UseUnit;
import common.UCom;
import domain.eSetting;
import enums.Layout;
import java.util.List;

/**
 * Участвует в измении свойств элемента конструкции через параметр
 */
public class UPar {

    //Укорочение мм от высоты ручки 
    public static double to_25013(Specific spcRec, Specific spcAdd) {

        String ps = spcAdd.getParam("null", 25013); //Укорочение от
        if (!"null".equals(ps)) {
            List<String> list = ParamList.find(25013).dict();  //[длины стороны, высоты ручки, сторона выс-ручки, половины стороны]             
            double dx = UCom.getDbl(spcAdd.getParam(0, 25030)); //"Укорочение, мм"

            if (list.get(0).equals(ps)) {
                return spcRec.width - dx;

            } else if (list.get(1).equals(ps)) {
                AreaStvorka stv = (AreaStvorka) spcAdd.elem5e.owner;
                return stv.knobHeight - dx;

            } else if (list.get(2).equals(ps)) {
                AreaStvorka stv = (AreaStvorka) spcAdd.elem5e.owner;
                return spcRec.width - stv.knobHeight - dx;

            } else if (list.get(3).equals(ps)) {
                return spcRec.width / 2 - dx;
            }
        }
        return spcAdd.elem5e.length();
    }

    //Расчёт количества ед. с шагом
    public static double to_14050_24050_33050_38050(Specific spcRec, Specific spcAdd) {

        int step = Integer.valueOf(spcAdd.getParam(-1, 14050, 24050, 33050, 38050)); //Шаг, мм
        if (step != -1) {
            double width_begin = UCom.getDbl(spcAdd.getParam(0, 14040, 24040, 33040, 38040)); //Порог расчета, мм
            int count_step = Integer.valueOf(spcAdd.getParam(1, 14060, 24060, 33060, 38060)); //"Количество на шаг"
            double width_next = 0;
            if ("null".equals(spcAdd.getParam("null", 38004, 39005))) {
                width_next = spcRec.elem5e.length() - width_begin;

            } else if ("по периметру".equals(spcAdd.getParam("null", 38004, 39005))) {
                width_next = (spcRec.elem5e.width() * 2 + spcRec.elem5e.height() * 2) - width_begin;

            } else if ("по площади".equals(spcAdd.getParam("null", 38004, 39005))) {
                width_next = spcRec.elem5e.width() * spcRec.elem5e.height() - width_begin;

            } else if ("длина по коробке".equals(spcAdd.getParam("null", 38004, 39005))
                    && "null".equals(spcAdd.getParam("null", 38010, 39002))) {
                double length = 0;
                if ("1".equals(spcAdd.getParam("null", 38010, 39002))) {
                    length = spcRec.elem5e.root.frames.get(Layout.BOTT).length();
                } else if ("2".equals(spcAdd.getParam("null", 38010, 39002))) {
                    length = spcRec.elem5e.root.frames.get(Layout.RIGHT).length();
                } else if ("3".equals(spcAdd.getParam("null", 38010, 39002))) {
                    length = spcRec.elem5e.root.frames.get(Layout.TOP).length();
                } else if ("4".equals(spcAdd.getParam("null", 38010, 39002))) {
                    length = spcRec.elem5e.root.frames.get(Layout.LEFT).length();
                }
                width_next = length - width_begin;
            }
            int count = (int) width_next / step;
            if (count_step == 1) {
                if (count < 1) {
                    return 1;
                }
                return (width_next % step > 0) ? ++count : count;
            } else {
                int count2 = (int) width_next / step;
                int count3 = (int) (width_next % step) / (step / count_step);
                return ((width_next % step) % (step / count_step) > 0) ? count2 * count_step + count3 + 1 : count2 * count_step + count3;
            }
        }
        return 0;
    }

    //Расчёт количества ед. с шагом
    public static double to_11050(Specific spcAdd, ElemJoining elemJoin) {

        int step = Integer.valueOf(spcAdd.getParam(-1, 11050)); //Шаг, мм
        if (step != -1) {
            double width_begin = UCom.getDbl(spcAdd.getParam(0, 11040)); //Порог расчета, мм
            int count_step = Integer.valueOf(spcAdd.getParam(1, 11060)); //"Количество на шаг"
            ElemSimple elem5e = null;
            double width_next = 0;

            if ("Да".equals(spcAdd.getParam("Нет", 11010, 12010))) {
                elem5e = elemJoin.elem1;

            } else if ("Да".equals(spcAdd.getParam("Нет", 11020, 12020))) {
                elem5e = elemJoin.elem2;

            } else {
                elem5e = elemJoin.elem1; //по умолч.
            }
            if ("большей".equals(spcAdd.getParam("", 11072, 12072))) {
                double length = (elem5e.width() > elem5e.height()) ? elem5e.width() : elem5e.height();
                width_next = length - width_begin;

            } else if ("меньшей".equals(spcAdd.getParam("", 11072, 12072))) {
                double length = (elem5e.width() < elem5e.height()) ? elem5e.width() : elem5e.height();
                width_next = length - width_begin;

            } else if ("общей".equals(spcAdd.getParam("", 11072, 12072))) {
                double length = elemJoin.elem2.width();
                width_next = length - width_begin;

            } else {
                width_next = elem5e.width() - width_begin;
            }
            int count = (int) width_next / step;
            if (count_step == 1) {
                if (count < 1) {
                    return 1;
                }
                return (width_next % step > 0) ? ++count : count;
            } else {
                int count2 = (int) width_next / step;
                int count3 = (int) (width_next % step) / (step / count_step);
                return ((width_next % step) % (step / count_step) > 0) ? count2 * count_step + count3 + 1 : count2 * count_step + count3;
            }
        }
        return 0;
    }

    //Количество ед.
    public static double to_11030_12060_14030_15040_25060_33030_34060_38030_39060(Specific spcAdd) {
        return UCom.getDbl(spcAdd.getParam(spcAdd.count,
                11030, 12060, 14030, 15040, 25060, 33030, 34060, 38030, 39060));
    }

    //Поправка, мм
    public static double to_12050_15050_34051_39020(Specific spcAdd) {
        if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) { //пог.м.
            return UCom.getDbl(spcAdd.getParam(0, 12050, 15050, 34050, 34051, 39020)); //Поправка, мм
        }
        return spcAdd.width;
    }

    //Длина, мм
    public static double to_12065_15045_25040_34070_39070(Specific spcAdd) {
        if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) { //пог.м.
            return UCom.getDbl(spcAdd.getParam(spcAdd.width, 12065, 15045, 25040, 34070, 39070)); //Длина, мм 
        }
        return spcAdd.width;
    }

    //Коэффициент, [ * коэф-т ]
    public static double to_12030_15030_25035_34030_39030(Specific spcAdd) {
        return UCom.getDbl(spcAdd.getParam("1", 12030, 15030, 25035, 34030, 39030));
    }

    //Коэффициент, [ / коэф-т ]
    public static double to_12040_15031_25036_34040_39040(Specific spcAdd) {
        return UCom.getDbl(spcAdd.getParam("1", 12040, 15031, 25036, 34040, 39040));
    }

    //Othe
    public static double to_11030_12060_14030_15040_24030_25060_33030_34060_38030_39060(Specific spcAdd) {
        return UCom.getDbl(spcAdd.getParam(spcAdd.quant1,
                11030, 12060, 14030, 15040, 24030, 25060, 33030, 34060, 38030, 39060));
    }

    //Задать Угол_реза_1/Угол_реза_2, °
    public static void to_34077_39077(Specific spcAdd) {
        if (spcAdd.getParam("-361", 34077, 39077).equals("-361") == false) {
            String[] arr = spcAdd.getParam("-1", 34077, 39077).split("/");
            if (arr[0].equals("*") == false) {
                spcAdd.anglCut0 = UCom.getDbl(arr[0]);
            }
            if (arr[1].equals("*") == false) {
                spcAdd.anglCut1 = UCom.getDbl(arr[1]);
            }
        }
    }

    //Ставить однократно
    public static double to_11070_12070_33078_34078(Specific spcAdd) {
        if ("Да".equals(spcAdd.getParam("Нет", 11070, 12070, 33078, 34078))) {
            return 1;
        } else {
            return spcAdd.count;
        }
    }

    //Углы реза
    public static void to_12075_34075_39075(ElemSimple elem5e, Specific spcAdd) {
        String txt = spcAdd.getParam("null", 12075, 34075, 39075);
        if (!"null".equals(txt)) {
            if ("по контейнерам".equals(txt)) {
                spcAdd.anglCut0 = elem5e.anglCut[0];
                spcAdd.anglCut1 = elem5e.anglCut[1];

            } else if ("установить (90° x 90°)".equals(txt)) {
                spcAdd.anglCut0 = 90;
                spcAdd.anglCut1 = 90;

            } else if ("установить (90° x 45°)".equals(txt)) {
                spcAdd.anglCut0 = 90;
                spcAdd.anglCut1 = 45;

            } else if ("установить (45° x 45°)".equals(txt)) {
                spcAdd.anglCut0 = 45;
                spcAdd.anglCut1 = 45;
            }
        }
    }

    //Высоту сделать длиной 
    public static void to_40007(Specific spcAdd) {
        if ("Да".equals(spcAdd.getParam("null", 40007))) {
            double height = spcAdd.height;
            spcAdd.height = spcAdd.width;
            spcAdd.width = height;
        }
    }

    //Округлять количество до ближайшего
    public static double to_39063(Specific spcAdd) {
        String txt = spcAdd.getParam("null", 39063);
        if (!"null".equals(txt)) {

            if ("меньшего целого числа".equals(txt)) {
                return Math.floor(spcAdd.count);

            } else if ("большего целого числа".equals(txt)) {
                return Math.ceil(spcAdd.count);

            } else if ("большего чётного числа".equals(txt)) {
                return Math.round(spcAdd.count);

            } else if ("большего нечётного числа".equals(txt)) {
                return Math.round(spcAdd.count) + 1;
            }
        }
        return spcAdd.count;
    }

    //Поправка ширины/высоты, мм 
    //Поправка на стороны четные/нечетные, мм     
    public static void to_40005_40010(Specific spcAdd) {
        if (!"null".equals(spcAdd.getParam("null", 40005))) {
            String[] arr = spcAdd.getParam("null", 40005).split("/");
            spcAdd.width = spcAdd.width + UCom.getDbl(arr[0]);
            spcAdd.height = spcAdd.height + UCom.getDbl(arr[1]);
        } else if (!"null".equals(spcAdd.getParam("null", 40010))) {
            String[] arr = spcAdd.getParam("null", 40010).split("/");
            spcAdd.height = spcAdd.height + UCom.getDbl(arr[0]);
            spcAdd.width = spcAdd.width + UCom.getDbl(arr[1]);
        }
    }
}
