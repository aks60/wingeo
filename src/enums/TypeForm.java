package enums;

import builder.model.Com5t;
import static builder.model.Com5t.gf;
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
            if (elem.h() == null) {
                return P02.id; //������� ������
            } else if (elem.h() != null) {
                return P04.id; //������� � ��������
            }

            //���������� (c�����, �����������...)
        } else if (List.of(Type.GLASS, Type.MOSQUIT, Type.RASKL, Type.SAND, Type.BLINDS).contains(elem.type)) {

            Coordinate coo1[] = elem.area.getGeometryN(0).copy().getCoordinates();
            //List<Coordinate> lis = new ArrayList();
            //Set<Integer> set = new HashSet();            
            Coordinate coo2[] = elem.owner.area.getGeometryN(0).copy().getCoordinates();
            for (int i = 0; i < coo1.length; i++) {
                coo1[i] = new Coordinate(Math.ceil(coo1[i].x), Math.ceil(coo1[i].y));             
            }
            for (int i = 0; i < coo2.length; i++) {
                coo2[i] = new Coordinate(Math.ceil(coo2[i].x), Math.ceil(coo2[i].y));
            }
            Geometry geo1 = gf.createPolygon(coo1);
            Geometry geo2 = gf.createPolygon(coo2);

            if (geo1.isRectangle() == true) {
                return P06.id; //������������� ���������� ��� ����

            } else {
                if (elem.area.getNumPoints() < Com5t.MAXSIDE) {
                    return P10.id; //�� �������������, �� ������� ����������

                } else if (geo2.isRectangle() == false && elem.area.getNumPoints() > Com5t.MAXSIDE) {
                    return P12.id; //�� ������������� ���������� � ������

                } else if (geo2.isRectangle() == true && elem.area.getNumPoints() > Com5t.MAXSIDE) {
                    return P14.id; //������������� ���������� � ������
                }
            }
        }

        return P00.id;  //�� ��������� �����
    }
}
