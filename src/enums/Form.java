package enums;

import static enums.Layout.values;

/**
 * Форма контура ограниченная ареа. Рассматривается в комбинации с типом
 * конструкции.
 */
public enum Form {
    BOTT(1, "Нижняя"),
    RIGHT(2, "Правая"),
    TOP(3, "Верхняя"),
    LEFT(4, "Левая"),
    SYMM(5, "Симметрия");

    public int id;
    public String name;

    Form(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Form get(int id) {
        for (Form form : values()) {
            if (form.id == id) {
                return form;
            }
        }
        return null;
    }
}
