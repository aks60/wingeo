package builder.model;

import builder.Wincalc;
import builder.making.TFilling;
import builder.making.TRecord;
import builder.script.GsonElem;
import common.UCom;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eSystree;
import enums.PKjson;
import enums.Type;
import enums.TypeArt;
import enums.UseUnit;
import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import startup.Test;

public class ElemGlass extends ElemSimple {

    public double radius = 0; //������ ������
    public double gzazo = 0; //����� ����� ������� � ������������� 
    public HashMap<Integer, Double> axisMap = new HashMap<Integer, Double>(); //������ �� ��� �� ������������
    public ElemSimple side_frame = null;
    public int side_index = 0;

    public Record rascRec = eArtikl.virtualRec(); //���������
    public int rascColor = -3; //���� ���������
    public int[] rascNumber = {2, 2}; //���������� ������ ��������� 

    public ElemGlass(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
    }

    @Override
    public void initArtikle() {

        if (isJson(gson.param, PKjson.artglasID)) {
            artiklRec = eArtikl.find(gson.param.get(PKjson.artglasID).getAsInt(), false);
        } else {
            Record sysreeRec = eSystree.find(winc.nuni); //�� ��������� �����������
            artiklRec = eArtikl.find2(sysreeRec.getStr(eSystree.glas));
        }
        artiklRecAn = artiklRec;

        //���� ������
        if (isJson(gson.param, PKjson.colorGlass)) {
            colorID1 = gson.param.get(PKjson.colorGlass).getAsInt();
            colorID2 = colorID1;
            colorID3 = colorID1;
        } else {
            Record artdetRec = eArtdet.find(artiklRec.getInt(eArtikl.id));
            Record colorRec = eColor.find3(artdetRec.getInt(eArtdet.color_fk));
            colorID1 = colorRec.getInt(eColor.id);
            colorID2 = colorID1;
            colorID3 = colorID1;
        }

        //���������
        if (isJson(gson.param, PKjson.artiklRasc)) {
            rascRec = eArtikl.find(gson.param.get(PKjson.artiklRasc).getAsInt(), false);
            //��������
            if (isJson(gson.param, PKjson.colorRasc)) {
                rascColor = eColor.find(gson.param.get(PKjson.colorRasc).getAsInt()).getInt(eColor.id);
            } else {
                rascColor = eArtdet.find(rascRec.getInt(eArtikl.id)).getInt(eArtdet.color_fk); //���� �� ���������
            }
            //����� �����.
            if (isJson(gson.param, PKjson.horRasc)) {
                rascNumber[0] = gson.param.get(PKjson.horRasc).getAsInt();
            }
            //����� ������.
            if (isJson(gson.param, PKjson.verRasc)) {
                rascNumber[1] = gson.param.get(PKjson.verRasc).getAsInt();
            }
        }
    }

    //���������� ������� �������/���� ��� ����������
    @Override
    public void setLocation() {
        try {
            //������� �� ������ ��� ���������� � �������� ������...
            Geometry geoFalz = owner.area.getGeometryN(2);

            Coordinate[] coo = geoFalz.getCoordinates();
            if (geoFalz.getEnvelopeInternal().getMaxY() <= coo[0].y) {
                coo[0].z = coo[1].z;
                coo[1].z = coo[coo.length - 2].z;
                coo[2].z = coo[coo.length - 2].z;
                coo[coo.length - 1].z = coo[1].z;
            }
        } catch (Exception e) {
            System.err.println("������:ElemGlass.setLocation. " + e);
        }
    }

    //������� ������������    
    @Override
    public void setSpecific() {
        try {
            spcRec.place = "���";
            spcRec.artiklRec(artiklRec);
            spcRec.color(colorID1, colorID2, colorID3);

            //���� ����������� gzazo � gaxis �� ������ ����� ����������. 
            new TFilling(winc, true).fill(this);

            //������� ������������
            this.area = buffer(owner.area.getGeometryN(0), winc.listElem, gzazo, 1);

            Envelope env = this.area.getEnvelopeInternal();
            spcRec.width = env.getWidth();
            spcRec.height = env.getHeight();

            //Test.init() 
        } catch (Exception e) {
            System.err.println("������:ElemGlass.setSpecific() " + e);
        }
    }

    //��������� ������������
    @Override
    public void addSpecific(TRecord spcAdd) {
        try {
            if (spcAdd.artiklRec.getStr(eArtikl.code).substring(0, 1).equals("@")) {
                return;
            }
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //���. ��. � ������ �����. 
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //���. ��. � �����
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //�������� ��         
            if (TypeArt.X502.isType(spcAdd.artiklRec)) {
                return;  //���� ����������� ����� �����
            }
            //���� ������
            Geometry geoFalz = owner.area.getGeometryN(2);
            //Test.init(owner.areaa.getGeometryN(2));

            //�������� �����.
            if (UseUnit.METR.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                Coordinate coo[] = geoFalz.getCoordinates();
                spcAdd.height = spcAdd.artiklRec.getDbl(eArtikl.height);
                spcAdd.anglHoriz = UGeo.anglHor(side_frame.x1(), side_frame.y1(), side_frame.x2(), side_frame.y2()); //���� � ��������� 

                //����
                if (side_frame.h() != null) {
                    int index = IntStream.range(1, coo.length).filter(k -> coo[k].z == side_frame.id).findFirst().getAsInt();
                    spcAdd.anglCut0 = UGeo.anglCut(spcAdd, geoFalz, coo.length - 2, 0, '-');
                    spcAdd.anglCut1 = UGeo.anglCut(spcAdd, geoFalz, index - 1, index, '+');
                    spcAdd.width += UGeo.lengthCurve(geoFalz, side_frame.id);

                    //���������
                } else {
                    Coordinate[] c1 = {coo[UGeo.getIndex(coo.length, side_index - 1)], coo[side_index], coo[UGeo.getIndex(coo.length, side_index + 1)]};
                    Coordinate[] c2 = {coo[side_index], coo[UGeo.getIndex(coo.length, side_index + 1)], coo[UGeo.getIndex(coo.length, side_index + 2)]};
                    double angBetween0 = Math.toDegrees(Angle.angleBetween(c1[0], c1[1], c1[2]));
                    double angBetween1 = Math.toDegrees(Angle.angleBetween(c2[0], c2[1], c2[2]));
                    spcAdd.anglCut0 = Math.abs(angBetween0 - UGeo.anglCut(spcAdd, geoFalz, UGeo.getIndex(coo.length, side_index - 1), side_index, '-'));
                    spcAdd.anglCut1 = Math.abs(angBetween1 - UGeo.anglCut(spcAdd, geoFalz, UGeo.getIndex(coo.length, side_index), UGeo.getIndex(coo.length, side_index + 1), '+'));

                    spcAdd.width += coo[side_index].distance(coo[side_index + 1]);

                }

                spcRec.spcList.add(spcAdd);

                //��������� �� �����������
                double angHor = spcAdd.anglHoriz;
                if ((angHor > 315 && angHor < 360 || angHor >= 0 && angHor < 45) || (angHor > 135 && angHor < 225)) {
                    if (spcAdd.mapParam.get(15010) != null) {
                        if ("���".equals(spcAdd.mapParam.get(15010)) == false) { //������� ������ ������
                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
                        }
                    }
                    if (spcAdd.mapParam.get(15011) != null) {
                        if ("������� �������".equals(spcAdd.mapParam.get(15011))) { //������ ���� �������
                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
                        }
                    }

                    //��������� �� ���������
                } else if ((angHor > 225 && angHor < 315) || (angHor > 45 && angHor < 135)) {
                    if (spcAdd.mapParam.get(15010) != null) {
                        if ("��".equals(spcAdd.mapParam.get(15010)) == false) { //������� ������ ������
                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
                        }
                    }
                    if (spcAdd.mapParam.get(15011) != null) {
                        if ("������� ������".equals(spcAdd.mapParam.get(15011))) { //������ ���� �������
                            spcAdd.width = spcAdd.width - 2 * spcAdd.height;
                        }
                    }
                }
                if ("�� �����������".equals(spcAdd.mapParam.get(15011))) { //������ ���� �������
                    //
                }
                spcAdd.width = UPar.to_12065_15045_25040_34070_39070(spcAdd); //����� ��
                spcAdd.width = spcAdd.width * UPar.to_12030_15030_25035_34030_39030(spcAdd); //"[ * ����-� ]"
                spcAdd.width = spcAdd.width / UPar.to_12040_15031_25036_34040_39040(spcAdd); //"[ / ����-� ]" 

                //�����   
            } else if (UseUnit.PIE.id == spcAdd.artiklRec.getInt(eArtikl.unit)) {

                if (spcAdd.mapParam.get(13014) != null) {
                    LineSegment s2 = UGeo.getSegment(geoFalz, side_index);
                    spcAdd.anglHoriz = UGeo.anglHor(s2.p0.x, s2.p0.y, s2.p1.x, s2.p1.y); //���� � ���������                     
                    if (UCom.containsNumbJust(spcAdd.mapParam.get(13014), spcAdd.anglHoriz) == true) { //���� ���������� �������
                        spcRec.spcList.add(spcAdd);
                    }
                } else {
                    spcRec.spcList.add(spcAdd);
                }
            } else {
                System.err.println("������� �� ���������");
            }
        } catch (Exception e) {
            System.err.println("������:ElemGlass.addSpecific()  " + e);
        }
    }

    public void rascladkaPaint() {
        if (this.rascRec.isVirtual() == false) {
            ArrayList<ElemSimple> list = UCom.filter(winc.listElem, Type.BOX_SIDE, Type.STV_SIDE, Type.IMPOST);
            Polygon areaProf = buffer(owner.area.getGeometryN(0), list, 0, 0);  //������� ����. �� ������ ������� ��� ���������� ���������
            Envelope envRasc = areaProf.getEnvelopeInternal();

            double artH = Math.round(this.rascRec.getDbl(eArtikl.height));
            final int numX = (gson.param.get(PKjson.horRasc) == null) ? 2 : gson.param.get(PKjson.horRasc).getAsInt();
            final int numY = (gson.param.get(PKjson.verRasc) == null) ? 2 : gson.param.get(PKjson.verRasc).getAsInt();
            final double dy = (envRasc.getMaxY() - envRasc.getMinY()) / numY, dx = (envRasc.getMaxX() - envRasc.getMinX()) / numX;
            Record colorRasc = eColor.find(this.rascColor);
            winc.gc2d.setColor(new Color(colorRasc.getInt(eColor.rgb)));

            if (owner.area.getNumPoints() > Com5t.MAXSIDE) {
                double arcID = winc.listElem.stream().filter(e -> e.h() != null).findFirst().get().id;
                List listC = Stream.of(areaProf.getCoordinates()).filter(c -> c.z == arcID).collect(toList());
                double w = 0;
                for (int i = 1; i < numX; i++) {
                    w = w + dx;
                    Polygon p = UGeo.newPolygon(Math.round(envRasc.getMinX() + w - artH / 2), envRasc.getMinY(), artH, (envRasc.getMaxY() - envRasc.getMinY()));
                    Shape shape = new ShapeWriter().toShape(p);
                    winc.gc2d.fill(shape);
                    winc.gc2d.fillRect((int) Math.round(envRasc.getMinX() + w - artH / 2), (int) envRasc.getMinY(), (int) artH, (int) (envRasc.getMaxY() - envRasc.getMinY()));
                }
            } else {
                double h = 0;
                for (int i = 1; i < numY; i++) {
                    h = h + dy;
                    winc.gc2d.fillRect((int) envRasc.getMinX(), (int) Math.round(envRasc.getMinY() + h - artH / 2), (int) (envRasc.getMaxX() - envRasc.getMinX()), (int) artH);
                }

                double w = 0;
                for (int i = 1; i < numX; i++) {
                    w = w + dx;
                    winc.gc2d.fillRect((int) Math.round(envRasc.getMinX() + w - artH / 2), (int) envRasc.getMinY(), (int) artH, (int) (envRasc.getMaxY() - envRasc.getMinY()));
                }
            }
        }
    }

    //����� �����������
    @Override
    public void paint() {
        Geometry geoFalz = owner.area.getGeometryN(2);
        if (geoFalz != null) {
            winc.gc2d.setColor(this.color());
            Shape shape = new ShapeWriter().toShape(geoFalz);
            winc.gc2d.fill(shape);
        }
    }

    public Double width() {
        return owner.area.getGeometryN(2).getEnvelopeInternal().getWidth();
    }

    public Double height() {
        return owner.area.getGeometryN(2).getEnvelopeInternal().getHeight();
    }
}
