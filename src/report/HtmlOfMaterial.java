package report;

import builder.Wincalc;
import builder.making.Specific;
import dataset.Record;
import domain.eColor;
import domain.ePrjprod;
import domain.eProject;
import enums.UseUnit;
import frames.Specifics;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HtmlOfMaterial {

    private static DecimalFormat df1 = new DecimalFormat("#0.0");
    private static DecimalFormat df2 = new DecimalFormat("#0.00");

    public static void material(Record projectRec) {
        try {
            URL path = HtmlOfMaterial.class.getResource("/resource/report/Material.html");
            File input = new File(path.toURI());
            Document doc = Jsoup.parse(input, "utf-8");

            //Заполним отчёт
            load(projectRec, doc);

            String str = doc.html();
            HtmlOfTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfMaterial.material()" + e);
        }
    }

    private static void load(Record projectRec, Document doc) {

        List<Specific> spcList2 = new ArrayList();
        List<Record> prjprodList = ePrjprod.find2(projectRec.getInt(eProject.id));
        for (Record prjprodRec : prjprodList) {
            String script = prjprodRec.getStr(ePrjprod.script);
            Wincalc winc = new Wincalc(script);
            winc.constructiv(true);
            spcList2.addAll(winc.listSpec);
        }
        List<RSpecific> spcList3 = new ArrayList();
        spcList2.forEach(el -> spcList3.add(new RSpecific(el)));
        double total = spcList3.stream().mapToDouble(spc -> spc.getCost1()).sum();
        
        doc.getElementById("h01").text("Заказ №" + projectRec.getStr(eProject.num_ord));        
        String template = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
        for (int i = 1; i < spcList3.size(); i++) {
            doc.getElementsByTag("tbody").append(template);
        }
        Elements trList = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr");
        for (int i = 0; i < spcList3.size(); i++) {
            RSpecific spc = spcList3.get(i);
            Elements tdList = trList.get(i).getElementsByTag("td");
            tdList.get(0).text(String.valueOf(i + 1));
            tdList.get(1).text(spc.getArtikl());
            tdList.get(2).text(spc.getName());
            tdList.get(3).text(spc.getColorID1());
            tdList.get(4).text(spc.getCount());
            tdList.get(5).text(spc.getUnit());
            tdList.get(6).text(spc.getPrice());
            tdList.get(7).text(spc.getCost());
        } 
        Object obj = doc.getElementsByTag("tfoot")
                .get(0).getElementsByTag("tr")
                .get(0).getElementsByTag("td")
                .get(1).text(df2.format(total));
    }
}
