package enums;

import builder.model.Com5t;
import builder.model.UGeo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

public enum TypeForm implements Enam {
    //� ������ ������������ ������ 1, 4, 10, 12 ���������
    //�������
    P00(1, "�� ��������� �����"), //��� 0 �� ���������
    P02(2, "������� ������"),
    P04(4, "������� � ��������"),
    //����������
    P06(6, "������������� ���������� ��� ����"),
    //P08(8, "�� �������������, ������������"),
    P10(10, "�� �������������, �� ������� ����������"),
    P12(12, "�� ������������� ���������� � ������"),
    P14(14, "������������� ���������� � ������");

    public int id;
    public String name;

    private TypeForm(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int numb() {
        return id;
    }

    public String text() {
        return name;
    }

    public Enam[] fields() {
        return values();
    }

    public static int typeform(Com5t elem) {

        //�������
        if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER, Type.SHTULP, Type.BOX_SIDE, Type.STV_SIDE).contains(elem.type)) {
            //������� ������
            //������� � ��������
            return (elem.h() == null) ? P02.id : P04.id;

            //���������� (c�����, �����������...)
        } else if (List.of(Type.GLASS, Type.MOSQUIT, Type.RASKL, Type.SAND, Type.BLINDS).contains(elem.type)) {

            List<Coordinate> list = new ArrayList();
            Set<Double> set = new HashSet();
            Coordinate coo[] = elem.area.getGeometryN(0).copy().getCoordinates();
            for (int i = 0; i < coo.length; i++) {
                if (set.add(coo[i].z)) {
                    list.add(new Coordinate(Math.ceil(coo[i].x), Math.ceil(coo[i].y)));
                }
            }
            Geometry geo = UGeo.newPolygon(list);
            if (elem.area.getNumPoints() < Com5t.MAXSIDE) {
                //������������� ���������� ��� ����
                //�� ������������� ��� ����
                return (geo.isRectangle()) ? P06.id : P10.id;
            } else {
                //������������� ���������� � ������
                //�� ������������� ���������� � ������
                return (geo.isRectangle()) ? P14.id : P12.id;
            }
        }

        return P00.id;  //�� ��������� �����
    }
}
