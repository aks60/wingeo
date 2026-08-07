package builder.making;

import builder.Wincalc;

public abstract class Cal5e {

    public Wincalc winc = null;
    public boolean shortPass = false;

    public Cal5e(Wincalc winc) {
        this.winc = winc;
    }
}
