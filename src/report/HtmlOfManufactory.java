package report;

import builder.Wincalc;
import builder.making.SpcRecord;
import dataset.Record;
import domain.ePrjprod;
import domain.eProject;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
            List<SpcRecord> spcList2 = new ArrayList<SpcRecord>();
            List<Record> prjprodList = ePrjprod.find2(projectRec.getInt(eProject.id));
            for (Record prjprodRec : prjprodList) {
                String script = prjprodRec.getStr(ePrjprod.script);
                Wincalc winc = new Wincalc(script);
                winc.specification(true);
                spcList2.addAll(winc.listSpec);
            }
            List<RSpecific> spcList3 = new ArrayList<RSpecific>();
            spcList2.forEach(el -> spcList3.add(new RSpecific(el)));

            Element div2 = doc.getElementById("div2");
            String template2 = div2.html();
            List<Wincalc> wincList = URep.wincList(prjprodList, 400);

            for (int i = 1; i < prjprodList.size(); i++) {
                div2.append(template2);
            }
            Elements tab2List = doc.getElementById("div2").getElementsByClass("tab2"),
                    tab3List = doc.getElementById("div2").getElementsByClass("tab3"),
                    tab4List = doc.getElementById("div2").getElementsByClass("tab4"),
                    tab5List = doc.getElementById("div2").getElementsByClass("tab5"),
                    tab6List = doc.getElementById("div2").getElementsByClass("tab6");

            //Цикл по изделиям
            for (int i = 0; i < prjprodList.size(); i++) {
                Wincalc winc = wincList.get(i);
                Record prjprodRec = prjprodList.get(i);
                for (int j = 0; j < tab2List.get(i).getElementsByTag("tr").size(); ++j) {
                    Elements tdList = tab2List.get(i).getElementsByTag("td");
                }
            }

            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfManufactory.load() " + e);
        }
    }
}
