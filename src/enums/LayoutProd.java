package enums;

//��� �� ������� ������� �� ���������
public enum LayoutProd implements Enam {
    P1(0, "�������"),
    P2(1, "�������"),
    P3(2, "�� ������� ����������");

    public int id;
    public String name;

    LayoutProd(int id, String name) {
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
