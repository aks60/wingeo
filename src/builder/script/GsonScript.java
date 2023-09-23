package builder.script;

import builder.script.test.Alutech3;
import builder.script.test.Alutex3;
import builder.script.test.Vidnal;
import builder.script.test.Sial3;
import builder.script.test.Krauss;
import builder.script.test.Bimax;
import builder.script.test.Sokol;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import common.eProp;
import enums.Form;
import enums.Layout;
import enums.Type;
import java.util.List;

/**
 * Модели конструкций. Загружаются в таблицу SYSMODEL.
 */
public class GsonScript {

    public static GsonRoot rootGson;

    public static String modelJson(Integer prj) {
        
// <editor-fold defaultstate="collapsed" desc="RECTANGL">
        if (prj == 601001) {
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь (ств. Z77)",
                    Layout.VERT, Type.RECTANGL, 900, 1300, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601002) {
            rootGson = new GsonRoot("1.0", null, null, null, "Montblanc\\Nord\\ОКНА",
                    Layout.HORIZ, Type.RECTANGL, 1300, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1300 / 2))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:3}"))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1300 / 2))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601003) {
            rootGson = new GsonRoot("1.0", null, null, null, "Darrio\\DARRIO 200\\ОКНА",
                    Layout.VERT, Type.RECTANGL, 1440, 1700, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 400))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1300));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 720))
                    .addArea(new GsonElem(Layout.HORIZ, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 720))
                    .addArea(new GsonElem(Layout.HORIZ, Type.STVORKA, "{typeOpen:3}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601004) {
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.RECTANGL, 1440, 1700, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT));
            rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 400))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1300));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 720))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 720))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601005) {
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь)",
                    Layout.HORIZ, Type.RECTANGL, 1600, 1700, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT));
            rootGson.addArea(new GsonElem(Layout.VERT, Type.AREA, 800))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 800))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601006) {
            rootGson = new GsonRoot("1.0", null, null, null, "RAZIO\\RAZIO 58 \\ОКНА",
                    Layout.HORIZ, Type.RECTANGL, 900, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601007) {
            rootGson = new GsonRoot("1.0", null, null, null, "NOVOTEX\\Techno 58\\ОКНА",
                    Layout.VERT, Type.RECTANGL, 1100, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 300))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1100));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 550))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 550))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601008) {
            rootGson = new GsonRoot("1.0", null, null, null, "Rehau\\Blitz new\\ОКНА",
                    Layout.HORIZ, Type.RECTANGL, 1200, 1700, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 600))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.VERT, Type.AREA, 600));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 550))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4, sysfurnID:534}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 1150))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 601009) {
            rootGson = new GsonRoot("1.0", null, null, null, "KBE Эксперт\\ОКНА\\Открывание внутрь",
                    Layout.HORIZ, Type.RECTANGL, 700, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 999) {
            rootGson = new GsonRoot("1.0", null, null, null, "KBE Эксперт\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.RECTANGL, 700, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            rootGson.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addElem(new GsonElem(Type.GLASS));

            //Тут просочилась ручка не подходящая по параметру. Возможно ошибка ПрофСтроя4
        } else if (prj == 601010) {
            rootGson = new GsonRoot("1.0", null, null, null, "KBE Эксперт\\ОКНА\\Открывание внутрь",
                    Layout.HORIZ, Type.RECTANGL, 1300, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            rootGson.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 700027) {
            rootGson = new GsonRoot("1.0", null, null, null, "Montblanc//Eco//ОКНА (штульп)",
                    Layout.HORIZ, Type.RECTANGL, 1300, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 450))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.SHTULP))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 850))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));

//        } else if (prj == 508634) {
//            rootGson = new GsonRoot("1.0", null, null, null, "Rehau\\Delight\\ОКНА(ФИГУРНАЯ СТВОРКА)",
//                    Layout.HORIZ, Type.RECTANGL, 900, 1400, null, null, null);
//            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
//                    .addElem(new GsonElem(Type.GLASS));
//
//        } else if (prj == 508777) {
//            rootGson = new GsonRoot("1.0", null, null, null, "Rehau\\Delight\\ОКНА(ФИГУРНАЯ СТВОРКА)",
//                    Layout.HORIZ, Type.RECTANGL, 900, 1400, null, null, null);
//            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
//                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
//                    .addElem(new GsonElem(Type.GLASS));
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="ARCH">
            //TODO Нерешённая проблема со штапиком
        } else if (prj == 604004) {
            rootGson = new GsonRoot("1.0", null, null, null, "Rehau\\Delight\\ОКНА",
                    Layout.VERT, Type.ARCH, 1300, 1700, 1050, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 650, Form.TOP))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1050));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));
            
        } else if (prj == 604005) {
            rootGson = new GsonRoot("1.0", null, null, null, "Wintech\\Termotech 742\\ОКНА",
                    Layout.VERT, Type.ARCH, 1300, 1500, 1200, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 300, Form.TOP))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1200));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604006) {
            rootGson = new GsonRoot("1.0", null, null, null, "Wintech\\Termotech 742\\ОКНА",
                    Layout.VERT, Type.ARCH, 1100, 1600, 1220, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 380, Form.TOP))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1220));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 550))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 550))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604007) {
            rootGson = new GsonRoot("1.0", null, null, null, "Rehau\\Blitz new\\ОКНА",
                    Layout.VERT, Type.ARCH, 1400, 1700, 1300, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 400, Form.TOP))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = (GsonElem) rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1300));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 700))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 700))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:3}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604008) {
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.ARCH, 1300, 1500, 1200, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 300, Form.TOP))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = (GsonElem) rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1200));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:3}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:3}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604009) {
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.ARCH, 1300, 1500, 1200, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 300, Form.TOP))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1200))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 604010) {
            rootGson = new GsonRoot("1.0", null, null, null, "Montblanc\\Nord\\ОКНА",
                    Layout.VERT, Type.ARCH, 1300, 1700, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 300, Form.TOP))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = (GsonElem) rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1400));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            GsonElem area2 = (GsonElem) area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650));
            area2.addArea(new GsonElem(Layout.VERT, Type.AREA, 557))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST));
            area2.addArea(new GsonElem(Layout.VERT, Type.AREA, 843))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508983) {
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\НЕПРЯМОУГОЛЬНЫЕ ОКНА, ДВЕРИ, АРКИ",
                    Layout.VERT, Type.ARCH, 1300, 1300, 1000, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 300, Form.TOP))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1000));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 650))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="TRAPEZE">
        } else if (prj == 506642) { // Трапеции без импоста
            rootGson = new GsonRoot("1.0", null, null, null, "KBE Эксперт\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, Form.RIGHT, 1000, 1300, 1000, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == -506642) { //Трапеции без импоста
            rootGson = new GsonRoot("1.0", null, null, null, "KBE Эксперт\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, Form.LEFT, 1000, 1000, 1300, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 605001) { // Трапеции
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, 1300, 1500, 1200, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 300, Form.RIGHT))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 1200))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == -605001) { // Трапеции
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, 1300, 1200, 1500, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 300, Form.LEFT))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 1200))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508916) { //Трапеции
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, 900, 1400, 1000, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 400, Form.RIGHT))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 1000))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == -508916) { //Трапеции
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, 900, 1000, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 400, Form.LEFT))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 1000))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 508945) { //Трапеции
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, 600, 600, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 800, Form.LEFT))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 600))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));
            
        } else if (prj == 506929) { //PUNIC = 425688
            rootGson = new GsonRoot("1.0", null, null, null, "Montblanc/Eco/Непрямоугольные окна",
                    Layout.VERT, Type.TRAPEZE, Form.SYMM, 790, 440, 615, 615, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addElem(new GsonElem(Type.GLASS));            
// </editor-fold>
            
// <editor-fold defaultstate="collapsed" desc="DOOR">
        } else if (prj == 508841) { //Двери
            rootGson = new GsonRoot("1.0", null, null, null, "KBE Эксперт\\ВХОДНЫЕ ДВЕРИ\\Дверь наружу",
                    Layout.VERT, Type.DOOR, 900, 2100, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT));
            GsonElem stv = rootGson.addArea(new GsonElem(Layout.VERT, Type.STVORKA));
            stv.addArea(new GsonElem(Layout.VERT, Type.AREA, 1300))
                    .addElem(new GsonElem(Type.GLASS));
            stv.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 800))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 700009) { //Двери
            rootGson = new GsonRoot("1.0", null, null, null, "Darrio\\Двери DARRIO\\Дверь внутрь",
                    Layout.VERT, Type.DOOR, 900, 2000, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT));
            GsonElem stv = rootGson.addArea(new GsonElem(Layout.VERT, Type.STVORKA));
            stv.addArea(new GsonElem(Layout.VERT, Type.AREA, 1400))
                    .addElem(new GsonElem(Type.GLASS));
            stv.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 600))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 700014) { // Двери
            rootGson = new GsonRoot("1.0", null, null, null, "Rehau\\Brilliant\\ДВЕРИ ВХОДНЫЕ\\Дверь наружу",
                    Layout.VERT, Type.DOOR, 900, 2100, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT));
            GsonElem stv = rootGson.addArea(new GsonElem(Layout.VERT, Type.STVORKA));
            stv.addArea(new GsonElem(Layout.VERT, Type.AREA, 600))
                    .addElem(new GsonElem(Type.GLASS));
            stv.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 1500))
                    .addElem(new GsonElem(Type.GLASS));
// </editor-fold>

        } // <editor-fold defaultstate="collapsed" desc="ХОЛОДНЫЙ  ТЕСТ, спецификации в базе нет">         
        else if (prj == 1043598818) { //Прямоугольное сложное
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.RECTANGL, 1440, 1700, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT));
            GsonElem areaT = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 400));
            areaT.addArea(new GsonElem(Layout.VERT, Type.AREA, 420))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));
            areaT.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 1020))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4}"))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1300));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 720))
                    .addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST));
            GsonElem area2 = area.addArea(new GsonElem(Layout.VERT, Type.AREA, 720));
            area2.addArea(new GsonElem(Layout.VERT, Type.AREA, 200))
                    .addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 400))
                    .addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST));
            GsonElem area3 = area2.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 700));
            area3.addArea(new GsonElem(Layout.VERT, Type.AREA, 220))
                    .addElem(new GsonElem(Type.GLASS));
            area3.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 300))
                    .addElem(new GsonElem(Type.GLASS));
            area3.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 200))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 1489528103) { //Прямоугольное сложное без створок
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.RECTANGL, 1440, 1700, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT));
            GsonElem areaT = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 400));
            areaT.addArea(new GsonElem(Layout.VERT, Type.AREA, 420))
                    //.addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1, sysfurnID:1634}"))
                    .addElem(new GsonElem(Type.GLASS));
            areaT.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 1020))
                    //.addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4, sysfurnID:1633}"))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 1300));
            area.addArea(new GsonElem(Layout.VERT, Type.AREA, 720))
                    //.addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:1, sysfurnID:1634}"))
                    .addElem(new GsonElem(Type.GLASS));
            area.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 720))
                    //.addArea(new GsonElem(Layout.VERT, Type.STVORKA, "{typeOpen:4, sysfurnID:1633}"))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 1620870217) { //Трапеции
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, Form.RIGHT, 1300, 1500, 1200, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 905754876) { //Трапеции
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\*Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, Form.RIGHT, 1300, 1200, 1500, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 1413114169) { //Трапеции
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\*Открывание внутрь",
                    Layout.VERT, Type.TRAPEZE, 1300, 1200, 1500, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 400, Form.LEFT))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.VERT, Type.AREA, 1100))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 770802872) { //TEST
            rootGson = new GsonRoot("1.0", null, null, null, "KBE 58\\ОКНА\\Открывание внутрь",
                    Layout.VERT, Type.RECTANGL, 1200, 1400, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT));
            GsonElem area1 = rootGson.addArea(new GsonElem(Layout.VERT, Type.AREA, 600));
            area1.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 200))
                    .addElem(new GsonElem(Type.GLASS));
            area1.addElem(new GsonElem(Type.IMPOST));
            area1.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 400))
                    .addElem(new GsonElem(Type.GLASS));
            rootGson.addElem(new GsonElem(Type.IMPOST));
            GsonElem area2 = rootGson.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 800));
            area2.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 200))
                    .addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST));

            GsonElem area3 = area2.addArea(new GsonElem(Layout.VERT, Type.AREA, 300));
            area3.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 400))
                    .addElem(new GsonElem(Type.GLASS));
            area3.addElem(new GsonElem(Type.IMPOST))
                    .addArea(new GsonElem(Layout.HORIZ, Type.AREA, 400))
                    .addElem(new GsonElem(Type.GLASS));

            area2.addElem(new GsonElem(Type.IMPOST));
            area2.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 400))
                    .addElem(new GsonElem(Type.GLASS));
            area2.addElem(new GsonElem(Type.IMPOST));
            area2.addArea(new GsonElem(Layout.HORIZ, Type.AREA, 300))
                    .addElem(new GsonElem(Type.GLASS));

        } else if (prj == 912042749) {
            rootGson = new GsonRoot("1.0", null, null, null, "ALUTECH\\ALT.W62\\Двери\\Внутрь",
                    Layout.VERT, Type.DOOR, 900, 2100, null, null, null);
            rootGson.addElem(new GsonElem(Type.FRAME_SIDE, Layout.BOTT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.RIGHT))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.TOP))
                    .addElem(new GsonElem(Type.FRAME_SIDE, Layout.LEFT));
            rootGson.addArea(new GsonElem(Layout.VERT, Type.STVORKA))
                    .addElem(new GsonElem(Type.GLASS));
        } // </editor-fold>          
        else {
            return null;
        }
        return rootGson.toJson();
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
            return Sial3.script(prj);

        } else if (base_name.toLowerCase().contains("alutex3")) {
            return Alutex3.script(prj);

        } else if (base_name.toLowerCase().contains("alutech3")) {
            return Alutech3.script(prj);

        } else if (base_name.toLowerCase().contains("bimax")) {
            return Bimax.script(prj);

        } else if (base_name.toLowerCase().contains("vidnal")) {
            return Vidnal.script(prj);

        } else if (base_name.toLowerCase().contains("krauss")) {
            return Krauss.script(prj);

        } else if (base_name.toLowerCase().contains("sokol")) {
            return Sokol.script(prj);
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
