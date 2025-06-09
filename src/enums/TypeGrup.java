package enums;

import static enums.UseUnit.values;

public enum TypeGrup implements Enam {
    PARAM_USER(1, "��������� �������."),
    COLOR_GRP(2, "������ �������"),
    SERI_ELEM(3, "����� ��"),
    PRICE_INC(4, "������� ��"),
    PRICE_DEC(5, "������ ��"),
    CATEG_ELEM(6, "��������� ��"),
    COLOR_MAP(7, "��������� �����.������"),
    CATEG_VST(8, "��������� ��������"),
    SYS_DATA(9, "��������� ������"),
    CATEG_KIT(10, "��������� ����������"),
    GROUP_VST(10, "������ ��������");

    public int id = 0;
    public String name = "";

    TypeGrup(int id, String name) {
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