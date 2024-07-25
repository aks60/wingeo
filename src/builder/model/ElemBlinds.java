package builder.model;

import builder.Wincalc;
import builder.making.SpcRecord;
import builder.script.GsonElem;
import common.UCom;
import domain.eArtikl;
import domain.eElement;
import enums.PKjson;
import enums.UseUnit;
import org.locationtech.jts.geom.Envelope;
//TODO - Реализовать жалюзи
//Жалюзи
/**
 * Правила расчёта.
 * Следует измерить ширину и высоту оконного проема в нескольких местах, как в случае с горизонтальными конструкциями;
 * К показателю ширины прибавить 20 см (по 10 см с каждой стороны); При монтаже жалюзи на потолок, к высоте нужно 
 * прибавить расстояние от окна (верхний откос) к потолку, если на стену – минимум +10 см.
 * @author aks
 */
public class ElemBlinds extends ElemSimple {

    public ElemBlinds(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(owner.winc, gson, owner);
    }

    @Override
    public void initArtikle() {
        //Артикл
        if (isJson(gson.param, PKjson.artiklID)) {
            this.artiklRec = eArtikl.find(gson.param.get(PKjson.artiklID).getAsInt(), false);
        } else {
            this.artiklRec = eArtikl.virtualRec();
        }
        this.artiklRecAn = artiklRec;

        //Цвет
        if (isJson(gson.param, PKjson.colorID1)) {
            this.colorID1 = gson.param.get(PKjson.colorID1).getAsInt();
        } else {
            this.colorID1 = -3;
        }
        //Состав жалюзи. ВНИМАЕИЕ! elementID подменён на sysprofRec
        if (isJson(gson.param, PKjson.elementID)) {
            this.sysprofRec = eElement.find4(gson.param.get(PKjson.elementID).getAsInt());
        } else {
            this.sysprofRec = eElement.up.newRecord();
        }
    }

    public void setLocation() {
        spcRec.place = "ВСТ." + layout().name.substring(0, 1).toLowerCase();
    }

    public void setSpecific() {
        try {
            spcRec.place = "ВСТ";
            spcRec.setArtikl(this.artiklRec);
            spcRec.colorID1 = this.colorID1;
            Envelope envMosq = owner.owner.area.getGeometryN(1).getEnvelopeInternal();
            double dXY = 25;
            this.area = UGeo.newPolygon( //жалюзи всегда прямоугольные
                    envMosq.getMinX() - dXY, envMosq.getMinY() - dXY,
                    envMosq.getMinX() - dXY, envMosq.getMaxY() + dXY,
                    envMosq.getMaxX() + dXY, envMosq.getMaxY() + dXY,
                    envMosq.getMaxX() + dXY, envMosq.getMinY() - dXY);
            spcRec.width = (envMosq.getMaxX() - envMosq.getMinX()) + 2 * dXY;
            spcRec.height = (envMosq.getMaxY() - envMosq.getMinY()) + 2 * dXY;

        } catch (Exception e) {
            System.err.println("Ошибка:ElemBlinds.setSpecific() " + e);
        }        
    }

    @Override
    public void addSpecific(SpcRecord spcAdd) {
        try {
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //кол. ед. с учётом парам.
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //кол. ед. с шагом
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //поправка мм            

            double anglHor = UGeo.anglHor(x1(), y1(), x2(), y2());
            if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) { //пог.м.  

                if (anglHor == 0 || anglHor == 180) {
                    spcAdd.width += spcAdd.elem5e.owner.width();
                } else if (anglHor == 90 || anglHor == 270) {
                    spcAdd.width += spcAdd.elem5e.owner.height();
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
            System.err.println("Ошибка:ElemMosquit.addSpecific() " + e);
        }        
    }

    @Override
    public void paint() {
        //Жалюзи на эскизе не прорисовываю
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
