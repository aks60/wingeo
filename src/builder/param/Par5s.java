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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Par5s {

    protected final int ID = 1;   //���� � �������  
    protected final int GRUP = 3;   //���� ���������    
    protected final int TEXT = 2;   //����� 
    protected Wincalc winc = null;
    public boolean shortPass = false;
    protected String versionPs = eSetting.val(2);
    public HashMap<Integer, String> mapParamTmp = new HashMap<Integer, String>();
    public Record detailRec = null; //������� ������� �����������
    protected ArrayList<ListenerAction> listenerList = null;

    public Par5s(Wincalc winc) {
        this.winc = winc;
    }

    //������ ���������� �� ��������� + ��������� ��������
    protected boolean filterParamDef(List<Record> paramList) {

        for (Record paramRec : paramList) {
            if (paramRec.getInt(GRUP) < 0) {
                Record syspar1Rec = winc.mapPardef.get(paramRec.getInt(GRUP));
                if (syspar1Rec == null) {
                    return false; //���� ������ ���
                }
                if (paramRec.getStr(TEXT).equals(syspar1Rec.getStr(eSyspar1.text)) == false) {
                    return false; //���� ������ ����, � �������� �� ������
                }
            }
        }
        return true;
    }

    //�������������� ���������
    protected void message(int code) {
//        if (code >= 0) {
//            //if (ParamList.find(code).pass() != 0) {
//            String str = ParamList.find(code).text();
//            System.err.println("�� ���������:  " + code + "-" + str);
//            //}
//        }
    }

    //�������������� ���������
    protected void message(HashMap<Integer, String> mapParam, int code) {
        if (code >= 0) {
            if (ParamList.find(code).pass() != 0) {
                System.err.println("��������! ��������� " + code + " VALUE " + mapParam.get(code) + " � ����������.");
            }
        }
    }

    //�������������� ���������
    protected void message(SpcRecord spc, int code) {
        if (code >= 0) {
            if (ParamList.find(code).pass() != 0) {
                System.err.println("��������! ID " + spc.id + " ��������� " + code + " VALUE " + spc.getParam("-1", code) + " � ����������.");
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
            
            Context cx = Context.enter();
            ScriptableObject globalScope = cx.initStandardObjects();
            
            engine.put("Q", Q);
            engine.put("L", L);
            engine.put("H", H);
            script = script.replace("ceil", "Math.ceil");
            return engine.eval(script);

        } catch (Exception e) {
            System.err.println("������: builder.param.Par5s.calcScript() " + e);
            return -1;
        }
    }
}
