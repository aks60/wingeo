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
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1600.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 1600.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 0.0));

            GsonElem area1 = rootGson.addArea(new GsonElem(Type.AREA));
            rootGson.addElem(new GsonElem(Type.IMPOST, 800.0, 0.0, 800.0, 1600.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{sysfurnID:216, typeOpen:2}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));

            area1.addArea(new GsonElem(Type.STVORKA, "{sysfurnID:1549, typeOpen:5}"))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));
            area1.addElem(new GsonElem(Type.IMPOST, .0, 800.0, 800.0, 800.0));
            area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID:715}"));

        } else if (prj == 508865) {//PS без стоимости
            rootGson = new GsonRoot(427653, prj, 3, 41, Type.RECTANGL, "Rehau / Sib / 1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1700.0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1700.0, "{sysprofID: 3055}"))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0, "{sysprofID: 3055}"));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, .0, 550.0, 1300.0, 550.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            area.addElem(new GsonElem(Type.IMPOST, 650.0, 550.0, 650.0, 1700.0));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));

        } else if (prj == 508920) {//PS без стоимости
            rootGson = new GsonRoot(427712, prj, 1, 198, Type.RECTANGL, "Montblanc / Eco / 1 ОКНА. PS без стоимости", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1750.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1450.0, 1750.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1450.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            rootGson.addElem(new GsonElem(Type.IMPOST, .0, 550.0, 1450.0, 550.0));
            GsonElem area = rootGson.addArea(new GsonElem(Type.AREA));

            area.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));
            area.addElem(new GsonElem(Type.IMPOST, 725.0, 550.0, 725.0, 1750.0));
            area.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS, "{artglasID: 4267}"));

        } else if (prj == 508954) { //PS нет стоимости 
            rootGson = new GsonRoot(427746, prj, 1, 366, Type.RECTANGL, "Wintech / Isotech 530 (серый уплотнитель) / 1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, .0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601001) {
            rootGson = new GsonRoot(427817, prj, 1, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)",
                    1009, 10009, 1009, "{ioknaParam: [-9504]}");  //маскитка Р400);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:1634}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601002) {
            rootGson = new GsonRoot(427818, prj, 1, 29, Type.RECTANGL, "Montblanc\\Nord\\1 ОКНА", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, .0));

            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:3, sysfurnID:860}")).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, .0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA, "{typeOpen:4, sysfurnID:860}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601003) {
            rootGson = new GsonRoot(427819, prj, 1, 81, Type.RECTANGL, "Darrio\\DARRIO 200\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, .0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1440.0, 400.0));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA, "{typeOpen:1, sysfurnID:389}")).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 720.0, 400.0, 720.0, 1700.0));
            area2.addArea(new GsonElem(Type.STVORKA, "{typeOpen:3, sysfurnID:819}")).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601006) {
            rootGson = new GsonRoot(427838, prj, 1, 110, Type.RECTANGL, "RAZIO\\RAZIO 58 N\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:5746}")); //или R4x10x4x10x4   

        } else if (prj == 700032) {
            rootGson = new GsonRoot(427877, prj, 1, 168, Type.RECTANGL, "Rehau / Termo / 1 ОКНА / Открывание внутрь (ств. Z60)", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, .0));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, .0, 650.0, 1400.0));
            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="ARCH"> 
        } else if (prj == 604009) {
            rootGson = new GsonRoot(427825, prj, 1, 8, Type.ARCH, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, 300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0, 300.0))
                    .addElem(new GsonElem(Type.GLASS));

//            rootGson.addElem(new GsonElem(Type.GLASS)).addElem(new GsonElem(Type.GLASS));
//            rootGson.addElem(new GsonElem(Type.IMPOST));
//            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="TRAPEZE"> 
        } else if (prj == 506642) {
            rootGson = new GsonRoot(425392, prj, 1, 54, Type.TRAPEZE, "KBE / KBE Эксперт / 1 ОКНА / Открывание внутрь (ств. Z 77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 350.0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID: 5241}"));

        } else if (prj == 605001) {
            rootGson = new GsonRoot(427850, prj, 1, 8, Type.TRAPEZE, "KBE\\KBE 58\\1 ОКНА\\*Открывание внутрь (ств. Z77)", 1009, 10009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1300.0, 400.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="DOOR">
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="ТЕСТ">       
        } else if (prj == 111) {  // -601006) {
            rootGson = new GsonRoot(427838, prj, 1, 110, Type.RECTANGL, "RAZIO\\RAZIO 58 N\\1 ОКНА", 1009, 1009, 1009);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1800.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 1800.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1600.0, 0.0));

            rootGson.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, .0, 500.0, 1600.0, 500.0));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, .0, 1000.0, 1600.0, 1000.0));
            GsonElem area3 = area2.addArea(new GsonElem(Type.AREA));

            area3.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area3.addElem(new GsonElem(Type.IMPOST, .0, 1500.0, 1600.0, 1500.0));
            area3.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } // </editor-fold>     
        else {
            return null;
        }
        return rootGson.toJson();
    }
}
