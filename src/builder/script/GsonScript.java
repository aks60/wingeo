package builder.script;

import builder.script.test.Bimax;
import static builder.script.test.Bimax.rootGeo;
import common.eProp;
import enums.Type;
import java.util.List;

/**
 * Модели конструкций. Загружаются в таблицу SYSMODEL.
 */
public class GsonScript {

    public static GsonRoot rootGeo = null;

    public static String modelJson(Integer prj) {
        
// <editor-fold defaultstate="collapsed" desc="RECTANGL">
        if (prj == 601001) {

        } else if (prj == 601002) {

        } else if (prj == 601003) {

        } else if (prj == 601004) {

        } else if (prj == 601005) {

        } else if (prj == 601006) {

        } else if (prj == 601007) {

        } else if (prj == 601008) {

        } else if (prj == 601009) {

        } else if (prj == 999) {

            //Тут просочилась ручка не подходящая по параметру. Возможно ошибка ПрофСтроя4
        } else if (prj == 501005) {
            rootGeo = new GsonRoot("2.0", prj, 2, 8, "KBE\\KBE 58\\1 ОКНА\\Открывание внутрь (ств. Z77)");
            rootGeo.addElem(new GsonElem(Type.FRAME_SIDE, 10.0, 10.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 10.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 1000.0))
                    .addElem(new GsonElem(Type.FRAME_SIDE, 1000.0, 10.0));

            GsonElem area1 = rootGeo.addArea(new GsonElem(Type.AREA));
            area1.addElem(new GsonElem(Type.GLASS));
            rootGeo.addElem(new GsonElem(Type.IMPOST, 10.0, 500.0, 1000.0, 500.0));
            GsonElem area2 = rootGeo.addArea(new GsonElem(Type.AREA));

            area2.addArea(new GsonElem(Type.AREA))
                    .addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST, 500.0, 1000.0, 500.0, 500.0))
                    .addArea(new GsonElem(Type.AREA))
                    .addElem(new GsonElem(Type.GLASS));
        } else if (prj == 700027) {

       } else if (prj == 508634) {

        } else if (prj == 508777) {

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="ARCH">
        } else if (prj == 604004) {
            
        } else if (prj == 604005) {

        } else if (prj == 604006) {

        } else if (prj == 604007) {

        } else if (prj == 604008) {

        } else if (prj == 604009) {

        } else if (prj == 604010) {

        } else if (prj == 508983) {
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="TRAPEZE">
        } else if (prj == 506642) { // Трапеции без импоста

        } else if (prj == -506642) { //Трапеции без импоста

        } else if (prj == 605001) { // Трапеции

        } else if (prj == -605001) { // Трапеции

        } else if (prj == 508916) { //Трапеции

        } else if (prj == -508916) { //Трапеции

        } else if (prj == 508945) { //Трапеции
            
        } else if (prj == 506929) { //PUNIC = 425688          
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
        return rootGeo.toJson();
    }

    public static List<Integer> modelList(String scale)     {
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
                    : List.of(601001, 601002, 601003, 601004, 601005, 601006,
                            601007, 601008, 601009, 601010, 506642, 604005, 604006, 604007, 604008, 604009, 604010);

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
