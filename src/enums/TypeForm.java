package enums;

import builder.model.Com5t;
import java.util.List;

public enum TypeForm implements Enam {
    //В БиМакс используюеся только 1, 4, 10, 12 параметры
    //ПРОФИЛИ
    P00(1, "не проверять форму"), //или 0 по умолчанию
    P02(2, "профиль прямой"),
    P04(4, "профиль с радиусом"),
    //ЗАПОЛНЕНИЯ
    P06(6, "прямоугольное заполнение без арок"),
    //P08(8, "не прямоугольное, произвольное"),
    P10(10, "не прямоугольное, не арочное заполнение"),
    P12(12, "не прямоугольное заполнение с арками"),
    P14(14, "прямоугольное заполнение с арками");

    public int id;
    public String name;

    private TypeForm(int id, String name) {
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
    
    //Профиль
    public static boolean isTypeformProf(Type tf) {
        return List.of(Type.IMPOST, Type.STOIKA, Type.ERKER, Type.SHTULP, Type.BOX_SIDE, Type.STV_SIDE).contains(tf);
    }

    //Заполнения (cтекло, стеклопакет...)
    public static boolean isTypeformFill(Type tf) {
        return List.of(Type.GLASS, Type.MOSQUIT, Type.RASKL, Type.SAND, Type.BLINDS).contains(tf);
    }

    public static TypeForm typeform(Com5t elem) {

        //Профиль
        if (isTypeformProf(elem.type)) {
            if (elem.h() == null) {
                return P02; //профиль прямой
            } else if (elem.h() != null) {
                return P04; //профиль с радиусом
            }

            //Заполнения (cтекло, стеклопакет...)
        } else if (isTypeformFill(elem.type)) {

            if (elem.area.isRectangle() == true) {
                return P06; //прямоугольное заполнение без арок

            } else if (elem.area.isRectangle() == false) {
                if (elem.area.getNumPoints() < Com5t.MAXPOINT) {
                    return P10; //не прямоугольное, не арочное заполнение

                } else if (elem.owner.area.isRectangle() == false && elem.area.getNumPoints() > Com5t.MAXPOINT) {
                    return P12; //не прямоугольное заполнение с арками

                } else if (elem.owner.area.isRectangle() == true && elem.area.getNumPoints() > Com5t.MAXPOINT) {
                    return P14; //прямоугольное заполнение с арками
                }
            }
        }

        return P00;  //не проверять форму
    }
}
