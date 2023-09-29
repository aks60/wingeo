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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ElemCross extends ElemSimple {

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
                areaLeft.intersect(UGeo.area(0, 0, 0, h, L0[2], h, L0[0], 0));
                areaRigh.intersect(UGeo.area(L0[0], 0, L0[2], h, w, h, w, 0));
            } else {
                areaRigh.intersect(UGeo.area(0, 0, 0, h, L0[2], h, L0[0], 0));
                areaLeft.intersect(UGeo.area(L0[0], 0, L0[2], h, w, h, w, 0));                
            }
            owner.childs().get(0).area = areaLeft;
            owner.childs().get(2).area = areaRigh;

            //UGeo.PRINT(UGeo.area(L0[0], 0, L0[2], h, w, h, w, 0));
            // UGeo.PRINT(areaRigh);
            //Предыднщая и последующая линия от совместной между area1 и area2
            Line2D.Double d[] = UGeo.prevAndNextSegment(areaLeft, areaRigh);
            this.setDimension(d[2].x1, d[2].y1, d[2].x2, d[2].y2);

            //Пересечение канвы сегментами импоста
            double L1[] = UGeo.crossCanvas(this.x1() + M[0], this.y1() + M[1], this.x2() + M[0], this.y2() + M[1], w, h);
            double L2[] = UGeo.crossCanvas(this.x1() - M[0], this.y1() - M[1], this.x2() - M[0], this.y2() - M[1], w, h);

            //UGeo.PRINT(owner.area);
            //Area импоста внутренняя
            //Area areaPadding = UGeo.areaPadding(winc.listFrame);        
            Area areaPadding = UGeo.areaPadding(owner.area, winc.listElem);

            areaPadding.intersect(UGeo.area(L1[0], L1[1], L1[2], L1[3], L2[2], L2[3], L2[0], L2[1]));
            //this.area = UGeo.areaReduc(areaPadding);
            this.area = areaPadding;

        } catch (Exception e) {
            this.area = null;
            System.err.println("Ошибка:Elem2Cross.setLocation() " + e);
        }
    }

    public void paint() {
        try {
            winc.gc2d.draw(this.area);
            winc.gc2d.draw(new Line2D.Double(this.x1(), this.y1(), this.x2(), this.y2()));

        } catch (Exception e) {
            System.err.println("Ошибка:Elem2Cross.paint() " + e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>       
}
