package enums;

import dataset.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//��� ������� � ������� �����������
//������� SYSPROF ���� USE_TYPE
public enum UseArtiklTo implements Enam {
    ANY(0, "����� ���"),
    FRAME(1, "�������"),//������� ������� ������� �������
    STVORKA(2, "�������"),//������� �� ������� �������. ����� ����� ���� �� 10 ������� ������� � ������ �������. ��������������� � ����� � ���������� ���������
    IMPOST(3, "������"),//������� ������� ����� ����� � ���������� ������, �� �� ����� �������
    STOIKA(5, "������"),//������� ������� ����� �����, ���������� � �������. ��� �������������� ��������� ������ �� ����� ������ ���������� L-����������
    CROSSBAR(6, "����������"),//�������, ������� ����� �����, �� �� ����� ���������� ������. ������������ ��� ������������ �������� �� ����������
    RASKLADKA(7, "���������"),//������� ���������. ��� �������� ����� ����������, ��� ������� ������ "���������, �� � ������" ������� ����� ������������ � ���� "���������" � ������ ��������������. �� ����� ���������� ������
    SHTULP(9, "������"),//������� �� ��������� ����������� ������� �������. ���������� �������� ����� "��������� ������� � ������������ �������-�������"  ���� "��������� ���������� - ��������������", � ��� �� ��������� "��������� ����������" � ������������ ���������
    ERKER(10, "�����");//������� ������ ��� ������. ����� ������ ������� �� 2 �������

    public int id;
    public String name;
    //public Record sysprofRec;
    //public List<Record> sysprofList = new ArrayList<Record>();

    UseArtiklTo(int id, String name) {
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

    public static UseArtiklTo get(int id) {
        return Stream.of(values()).filter(en -> en.numb() == id).findFirst().orElse(null);
    }
    
    
}
