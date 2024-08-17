package builder.making;

import builder.Wincalc;
import builder.model.AreaSimple;
import builder.model.ElemSimple;
import dataset.Query;
import enums.TypeArt;

public abstract class Cal5e {

    public Wincalc winc = null;
    public String conf = Query.conf;
    public boolean shortPass = false;

    public Cal5e(Wincalc winc) {
        this.winc = winc;
    }

    public void calc() {
//        conf = Query.conf;
//        Query.conf = "calc";
    }
}
