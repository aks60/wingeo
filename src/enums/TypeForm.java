package enums;

import builder.model.Com5t;
import static builder.model.Com5t.gf;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

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
    
    public static int typeform(Com5t elem) {

        //Профиль
        if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER, Type.SHTULP, Type.BOX_SIDE, Type.STV_SIDE).contains(elem.type)) {
            if (elem.h() == null) {
                return P02.id; //профиль прямой
            } else if (elem.h() != null) {
                return P04.id; //профиль с радиусом
            }

            //Заполнения (cтекло, стеклопакет...)
        } else if (List.of(Type.GLASS, Type.MOSQUIT, Type.RASKL, Type.SAND, Type.BLINDS).contains(elem.type)) {

            Coordinate coo1[] = elem.area.getGeometryN(0).copy().getCoordinates();
            //List<Coordinate> lis = new ArrayList();
            //Set<Integer> set = new HashSet();            
            Coordinate coo2[] = elem.owner.area.getGeometryN(0).copy().getCoordinates();
            for (int i = 0; i < coo1.length; i++) {
                coo1[i] = new Coordinate(Math.ceil(coo1[i].x), Math.ceil(coo1[i].y));             
            }
            for (int i = 0; i < coo2.length; i++) {
                coo2[i] = new Coordinate(Math.ceil(coo2[i].x), Math.ceil(coo2[i].y));
            }
            Geometry geo1 = gf.createPolygon(coo1);
            Geometry geo2 = gf.createPolygon(coo2);

            if (geo1.isRectangle() == true) {
                return P06.id; //прямоугольное заполнение без арок

            } else {
                if (elem.area.getNumPoints() < Com5t.MAXSIDE) {
                    return P10.id; //не прямоугольное, не арочное заполнение

                } else if (geo2.isRectangle() == false && elem.area.getNumPoints() > Com5t.MAXSIDE) {
                    return P12.id; //не прямоугольное заполнение с арками

                } else if (geo2.isRectangle() == true && elem.area.getNumPoints() > Com5t.MAXSIDE) {
                    return P14.id; //прямоугольное заполнение с арками
                }
            }
        }

        return P00.id;  //не проверять форму
    }
}
