package builder.model;

import builder.Wincalc;
import static builder.model.Com5t.aff;
import static builder.model.Com5t.gf;
import builder.script.GsonElem;
import dataset.Record;
import domain.eArtikl;
import enums.TypeJoin;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

public class AreaArch extends AreaSimple {

    public AreaArch(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
        this.owner = this;
    }

    //������� ����. ����. ���������� ����� �������� ��� �����������
    @Override
    public void setLocation() {
        Record artiklRec = (this.frames.get(0).artiklRecAn == null) ? eArtikl.virtualRec() : this.frames.get(0).artiklRecAn;
        ArrayList<Coordinate> listShell = new ArrayList<Coordinate>();
        Geometry arcA = null, arcB = null;
        try {
            //������� ����
            for (ElemSimple frame : this.frames) {
                if (frame.h() != null) {
                    double dh = artiklRec.getDbl(eArtikl.height);
                    LineSegment segm = UGeo.normalizeSegm(new LineSegment(frame.x1(), frame.y1(), frame.x2(), frame.y2()));
                    double ANG = Math.toDegrees(segm.angle());

                    if (ANG == 0) {
                        arcB = UGeo.newLineArch(segm.p0.x, segm.p1.x, segm.p0.y, frame.h(), frame.id);  //����. ���� �� ����������� 
                    } else {
                        //������� �� �����������
                        aff.setToRotation(Math.toRadians(-ANG), segm.p0.x, segm.p0.y);
                        segm = UGeo.getSegment((LineString) aff.transform(segm.toGeometry(gf)));//������������� ����� � ��������
                        arcA = UGeo.newLineArch(segm.p0.x, segm.p1.x, segm.p0.y, frame.h(), frame.id);  //����. ���� �� �����������   

                        //�������� �������
                        aff.setToRotation(Math.toRadians(ANG), segm.p0.x, segm.p0.y);
                        arcB = aff.transform(arcA);
                    }
                    List.of(arcB.getCoordinates()).forEach(c -> c.setZ(frame.id));
                    listShell.addAll(List.of(arcB.getCoordinates()));

                } else {
                    listShell.add(new Coordinate(frame.x1(), frame.y1(), frame.id));
                }
            }
            listShell.add(listShell.get(0));

            //�rea ���� 
            double dh = artiklRec.getDbl(eArtikl.height) - artiklRec.getDbl(eArtikl.size_centr) - artiklRec.getDbl(eArtikl.size_falz);
            Polygon geoShell = gf.createPolygon(listShell.toArray(new Coordinate[0]));

//            Polygon geoInner = (Polygon) UGeo.bufferOp((Polygon) geoShell, -artiklRec.getDbl(eArtikl.height));
//            Polygon geoFalz = (Polygon) UGeo.bufferOp((Polygon) geoShell, -dh);
//            this.area = gf.createMultiPolygon(new Polygon[]{geoShell, geoInner, geoFalz});
//
            this.area = UGeo.multiPolygon(geoShell, winc.listElem);

            splitLocation((Polygon) this.area.getGeometryN(0), this.childs); //����������� ���������� ��������

            //new Test().mpol = this.area;
        } catch (Exception e) {
            System.err.println("������:AreaArch.setLocation" + toString() + e);
        }
    }

    //L - ����������
    @Override
    public void addJoining() {
        try {
            winc.listJoin.clear();

            super.addJoining(); //T - ����������

            //L - ����������
            for (int i = 0; i < this.frames.size(); i++) { //���� �� �������� ����
                ElemFrame nextFrame = (ElemFrame) this.frames.get((i == this.frames.size() - 1) ? 0 : i + 1);
                winc.listJoin.add(new ElemJoining(this.winc, TypeJoin.ANGL, this.frames.get(i), nextFrame));
            }
        } catch (Exception e) {
            System.err.println("AreaRectangl.joining() " + e);
        }
    }

    @Override
    public void paint() {
        super.paint();

//        if (this.area != null) {
//            Shape shape = new ShapeWriter().toShape(this.area);
//
//            winc.gc2d.setColor(new java.awt.Color(eColor.find(this.colorID2).getInt(eColor.rgb)));
//            winc.gc2d.fill(shape);
//
//            winc.gc2d.setColor(new java.awt.Color(000, 000, 000));
//            winc.gc2d.draw(shape);
//        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>      
}
