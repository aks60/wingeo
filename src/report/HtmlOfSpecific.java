package report;

import builder.Wincalc;
import builder.making.Specific;
import builder.making.Tariffic;
import common.ArraySpc;
import dataset.Record;
import domain.eArtikl;
import domain.ePrjprod;
import domain.eProject;
import frames.UGui;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlOfSpecific {

    private static int npp = 0;
    private static DecimalFormat df1 = new DecimalFormat("#0.0");
    private static DecimalFormat df2 = new DecimalFormat("#0.00");

    public static void specific(Record projectRec) {
        try {
            npp = 0;
            URL path = HtmlOfSpecific.class.getResource("/resource/report/Specific.html");
            File input = new File(path.toURI());
            Document doc = Jsoup.parse(input, "utf-8");

            //Заполним отчёт
            load1(projectRec, doc);

            String str = doc.html();
            HtmlOfTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSpecific.specific()" + e);
        }
    }

    private static void load1(Record projectRec, Document doc) {
        List<Specific> spcList2 = new ArrayList();
        List<Record> prjprodList = ePrjprod.find2(projectRec.getInt(eProject.id));
        Wincalc winc = new builder.Wincalc();

        //Цикл по конструкциям заказа
        for (Record prjprodRec : prjprodList) {
            String script = prjprodRec.getStr(ePrjprod.script);
            winc.build(script);
            winc.constructiv(true);
            spcList2.addAll(winc.listSpec); //добавим спецификацию
            ArraySpc<Specific> kitList = Tariffic.kits(prjprodRec, winc, true); 
            spcList2.addAll(kitList); //добавим комплекты
        }
        
        List<RSpecific> spcList3 = new ArrayList();
        spcList2.forEach(el -> spcList3.add(new RSpecific(el)));
        String num = projectRec.getStr(eProject.num_ord);
        String date = UGui.simpleFormat.format(projectRec.get(eProject.date4));

        List<RSpecific> s1 = spcList3.stream().filter(s -> s.spc().artiklRec.getInt(eArtikl.level1) == 1).collect(toList());
        List<RSpecific> s2 = RSpecific.groups(spcList3.stream().filter(s -> s.spc().artiklRec.getInt(eArtikl.level1) == 2).collect(toList()));
        List<RSpecific> s3 = RSpecific.groups(spcList3.stream().filter(s -> s.spc().artiklRec.getInt(eArtikl.level1) == 3).collect(toList()));
        List<RSpecific> s5 = spcList3.stream().filter(s -> s.spc().artiklRec.getInt(eArtikl.level1) == 5).collect(toList());

        doc.getElementById("h01").text("Смета №" + projectRec.getStr(eProject.num_ord));       
        Elements template = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr");
        doc.getElementsByTag("tbody").get(0).html("");

        template.get(0).getElementsByTag("td").get(0).selectFirst("b").text("ПРОФИЛИ");
        doc.getElementsByTag("tbody").append(template.get(0).html());
        s1.forEach(spc -> templateAdd(template, spc, doc));
        template.get(0).getElementsByTag("td").get(0).selectFirst("b").text("АКСЕССУАРЫ");
        doc.getElementsByTag("tbody").append(template.get(0).html());
        s2.forEach(spc -> templateAdd(template, spc, doc));
        template.get(0).getElementsByTag("td").get(0).selectFirst("b").text("УПЛОТНЕНИЯ");
        doc.getElementsByTag("tbody").append(template.get(0).html());
        s3.forEach(spc -> templateAdd(template, spc, doc));
        template.get(0).getElementsByTag("td").get(0).selectFirst("b").text("ЗАПОЛНЕНИЯ");
        doc.getElementsByTag("tbody").append(template.get(0).html());
        s5.forEach(spc -> templateAdd(template, spc, doc));

        double total = s1.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + s2.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + s3.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + s5.stream().mapToDouble(spc -> spc.getCost1()).sum();
        doc.getElementsByTag("tfoot").get(0).selectFirst("tr:eq(0)")
                .selectFirst("td:eq(1)").text(df1.format(total));
    }

    private static void templateAdd(Elements template, RSpecific spc, Document doc) {
        Elements tdList = template.get(1).getElementsByTag("td");
        tdList.get(0).text(String.valueOf(++npp));
        tdList.get(1).text(spc.getArtikl());
        tdList.get(2).text(spc.getName());
        tdList.get(3).text(spc.getColorID1());
        tdList.get(4).text(spc.getWidth());
        tdList.get(5).text(spc.getAngl());
        tdList.get(6).text(spc.getUnit());        
        tdList.get(7).text(spc.getCount());
        tdList.get(8).text(spc.getWeight());        
        tdList.get(9).text(spc.getPrice());
        tdList.get(10).text(spc.getCost());
        doc.getElementsByTag("tbody").append(template.get(1).html());
    }
}
