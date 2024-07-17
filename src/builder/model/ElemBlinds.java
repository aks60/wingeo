package builder.model;

import builder.Wincalc;
import builder.making.SpcRecord;
import builder.script.GsonElem;

//Жалюзи
public class ElemBlinds extends ElemSimple {

    public ElemBlinds(Wincalc winc, GsonElem gson, AreaSimple owner) {
        super(owner.winc, gson, owner);
    }

    @Override
    public void initArtikle() {
    }

    public void setLocation() {
    }

    public void setSpecific() {
    }

    @Override
    public void addSpecific(SpcRecord spcAdd) {
    }

    @Override
    public void paint() {
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
