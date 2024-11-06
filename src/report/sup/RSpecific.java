package report.sup;

import builder.making.SpcRecord;
import domain.eColor;
import common.UCom;
import enums.UseUnit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RSpecific {
    
    private SpcRecord spc;
    private boolean otx = true;

    public RSpecific(SpcRecord spc) {
        this.spc = spc;
    }

    public RSpecific(SpcRecord spc, boolean otx) {
        this.spc = spc;
        this.otx = otx;
    }

    public SpcRecord spc() {
        return spc;
    }

    public double id() {
        return spc.id;
    }
    
    public String artikl() {
        return spc.artikl;
    }

    public String name() {
        return spc.name;
    }

    public String cname1() {
        return eColor.find(spc.colorID1).getStr(eColor.name);
    }

    public String count() {
        if (spc.unit == UseUnit.PIE.id) {
            return String.valueOf(spc.count);
        } else {
            return UCom.format(spc.quant2, -2);
        }
    }

    public String unit() {
        return UseUnit.getName(spc.unit);
    }

    public String width() {
        if (spc.width > 0) {
            return UCom.format(spc.width, -2);
        }
        return "";
    }
    
    public String height() {
        if (spc.height > 0) {
            return UCom.format(spc.height, -2);
        }
        return "";
    }

    public String ang0() {
      return UCom.format(spc.anglCut0, -1);  
    }
    
    public String ang1() {
      return UCom.format(spc.anglCut1, -1);  
    }
    
    public String angles() {
        String anglCut0 = (spc.anglCut0 == 0 || spc.anglCut0 == -1) ? "" : UCom.format(spc.anglCut0, -1);
        String anglCut1 = (spc.anglCut1 == 0 || spc.anglCut1 == -1) ? "" : UCom.format(spc.anglCut1, -1);
        String X = ("".equals(anglCut0) && "".equals(anglCut0)) ? "" : "x";
        return anglCut0 + X + anglCut1;
    }

    public String anglHor() {
        return UCom.format(spc.anglHoriz, -1);
    }
    
    public String weight() {
        if (spc.weight > 0) {
            return UCom.format(spc.weight, -2);
        }
        return "";
    }

    public String space() {
        return UCom.format(spc.width, -1) + " x " + UCom.format(spc.height, -1);
    }

    public String sebes2() {
        if (otx) {
            return UCom.format(spc.sebes2, -1);
        }
        return UCom.format(spc.sebes1, -1);
    }

    public String price2() {
        if (otx) {
            return UCom.format(spc.price2, -1);
        }
        return UCom.format(spc.price1, -1);
    }

    //--------------------------------------------------------------------------  
    public static List<RSpecific> groups2(List<RSpecific> listSpr) {
        List<RSpecific> list = new ArrayList<RSpecific>();
        Map<String, RSpecific> map = new HashMap<String, RSpecific>();

        for (RSpecific newRec : listSpr) {
            String key = newRec.spc.artikl + "." + newRec.spc.colorID1 + "." + newRec.spc.colorID2 + "." + newRec.spc.colorID3;
            RSpecific oldRec = map.put(key, new RSpecific(newRec.spc));
            if (oldRec != null) {
                newRec.spc.weight = newRec.spc.weight + oldRec.spc.weight;
                newRec.spc.count = newRec.spc.count + oldRec.spc.count;
                newRec.spc.quant1 = newRec.spc.quant1 + oldRec.spc.quant1;
                newRec.spc.quant2 = newRec.spc.quant2 + oldRec.spc.quant2;
                newRec.spc.sebes2 = newRec.spc.sebes2 + oldRec.spc.sebes2;
                newRec.spc.price1 = newRec.spc.price1 + oldRec.spc.price1;
                newRec.spc.price2 = newRec.spc.price2 + oldRec.spc.price2;
            }
        }
        map.entrySet().forEach(act -> list.add(act.getValue()));
        Collections.sort(list, (o1, o2) -> (o1.spc.name).compareTo(o2.spc.name));
        return list;
    }

    public static List<RSpecific> groups3(List<RSpecific> listSpr) {
        List<RSpecific> list = new ArrayList<RSpecific>();
        Map<String, RSpecific> map = new HashMap<String, RSpecific>();

        for (RSpecific newRec : listSpr) {
            String key = newRec.spc.artikl + "." + newRec.spc.colorID1 + "." + newRec.spc.colorID2 + "." + newRec.spc.colorID3;
            RSpecific oldRec = map.put(key, new RSpecific(newRec.spc));
            if (oldRec != null) {
                //int unitID = oldRec.spc.artiklRec().getInt(eArtikl.unit);
                newRec.spc.count = newRec.spc.count + oldRec.spc.count;
                newRec.spc.sebes2 = newRec.spc.sebes2 + oldRec.spc.sebes2;
                newRec.spc.price2 = newRec.spc.price2 + oldRec.spc.price2;
            }
        }
        map.entrySet().forEach(act -> list.add(act.getValue()));
        Collections.sort(list, (o1, o2) -> (o1.spc.name).compareTo(o2.spc.name));
        return list;
    }

    public double cost1() {
        return spc.price1;
    }

    public double cost2() {
        return spc.price2;
    }
}
