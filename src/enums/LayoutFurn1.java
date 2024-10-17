/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;

import static enums.UseSideTo.values;

//������� ���������/ ��������! ������ LayoutFurn1.id ��������� � LayoutArea.id  
public enum LayoutFurn1 implements Enam {
    BOTT(1, "������"),
    RICH(2, "������"),
    TOP(3, "�������"),
    LEFT(4, "�����"),
    VERT(-1, "������������"),
    HOR(-2, "��������������");

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
