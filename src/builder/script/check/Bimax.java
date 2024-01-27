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
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1600))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600, 1600))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600, 0));

            GsonElem area1 = rootGson.addArea(new GsonElem(Type.AREA));
            rootGson.addElem(new GsonElem(Type.IMPOST, 800, 0, 800, 1600));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{sysfurnID:216, typeOpen:2}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));

            area1.addArea(new GsonElem(Type.STVORKA, "{sysfurnID:1549, typeOpen:5}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));
            area1.addElem(new GsonElem(Type.IMPOST, 0, 800, 800, 800));
            area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));

        } else if (prj == 508865) {//PS без стоимости
            rootGson = new GsonRoot(427653, prj, 3, 41, Type.RECTANGL, "Rehau / Sib / 1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1700, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 1700, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 0, "{sysprofID: 3055}"));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0, 550, 1300, 550));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            area.addElem(new GsonElem(Type.IMPOST, 650, 550, 650, 1700));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));

        } else if (prj == 508920) {//PS без стоимости
            rootGson = new GsonRoot(427712, prj, 1, 198, Type.RECTANGL, "Montblanc / Eco / 1 ОКНА. PS без стоимости", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1750))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1450, 1750))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1450, 0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0, 550, 1450, 550));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            area.addElem(new GsonElem(Type.IMPOST, 725, 550, 725, 1750));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));

        } else if (prj == 508954) { //PS нет стоимости 
            rootGson = new GsonRoot(427746, prj, 1, 366, Type.RECTANGL, "Wintech / Isotech 530 (серый уплотнитель) / 1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1400))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 1400))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650, 0, 650, 1400));
            rootGson.addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601001) {
            rootGson = new GsonRoot(427817, prj, 1, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)",
                    1009, 10009, 1009, "{ioknaParam: [-9504]}");  //маскитка Р400);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1300))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900, 1300))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900, 0))
                    .addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1634}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601002) {
            rootGson = new GsonRoot(427818, prj, 1, 29, Type.RECTANGL, "Montblanc\\Nord\\1 ОКНА", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1400))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 1400))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 0));

            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:3, sysfurnID:860}")).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650, 0, 650, 1400));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:860}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601003) {
            rootGson = new GsonRoot(427819, prj, 1, 81, Type.RECTANGL, "Darrio\\DARRIO 200\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1700))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440, 1700))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440, 0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0, 400, 1440, 400));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:389}")).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 720, 400, 720, 1700));
            area2.addArea(new GsonElem(Type.STVORKA, "{typeOpen:3, sysfurnID:819}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601006) {
            rootGson = new GsonRoot(427838, prj, 1, 110, Type.RECTANGL, "RAZIO\\RAZIO 58 N\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1400))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900, 1400))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900, 0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:5746}")); //или R4x10x4x10x4   

        } else if (prj == 700032) {
            rootGson = new GsonRoot(427877, prj, 1, 168, Type.RECTANGL, "Rehau / Termo / 1 ОКНА / Открывание внутрь (ств. Z60)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1400))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 1400))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 0));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650, 0, 650, 1400));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="ARCH"> 

        } else if (prj == 604004) {
            rootGson = new GsonRoot(427858, prj, 1, 37, Type.ARCH, "Rehau\\Delight\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 650))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1050))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 1050))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 650, 650));

            rootGson.addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0, 650, 1300, 650, "{sysprofID:3246}"));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, 650, 1700, 650, 400));
            area.addElem(new GsonElem(Type.IMPOST, "{sysprofID:3246}"));
            area.addArea(new GsonElem(Type.AREA)).addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:91}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508908) {
            rootGson = new GsonRoot(427696, prj, 1, 17, Type.ARCH, "KBE / KBE 58 / 3 НЕПРЯМОУГОЛЬНЫЕ ОКНА/ДВЕРИ / АРКИ", 1009, 10010, 10000);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 300))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1000))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900, 1000))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900, 300, 200))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == -508908) {
            rootGson = new GsonRoot(427696, 508908, 5, 17, Type.ARCH, "KBE / KBE 58 / 3 НЕПРЯМОУГОЛЬНЫЕ ОКНА/ДВЕРИ / АРКИ", 1009, 10010, 10000);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 200))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1200))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 1200))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 200, 200));

            rootGson.addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0, 240, 1300, 240));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508035) { //СМ.PS
            rootGson = new GsonRoot(426804, prj, 1, 99, Type.ARCH, "Rehau / Blitz / 1 ОКНА / Открывание внутрь (ств. Z60)", 1009, 1009, 1009);

        } else if (prj == 507998) { //СМ.PS
            rootGson = new GsonRoot(426766, prj, 1, 198, Type.ARCH, "Montblanc / Eco / 1 ОКНА", 1009, 1009, 1009);

        } else if (prj == 507830) { //СМ.PS (Нет ветки в системе но Test неплохой)
            rootGson = new GsonRoot(426594, prj, 1, 000, Type.ARCH, "KBE / KBE Эталон / 1 ОКНА / Открывание внутрь (ств. Z77)", 1009, 1009, 1009);

        } else if (prj == 507965) { //СМ.PS (Нет ветки в системе но Test неплохой)
            rootGson = new GsonRoot(426733, prj, 1, 000, Type.ARCH, "KBE / KBE Эталон / 1 ОКНА / Открывание внутрь (ств. Z77)", 1009, 1009, 1009);

        } else if (prj == 508035) { //СМ.PS
            rootGson = new GsonRoot(424982, prj, 1, 198, Type.ARCH, "Montblanc / Eco / 1 ОКНА", 1009, 1009, 1009);

        } else if (prj == 505072) { //СМ.PS 2015-07 г.
            rootGson = new GsonRoot(423775, prj, 1, 4, Type.ARCH, "Стеклопакеты", 1009, 1009, 1009);

            //КОНЕЦ  2015-07 г.    
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="TRAPEZE"> 
        } else if (prj == 506642) {
            rootGson = new GsonRoot(425392, prj, 1, 54, Type.TRAPEZE, "KBE / KBE Эксперт / 1 ОКНА / Открывание внутрь (ств. Z 77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1300))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000, 1300))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000, 350))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID: 5241}"));

        } else if (prj == 605001) {
            rootGson = new GsonRoot(427850, prj, 1, 8, Type.TRAPEZE, "KBE\\KBE 58\\1 ОКНА\\*Открывание внутрь (ств. Z77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1500))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 1500))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300, 300));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0, 400, 1300, 400));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="DOOR">
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="ТЕСТ">       
        } else if (prj == 111) {  // -601006) {
            rootGson = new GsonRoot(427838, prj, 1, 110, Type.RECTANGL, "RAZIO\\RAZIO 58 N\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0, 0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0, 1800))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600, 1800))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600, 0));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0, 500, 1600, 500));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 0, 1000, 1600, 1000));
            GsonElem area3 = area2.addArea(new GsonElem(Type.AREA));

            area3.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area3.addElem(new GsonElem(Type.IMPOST, 0, 1500, 1600, 1500));
            area3.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } // </editor-fold>     
        else {
            return null;
        }
        return rootGson.toJson();
    }
}
