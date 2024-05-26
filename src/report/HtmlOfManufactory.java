package report;

import builder.Wincalc;
import builder.making.SpcRecord;
import dataset.Record;
import domain.ePrjprod;
import domain.eProject;
import enums.TypeArt;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//Задание в цех
public class HtmlOfManufactory {

    private static DecimalFormat df1 = new DecimalFormat("#0.0");
    private static DecimalFormat df2 = new DecimalFormat("#0.00");

    public static void manufactory(Record projectRec) {
        try {
            URL path = HtmlOfManufactory.class.getResource("/resource/report/Manufactory.html");
            File input = new File(path.toURI());
            Document doc = Jsoup.parse(input, "utf-8");

            //Заполним отчёт
            load(projectRec, doc);

            String str = doc.html();
            HtmlOfTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfManufactory.manufactory() " + e);
        }
    }

    private static void load(Record projectRec, Document doc) {
        try {
            List<Record> prjprodList = ePrjprod.find2(projectRec.getInt(eProject.id));
            List<Wincalc> wincList = URep.wincList(prjprodList, 400);

            //Заполним файл шаблонами заказов
            Element div2 = doc.getElementById("div2");
            String templateBody = div2.html();
            for (int i = 1; i < prjprodList.size(); i++) {
                div2.append(templateBody);
            }

            Element tab2 = doc.getElementById("tab2"), tab3 = doc.getElementById("tab3"),
                    tab4 = doc.getElementById("tab4"), tab5 = doc.getElementById("tab5"),
                    tab6 = doc.getElementById("tab6");
            String template2Rec = tab2.getElementsByTag("tr").get(1).html(),
                    template3Rec = tab3.getElementsByTag("tr").get(1).html(),
                    template4Rec = tab4.getElementsByTag("tr").get(1).html(),
                    template5Rec = tab5.getElementsByTag("tr").get(1).html(),
                    template6Rec = tab6.getElementsByTag("tr").get(1).html();

            //Цикл по изделиям
            for (int i = 0; i < prjprodList.size(); i++) {

                String script = prjprodList.get(i).getStr(ePrjprod.script);
                Wincalc winc = new Wincalc(script);
                winc.specification(true);

                //Таблица №3 ПРОФИЛЬ / АРМИРОВАНИЕ          
                List<SpcRecord> spcList3 = new ArrayList(), spcList3a = new ArrayList();
                loadTab3Specific(winc, tab3, template3Rec, spcList3, spcList3a); //спецификация для изделия 
                //doc.getElementById("tab3").getElementsByTag("tr").remove();
                //spcList3.forEach(act -> tab3.append(template3Rec));
                for (int f = 1; f < spcList3.size(); ++f) {
                    tab3.getElementsByTag("tbody").append(template3Rec);
                }                

                for (int j = 0; j < spcList3.size(); j++) { //заполним строки 
                    Elements tdList3 = tab3.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    tdList3.get(0).text(String.valueOf(j + 1));
                    tdList3.get(1).text(str(spcList3.get(j).artikl));
                    tdList3.get(2).text(str(spcList3.get(j).name));
                    tdList3.get(3).text(str(spcList3.get(j).width));
                    tdList3.get(4).text(str(spcList3.get(j).anglCut0));
                    tdList3.get(5).text(str(spcList3.get(j).anglCut1));
                    tdList3.get(6).text(str(spcList3.get(j).count));
                    tdList3.get(7).text(str(spcList3.get(j).anglHoriz));
                    tdList3.get(8).text("");
                    tdList3.get(9).text(str(spcList3a.get(j).width));
                    tdList3.get(10).text(str(spcList3a.get(j).artikl));
                }

                //Таблица №4 УПЛОТНИТЕЛИ          
                List<SpcRecord> spcList4 = loadTab4Specific(winc, tab4, template4Rec);
                for (int f = 1; f < spcList4.size(); ++f) {
                    tab4.getElementsByTag("tbody").append(template4Rec);
                }
                for (int j = 0; j < spcList4.size(); j++) { //заполним строки 
                    Elements tdList4 = tab4.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    tdList4.get(0).text(String.valueOf(j + 1));
                    tdList4.get(1).text(str(spcList4.get(j).artikl));
                    tdList4.get(2).text(str(spcList4.get(j).name));
                    tdList4.get(3).text(str(spcList4.get(j).unit));
                    tdList4.get(4).text(str(spcList4.get(j).width));
                }

                //Таблица №5 ШТАПИК          
                List<SpcRecord> spcList5 = loadTab5Specific(winc, tab5, template5Rec);

            }

            //Загрузим изображения
            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }

        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfManufactory.load() " + e);
        }
    }

    //ПРОФИЛЬ / АРМИРОВАНИЕ
    public static void loadTab3Specific(Wincalc winc, Element tab, String templateRec, List<SpcRecord> spcList2, List<SpcRecord> spcList3) {

        winc.listSpec.forEach(spcRec -> { //профиля
            if (TypeArt.isType(spcRec.artiklRec, TypeArt.X100, TypeArt.X101, TypeArt.X102, TypeArt.X103, TypeArt.X104, TypeArt.X105) == true) {
                spcList2.add(spcRec);
            }
        });
        spcList2.forEach(spcRec1 -> { //армирование
            SpcRecord spcRec3 = new SpcRecord();
            for (SpcRecord spcRec2 : winc.listSpec) {
                if (TypeArt.isType(spcRec2.artiklRec, TypeArt.X107) == true && spcRec2.elem5e.id == spcRec1.id) {
                    spcRec3 = spcRec2;
                }
            }
            spcList3.add(spcRec3);
        });
    }

    //УПЛОТНИТЕЛИ
    public static List<SpcRecord> loadTab4Specific(Wincalc winc, Element tab, String templateRec) {

        List<SpcRecord> spcList = new ArrayList();
        winc.listSpec.forEach(spcRec -> { //профиля
            if (TypeArt.isType(spcRec.artiklRec, TypeArt.X135) == true) {
                spcList.add(spcRec);
            }
        });
        return spcList;
    }

    //ШТАПИК
    public static List<SpcRecord> loadTab5Specific(Wincalc winc, Element tab, String templateRec) {

        List<SpcRecord> spcList = new ArrayList();
        winc.listSpec.forEach(spcRec -> { //профиля
            if (TypeArt.isType(spcRec.artiklRec, TypeArt.X108) == true) {
                spcList.add(spcRec);
            }
        });
        return spcList;
    }

    private static String str(Object txt) {
        if (txt == null) {
            return "";
        } else if (txt instanceof Number) {
            return df2.format(txt);
        }
        return txt.toString();
    }
}
