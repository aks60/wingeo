package builder.making;

import builder.Wincalc;
import builder.model.AreaSimple;
import builder.model.ElemSimple;
import dataset.Query;
import enums.TypeArtikl;

public abstract class Cal5e {

    public Wincalc winc = null;
    public String conf = Query.conf;
    public boolean shortPass = false;

    public Cal5e(Wincalc winc) {
        this.winc = winc;
    }

    public void calc() {
        conf = Query.conf;
        Query.conf = "calc";
    }

    public static void artype(SpcRecord spcAdd, ElemSimple elem5e) {
        TypeArtikl typ = TypeArtikl.find(spcAdd.artiklRec);
        switch (typ) {
            case X100:;
            case X101:
            case X102:
            case X103:
            case X104:
            case X105:
            case X106:
            case X107:
            case X108:
            case X109:
            case X110:
            case X111:
            case X112:
            case X113:
            case X114:
            case X115:
            case X116:
            case X117:
            case X118:
            case X119:
            case X120:
            case X125:
            case X130:
            case X131:
            case X132:
            case X133:
            case X134:
            case X135:
            case X136:
            case X137:
            case X149:
            case X150:
            case X165:
            case X170:
            case X175:
                elem5e.addSpecific(spcAdd);
                break;
            case X200:
            case X201:
            case X202:
            case X203:
            case X204:
            case X205:
            case X206:
            case X209:
            case X210:
            case X211:
            case X212:
            case X213:
            case X214:
            case X215:
            case X220:
            case X230:
            case X231:
            case X250:
            case X290:
                elem5e.addSpecific(spcAdd);
                break;
            case X300:
            case X301:
            case X302:
            case X303:
            case X304:
            case X315:
            case X320:
            case X330:
            case X350:
            case X371:
            case X372:
                elem5e.addSpecific(spcAdd);
                break;
            case X400:
            case X401:
            case X402:
            case X403:
                elem5e.addSpecific(spcAdd);
                break;
            case X500:
            case X501:
            case X502:
            case X503:
            case X504:
            case X505:
            case X506:
            case X509:
            case X511:
            case X512:
            case X515:
            case X520:
            case X550:
            case X590:
            case X599:
                elem5e.addSpecific(spcAdd);
                break;
            default:
                throw new AssertionError();
        }
    }
}
