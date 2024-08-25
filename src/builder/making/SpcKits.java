package builder.making;

import builder.Wincalc;
import builder.param.KitDet;
import dataset.Query;

//TODO Сделать комплекты в списке спецификации
public class SpcKits extends Cal5e {

    private KitDet kitDet = null;

    public SpcKits(Wincalc winc) {
        super(winc);
    }

    public void calc(double Q, double L, double H, Query kitDet) {
    }
}
