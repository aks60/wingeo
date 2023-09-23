package enums;

public enum UseFurn1 implements Enam {
    P1(1, "Поворотная"),
    P2(2, "Раздвижная"),
    P3(3, "Раздвижная<=>"),
    P4(4, "Раздвижная|^|");

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
