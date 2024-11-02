package report.sup;

import builder.making.SpcRecord;
import domain.eArtikl;
import domain.eColor;
import enums.UseUnit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class RSpecific {

    private static DecimalFormat df1 = new DecimalFormat("#0.0");
    private static DecimalFormat df2 = new DecimalFormat("#0.00");
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

    public String getArtikl() {
        return spc.artikl;
    }

    public String getName() {
        return spc.name;
    }

    public String getColorID1() {
        return eColor.find(spc.colorID1).getStr(eColor.name);
    }

    public String getCount() {
        if (spc.unit == UseUnit.PIE.id) {
            return String.valueOf(spc.count);
        } else {
            return df2.format(spc.quant2);
        }
    }

    public String getUnit() {
        return UseUnit.getName(spc.unit);
    }

    public String getWidth() {
        if (spc.width > 0) {
            return df2.format(spc.width);
        }
        return "";
    }

    public String getAngl() {
        String anglCut0 = (spc.anglCut0 == 0 || spc.anglCut0 == -1) ? "" : df1.format(spc.anglCut0);
        String anglCut1 = (spc.anglCut1 == 0 || spc.anglCut1 == -1) ? "" : df1.format(spc.anglCut1);
        String X = ("".equals(anglCut0) && "".equals(anglCut0)) ? "" : "x";
        return anglCut0 + X + anglCut1;
    }

    public String getWeight() {
        if (spc.weight > 0) {
            return df2.format(spc.weight);
        }
        return "";
    }

    public String getSpace() {
        return df1.format(spc.width) + " x " + df2.format(spc.height);
    }

    public String getSebes2() {
        if (otx) {
            return df1.format(spc.sebes2);
        }
        return df1.format(spc.sebes1);
    }

    public String getPrice2() {
        if (otx) {
            return df1.format(spc.price2);
        }
        return df1.format(spc.price1);
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

    public double getCost1() {
        return spc.price1;
    }

    public double getCost2() {
        return spc.price2;
    }
}
