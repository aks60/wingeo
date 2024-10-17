package enums;

import static enums.UseSideTo.values;
import static enums.TypeOpen1.values;

//������� �����������. ������ ���. �������.
//TODO �� ���������� � �������������� ����������� ������� ��������� � 24038 � 25038
public enum LayoutFurn3 implements Enam {
    P1(1, "������"),
    P2(2, "������"),
    P3(3, "�������"),
    P4(4, "�����"),
    P5(-1, "������������"),
    P6(-2, "��������������");    

    public int id;
    public String name;

    LayoutFurn3(int id, String name) {
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
