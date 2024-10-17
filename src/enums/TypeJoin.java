package enums;

//�� ��� ����� ��������� � ���� ������
public enum TypeJoin {

    NONE(00, "�� ���������"),
    FLAT(10, "�����������"),
    ANGL(20, "������� �� ��"),
    ANG1(30, "������� �����"),
    ANG2(31, "������� ������"),
    TIMP(40, "� - �������� ������"),
    TCON(41, "� - �������� `�����`");

    public int id;
    public String name;

    TypeJoin(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TypeJoin get(int id) {
        for (TypeJoin v : values()) {
            if (v.id == id) {
                return v;
            }
        }
        return NONE;
    }
}
