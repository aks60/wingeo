package report;

import builder.Kitcalc;
import report.sup.RTable;
import report.sup.RRecord;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.making.TRecord;
import builder.making.TTariffic;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//Спецификация
public class RSpecific {

    private static int npp = 0;

    public void parseDoc(List<Record> prjprodList) {
        try {
            npp = 0;
            InputStream in = getClass().getResourceAsStream("/resource/report/Specific.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile, "windows-1251");

            Record prjprodRec = prjprodList.get(0);
            Record projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));

            //Заполним отчёт
            loadDoc(projectRec, prjprodList, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR*2 " + e, "ВНИМАНИЕ!", 1);
            System.err.println("Ошибка:RSpecific.parseDoc1() " + e);
        }
    }

    private static void loadDoc(Record projectRec, List<Record> prjprodList, Document doc) {

        List<TRecord> spcList = new ArrayList<TRecord>();
        List<RRecord> kitList = new ArrayList<RRecord>();
        Wincalc winc = new Wincalc();

        //Цикл по конструкциям заказа
        for (Record prjprodRec : prjprodList) {
            String script = prjprodRec.getStr(ePrjprod.script);
            if (script.isEmpty() == false) {
                winc.build(script);
                winc.specific(true);
                spcList.addAll(winc.listSpec); //добавим спецификацию
            }
            List<TRecord> list = Kitcalc.tarifficProd(winc, prjprodRec, 0, true, false); //добавим комплекты
            list.forEach(rec -> kitList.add(new RRecord(rec)));
        }

        List<RRecord> listSpc = new ArrayList<RRecord>();
        spcList.forEach(rec -> listSpc.add(new RRecord(rec)));
        String num = projectRec.getStr(eProject.num_ord);
        String date = UGui.simpleFormat.format(projectRec.get(eProject.date5));

        List<RRecord> listSpc1 = listSpc.stream().filter(rec -> rec.spc().artiklRec.getInt(eArtikl.level1) == 1).collect(toList());
        List<RRecord> listSpc2 = RRecord.groups4R(listSpc.stream().filter(rec -> rec.spc().artiklRec.getInt(eArtikl.level1) == 2).collect(toList()));
        List<RRecord> listSpc3 = RRecord.groups4R(listSpc.stream().filter(rec -> rec.spc().artiklRec.getInt(eArtikl.level1) == 3).collect(toList()));
        List<RRecord> listSpc5 = listSpc.stream().filter(rec -> rec.spc().artiklRec.getInt(eArtikl.level1) == 5).collect(toList());

        doc.getElementById("h01").text("Смета №" + projectRec.getStr(eProject.num_ord));
        doc.getElementsByTag("thead").get(0).getElementsByTag("tr").get(0).getElementsByTag("th").get(0).html("Дата: " + date + " г.");

        Elements templateRec = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr");
        doc.getElementsByTag("tbody").get(0).html("");

        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("ПРОФИЛИ");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        listSpc1.forEach(rec -> recordAdd(templateRec, rec, doc));

        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("АКСЕССУАРЫ");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        listSpc2.forEach(rec -> recordAdd(templateRec, rec, doc));

        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("УПЛОТНЕНИЯ");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        listSpc3.forEach(rec -> recordAdd(templateRec, rec, doc));

        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("ЗАПОЛНЕНИЯ");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        listSpc5.forEach(rec -> recordAdd(templateRec, rec, doc));

        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("КОМПЛЕКТЫ");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        kitList.forEach(rec -> recordAdd(templateRec, rec, doc));

        double total = listSpc1.stream().mapToDouble(spc -> spc.cost1()).sum()
                + listSpc2.stream().mapToDouble(spc -> spc.cost1()).sum()
                + listSpc3.stream().mapToDouble(spc -> spc.cost1()).sum()
                + listSpc5.stream().mapToDouble(spc -> spc.cost1()).sum()
                + kitList.stream().mapToDouble(spc -> spc.cost1()).sum();
        doc.getElementsByTag("tfoot").get(0).selectFirst("tr:eq(0)")
                .selectFirst("td:eq(1)").text(UCom.format(total, 9));
    }

    private static void recordAdd(Elements templateRec, RRecord specificRec, Document doc) {
        Elements tdList = templateRec.get(1).getElementsByTag("td");
        tdList.get(0).text(String.valueOf(++npp));
        tdList.get(1).text(specificRec.artikl());
        tdList.get(2).text(specificRec.name());
        tdList.get(3).text(specificRec.color(1));
        tdList.get(4).text(specificRec.width());
        tdList.get(5).text(specificRec.angles());
        tdList.get(6).text(specificRec.unit());
        tdList.get(7).text(specificRec.count());
        tdList.get(8).text(specificRec.weight());
        tdList.get(9).text(specificRec.sebes2());
        tdList.get(10).text(specificRec.price2());
        doc.getElementsByTag("tbody").append(templateRec.get(1).html());
    }
}
