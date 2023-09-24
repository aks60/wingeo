package builder.model;

import builder.Wincalc;
import builder.script.GsonElem;
import dataset.Record;
import domain.eArtikl;
import domain.eElement;
import domain.eSysfurn;
import enums.LayoutHandle;
import enums.TypeOpen1;

public class AreaStvorka extends AreaSimple {

    public Record sysfurnRec = eSysfurn.up.newRecord(); //фурнитура
    public Record handleRec = eArtikl.virtualRec(); //ручка
    public Record loopRec = eArtikl.virtualRec(); //подвес(петли)
    public Record lockRec = eArtikl.virtualRec(); //замок
    public Record mosqRec = eArtikl.virtualRec(); //москитка
    public Record elementRec = eElement.up.newRecord(); //состав москидки  

    public int handleColor = -3; //цвет ручки
    public int loopColor = -3; //цвет подвеса
    public int lockColor = -3; //цвет замка
    public int mosqColor = -3; //цвет москитки

    public double handleHeight = 0; //высота ручки
    public TypeOpen1 typeOpen = TypeOpen1.INVALID; //направление открывания
    public LayoutHandle handleLayout = LayoutHandle.VARIAT; //положение ручки на створке      
    public boolean paramCheck[] = {true, true, true, true, true, true, true, true};
    public double offset[] = {0, 0, 0, 0};
    
    public AreaStvorka(Wincalc wing, GsonElem gson, Com5t owner) {
        super(wing, gson, owner);
    }
}
