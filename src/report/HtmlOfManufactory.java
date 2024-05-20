package report;

import builder.Wincalc;
import builder.making.SpcRecord;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import common.eProp;
import dataset.Query;
import dataset.Record;
import domain.ePrjprod;
import domain.eProject;
import domain.eSysprod;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
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
            //List<Wincalc> wincList = wincList(prjprodList, length);
        
//        double total = spcList3.stream().mapToDouble(spc -> spc.getCost1()).sum();
//
//        doc.getElementById("h01").text("Заказ №" + projectRec.getStr(eProject.num_ord));
//        String template = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
//        for (int i = 1; i < spcList3.size(); i++) {
//            doc.getElementsByTag("tbody").append(template);
//        }
//        Elements trList = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr");
//        for (int i = 0; i < spcList3.size(); i++) {
//            RSpecific spc = spcList3.get(i);
//            Elements tdList = trList.get(i).getElementsByTag("td");
//            tdList.get(0).text(String.valueOf(i + 1));
//            tdList.get(1).text(spc.getArtikl());
//            tdList.get(2).text(spc.getName());
//            tdList.get(3).text(spc.getColorID1());
//            tdList.get(4).text(spc.getCount());
//            tdList.get(5).text(spc.getUnit());
//            tdList.get(6).text(spc.getPrice());
//            tdList.get(7).text(spc.getCost());
//        }
    }
}
