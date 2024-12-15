package report;

import builder.Kitcalc;
import report.sup.RTable;
import report.sup.RRecord;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.making.TRecord;
import common.UCom;
import dataset.Record;
import domain.ePrjprod;
import domain.eProject;
import frames.UGui;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

//Разход материала
public class RMaterial {

    private static int npp = 0;

    public void parseDoc(List<Record> prjprodList) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Material.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            Record prjprodRec = prjprodList.get(0);
            Record projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));

            //Заполним отчёт
            loadDoc(projectRec, prjprodList, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            System.err.println("Ошибка:RMaterial.parseDoc1()" + e);
        }
    }

    private static void loadDoc(Record projectRec, List<Record> prjprodList, Document doc) {

        List<RRecord> spcList = new ArrayList<RRecord>();
        List<TRecord> winSpc = new ArrayList<TRecord>();
        List<TRecord> kitSpc = new ArrayList<TRecord>();
        Wincalc winc = new Wincalc();

        for (Record prjprodRec : prjprodList) {
            String script = prjprodRec.getStr(ePrjprod.script);
            if (script.isEmpty() == false) {
                winc.build(script); //калкуляция 
                winc.specific(true);
                winSpc.addAll(winc.listSpec);
            }
            kitSpc = Kitcalc.tarifficProd(winc, prjprodRec, 0, true, true); //добавим комплекты
        }
        winSpc.forEach(el -> spcList.add(new RRecord(el)));
        kitSpc.forEach(el -> spcList.add(new RRecord(el)));

        List<RRecord> groupList = RRecord.groups4R(spcList);

        Elements templateRec = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr");
        groupList.forEach(act -> doc.getElementsByTag("tbody").append(templateRec.get(0).html()));

        String date = UGui.simpleFormat.format(projectRec.get(eProject.date6));
        double total = groupList.stream().mapToDouble(spc -> spc.cost1()).sum();

        doc.getElementById("h01").text("Заказ №" + projectRec.getStr(eProject.num_ord));
        doc.getElementsByTag("thead").get(0).getElementsByTag("tr").get(0).getElementsByTag("th").get(0).html("Дата изготовления заказа: " + date + " г.");

        doc.getElementsByTag("tbody").get(0).getElementsByTag("tr").remove(1);
        Elements trList = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr");

        for (int i = 0; i < groupList.size(); i++) {
            recordAdd(trList.get(i).getElementsByTag("td"), groupList.get(i));
        }

        doc.getElementsByTag("tfoot").get(0).selectFirst("tr:eq(0)")
                .selectFirst("td:eq(1)").text(UCom.format(total, 9));
    }

    private static void recordAdd(Elements tdList, RRecord spcRec) {
        tdList.get(0).text(String.valueOf(++npp));
        tdList.get(1).text(spcRec.artikl());
        tdList.get(2).text(spcRec.name());
        tdList.get(3).text(spcRec.color(1));
        tdList.get(4).text(spcRec.quant(2));
        tdList.get(5).text(spcRec.unit());
        tdList.get(6).text(spcRec.sebes2());
        tdList.get(7).text(spcRec.price2());
    }
}
