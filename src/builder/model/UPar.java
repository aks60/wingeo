package builder.model;

import builder.making.SpcRecord;
import domain.eArtikl;
import builder.param.ParamList;
import enums.UseUnit;
import common.UCom;
import domain.eSetting;
import enums.Layout;
import java.util.HashMap;
import java.util.List;

/**
 * ��������� � ������� ������� �������� ����������� ����� ��������
 */
public class UPar {

    //���������� �� �� ������ ����� 
    public static double to_25013(SpcRecord spcRec, SpcRecord spcAdd) {

        String ps = spcAdd.getParam("null", 25013); //���������� ��
        if (!"null".equals(ps)) {
            List<String> list = ParamList.find(25013).dict();  //[����� �������, ������ �����, ������� ���-�����, �������� �������]             
            double dx = UCom.getDbl(spcAdd.getParam(0, 25030)); //"����������, ��"

            if (list.get(0).equals(ps)) {
                return spcRec.width - dx;

            } else if (list.get(1).equals(ps)) {
                AreaStvorka stv = (AreaStvorka) spcAdd.elem5e.owner;
                return stv.knobHeight - dx;

            } else if (list.get(2).equals(ps)) {
                AreaStvorka stv = (AreaStvorka) spcAdd.elem5e.owner;
                return spcRec.width - stv.knobHeight - dx;

            } else if (list.get(3).equals(ps)) {
                return spcRec.width / 2 - dx;
            }
        }
        return spcAdd.elem5e.length();
    }

    //������ ���������� ��. � �����
    public static double to_14050_24050_33050_38050(SpcRecord spcRec, SpcRecord spcAdd) {

        int step = Integer.valueOf(spcAdd.getParam(-1, 14050, 24050, 33050, 38050)); //���, ��
        if (step != -1) {
            double width_begin = UCom.getDbl(spcAdd.getParam(0, 14040, 24040, 33040, 38040)); //����� �������, ��
            int count_step = Integer.valueOf(spcAdd.getParam(1, 14060, 24060, 33060, 38060)); //"���������� �� ���"
            double width_next = 0;
            if ("null".equals(spcAdd.getParam("null", 38004, 39005))) {
                width_next = spcRec.elem5e.length() - width_begin;

            } else if ("�� ���������".equals(spcAdd.getParam("null", 38004, 39005))) {
                width_next = (spcRec.elem5e.width() * 2 + spcRec.elem5e.height() * 2) - width_begin;

            } else if ("�� �������".equals(spcAdd.getParam("null", 38004, 39005))) {
                width_next = spcRec.elem5e.width() * spcRec.elem5e.height() - width_begin;

            } else if ("����� �� �������".equals(spcAdd.getParam("null", 38004, 39005))
                    && "null".equals(spcAdd.getParam("null", 38010, 39002))) {
                double length = 0;
                if ("1".equals(spcAdd.getParam("null", 38010, 39002))) {
                    length = UCom.layout(spcRec.elem5e.root.frames, Layout.BOTT).length();
                } else if ("2".equals(spcAdd.getParam("null", 38010, 39002))) {
                    length = UCom.layout(spcRec.elem5e.root.frames, Layout.RIGHT).length();
                } else if ("3".equals(spcAdd.getParam("null", 38010, 39002))) {
                    length = UCom.layout(spcRec.elem5e.root.frames, Layout.TOP).length();
                } else if ("4".equals(spcAdd.getParam("null", 38010, 39002))) {
                    length = UCom.layout(spcRec.elem5e.root.frames, Layout.LEFT).length();
                }
                width_next = length - width_begin;
            }
            int count = (int) width_next / step;
            if (count_step == 1) {
                if (count < 1) {
                    return 1;
                }
                return (width_next % step > 0) ? ++count : count;
            } else {
                int count2 = (int) width_next / step;
                int count3 = (int) (width_next % step) / (step / count_step);
                return ((width_next % step) % (step / count_step) > 0) ? count2 * count_step + count3 + 1 : count2 * count_step + count3;
            }
        }
        return 0;
    }

    //������ ���������� ��. � �����
    public static double to_11050(SpcRecord spcAdd, ElemJoining elemJoin) {

        int step = Integer.valueOf(spcAdd.getParam(-1, 11050)); //���, ��
        if (step != -1) {
            double width_begin = UCom.getDbl(spcAdd.getParam(0, 11040)); //����� �������, ��
            int count_step = Integer.valueOf(spcAdd.getParam(1, 11060)); //"���������� �� ���"
            ElemSimple elem5e = null;
            double width_next = 0;

            if ("��".equals(spcAdd.getParam("���", 11010, 12010))) {
                elem5e = elemJoin.elem1;

            } else if ("��".equals(spcAdd.getParam("���", 11020, 12020))) {
                elem5e = elemJoin.elem2;

            } else {
                elem5e = elemJoin.elem1; //�� �����.
            }
            if ("�������".equals(spcAdd.getParam("", 11072, 12072))) {
                double length = (elem5e.width() > elem5e.height()) ? elem5e.width() : elem5e.height();
                width_next = length - width_begin;

            } else if ("�������".equals(spcAdd.getParam("", 11072, 12072))) {
                double length = (elem5e.width() < elem5e.height()) ? elem5e.width() : elem5e.height();
                width_next = length - width_begin;

            } else if ("�����".equals(spcAdd.getParam("", 11072, 12072))) {
                double length = elemJoin.elem2.width();
                width_next = length - width_begin;

            } else {
                width_next = elem5e.width() - width_begin;
            }
            int count = (int) width_next / step;
            if (count_step == 1) {
                if (count < 1) {
                    return 1;
                }
                return (width_next % step > 0) ? ++count : count;
            } else {
                int count2 = (int) width_next / step;
                int count3 = (int) (width_next % step) / (step / count_step);
                return ((width_next % step) % (step / count_step) > 0) ? count2 * count_step + count3 + 1 : count2 * count_step + count3;
            }
        }
        return 0;
    }

    //���������� ��.
    public static double to_11030_12060_14030_15040_25060_33030_34060_38030_39060(SpcRecord spcAdd) {
        return UCom.getDbl(spcAdd.getParam(spcAdd.count,
                11030, 12060, 14030, 15040, 25060, 33030, 34060, 38030, 39060));
    }

    //��������, ��
    public static double to_12050_15050_34051_39020(SpcRecord spcAdd) {
        if (UseUnit.METR.id == spcAdd.artiklRec().getInt(eArtikl.unit)) { //���.�.
            return UCom.getDbl(spcAdd.getParam(0, 12050, 15050, 34050, 34051, 39020)); //��������, ��
        }
        return 0; //spcAdd.width;
    }

    //�����, ��
    public static double to_12065_15045_25040_34070_39070(SpcRecord spcAdd) {
        if (UseUnit.METR.id == spcAdd.artiklRec().getInt(eArtikl.unit)) { //���.�.
            return UCom.getDbl(spcAdd.getParam(spcAdd.width, 12065, 15045, 25040, 34070, 39070)); //�����, �� 
        }
        return spcAdd.width;
    }

    //�����������, [ * ����-� ]
    public static double to_12030_15030_25035_34030_39030(SpcRecord spcAdd) {
        return UCom.getDbl(spcAdd.getParam("1", 12030, 15030, 25035, 34030, 39030));
    }

    //�����������, [ / ����-� ]
    public static double to_12040_15031_25036_34040_39040(SpcRecord spcAdd) {
        return UCom.getDbl(spcAdd.getParam("1", 12040, 15031, 25036, 34040, 39040));
    }

    //Othe
    public static double to_11030_12060_14030_15040_24030_25060_33030_34060_38030_39060(SpcRecord spcAdd) {
        return UCom.getDbl(spcAdd.getParam(spcAdd.quant1,
                11030, 12060, 14030, 15040, 24030, 25060, 33030, 34060, 38030, 39060));
    }

    //������ ����_����_1/����_����_2, �
    public static void to_34077_39077(SpcRecord spcAdd) {
        if (spcAdd.getParam("-361", 34077, 39077).equals("-361") == false) {
            String[] arr = spcAdd.getParam("-1", 34077, 39077).split("/");
            if (arr[0].equals("*") == false) {
                spcAdd.anglCut0 = UCom.getDbl(arr[0]);
            }
            if (arr[1].equals("*") == false) {
                spcAdd.anglCut1 = UCom.getDbl(arr[1]);
            }
        }
    }

    //������� ����������
    public static double to_11070_12070_33078_34078(SpcRecord spcAdd) {
        if ("��".equals(spcAdd.getParam("���", 11070, 12070, 33078, 34078))) {
            return 1;
        } else {
            return spcAdd.count;
        }
    }

    //���� ����
    public static void to_12075_34075_39075(ElemSimple elem5e, SpcRecord spcAdd) {
        String txt = spcAdd.getParam("null", 12075, 34075, 39075);
        if (!"null".equals(txt)) {
            if ("�� �����������".equals(txt)) {
                spcAdd.anglCut0 = elem5e.spcRec.anglCut0;
                spcAdd.anglCut1 = elem5e.spcRec.anglCut1;

            } else if ("���������� (90� x 90�)".equals(txt)) {
                spcAdd.anglCut0 = 90;
                spcAdd.anglCut1 = 90;

            } else if ("���������� (90� x 45�)".equals(txt)) {
                spcAdd.anglCut0 = 90;
                spcAdd.anglCut1 = 45;

            } else if ("���������� (45� x 45�)".equals(txt)) {
                spcAdd.anglCut0 = 45;
                spcAdd.anglCut1 = 45;
            }
        }
    }

    //������ ������� ������ 
    public static void to_40007(SpcRecord spcAdd) {
        if ("��".equals(spcAdd.getParam("null", 40007))) {
            double height = spcAdd.height;
            spcAdd.height = spcAdd.width;
            spcAdd.width = height;
        }
    }

    //��������� ���������� �� ����������
    public static double to_39063(SpcRecord spcAdd) {
        String txt = spcAdd.getParam("null", 39063);
        if (!"null".equals(txt)) {

            if ("�������� ������ �����".equals(txt)) {
                return Math.floor(spcAdd.count);

            } else if ("�������� ������ �����".equals(txt)) {
                return Math.ceil(spcAdd.count);

            } else if ("�������� ������� �����".equals(txt)) {
                return Math.round(spcAdd.count);

            } else if ("�������� ��������� �����".equals(txt)) {
                return Math.round(spcAdd.count) + 1;
            }
        }
        return spcAdd.count;
    }

    //�������� ������/������, �� 
    //�������� �� ������� ������/��������, ��     
    public static void to_40005_40010(SpcRecord spcAdd) {
        if (!"null".equals(spcAdd.getParam("null", 40005))) {
            String[] arr = spcAdd.getParam("null", 40005).split("/");
            spcAdd.width = spcAdd.width + UCom.getDbl(arr[0]);
            spcAdd.height = spcAdd.height + UCom.getDbl(arr[1]);
        } else if (!"null".equals(spcAdd.getParam("null", 40010))) {
            String[] arr = spcAdd.getParam("null", 40010).split("/");
            spcAdd.height = spcAdd.height + UCom.getDbl(arr[0]);
            spcAdd.width = spcAdd.width + UCom.getDbl(arr[1]);
        }
    }

    //���������� ��.
    public static double to_7030_7031_8060_8061_9060_9061(HashMap<Integer, String> mapParam) {
        String numb = getParam(0, mapParam, 7030, 7031, 8060, 8061, 9060, 9061);
        return Double.valueOf(numb);
    }

    //��������, ��
    public static double to_8050(HashMap<Integer, String> mapParam) {
        String numb = getParam(0, mapParam, 8050);
        return Double.valueOf(numb);
    }

    //�����, ��
    public static Double to_8065_8066_9065_9066(HashMap<Integer, String> mapParam) {
        String numb = getParam(0, mapParam, 8065, 8066, 9065, 9066);
        return Double.valueOf(numb);
    }

    //������, ��
    public static Double to_8070_8071_9070_9071(HashMap<Integer, String> mapParam) {
        String numb = getParam("null", mapParam, 8070, 8071, 9070, 9071);
        if (!"null".equals(numb)) {
            return Double.valueOf(numb);
        }
        return null;
    }

    //���� ���� "90�90", "90�45", "45�90", "45�45"
    public static Double to_8075(HashMap<Integer, String> mapParam, int m) {
        String angl = getParam("null", mapParam, 8075);
        if (!"null".equals(angl)) {
            String s[] = angl.split("�");
            return Double.valueOf(s[m]);
        }
        return null;
    }

    public static String getParam(Object def, HashMap<Integer, String> mapParam, int... p) {

        if (mapParam != null) {
            for (int index = 0; index < p.length; ++index) {
                int key = p[index];
                String str = mapParam.get(Integer.valueOf(key));
                if (str != null) {
                    return str;
                }
            }
        }
        return String.valueOf(def);
    }
}
