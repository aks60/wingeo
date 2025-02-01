package builder.model;

import builder.Wincalc;
import builder.making.TRecord;
import builder.script.GsonElem;
import common.UCom;
import domain.eArtikl;
import static domain.eArtikl.size_centr;
import domain.eSysprof;
import domain.eSyssize;
import enums.Layout;
import enums.PKjson;
import enums.Type;
import enums.TypeArt;
import enums.TypeJoin;
import enums.UseSideTo;
import java.awt.Shape;
import java.util.List;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.PointLocation;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.GeometryFixer;
import startup.Test;

public class ElemCross extends ElemSimple {

    public ElemCross(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(winc, gson, owner);
        this.addListenerEvents();
    }

    @Override
    public void initArtikle() {

        colorID1 = (isJson(gson.param, PKjson.colorID1)) ? gson.param.get(PKjson.colorID1).getAsInt() : winc.colorID1;
        colorID2 = (isJson(gson.param, PKjson.colorID2)) ? gson.param.get(PKjson.colorID2).getAsInt() : winc.colorID2;
        colorID3 = (isJson(gson.param, PKjson.colorID3)) ? gson.param.get(PKjson.colorID3).getAsInt() : winc.colorID3;

        if (isJson(gson.param, PKjson.sysprofID)) { //������� ����� ��������
            sysprofRec = eSysprof.find3(gson.param.get(PKjson.sysprofID).getAsInt());
        } else {
            sysprofRec = eSysprof.find4(winc.nuni, type.id2, UseSideTo.ANY);
        }
        artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false);
        artiklRecAn = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), true);

        //������� ��� ��������� ������ ��������
        if (artiklRecAn.getDbl(eArtikl.id) == -3) {
            artiklRec.setNo(eArtikl.height, artiklRec.getDbl(eArtikl.height) + 16);
            artiklRecAn.setNo(eArtikl.height, artiklRec.getDbl(eArtikl.height) + 16);
        }

        //���� ������ �����������
        if (artiklRec.getInt(1) == -3) {
            artiklRec.setNo(size_centr, 40);
            artiklRecAn.setNo(size_centr, 40);
        }
    }

    @Override
    public void setLocation() {
        try {
            //����� ������� ��������
            Geometry[] geoSplit = UGeo.splitPolygon(owner.area.getGeometryN(0).copy(), this);

            //new Test().mpol = geoSplit[0];
            owner.childs.get(0).area = (Polygon) geoSplit[1];
            owner.childs.get(2).area = (Polygon) geoSplit[2];

            //����� ���������� �������
            Geometry lineImp = geoSplit[0];
            
            
            if (lineImp.getGeometryType().equals("MultiLineString")) { //����������� ��������
                int index = (lineImp.getGeometryN(0).getLength() > lineImp.getGeometryN(1).getLength()) ? 0 : 1;
                lineImp = lineImp.getGeometryN(index);
            }

            //TODO ���������� ���. �����.
            if (this.layout() == Layout.VERT) {
                this.setDimension(lineImp.getCoordinates()[1].x, lineImp.getCoordinates()[1].y, lineImp.getCoordinates()[0].x, lineImp.getCoordinates()[0].y);
            } else {
                this.setDimension(lineImp.getCoordinates()[0].x, lineImp.getCoordinates()[0].y, lineImp.getCoordinates()[1].x, lineImp.getCoordinates()[1].y);
            }

            //���������� ����       
            Geometry geo2 = UGeo.bufferCross(owner.area.getGeometryN(0), winc.listElem, 0, 0);
            if (owner.area.getGeometryN(1).isValid() == false) { //TODO ����������� ��������
                GeometryFixer fix = new GeometryFixer(owner.area.getGeometryN(1));
                geo2 = (Polygon) fix.getResult().getGeometryN(0);
            }

            //����� � ������ ������� ����� �������
            double delta = this.artiklRec.getDbl(eArtikl.height) - this.artiklRec.getDbl(eArtikl.size_centr); //������
            LineSegment baseSegm = new LineSegment(new Coordinate(this.x1(), this.y1()), new Coordinate(this.x2(), this.y2()));
            LineSegment moveBaseSegment[] = {baseSegm.offset(+delta), baseSegm.offset(-delta)};

            //����� ����������� ����� ���������� �������
            Polygon areaCanvas = UGeo.newPolygon(0, 0, 0, 10000, 10000, 10000, 10000, 0);
            Coordinate C1[] = UGeo.geoCross(areaCanvas, moveBaseSegment[0]);
            Coordinate C2[] = UGeo.geoCross(areaCanvas, moveBaseSegment[1]);

            //���� ������� �������� areaPadding 
            Polygon areaExp = UGeo.newPolygon(C2[0].x, C2[0].y, C1[0].x, C1[0].y, C1[1].x, C1[1].y, C2[1].x, C2[1].y);
            Polygon areaImp = (Polygon) areaExp.intersection(geo2); //������� �������� �����������
            if (areaImp != null) {
                this.area = areaImp;
            }
            //new Test().mpol = this.area;

        } catch (Exception e) {
            System.err.println("������:ElemCross.setLocation " + e);
        }
    }

    //������� ������������ 
    @Override
    public void setSpecific() {
        try {
            spcRec.place = "���." + layout().name.substring(0, 1).toLowerCase();
            spcRec.artiklRec(artiklRec);
            spcRec.color(colorID1, colorID2, colorID3);
            spcRec.setAnglCut(90, 90);
            spcRec.anglHoriz = UGeo.anglHor(x1(), y1(), x2(), y2());
            spcRec.height = artiklRec.getDbl(eArtikl.height);

            Coordinate coo[] = this.area.getCoordinates();
            spcRec.anglCut0 = Math.toDegrees(Angle.angleBetween(coo[coo.length - 2], coo[0], coo[1]));
            spcRec.anglCut1 = Math.toDegrees(Angle.angleBetween(coo[0], coo[1], coo[2]));
            spcRec.anglCut0 = (spcRec.anglCut0 > 90) ? 180 - spcRec.anglCut0 : spcRec.anglCut0;
            spcRec.anglCut1 = (spcRec.anglCut1 > 90) ? 180 - spcRec.anglCut1 : spcRec.anglCut1;

            //�� ������ ����� ������� �� ���������, ����� ���� � ������������
            double zax = (winc.syssizRec != null) ? winc.syssizRec.getDbl(eSyssize.zax) : 0;
            zax = 2 * (zax + this.artiklRec.getDbl(eArtikl.size_falz));

            if (type == Type.IMPOST) {
                LineSegment ls = new LineSegment();
                //����� ������� - ����� ������� �������
                for (int i = 1; i < coo.length; i++) {
                    ls.setCoordinates(coo[i - 1], coo[i]);
                    spcRec.width = (spcRec.width < ls.getLength()) ? ls.getLength() : spcRec.width;
                }
                spcRec.width = spcRec.width + zax;
                
            } else if (type == Type.SHTULP || type == Type.STOIKA) {
                spcRec.width = length();
            }

        } catch (Exception e) {
            System.err.println("������:ElemCross.setSpecific() " + e);
        }
    }

    //�������� ������������ 
    @Override
    public void addSpecific(TRecord spcAdd) { //���������� ������������ ��������� ���������
        try {
            if(spcAdd.artiklRec.getStr(eArtikl.code).substring(0, 1).equals("@")) {
                return;
            }
            spcAdd.count = UPar.to_11030_12060_14030_15040_25060_33030_34060_38030_39060(spcAdd); //���. ��. � ������ �����. 
            spcAdd.count += UPar.to_14050_24050_33050_38050(spcRec, spcAdd); //���. ��. � �����
            spcAdd.width += UPar.to_12050_15050_34051_39020(spcAdd); //�������� �� 

            //�����������
            if (TypeArt.isType(spcAdd.artiklRec, TypeArt.X107)) {
                spcAdd.place = "���." + layout().name.substring(0, 1).toLowerCase();
                spcAdd.setAnglCut(90, 90);
            }
            if (List.of(1, 3, 5).contains(spcAdd.artiklRec.getInt(eArtikl.level1)) && spcRec.id != spcAdd.id) {
                spcAdd.width += spcRec.width;
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
            System.err.println("������:ElemCross.addSpecific() " + e);
        }
    }

    public void joining() {

        if (this.type == Type.SHTULP) {
            for (ElemSimple elem : winc.listElem) {
                if (elem.type == Type.STVORKA_SIDE) {
                    Coordinate coo[] = elem.area.getCoordinates();
                    LineSegment segm = UGeo.getSegment(area, 0);
                    boolean b = PointLocation.isInRing(segm.midPoint(), coo);
                    if (b) {
                        winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.FLAT, this, elem));
                    }
                }
            }
        }
    }

    //����� �����������
    @Override
    public void paint() {
        if (this.area != null) {
            winc.gc2d.setColor(this.color());

            super.paint();

            Shape shape = new ShapeWriter().toShape(this.area);
            winc.gc2d.fill(shape);
            winc.gc2d.setColor(new java.awt.Color(000, 000, 000));
            winc.gc2d.draw(shape);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>       
}
