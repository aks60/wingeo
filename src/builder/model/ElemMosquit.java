package builder.model;

import builder.Wincalc;
import builder.making.TRecord;
import builder.script.GsonElem;
import common.UCom;
import dataset.Query;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import domain.eElement;
import enums.PKjson;
import enums.TypeArt;
import enums.UseUnit;
import java.awt.BasicStroke;
import java.awt.Color;
import org.locationtech.jts.geom.Envelope;

public class ElemMosquit extends ElemSimple {

    public double anglHoriz = 0;

    public ElemMosquit(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(owner.winc, gson, owner);
    }

    @Override
    public void initArtikle() {
        //������
        if (isJson(gson.param, PKjson.artiklID)) {
            this.artiklRec = eArtikl.find(gson.param.get(PKjson.artiklID).getAsInt(), false);
        } else {
            this.artiklRec = eArtikl.virtualRec();
        }
        this.artiklRecAn = artiklRec;

        //����
        if (isJson(gson.param, PKjson.colorID1)) {
            this.colorID1 = gson.param.get(PKjson.colorID1).getAsInt();
        } else {
            this.colorID1 = -3;
        }

        //������ ��������. ��������! elementID ������� �� sysprofRec
        if (isJson(gson.param, PKjson.elementID)) {
            this.sysprofRec = eElement.find(gson.param.get(PKjson.elementID).getAsInt());
        } else {
            this.sysprofRec = eElement.up.newRecord(Query.SEL);
        }
    }

    //��������� ��������� ��������� ����
    public void setLocation() {
        spcRec.place = "���." + layout().name.substring(0, 1).toLowerCase();
    }

    //������� ������������    
    public void setSpecific() {
        try {
            spcRec.place = "���";
            spcRec.artiklRec(this.artiklRec);
            spcRec.colorID1 = this.colorID1;
            Envelope envMosq = owner.owner.area.getGeometryN(1).getEnvelopeInternal();
            double dXY = 25;
            this.area = UGeo.newPolygon( //�������� ������ �������������
                    envMosq.getMinX() - dXY, envMosq.getMinY() - dXY,
                    envMosq.getMinX() - dXY, envMosq.getMaxY() + dXY,
                    envMosq.getMaxX() + dXY, envMosq.getMaxY() + dXY,
                    envMosq.getMaxX() + dXY, envMosq.getMinY() - dXY);
            spcRec.width = (envMosq.getMaxX() - envMosq.getMinX()) + 2 * dXY;
            spcRec.height = (envMosq.getMaxY() - envMosq.getMinY()) + 2 * dXY;

        } catch (Exception e) {
            System.err.println("������:ElemMosquit.setSpecific() " + e);
        }
    }

    //��������� ������������
    @Override
    public void addSpecific(TRecord spcAdd) {
        try {
            if(spcAdd.artiklRec.getStr(eArtikl.code).substring(0, 1).equals("@")) {
                return;
            }            
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //���. ��. � ������ �����.
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //���. ��. � �����
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //�������� ��            

            double anglHor = UGeo.anglHor(x1(), y1(), x2(), y2());
            if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) { //���.�.  

                if (anglHor == 0 || anglHor == 180) {
                    spcAdd.width += spcAdd.elem5e.owner.width();
                } else if (anglHor == 90 || anglHor == 270) {
                    spcAdd.width += spcAdd.elem5e.owner.height();
                }
            }
            UPar.to_12075_34075_39075(this, spcAdd); //���� ����
            UPar.to_34077_39077(spcAdd); //������ ����_����_1/����_����_2
            spcAdd.height = UCom.getDbl(spcAdd.getParam(spcAdd.height, 40006)); //������ ����������, �� 
            spcAdd.width = UPar.to_12065_15045_25040_34070_39070(spcAdd); //����� ��
            spcAdd.width = UCom.getDbl(spcAdd.getParam(spcAdd.width, 40004)); //������ ����������, ��        
            spcAdd.width = spcAdd.width * UPar.to_12030_15030_25035_34030_39030(spcAdd);//"[ * ����-� ]"
            spcAdd.width = spcAdd.width / UPar.to_12040_15031_25036_34040_39040(spcAdd);//"[ / ����-� ]"
            UPar.to_40005_40010(spcAdd); //�������� �� ������� ������/�������� (������/������), ��
            UPar.to_40007(spcAdd); //������ ������� ������
            spcAdd.count = UPar.to_11070_12070_33078_34078(spcAdd); //������� ����������
            spcAdd.count = UPar.to_39063(spcAdd); //��������� ���������� �� ���������� 

            if (spcRec.id != spcAdd.id) {
                spcRec.spcList.add(spcAdd);
            }

        } catch (Exception e) {
            System.err.println("������:ElemMosquit.addSpecific() " + e);
        }
    }

    //����� �����������
    @Override
    public void paint() {
        if (this.artiklRec.isVirtual() == false) {
            Envelope envMosq = owner.area.getGeometryN(1).getEnvelopeInternal();
            int z = (winc.scale < 0.1) ? 80 : 30;
            int h = 0, w = 0;
            Record colorMosq = eColor.find(this.colorID1);
            winc.gc2d.setColor(this.color());
            winc.gc2d.setStroke(new BasicStroke(1));

            for (int i = 1; i < (envMosq.getMaxY() - envMosq.getMinY()) / z; i++) {
                h = h + z;
                winc.gc2d.drawLine((int) envMosq.getMinX(), (int) (envMosq.getMinY() + h), (int) envMosq.getMaxX(), (int) (envMosq.getMinY() + h));
            }
            for (int i = 1; i < (envMosq.getMaxX() - envMosq.getMinX()) / z; i++) {
                w = w + z;
                winc.gc2d.drawLine((int) (envMosq.getMinX() + w), (int) envMosq.getMinY(), (int) (envMosq.getMinX() + w), (int) envMosq.getMaxY());
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
