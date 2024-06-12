package builder.param;

import dataset.Record;
import domain.eSetting;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.making.SpcRecord;
import common.listener.ListenerAction;
import domain.eSyspar1;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Par5s {

    protected final int ID = 1;   //Ключ в таблице  
    protected final int GRUP = 3;   //Ключ параметра    
    protected final int TEXT = 2;   //Текст 
    protected Wincalc winc = null;
    public boolean shortPass = false;
    protected String versionPs = eSetting.val(2);
    public HashMap<Integer, String> mapParamTmp = new HashMap<Integer, String>();
    public Record detailRec = null; //текущий элемент детализации
    protected ArrayList<ListenerAction> listenerList = null;

    public Par5s(Wincalc winc) {
        this.winc = winc;
    }

    //Фильтр параметров по умолчанию + выбранных клиентом
    protected boolean filterParamDef(List<Record> paramList) {

        for (Record paramRec : paramList) {
            if (paramRec.getInt(GRUP) < 0) {
                Record syspar1Rec = winc.mapPardef.get(paramRec.getInt(GRUP));
                if (syspar1Rec == null) {
                    return false; //если группы нет
                }
                if (paramRec.getStr(TEXT).equals(syspar1Rec.getStr(eSyspar1.text)) == false) {
                    return false; //если группа есть, а параметр не совпал
                }
            }
        }
        return true;
    }

    //Необработанные параметры
    protected void message(int code) {
//        if (code >= 0) {
//            //if (ParamList.find(code).pass() != 0) {
//            String str = ParamList.find(code).text();
//            System.err.println("Не обработан:  " + code + "-" + str);
//            //}
//        }
    }

    //Необработанные параметры
    protected void message(HashMap<Integer, String> mapParam, int code) {
        if (code >= 0) {
            if (ParamList.find(code).pass() != 0) {
                System.err.println("ВНИМАНИЕ! ПАРААМЕТР " + code + " VALUE " + mapParam.get(code) + " В РАЗРАБОТКЕ.");
            }
        }
    }

    //Необработанные параметры
    protected void message(SpcRecord spc, int code) {
        if (code >= 0) {
            if (ParamList.find(code).pass() != 0) {
                System.err.println("ВНИМАНИЕ! ID " + spc.id + " ПАРААМЕТР " + code + " VALUE " + spc.getParam("-1", code) + " В РАЗРАБОТКЕ.");
            }
        }
    }

    public void listenerFire() {
        for (ListenerAction lp : listenerList) {
            lp.action();
        }
    }

    protected Object calcScript(double Q, double L, double H, String script) {
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("nashorn"); //factory.getEngineByName("JavaScript");
            engine.put("Q", Q);
            engine.put("L", L);
            engine.put("H", H);
            script = script.replace("ceil", "Math.ceil");
            return engine.eval(script);

        } catch (Exception e) {
            System.err.println("Ошибка: builder.param.Par5s.calcScript() " + e);
            return -1;
        }
    }
}
