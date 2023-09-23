package enums;

import static enums.UseSide.values;
import static enums.TypeOpen1.values;

//Стороны конструкции. Против час. стрелки.
//TODO не разобрался с отрицательными параметрами которые привязаны к 24038 и 25038
public enum LayoutFurn3 implements Enam {
    P1(1, "Нижняя"),
    P2(2, "Правая"),
    P3(3, "Верхняя"),
    P4(4, "Левая"),
    P5(-1, "Вертикальная"),
    P6(-2, "Горизонтальная");    

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
