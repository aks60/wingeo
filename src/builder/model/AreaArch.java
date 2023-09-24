package builder.model;

import builder.Wincalc;
import builder.IElem5e;
import domain.eArtikl;
import enums.LayoutJoin;
import enums.TypeJoin;
import enums.Layout;
import builder.making.Specific;
import enums.Type;

public class AreaArch extends AreaSimple {

    public double radiusArch = 0; //радиус арки

    public AreaArch(Wincalc winc) {
        super(winc);
//        setDimension(0, 0, winc.rootGson.width(), winc.rootGson.height());
    }

    //Угловые соединения
    public void joining() {

//        super.joining(); //T - соединения
//
//        ElemSimple elemBott = frames.get(Layout.BOTT), elemRight = frames.get(Layout.RIGHT),
//                elemArch = frames.get(Layout.TOP), elemLeft = frames.get(Layout.LEFT);
//
//        double dh = elemArch.artiklRec.getDbl(eArtikl.height);
//        double h = height() - winc.height2;
//        double w = width();
//        double r = (Math.pow(w / 2, 2) + Math.pow(h, 2)) / (2 * h);  //R = (L2 + H2) / 2H - радиус арки        
//        double rad1 = Math.acos((w / 2) / r); // Math.toDegrees() — преобразование радианов в градусы ... Math.asin() — арксинус
//        double rad2 = Math.acos((w - 2 * dh) / ((r - dh) * 2));
//        double a1 = r * Math.sin(rad1);
//        double a2 = (r - dh) * Math.sin(rad2);
//        double ang3 = 90 - Math.toDegrees(Math.atan((a1 - a2) / dh)); //угол реза рамы
//        double ang4 = 90 - (Math.toDegrees(rad1) - (90 - ang3)); //угол реза арки
//        radiusArch = r;
//       
//        winc.listJoin.add(new ElemJoining(winc, TypeJoin.VAR20, LayoutJoin.ANGL, elemLeft, elemBott));  //угловое соединение левое нижнее     
//        winc.listJoin.add(new ElemJoining(winc, TypeJoin.VAR20, LayoutJoin.ANGL, elemBott, elemRight)); //угловое соединение правое нижнее
//
//        ElemJoining elemJoin2 = new ElemJoining(winc, TypeJoin.VAR20, LayoutJoin.ANGL, elemRight, elemArch); //угловое соединение правое верхнее
//        elemJoin2.elem2.anglCut(0, ang4);  //угол реза арки
//        elemJoin2.elem1.anglCut(1, ang3);  //угол реза рамы                             
//        winc.listJoin.add(elemJoin2);
//        
//        ElemJoining elemJoin1 = new ElemJoining(winc, TypeJoin.VAR20, LayoutJoin.ANGL, elemArch, elemLeft); //угловое соединение левое верхнее
//        elemJoin1.elem2.anglCut(0, ang3);  //угол реза рамы
//        elemJoin1.elem1.anglCut(1, ang4);  //угол реза арки
//        winc.listJoin.add(elemJoin1);
    }
}
