package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eFurndet;
import domain.eFurniture;
import domain.eFurnside1;
import domain.eFurnside2;
import domain.eSysfurn;
import enums.Layout;
import java.util.List;
import builder.Wincalc;
import builder.param.FurnitureDet;
import builder.param.FurnitureVar;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.ElemSimple;
import common.UCom;
import dataset.Query;
import enums.Type;
import java.util.ArrayList;
import java.util.HashMap;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import org.locationtech.jts.geom.Envelope;

/**
 * Фурнитура
 */
public class SpcFurniture extends Cal5e {

    private FurnitureVar furnitureVar = null;
    private FurnitureDet furnitureDet = null;
    private final List list = List.of(9, 11, 12); //замок, ручка, петля 
    private boolean max_size_message = true;

    public SpcFurniture(Wincalc winc) {
        super(winc);
        furnitureVar = new FurnitureVar(winc);
        furnitureDet = new FurnitureDet(winc);
    }

    public SpcFurniture(Wincalc winc, boolean shortPass) {
        super(winc);
        furnitureVar = new FurnitureVar(winc);
        furnitureDet = new FurnitureDet(winc);
        this.shortPass = shortPass;
    }

    @Override
    public void calc() {
        super.calc();
        ArrayList<AreaSimple> stvorkaList = winc.listArea.filter(Type.STVORKA);
        try {
            //Подбор фурнитуры по параметрам
            List<Record> sysfurnList = eSysfurn.find(winc.nuni); //список фурнитур в системе
            if (sysfurnList.isEmpty() == false) {
                Record sysfurnRec = sysfurnList.get(0); //значение по умолчанию, первая SYSFURN в списке системы

                //Цикл по створкам      
                for (AreaSimple areaStv : stvorkaList) {
                    AreaStvorka stv = (AreaStvorka) areaStv;

                    //Найдём из списка сист.фурн. фурнитуру которая установлена в створку                 
                    sysfurnRec = sysfurnList.stream().filter(rec -> rec.getInt(eSysfurn.id) == stv.sysfurnRec.getInt(eSysfurn.id)).findFirst().orElse(sysfurnRec);
                    Record furnityreRec = eFurniture.find(sysfurnRec.getInt(eSysfurn.furniture_id));

                    //Проверка с предупреждением на max высоту, ширину, периметр
                    Envelope env = stv.area.getEnvelopeInternal();
                    double stv_width = env.getWidth();
                    double stv_height = env.getHeight();
                    boolean p2_max = (furnityreRec.getDbl(eFurniture.max_p2) < (stv_width * 2 + stv_height * 2) / 2);
                    if (p2_max || furnityreRec.getDbl(eFurniture.max_height) < stv_height
                            || furnityreRec.getDbl(eFurniture.max_width) < stv_width) {
                        if (max_size_message == true) {
                            JOptionPane.showMessageDialog(null, "Размер створки превышает максимальный размер фурнитуры.", "ВНИМАНИЕ!", 1);
                        }
                        max_size_message = false;
                    }
                    variant(stv, furnityreRec, 1); //основная фурнитура
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Furniture.calc() " + e);
        } finally {
            Query.conf = conf;
        }
    }

    protected void variant(AreaSimple areaStv, Record furnitureRec, int count) {
        try {
            List<Record> furndetList1 = eFurndet.find(furnitureRec.getInt(eFurniture.id)); //детализация первый уровень
            List<Record> furndetList2 = furndetList1.stream()
                    .filter(rec -> rec.getInt(eFurndet.id) != rec.getInt(eFurndet.furndet_pk)).collect(toList()); //детализация второй уровень

            //Цикл по описанию сторон фурнитуры
            List<Record> furnsidetList = eFurnside1.find(furnitureRec.getInt(eFurniture.id)); //список описания сторон
            for (Record furnside1Rec : furnsidetList) {
                ElemSimple elemFrame = areaStv.frames.get((Layout) Layout.ANY.find(furnside1Rec.getInt(eFurnside1.side_num)));

                //ФИЛЬТР вариантов с учётом стороны
                if (furnitureVar.filter(elemFrame, furnside1Rec) == false) {
                    return;
                }
            }

            //Цикл по детализации (первый уровень)        
            for (Record furndetRec1 : furndetList1) {
                if (furndetRec1.getInt(eFurndet.furndet_pk) == furndetRec1.getInt(eFurndet.id)) {
                    if (detail(areaStv, furndetRec1, count) == true) {

                        //Цикл по детализации (второй уровень)
                        for (Record furndetRec2 : furndetList2) {
                            if (furndetRec2.getInt(eFurndet.furndet_pk) == furndetRec1.getInt(eFurndet.pk)) {
                                if (detail(areaStv, furndetRec2, count) == true) {

                                    //Цикл по детализации (третий уровень)
                                    for (Record furndetRec3 : furndetList2) {
                                        if (furndetRec3.getInt(eFurndet.furndet_pk) == furndetRec2.getInt(eFurndet.pk)) {
                                            detail(areaStv, furndetRec3, count);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Furniture.variant() " + e);
        }
    }

    protected boolean detail(AreaSimple areaStv, Record furndetRec, int countKit) {
        try {
            Record artiklRec = eArtikl.find(furndetRec.getInt(eFurndet.artikl_id), false);
            HashMap<Integer, String> mapParam = new HashMap<Integer, String>(); //тут накапливаются параметры element и specific

            //Сделано для убыстрения поиска ручки, 
            //подвеса, замка при конструировании окна
            if (shortPass == true) {
                if (furndetRec.getInt(eFurndet.furndet_pk) == furndetRec.getInt(eFurndet.id) && furndetRec.get(eFurndet.furniture_id2) == null) {
                    if ((artiklRec.getInt(eArtikl.level1) == 2 && list.contains(artiklRec.getInt(eArtikl.level2)) == false)
                            || artiklRec.getInt(eArtikl.level1) != 2) { //т.к. ручки, подвеса, замка на этом уровне нет
                        return false;
                    }
                }
            }
            furnitureDet.detailRec = furndetRec; //для тестирования

            //ФИЛЬТР параметров детализации 
            if (furnitureDet.filter(mapParam, areaStv, furndetRec) == false) {
                return false;
            }

            //Проверка по ограничению сторон
            //Цикл по ограничению сторон фурнитуры
            List<Record> furnside2List = eFurnside2.find(furndetRec.getInt(eFurndet.id));
            for (Record furnside2Rec : furnside2List) {
                ElemSimple el;
                double length = 0;
                int side = furnside2Rec.getInt(eFurnside2.side_num);

                if (side < 0) {
                    String txt = (furnitureDet.mapParamTmp.getOrDefault(24038, null) == null)
                            ? furnitureDet.mapParamTmp.getOrDefault(25038, "*/*")
                            : furnitureDet.mapParamTmp.getOrDefault(24038, "*/*");
                    String[] par = txt.split("/");
                    if (side == -1) {
                        side = (par[0].equals("*") == true) ? 99 : Integer.valueOf(par[0]);
                    } else if (side == -2) {
                        side = (par[1].equals("*") == true) ? 99 : Integer.valueOf(par[1]);
                    }
                }
                if (side == 1) {
                    el = areaStv.frames.get(Layout.BOTT);
                    length = el.spcRec.width - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                } else if (side == 2) {
                    el = areaStv.frames.get(Layout.RIGHT);
                    length = el.spcRec.width - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                } else if (side == 3) {
                    el = areaStv.frames.get(Layout.TOP);
                    length = el.spcRec.width - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                } else if (side == 4) {
                    el = areaStv.frames.get(Layout.LEFT);
                    length = el.spcRec.width - 2 * el.artiklRec.getDbl(eArtikl.size_falz);
                }
                if (length >= furnside2Rec.getDbl(eFurnside2.len_max) || (length < furnside2Rec.getDbl(eFurnside2.len_min))) {
                    
                    return false; //не прошли ограничение сторон
                }
            }

            //Не НАБОР (элемент из мат. ценности)
            if (furndetRec.get(eFurndet.furniture_id2) == null) {
                if (artiklRec.getInt(eArtikl.id) != -1) { //артикул есть
                    
                    ElemSimple sideStv = determOfSide(mapParam, areaStv);
                    SpcRecord spcAdd = new SpcRecord("ФУРН", furndetRec, artiklRec, sideStv, mapParam);

                    //Ловим ручку, подвес, замок и 
                    //присваиваем знач. в створку
                    setPropertyStv(areaStv, spcAdd);

                    if (UColor.colorFromProduct(spcAdd)) { //подбор по цвету

                        //Добавим спецификацию в элемент
                        if (shortPass == false) {
                            spcAdd.count = UCom.getDbl(spcAdd.getParam(spcAdd.count, 24030));
                            spcAdd.count = spcAdd.count * countKit; //умножаю на количество комплектов
                            sideStv.addSpecific(spcAdd);
                        }
                    }
                }

                //Это НАБОР 
            } else {
                int countKi2 = (mapParam.get(24030) == null) ? 1 : Integer.valueOf((mapParam.get(24030)));
                Record furnitureRec2 = eFurniture.find(furndetRec.getInt(eFurndet.furniture_id2));

                variant(areaStv, furnitureRec2, countKi2); //рекурсия обработки наборов
            }
            return true;

        } catch (Exception e) {
            System.err.println("Ошибка:Furniture.detail() " + e);
            return false;
        }
    }

    //Ловим ручку, подвес, замок и 
    //присваиваем знач. в створку    
    private void setPropertyStv(AreaSimple areaStv, SpcRecord spcAdd) {
        AreaStvorka stv = (AreaStvorka) areaStv;

        if (spcAdd.artiklRec.getInt(eArtikl.level1) == 2) {
            boolean add_specific = true;
            //Ручка
            if (spcAdd.artiklRec.getInt(eArtikl.level2) == 11) {
                if (UColor.colorFromProduct(spcAdd) == true) { //подбор по цвету

                    if (stv.knobRec.getInt(eArtikl.id) == -3) {
                        stv.knobRec = spcAdd.artiklRec;
                        add_specific = true;
                    } else {
                        add_specific = (stv.knobRec.getInt(eArtikl.id) == spcAdd.artiklRec.getInt(eArtikl.id));
                    }
                    if (add_specific == true && stv.knobColor == -3) {
                        stv.knobColor = spcAdd.colorID1;
                    }
                }

                //Подвес
            } else if (spcAdd.artiklRec.getInt(eArtikl.level2) == 12) {
                if (UColor.colorFromProduct(spcAdd) == true) { //подбор по цвету

                    if (stv.loopRec.getInt(eArtikl.id) == -3) {
                        stv.loopRec = spcAdd.artiklRec;
                        add_specific = true;
                    } else {
                        add_specific = (stv.loopRec.getInt(eArtikl.id) == spcAdd.artiklRec.getInt(eArtikl.id));
                    }
                    if (add_specific == true && stv.loopColor == -3) {
                        stv.loopColor = spcAdd.colorID1;
                    }
                }

                //Замок  
            } else if (spcAdd.artiklRec.getInt(eArtikl.level2) == 9) {
                if (UColor.colorFromProduct(spcAdd) == true) { //подбор по цвету

                    if (stv.lockRec.getInt(eArtikl.id) == -3) {
                        stv.lockRec = spcAdd.artiklRec;
                        add_specific = true;
                    } else {
                        add_specific = (stv.lockRec.getInt(eArtikl.id) == spcAdd.artiklRec.getInt(eArtikl.id));
                    }
                    if (add_specific == true && stv.lockColor == -3) {
                        stv.lockColor = spcAdd.colorID1;
                    }
                }
            }
        }
    }

    public ElemSimple determOfSide(HashMap<Integer, String> mapParam, AreaSimple area5e) {

        //Через параметр
        if ("1".equals(mapParam.get(25010))) {
            return area5e.frames.get(Layout.BOTT);
        } else if ("2".equals(mapParam.get(25010))) {
            return area5e.frames.get(Layout.RIGHT);
        } else if ("3".equals(mapParam.get(25010))) {
            return area5e.frames.get(Layout.TOP);
        } else if ("4".equals(mapParam.get(25010))) {
            return area5e.frames.get(Layout.LEFT);
        } else {
            //Там где крепится ручка
            return determOfSide(area5e);
        }
    }

    //Там где крепится ручка
    public static ElemSimple determOfSide(AreaSimple area5e) {
        if (area5e instanceof AreaStvorka) {
            int id = ((AreaStvorka) area5e).typeOpen.id;
            if (List.of(1, 3, 11).contains(id)) {
                return area5e.frames.get(Layout.LEFT);
            } else if (List.of(2, 4, 12).contains(id)) {
                return area5e.frames.get(Layout.RIGHT);
            } else {
                return area5e.frames.get(Layout.BOTT);
            }
        }
        return area5e.frames.stream().findFirst().get();  //первая попавшаяся        
    }
}
