package report.sup;

import builder.making.TRecord;
import domain.eColor;
import common.UCom;
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

    public String quant(int index) {
        return (index == 1) ? UCom.format(spc.quant1, -1) : UCom.format(spc.quant2, -1);
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
    public static List<RRecord> groups2(List<RRecord> listSpr) {
        List<RRecord> list = new ArrayList<RRecord>();
        Map<String, RRecord> map = new HashMap<String, RRecord>();

        for (RRecord newRec : listSpr) {
            String key = newRec.spc.artikl + "." + newRec.spc.colorID1 + "." + newRec.spc.colorID2 + "." + newRec.spc.colorID3;
            RRecord oldRec = map.put(key, new RRecord(newRec.spc));
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

    public static List<RRecord> groups3(List<RRecord> listSpc) {
        List<RRecord> listOut = new ArrayList<RRecord>();
        Map<String, RRecord> map = new HashMap<String, RRecord>();
        for (RRecord newRec : listSpc) {

            String key = newRec.spc.artikl + "." + newRec.spc.colorID1 + "." + newRec.spc.colorID2 + "." + newRec.spc.colorID3;
            RRecord oldRec = map.put(key, new RRecord(newRec.spc));
            if (oldRec != null) {
                newRec.spc.weight += oldRec.spc.weight;
                newRec.spc.quant1 += oldRec.spc.quant1;
                newRec.spc.quant2 += oldRec.spc.quant2;
                newRec.spc.count += oldRec.spc.count;
                newRec.spc.price1 += oldRec.spc.price1;
                newRec.spc.price2 += oldRec.spc.price2;
            }
        }
        map.entrySet().forEach(act -> listOut.add(act.getValue()));
        Collections.sort(listOut, (o1, o2) -> (o1.spc.name).compareTo(o2.spc.name));
        return listOut;
    }

    public double cost1() {
        return spc.price1;
    }

    public double cost2() {
        return spc.price2;
    }

    public String toString() {
        return spc.artikl + " - " + spc.name + " - " + spc.width + " - " + spc.quant2 + " - " + spc.price1;
    }
}
