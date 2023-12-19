package builder.script;

import builder.script.check.Bimax;
import common.eProp;
import enums.Type;
import java.util.List;

/**
 * Модели конструкций. Загружаются в таблицу SYSMODEL.
 */
public class GsonScript {

    public static GsonRoot rootGson = null;

    public static String modelJson(Integer prj) {

// <editor-fold defaultstate="collapsed" desc="RECTANGL">
        if (prj == 601001) { //PUNIC = 427817
            rootGson = new GsonRoot("2.0", Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1300.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601002) { //PUNIC = 427818  S = 1300*1400
            rootGson = new GsonRoot("2.0", Type.RECTANGL, "Montblanc\\Nord\\1 ОКНА");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, .0))
                    .addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 650.0, 1400.0, 650.0, .0));
            rootGson.addArea(new GsonElem(Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601006) { //PUNIC = 427838
            rootGson = new GsonRoot("1.0", Type.RECTANGL, "RAZIO \\ RAZIO 58 \\ 1 ОКНА");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 1400.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 900.0, 0.0))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 501003) {
            rootGson = new GsonRoot("2.0", Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, .0, 500.0, 1000.0, 500.0))
                    .addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601003) { //PUNIC = 427819
            rootGson = new GsonRoot("2.0", Type.RECTANGL, "Darrio\\DARRIO 200\\1 ОКНА");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, 1700.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1440.0, .0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 400.0, 1440.0, 400.0));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 720.0, 1700.0, 720.0, 400.0));
            area2.addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 501005) {
            rootGson = new GsonRoot("2.0", Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 0.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 0.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 0.0));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 500.0, 1000.0, 500.0));
            GsonElem area2 = rootGson.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 500.0, 1000.0, 500.0, 500.0));
            area2.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

        } else if (prj == 501006) {
            rootGson = new GsonRoot("2.0", Type.RECTANGL, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 0.0));

            GsonElem area1 = rootGson.addArea(new GsonElem(Type.AREA));
            rootGson.addElem(new GsonElem(Type.IMPOST, .0, 500.0, 1000.0, 500.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

            area1.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            area1.addElem(new GsonElem(Type.IMPOST, 500.0, 1000.0, 500.0, 500.0))
                    .addArea(new GsonElem(Type.STVORKA)).addElem(new GsonElem(Type.GLASS));
            //.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));

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
        } else if (prj == 605001) {
            rootGson = new GsonRoot("2.0", Type.TRAPEZE, "KBE\\KBE 58\\1 ОКНА\\*Открывание внутрь (ств. Z77)");
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, .0, .0)
                    .addElem(new GsonElem(Type.FRAME_SIDE, .0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 1500.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1300.0, 300.0)));

            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST, 0.0, 300.0, 1300.0, 300.0));
            rootGson.addArea(new GsonElem(Type.AREA)).addElem(new GsonElem(Type.GLASS));
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="DOOR">
        } else if (prj == 508841) { //Двери

        } else if (prj == 700009) { //Двери

        } else if (prj == 700014) { // Двери
// </editor-fold>

        } // <editor-fold defaultstate="collapsed" desc="ХОЛОДНЫЙ  ТЕСТ, спецификации в базе нет">         
        else if (prj == 1043598818) { //Прямоугольное сложное

        } else if (prj == 1489528103) { //Прямоугольное сложное без створок

        } else if (prj == 1620870217) { //Трапеции

        } else if (prj == 905754876) { //Трапеции

        } else if (prj == 1413114169) { //Трапеции

        } else if (prj == 770802872) { //TEST

        } else if (prj == 912042749) {

        } // </editor-fold>          
        else {
            return null;
        }
        return rootGson.toJson();
    }

    public static List<Integer> modelList(String scale) {
        String base_name = (eProp.base_num.read().equals("1")) ? eProp.base1.read()
                : (eProp.base_num.read().equals("2")) ? eProp.base2.read() : eProp.base3.read();

        if (base_name.toLowerCase().contains("sial3")) {
            return List.of(601001, 601002, 601003, 601004, 601007, 601008);

        } else if (base_name.toLowerCase().contains("alutech3")) {
            return List.of(601001, 601002, 601003, 601004);

        } else if (base_name.toLowerCase().contains("alutex3")) {
            return List.of(4);

        } else if (base_name.toLowerCase().contains("bimax")) {
            return ("max".equals(scale)) ? List.of(
                    508807, 508809, 508966, //москитки
                    601001, 601002, 601003, 601004, 601005, 601006, 601007, 601008, 601009, 601010, //прямоугольные окна
                    700027, 604004, 604005, 604006, 604007, 604008, 604009, 604010, //арки
                    508983, 506642, -506642, 605001, -605001, 508916, -508916, 508945, //трапеции  
                    508841, 700009, 700014) //двери
                    : List.of(601006, 601001, 601002, 601003, 604009, 604005, 605001, -605001,
                            506642, -506642, 508916, -508916, 508841, 700009, 700014);

        } else if (base_name.toLowerCase().contains("vidnal.fdb")) {
            return List.of(26);

        } else if (base_name.toLowerCase().contains("krauss")) {
            return null;

        } else if (base_name.toLowerCase().contains("sokol")) {
            return List.of(1);
        }
        return null;
    }

    public static String productJson(Integer prj) {
        String base_name = (eProp.base_num.read().equals("1")) ? eProp.base1.read()
                : (eProp.base_num.read().equals("2")) ? eProp.base2.read() : eProp.base3.read();

        if (base_name.toLowerCase().contains("sial3")) {
            //return Sial3.script(prj);

        } else if (base_name.toLowerCase().contains("alutex3")) {
            //return Alutex3.script(prj);

        } else if (base_name.toLowerCase().contains("alutech3")) {
            //return Alutech3.script(prj);

        } else if (base_name.toLowerCase().contains("bimax")) {
            return Bimax.script(prj);

        } else if (base_name.toLowerCase().contains("vidnal")) {
            //return Vidnal.script(prj);

        } else if (base_name.toLowerCase().contains("krauss")) {
            //return Krauss.script(prj);

        } else if (base_name.toLowerCase().contains("sokol")) {
            //return Sokol.script(prj);
        }
        return null;
    }

    public static List<Integer> productList(String scale) {
        String base_name = (eProp.base_num.read().equals("1")) ? eProp.base1.read()
                : (eProp.base_num.read().equals("2")) ? eProp.base2.read() : eProp.base3.read();

        if (base_name.toLowerCase().contains("sial3")) {
            return List.of(601001, 601002, 601003, 601004, 601007, 601008);

        } else if (base_name.toLowerCase().contains("alutech3")) {
            return List.of(601001, 601002, 601003, 601004);

        } else if (base_name.toLowerCase().contains("alutex3")) {
            return List.of(4);

        } else if (base_name.toLowerCase().contains("bimax")) {
            return ("max".equals(scale)) ? List.of(
                    508807, 508809, 508966, //москитки
                    601001, 601002, 601003, 601004, 601005, 601006, 601007, 601008, 601009, 601010, //прямоугольные окна
                    700027, 604004, 604005, 604006, 604007, 604008, 604009, 604010, //арки
                    506642, -506642, 605001, 508916, 508945, 508841, 700009, 700014) //трапеции, двери
                    //                    : List.of(601001, 601002, 601003, 601004, 601005, 601006,
                    //                            601007, 601008, 601009, 601010, 604005, 604006, 604007, 604008, 604009, 604010);
                    : List.of(601001, 601002, 601003, 601006);

        } else if (base_name.toLowerCase().contains("vidnal.fdb")) {
            return List.of(26);

        } else if (base_name.toLowerCase().contains("krauss")) {
            return null;

        } else if (base_name.toLowerCase().contains("sokol")) {
            return List.of(1);
        }
        return null;
    }

    public static String path() {
        String base_name = (eProp.base_num.read().equals("1")) ? eProp.base1.read()
                : (eProp.base_num.read().equals("2")) ? eProp.base2.read() : eProp.base3.read();

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
