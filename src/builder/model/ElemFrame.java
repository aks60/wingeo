package builder.model;

import builder.Wincalc;
import builder.making.SpcRecord;
import builder.making.UColor;
import builder.script.GsonElem;
import common.ArrayLoop;
import common.UCom;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eSysprof;
import domain.eSyssize;
import enums.Layout;
import enums.PKjson;
import enums.Type;
import enums.TypeArt;
import enums.UseSideTo;
import java.awt.Shape;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

public class ElemFrame extends ElemSimple {

    public double radiusArc = 0; //радиус арки
    public double lengthArc = 0; //длина арки  

    public ElemFrame(Wincalc winc, double id, GsonElem gson, AreaSimple owner) {
        super(winc, id, gson, owner);
        if (gson.type == Type.FRAME_SIDE) {
            addEvents();
        }
    }

    //Test
    public ElemFrame(double id, GsonElem gson) {
        super(id, gson);
        if (gson.type == Type.FRAME_SIDE) {
            addEvents();
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
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSideTo.BOT, UseSideTo.HORIZ);
                } else if (Layout.RIGHT.equals(layout())) {
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSideTo.RIGHT, UseSideTo.VERT);
                } else if (Layout.TOP.equals(layout())) {
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSideTo.TOP, UseSideTo.HORIZ);
                } else if (Layout.LEFT.equals(layout())) {
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSideTo.LEFT, UseSideTo.VERT);
                } else {
                    sysprofRec = eSysprof.find5(winc.nuni, type.id2, UseSideTo.ANY, UseSideTo.ANY);
                }
            }

            artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false); //артикул
            artiklRecAn = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), true); //аналог 

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.initConstructiv() " + e);
        }
    }

    //Рассчёт полигона стороны рамы
    @Override
    public void setLocation() {
        try {
            //new Test().mpol = owner.area;
            Geometry geo1 = owner.area.getGeometryN(0), geo2 = owner.area.getGeometryN(1); //внешн. и внутр. ареа арки.
            Coordinate c1[] = geo1.getCoordinates(), c2[] = geo2.getCoordinates();
            for (int i = 0; i < c1.length; i++) {
                if (c1[i].z == this.id) {
                    if (this.h() != null) { //полигон арки

                        List<Coordinate> list = new ArrayLoop();
                        List<Coordinate> c1a = UGeo.getSegmentArch(c1, this); //внешн.коорд.арки
                        List<Coordinate> c2a = UGeo.getSegmentArch(c2, this); //внутр.коорд.арки
                        c2a.add(geo2.getCoordinates()[0]); //посл.точка арки
                        Collections.reverse(c2a); //против час.стрелки

                        list.addAll(c1a);
                        list.addAll(c2a);
                        list.add(c1a.get(0));

                        Polygon poly = gf.createPolygon(list.toArray(new Coordinate[0])); //полигон рамы арки
                        this.area = poly;
                        Coordinate coo[] = this.area.getCoordinates();                       

                    } else { //полигон рамы   
                        this.area = UGeo.newPolygon(this.x1(), this.y1(), this.x2(), this.y2(), c2[i + 1].x, c2[i + 1].y, c2[i].x, c2[i].y);
                    }
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.setLocation" + toString() + e);
        }
    }

    //Главная спецификация
    @Override
    public void setSpecific() {  //добавление основной спецификации
        try {
            spcRec.place = "ВСТ." + layout().name.substring(0, 1).toLowerCase();
            spcRec.artiklRec(artiklRec);
            spcRec.color(colorID1, colorID2, colorID3);
            Coordinate coo[] = this.area.getCoordinates();

            //Углы реза (у рамы всегда 4 вершины)
            if (this.h() == null) {
                spcRec.anglCut0 = Math.toDegrees(Angle.angleBetween(coo[coo.length - 2], coo[0], coo[1]));
                spcRec.anglCut1 = Math.toDegrees(Angle.angleBetween(coo[coo.length - 5], coo[coo.length - 4], coo[coo.length - 3]));
            } else {
                double h = this.artiklRecAn.getDbl(eArtikl.height);
                int index = IntStream.range(1, coo.length).filter(j -> coo[j - 1].distance(coo[j]) > h).findFirst().getAsInt();
                spcRec.anglCut0 = Math.toDegrees(Angle.angleBetween(coo[coo.length - 2], coo[0], coo[1])); 
                spcRec.anglCut1 = Math.toDegrees(Angle.angleBetween(coo[index - 2], coo[index - 1], coo[index]));
            }
            double delta = winc.syssizRec.getDbl(eSyssize.prip) * Math.sin(Math.toRadians(45));
            double prip1 = delta / Math.sin(Math.toRadians(spcRec.anglCut0));
            double prip2 = delta / Math.sin(Math.toRadians(spcRec.anglCut1));
            spcRec.width = (winc.syssizRec == null) ? length() : length() + prip1 + prip2;
            spcRec.height = artiklRec.getDbl(eArtikl.height);

        } catch (Exception e) {
            System.err.println("Ошибка:ElemFrame.setSpecific() " + e);
        }
    }

    //Вложеная спецификация
    @Override
    public void addSpecific(SpcRecord spcAdd) { //добавление спесификаций зависимых элементов
        try {
            if(spcAdd.artiklRec().getStr(eArtikl.code).substring(0, 1).equals("@")) {
                return;
            }            
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //кол. ед. с учётом парам. 
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //кол. ед. с шагом
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //поправка мм
            //Армирование
            if (TypeArt.isType(spcAdd.artiklRec(), TypeArt.X107)) {
                spcAdd.place = "ВСТ." + layout().name.substring(0, 1).toLowerCase();
                spcAdd.setAnglCut(90, 90);
                spcRec.anglHoriz = UGeo.anglHor(x1(), y1(), x2(), y2());

                spcAdd.width += length();

                if ("от внутреннего угла".equals(spcAdd.getParam(null, 34010))) {
                    double dw1 = artiklRec.getDbl(eArtikl.height) / Math.tan(Math.toRadians(spcRec.anglCut0));
                    double dw2 = artiklRec.getDbl(eArtikl.height) / Math.tan(Math.toRadians(spcRec.anglCut1));
                    spcAdd.width = spcAdd.width + 2 * winc.syssizRec.getDbl(eSyssize.prip) - dw1 - dw2;

                } else if ("от внутреннего фальца".equals(spcAdd.getParam(null, 34010))) {
                    Double dw1 = (artiklRec.getDbl(eArtikl.height) - artiklRec.getDbl(eArtikl.size_falz)) / Math.tan(Math.toRadians(spcRec.anglCut0));
                    Double dw2 = (artiklRec.getDbl(eArtikl.height) - artiklRec.getDbl(eArtikl.size_falz)) / Math.tan(Math.toRadians(spcRec.anglCut1));
                    spcAdd.width = spcAdd.width + 2 * winc.syssizRec.getDbl(eSyssize.prip) - dw1 - dw2;
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
                        colorID = UColor.colorFromArtikl(spcAdd.artiklRec().getInt(eArtikl.id), 1, elemStv.knobColor);

                    } else if ("по текстуре подвеса".equals(spcAdd.getParam("null", 24006))) {
                        for (ElemSimple elem : areaStv.frames) {
                            for (SpcRecord spc : elem.spcRec.spcList) {
                                if (spc.artiklRec().getInt(eArtikl.level1) == 2 && spc.artiklRec().getInt(eArtikl.level2) == 12) {
                                    colorID = UColor.colorFromArtikl(spcAdd.artiklRec().getInt(eArtikl.id), 1, spc.colorID1);
                                }
                            }
                        }

                    } else if ("по текстуре замка".equals(spcAdd.getParam("null", 24006))) {
                        for (ElemSimple elem : areaStv.frames) {
                            for (SpcRecord spc : elem.spcRec.spcList) {
                                if (spc.artiklRec().getInt(eArtikl.level1) == 2 && spc.artiklRec().getInt(eArtikl.level2) == 9) {
                                    colorID = UColor.colorFromArtikl(spcAdd.artiklRec().getInt(eArtikl.id), 1, spc.colorID1);
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
                    if (builder.making.SpcFurniture.determOfSide(owner) == this) {
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
                if (TypeArt.isType(spcAdd.artiklRec(), TypeArt.X109)) {
                    if (layout().id == Integer.valueOf(spcAdd.getParam("0", 24010, 25010, 38010, 39002))) {  //"номер стороны"   
                        if ("null".equals(spcAdd.getParam("null", 25013)) == false //"укорочение от"
                                && spcAdd.getParam(0, 25030).equals(0) == false) { //"укорочение, мм"  
                            spcAdd.width = UPar.to_25013(spcRec, spcAdd); //укорочение от высоты ручки
                        }
                    } else {
                        spcAdd.width += width() + winc.syssizRec.getDbl(eSyssize.prip) * 2;
                    }

                } else if (List.of(1, 3, 5).contains(spcAdd.artiklRec().getInt(eArtikl.level1)) && spcRec.id != spcAdd.id) {
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

            super.paint();         
            winc.gc2d.setColor(this.color());
            Shape shape = new ShapeWriter().toShape(this.area.getGeometryN(0));
            winc.gc2d.fill(shape);

            winc.gc2d.setColor(new java.awt.Color(000, 000, 000));
            winc.gc2d.draw(shape);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
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
