/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;

import static enums.UseSideTo.values;
import static enums.TypeOpen1.values;

//����������� ������, ���������� ������� ���������
public enum UseFurn3 implements Enam {
    P1(1, "�������"),
    P2(2, "��� ��������"),
    P3(3 , "��������� ������");

    public int id;
    public String name;

    UseFurn3(int id, String name) {
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
