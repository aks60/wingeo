/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;
public enum UseFurn3 implements Enam {
    P1(1, "Сторона"),
    P2(2, "Ось поворота"),
    P3(3 , "Крепление петель");

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
