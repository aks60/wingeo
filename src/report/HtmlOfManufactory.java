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
import javax.swing.JTextField;
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

            Elements tab2List = doc.getElementById("div2").getElementsByClass("tab2"),
                    tab3List = doc.getElementById("div2").getElementsByClass("tab3"),
                    tab4List = doc.getElementById("div2").getElementsByClass("tab4"),
                    tab5List = doc.getElementById("div2").getElementsByClass("tab5"),
                    tab6List = doc.getElementById("div2").getElementsByClass("tab6");
            String template2Rec = tab2List.get(0).getElementsByTag("tr").get(1).html(),
                    template3Rec = tab3List.get(0).getElementsByTag("tr").get(1).html(),
                    template4Rec = tab4List.get(0).getElementsByTag("tr").get(1).html(),
                    template5Rec = tab5List.get(0).getElementsByTag("tr").get(1).html(),
                    template6Rec = tab6List.get(0).getElementsByTag("tr").get(1).html();

            //Цикл по изделиям
            for (int i = 0; i < prjprodList.size(); i++) {

                //Таблица №3 ПРОФИЛЬ / АРМИРОВАНИЕ          
                Element tab3 = tab3List.get(i);

                List<SpcRecord> spcList3 = new ArrayList(), spcList3a = new ArrayList();
                loadSpecific(prjprodList.get(i), spcList3, spcList3a); //спецификация для изделия     

                spcList3.forEach(act -> tab3.getElementsByTag("tbody").append(template3Rec));
                for (int j = 0; j < spcList3.size(); j++) { //заполним строки 
                    Elements tdList3 = tab3List.get(i).getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    tdList3.get(0).text(String.valueOf(i + 1));
                    tdList3.get(1).text(spcList3.get(j).artikl);
                    tdList3.get(2).text(spcList3.get(j).name);
                    tdList3.get(4).text(str(spcList3.get(j).anglCut0));
                    tdList3.get(5).text(str(spcList3.get(j).anglCut1));
                }
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

    public static void loadSpecific(Record prjprodRec, List<SpcRecord> spcList2, List<SpcRecord> spcList3) {

        String script = prjprodRec.getStr(ePrjprod.script);
        Wincalc winc = new Wincalc(script);
        winc.specification(true);

        winc.listSpec.forEach(spcRec -> { //профиля
            if (TypeArt.isType(spcRec.artiklRec, TypeArt.X100, TypeArt.X101, TypeArt.X102, TypeArt.X103, TypeArt.X104, TypeArt.X105) == true) {
                spcList2.add(spcRec);
            }
        });
        spcList2.forEach(spcRec1 -> { //армирование
            winc.listSpec.forEach(spcRec2 -> {
                if (TypeArt.isType(spcRec2.artiklRec, TypeArt.X107) == true && spcRec2.elem5e.id == spcRec1.id) {
                    spcList3.add(spcRec2);
                }
            });
        });
    }

    private static String str(Object txt) {
        if (txt == null) {
            return "";
        } else if(txt instanceof Number) {
            return df2.format(txt);
        }
        return txt.toString();
    }
}
