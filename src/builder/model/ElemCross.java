package builder.model;

import builder.Wincalc;
import builder.making.Specific;
import builder.script.GsonElem;
import common.UCom;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import domain.eSysprof;
import domain.eSyssize;
import enums.Layout;
import enums.PKjson;
import enums.Type;
import enums.TypeArtikl;
import enums.UseSide;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.List;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.GeometryFixer;

public class ElemCross extends ElemSimple {

    public ElemCross(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        events();
    }

    @Override
    public void initConstructiv() {

        colorID1 = (isJson(gson.param, PKjson.colorID1)) ? gson.param.get(PKjson.colorID1).getAsInt() : winc.colorID1;
        colorID2 = (isJson(gson.param, PKjson.colorID2)) ? gson.param.get(PKjson.colorID2).getAsInt() : winc.colorID2;
        colorID3 = (isJson(gson.param, PKjson.colorID3)) ? gson.param.get(PKjson.colorID3).getAsInt() : winc.colorID3;

        double angl = UGeo.anglHor(this);
        if (isJson(gson.param, PKjson.sysprofID)) { //профили через параметр
            sysprofRec = eSysprof.find3(gson.param.get(PKjson.sysprofID).getAsInt());
        } else {
            sysprofRec = eSysprof.find4(winc.nuni, type.id2, UseSide.ANY);
        }
        artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false);
        artiklRecAn = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), true);
    }

    public void setLocation() {
        try {
            //Пилим полигон импостом
            Geometry[] geoSplit = UGeo.geoSplit(owner.geom, this.x1(), this.y1(), this.x2(), this.y2());
            Polygon geo1 = (Polygon) geoSplit[1];
            Polygon geo2 = (Polygon) geoSplit[2];
            owner.childs.get(0).geom = geo1;
            owner.childs.get(2).geom = geo2;

            //Новые координаты импоста
            Geometry lineImp = owner.geom.intersection(geoSplit[0]);
            if (lineImp.getGeometryType().equals("MultiLineString")) { //исправление коллизий
                int index = (lineImp.getGeometryN(0).getLength() > lineImp.getGeometryN(1).getLength()) ? 0 : 1;
                lineImp = lineImp.getGeometryN(index);
            }
            //Присваиваю нов. коорд.
            this.setDimension(lineImp.getCoordinates()[0].x, lineImp.getCoordinates()[0].y, lineImp.getCoordinates()[1].x, lineImp.getCoordinates()[1].y);

            //Внутренняя ареа       
            Polygon geoPadding = UGeo.geoPadding(owner.geom, winc.listElem, 0);
            if (geoPadding.isValid() == false) { //исправление коллизий
                GeometryFixer fix = new GeometryFixer(geoPadding);
                geoPadding = (Polygon) fix.getResult().getGeometryN(0);
            }

            //Левый и правый сегмент вдоль импоста
            double delta = this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr); //ширина
            LineSegment baseSegm = new LineSegment(new Coordinate(this.x1(), this.y1()), new Coordinate(this.x2(), this.y2()));
            LineSegment moveBaseSegment[] = {baseSegm.offset(+delta), baseSegm.offset(-delta)};

            //Точки пересечения канвы сегментами импоста
            Polygon areaCanvas = UGeo.newPolygon(0, 0, 0, 10000, 10000, 10000, 10000, 0);
            Coordinate C1[] = UGeo.geoIntersect(areaCanvas, moveBaseSegment[0]);
            Coordinate C2[] = UGeo.geoIntersect(areaCanvas, moveBaseSegment[1]);

            //Ареа импоста обрезаем areaPadding 
            Polygon areaExp = UGeo.newPolygon(C2[0].x, C2[0].y, C1[0].x, C1[0].y, C1[1].x, C1[1].y, C2[1].x, C2[1].y);
            this.geom = (Polygon) areaExp.intersection(geoPadding);

        } catch (Exception e) {
            System.err.println("Ошибка:ElemCross.setLocation " + e);
        }
    }

    //Главная спецификация 
    @Override
    public void setSpecific() {
        try {
            spcRec.place = (Layout.HORIZ == owner.layout()) ? "ВСТ.в" : "ВСТ.г";
            spcRec.setArtikl(artiklRec);
            spcRec.colorID1 = colorID1;
            spcRec.colorID2 = colorID2;
            spcRec.colorID3 = colorID3;
            spcRec.anglCut0 = 90;
            spcRec.anglCut1 = 90;
            spcRec.anglHoriz = anglHoriz;

            if (type == Type.IMPOST) {
                //На эскизе заход импоста не показываю, сразу пишу в спецификацию
                Record syssizeRec = eSyssize.find(artiklRec); //системные константы 
                if (syssizeRec.getInt(eSyssize.id) != -1) {
                    double zax = syssizeRec.getDbl(eSyssize.zax);
                    if (Layout.HORIZ == owner.layout()) { //ареа слева направо  
                        ElemSimple inTop = joinFlat(Layout.TOP), inBott = joinFlat(Layout.BOTT);
                        spcRec.width = (inBott.y1() - inBott.artiklRec.getDbl(eArtikl.height) + inBott.artiklRec.getDbl(eArtikl.size_centr))
                                - (inTop.y2() + inTop.artiklRec.getDbl(eArtikl.height) - inTop.artiklRec.getDbl(eArtikl.size_centr))
                                + zax * 2 + inBott.artiklRec.getDbl(eArtikl.size_falz) + inTop.artiklRec.getDbl(eArtikl.size_falz);
                        spcRec.height = artiklRec.getDbl(eArtikl.height);

                    } else if (Layout.VERT == owner.layout()) { //ареа сверху вниз
                        ElemSimple inLeft = joinFlat(Layout.LEFT), inRight = joinFlat(Layout.RIGHT);
                        spcRec.width = (inRight.x1() - inRight.artiklRec.getDbl(eArtikl.height) + inRight.artiklRec.getDbl(eArtikl.size_centr))
                                - (inLeft.x1() + inLeft.artiklRec.getDbl(eArtikl.height) - inLeft.artiklRec.getDbl(eArtikl.size_centr))
                                + zax * 2 + inLeft.artiklRec.getDbl(eArtikl.size_falz) + inRight.artiklRec.getDbl(eArtikl.size_falz);
                        spcRec.height = artiklRec.getDbl(eArtikl.height);
                    }
                } else {
                    if (Layout.HORIZ == owner.layout()) { //слева направо  
                        spcRec.width = length();
                        spcRec.height = artiklRec.getDbl(eArtikl.height);

                    } else if (Layout.VERT == owner.layout()) { //снизу вверх
                        spcRec.width = length();
                        spcRec.height = artiklRec.getDbl(eArtikl.height);
                    }
                }
            } else if (type == Type.SHTULP) {
                if (Layout.HORIZ == owner.layout()) { //слева направо  
                    spcRec.width = y2() - y1();
                    spcRec.height = artiklRec.getDbl(eArtikl.height);

                } else if (Layout.VERT == owner.layout()) { //сверху вниз
                    spcRec.width = x2() - x1();
                    spcRec.height = artiklRec.getDbl(eArtikl.height);
                }
            } else if (type == Type.STOIKA) {
                if (Layout.HORIZ == owner.layout()) { //слева направо  
                    spcRec.width = y2() - y1();
                    spcRec.height = artiklRec.getDbl(eArtikl.height);

                } else if (Layout.VERT == owner.layout()) { //сверху вниз
                    spcRec.width = x2() - x1();
                    spcRec.height = artiklRec.getDbl(eArtikl.height);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:ElemCross.setSpecific() " + e);
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
            }
            if (List.of(1, 3, 5).contains(spcAdd.artiklRec.getInt(eArtikl.level1)) && spcRec.id != spcAdd.id) {
                spcAdd.width += spcRec.width;
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
            System.err.println("Ошибка:ElemCross.addSpecific() " + e);
        }
    }

    public void paint() {

        if (this.geom != null) {
            java.awt.Color color = winc.gc2d.getColor();
            Shape shape = new ShapeWriter().toShape(this.geom);

            winc.gc2d.setColor(new java.awt.Color(eColor.find(this.colorID2).getInt(eColor.rgb)));
            winc.gc2d.fill(shape);

            winc.gc2d.setColor(new java.awt.Color(000, 000, 000));
            winc.gc2d.draw(new Line2D.Double(this.x1(), this.y1(), this.x2(), this.y2()));
            winc.gc2d.draw(shape);
            winc.gc2d.setColor(color);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    public Layout layout() {
        double angl = this.anglHoriz();
        if (angl == 90) {
            return Layout.VERT;
        } else if (angl == 0) {
            return Layout.HORIZ;
        } else if (angl == -90) {
            return Layout.VERT;
        } else if (angl == 180) {
            return Layout.HORIZ;
        }
        return Layout.ANY;
    }

    // </editor-fold>       
}
