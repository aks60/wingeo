package builder.script.check;

import builder.script.GsonElem;
import builder.script.GsonRoot;
import static builder.script.GsonScript.rootGson;
import enums.Type;

public final class Bimax {

    public static String systemScript(Integer prj) {

// <editor-fold defaultstate="collapsed" desc="RECTANGL">
        if (prj == 316631) {
            rootGson = new GsonRoot(389458, prj, 1, 54, Type.RECTANGL, "KBE / KBE ������� / 1 ���� / ���������� ������ (���. Z 77)",
                    1009, 1009, 1009, "{ioknaParam: [-9504]}");  //�������� �400);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 950.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1120.0, 950.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1120.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 777001) {
            rootGson = new GsonRoot(389458, prj, 1, 54, Type.RECTANGL, "KBE / KBE ������� / 1 ���� / ���������� ������ (xxx)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1200.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 1200.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            //rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 600.0, 900.0, 600.0));
            rootGson.addElem(new GsonElem(Type.IMPOST, 450.0, 0.0, 450.0, 1200.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508852) { //PS ���������� �������!
            rootGson = new GsonRoot(427640, prj, 1, 237, Type.RECTANGL, "Teplowin 500 / Estetic / 1 ����.  PS ���������� �������!", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1600.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1600.0, 1600.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1600.0, 0.0));

            GsonElem area1 = rootGson.addArea(new GsonElem(Type.AREA));
            rootGson.addElem(new GsonElem(Type.IMPOST, 800.0, 0.0, 800.0, 1600.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{sysfurnID:216, typeOpen:2}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));

            area1.addArea(new GsonElem(Type.STVORKA, "{sysfurnID:1549, typeOpen:5}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));
            area1.addElem(new GsonElem(Type.IMPOST, 0.0, 800.0, 800.0, 800.0));
            area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));

        } else if (prj == 508865) {//PS ��� ���������
            rootGson = new GsonRoot(427653, prj, 3, 41, Type.RECTANGL, "Rehau / Sib / 1 ����", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1700.0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1700.0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 0.0, "{sysprofID: 3055}"));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 550.0, 1300.0, 550.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 550.0, 650.0, 1700.0));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));

        } else if (prj == 508920) {//PS ��� ���������
            rootGson = new GsonRoot(427712, prj, 1, 198, Type.RECTANGL, "Montblanc / Eco / 1 ����. PS ��� ���������", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1750.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1450.0, 1750.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1450.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 550.0, 1450.0, 550.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            area.addElem(new GsonElem(Type.IMPOST, 725.0, 550.0, 725.0, 1750.0));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));

        } else if (prj == 508954) { //PS ��� ��������� 
            rootGson = new GsonRoot(427746, prj, 1, 366, Type.RECTANGL, "Wintech / Isotech 530 (����� �����������) / 1 ����", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601001) {
            rootGson = new GsonRoot(427817, prj, 1, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ����\\���������� ������ (���. Z77)",
                    1009, 10009, 1009, "{ioknaParam: [-9504]}");  //�������� �400);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1300.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 1300.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 0.0))
                    .addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1634}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601002) {
            rootGson = new GsonRoot(427818, prj, 1, 29, Type.RECTANGL, "Montblanc\\Nord\\1 ����", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:3, sysfurnID:860}")).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:860}")).addElem(new GsonElem(Type.GLASS));

//        } else if (prj == -601002) {
//            rootGson = new GsonRoot(427818, prj, 1, 29, Type.RECTANGL, "Montblanc\\Nord\\1 ����", 1009, 10009, 1009);
//            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1363.013698630137))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0));
//
//            rootGson.addElem(new GsonElem(Type.GLASS));
//            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1381.5068493150684));
//            rootGson.addElem(new GsonElem(Type.GLASS));
        } else if (prj == 601003) {
            rootGson = new GsonRoot(427819, prj, 1, 81, Type.RECTANGL, "Darrio\\DARRIO 200\\1 ����", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1440.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1440.0, 400.0));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:389}")).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 720.0, 400.0, 720.0, 1700.0));
            area2.addArea(new GsonElem(Type.STVORKA, "{typeOpen:3, sysfurnID:819}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601004) {
            rootGson = new GsonRoot(427820, prj, 1, 8, Type.RECTANGL, "KBE 58\\����\\���������� ������", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1440.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1440.0, 400.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1634}")).addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 720.0, 400.0, 720.0, 1700.0))
                    .addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:1633}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601005) {
            rootGson = new GsonRoot(427840, prj, 1, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ����\\���������� ������ (���. Z77)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1600.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1600.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1634}"))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 800.0, 0.0, 800.0, 1700.0))
                    .addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:1633}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601006) {
            rootGson = new GsonRoot(427838, prj, 1, 110, Type.RECTANGL, "RAZIO\\RAZIO 58 N\\1 ����", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 0.0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:5746}")); //��� R4x10x4x10x4   

        } else if (prj == 601007) {
            rootGson = new GsonRoot(427842, prj, 1, 87, Type.RECTANGL, "NOVOTEX\\Techno 58\\1 ����", 1009, 10018, 10018);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1100.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1100.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 300.0, 1100.0, 300.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1537}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 550.0, 300.0, 550.0, 1700.0));
            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:1536}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601008) { //, 1200, 1700 28014
            rootGson = new GsonRoot(427848, prj, 1, 99, Type.RECTANGL, "Rehau\\Blitz new\\����", 1009, 28014, 21057);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1200.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1200.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 600.0, 0.0, 600.0, 1700.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:534}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 600.0, 550.0, 1200.0, 550.0));
            area.addArea(new GsonElem(Type.AREA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601009) {
            rootGson = new GsonRoot(427851, prj, 1, 54, Type.RECTANGL, "KBE\\KBE �������\\1 ����\\���������� ������ (���. Z77)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 700.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 700.0, 0.0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:4663}")); //��� 4x12x4x12x4

            //��� ����������� ����� �� ���������� �� ���������. �������� ������ ���������4}
        } else if (prj == 601010) {
            rootGson = new GsonRoot(427852, prj, 1, 54, Type.RECTANGL, "KBE\\KBE �������\\1 ����\\���������� ������ (���. Z77)",
                    1009, 1009, 1009, "{ioknaParam:[-8545]}"); //�������� �������� ��� ps ������������
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA, "{stvorkaBot: {sysprofID: 1121}, stvorkaLef: {sysprofID: 1121}"
                    + ", stvorkaRig: {sysprofID: 1121}, stvorkaTop: {sysprofID: 1121}, typeOpen:1, sysfurnID:2335}")) //,artiklHandl:2159,colorHandl:1009}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:4663}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{stvorkaBot: {sysprofID: 1121}, stvorkaLef: {sysprofID: 1121}"
                    + ", stvorkaRig: {sysprofID: 1121}, stvorkaTop: {sysprofID: 1121}, sysprofID:1121, typeOpen:4, sysfurnID:2916}")) //,artiklHandl:5058,colorHandl:1009}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:4663}"));

        } else if (prj == 700032) {
            rootGson = new GsonRoot(427877, prj, 1, 168, Type.RECTANGL, "Rehau / Termo / 1 ���� / ���������� ������ (���. Z60)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="ARCH">  
        } else if (prj == 498590) {
            rootGson = new GsonRoot(417116, prj, 1, 128, Type.ARCH, "KBE / KBE 76 / 1 ����", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 590.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1760.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1670.0, 1760.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1670.0, 590.0, 590.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 619.001, 1670.0, 619.001));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:2745}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 835.0, 619.001, 835.0, 1760.0));
            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:2744}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 499391) { //PS - ������� ��������
            rootGson = new GsonRoot(417938, prj, 1, 37, Type.ARCH, "Rehau / Delight / 1 ����", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 490.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1990.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1450.0, 1990.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1450.0, 490.0, 490.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 517.0, 1450.0, 517.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:2745}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 725.0, 517.0, 725.0, 1990.0));
            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:2744}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508908) {
            rootGson = new GsonRoot(427696, prj, 1, 17, Type.ARCH, "KBE / KBE 58 / 3 ��������������� ����/����� / ����", 1009, 10010, 10000);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1000.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 1000.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 300.0, 200.0))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == -508908) {
            rootGson = new GsonRoot(427696, 508908, 5, 17, Type.ARCH, "KBE / KBE 58 / 3 ��������������� ����/����� / ����", 1009, 10010, 10000);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 200.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1200.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1200.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 200.0, 200.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 240.0, 1300.0, 240.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 505072) { //��.PS 2015-07 �. (�����������)
            rootGson = new GsonRoot(423775, prj, 1, 4, Type.ARCH, "������������", 1009, 1009, 1009);

            //} else if (prj == 507830) { //��.PS (��� ����� � ������� �� Test ��������)
            //    rootGson = new GsonRoot(426594, prj, 1, 000, Type.ARCH, "KBE / KBE ������ / 1 ���� / ���������� ������ (���. Z77)", 1009, 1009, 1009);
        } else if (prj == 507965) { //��.PS (��� ����� � ������� �� Test ��������)
            rootGson = new GsonRoot(426733, prj, 1, 000, Type.ARCH, "KBE / KBE ������ / 1 ���� / ���������� ������ (���. Z77)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 350.0, 1400 - 1319.94))
                    .addElem(new GsonElem(Type.BOX_SIDE, 90.0, 1400 - 1109.97))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400 - 790.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400 - 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1030.0, 1400 - 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1030.0, 1400 - 790.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 940.0, 1400 - 1109.99))
                    .addElem(new GsonElem(Type.BOX_SIDE, 350.0, 1400 - 1319.94))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 507998) { //��.PS
            rootGson = new GsonRoot(426766, prj, 1, 198, Type.ARCH, "Montblanc / Eco / 1 ����", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1780.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 800.0, 1780.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 800.0, 400.0, 400.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 440.0, 800.0, 440.0));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508035) { //��.PS (� ����������)
            rootGson = new GsonRoot(426804, prj, 1, 99, Type.ARCH, "Rehau / Blitz / 1 ���� / ���������� ������ (���. Z60)", 1009, 1009, 1009);

        } else if (prj == 604009) {
            rootGson = new GsonRoot(427825, prj, 1, 8, Type.ARCH, "KBE\\KBE 58\\1 ����\\���������� ������ (���. Z77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 300.0, 300.0));

            rootGson.addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 343.5, 1300.0, 343.5));
            rootGson.addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604004) {
            rootGson = new GsonRoot(427858, prj, 1, 37, Type.ARCH, "Rehau / Delight / 1 ����", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 650.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 650.0, 650.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 680.0, 1300.0, 680.0, "{sysprofID:3246}"));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 680.0, 650.0, 1700.0, "{sysprofID:3246}"));
            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:91}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604005) {
            rootGson = new GsonRoot(427833, prj, 1, 135, Type.ARCH, "Wintech\\Termotech 742\\1 ����", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 300.0, 300.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 342.0, 1300.0, 342.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:2745}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 350.0, 650.0, 1500.0));
            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:2744}"))
                    .addElem(new GsonElem(Type.GLASS));

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="TRAPEZE"> 
        } else if (prj == 506642) {
            rootGson = new GsonRoot(425392, prj, 1, 54, Type.TRAPEZE, "KBE / KBE ������� / 1 ���� / ���������� ������ (���. Z 77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1300.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1000.0, 1300.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1000.0, 350.0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID: 5241}"));

        } else if (prj == 605001) {
            rootGson = new GsonRoot(427850, prj, 1, 8, Type.TRAPEZE, "KBE\\KBE 58\\1 ����\\*���������� ������ (���. Z77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 300.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1300.0, 400.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508916) {
            rootGson = new GsonRoot(427708, prj, 2, 8, Type.TRAPEZE, "KBE 58\\����\\���������� ������", 1009, 10005, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 400.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 450.0, 900.0, 450.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:1633}")).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="DOOR">
        } else if (prj == 508841) {
            rootGson = new GsonRoot(427629, prj, 2, 8, Type.DOOR, "KBE / KBE ������� / 6 ������� ����� / ����� ������", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 2100.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 2100.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 0.0));

            GsonElem stv = rootGson.addArea(new GsonElem(Type.STVORKA));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            stv.addElem(new GsonElem(Type.IMPOST, 0.0, 1300.0, 900.0, 1300.0));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 700009) {
            rootGson = new GsonRoot(427847, prj, 2, 330, Type.DOOR, "Darrio / ����� DARRIO / ����� ������", 1009, 10004, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 2000.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 2000.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 0.0));

            GsonElem stv = rootGson.addArea(new GsonElem(Type.STVORKA));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            stv.addElem(new GsonElem(Type.IMPOST, 0.0, 1400.0, 900.0, 1400.0));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 700014) {
            rootGson = new GsonRoot(427856, prj, 1, 66, Type.DOOR, "Rehau\\Brilliant\\4 ����� �������\\����� ������", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 2100.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 2100.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 900.0, 0.0));

            GsonElem stv = rootGson.addArea(new GsonElem(Type.STVORKA));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            stv.addElem(new GsonElem(Type.IMPOST, 0.0, 1500.0, 900.0, 1500.0));
            stv.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="�� �������� ��. Test.param()">  
        } else if (prj == -501006) {
            rootGson = new GsonRoot(427820, prj, 1, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ����\\���������� ������ (���. Z77)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1000.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1000.0, 1000.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1000.0, 0.0));

            GsonElem area1 = rootGson.addArea(new GsonElem(Type.AREA));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 500.0, 1000.0, 500.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

            area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area1.addElem(new GsonElem(Type.IMPOST, 500.0, 500.0, 500.0, 1000.0))
                    .addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == -601006) {
            rootGson = new GsonRoot(427838, prj, 1, 110, Type.RECTANGL, "RAZIO\\RAZIO 58 N\\1 ����", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1800.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1600.0, 1800.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1600.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 500.0, 1600.0, 500.0));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 0.0, 1000.0, 1600.0, 1000.0));
            GsonElem area3 = area2.addArea(new GsonElem(Type.AREA));

            area3.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area3.addElem(new GsonElem(Type.IMPOST, 0.0, 1500.0, 1600.0, 1500.0));
            area3.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == -601004) {
            rootGson = new GsonRoot(427820, prj, 1, 8, Type.RECTANGL, "KBE 58\\����\\���������� ������", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1440.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1440.0, 400.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1634, colorKnob: 1010}")).addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 720.0, 400.0, 720.0, 1700.0))
                    .addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:1633}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == -604005) {
            rootGson = new GsonRoot(427833, prj, 1, 135, Type.ARCH, "Wintech\\Termotech 742\\1 ����", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 300.0, 300.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 350.0, 1300.0, 350.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:2745}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 350.0, 650.0, 1500.0));
            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:2744}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == -700027) {  //����������
            rootGson = new GsonRoot(427872, prj, 1, 198, Type.RECTANGL, "Montblanc / Eco / 1 ���� (������)", 1009, 1009, 1009, "{ioknaParam:[-8252]}"); //�������� ����������������, �������� ��� ps ������������
            rootGson.addElem(new GsonElem(Type.BOX_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.BOX_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:2915}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:3293}"));
            rootGson.addElem(new GsonElem(Type.SHTULP, 450.0, 0.0, 450.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:2913}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:4267}"));
        } // </editor-fold>     
        else {
            return null;
        }
        return rootGson.toJson();
    }
}
