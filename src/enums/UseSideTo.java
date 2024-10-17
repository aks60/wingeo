package enums;

// ������� ��� �������� (SYSPROA.ASETS)

import static enums.UseArtiklTo.values;
import static enums.TypeOpen1.values;
import java.util.stream.Stream;
/**
 * 
 * select distinct ASETS from PRO4_SYSPROA where region_id = 177 order by ASETS
 */
// 
public enum UseSideTo implements Enam { 
    VERT(-3, "������������"),//��������� ������������ ��������� �������
    HORIZ(-2, "��������������"),//��������� �������������� ��������� �������
    ANY(-1, "�����"),//��������� ��������� ������� �� ����� ������� � � ����� ���������
    MANUAL(0, "�������"),//������ �� �������������� ��������� ����������� �������� ��� ��������� �� ��������� ������ ���������� 
    BOT(1, "������"),//��������� ��������� ������� ������ �� ������ ������� ������� ��� �������
    RIGHT(2, "������"),// ��������� ��������� ������� ������ �� ������ ������� ������� ��� �������
    TOP(3, "�������"),//��������� ��������� ������� ������ �� ������� ������� ������� ��� �������
    LEFT(4, "�����");//��������� ��������� ������� ������ �� ������ ����� ������� ��� �������

    public int id;
    public String name;

    UseSideTo(int id, String name) {
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
    
    public static UseSideTo get(int id) {
        return Stream.of(values()).filter(en -> en.numb() == id).findFirst().orElse(null);
    }
}
