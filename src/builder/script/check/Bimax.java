package builder.script.check;

import builder.script.GsonElem;
import builder.script.GsonRoot;
import static builder.script.GsonScript.rootGson;
import enums.Type;

public final class Bimax {

    public static String systemScript(Integer prj) {
// <editor-fold defaultstate="collapsed" desc="RECTANGL">

        if (prj == 508852) { //PS перепутаны системы!
            rootGson = new GsonRoot(427640, prj, 1, 237, Type.RECTANGL, "Teplowin 500 / Estetic / 1 ОКНА.  PS перепутаны системы!", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1600.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 1600.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 0.0));

            GsonElem area1 = rootGson.addArea(new GsonElem(Type.AREA));
            rootGson.addElem(new GsonElem(Type.IMPOST, 800.0, 0.0, 800.0, 1600.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{sysfurnID:216, typeOpen:2}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));

            area1.addArea(new GsonElem(Type.STVORKA, "{sysfurnID:1549, typeOpen:5}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));
            area1.addElem(new GsonElem(Type.IMPOST, 0.0, 800.0, 800.0, 800.0));
            area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));

        } else if (prj == 508865) {//PS нет стоимости
            rootGson = new GsonRoot(427653, prj, 3, 41, Type.RECTANGL, "Rehau / Sib / 1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1700.0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1700.0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0, "{sysprofID: 3055}"));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 550.0, 1300.0, 550.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 550.0, 650.0, 1700.0));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));

        } else if (prj == 508920) {//PS нет стоимости
            rootGson = new GsonRoot(427712, prj, 1, 198, Type.RECTANGL, "Montblanc / Eco / 1 ОКНА. PS нет стоимости", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1750.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1450.0, 1750.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1450.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 550.0, 1450.0, 550.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            area.addElem(new GsonElem(Type.IMPOST, 725.0, 550.0, 725.0, 1750.0));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));

        } else if (prj == 508954) { //PS нет стоимости 
            rootGson = new GsonRoot(427746, prj, 1, 366, Type.RECTANGL, "Wintech / Isotech 530 (серый уплотнитель) / 1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601001) {
            rootGson = new GsonRoot(427817, prj, 1, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)",
                    1009, 10009, 1009, "{ioknaParam: [-9504]}");  //маскитка Р400);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1634}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601002) {
            rootGson = new GsonRoot(427818, prj, 1, 29, Type.RECTANGL, "Montblanc\\Nord\\1 ОКНА", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:3, sysfurnID:860}")).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:860}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601003) {
            rootGson = new GsonRoot(427819, prj, 1, 81, Type.RECTANGL, "Darrio\\DARRIO 200\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1440.0, 400.0));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:389}")).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 720.0, 400.0, 720.0, 1700.0));
            area2.addArea(new GsonElem(Type.STVORKA, "{typeOpen:3, sysfurnID:819}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601005) {
            rootGson = new GsonRoot(427840, prj, 1, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1634}"))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 800.0, 0.0, 800.0, 1700.0))
                    .addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:1633}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601006) {
            rootGson = new GsonRoot(427838, prj, 1, 110, Type.RECTANGL, "RAZIO\\RAZIO 58 N\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:5746}")); //или R4x10x4x10x4   

        } else if (prj == 601007) {
            rootGson = new GsonRoot(427842, prj, 1, 87, Type.RECTANGL, "NOVOTEX\\Techno 58\\1 ОКНА", 1009, 10018, 10018);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1100.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1100.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 300.0, 1100.0, 300.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1537}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 550.0, 300.0, 550.0, 1700.0));
            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:1536}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601008) { //, 1200, 1700 28014
            rootGson = new GsonRoot(427848, prj, 1, 99, Type.RECTANGL, "Rehau\\Blitz new\\ОКНА", 1009, 28014, 21057);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1200.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1200.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 600.0, 0.0, 600.0, 1700.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:534}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 600.0, 550.0, 1200.0, 550.0));
            area.addArea(new GsonElem(Type.AREA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601009) {
            rootGson = new GsonRoot(427851, prj, 1, 54, Type.RECTANGL, "KBE\\KBE Эксперт\\1 ОКНА\\Открывание внутрь (ств. Z77)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 700.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 700.0, 0.0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:4663}")); //или 4x12x4x12x4

            //Тут просочилась ручка не подходящая по параметру. Возможно ошибка ПрофСтроя4}
        } else if (prj == 601010) {
            rootGson = new GsonRoot(427852, prj, 1, 54, Type.RECTANGL, "KBE\\KBE Эксперт\\1 ОКНА\\Открывание внутрь (ств. Z77)",
                    1009, 1009, 1009, "{ioknaParam:[-8545]}"); //параметр подогнал для ps спецификации
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA, "{stvorkaBot: {sysprofID: 1121}, stvorkaLef: {sysprofID: 1121}"
                    + ", stvorkaRig: {sysprofID: 1121}, stvorkaTop: {sysprofID: 1121}, typeOpen:1, sysfurnID:2335}")) //,artiklHandl:2159,colorHandl:1009}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:4663}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{stvorkaBot: {sysprofID: 1121}, stvorkaLef: {sysprofID: 1121}"
                    + ", stvorkaRig: {sysprofID: 1121}, stvorkaTop: {sysprofID: 1121}, sysprofID:1121, typeOpen:4, sysfurnID:2916}")) //,artiklHandl:5058,colorHandl:1009}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:4663}"));

        } else if (prj == 700032) {
            rootGson = new GsonRoot(427877, prj, 1, 168, Type.RECTANGL, "Rehau / Termo / 1 ОКНА / Открывание внутрь (ств. Z60)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 0.0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="ARCH">             
        } else if (prj == 508908) {
            rootGson = new GsonRoot(427696, prj, 1, 17, Type.ARCH, "KBE / KBE 58 / 3 НЕПРЯМОУГОЛЬНЫЕ ОКНА/ДВЕРИ / АРКИ", 1009, 10010, 10000);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 300.0, 200.0))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == -508908) {
            rootGson = new GsonRoot(427696, 508908, 5, 17, Type.ARCH, "KBE / KBE 58 / 3 НЕПРЯМОУГОЛЬНЫЕ ОКНА/ДВЕРИ / АРКИ", 1009, 10010, 10000);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 200.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1200.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1200.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 200.0, 200.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 240.0, 1300.0, 240.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 505072) { //СМ.PS 2015-07 г. (стеклопакет)
            rootGson = new GsonRoot(423775, prj, 1, 4, Type.ARCH, "Стеклопакеты", 1009, 1009, 1009);

        //} else if (prj == 507830) { //СМ.PS (Нет ветки в системе но Test неплохой)
        //    rootGson = new GsonRoot(426594, prj, 1, 000, Type.ARCH, "KBE / KBE Эталон / 1 ОКНА / Открывание внутрь (ств. Z77)", 1009, 1009, 1009);

        } else if (prj == 507965) { //СМ.PS (Нет ветки в системе но Test неплохой)
            rootGson = new GsonRoot(426733, prj, 1, 000, Type.ARCH, "KBE / KBE Эталон / 1 ОКНА / Открывание внутрь (ств. Z77)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 350.0, 1400 - 1319.94))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 90.0, 1400 - 1109.97))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400 - 790.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400 - 0.0))            
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1030.0, 1400 - 0.0))            
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1030.0, 1400 - 790.0))            
                    .addElem(new GsonElem(Type.FRAME_SIDE, 940.0, 1400 - 1109.99))            
                    .addElem(new GsonElem(Type.FRAME_SIDE, 350.0, 1400 - 1319.94))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 507998) { //СМ.PS
            rootGson = new GsonRoot(426766, prj, 1, 198, Type.ARCH, "Montblanc / Eco / 1 ОКНА", 1009, 1009, 1009);

        } else if (prj == 508035) { //СМ.PS (с раскладкой)
            rootGson = new GsonRoot(426804, prj, 1, 99, Type.ARCH, "Rehau / Blitz / 1 ОКНА / Открывание внутрь (ств. Z60)", 1009, 1009, 1009);
            //КОНЕЦ  2015-07 г.    

        } else if (prj == 604009) {
            rootGson = new GsonRoot(427825, prj, 1, 8, Type.ARCH, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0));

            rootGson.addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 343.5, 1300.0, 343.5));
            rootGson.addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604004) {
            rootGson = new GsonRoot(427858, prj, 1, 37, Type.ARCH, "Rehau / Delight / 1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 690.0, 1300.0, 690.0, "{sysprofID:3246}"));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 650.0, 650.0, 1700.0, "{sysprofID:3246}"));
            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:91}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604005) {
            rootGson = new GsonRoot(427833, prj, 1, 135, Type.ARCH, "Wintech\\Termotech 742\\1 ОКНА", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0));

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
            rootGson = new GsonRoot(425392, prj, 1, 54, Type.TRAPEZE, "KBE / KBE Эксперт / 1 ОКНА / Открывание внутрь (ств. Z 77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 350.0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID: 5241}"));

        } else if (prj == 605001) {
            rootGson = new GsonRoot(427850, prj, 1, 8, Type.TRAPEZE, "KBE\\KBE 58\\1 ОКНА\\*Открывание внутрь (ств. Z77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1300.0, 400.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="DOOR">
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="ТЕСТ">       
        } else if (prj == -601006) {
            rootGson = new GsonRoot(427838, prj, 1, 110, Type.RECTANGL, "RAZIO\\RAZIO 58 N\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1800.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 1800.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 500.0, 1600.0, 500.0));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 0.0, 1000.0, 1600.0, 1000.0));
            GsonElem area3 = area2.addArea(new GsonElem(Type.AREA));

            area3.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area3.addElem(new GsonElem(Type.IMPOST, 0.0, 1500.0, 1600.0, 1500.0));
            area3.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601004) {
            rootGson = new GsonRoot(427820, prj, 1, 8, Type.RECTANGL, "KBE 58\\ОКНА\\Открывание внутрь", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1440.0, 400.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1634}")).addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 720.0, 400.0, 720.0, 1700.0))
                    .addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:1633}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == -604005) {
            rootGson = new GsonRoot(427833, prj, 1, 135, Type.ARCH, "Wintech\\Termotech 742\\1 ОКНА", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 350.0, 1300.0, 350.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:2745}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 350.0, 650.0, 1500.0));
            area.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:2744}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 700027) {  //штульповое
            rootGson = new GsonRoot(427872, prj, 1, 198, Type.RECTANGL, "Montblanc / Eco / 1 ОКНА (штульп)", 1009, 1009, 1009, "{ioknaParam:[-8252]}"); //параметр недействительный, подогнал для ps спецификации
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0));

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
