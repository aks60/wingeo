package builder.param;

import dataset.Record;
import java.util.HashMap;
import java.util.List;
import builder.Wincalc;
import domain.eKitdet;
import domain.eKitpar2;

//����������
public class KitDet extends Par5s {

    private double Q, L, H;

    public KitDet(double Q, double L, double H) {
        super(new Wincalc());
        this.Q = Q;
        this.L = L;
        this.H = H;
    }

    public boolean filter(HashMap<Integer, String> mapParam, Record kitdetRec) {

        List<Record> paramList = eKitpar2.filter(kitdetRec.getInt(eKitdet.id)); //������ ���������� �����������  
        if (filterParamDef(paramList) == false) {
            return false; //��������� �� ���������
        }
        //���� �� ���������� ����������
        for (Record rec : paramList) {
            if (check(mapParam, rec) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean check(HashMap<Integer, String> mapParam, Record rec) {

        int grup = rec.getInt(GRUP);
        try {
            switch (grup) {
                case 7030:
                case 7031:
                case 8060:
                case 8061:
                case 9060:
                case 9061: //����������                     
                {
                    Object v = calcScript(Q, L, H, rec.getStr(TEXT));
                    mapParam.put(grup, String.valueOf(v));
                }
                break;
                case 7040:  //����� �������, �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 7050:  //���, �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 7060:  //���������� �� ��� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 7081:                   
                case 8050:  //��������, �� 
                    mapParam.put(grup, rec.getStr(TEXT));
                    break;
                case 8065:
                case 8066:
                case 9065:
                case 9066: //�����, ��                   
                {
                    Object v = calcScript(Q, L, H, rec.getStr(TEXT));
                    mapParam.put(grup, String.valueOf(v));
                }
                break;
                case 8070:
                case 8071:
                case 9070:
                case 9071: //������, ��                     
                {
                    Object v = calcScript(Q, L, H, rec.getStr(TEXT));
                    mapParam.put(grup, String.valueOf(v));
                }
                break;
                case 8075: //���� ���� 
                    mapParam.put(rec.getInt(GRUP), rec.getStr(TEXT));
                    break;
                    //-----------------------�����������------------------------
                case 9081:  //���� ������ ���������, �� 
                    message(rec.getInt(GRUP));
                    break;                     
                case 8081:  //������ ���������, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 9050:  //�������� �����, �� 
                    message(rec.getInt(GRUP));
                    break;
                case 9055:  //�������� ������, ��
                    message(rec.getInt(GRUP));
                    break;                    
                case 8083:  //������� ����� � ���������, �� 
                    message(rec.getInt(GRUP));
                    break;                
                case 9083:  //������� ����� � ���������/����� , �� 
                    message(rec.getInt(GRUP));
                    break;                
                case 8097:  //������������ �� ����� 
                    message(rec.getInt(GRUP));
                    break;
                case 9097:  //������������ �� ������� 
                    message(rec.getInt(GRUP));
                    break;
                case 7098:                 
                case 8098:                   
                case 9098:  //������� (�������) 
                    message(rec.getInt(GRUP));
                    break;
                case 7099:                   
                case 8099:                  
                case 9099:  //������������, �/�. 
                    message(rec.getInt(GRUP));
                    break;
                default:
                    assert !(grup > 0 && grup < 50000) : "��� " + grup + "  �� ���������!!!";
            }
        } catch (Exception e) {
            System.err.println("������:param.FillingDet.check()  parametr=" + grup + "    " + e);
            return false;
        }
        return true;
    }
}
