package builder.model;

import builder.Wincalc;
import builder.making.Specific;
import builder.making.UColor;
import builder.script.GsonElem;
import common.UCom;
import domain.eArtikl;
import domain.eColor;
import domain.eSetting;
import domain.eSysprof;
import domain.eSyssize;
import enums.Layout;
import enums.PKjson;
import enums.Type;
import enums.TypeArtikl;
import enums.UseSide;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.util.GeometricShapeFactory;

public class ElemFrame extends ElemSimple {

    public double radiusArc = 0; //радиус арки
    public double lengthArc = 0; //длина арки     

    public ElemFrame(Wincalc winc, double id, GsonElem gson, AreaSimple owner) {
        super(winc, id, gson, owner);
        if (gson.type == Type.FRAME_SIDE) {
            events();
        }
    }

    /**
     * Профиль через параметр или первая запись в системе см. табл. sysprof Цвет
     * если нет параметра то берём winc.color.
     */
    @Override
    public void initArtikle() {
        try {
            colorID1 = (isJson(gson.param, PKjson.colorID1)) ? gson.param.get(PKjson.colorID1).getAsInt() : winc.colorID1;
            colorID2 = (isJson(gson.param, PKjson.colorID2)) ? gson.param.get(PKjson.colorID2).getAsInt() : winc.colorID2;
            colorID3 = (isJson(gson.param, PKjson.colorID3)) ? gson.param.get(PKjson.colorID3).getAsInt() : winc.colorID3;

            if (isJson(gson.param, PKjson.sysprofID)) { //профили через параметр
                sysprofRec = eSysprof.find3(gson.param.get(PKjson.sysprofID).getAsInt());

            } else if (owner.sysprofRec != null) { //профили через параметр рамы, створки
                sysprofRec = owner.sysprofRec;
            } else {
                if (Layout.BOTT.equals(layout())) {
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSide.BOT, UseSide.HORIZ);
                } else if (Layout.RIGHT.equals(layout())) {
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSide.RIGHT, UseSide.VERT);
                } else if (Layout.TOP.equals(layout())) {
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSide.TOP, UseSide.HORIZ);
                } else if (Layout.LEFT.equals(layout())) {
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSide.LEFT, UseSide.VERT);
                } else {
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSide.ANY, UseSide.ANY);
                }
            }

            artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false); //артикул
            artiklRecAn = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), true); //аналог 

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.initConstructiv() " + e);
        }
    }

    //Рассчёт полигона стороны рамы
    public void setLocation() {
        try {
            for (int i = 0; i < owner.frames.size(); i++) {
                if (owner.frames.get(i).id == this.id) {
                    
                    if (this.h() != null) {  // arch
                        GeometricShapeFactory gsf = new GeometricShapeFactory();
                        double R = this.radiusArc, L = this.length(), dH = this.artiklRec.getDbl(eArtikl.height);

                        double ang1 = Math.PI / 2 - Math.asin(L / (R * 2));
                        gsf.setSize(2 * R);
                        gsf.setBase(new Coordinate(L / 2 - R, 0));
                        LineString arc1 = gsf.createArc(Math.PI + ang1, Math.PI - 2 * ang1);
                        List.of(arc1.getCoordinates()).forEach(l -> l.setZ(this.id));

                        double ang2 = Math.PI / 2 - Math.asin((L - 2 * dH) / ((R - dH) * 2));
                        gsf.setSize(2 * R - 2 * dH);
                        gsf.setBase(new Coordinate(L / 2 - R + dH, dH));
                        LineString arc2 = gsf.createArc(Math.PI + ang2, Math.PI - 2 * ang2);
                        List.of(arc2.getCoordinates()).forEach(l -> l.setZ(this.id));

                        List<Coordinate> list1 = new ArrayList(List.of(arc1.getCoordinates()));
                        List<Coordinate> list2 = new ArrayList(List.of(arc2.reverse().getCoordinates()));
                        list2.add(list1.get(0));
                        list1.addAll(list2);
                        this.area = gf.createLineString(list1.toArray(new Coordinate[0]));

                    } else { //polygon
                        int k = (i == 0) ? owner.frames.size() - 1 : i - 1;
                        int j = (i == (owner.frames.size() - 1)) ? 0 : i + 1;
                        ElemSimple e1 = owner.frames.get(k);
                        ElemSimple e2 = owner.frames.get(j);

                        //Ширина сегментов
                        double w0 = this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr);
                        double w1 = e1.artiklRec.getDbl(eArtikl.height) - e1.artiklRec.getDbl(eArtikl.size_centr);
                        double w2 = e2.artiklRec.getDbl(eArtikl.height) - e2.artiklRec.getDbl(eArtikl.size_centr);

                        //Входящие и выходящие сегменты
                        LineSegment segm0 = new LineSegment(this.x1(), this.y1(), this.x2(), this.y2());
                        LineSegment segm1 = new LineSegment(e1.x1(), e1.y1(), e1.x2(), e1.y2());
                        LineSegment segm2 = new LineSegment(e2.x1(), e2.y1(), e2.x2(), e2.y2());

                        //Сдвиг сегментов внутрь
                        LineSegment segm3 = segm0.offset(-w0);
                        LineSegment segm4 = segm1.offset(-w1);
                        LineSegment segm5 = segm2.offset(-w2);

                        Coordinate c1 = Intersection.intersection(
                                new Coordinate(segm3.p0.x, segm3.p0.y), new Coordinate(segm3.p1.x, segm3.p1.y),
                                new Coordinate(segm4.p0.x, segm4.p0.y), new Coordinate(segm4.p1.x, segm4.p1.y));
                        Coordinate c2 = Intersection.intersection(
                                new Coordinate(segm3.p0.x, segm3.p0.y), new Coordinate(segm3.p1.x, segm3.p1.y),
                                new Coordinate(segm5.p0.x, segm5.p0.y), new Coordinate(segm5.p1.x, segm5.p1.y));

                        //Полигон элемента конструкции 
                        this.area = UGeo.newPolygon(x1(), y1(), x2(), y2(), c2.x, c2.y, c1.x, c1.y);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.setLocation" + toString() + e);
        }
    }

    public Layout layout() {
        try {
            if (owner.area.getNumPoints() == 4
                    || owner.area.getNumPoints() == 5) {
                int index = UGeo.getIndex(owner.area, this);
                if (index == 0) {
                    return Layout.LEFT;
                } else if (index == 1) {
                    return Layout.BOTT;
                } else if (index == 2) {
                    return Layout.RIGHT;
                } else if (index == 3) {
                    return Layout.TOP;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.layout() " + e);
        }
        return Layout.ANY;
    }

    //Главная спецификация
    @Override
    public void setSpecific() {  //добавление основной спецификации
        try {
            double prip1 = winc.syssizRec.getDbl(eSyssize.prip) / Math.cos(Math.toRadians(anglCut[0] - 45));
            double prip2 = winc.syssizRec.getDbl(eSyssize.prip) / Math.cos(Math.toRadians(anglCut[1] - 45));
            spcRec.place = "ВСТ." + layout().name.substring(0, 1).toLowerCase();
            spcRec.setArtikl(artiklRec);
            spcRec.setColor(colorID1, colorID2, colorID3);
            spcRec.setAnglCut(anglCut[0], anglCut[1]);
            spcRec.width = (winc.syssizRec == null) ? length() : length() + prip1 + prip2;
            spcRec.height = artiklRec.getDbl(eArtikl.height);

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.setSpecific() " + e);
        }
    }

    //Вложеная спецификация
    @Override
    public void addSpecific(Specific spcAdd) { //добавление спесификаций зависимых элементов
        try {
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //кол. ед. с учётом парам. 
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //кол. ед. с шагом
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //поправка мм
            //Армирование
            if (TypeArtikl.isType(spcAdd.artiklRec, TypeArtikl.X107)) {
                spcAdd.place = "ВСТ." + layout().name.substring(0, 1).toLowerCase();
                spcAdd.anglCut0 = 90;
                spcAdd.anglCut1 = 90;
                spcRec.anglHoriz = Math.abs(anglHoriz());

                spcAdd.width += length();

                if ("ps3".equals(eSetting.val(2))) {
                    if ("Да".equals(spcAdd.getParam(null, 34010))) {
                        Double dw1 = artiklRec.getDbl(eArtikl.height) / Math.tan(Math.toRadians(anglCut[0]));
                        Double dw2 = artiklRec.getDbl(eArtikl.height) / Math.tan(Math.toRadians(anglCut[1]));
                        spcAdd.width = spcAdd.width + 2 * winc.syssizRec.getDbl(eSyssize.prip) - dw1 - dw2;
                    }
                } else {
                    if ("от внутреннего угла".equals(spcAdd.getParam(null, 34010))) {
                        double dw1 = artiklRec.getDbl(eArtikl.height) / Math.tan(Math.toRadians(anglCut[0]));
                        double dw2 = artiklRec.getDbl(eArtikl.height) / Math.tan(Math.toRadians(anglCut[1]));
                        spcAdd.width = spcAdd.width + 2 * winc.syssizRec.getDbl(eSyssize.prip) - dw1 - dw2;

                    } else if ("от внутреннего фальца".equals(spcAdd.getParam(null, 34010))) {
                        Double dw1 = (artiklRec.getDbl(eArtikl.height) - artiklRec.getDbl(eArtikl.size_falz)) / Math.tan(Math.toRadians(anglCut[0]));
                        Double dw2 = (artiklRec.getDbl(eArtikl.height) - artiklRec.getDbl(eArtikl.size_falz)) / Math.tan(Math.toRadians(anglCut[1]));
                        spcAdd.width = spcAdd.width + 2 * winc.syssizRec.getDbl(eSyssize.prip) - dw1 - dw2;
                    }
                }

            } else {
                //Выбран авто расчет подвеса
                if (spcAdd.getParam("null", 24013).equals("null") == false) {
                    if (spcAdd.getParam("null", 24013).equals("Да")) {
                        int color = winc.colorID1;
                        if (winc.colorID1 != spcAdd.colorID1) {
                            return;
                        }
                    }
                }
                //Установить текстуру
                if (spcAdd.getParam("null", 24006).equals("null") == false) {
                    int colorID = -1;
                    AreaStvorka elemStv = ((AreaStvorka) owner);
                    AreaSimple areaStv = ((AreaSimple) owner);
                    if ("по текстуре ручки".equals(spcAdd.getParam("null", 24006))) {
                        colorID = UColor.colorFromArtikl(spcAdd.artiklRec.getInt(eArtikl.id), 1, elemStv.knobColor);

                    } else if ("по текстуре подвеса".equals(spcAdd.getParam("null", 24006))) {
                        for (ElemSimple elem : areaStv.frames) {
                            for (Specific spc : elem.spcRec.spcList) {
                                if (spc.artiklRec.getInt(eArtikl.level1) == 2 && spc.artiklRec.getInt(eArtikl.level2) == 12) {
                                    colorID = UColor.colorFromArtikl(spcAdd.artiklRec.getInt(eArtikl.id), 1, spc.colorID1);
                                }
                            }
                        }

                    } else if ("по текстуре замка".equals(spcAdd.getParam("null", 24006))) {
                        for (ElemSimple elem : areaStv.frames) {
                            for (Specific spc : elem.spcRec.spcList) {
                                if (spc.artiklRec.getInt(eArtikl.level1) == 2 && spc.artiklRec.getInt(eArtikl.level2) == 9) {
                                    colorID = UColor.colorFromArtikl(spcAdd.artiklRec.getInt(eArtikl.id), 1, spc.colorID1);
                                }
                            }
                        }
                    }
                    if (colorID != -1) {
                        spcAdd.colorID1 = colorID;
                        spcAdd.colorID2 = colorID;
                        spcAdd.colorID3 = colorID;
                    }
                }
                //Ручка от низа створки, мм 
                if (spcAdd.getParam("null", 24072, 25072).equals("null") == false) {
                    if (builder.making.Furniture.determOfSide(owner) == this) {
                        AreaStvorka stv = (AreaStvorka) owner;
                        stv.knobHeight = UCom.getDbl(spcAdd.getParam(stv.knobHeight, 24072, 25072));
                    }
                }
                //Укорочение от
                if (spcAdd.getParam("null", 25013).equals("null") == false) {
                    if ("длины стороны".equals(spcAdd.getParam("null", 25013))) {
                        spcAdd.width = length() - UCom.getDbl(spcAdd.getParam(0, 25030)); //укорочение, мм

                    } else if ("высоты ручки".equals(spcAdd.getParam("null", 25013))) {
                        AreaStvorka stv = (AreaStvorka) owner;
                        spcAdd.width = stv.knobHeight - UCom.getDbl(spcAdd.getParam(0, 25030)); //укорочение, мм

                    } else if ("сторона - выс. ручки".equals(spcAdd.getParam("null", 25013))) {
                        AreaStvorka stv = (AreaStvorka) owner;
                        spcAdd.width = lengthArc - stv.knobHeight - UCom.getDbl(spcAdd.getParam(0, 25030)); //укорочение, мм                        

                    } else if ("половины стороны".equals(spcAdd.getParam("null", 25013))) {
                        spcAdd.width = (lengthArc / 2) - UCom.getDbl(spcAdd.getParam(0, 25030)); //укорочение, мм 
                    }
                }
                //Фурнитура
                if (TypeArtikl.isType(spcAdd.artiklRec, TypeArtikl.X109)) {
                    if (layout().id == Integer.valueOf(spcAdd.getParam("0", 24010, 25010, 38010, 39002))) {  //"номер стороны"   
                        if ("null".equals(spcAdd.getParam("null", 25013)) == false //"укорочение от"
                                && spcAdd.getParam(0, 25030).equals(0) == false) { //"укорочение, мм"  
                            spcAdd.width = UPar.to_25013(spcRec, spcAdd); //укорочение от высоты ручки
                        }
                    } else {
                        spcAdd.width += width() + winc.syssizRec.getDbl(eSyssize.prip) * 2;
                    }

                } else if (List.of(1, 3, 5).contains(spcAdd.artiklRec.getInt(eArtikl.level1)) && spcRec.id != spcAdd.id) {
                    spcAdd.width += spcRec.width;
                }
            }
            UPar.to_12075_34075_39075(this, spcAdd); //углы реза
            UPar.to_34077_39077(spcAdd); //задать Угол_реза_1/Угол_реза_2
            spcAdd.height = UCom.getDbl(spcAdd.getParam(spcAdd.height, 40006)); //высота заполнения, мм 
            spcAdd.width = UPar.to_12065_15045_25040_34070_39070(spcAdd); //длина мм
            spcAdd.width = UCom.getDbl(spcAdd.getParam(spcAdd.width, 40004)); //ширина заполнения, мм        
            spcAdd.width = spcAdd.width * UPar.to_12030_15030_25035_34030_39030(spcAdd);//"[ * коэф-т ]"
            spcAdd.width = spcAdd.width / UPar.to_12040_15031_25036_34040_39040(spcAdd);//"[ / коэф-т ]"
            UPar.to_40005_40010(spcAdd); //Поправка на стороны четные/нечетные (ширины/высоты), мм
            UPar.to_40007(spcAdd); //высоту сделать длиной
            spcAdd.count = UPar.to_11070_12070_33078_34078(spcAdd); //ставить однократно
            spcAdd.count = UPar.to_39063(spcAdd); //округлять количество до ближайшего

            if (spcRec.id != spcAdd.id) {
                spcRec.spcList.add(spcAdd);
            }

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.addSpecific() " + e);
        }
    }

    //Линии размерности
    @Override
    public void paint() {
        if (this.area != null) {
            Shape shape = new ShapeWriter().toShape(this.area);

            winc.gc2d.setColor(new java.awt.Color(eColor.find(this.colorID2).getInt(eColor.rgb)));
            winc.gc2d.fill(shape);

            winc.gc2d.setColor(new java.awt.Color(000, 000, 000));
            winc.gc2d.draw(shape);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
//    public double h() {
//        return (gson.h != null) ? gson.h : -1;       
//    }
    @Override
    public double x2() {
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                return owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).x1();
            }
        }
        return -1;
    }

    @Override
    public double y2() {
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                return owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).y1();
            }
        }
        return -1;
    }

    @Override
    public void x2(double v) {
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).x1(v);
                return;
            }
        }
    }

    @Override
    public void y2(double v) {
        for (int i = 0; i < owner.frames.size(); i++) {
            if (owner.frames.get(i).x1() == this.x1() && owner.frames.get(i).y1() == this.y1()) {
                owner.frames.get((i == owner.frames.size() - 1) ? 0 : i + 1).y1(v);
                return;
            }
        }
    }
    // </editor-fold>     
}
