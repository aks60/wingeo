package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import com.google.gson.JsonObject;
import domain.eArtikl;
import domain.eSysprof;
import enums.PKjson;
import enums.UseSide;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import org.locationtech.jts.awt.ShapeReader;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

public class ElemCross extends ElemSimple {

    public Area areaTest1 = null;
    public Area areaTest2 = null;
    public Line2D.Double lineTest1 = null;
    public Line2D.Double lineTest2 = null;
    public Line2D.Double lineTest3 = null;

    public ElemCross(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        initСonstructiv(gson.param);
        mouseEvent();
    }

    public void initСonstructiv(JsonObject param) {

        colorID1 = (isJson(param, PKjson.colorID1)) ? param.get(PKjson.colorID1).getAsInt() : winc.colorID1;
        colorID2 = (isJson(param, PKjson.colorID2)) ? param.get(PKjson.colorID2).getAsInt() : winc.colorID2;
        colorID3 = (isJson(param, PKjson.colorID3)) ? param.get(PKjson.colorID3).getAsInt() : winc.colorID3;

        double angl = UGeo.horizontAngl(this);
        if (isJson(param, PKjson.sysprofID)) { //профили через параметр
            sysprofRec = eSysprof.find3(param.get(PKjson.sysprofID).getAsInt());
        } else {
            sysprofRec = eSysprof.find4(winc.nuni, type.id2, UseSide.ANY);
        }
        artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false);
        artiklRecAn = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), true);
    }

    public void setLocation() {
        //System.out.println(id);
        try {
            anglHoriz = UGeo.horizontAngl(this);
            double w = owner.area.getBounds2D().getMaxX();
            double h = owner.area.getBounds2D().getMaxY();

            //Пересечение канвы вектором импоста. Area слева и справа от импоста
            Area P[] = UGeo.splitCanvas(UGeo.areaPoly(0, 0, w, 0, w, h, 0, h), this);
            
            ShapeReader shr = new ShapeReader(gfac);
            Geometry P1 = shr.read(P[0].getPathIterator(null));
            Geometry P2 = shr.read(P[1].getPathIterator(null));
            
            Polygon areaTop = (Polygon) owner.geom.copy();
            Polygon areaBot = (Polygon) owner.geom.copy();
            areaTop.intersection(P1);
            areaBot.intersection(P2);

            owner.childs().get(0).geom = areaTop;
            owner.childs().get(2).geom = areaBot;

            //Предыдущая и последующая линия от совместной между area1 и area2
            LineSegment d[] = UJts.prevAndNextSegment(areaTop, areaBot);

            if (d != null) {
                this.setDimension(d[2].p0.x, d[2].p0.y, d[2].p1.x, d[2].p0.y);
                double M[] = UGeo.diffOnAngl(UGeo.horizontAngl(this), //ширина импоста
                        this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr));

                //Пересечение канвы сегментами импоста
                double L1[] = UJts.crossCanvas(this.x1() + M[0], this.y1() + M[1], this.x2() + M[0], this.y2() + M[1], w, h);
                double L2[] = UJts.crossCanvas(this.x1() - M[0], this.y1() - M[1], this.x2() - M[0], this.y2() - M[1], w, h);

                //Area импоста внутренняя       
                Area areaPadding = UJts.areaPadding(owner.geom, winc.listElem);
                Area areaClip = UGeo.areaPoly(L1[0], L1[1], L1[2], L1[3], L2[2], L2[3], L2[0], L2[1]);

                if (areaClip != null) {
                    areaPadding.intersect(areaClip);
                    this.area = areaPadding;
                }
            }
        } catch (Exception e) {
            this.area = null;
            System.err.println("Ошибка:ElemCross.setLocation() " + e);
        }
    }

    public void paint() {
        try {
            java.awt.Color color = winc.gc2d.getColor();

            if (this.areaTest1 != null) {
                winc.gc2d.setColor(new java.awt.Color(255, 000, 000));
                winc.gc2d.draw(this.areaTest1);
            }
            if (this.areaTest2 != null) {
                winc.gc2d.setColor(new java.awt.Color(000, 255, 000));
                winc.gc2d.draw(this.areaTest2);
            }
            if (this.lineTest1 != null) {
                winc.gc2d.setColor(new java.awt.Color(00, 000, 255));
                winc.gc2d.draw(this.lineTest1);
            }
            if (this.lineTest2 != null) {
                winc.gc2d.setColor(new java.awt.Color(00, 000, 255));
                winc.gc2d.draw(this.lineTest2);
            }
            if (this.lineTest3 != null) {
                winc.gc2d.setColor(new java.awt.Color(00, 000, 255));
                winc.gc2d.draw(this.lineTest3);
            }
            winc.gc2d.setColor(color);
            if (this.area != null) {
                winc.gc2d.draw(this.area);
                //winc.gc2d.draw(new Line2D.Double(this.x1(), this.y1(), this.x2(), this.y2()));
            }

        } catch (Exception e) {
            System.err.println("Ошибка:ElemCross.paint() " + e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    public void setLocation4() {
        //System.out.println(id);
        try {
            anglHoriz = UGeo.horizontAngl(this);
            double w = owner.area.getBounds2D().getMaxX();
            double h = owner.area.getBounds2D().getMaxY();

            //Пересечение канвы вектором импоста. Area слева и справа от импоста
            Area P[] = UGeo.splitCanvas(UGeo.areaPoly(0, 0, w, 0, w, h, 0, h), this);
            Area areaTop = (Area) owner.area.clone();
            Area areaBot = (Area) owner.area.clone();
            areaTop.intersect(P[0]);
            areaBot.intersect(P[1]);

            owner.childs().get(0).area = areaTop;
            owner.childs().get(2).area = areaBot;

            //Предыдущая и последующая линия от совместной между area1 и area2
            Line2D.Double d[] = UGeo.prevAndNextSegment(areaTop, areaBot);

            if (d != null) {
                this.setDimension(d[2].x1, d[2].y1, d[2].x2, d[2].y2);
                double M[] = UGeo.diffOnAngl(UGeo.horizontAngl(this), //ширина импоста
                        this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr));

                //Пересечение канвы сегментами импоста
                double L1[] = UGeo.crossCanvas(this.x1() + M[0], this.y1() + M[1], this.x2() + M[0], this.y2() + M[1], w, h);
                double L2[] = UGeo.crossCanvas(this.x1() - M[0], this.y1() - M[1], this.x2() - M[0], this.y2() - M[1], w, h);

                //Area импоста внутренняя       
                Area areaPadding = UGeo.areaPadding(owner.area, winc.listElem);
                Area areaClip = UGeo.areaPoly(L1[0], L1[1], L1[2], L1[3], L2[2], L2[3], L2[0], L2[1]);

                if (areaClip != null) {
                    areaPadding.intersect(areaClip);
                    this.area = areaPadding;
                }
            }
        } catch (Exception e) {
            this.area = null;
            System.err.println("Ошибка:ElemCross.setLocation() " + e);
        }
    }    
    
    public void setLocation3() {
        //System.out.println(id);
        try {
            anglHoriz = UGeo.horizontAngl(this);
            double w = owner.area.getBounds2D().getMaxX();
            double h = owner.area.getBounds2D().getMaxY();

            //Пересечение канвы вектором импоста
            double P[] = UGeo.splitCanvas(this.x1(), this.y1(), this.x2(), this.y2(), w, h);
            if (id == 8.0) {
                areaTest1 = UGeo.areaPoly(P);
            }

            //Area слева и справа от импоста
            Area areaTop = (Area) owner.area.clone();
            Area areaBot = (Area) owner.area.clone();
            areaTop.intersect(UGeo.areaPoly(P));
            areaBot.subtract(UGeo.areaPoly(P));
            owner.childs().get(0).area = areaTop;
            owner.childs().get(2).area = areaBot;

            //Предыдущая и последующая линия от совместной между area1 и area2
            Line2D.Double d[] = UGeo.prevAndNextSegment(areaTop, areaBot);

            if (d != null) {
                this.setDimension(d[2].x1, d[2].y1, d[2].x2, d[2].y2);
                double M[] = UGeo.diffOnAngl(UGeo.horizontAngl(this), //ширина импоста
                        this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr));

                //Пересечение канвы сегментами импоста
                double L1[] = UGeo.crossCanvas(this.x1() + M[0], this.y1() + M[1], this.x2() + M[0], this.y2() + M[1], w, h);
                double L2[] = UGeo.crossCanvas(this.x1() - M[0], this.y1() - M[1], this.x2() - M[0], this.y2() - M[1], w, h);

                //Area импоста внутренняя       
                Area areaPadding = UGeo.areaPadding(owner.area, winc.listElem);
                Area areaClip = UGeo.areaPoly(L1[0], L1[1], L1[2], L1[3], L2[2], L2[3], L2[0], L2[1]);

                if (areaClip != null) {
                    areaPadding.intersect(areaClip);
                    this.area = areaPadding;
                }
            }
        } catch (Exception e) {
            this.area = null;
            System.err.println("Ошибка:ElemCross.setLocation() " + e);
        }
    }

    public void setLocation2() {
        try {
            anglHoriz = UGeo.horizontAngl(this);
            double w = owner.area.getBounds2D().getMaxX();
            double h = owner.area.getBounds2D().getMaxY();

            //Ширина импоста
            double M[] = UGeo.diffOnAngl(UGeo.horizontAngl(this), this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr));

            //Пересечение канвы вектором импоста при y1 = 0, y2 = h
            double L0[] = UGeo.crossCanvas(this.x1(), this.y1(), this.x2(), this.y2(), w, h);

            //Area слева и справа от импоста
            Area areaLeft = (Area) owner.area.clone();
            Area areaRigh = (Area) owner.area.clone();
            if (this.y1() > this.y2()) {
                areaLeft.intersect(UGeo.areaPoly(0, 0, 0, h, L0[2], h, L0[0], 0));
                areaRigh.intersect(UGeo.areaPoly(L0[0], 0, L0[2], h, w, h, w, 0));
            } else {
                areaRigh.intersect(UGeo.areaPoly(0, 0, 0, h, L0[2], h, L0[0], 0));
                areaLeft.intersect(UGeo.areaPoly(L0[0], 0, L0[2], h, w, h, w, 0));
            }
            Area areaLeft2 = UGeo.areaReduc(areaLeft);
            Area areaRigh2 = UGeo.areaReduc(areaRigh);

            owner.childs().get(0).area = areaLeft2;
            owner.childs().get(2).area = areaRigh2;

            UGeo.PRINT(areaLeft2);
            Area areaTest = UGeo.areaReduc(areaLeft2);
            UGeo.PRINT(areaTest);

            //Предыднщая и последующая линия от совместной между area1 и area2
            Line2D.Double d[] = UGeo.prevAndNextSegment(areaLeft2, areaRigh2);
            if (d != null) {
                this.setDimension(d[2].x1, d[2].y1, d[2].x2, d[2].y2);

                //Пересечение канвы сегментами импоста
                double L1[] = UGeo.crossCanvas(this.x1() + M[0], this.y1() + M[1], this.x2() + M[0], this.y2() + M[1], w, h);
                double L2[] = UGeo.crossCanvas(this.x1() - M[0], this.y1() - M[1], this.x2() - M[0], this.y2() - M[1], w, h);

                //Area импоста внутренняя
                //Area areaPadding = UGeo.areaPadding(winc.listFrame);        
                Area areaPadding = UGeo.areaPadding(owner.area, winc.listElem);
                if (areaPadding != null) {
                    areaPadding.intersect(UGeo.areaPoly(L1[0], L1[1], L1[2], L1[3], L2[2], L2[3], L2[0], L2[1]));
                    this.area = UGeo.areaReduc(areaPadding);
                    //this.area = areaPadding;
                }
            }
        } catch (Exception e) {
            this.area = null;
            System.err.println("Ошибка:ElemCross.setLocation() " + e);
        }
    }

    // </editor-fold>       
}
