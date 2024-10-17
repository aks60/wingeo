
package enums;

import dataset.Field;
import static enums.UseColor.values;


public enum TypeUse implements Enam {
    
    EMPTY(0, "0. Не определен"),
    WIN_OPEN_IN(1, "1. Окно открывание внутрь"),
    WIN_OPEN_OUT(2, "2. Окно открывание наружу"),
    WIN_EXP(3, "3. Окно раздвижное"),
    DOOR_OPEN_IN(4, "4. Дверь открывание внутрь"),
    DOOR_OPEN_OUT(5, "5. Дверь открывание наружу"),
    MOSQUITO_NET(6, "6. Москитная сетка"),
    GLASS_PAC(7, "7. Стеклопакет"),
    ROLLT(8, "8. Роллета"),
    VITRAG(9, "9. Витраж");

    public int id;
    public String name;

    TypeUse(int id, String name) {
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
