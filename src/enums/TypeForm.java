package enums;

import static enums.UseColor.values;

public enum TypeForm implements Enam {

    //� ������ ������������ ������ 1, 4, 10, 12 ���������
    P00(1, "�� ��������� �����"), //��� 0 �� ���������
    //P02(2, "������� ������"),
    P04(4, "������� � ��������"),
    //P06(6, "������������� ���������� ��� ����"),
    //P08(8, "�� �������������, ������������"),
    P10(10, "�� �������������, �� ������� ����������"),
    P12(12, "�� ������������� ���������� � ������");
    //P14(14, "������������� ���������� � ������");

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
}
