package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import enums.TypeJoin;
import java.util.ArrayList;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

public class AreaRectangl extends AreaSimple {

    public AreaRectangl(Wincalc winc, GsonElem gson) {
        super(winc, gson, null);
        this.owner = this;
    }

    //������� ����. ����. ���������� ����� �������� ��� �����������
    @Override
    public void setLocation() {
        try {
            //������� ����
            ArrayList<Coordinate> cooBox = new ArrayList<Coordinate>();
            this.frames.forEach(frame -> cooBox.add(new Coordinate(frame.x1(), frame.y1(), frame.id)));
            cooBox.add(new Coordinate(this.frames.get(0).x1(), this.frames.get(0).y1(), this.frames.get(0).id));

            //�rea ����            
            Polygon geoShell = gf.createPolygon(cooBox.toArray(new Coordinate[0]));
            Polygon geoInner = Com5t.buffer(geoShell, winc.listElem, 0, 0);
            Polygon geoFalz = Com5t.buffer(geoShell, winc.listElem, 0, 1);
            this.area = gf.createMultiPolygon(new Polygon[]{geoShell, geoInner, geoFalz});

            //Test.init(this.area);
        } catch (Exception e) {
            System.err.println("������:AreaRectangl.setLocation" + toString() + e);
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
            System.err.println("AreaRectangl.addJoining() " + e);
        }
    }

    @Override
    public void paint() {
        super.paint();
    }

    // <editor-fold defaultstate="collapsed" desc="GET-SET">
    // </editor-fold>     
}
