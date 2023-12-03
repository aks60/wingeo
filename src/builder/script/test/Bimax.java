package builder.script.test;

import builder.script.GsonElem;
import builder.script.GsonRoot;
import com.google.gson.GsonBuilder;
import enums.Layout;
import enums.Type;

public final class Bimax {

    public static GsonRoot rootGeo;

    public static String script(Integer prj) {
// <editor-fold defaultstate="collapsed" desc="RECTANGL">
        if (prj == 601001) { //PUNIC = 427595
            rootGeo = new GsonRoot("2.0", prj, 2, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)", 1009, 1009, 1009);
            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));
            
        } else if (prj == 601006) { //PUNIC = 427838
            rootGeo = new GsonRoot("1.0", prj, 1, 110,Type.RECTANGL, "RAZIO\\RAZIO 58 N\\1 ОКНА", 1009, 1009, 1009);
            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addElem(new GsonElem(Type.GLASS, "{artglasID:5746}")); //или R4x10x4x10x4
            
        } else if (prj == 501003) {
            rootGeo = new GsonRoot("2.0", prj, 2, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)");
            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 0.0));

            rootGeo.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGeo.addElem(new GsonElem(Type.IMPOST, .0, 500.0, 1000.0, 500.0))
                    .addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 501004) {
            rootGeo = new GsonRoot("2.0", prj, 2, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)", 1009, 1009, 1009);
            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 900.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 600.0, 900.0));

            rootGeo.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGeo.addElem(new GsonElem(Type.IMPOST, 200.0, 900.0, 201.0, 300.0));
            rootGeo.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601003) { //PUNIC = 427819
            rootGeo = new GsonRoot("2.0", prj, 1, 81, Type.RECTANGL, "Darrio\\DARRIO 200\\1 ОКНА", 1009, 1009, 1009);
            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, .0))
                    .addArea(new GsonElem(Type.AREA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 501005) {
            rootGeo = new GsonRoot("2.0", prj, 2, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)", 1009, 1009, 1009);
            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 0.0));

            GsonElem area1 = rootGeo.addArea(new GsonElem(Type.AREA));
            area1.addElem(new GsonElem(Type.GLASS));
            rootGeo.addElem(new GsonElem(Type.IMPOST, 0.0, 500.0, 1000.0, 500.0));
            GsonElem area2 = rootGeo.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 500.0, 1000.0, 500.0, 500.0))
                    .addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 501006) {
            rootGeo = new GsonRoot("2.0", prj, 2, 8, Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)", 1009, 1009, 1009);
            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 0.0));

            GsonElem area1 = rootGeo.addArea(new GsonElem(Type.AREA));
            rootGeo.addElem(new GsonElem(Type.IMPOST, .0, 500.0, 1000.0, 500.0));
            GsonElem area2 = rootGeo.addArea(new GsonElem(Type.AREA));
            area2.addElem(new GsonElem(Type.GLASS));

            area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area1.addElem(new GsonElem(Type.IMPOST, 500.0, 1000.0, 500.0, 500.0));
            //area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area1.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="ARCH"> 
//        } else if (prj == 501007) {
//            rootGeo = new GsonRoot("2.0", prj, 2, 8, Type.ARCH, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)");
//            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1000.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1000.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 0.0));
//
//            rootGeo.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
//            rootGeo.addElem(new GsonElem(Type.IMPOST, .0, 500.0, 1000.0, 500.0));
//            rootGeo.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="TRAPEZE"> 
//        } else if (prj == 501002) {
//            rootGeo = new GsonRoot("2.0", prj, 2, 8, Type.TRAPEZE, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)");
//            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 500.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 500.0, 1000.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 400.0, 1770.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 3500.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 3500.0, 3500.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 4000.0, 1000.0))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, 3500.0, 500.0));
//
//            rootGeo.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
//            rootGeo.addElem(new GsonElem(Type.IMPOST, 1000.0, 500.0, 3500.0, 3500.0))
//                    .addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="DOOR">
// </editor-fold>
        } else {
            return null;
        }
        return new GsonBuilder().create().toJson(rootGeo);
    }
}
