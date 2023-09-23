package enums;

//Вид со стороны изделия по умолчанию
public enum LayoutProduct implements Enam {
    P1(0, "Изнутри"),
    P2(1, "Снаружи"),
    P3(2, "Со стороны открывания");

    public int id;
    public String name;

    LayoutProduct(int id, String name) {
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
