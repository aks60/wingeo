
package enums;

public enum LayoutFurn1 implements Enam {
    BOTT(1, "Нижняя"),
    RICH(2, "Правая"),
    TOP(3, "Верхняя"),
    LEFT(4, "Левая"),
    VERT(-1, "Вертикальная"),
    HOR(-2, "Горизонтальная");

    public int id;
    public String name;

    LayoutFurn1(int id, String name) {
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
