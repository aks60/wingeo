package report.sup;

import builder.making.TRecord;
import builder.making.TTariffic;
import domain.eColor;
import common.UCom;
import domain.eArtikl;
import enums.UseUnit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RRecord {

    private int mpp = 0;
    private TRecord spc;
    private boolean otx = true;

    public RRecord(TRecord spc) {
        this.spc = spc;
    }

    public RRecord(TRecord spc, boolean otx) {
        this.spc = spc;
        this.otx = otx;
    }

    public TRecord spc() {
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

    public String color(int i) {
        if (i == 1) {
            return eColor.find(spc.colorID1).getStr(eColor.name);
        } else if (i == 2) {
            return eColor.find(spc.colorID1).getStr(eColor.name) + " / "
                    + eColor.find(spc.colorID2).getStr(eColor.name);
        } else {
            return eColor.find(spc.colorID1).getStr(eColor.name) + " / "
                    + eColor.find(spc.colorID2).getStr(eColor.name) + " / "
                    + eColor.find(spc.colorID3).getStr(eColor.name);
        }
    }

    public String count() {
        //if (spc.unit == UseUnit.PIE.id) {
        return String.valueOf(spc.count);
        //} else {
        //    return UCom.format(spc.quant2, -2);
        //}
    }

    public String unit() {
        return UseUnit.getName(spc.unit);
    }

    public String width() {
        if (spc.width > 0) {
            return UCom.format(spc.width / 1000, -3);
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

    public String quant(int index) {
        //double quant = (index == 1) ? spc.quant1 : spc.quant2;
        //return UCom.format(3.12345, 3);
        return (index == 1) ? UCom.format(spc.quant1, 3) : UCom.format(spc.quant2, 3);
    }

    public String costprice() {
        if (otx) {
            return UCom.format(spc.price, -1);
        }
        return UCom.format(spc.costprice, -1);
    }

    public String price2() {
        if (otx) {
            return UCom.format(spc.cost2, -1);
        }
        return UCom.format(spc.cost1, -1);
    }

    public static List<TRecord> groups4T(List<TRecord> listSpc) {
        List<TRecord> listOut = new ArrayList<TRecord>();
        Map<String, TRecord> map = new HashMap<String, TRecord>();
        for (TRecord newRec : listSpc) {

            String key = newRec.artikl + "." + newRec.colorID1 + "." + newRec.colorID2 + "." + newRec.colorID3;
            TRecord oldRec = map.put(key, new TRecord(newRec));
            if (oldRec != null) {
                newRec.weight += oldRec.weight;
                newRec.count += oldRec.count;
                newRec.quant1 += oldRec.quant1;
                newRec.quant2 += oldRec.quant2;
                newRec.cost1 += oldRec.cost1;
                newRec.cost2 += oldRec.cost2;
            }
        }
        map.entrySet().forEach(act -> listOut.add(act.getValue()));
        Collections.sort(listOut, (o1, o2) -> (o1.name).compareTo(o2.name));
        return listOut;
    }

    public static List<RRecord> groups4R(List<RRecord> listSpc) {
        List<RRecord> listOut = new ArrayList<RRecord>();
        Map<String, RRecord> map = new HashMap<String, RRecord>();
        for (RRecord newRec : listSpc) {

            String key = newRec.spc.artikl + "." + newRec.spc.colorID1 + "." + newRec.spc.colorID2 + "." + newRec.spc.colorID3;
            RRecord oldRec = map.put(key, new RRecord(newRec.spc));
            if (oldRec != null) {
                newRec.spc.weight += oldRec.spc.weight;
                newRec.spc.count += oldRec.spc.count;
                newRec.spc.quant1 += oldRec.spc.quant1;
                newRec.spc.quant2 += oldRec.spc.quant2;
                newRec.spc.cost1 += oldRec.spc.cost1;
                newRec.spc.cost2 += oldRec.spc.cost2;
            }
        }
        map.entrySet().forEach(act -> listOut.add(act.getValue()));
        Collections.sort(listOut, (o1, o2) -> (o1.spc.name).compareTo(o2.spc.name));
        return listOut;
    }

    public double cost1() {
        return spc.cost1;
    }

    public double cost2() {
        return spc.cost2;
    }

    public String toString() {
        return spc.artikl + " - " + spc.name + " - " + spc.width + " - " + spc.quant2 + " - " + spc.cost1;
    }
}
