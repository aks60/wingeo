package builder.making;

import dataset.Record;
import domain.eArtikl;
import domain.eFurndet;
import domain.eFurniture;
import domain.eFurnside1;
import domain.eFurnside2;
import domain.eSysfurn;
import enums.Layout;
import java.util.LinkedList;
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

/**
 * Фурнитура
 */
public class FurnitureSpc extends Cal5e {

    private FurnitureVar furnitureVar = null;
    private FurnitureDet furnitureDet = null;
    private final List list = List.of(9, 11, 12);
    private boolean max_size_message = true;

    public FurnitureSpc(Wincalc winc) {
        super(winc);
        furnitureVar = new FurnitureVar(winc);
        furnitureDet = new FurnitureDet(winc);
    }

    public FurnitureSpc(Wincalc winc, boolean shortPass) {
        super(winc);
        furnitureVar = new FurnitureVar(winc);
        furnitureDet = new FurnitureDet(winc);
        this.shortPass = shortPass;
        calc();
    }

    @Override
    public void calc() {
        super.calc();
        ArrayList<AreaSimple> stvorkaList = winc.listArea.filter(Type.STVORKA);
        try {
            //Цикл по створкам      
            for (AreaSimple areaStv : stvorkaList) {

                AreaStvorka stv = (AreaStvorka) areaStv;
                //Подбор фурнитуры по параметрам
                List<Record> sysfurnList = eSysfurn.find(winc.nuni); //список фурнитур в системе профилей
                if (sysfurnList.isEmpty() == false) {
                    Record sysfurnRec = sysfurnList.get(0); //значение по умолчанию, первая SYSFURN в списке системы

                    //Теперь найдём из списка сист. фурн. фурнитуру которая в створке                 
                    sysfurnRec = sysfurnList.stream().filter(rec -> rec.getInt(eSysfurn.id) == stv.sysfurnRec.getInt(eSysfurn.id)).findFirst().orElse(sysfurnRec);
                    Record furnityreRec = eFurniture.find(sysfurnRec.getInt(eSysfurn.furniture_id));

                    //Проверка на max высоту, ширину
                    double max_width = stvorkaList.stream().max((s1, s2) -> s1.width().compareTo(s2.width())).get().width(); //сторона створки
                    double max_height = stvorkaList.stream().max((s1, s2) -> s1.height().compareTo(s2.height())).get().height(); //сторона створки
                    boolean p2_max = stvorkaList.stream().anyMatch(s -> furnityreRec.getDbl(eFurniture.max_p2) < (s.width() * 2 + s.height() * 2) / 2);
                    if (p2_max || furnityreRec.getDbl(eFurniture.max_height) < max_height || furnityreRec.getDbl(eFurniture.max_width) < max_width) {
                        if (max_size_message == true) {
                            JOptionPane.showMessageDialog(null, "Размер створки превышает максимальный размер по фурнитуре.", "ВНИМАНИЕ!", 1);
                        }
                        max_size_message = false;
                    }

                    variant(areaStv, furnityreRec, 1); //основная фурнитура
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
            List<Record> furndetList1 = eFurndet.find(furnitureRec.getInt(eFurniture.id));
            List<Record> furndetList2 = furndetList1.stream().filter(rec -> rec.getInt(eFurndet.id) != rec.getInt(eFurndet.furndet_pk)).collect(toList());
            List<Record> furnsidetList = eFurnside1.find(furnitureRec.getInt(eFurniture.id));

            //Цикл по описанию сторон фурнитуры
            for (Record furnside1Rec : furnsidetList) {
                ElemSimple elemFrame = areaStv.frames.get((Layout) Layout.ANY.find(furnside1Rec.getInt(eFurnside1.side_num)));

                //ФИЛЬТР вариантов с учётом стороны
                if (furnitureVar.filter(elemFrame, furnside1Rec) == false) {
                    return;
                }
            }

            //Цикл по детализации (уровень 1)        
            for (Record furndetRec1 : furndetList1) {
                if (furndetRec1.getInt(eFurndet.furndet_pk) == furndetRec1.getInt(eFurndet.id)) {
                    if (detail(areaStv, furndetRec1, count) == true) {

                        //Цикл по детализации (уровень 2)
                        for (Record furndetRec2 : furndetList2) {
                            if (furndetRec2.getInt(eFurndet.furndet_pk) == furndetRec1.getInt(eFurndet.pk)) {
                                if (detail(areaStv, furndetRec2, count) == true) {

                                    //Цикл по детализации (уровень 3)
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
            HashMap<Integer, String> mapParam = new HashMap<Integer, String> (); //тут накапливаются параметры element и specific

            //Сделано для убыстрения поиска ручки, подвеса, замка при конструировании окна
            if (shortPass == true) {
                if (furndetRec.getInt(eFurndet.furndet_pk) == furndetRec.getInt(eFurndet.id) && furndetRec.get(eFurndet.furniture_id2) == null) {
                    if (artiklRec.getInt(eArtikl.level1) != 2 || (artiklRec.getInt(eArtikl.level1) == 2
                            && list.contains(artiklRec.getInt(eArtikl.level2)) == false)) { //т.к. ручки, подвеса, замка на этом уровне нет
                        return false;
                    }
                }
            }

            //ФИЛЬТР детализации            
            furnitureDet.detailRec = furndetRec; //для тестирования
            if (furnitureDet.filter(mapParam, areaStv, furndetRec) == false) {
                return false; //параметры детализации
            }
            List<Record> furnside2List = eFurnside2.find(furndetRec.getInt(eFurndet.id));

            //Цикл по ограничению сторон фурнитуры
            for (Record furnside2Rec : furnside2List) {
                ElemSimple el;
                double width = 0;
                int side = furnside2Rec.getInt(eFurnside2.side_num);

                if (side < 0) {
                    String txt = (furnitureDet.mapParamTmp.getOrDefault(24038, null) == null)
                            ? furnitureDet.mapParamTmp.getOrDefault(25038, "*/*") : furnitureDet.mapParamTmp.getOrDefault(24038, "*/*");
                    String[] par = txt.split("/");
                    if (side == -1) {
                        side = (par[0].equals("*") == true) ? 99 : Integer.valueOf(par[0]);
                    } else if (side == -2) {
                        side = (par[1].equals("*") == true) ? 99 : Integer.valueOf(par[1]);
                    }
                }
                if (side == 1) {// || side == -2) {
                    el = areaStv.frames.get(Layout.BOTT);
                    double size_falz = (el.artiklRec.getDbl(eArtikl.size_falz) == 0) ? 21 : el.artiklRec.getDbl(eArtikl.size_falz);
                    width = el.spcRec.width - 2 * size_falz;
                } else if (side == 2) {// || side == -1) {
                    el = areaStv.frames.get(Layout.RIGHT);
                    double size_falz = (el.artiklRec.getDbl(eArtikl.size_falz) == 0) ? 21 : el.artiklRec.getDbl(eArtikl.size_falz);
                    width = el.spcRec.width - 2 * size_falz;
                } else if (side == 3) {// || side == -2) {
                    el = areaStv.frames.get(Layout.TOP);
                    double size_falz = (el.artiklRec.getDbl(eArtikl.size_falz) == 0) ? 21 : el.artiklRec.getDbl(eArtikl.size_falz);
                    width = el.spcRec.width - 2 * size_falz;
                } else if (side == 4) {// || side == -1) {
                    el = areaStv.frames.get(Layout.LEFT);
                    double size_falz = (el.artiklRec.getDbl(eArtikl.size_falz) == 0) ? 21 : el.artiklRec.getDbl(eArtikl.size_falz);
                    width = el.spcRec.width - 2 * size_falz;
                }
                if (width >= furnside2Rec.getDbl(eFurnside2.len_max) || (width < furnside2Rec.getDbl(eFurnside2.len_min))) {
                    return false;
                }
            }

            //Если это элемент из мат. ценности (не НАБОР)
            if (furndetRec.get(eFurndet.furniture_id2) == null) {
                if (artiklRec.getInt(eArtikl.id) != -1) {
                    ElemSimple sideStv = determOfSide(mapParam, areaStv);
                    SpcRecord spcAdd = new SpcRecord("ФУРН", furndetRec, artiklRec, sideStv, mapParam);

                    //Ловим ручку, подвес, замок
                    if (propertyStv(areaStv, spcAdd)) {
                        if (UColor.colorFromProduct(spcAdd)) { //подбор по цвету

                            //Добавим спецификацию в элемент
                            if (shortPass == false) {
                                spcAdd.count = UCom.getDbl(spcAdd.getParam(spcAdd.count, 24030));
                                spcAdd.count = spcAdd.count * countKit; //умножаю на количество комплектов
                                sideStv.addSpecific(spcAdd);
                            }
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

    private boolean propertyStv(AreaSimple areaStv, SpcRecord spcAdd) {
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
                return add_specific;

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
                return add_specific;

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
                return add_specific;
            }
        }
        return true;
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
