package enums;

import builder.model.Com5t;
import java.util.List;

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

            if (elem.area.isRectangle() == true) {
                return P06.id; //������������� ���������� ��� ����

            } else if (elem.area.isRectangle() == false) {
                
                Object o1 = elem.area.getNumPoints();
                
                if (elem.area.getNumPoints() < Com5t.MAXSIDE) {
                    return P10.id; //�� �������������, �� ������� ����������

                } else if (elem.owner.area.isRectangle() == false && elem.area.getNumPoints() > Com5t.MAXSIDE) {
                    return P12.id; //�� ������������� ���������� � ������

                } else if (elem.owner.area.isRectangle() == true && elem.area.getNumPoints() > Com5t.MAXSIDE) {
                    return P14.id; //������������� ���������� � ������
                }
            }
        }

        return P00.id;  //�� ��������� �����
    }
}
