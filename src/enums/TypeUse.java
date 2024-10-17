
package enums;

import dataset.Field;
import static enums.UseColor.values;


public enum TypeUse implements Enam {
    
    EMPTY(0, "0. �� ���������"),
    WIN_OPEN_IN(1, "1. ���� ���������� ������"),
    WIN_OPEN_OUT(2, "2. ���� ���������� ������"),
    WIN_EXP(3, "3. ���� ����������"),
    DOOR_OPEN_IN(4, "4. ����� ���������� ������"),
    DOOR_OPEN_OUT(5, "5. ����� ���������� ������"),
    MOSQUITO_NET(6, "6. ��������� �����"),
    GLASS_PAC(7, "7. �����������"),
    ROLLT(8, "8. �������"),
    VITRAG(9, "9. ������");

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
