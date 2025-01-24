package builder.script;

import builder.script.check.Bimax;
import common.ePref;
import enums.Type;
import java.util.List;

//������ �����������. ����������� � ������� SYSMODEL.
public class GsonScript {

    public static GsonRoot rootGson = null;

    public static String modelScript(Integer prj) {

// <editor-fold defaultstate="collapsed" desc="RECTANGL">
        if (prj == 501006) {
            rootGson = new GsonRoot(Type.RECTANGL, "KBE\\KBE 58\\1 ����\\���������� ������ (���. Z77)");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 0.0));

            GsonElem area1 = rootGson.addArea(new GsonElem(Type.AREA));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 500.0, 1000.0, 500.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

            area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area1.addElem(new GsonElem(Type.IMPOST, 500.0, 500.0, 500.0, 1000.0))
                    .addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508852) { //PS ���������� �������!
            rootGson = new GsonRoot(Type.RECTANGL, "Teplowin 500 / Estetic / 1 ����.  PS ���������� �������!");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1600.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 1600.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 0.0));

            GsonElem area1 = rootGson.addArea(new GsonElem(Type.AREA));
            rootGson.addElem(new GsonElem(Type.IMPOST, 800.0, 0., 800.0, 1600.0));
            rootGson.addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));

            area1.addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));
            area1.addElem(new GsonElem(Type.IMPOST, 0.0, 800.0, 800.0, 800.0));
            area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508920) {
            rootGson = new GsonRoot(Type.RECTANGL, "Montblanc / Eco / 1 ����. PS ��� ���������");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1750.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1450.0, 1750.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1450.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 550.0, 1450.0, 550.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 725.0, 550.0, 725.0, 1750.0));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601001) {
            rootGson = new GsonRoot(Type.RECTANGL, "KBE\\KBE 58\\1 ����\\���������� ������ (���. Z77)");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601002) {
            rootGson = new GsonRoot(Type.RECTANGL, "Montblanc\\Nord\\1 ����");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601006) {
            rootGson = new GsonRoot(Type.RECTANGL, "RAZIO \\ RAZIO 58 \\ 1 ����");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601003) {
            rootGson = new GsonRoot(Type.RECTANGL, "Darrio\\DARRIO 200\\1 ����");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1440.0, 400.0));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 720.0, 400.0, 720.0, 1700.0));
            area2.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601004) {
            rootGson = new GsonRoot(Type.RECTANGL, "KBE 58\\����\\���������� ������");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1440.0, 400.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 720.0, 1300.0, 720.0, 1700.0))
                    .addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>   
// <editor-fold defaultstate="collapsed" desc="ARCH">
        } else if (prj == 507998) {
            rootGson = new GsonRoot(Type.ARCH, "Montblanc / Eco / 1 ����");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1780.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 800.0, 1780.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 800.0, 400.0, 400.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 440.0, 800.0, 440.0));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604004) {
            rootGson = new GsonRoot(Type.ARCH, "Rehau / Delight / 1 ����");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0));
            //.addElem(new GsonElem(Type.GLASS));

            rootGson.addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 690.0, 1300.0, 690.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 650.0, 650.0, 1700.0));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604005) {
            rootGson = new GsonRoot(Type.ARCH, "Wintech\\Termotech 742\\1 ����");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 350.0, 1300.0, 350.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 350.0, 650.0, 1500.0));
            area.addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="TRAPEZE">
        } else if (prj == 506642) { //�������� ��� �������
            rootGson = new GsonRoot(Type.TRAPEZE, "KBE / KBE ������� / 1 ���� / ���������� ������ (���. Z 77)");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 350.0))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 605001) {
            rootGson = new GsonRoot(Type.TRAPEZE, "KBE\\KBE 58\\1 ����\\*���������� ������ (���. Z77)");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1300.0, 400.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508916) {
            rootGson = new GsonRoot(Type.TRAPEZE, "KBE 58\\����\\���������� ������");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 400.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 450.0, 900.0, 450.0));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="DOOR">
        } else if (prj == 508841) {
            rootGson = new GsonRoot(Type.DOOR, "KBE / KBE ������� / 6 ������� ����� / ����� ������");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 2100.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 2100.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0));

            GsonElem stv = rootGson.addArea(new GsonElem(Type.STVORKA));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            stv.addElem(new GsonElem(Type.IMPOST, 0.0, 1300.0, 900.0, 1300.0));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 700009) {
            rootGson = new GsonRoot(Type.DOOR, "Darrio / ����� DARRIO / ����� ������");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 2000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 2000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0));

            GsonElem stv = rootGson.addArea(new GsonElem(Type.STVORKA));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            stv.addElem(new GsonElem(Type.IMPOST, 0.0, 1400.0, 900.0, 1400.0));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 700014) {
            rootGson = new GsonRoot(Type.DOOR, "Rehau\\Brilliant\\4 ����� �������\\����� ������");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 2100.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 2100.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0));

            GsonElem stv = rootGson.addArea(new GsonElem(Type.STVORKA));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            stv.addElem(new GsonElem(Type.IMPOST, 0.0, 1500.0, 900.0, 1500.0));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="����"> 
        } else if (prj == 1043598818) {
// </editor-fold>  
        } else {
            return null;
        }
        return rootGson.toJson();
    }

    public static List<Integer> modelList(String scale) {
        String base_name = (ePref.base_num.getProp().equals("1")) ? ePref.base1.getProp()
                : (ePref.base_num.getProp().equals("2")) ? ePref.base2.getProp() : ePref.base3.getProp();

        if (base_name.toLowerCase().contains("sial3")) {
            return List.of(601001, 601002, 601003, 601004, 601007, 601008);

        } else if (base_name.toLowerCase().contains("alutech3")) {
            return List.of(601001, 601002, 601003, 601004);

        } else if (base_name.toLowerCase().contains("alutex3")) {
            return List.of(4);

        } else if (base_name.toLowerCase().contains("bimax")) {
            return ("max".equals(scale)) ? List.of(
                    508807, 508809, 508966, //��������
                    601001, 601002, 601003, 601004, 601005, 601006, 601007, 601008, 601009, 601010, //�������������
                    700027, 604004, 604005, 604006, 604007, 604008, 604009, 604010, //����
                    508983, 506642, -506642, 605001, -605001, 508916, -508916, 508945, //��������  
                    508841, 700009, 700014) //�����
                    : List.of(501006, 508852, 508920, 601001, 601002, 601006, 601003, 601004, 507998, 604004, 604005,//�������������
                            507998, 604004, 604005, //����
                            506642, 605001, 508916, //�������� 
                            508841, 700009, 700014); //�����

        } else if (base_name.toLowerCase().contains("vidnal.fdb")) {
            return List.of(26);

        } else if (base_name.toLowerCase().contains("krauss")) {
            return null;

        } else if (base_name.toLowerCase().contains("sokol")) {
            return List.of(1);
        }
        return null;
    }

    public static List<Integer> systemList(String scale) {
        String base_name = (ePref.base_num.getProp().equals("1")) ? ePref.base1.getProp()
                : (ePref.base_num.getProp().equals("2")) ? ePref.base2.getProp() : ePref.base3.getProp();

        if (base_name.toLowerCase().contains("sial3")) {
            return List.of(601001, 601002, 601003, 601004, 601007, 601008);

        } else if (base_name.toLowerCase().contains("alutech3")) {
            return List.of(601001, 601002, 601003, 601004);

        } else if (base_name.toLowerCase().contains("alutex3")) {
            return List.of(4);

        } else if (base_name.toLowerCase().contains("bimax")) {
            return ("max".equals(scale)) ? List.of(
                    508807, 508809, 508966, //��������
                    601001, 601002, 601003, 601004, 601005, 601006, 601007, 601008, 601009, 601010, //������������� ����
                    700027, 604004, 604005, 604006, 604007, 604008, 604009, 604010, //����
                    506642, -506642, 605001, 508916, 508945, 508841, 700009, 700014) //��������, �����
                    : List.of(601001, 601002, 601003, 601004, 601005, 601006, 601007, 601008, 601009, 601010, 604005);
//                    : List.of(501006, 508852, 508920, 601001, 601002, 601006, 601003, 601004, 507998, 604004, 604005,//�������������
//                            507998, 604004, 604005, //����
//                            506642, 605001, 508916, //�������� 
//                            508841, 700009, 700014); //�����            

        } else if (base_name.toLowerCase().contains("vidnal.fdb")) {
            return List.of(26);

        } else if (base_name.toLowerCase().contains("krauss")) {
            return null;

        } else if (base_name.toLowerCase().contains("sokol")) {
            return List.of(1);
        }
        return null;
    }

    public static String scriptPath(Integer prj) {
        String base_name = (ePref.base_num.getProp().equals("1")) ? ePref.base1.getProp()
                : (ePref.base_num.getProp().equals("2")) ? ePref.base2.getProp() : ePref.base3.getProp();

        if (base_name.toLowerCase().contains("sial3")) {
            //return Sial3.script(prj);

        } else if (base_name.toLowerCase().contains("alutex3")) {
            //return Alutex3.script(prj);

        } else if (base_name.toLowerCase().contains("alutech3")) {
            //return Alutech3.script(prj);

        } else if (base_name.toLowerCase().contains("bimax")) {
            return Bimax.systemScript(prj);

        } else if (base_name.toLowerCase().contains("binet")) {
            return Bimax.systemScript(prj);

        } else if (base_name.toLowerCase().contains("vidnal")) {
            //return Vidnal.script(prj);

        } else if (base_name.toLowerCase().contains("krauss")) {
            //return Krauss.script(prj);

        } else if (base_name.toLowerCase().contains("sokol")) {
            //return Sokol.script(prj);
        }
        return null;
    }

    public static String filePath() {
        String base_name = (ePref.base_num.getProp().equals("1")) ? ePref.base1.getProp()
                : (ePref.base_num.getProp().equals("2")) ? ePref.base2.getProp() : ePref.base3.getProp();

        if (base_name.toLowerCase().contains("sial3.fdb")) {
            return "D:\\Okna\\Database\\ps3\\sial3b.fdb";

        } else if (base_name.toLowerCase().contains("alutech3.fdb")) {
            return "D:\\Okna\\Database\\ps3\\alutech3.FDB";

        } else if (base_name.toLowerCase().contains("alutex3.fdb")) {
            return "D:\\Okna\\Database\\ps3\\alutex3.FDB";

        } else if (base_name.toLowerCase().contains("bimax.fdb")) {
            return "D:\\Okna\\Database\\ps4\\ITEST.FDB";

        } else if (base_name.toLowerCase().contains("vidnal.fdb")) {
            return "D:\\Okna\\Database\\ps4\\vidnal.fdb";

        } else if (base_name.toLowerCase().contains("krauss.fdb")) {
            return "D:\\Okna\\Database\\ps4\\krauss.fdb";

        } else if (base_name.toLowerCase().contains("sokol.fdb")) {
            return "D:\\Okna\\Database\\ps4\\sokol.fdb";
        }
        return null;
    }
}
