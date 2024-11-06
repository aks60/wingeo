package report;

import report.sup.RTable;
import report.sup.RSpecific;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.making.SpcRecord;
import builder.making.SpcTariffic;
import common.UCom;
import dataset.Record;
import domain.ePrjprod;
import domain.eProject;
import frames.UGui;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

//Разход материала
public class Material {

    private static int npp = 0;

    public void parseDoc(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Material.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            //Заполним отчёт
            loadDoc(projectRec, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfMaterial.material()" + e);
        }
    }

    private static void loadDoc(Record projectRec, Document doc) {

        List<RSpecific> spcList = new ArrayList<RSpecific>();
        List<SpcRecord> listSpc = new ArrayList<SpcRecord>();
        List<SpcRecord> listKit = new ArrayList<SpcRecord>();        

        List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
        for (Record prjprodRec : prjprodList) {

            String script = prjprodRec.getStr(ePrjprod.script);
            Wincalc winc = new Wincalc(script);
            winc.specification(true);

            listSpc.addAll(winc.listSpec);
            listKit = SpcTariffic.kits(prjprodRec, winc, true); //добавим комплекты
        }
        listSpc.forEach(el -> spcList.add(new RSpecific(el)));
        listKit.forEach(el -> spcList.add(new RSpecific(el)));
        
      List<RSpecific> groupList = RSpecific.groups3(spcList);
       
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

    private static void recordAdd(Elements tdList, RSpecific spcRec) {
        tdList.get(0).text(String.valueOf(++npp));
        tdList.get(1).text(spcRec.artikl());
        tdList.get(2).text(spcRec.name());
        tdList.get(3).text(spcRec.cname1());
        tdList.get(4).text(spcRec.count());
        tdList.get(5).text(spcRec.unit());
        tdList.get(6).text(spcRec.sebes2());
        tdList.get(7).text(spcRec.price2());
    }
}
