package report;

import builder.making.SpcRecord;
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

    public String getPrice() {
        if (otx) {
            return df1.format(spc.sebes2);
        }
        return df1.format(spc.sebes1);
    }

    public String getCost() {
        if (otx) {
            return df1.format(spc.price2);
        }
        return df1.format(spc.price1);
    }

    //--------------------------------------------------------------------------  
    public static List<RSpecific> groups(List<RSpecific> listSpr) {
        HashSet<String> hs = new HashSet<String>();
        List<RSpecific> list = new ArrayList<RSpecific>();
        Map<String, RSpecific> map = new HashMap<String, RSpecific>();

        for (RSpecific sr : listSpr) {
            String key = sr.spc.artikl + sr.spc.colorID1 + sr.spc.colorID2 + sr.spc.colorID3;
            if (hs.add(key)) {
                map.put(key, new RSpecific(sr.spc));
            } else {
                RSpecific s = map.get(key);
                s.spc.weight = s.spc.weight + sr.spc.weight;
                s.spc.count = s.spc.count + sr.spc.count;
                s.spc.quant1 = s.spc.quant1 + sr.spc.quant1;
                s.spc.quant2 = s.spc.quant2 + sr.spc.quant2;
                s.spc.sebes2 = s.spc.sebes2 + sr.spc.sebes2;
                s.spc.price1 = s.spc.price1 + sr.spc.price1;
                s.spc.price2 = s.spc.price2 + sr.spc.price2;
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
