package builder.param;

import dataset.Record;
import domain.eSetting;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import builder.making.TRecord;
import common.listener.ListenerAction;
import common.listener.ListenerRecord;
import domain.eSyspar1;
import java.util.ArrayList;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Par5s {

    protected ListenerRecord listener = null;

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
    protected void message(TRecord spc, int code) {
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

    protected Object calcScript(double q, double l, double h, String script) {
        try {
            Context cx = Context.enter();
            Scriptable scope = cx.initStandardObjects();
            ScriptableObject.putProperty(scope, "Q", q);
            ScriptableObject.putProperty(scope, "L", l);
            ScriptableObject.putProperty(scope, "H", h);
            Object result = cx.evaluateString(scope, script, "<cmd>", 1, null);
            return result;

        } catch (Exception e) {
            System.err.println("������: Par5s.calcScript() " + e);
        } finally {
            Context.exit();
        }
        return 0;
    }

    protected Object calcScrip2(double Q, double L, double H, String script) {
//        try {
//            JexlEngine jexl = new JexlBuilder().create();
//            JexlExpression expression = jexl.createExpression(script);
//            MapContext context = new MapContext();
//            context.set("Q", Q);
//            context.set("L", L);
//            context.set("H", H);
//            Object result = expression.evaluate(context);
//            if (result == null) {
//                return 0;
//            }
//            return result;
//
//        } catch (Exception e) {
//            System.err.println("������: Par5s.calcScript() " + e);
//            return 0;
//        }
        return null;
    }
}
