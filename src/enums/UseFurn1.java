package enums;

public enum UseFurn1 implements Enam {
    P1(1, "����������"),
    P2(2, "����������"),
    P3(3, "����������<=>"),
    P4(4, "����������|^|");

    public int id;
    public String name;

    UseFurn1(int id, String name) {
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
