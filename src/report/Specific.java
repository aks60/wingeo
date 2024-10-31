package report;

import report.sup.RTable;
import report.sup.RSpecific;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.making.SpcRecord;
import builder.making.SpcTariffic;
import common.UCom;
import dataset.Record;
import domain.eArtikl;
import domain.ePrjprod;
import domain.eProject;
import frames.UGui;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

//Спецификация
public class Specific {

    private static int npp = 0;

    public  void parseDoc(Record projectRec) {
        try {
            npp = 0;
            InputStream in = getClass().getResourceAsStream("/resource/report/Specific.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile, "windows-1251");

            //Заполним отчёт
            loadDoc1(projectRec, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR*2 " + e, "ВНИМАНИЕ!", 1);
            System.err.println("Ошибка:HtmlOfSpecific.specific()" + e);
        }
    }

    private static void loadDoc1(Record projectRec, Document doc) {
        
        //Elements tdList = tab2List.get(i).getElementsByTag("td");
        List<SpcRecord> spcList = new ArrayList<SpcRecord>();
        List<RSpecific> kitList = new ArrayList<RSpecific>();
        List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
        Wincalc winc = new builder.Wincalc();

        //Цикл по конструкциям заказа
        for (Record prjprodRec : prjprodList) {
            String script = prjprodRec.getStr(ePrjprod.script);
            winc.build(script);
            winc.specification(true);
            spcList.addAll(winc.listSpec); //добавим спецификацию
            
            List<SpcRecord> list = SpcTariffic.kits(prjprodRec, winc, true); //добавим комплекты
            list.forEach(rec -> kitList.add(new RSpecific(rec)));
        }
        
        List<RSpecific> listSpc = new ArrayList<RSpecific>();
        spcList.forEach(rec -> listSpc.add(new RSpecific(rec)));
        String num = projectRec.getStr(eProject.num_ord);
        String date = UGui.simpleFormat.format(projectRec.get(eProject.date4));

        List<RSpecific> s1 = listSpc.stream().filter(s -> s.spc().artiklRec().getInt(eArtikl.level1) == 1).collect(toList());
        List<RSpecific> s2 = RSpecific.groups(listSpc.stream().filter(s -> s.spc().artiklRec().getInt(eArtikl.level1) == 2).collect(toList()));
        List<RSpecific> s3 = RSpecific.groups(listSpc.stream().filter(s -> s.spc().artiklRec().getInt(eArtikl.level1) == 3).collect(toList()));
        List<RSpecific> s5 = listSpc.stream().filter(s -> s.spc().artiklRec().getInt(eArtikl.level1) == 5).collect(toList());

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
        template.get(0).getElementsByTag("td").get(0).selectFirst("b").text("КОМПЛЕКТЫ");
        doc.getElementsByTag("tbody").append(template.get(0).html());
        kitList.forEach(spc -> templateAdd(template, spc, doc));

        double total = s1.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + s2.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + s3.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + s5.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + kitList.stream().mapToDouble(spc -> spc.getCost1()).sum();
        doc.getElementsByTag("tfoot").get(0).selectFirst("tr:eq(0)")
                .selectFirst("td:eq(1)").text(UCom.format(total, 9));
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
