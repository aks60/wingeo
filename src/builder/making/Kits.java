package builder.making;

import builder.Wincalc;
import builder.param.KitDet;
import dataset.Query;

public class Kits extends Cal5e {

    private KitDet kitDet = null;

    public Kits(Wincalc winc) {
        super(winc);
    }

    public void calc(double Q, double L, double H, Query kitDet) {
        super.calc();
        try {

        } catch (Exception e) {
            System.err.println("Ошибка:kits.calc() " + e);
        } finally {
            Query.conf = conf;
        }
    }
}
